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
import lrg.jMondrian.access.IObjectCommand;

public class Node<T> extends AbstractFigure<T> {

	private IObjectCommand<? super T, ? extends Number> relativeX, relativeY;
	private boolean translated = false;
	
    private double absoluteX = 0, absoluteY = 0;
    protected AbstractNodePainter<? super T> painter;

    public Node(T entity, AbstractNodePainter<? super T> painter){
        super(entity);
        this.painter = painter;
    }

    public double getWidth() {
        return painter.getWidth(entity);
    }

    public double getHeight() {
        return painter.getHeight(entity);
    }

    public double getRelativeX() {
        return translated ? relativeX.setReceiver(entity).execute().doubleValue() : painter.getX(entity);
    }

    public double getRelativeY() {
        return translated ? relativeY.setReceiver(entity).execute().doubleValue() : painter.getY(entity);
    }

    public double getAbsoluteX() {
        return absoluteX;
    }

    public double getAbsoluteY() {
        return absoluteY;
    }

    public void show(ViewRendererInterface renderer, double xBias, double yBias, boolean last) {
    	if (translated) {
            absoluteX = xBias + relativeX.setReceiver(entity).execute().doubleValue();
            absoluteY = yBias + relativeY.setReceiver(entity).execute().doubleValue();
            painter.paint(renderer, entity, absoluteX-painter.getX(entity), absoluteY-painter.getY(entity), last);
    	} else {
            absoluteX = painter.getX(entity) + xBias;
            absoluteY = painter.getY(entity) + yBias;
            painter.paint(renderer, entity, xBias, yBias, last);
    	}
    }

    public void translateTo(IObjectCommand<? super Object, ? extends Number> xCmd, IObjectCommand<? super Object, ? extends Number> yCmd) {
    	relativeX = xCmd;
    	relativeY = yCmd;
    	translated = true;
    }
    
    @Deprecated
    public void translateTo(lrg.jMondrian.commands.AbstractNumericalCommand xCmd, lrg.jMondrian.commands.AbstractNumericalCommand yCmd) {
    	translateTo(xCmd.boxed(), yCmd.boxed());
    }
    
}