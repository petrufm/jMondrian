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
package lrg.jMondrian.view.space;

import static lrg.jMondrian.view.space.SpaceProperties.*;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;

public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected double[] value = new double[DIMENSIONS];
	
	public Point(double... value) {
		int max = DIMENSIONS<value.length? DIMENSIONS : value.length;
		for(int i=0;i<max;i++) 
			this.value[i] = value[i];
	}
	
	public Point(Point2D geomPoint) {
		this.value[0] = geomPoint.getX();
		this.value[1] = geomPoint.getY();
	}
	
	public double get(int vertex) {
		return value[vertex];
	}
	
	public Point set(int vertex, double value) {
		this.value[vertex] = value;
		return this;
	}
	
	public Point set(Point other) {
		for(int i=0;i<DIMENSIONS;i++) 
			this.value[i] = other.value[i];
		return this;
	}
	
	public double[] value() {
		return value;
	}
	
	public double x() {
		return value[X];
	}
	public double y() {
		return value[Y];
	}
	public double z() {
		return value[Z];
	}
	
	public int xInt() {
		return (int)Math.round(value[X]);
	}
	public int yInt() {
		return (int)Math.round(value[Y]);
	}
	public int zInt() {
		return (int)Math.round(value[Z]);
	}
	
	public Point x(double value) {
		this.value[X] = value;
		return this;
	}
	public Point y(double value) {
		this.value[Y] = value;
		return this;
	}
	public Point z(double value) {
		this.value[Z] = value;
		return this;
	}
	
	public Point moveHalfTowards(Point extent) {
		for(int i=0;i<DIMENSIONS;i++)
			value[i] = 0.5*(extent.value[i] + value[i]);
		return this;
	}
	public Point middleOfSegment(Point extent) {
		Point p =new Point();
		for(int i=0;i<DIMENSIONS;i++)
			p.value[i] = 0.5*(extent.value[i] + value[i]);
		return p;
	}
	
	public Point add(Vector v) {
		for(int i=0;i<DIMENSIONS;i++)
			value[i] += v.value[i];
		return this;
	}
	public Point sub(Vector v) {
		for(int i=0;i<DIMENSIONS;i++)
			value[i] -= v.value[i];
		return this;
	}
	
	public Point cell(double multiplier) {
		for (int i = 0; i < DIMENSIONS; i++) 
			value[i] = Math.floor(value[i]*multiplier);
		
		return this;
	}
	
	public Point clone() {
		return new Point(value);
	}
	
	public int dimensions() {
		return value.length;
	}
	
	@Override
	public String toString() {
		String vertecies = "";
		for (double v:value) vertecies+=" "+v;
		return "Point"+DIMENSIONS+": ("+vertecies+")";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + Arrays.hashCode(value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Point other = (Point) obj;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}
}
