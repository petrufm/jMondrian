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
package lrg.jMondrian.painters;

import lrg.jMondrian.access.IObjectCommand;
import lrg.jMondrian.util.CommandColor;
import lrg.jMondrian.view.ViewRendererInterface;

public class LineEdgePainter<E> extends AbstractEdgePainter<E> {

    private boolean oriented;

    public LineEdgePainter(IObjectCommand<? super E,?> fromCommand, IObjectCommand<? super E,?> toCommand){
        this(fromCommand,toCommand,false);
    }

    public LineEdgePainter(IObjectCommand<? super E,?> fromCommand, IObjectCommand<? super E,?> toCommand, boolean oriented){
        super(fromCommand,toCommand);
        this.oriented = oriented;
    }
    
    protected boolean isOriented() {
		return oriented;
	}

    public void paint(ViewRendererInterface window, E entity, double x1Bias, double y1Bias, double x2Bias, double y2Bias) {
        try {
            colorCommand.setReceiver(entity);
            fromCommand.setReceiver(entity);
            toCommand.setReceiver(entity);
            nameCommand.setReceiver(entity);
            int color = colorCommand.execute().intValue();
            window.getShapeFactory().addLine(entity,this.toString(),(int)x1Bias,(int)y1Bias,(int)x2Bias,(int)y2Bias,color, oriented);
        } catch(CommandColor.InvisibleException e) {
            return;
        }
    }

}
