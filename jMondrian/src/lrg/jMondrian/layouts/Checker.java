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

import lrg.jMondrian.access.IObjectCommand;
import lrg.jMondrian.figures.EdgeFigure;
import lrg.jMondrian.figures.Node;

import java.util.*;

public class Checker extends AbstractLayout {

    private int prefferedWidth = 1500;
    private final IObjectCommand<? super Object, ? extends Number> measure;
    private final Comparator<? super Object> cmp;
    private int xDist = 5, yDist = 5;
    
    public Checker(IObjectCommand<? super Object, ? extends Number> measure) {
        this.measure = measure;
        cmp = null;
    }

    public Checker(IObjectCommand<? super Object,? extends Number> measure, int maxWidth) {
        this(measure);
        this.prefferedWidth = maxWidth;
    }

    public Checker(Comparator<? super Object> cmp) {
    	measure = null;
    	this.cmp = cmp;
    }

    public Checker(Comparator<? super Object> cmp, int maxWidth) {
    	this(cmp);
    	this.prefferedWidth = maxWidth;
    }

    @Deprecated
    public Checker(lrg.jMondrian.commands.AbstractNumericalCommand measure) {
    	this(measure.boxed());
    }
    
    @Deprecated
    public Checker(lrg.jMondrian.commands.AbstractNumericalCommand measure, int maxWidth) {
    	this(measure.boxed(),maxWidth);
    }
    
    protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {

        //Sort the list
        List<Node<?>> tmpList = new ArrayList<Node<?>>();
        tmpList.addAll(nodeList);
        
        if(measure != null) {
        	Collections.sort(tmpList, new Comparator<Node<?>>() {
        		public int compare(Node<?> a, Node<?> b) {
        			measure.setReceiver(a.getEntity());
        			double x = measure.execute().doubleValue();
        			measure.setReceiver(b.getEntity());
        			double y = measure.execute().doubleValue();
        			return (int)x - (int)y;
        		}
        	});
        } else {
        	Collections.sort(tmpList,new Comparator<Node<?>>() {
				public int compare(Node<?> o1, Node<?> o2) {
					return cmp.compare(o1.getEntity(), o2.getEntity());
				}        		
        	});
        }

        //Flow
        double nextX = xDist;
        double nextY = yDist;
        double currentWidth;
        double currentHeight;
        double maxHeight = 0;
        double maxWidth = 0;

        ControlXY xCmd = new ControlXY();
        ControlXY yCmd = new ControlXY();

        Iterator<Node<?>> it = tmpList.iterator();
        while(it.hasNext()) {

        	Node<?> figure = it.next();

            currentWidth =  figure.getWidth();
            if((nextX != xDist) && (nextX + xDist + currentWidth > prefferedWidth)) {
                nextY += maxHeight + yDist;
                nextX = xDist;
                maxHeight = 0;
            }

            xCmd.link(figure,nextX);
            yCmd.link(figure,nextY);

            nextX += xDist + currentWidth;

            if(nextX > maxWidth) {
                maxWidth = nextX;
            }

            currentHeight = figure.getHeight();
            if(maxHeight < currentHeight) {
                maxHeight = currentHeight;
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
}
