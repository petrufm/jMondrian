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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import lrg.jMondrian.view.space.Point;
import lrg.jMondrian.view.space.Vector;

public abstract class PlainShape {
	//private static final Color selectionBorder = new Color(0.8f,0.9f,1.0f);
	private static final Color selectionBorder = new Color(1.0f,0.5f,0.2f);
	private static final Color selectionFill = new Color(0.8f,0.8f,0.8f);
	
	private Object model;
	protected Paint border, fill;
	protected String description;
	protected Rectangle2D bounds;
	
	public PlainShape(Object model, String description, Paint fill, Paint border) {
		this.model = model;
		this.description = description;
		this.fill = fill;
		this.border = border;
	}
	
	public abstract void paint(Graphics2D g, AffineTransform t);
	public abstract boolean containsPoint(Point p, double epsilonSquared);

	public Rectangle2D getBounds() {
		return bounds;
	}
	
	public Object getModel() {
		return model;
	}
	
	public String getDescription() {
		return description;
	}
	
	private Rectangle2D getSelectionShape(AffineTransform t) {
    	Rectangle2D selectionBound = t.createTransformedShape(bounds).getBounds2D();
    	selectionBound.setFrame(selectionBound.getX()-2.0, selectionBound.getY()-2.0, selectionBound.getWidth()+4.0, selectionBound.getHeight()+4.0);
    	return selectionBound;
	}
	
	public void paintHighlightBackground(Graphics2D g, AffineTransform t) {
		// this should be uncommented so that an highlighted object is painted
		// on top of all others
		// it is currently commented since figure-in-node things will paint the 
		// figure on top of it's content
		//paint(g,t);
	}
	
	public void paintHighlightForeground(Graphics2D g, AffineTransform t) {
    	g.setPaint(selectionBorder);
    	g.draw(getSelectionShape(t));
	}
	
	public Rectangle2D painDescription(Graphics2D g, int pointerX, int pointerY, Dimension windowSize) {
		if (description.isEmpty()) return null;
		
    	FontMetrics m = g.getFontMetrics();
    	
    	String[] splits = description.split("\n");
    	Rectangle2D[] splitBounds = new Rectangle2D[splits.length];

    	float maxWidth = 0, totalHeight = 0;
    	for (int i=0; i<splits.length; i++) {
    		String split = splits[i];
    		splitBounds[i] = m.getStringBounds(split, g);
			totalHeight += splitBounds[i].getHeight();
			float w = (float)splitBounds[i].getWidth();
			if (w>maxWidth) maxWidth = w;
		}
    	
    	if (pointerX+maxWidth>windowSize.width) {
    		pointerX = (maxWidth>windowSize.width) ? 0 : Math.round(windowSize.width-maxWidth); 
    	}
    	
    	Rectangle2D sb = new Rectangle2D.Double(pointerX+splitBounds[0].getX(),pointerY+splitBounds[0].getY(),maxWidth,totalHeight); 
    	g.setPaint(new Color(1.0f,0.975f,0.9f));
    	g.fill(sb);
    	g.setPaint(selectionFill);
    	g.draw(sb);
    	
    	g.setColor(Color.black);
    	totalHeight = 0;
    	for (int i=0; i<splits.length; i++) {
        	g.drawString(splits[i], pointerX+2, pointerY+totalHeight);
			totalHeight += splitBounds[i].getHeight();
    	}
    	return sb;
	}
	
	
	public static class TextShape extends PlainShape {
		private String text;
		private Point position;
		private boolean accurateBounds = false;
		
		public TextShape(Object model, String description, Paint fill, Paint border, String text, double... coordinates) {
			this(model,description,fill,border,text,new Point(coordinates));
		}
		
		public TextShape(Object model, String description, Paint fill, Paint border, String text, Point position) {
			super(model,description,fill,border);
			this.text = text;
			this.position = position;
			bounds = new Rectangle2D.Double(position.x(),position.y(),500,10);
		}
		
