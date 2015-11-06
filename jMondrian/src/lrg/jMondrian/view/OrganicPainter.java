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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;

import static lrg.jMondrian.util.CommandColor.isColorTransparent;
import lrg.jMondrian.util.StaticFigures;
import lrg.jMondrian.view.space.Point;
import lrg.jMondrian.view.space.Vector;

public class OrganicPainter extends MouseAdapter implements KeyListener, ShapeElementFactory {
	private static final double SELECTION_EPSILON = 8; // in pixels
	private static final long SELECTION_RECHECK_DELAY = 500; // in miliseconds
	private static final Color BACKGROUND = Color.WHITE; 
	
	private Point mousePressPoint = null;
	private ViewWindow vw;
    private List<PlainShape> shapes;
    private boolean repaintRequired = true;
    
    private boolean pointerMoved = false;
    private int pointerX, pointerY;
    private long lastSelectionCheck = 0;
    private PlainShape selected = null;
    
    private AffineTransform inverted;
    private double selectionEpsilon=0;
	
	public OrganicPainter() {
		shapes = new ArrayList<PlainShape>();
		vw = new ViewWindow();
		viewChanged();
		// this.processors = Runtime.getRuntime().availableProcessors();
		// this.ex = Executors.newFixedThreadPool(processors);
	}
	
	public void clear() {
		mousePressPoint = null;
		shapes = new ArrayList<PlainShape>();
		repaintRequired = true;
		pointerMoved = false;
		lastSelectionCheck = 0;
		selected = null;
	}
	
    public boolean paintFrame(IGraphicsProvider im, Dimension size, long frame) {
    	long now = System.currentTimeMillis();
        Rectangle2D view = vw.getViewWindow(size.width, size.height);
    	if (pointerMoved && (now-lastSelectionCheck>SELECTION_RECHECK_DELAY)) {
    		PlainShape s = findShape(pointerX, pointerY, view); 
    		lastSelectionCheck = now;
    		if (s!=selected) {
        		selected = s; 
            	repaint();
    		}
    	}
		if (!isRepaintRequired()) {
			return false;
		}
		
    	repaintRequired = false;
    	
        AffineTransform transf = vw.createAffineTransform();
        Graphics2D g2 = im.getPainter();

        // Erase the previous image
		g2.setColor(BACKGROUND);
		g2.fillRect(0, 0, size.width, size.height);
        
		// Paint the frame into the image
        
        //long t = System.nanoTime(), v=0;
    	for(PlainShape s:shapes)
    		if(view.intersects(s.getBounds())) {
    			//v++;
    			s.paint(g2, transf);
    		}
    	//t = (System.nanoTime() - t)/1000;
    	//g2.setPaint(Color.black);
    	//g2.drawString(v+"/"+shapes.size()+" paints in "+(t/1000d)+" ms", 100, 100); 
    	
        if (null!=selected) {
        	selected.paintHighlightBackground(g2, transf);
        	selected.paintHighlightForeground(g2, transf);
        	selected.painDescription(g2, pointerX+5, pointerY+32,size);
        }
    	g2.dispose();
        return true;
    }
    
    public boolean isRepaintRequired() {
		return repaintRequired;
	}
    
