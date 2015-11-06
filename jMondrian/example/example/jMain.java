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
package example;

import lrg.jMondrian.figures.Figure;
import lrg.jMondrian.painters.*;
import lrg.jMondrian.commands.*;
import lrg.jMondrian.layouts.*;
import lrg.jMondrian.view.SwingObserver;
import lrg.jMondrian.view.ViewRenderer;
import lrg.jMondrian.util.*; 

import java.util.ArrayList;

public class jMain {
	
    public static void main(String argv[]) {
	
		//Some numbers
		ComplexNumber a,b,c;
        ArrayList<ComplexNumber> nodes = new ArrayList<ComplexNumber>();
        nodes.add(a = new ComplexNumber(20,20));
        nodes.add(b = new ComplexNumber(100,100));
        nodes.add(c = new ComplexNumber(50,50));

		//Some relations (whatever they are)
		//When you don't have a first class entity for a relation use the placeholder
		ArrayList<RelationPlaceholder<ComplexNumber>> edges = new ArrayList<RelationPlaceholder<ComplexNumber>>();
		edges.add(new RelationPlaceholder<ComplexNumber>(b,a));
		edges.add(new RelationPlaceholder<ComplexNumber>(c,a));
				
		//Example 1
        Figure f = new Figure().observer(new SwingObserver());
        f.nodesUsing(nodes, new RectangleNodePainter(true).width(
			new AbstractNumericalCommand("Real") {
                public double execute() {
                     return ((ComplexNumber)receiver).x;
                }
        }).height(new AbstractNumericalCommand("Imag") {
                public double execute() {
                     return ((ComplexNumber)receiver).y;
                }
        }));
        f.layout(new FlowLayout());
        renderFigure(f);
        
		//Example 2
        f = new Figure().observer(new SwingObserver());
        f.nodesUsing(nodes, new EllipseNodePainter(10,10,true).x(
			new AbstractNumericalCommand("Real") {
                public double execute() {
                     return ((ComplexNumber)receiver).x;
                }
        }).y(new AbstractNumericalCommand("Imag") {
                public double execute() {
                     return ((ComplexNumber)receiver).y;
                }
        }).color(new LinearNormalizerColor(nodes,new AbstractNumericalCommand("Module") {
			public double execute() {
				return ((ComplexNumber)receiver).x*((ComplexNumber)receiver).x + ((ComplexNumber)receiver).y*((ComplexNumber)receiver).y;
			}
		})).name(new AbstractStringCommand("toString") {
			public String execute() {
				return receiver.toString();
			}
		}));
        f.layout(new ScatterPlotLayout());
        renderFigure(f);
		
		//Example 3
        f = new Figure().observer(new SwingObserver());
        f.nodesUsing(nodes, new RectangleNodePainter(10,10,true).name(new AbstractStringCommand("toString") {
			public String execute() {
				return receiver.toString();
			}
		}));
		f.edgesUsing(edges, new LineEdgePainter(new AbstractEntityCommand() {
			public Object execute() {
				return ((RelationPlaceholder)receiver).getSource();
			}
		}, new AbstractEntityCommand() {
			public Object execute() {
				return ((RelationPlaceholder)receiver).getDestination();
			}			
		}));
        f.layout(new TreeLayout(50,50));
        renderFigure(f);

		//Example 4
        f = new Figure().observer(new SwingObserver());
        f.nodesUsingForEach(nodes, new RectangleNodePainter(10,10,true).name(new AbstractStringCommand("toString") {
			public String execute() {
				return receiver.toString();
			}
		}), new AbstractFigureDescriptionCommand() {
			public Figure describe() {
				Figure in = new Figure().observer(observer);
				ArrayList list = new ArrayList();
				//If a.equels(b) == true then a and b cannot have two distinct visual representattions!
				list.add(new Integer(((ComplexNumber)receiver).x));
				list.add(new Integer(((ComplexNumber)receiver).y*2));
				in.nodesUsing(list,new RectangleNodePainter(20,20,true).name(new AbstractStringCommand("Name") {
					public String execute() {
						return receiver.toString();
					}
				}));
				in.layout(new FlowLayout());
				return in;
			}
		});
		f.edgesUsing(edges, new LineEdgePainter(new AbstractEntityCommand() {
			public Object execute() {
				return ((RelationPlaceholder)receiver).getSource();
			}
		}, new AbstractEntityCommand() {
			public Object execute() {
				return ((RelationPlaceholder)receiver).getDestination();
			}			
		}));
        f.layout(new TreeLayout(50,50));
        renderFigure(f);
        
    }

    private static void renderFigure(Figure<?> f) {
        ViewRenderer r = new ViewRenderer();
        f.renderOn(r);
        r.open();
    }
    
    
}