		public void paint(Graphics2D g, AffineTransform t) {
			if(!accurateBounds) {
				Rectangle2D bound = g.getFontMetrics().getStringBounds(text, g);
				bound.setFrame(position.x()+bound.getX(), position.y()+bound.getY(), bound.getWidth(), bound.getHeight());
				bounds = bound;
				accurateBounds = true;
			}
            Point actual = position.clone();
            t.transform(position.value(), 0, actual.value(), 0, 1);
            g.setPaint(fill);
            g.drawString(text,actual.xInt(),actual.yInt());
		}
		
		public boolean containsPoint(Point p, double epsilonSquared) {
			return position.equals(p);
		}
	}
	
	public static class RegullarShape extends PlainShape {
		private List<Shape> shapes;
		private boolean borderVisible = true, fillVisible = true;
		
		public RegullarShape(Object model, String description, Paint fill, Paint border, Shape... shapes) {
			this(model,description,fill,border,Arrays.asList(shapes));
		}

		public RegullarShape(Object model, String description, Paint fill, Paint border, List<Shape> shapes) {
			super(model,description,fill,border);
			this.shapes = shapes;
			
			bounds = shapes.get(0).getBounds2D();
			double x1 = bounds.getX(), y1=bounds.getY(), x2=x1+bounds.getWidth(),y2=y1+bounds.getHeight();
			
			for (int i = 1; i < shapes.size(); i++) {
				Rectangle2D sb = shapes.get(i).getBounds2D();

				double sbx1 = sb.getX(), sby1 = sb.getY(); 
				double sbx2 = sbx1 + sb.getWidth();
				double sby2 = sby1 + sb.getHeight();
				
				if (sbx1<x1) x1 = sbx1;
				if (sby1<y1) y1 = sby1;
				if (sbx2>x2) x2 = sbx2;
				if (sby2>y2) y2 = sby2;
			}
			if (x2==x1) x2++;
			if (y2==y1) y2++;
			bounds = new Rectangle2D.Double(x1,y1,x2-x1,y2-y1);
		}
		
		public RegullarShape setBorderVisible(boolean borderVisible) {
			this.borderVisible = borderVisible;
			return this;
		}
		public RegullarShape setFillVisible(boolean fillVisible) {
			this.fillVisible = fillVisible;
			return this;
		}
		
		public void paint(Graphics2D g, AffineTransform t) {
			Stroke normal = g.getStroke();
			float width = (float)t.getScaleX();
			BasicStroke thick = new BasicStroke(width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,/* unused*/10.0f, null, 0.0f);
			
			g.setStroke(thick);
			for(Shape s:shapes) {
				Shape actual = t.createTransformedShape(s);
				if(fillVisible)  {
					g.setPaint(fill);
					g.fill(actual); 
				}
				
				if (borderVisible) {
					g.setPaint(border);
					g.draw(actual);
				}
			}
			g.setStroke(normal);
		}
		
		public void paintHighlightForeground(Graphics2D g, AffineTransform t) {
			Stroke normal = g.getStroke();
			float width = 2.5f * (float)t.getScaleX();
			BasicStroke thick = new BasicStroke(width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,/* unused*/10.0f, null, 0.0f);
			g.setStroke(thick);
			for(Shape s:shapes) {
				Shape actual = t.createTransformedShape(s);
				g.setPaint(selectionBorder);
				g.draw(actual);
			}
			g.setStroke(normal);
		}

		public boolean containsPoint(Point p, double epsilonSquared) {
			BasicStroke thick = new BasicStroke((float)epsilonSquared,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,/* unused*/10.0f, null, 0.0f);
			for(Shape s:shapes) {
				if (fillVisible) {
					if (s.contains(p.x(), p.y()))
						return true;
				} else {
					if (borderVisible) {
						if (thick.createStrokedShape(s).contains(p.x(),p.y()))
							return true;
					}
				}
			}
			return false;
		}
		
	}
	
}
