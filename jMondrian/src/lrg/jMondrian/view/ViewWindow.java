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

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import lrg.jMondrian.view.space.Point;
import lrg.jMondrian.view.space.SpaceProperties;
import lrg.jMondrian.view.space.Vector;


public class ViewWindow {
	private static final double SCALE_FACTOR = 1.1;
	private static final double SCALE_FACTOR_INC = SCALE_FACTOR - 1.0;
	private static final double SCALE_FACTOR_RATIO = SCALE_FACTOR_INC / SCALE_FACTOR;
	
	private Point origin;
	private double scale;
	
	public ViewWindow() {
		origin = SpaceProperties.ORIGIN.clone();
		scale = 1.0;
	}
	
	public void scaleUp(Vector p) {
		if (scale<10) { 
    		origin.add(p.scalarProduct(SCALE_FACTOR_RATIO));
			scale*=SCALE_FACTOR;
		}
	}
	public void scaleDown(Vector p) {
		if (scale>0.01) {
    		origin.sub(p.scalarProduct(SCALE_FACTOR_INC));
    		scale/=SCALE_FACTOR;
		}
	}
	
	public void translateWith(Vector v) {
		origin.add(v);
	}
	
	public Point origin() {
		return origin;
	}
	
	public Rectangle2D getViewWindow(double winWidth, double winHeight) {
		return new Rectangle2D.Double(origin.x(),origin.y(),winWidth/scale,winHeight/scale); 
	}
	
	public AffineTransform createAffineTransform() {
		return new AffineTransform(scale,0,0,scale,-origin.x()*scale,-origin.y()*scale);
	}
	
	public Point applyPoint(Point p) {
		return new Vector(origin,p).scalarProduct(scale);
	}
	public Point inverseApplyPoint(Point p) {
		return new Vector(p,origin).scalarProduct(1/scale);
	}
	public Vector applyVector(Vector v) {
		return v.scalarProduct(scale);
	}
	public Vector inverseApplyVector(Vector v) {
		return v.scalarProduct(1/scale);
	}
	
	public double applyX(double x) {
		return (x-origin.x())*scale;
	}
	
	public double applyY(double y) {
		return (y-origin.y())*scale;
	}
	
	public double applyD(double d) {
		return d*scale;
	}
	
	public int applyXint(double x) {
		return (int)Math.round((x-origin.x())*scale);
	}
	
	public int applyYint(double y) {
		return (int)Math.round((y-origin.y())*scale);
	}
	
	public int applyDint(double d) {
		return (int)Math.round(d*scale);
	}
}