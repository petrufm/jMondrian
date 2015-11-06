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

import lrg.jMondrian.painters.*;
import lrg.jMondrian.view.ViewRendererInterface;

public class EdgeFigure<E> extends AbstractFigure<E> {

	public static abstract class NodeConnection {
        public abstract double computeX(Node<?> n);
        public abstract double computeY(Node<?> n);
	}

    public static final NodeConnection MIDDLE = new NodeConnection() {
        public double computeX(Node<?> n) {
            return n.getAbsoluteX() + n.getWidth() / 2;
        }
        public double computeY(Node<?> n) {
            return n.getAbsoluteY() + n.getHeight() / 2;
        }
    };

    public static final NodeConnection UP_MIDDLE = new NodeConnection() {
        public double computeX(Node<?> n) {
            return n.getAbsoluteX() + n.getWidth() / 2;
        }
        public double computeY(Node<?> n) {
            return n.getAbsoluteY();
        }
    };

    public static final NodeConnection DOWN_MIDDLE = new NodeConnection() {
        public double computeX(Node<?> n) {
            return n.getAbsoluteX() + n.getWidth() / 2;
        }
        public double computeY(Node<?> n) {
            return n.getAbsoluteY() + n.getHeight();
        }
    };

    public static final NodeConnection CENTER_LEFT = new NodeConnection() {
        public double computeX(Node<?> n) {
            return n.getAbsoluteX();
        }
        public double computeY(Node<?> n) {
            return n.getAbsoluteY() + n.getHeight()/2;
        }
    };

    public static final NodeConnection CENTER_RIGHT = new NodeConnection() {
        public double computeX(Node<?> n) {
            return n.getAbsoluteX() + n.getWidth();
        }
        public double computeY(Node<?> n) {
            return n.getAbsoluteY() + n.getHeight()/2;
        }
    };    

    
    public static abstract class Connection {
        public abstract double computeFromX(EdgeFigure<?> e);
        public abstract double computeFromY(EdgeFigure<?> e);
        public abstract double computeToX(EdgeFigure<?> e);
        public abstract double computeToY(EdgeFigure<?> e);
    }
    
    public static class BasicConnection extends Connection {
    	private NodeConnection from, to;
    	
    	public BasicConnection(NodeConnection from, NodeConnection to) {
    		this.from = from;
    		this.to = to;
		}
    	
        public double computeFromX(EdgeFigure<?> e) {	return from.computeX(e.getFrom()); }
        public double computeFromY(EdgeFigure<?> e) {	return from.computeY(e.getFrom()); }
        public double computeToX(EdgeFigure<?> e) {		return to.computeX(e.getTo()); }
        public double computeToY(EdgeFigure<?> e) {		return to.computeY(e.getTo()); }
    }
    
    public static final Connection AUTO = new AutoConnection();    
    
    
    private Node<?> from;
    private Node<?> to;
    private AbstractEdgePainter<? super E> painter;
    private Connection connection;

    public EdgeFigure(E entity, AbstractEdgePainter<? super E> painter, Node<?> from, Node<?> to){
        super(entity);
        this.painter = painter;
        this.from = from;
        this.to = to;
        connection = new BasicConnection(MIDDLE, MIDDLE);
    }

    public Node<?> getFrom() {
        return from;
    }

    public Node<?> getTo() {
        return to;
    }

    public void setConnectionStyle(Connection connection) {
    	this.connection = connection;
    }
    public void setConnectionStyle(NodeConnection from, NodeConnection to) {
    	this.connection = new BasicConnection(from, to);
    }

    public void show(ViewRendererInterface renderer) {
        painter.paint(renderer, entity, 
        		connection.computeFromX(this), connection.computeFromY(this), 
        		connection.computeToX(this), connection.computeToY(this));
    }
    
    
}

class AutoConnection extends EdgeFigure.Connection {
	
	@Override
	public double computeFromX(EdgeFigure<?> e) {
		Node<?> f = e.getFrom(), t = e.getTo();
		double 
			x1=f.getAbsoluteX(),y1=f.getAbsoluteY(),
			x2=t.getAbsoluteX(),y2=t.getAbsoluteY();
		
		double dy = y2-y1, ady = 2*Math.abs(dy);
		double th = f.getHeight()+t.getHeight();

		if (ady>th)
			return x1+f.getWidth()/2;
		else {
			if (x1<x2) return x1+f.getWidth();  
			else if (x1>x2) return x1-1;
			else return x1+f.getWidth()/2;
		}
	}
	
	@Override
	public double computeFromY(EdgeFigure<?> e) {
		Node<?> f = e.getFrom(), t = e.getTo();
		double 
			y1=f.getAbsoluteY(),
			y2=t.getAbsoluteY();

		double dy = y2-y1, ady = 2*Math.abs(dy);
		double th = f.getHeight()+t.getHeight();

		if (ady<=th)
			return y1+f.getHeight()/2;
		else {
			if (y1<y2) return y1+f.getHeight();  
			else return y1-1; 
		}
	}
	
	@Override
	public double computeToX(EdgeFigure<?> e) {
		Node<?> f = e.getFrom(), t = e.getTo();
		double 
			x1=f.getAbsoluteX(),
			x2=t.getAbsoluteX();

		double dx = x2-x1, adx = 2*Math.abs(dx);
		double th = f.getHeight()+t.getHeight();
		
		if (adx<=th)
			return x2+t.getWidth()/2;
		else {
			if (x1<x2) return x2-1;  
			else return x2+t.getWidth(); 
		}
	}
	@Override
	public double computeToY(EdgeFigure<?> e) {
		Node<?> f = e.getFrom(), t = e.getTo();
		double 
			x1=f.getAbsoluteX(),y1=f.getAbsoluteY(),
			x2=t.getAbsoluteX(),y2=t.getAbsoluteY();

		double dx = x2-x1, adx = 2*Math.abs(dx);
		double th = f.getHeight()+t.getHeight();
		
		if (adx>th)
			return y2+t.getHeight()/2;
		else {
			if (y1<y2) return y2-1;  
			else if (y1>y2) return y2+t.getHeight();
			else return y2+t.getHeight()/2;
		}
	}
}
