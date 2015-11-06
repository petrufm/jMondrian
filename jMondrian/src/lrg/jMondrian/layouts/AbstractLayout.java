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

import java.util.List;
import java.util.HashMap;

import lrg.jMondrian.access.Command;
import lrg.jMondrian.figures.Node;
import lrg.jMondrian.figures.EdgeFigure;

public abstract class AbstractLayout {

    public final <N,E> double[] applyLayout(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
        return distributeNodes(nodeList,edgeList);
    }

    protected abstract double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList);

    protected static class ControlXY extends Command<Object,Double> {
        private HashMap<Object,Double> m = new HashMap<Object,Double>();
        public Double execute() {
        	Double x=m.get(receiver);
        	return x;
        }
        public void link(Node<?> n, double pos) {
            m.put(n.getEntity(),pos);
        }
    }
}
