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

import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import lrg.jMondrian.util.PeriodicExecutor;

public class FullScreenRenderer implements ViewRendererInterface, Runnable, IGraphicsProvider,KeyListener {
	private BufferStrategy strategy;
	private GraphicsDevice device;
	private BufferCapabilities.FlipContents flipContents;
	private Dimension offDimension;
	private Window win;
	private Frame frame;
	
	private PeriodicExecutor animator;
	private OrganicPainter painter;
	
	public FullScreenRenderer(int fps) {
		this.painter = new OrganicPainter();
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		device = ge.getDefaultScreenDevice();
		frame = new Frame("jrg.jMondrian",device.getDefaultConfiguration());
		win = new Window(frame);
		
		win.addMouseMotionListener(painter);
		win.addMouseListener(painter);
		win.addMouseWheelListener(painter);
		frame.addKeyListener(painter);
		frame.addKeyListener(this);
		animator = new PeriodicExecutor("OrganicView-Painter",fps,this);
	}

	@Override
	public ShapeElementFactory getShapeFactory() {
		return painter;
	}

	@Override
	public void setPreferredSize(int width, int height) {
		offDimension = new Dimension(width,height);
	}

	@Override
	public void run() {
		//if (!flipContents.equals(BufferCapabilities.FlipContents.BACKGROUND)) {
			// Clear background
			//g.setColor(Color.white);
			//g.fillRect(0, 0, offDimension.width, offDimension.height);
		//}
		if (painter.paintFrame(this, offDimension, animator.unsafeGetFrameNo())) {
			
			Graphics g = getPainter();
			g.setColor(Color.black);
			Dimension ss = win.getSize();
			int ww = offDimension.width, wh = offDimension.height;
			int sw = ss.width, sh = ss.height;
			g.fillRect(0, wh, sw, sh-wh);
			g.fillRect(ww, 0, sw-ww, wh);

			strategy.show(); // Flip the back buffer to the screen
		}
		
	}	
	
	@Override
	public Graphics2D getPainter() {
		return (Graphics2D)strategy.getDrawGraphics();
	}
	
	public void open() {
		GraphicsConfiguration gc = device.getDefaultConfiguration();
		BufferCapabilities bufCap = gc.getBufferCapabilities();
		
		frame.setVisible(true);
		device.setFullScreenWindow(win);
		win.requestFocus();

		int numBuffers = 2; // Includes front buffer
		win.createBufferStrategy(numBuffers);
		strategy = win.getBufferStrategy();
		bufCap = strategy.getCapabilities();
		flipContents = bufCap.getFlipContents();

		offDimension = win.getSize();
		animator.restart();
	}
	
	@Override public void keyPressed(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		Dimension ws = win.getSize();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			animator.stop();
			frame.setVisible(false);
			device.setFullScreenWindow(null);
    		System.exit(0);
			break;
		case KeyEvent.VK_1:
			offDimension = ws;
			painter.repaint();
			break;
		case KeyEvent.VK_2:
			offDimension = new Dimension(3*ws.width/4,3*ws.height/4); 
			painter.repaint();
			break;
		case KeyEvent.VK_3:
			offDimension = new Dimension(ws.width/2,ws.height/2);
			painter.repaint();
			break;
		case KeyEvent.VK_4:
			offDimension = new Dimension(ws.width/3,ws.height/3);
			painter.repaint();
			break;
		default:
			break;
		}
	}
	@Override public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		new FullScreenRenderer(28).open();
	}

}
