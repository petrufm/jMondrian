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
package lrg.jMondrian.util;

import java.awt.Shape;
import java.awt.geom.PathIterator;

public class StaticFigures {

	public static double[] getArrow(Shape curve) {
    	PathIterator pi = curve.getPathIterator(null,0.7);
    	double x1=0, y1=0, x2=0, y2=0;
    	
    	while(!pi.isDone()) {
    		double point[] = new double[6];
    		int ret = pi.currentSegment(point);
    		pi.next();
    		if (ret==PathIterator.SEG_LINETO || ret==PathIterator.SEG_MOVETO) {
    			x1 = x2; y1 = y2;
    			x2 = point[0]; y2 = point[1];
    		}
    	}
		return getArrow(x1,y1,x2,y2);
	}
	
	public static double[] getArrow(double x1, double y1, double x2, double y2) {
        double epsilon = Math.PI / 6;
        double dist = 10;
        double angleWithOY;
        if(x2 - x1 != 0 || y2 - y1 != 0) {
            if(x2 - x1 == 0) {
                if(y2 - y1 > 0) angleWithOY = 0;
                else angleWithOY = Math.PI;
            } else if(y2 - y1 == 0) {
                if(x2 - x1 > 0) angleWithOY = Math.PI / 2;
                else angleWithOY = Math.PI / 2 + Math.PI;
            } else {
                angleWithOY = Math.atan(((double)(x2 - x1)) / (y2 - y1));
                if(y2 - y1 < 0) {
                    angleWithOY = angleWithOY + Math.PI;
                }
            }
            double xa = x2 - dist / Math.cos(epsilon) * Math.cos(Math.PI / 2 - angleWithOY - epsilon);
            double ya = y2 - dist / Math.cos(epsilon) * Math.sin(Math.PI / 2 - angleWithOY - epsilon);
            double xap= x2 - dist / Math.cos(epsilon) * Math.cos(Math.PI / 2 - angleWithOY + epsilon);
            double yap= y2 - dist / Math.cos(epsilon) * Math.sin(Math.PI / 2 - angleWithOY + epsilon);
            return new double[]{xa,ya,x2,y2,xap,yap,x2,y2};
        }
        return null;
	}	
}
