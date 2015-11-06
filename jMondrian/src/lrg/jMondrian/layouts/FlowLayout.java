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
package lrg.jMondrian.layouts;

import lrg.jMondrian.figures.*;

import java.util.List;
import java.util.Iterator;

public class FlowLayout extends AbstractLayout {

    private double prefferedWidth;
    private double xDist, yDist;

    public FlowLayout() {
        this(5,5,0);
    }

    public FlowLayout(double maxWidth) {
        this.prefferedWidth = maxWidth;
        xDist = 5;
        yDist = 5;
    }

    public FlowLayout(double xDist, double yDist, double maxWidth) {
        this.prefferedWidth = maxWidth;
        this.xDist = xDist;
        this.yDist = yDist;
    }

    protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
        double nextX = xDist;
        double nextY = yDist;
        double lastHeight;
        double maxHeight = 0;
        double maxWidth = 0;

        ControlXY xCmd = new ControlXY();
        ControlXY yCmd = new ControlXY();

        Iterator<Node<?>> it = nodeList.iterator();
        while(it.hasNext()) {

            Node<?> figure = it.next();
            xCmd.link(figure,nextX);
            yCmd.link(figure,nextY);

            nextX += xDist + figure.getWidth();
            if(nextX >= maxWidth) {
                maxWidth = nextX;
            }
            lastHeight = figure.getHeight();
            if(maxHeight < lastHeight) {
                maxHeight = lastHeight;
            }
            if(prefferedWidth != 0 && nextX > prefferedWidth) {
                nextY += maxHeight + yDist;
                nextX = xDist;
                maxHeight = 0;
            }
        }

		for(int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).translateTo(xCmd,yCmd);
        }

        double rez[] = new double[2];
        rez[0] = maxWidth;
        rez[1] = maxHeight + nextY + yDist;

        return rez;
    }

    
    public static class Vertical extends FlowLayout {
        public Vertical() {
        	super();
        }

        public Vertical(double maxHeight) {
        	super(maxHeight);
        }

        public Vertical(double xDist, double yDist, double maxHeight) {
        	super(xDist,yDist,maxHeight);
        }
        
        protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
            double nextX = super.xDist;
            double nextY = super.yDist;
            double lastWidth;
            double maxHeight = 0;
            double maxWidth = 0;

            ControlXY xCmd = new ControlXY();
            ControlXY yCmd = new ControlXY();

            Iterator<Node<?>> it = nodeList.iterator();
            while(it.hasNext()) {

                Node<?> figure = it.next();
                xCmd.link(figure,nextX);
                yCmd.link(figure,nextY);

                nextY += super.yDist + figure.getHeight();
                if(nextY >= maxHeight) {
                	maxHeight = nextY;
                }
                lastWidth = figure.getWidth();
                if(maxWidth < lastWidth) {
                	maxWidth = lastWidth;
                }
                if(super.prefferedWidth != 0 && nextY > super.prefferedWidth) {
                    nextX += maxWidth + super.xDist;
                    nextY = super.yDist;
                    maxWidth = 0;
                }
            }

    		for(int i = 0; i < nodeList.size(); i++) {
                nodeList.get(i).translateTo(xCmd,yCmd);
            }

            double rez[] = new double[2];
            rez[0] = maxWidth + nextX + super.xDist;
            rez[1] = maxHeight;

            return rez;
        }        
    	
    }
}