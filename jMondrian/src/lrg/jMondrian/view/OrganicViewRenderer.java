/*
 * Copyright (c) 2006-2015 Petru-Florin Mihancea, Mihai Balint, Andreea Ionete
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lrg.jMondrian.view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.awt.image.VolatileImage;

import lrg.jMondrian.access.IObjectCommand;
import lrg.jMondrian.figures.Figure;
import lrg.jMondrian.util.MenuReaction;
import lrg.jMondrian.util.PeriodicExecutor;


public class OrganicViewRenderer extends Frame implements ViewRendererInterface, Runnable, IGraphicsProvider {
	private static final long serialVersionUID = 1L;
	
	private PeriodicExecutor animator;
	private Dimension offDimension;
	private VolatileImage offImage;
	private OrganicPainter painter;
	private IObjectCommand<?, ? extends Figure<?>> figureSpec = null;
	private Button reset, zoomIn, zoomOut;
	private Checkbox aaToggle;
	private boolean antialias=true;
	public void setMenuReaction(final MenuReaction mr) {
		addMouseListener(new MouseListener() {
			private void reaction(MouseEvent e) {
				if(mr != null) {
                    Object ent = painter.findEntity(e.getX(),e.getY());
                    if(ent != null) {
                    	mr.buildFor(ent, e);
                    }
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {reaction(arg0);}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}			
		});
	}

	public OrganicViewRenderer() {
		this(new OrganicPainter());
	}
	
	public OrganicViewRenderer(OrganicPainter painter) {
		this(28,painter);
	}
	
	public OrganicViewRenderer(int fps, OrganicPainter painter) {
		setTitle("lrg.jMondrian");
		setSize(800, 600);
		setLocation(200, 150);
		setVisible(false);
		addWindowListener(new OViewStateListener(this));
		addMouseMotionListener(painter);
		addMouseListener(painter);
		addMouseWheelListener(painter);
		addKeyListener(painter);
		
		setLayout(null);
		add(reset = new Button("Reload"));
		add(zoomIn = new Button("Zoom In"));
		add(zoomOut = new Button("Zoom Out"));
		add(zoomOut = new Button("Zoom Out"));
		add(aaToggle = new Checkbox("Toggle AA"));
		
		zoomIn.addActionListener(new ZoomInAction());
		zoomOut.addActionListener(new ZoomOutAction());
		aaToggle.setState(antialias);
		aaToggle.addItemListener(new ToggleAAAction());
		
		this.painter = painter;
		animator = new PeriodicExecutor("OrganicView-Painter",fps,this);
	}
	
	public void run() {
		// called by the animator
		repaint();
	}    
	
	@Override
	public Graphics2D getPainter() {
		// should only be called within the context of an update(Graphics)
		Graphics2D g2 = (Graphics2D)offImage.getGraphics(); 
		if(antialias) 
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		return g2;
	}
	
	/**
	 * Update a frame of animation.
	 */
	public void update(Graphics g) {
		Dimension d = getSize();
		GraphicsConfiguration gc = getGraphicsConfiguration(); 
		if ((offImage == null)
		 || (d.width != offDimension.width)
		 || (d.height != offDimension.height)) {
		    offDimension = d;
		    //offImage = createImage(d.width, d.height);
		    //offImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		    //offImage = createMemoImage();
		    //offImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
		    //	.createCompatibleVolatileImage(d.width, d.height);
		    offImage = gc.createCompatibleVolatileImage(offDimension.width, offDimension.height);
		    painter.repaint();
		}
		do{
			int valCode = offImage.validate(gc);
			// to re-render the image anyway. 
			if(valCode==VolatileImage.IMAGE_INCOMPATIBLE) {
				offImage = gc.createCompatibleVolatileImage(offDimension.width, offDimension.height);
			}
			painter.paintFrame(this, offDimension, animator.unsafeGetFrameNo());
			
			g.drawImage(offImage, 0, 0, null);
		} while(offImage.contentsLost());

		if (figureSpec==null) reset.setEnabled(false);
		reset.setBounds(10, offDimension.height-22, 90, 20);
		zoomIn.setBounds(200, offDimension.height-22, 90, 20);
		zoomOut.setBounds(300, offDimension.height-22, 90, 20);
		aaToggle.setBounds(offDimension.width-110, offDimension.height-22, 90, 20);
		reset.update(g);
		zoomIn.update(g);
		zoomOut.update(g);
		aaToggle.update(g);
	}
	
	private void update1(Graphics g) {
		// old update - uses slower back buffer (when non-volatile image is created)
		// incorrectly uses volatile back-buffer
		Dimension d = getSize();
	
		// Create the offscreen graphics context
		if ((offImage == null)
		 || (d.width != offDimension.width)
		 || (d.height != offDimension.height)) {
		    offDimension = d;
		    //offImage = createImage(d.width, d.height);
		    //offImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		    //offImage = createMemoImage();
		    //offImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
		    //	.createCompatibleVolatileImage(d.width, d.height);
		    offImage = getGraphicsConfiguration().createCompatibleVolatileImage(d.width, d.height);
		}
	
		//painter.paintFrame(offGraphics, d, animator.unsafeGetFrameNo());
		painter.paintFrame(this, d, animator.unsafeGetFrameNo());
	
		// Paint the image onto the screen
		g.drawImage(offImage, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
	}
	
	private Image createMemoImage() {
		// EXPERIMENTal only, attept to create faster back buffer
   	    int size = offDimension.width * offDimension.height;
		int[] pixels = new int[size];

	    MemoryImageSource source = new MemoryImageSource(offDimension.width, offDimension.height, pixels, 0, offDimension.width);

	    source.setAnimated(true);
	    source.setFullBufferUpdates(true);
	    Image img = Toolkit.getDefaultToolkit().createImage(source);
		return img;
	}
	
	/**
	 * Paint the previous frame (if any).
	 */
	public void paint(Graphics g) {
		// everything is done in update, no need to do anything here
	}
	
	public String unsafeStats() {
		return "Window: "+animator.unsafeStats();
	}
	
	public void stop() {
		animator.stop();
	}
	    
    private static class OViewStateListener extends WindowAdapter {
    	private OrganicViewRenderer target;
    	
    	public OViewStateListener(OrganicViewRenderer target) {
    		this.target = target;
    	}
    	
    	@Override
    	public void windowClosing(WindowEvent arg0) {
    		target.stop();
    		target.dispose();
    		//System.exit(0);
    	}
    }
	    
	    
	@Override
	public ShapeElementFactory getShapeFactory() {
		return painter;
	}

	@Override
	public void setPreferredSize(int width, int height) {
        this.setPreferredSize(new Dimension(width,height));        
	}
	
	public void open() {
		setVisible(true);
		animator.restart();
	}
	
	public <T> void open(IObjectCommand<T, Figure<T>> spec) {
		painter.clear();
		(figureSpec = spec).execute().renderOn(this);
		reset.addActionListener(new ReloadFigureAction());
		open();
	}
	
	private class ReloadFigureAction implements ActionListener,Runnable {
		@Override public synchronized void actionPerformed(ActionEvent e) {
			reset.setEnabled(false);
			reset.removeActionListener(this);
			animator.stop();
			new Thread(this,"ReloadFigure Thread").start();
		}
		
		@Override public synchronized void run() {
			painter.clear();
			figureSpec.execute().renderOn(OrganicViewRenderer.this);
			animator.restart();
			reset.addActionListener(this);
			reset.setEnabled(true);
		}
	}
	
	private class ZoomInAction implements ActionListener {
		@Override public synchronized void actionPerformed(ActionEvent e) {
			painter.centerZoomIn(offDimension.width/2, offDimension.height/2);
		}
	}
	private class ZoomOutAction implements ActionListener {
		@Override public synchronized void actionPerformed(ActionEvent e) {
			painter.centerZoomOut(offDimension.width/2, offDimension.height/2);
		}
	}
	
	private class ToggleAAAction implements ItemListener {
		@Override public void itemStateChanged(ItemEvent e) {
			antialias = e.getStateChange()==ItemEvent.SELECTED;
			painter.repaint();
		}
	}
	

}
