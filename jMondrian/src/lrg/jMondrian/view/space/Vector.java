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

import java.awt.geom.Line2D;
import java.io.Serializable;

public class Vector extends Point implements Serializable {
	private static final long serialVersionUID = 1L;
	// private Double length; 

	private Vector() {
		super();
	}
	
	public Vector(double... value) {
		super(value);
	}
	
	public Vector(Point from, Point to) {
		super();
		for (int i=0; i<DIMENSIONS; i++)
			// to = from + this => this = to - from
			value[i] = to.value[i] - from.value[i];
	}

	public Vector(Line2D geomLine) {
		super();
		value[0] = geomLine.getX2() - geomLine.getX1();
		value[1] = geomLine.getY2() - geomLine.getY1();
	}
	
	public double lengthSquared() {
		double lengthSquared = 0d;
		for (int i=0; i<DIMENSIONS; i++)
			lengthSquared += value[i]*value[i];
		return lengthSquared; 
	}
	
	public double lengthSummed() {
		double lengthSummed = 0d;
		for (int i=0; i<DIMENSIONS; i++) {
			double v = value[i];
			lengthSummed += (v>=0) ? v : -v; 
		}
		return lengthSummed; 
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	public Vector add(Vector v) {
		super.add(v);
		return this;
	}
	
	public Vector sub(Vector v) {
		super.sub(v);
		return this;
	}
	
	public Vector clone() {
		return new Vector(value);
	}
	
	public double vertexProduct() {
		double result = 1d;
		for (double v : value)
			result *=v;
		return result;
	}
	
	public Vector cloneAndScalarProduct(double multiplier) {
		Vector n = new Vector(); 
		for(int i=0;i<DIMENSIONS;i++) 
			n.value[i] = value[i]*multiplier;
		return n;
	}
	
	public Vector scalarProduct(double multiplier) {
		for (int i = 0; i < DIMENSIONS; i++) 
			value[i] *= multiplier;
		
		return this;
	}
	
	/**
	 * returns the projection of other on this
	 * 
	 * 			  ^'
	 *     other /|
	 *          / | 
	 *         /  |
	 *        /   |
	 *       o--->+--------> this
	 *         ^result
	 */
	public Vector projectionOn(Vector other) {
		return other.cloneAndScalarProduct(
			scalarProduct(other)/other.lengthSquared()
		);
	}

	/**
	 * return the projection of projectedPoint on the line determined by 
	 * basePoint and this
	 * 
	 * 			  o <- projectedPoint
	 *           /|
	 *          / | 
	 *         /  |
	 *        /   |
	 *   +-> o----o--------> this
	 *   |        ^result
	 * basePoint  
	 * WARNING basePoint is modified!!!
	 */
	public Point projectionOf(Point basePoint, Point projectedPoint) {
		return basePoint.add(new Vector(basePoint,projectedPoint).projectionOn(this));
	}

	/**
	 * return a vector normal to this with it's length equal to the distance from 
	 * projectionPoint to the line determined by basePoint and this
	 * 
	 * 			  o <- projectedPoint
	 *           /^
	 *          / | <- result
	 *         /  |
	 *        /   |
	 *   +-> o----o--------> this
	 *   |        
	 * basePoint  
	 */
	public Vector projectingVectorOf(Point basePoint, Point projectedPoint) {
		Vector baseToPoint = new Vector(basePoint,projectedPoint); 
		return baseToPoint.sub(baseToPoint.projectionOn(this));
	}
	
	public Vector projectingVectorOf(Vector other) {
		return other.clone().sub(other.projectionOn(this));
	}
	
	public double scalarProduct(Vector multiplier) {
		double result = 0d;
		for (int i = 0; i < DIMENSIONS; i++) 
			result += value[i]*multiplier.value[i];
		
		return result;
	}
	
	public Vector matrixProduct(Vector... matrix) {
		Vector self = this.clone();
		for (int i = 0; i < DIMENSIONS; i++)
			set(i, self.scalarProduct(matrix[i]));
		return this;
	}
	
	public Vector normalize() {
		double length = length();
		if (length>0)
			return scalarProduct(1/length);
		return this;
	}

	public Vector length(double length) {
		double olen = length();
		if (olen>0)
			return scalarProduct(length/olen);
		return this;
	}
	
	public Vector cloneWithLength(double length) {
		double olen = length();
		if (olen>0)
			return cloneAndScalarProduct(length/olen);
		return clone();
	}
	
	public Vector rotateXY90CW() {
		/*
		 * -pi/2
		 * 
		 *  ( cos 	-sin 	0)	(0	1	0)  (x) 	(y)
		 *  ( sin 	cos		0)->(-1	0	0)  (y) = 	(-x)
		 *   0		0		1)	(0	0	1)  (z)		(z)
		 */
		double temp;
		temp = value[Y]; value[Y] = -value[X]; value[X] = temp;
		return this;
	}
	public Vector rotateXY90CCW() {
		/*
		 * pi/2
		 * 
		 *  ( cos 	-sin 	0)	(0	-1	0)  (x) 	(-y)
		 *  ( sin 	cos		0)->(1	0	0)  (y) = 	(x)
		 *   0		0		1)	(0	0	1)  (z)		(z)
		 */
		double temp;
		temp = -value[Y]; value[Y] = value[X]; value[X] = temp;
		return this;
	}

	public Vector rotateXY(double radians) {
		return matrixProduct(xyRotationMatrix3(radians));
		/*
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		double newX = cos*value[X] - sin*value[Y];
		double newY = sin*value[X] + cos*value[Y];
		value[X] = newX; value[Y] = newY;
		
		return this; */
	}
	
	public double angleRadXY() {
		return Math.atan2(value[Y], value[X]);
	}
	
	
	@Override
	public String toString() {
		String vertecies = "";
		for (double v:value) vertecies+=" "+v;
		return "Vector"+DIMENSIONS+": ("+vertecies+")";
	}
	
	
	public static Vector[] xyRotationMatrix3(double radians) {
		Vector[] result = new Vector[3];
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		
		result[0] = new Vector(	cos,	-sin,	0);
		result[1] = new Vector(	sin,	cos,	0);
		result[2] = new Vector(	0,		0,		1);
		return result;
	}
	
	public static Vector[] xyRotationMatrix3(double sin, double cos) {
		Vector[] result = new Vector[3];
		
		result[0] = new Vector(	cos,	-sin,	0);
		result[1] = new Vector(	sin,	cos,	0);
		result[2] = new Vector(	0,		0,		1);
		return result;
	}
}