    public void repaint() {
    	repaintRequired = true;
    }
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		Vector zoomVector = new Vector(mwe.getX(), mwe.getY(), 0);
		if (mwe.getWheelRotation()>0)
			vw.scaleDown(vw.inverseApplyVector(zoomVector));
		else
			vw.scaleUp(vw.inverseApplyVector(zoomVector));
		viewChanged();
	}
	
	public void centerZoomIn(int centerX, int centerY) {
		Vector zoomVector = new Vector(centerX, centerY, 0);
		vw.scaleUp(vw.inverseApplyVector(zoomVector));
		viewChanged();
	}
	public void centerZoomOut(int centerX, int centerY) {
		Vector zoomVector = new Vector(centerX, centerY, 0);
		vw.scaleDown(vw.inverseApplyVector(zoomVector));
		viewChanged();
	}

	@Override public void mouseDragged(MouseEvent me) {
		mouseMoved(me);
		if (null!=mousePressPoint) {
			Point newPoint = new Point(me.getX(), me.getY(), 0);
			vw.translateWith(vw.inverseApplyVector(new Vector(newPoint,mousePressPoint)));
			mousePressPoint = newPoint;
			viewChanged();
		}
	}

	private void viewChanged() {
		try {
			inverted = vw.createAffineTransform().createInverse();
			selectionEpsilon = inverted.getScaleX()*SELECTION_EPSILON;
			if (selectionEpsilon<1.0) selectionEpsilon = 1.0;
		} catch (NoninvertibleTransformException e) {}
		repaint();
	}

	@Override public void mousePressed(MouseEvent me) {
		if (me.getButton()==MouseEvent.BUTTON1) {
			mousePressPoint = new Point(me.getX(), me.getY(), 0);
		}
	}
	
	@Override public void mouseReleased(MouseEvent me) {
		if (me.getButton()==MouseEvent.BUTTON1) {
			mousePressPoint = null;
		}
		if (me.getButton()==MouseEvent.BUTTON3) {
			
		} 
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		if (mx!=pointerX || my!=pointerY) {
			pointerX = e.getX();
			pointerY = e.getY();
			pointerMoved = true;
		}
	}
    
	@Override public void keyPressed(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}

	
	
	@Override
	public void addEllipse(Object ent, String descr, int x1, int y1, int width, int heigth, int color, boolean border) {
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),Color.BLACK,
			new Ellipse2D.Double(x1,y1,width,heigth))
			.setBorderVisible(border)
			.setFillVisible(!isColorTransparent(color))
		);
	}

	@Override
	public void addEllipse(Object ent, String descr, int x1, int y1, int width, int heigth, int color, int frameColor) {
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),new Color(frameColor),
			new Ellipse2D.Double(x1,y1,width,heigth))
			.setFillVisible(!isColorTransparent(color))
		);
	}

	@Override
	public void addLine(Object ent, String descr, int x1, int y1, int x2, int y2, int color, boolean oriented) {
		List<Shape> lineArt = new ArrayList<Shape>();
		lineArt.add(new Line2D.Double(x1,y1,x2,y2));
		
        if(oriented) {
        		double arrow[] = StaticFigures.getArrow(x1, y1, x2, y2);
        		if (arrow!=null) {
                    lineArt.add(new Line2D.Double(arrow[0],arrow[1],arrow[2],arrow[3]));
                    lineArt.add(new Line2D.Double(arrow[4],arrow[5],arrow[6],arrow[7]));
        		}
        }
        
        shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),new Color(color),
			lineArt).setFillVisible(false));
	}
	


	@Override
	public void addPolyLine(Object ent, String descr, List<Integer> x, List<Integer> y) {
		this.addPolyLine(ent, descr, x, y, Color.LIGHT_GRAY.getRGB(), false, true);
	}

	@Override
    public void addPolyLine(Object ent, String descr, List<Integer> x, List<Integer> y, int color, boolean oriented, boolean closed) {
		if (x.isEmpty() || y.isEmpty() || y.size()<x.size()) 
			return;
		List<Shape> poly = new ArrayList<Shape>();
		for (int i = 1; i < x.size(); i++) 
			poly.add(new Line2D.Double(x.get(i-1),y.get(i-1),x.get(i),y.get(i)));
		
		if (closed)
			poly.add(new Line2D.Double(x.get(0),y.get(0),x.get(x.size()-1),y.get(y.size()-1)));
		
        if(oriented && x.size()>1) {
    		double arrow[] = StaticFigures.getArrow(x.get(x.size()-2),y.get(y.size()-2), x.get(x.size()-1),y.get(y.size()-1));
    		if (arrow!=null) {
    			poly.add(new Line2D.Double(arrow[0],arrow[1],arrow[2],arrow[3]));
                poly.add(new Line2D.Double(arrow[4],arrow[5],arrow[6],arrow[7]));
    		}
        }
		
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),new Color(color),
			poly).setFillVisible(false));
	}
	
	
	@Override
    public void addQuadCurve(Object ent, String descr, double x1, double y1, double cx1, double cy1, double x2, double y2, int color, boolean oriented) {
		List<Shape> poly = new ArrayList<Shape>();
		Shape curve = new QuadCurve2D.Double(x1,y1, cx1,cy1, x2,y2);
		
		if(oriented) {
    		double arrow[] = StaticFigures.getArrow(curve);
    		if (arrow!=null) {
    			poly.add(new Line2D.Double(arrow[0],arrow[1],arrow[2],arrow[3]));
                poly.add(new Line2D.Double(arrow[4],arrow[5],arrow[6],arrow[7]));
    		}
		}
		
		poly.add(curve);
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),new Color(color),
			poly).setFillVisible(false)
		);
    }

	@Override
    public void addCubicCurve(Object ent, String descr, double x1, double y1, double cx1, double cy1, double cx2, double cy2, double x2, double y2, int color, boolean oriented) {
		List<Shape> poly = new ArrayList<Shape>();
		Shape curve = new CubicCurve2D.Double(x1,y1, cx1,cy1, cx2,cy2, x2,y2); 

		if(oriented) {
    		double arrow[] = StaticFigures.getArrow(curve);
    		if (arrow!=null) {
    			poly.add(new Line2D.Double(arrow[0],arrow[1],arrow[2],arrow[3]));
                poly.add(new Line2D.Double(arrow[4],arrow[5],arrow[6],arrow[7]));
    		}
		}
		
		poly.add(curve);
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),new Color(color),
			poly).setFillVisible(false)
		);
    }
	

	@Override
	public void addRectangle(Object ent, String descr, int x1, int y1, int width, int heigth, int color, boolean border) {
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),Color.BLACK,
			new Rectangle2D.Double(x1,y1,width,heigth))
			.setBorderVisible(border)
			.setFillVisible(!isColorTransparent(color))
			);
	}

	@Override
	public void addRectangle(Object ent, String descr, int x1, int y1, int width, int heigth, int color, int frameColor) {
		shapes.add(new PlainShape.RegullarShape(ent,descr,new Color(color),new Color(frameColor),
			new Rectangle2D.Double(x1,y1,width,heigth))
			.setFillVisible(!isColorTransparent(color))
		);
	}

	@Override
	public void addText(Object ent, String descr, String text, int x1, int y1, int color) {
		if (null==text) text = "null";
		if (!text.isEmpty())
			shapes.add(new PlainShape.TextShape(ent,descr,new Color(color),Color.BLACK,text,x1,y1));
	}

	public PlainShape findShape(int x, int y, Rectangle2D viewWindow) {
			Point real = new Point(0,0);
			inverted.transform(new Point(x,y).value(), 0, real.value(), 0, 1);
			
			ListIterator<PlainShape> it = shapes.listIterator(shapes.size());
			while(it.hasPrevious()) {
				PlainShape ps = it.previous();
				if (null!=viewWindow && !viewWindow.intersects(ps.getBounds()))
					continue;
				if (ps.containsPoint(real,selectionEpsilon))
					return ps;
			}
			return null;
	}

	@Override
	public Object findEntity(int x, int y) {
		PlainShape ps = findShape(x, y, null);
		if (null!=ps)
			return ps.getModel();
		return null;
	}

	@Override
	public String findStatusInformation(int x, int y) {
		PlainShape ps = findShape(x, y, null);
		if (null!=ps)
			return ps.getDescription();
		return "";
	}

	/**
	 * Parallel painting attempt - results in worse performance, reason... unknown
	 */
	
	private int processors;
	private ExecutorService ex;
	
	private Boolean runningPaintsLock = true;
	private Integer runningPaints = 0;
	

	private class SahpePainter implements Runnable {
		private int start, end;
		private Rectangle2D view;
		private AffineTransform transf;
		private Graphics2D g2;
		
		public SahpePainter(int start, int end, Rectangle2D view, Graphics2D g2, AffineTransform transf) {
			this.start = start;
			this.end = end;
			this.view = view;
			this.g2 = g2;
			this.transf = transf;
			synchronized (runningPaintsLock) { runningPaints++; }
		}
		
		@Override
		public void run() {
			for (int i = start; i < end; i++) {
				PlainShape s = shapes.get(i);
	    		if(view.intersects(s.bounds))
	    			s.paint(g2, transf);
			}
			g2.dispose();
			synchronized (runningPaintsLock) { 
				runningPaints--; 
				runningPaintsLock.notifyAll(); 
			}
		}
	}
}
