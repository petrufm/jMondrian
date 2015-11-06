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

import lrg.jMondrian.view.ViewRendererInterface;
import lrg.jMondrian.access.Command;
import lrg.jMondrian.util.CommandColor;

import java.awt.Color;

public class EllipseNodePainter<T> extends AbstractNodePainter<T> {

    private boolean withBorders = true;

    public EllipseNodePainter(boolean withBorders){
        this(0, 0,withBorders);
    }

    public EllipseNodePainter(final double width, final double height, boolean withBorders){

        this.withBorders = withBorders;

        nameCommand = new Command.Constant<T, String>("");

        widthCommand = new Command.Constant<T, Double>(width);

        heightCommand = new Command.Constant<T, Double>(height);

        textCommand = new Command.Constant<T, String>("");

        frameColorCommand = CommandColor.BLACK;
        colorCommand = CommandColor.WHITE;
    }

    public void paint(ViewRendererInterface window, T entity, double x1Bias, double y1Bias, boolean last) {

        double color, frameColor = 0;
        boolean invisibleBorder = false;
        boolean invisibleContent = false;
        
        try {
            color = colorCommand.setReceiver(entity).execute().doubleValue();
        } catch (CommandColor.InvisibleException e) {
        	invisibleContent = true;
        	color = CommandColor.transparentColor(); // white with alpha set to transparent
        }

        try {
            frameColor = frameColorCommand.setReceiver(entity).execute().doubleValue();
        } catch (CommandColor.InvisibleException e) {
            invisibleBorder = true;
            if (invisibleContent) return;
        }

        double x = x1Bias;
        double y = y1Bias;

        widthCommand.setReceiver(entity);
        double width = widthCommand.execute().doubleValue();

        heightCommand.setReceiver(entity);
        double height = heightCommand.execute().doubleValue();

        x += getX(entity);
        y += getY(entity);

        nameCommand.setReceiver(entity);

        if(withBorders) {
            if(!invisibleBorder)
                window.getShapeFactory().addEllipse(entity, this.toString(),(int)x,(int)y,(int)width,(int)height,(int)color, (int)frameColor);
            else {
                window.getShapeFactory().addEllipse(entity, this.toString(),(int)x,(int)y,(int)width,(int)height,(int)color, false);                
            }
        } else {
            window.getShapeFactory().addEllipse(entity, this.toString(),(int)x,(int)y,(int)width,(int)height,(int)color, false);
        }

        textCommand.setReceiver(entity);
        window.getShapeFactory().addText(entity, this.toString(), textCommand.execute(), (int)x, (int)y - 5, Color.BLACK.getRGB());
    }

}
