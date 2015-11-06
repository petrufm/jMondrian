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
package lrg.jMondrian.figures;

import lrg.jMondrian.access.Command;
import lrg.jMondrian.access.IFigureCommand;
import lrg.jMondrian.layouts.AbstractLayout;
import lrg.jMondrian.layouts.FlowLayout;
import lrg.jMondrian.painters.AbstractEdgePainter;
import lrg.jMondrian.painters.AbstractNodePainter;
import lrg.jMondrian.util.IMondrianObserver;
import lrg.jMondrian.util.NullObserver;
import lrg.jMondrian.view.ViewRendererInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Figure<O> extends Node<O> {

    //Node interface
    private double[] dimension = null;

    public double getWidth() {
        if(dimension == null) {
            dimension = layout.applyLayout(nodes,edges);
        }
        return dimension[0] < minimumW ? minimumW : dimension[0];
    }

    public double getHeight() {
        if(dimension == null) {
            dimension = layout.applyLayout(nodes,edges);
        }
        return dimension[1] < minimumH ? minimumH : dimension[1];
    }

    public void show(ViewRendererInterface window, double x1Bias, double y1Bias, boolean last) {
    	observer.subTask("Drawing picture");
    	//observer.setReaminingWork(nodes.size() + edges.size(),IMondrianObserver.WorkPrecision.APPROXIMATE);
    	
        if(dimension == null) {
            dimension = layout.applyLayout(nodes, edges);
        }
        if(painter != null) {
            painter.width(new Command<Object, Double>() {
                public Double execute() {
                    return Figure.this.getWidth();
                }
            });
            painter.height(new Command<Object, Double>() {
                public Double execute() {
                    return Figure.this.getHeight();
                }
            });
            super.show(window, x1Bias, y1Bias, last);
        }
        for(int i = 0; i < nodes.size(); i++) {
            nodes.get(i).show(window,this.getAbsoluteX(), this.getAbsoluteY(), i == nodes.size() - 1);
            observer.worked(1);
        }
        for(int j = 0; j < edges.size(); j++) {
            edges.get(j).show(window);
            observer.worked(1);
        }
    }

    //Figure interface
    public Figure() {
        this(10,10);
    }

    private int minimumW, minimumH;

    public Figure(int minimumW, int minimumH) {
        super(null,null);
        layout = new FlowLayout();
        this.minimumW = minimumW;
        this.minimumH = minimumH;
        nodeLookUp = new HashMap<Object, Node<?>>();
    }

    private HashMap<Object,Node<?>> nodeLookUp;
    private List<Node<?>> nodes = new ArrayList<Node<?>>();
    private List<EdgeFigure<?>> edges = new ArrayList<EdgeFigure<?>>();
    private AbstractLayout layout;
    private IMondrianObserver observer = new NullObserver();

    public <N> void nodesUsing(List<N> nodes, AbstractNodePainter<? super N> painter) {
    	observer.subTask("Adding Nodes");
    	observer.setReaminingWork(2*nodes.size(),IMondrianObserver.WorkPrecision.APPROXIMATE);
        Iterator<N> it = nodes.iterator();
        while(it.hasNext()) {
            N anEntity = it.next();
            Node<N> aNode = new Node<N>(anEntity,painter);
            nodeLookUp.put(anEntity,aNode);
            this.nodes.add(aNode);
            observer.worked(1);
        }
    }

    public <F, N extends F> void nodesUsingForEach(List<N> nodes, AbstractNodePainter<? super F> painter, IFigureCommand<F> cmd) {
    	observer.subTask("Adding Nodes with content");
    	observer.setReaminingWork(2*nodes.size(),IMondrianObserver.WorkPrecision.APPROXIMATE);
        Iterator<N> it = nodes.iterator();
        while(it.hasNext()) {
            N anEntity = it.next();
            cmd.setReceiver(anEntity);
            Figure<F> aNode = cmd.setObserver(observer).describe();
            aNode.entity = anEntity;
            aNode.painter = painter;
            nodeLookUp.putAll(aNode.nodeLookUp);
            nodeLookUp.put(anEntity,aNode);
            this.nodes.add(aNode);
            observer.worked(1);
        }
    }

    public <E> void edgesUsing(List<E> edges, AbstractEdgePainter<? super E> painter) {
    	observer.subTask("Adding Edges");
    	observer.setReaminingWork(2*edges.size(),IMondrianObserver.WorkPrecision.APPROXIMATE);
        Iterator<E> it = edges.iterator();
        while(it.hasNext()) {
            E anEntity = it.next();
            Object f = painter.getFrom(anEntity);
            Object t = painter.getTo(anEntity);
            Node<?> ff = nodeLookUp.get(f);
            Node<?> tt = nodeLookUp.get(t);
            EdgeFigure<E> edge = new EdgeFigure<E>(anEntity,painter,ff,tt);
            this.edges.add(edge);
            observer.worked(1);
        }
    }

    public void layout(AbstractLayout layout) {
        this.layout = layout;
    }
    
    public Figure<O> observer(IMondrianObserver observer) {
		this.observer = observer;
		return this;
	}

    public void renderOn(ViewRendererInterface renderer) {
        show(renderer,0,0,false);
        observer.workComplete();
        renderer.setPreferredSize((int)this.getWidth(),(int)this.getHeight());
    }

}