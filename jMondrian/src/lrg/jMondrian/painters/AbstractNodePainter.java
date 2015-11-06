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
import lrg.jMondrian.access.CaseCommand;
import lrg.jMondrian.access.Command;
import lrg.jMondrian.access.IObjectCommand;

public abstract class AbstractNodePainter<T> {

    protected IObjectCommand<? super T, ? extends Number> widthCommand;
    protected IObjectCommand<? super T, ? extends Number> heightCommand;
    protected IObjectCommand<? super T, ? extends Number> colorCommand;
    private IObjectCommand<? super T, ? extends Number> xCommand;
    private IObjectCommand<? super T, ? extends Number> yCommand;
    protected IObjectCommand<? super T, String> textCommand;
    protected IObjectCommand<? super T, String> nameCommand;
    protected IObjectCommand<? super T, ? extends Number> frameColorCommand;
    
    public AbstractNodePainter() {
        xCommand = yCommand = new Command<T,Double>() {
            public Double execute() {
                return 0d;
            }
        };
	}

    public AbstractNodePainter<T> width(IObjectCommand<? super T,? extends Number> widthCommand){
        this.widthCommand = widthCommand;
        return this;
    }
    public <N extends Number> AbstractNodePainter<T> width(N width){
        return width(new Command.Constant<T, N>(width));
    }
    public double getWidth(T entity){
        return this.widthCommand.setReceiver(entity).execute().doubleValue();
    }

    public AbstractNodePainter<T> height(IObjectCommand<? super T,? extends Number> heightCommand){
        this.heightCommand = heightCommand;
        return this;
    }
    public <N extends Number> AbstractNodePainter<T> height(N height){
        return height(new Command.Constant<T, N>(height));
    }
    public double getHeight(T entity){
        return heightCommand.setReceiver(entity).execute().doubleValue();
    }

    /**
     * @param colorCommand - command for obtaining the fill color from the model object
     * @return this
     */
    public AbstractNodePainter<T> color(IObjectCommand<? super T,? extends Number> colorCommand){
        this.colorCommand = colorCommand;
        return this;
    }
    public AbstractNodePainter<T> color(IObjectCommand<? super T, Boolean> colorGuard, Double colorValue) {
        this.colorCommand = new CaseCommand<T, Number>(this.colorCommand).newCase(colorGuard, colorValue);
        return this;
    }
    public AbstractNodePainter<T> color(IObjectCommand<? super T, Boolean> colorGuard, IObjectCommand<? super T, ? extends Number> colorValue) {
        this.colorCommand = new CaseCommand<T, Number>(this.colorCommand).newCase(colorGuard, colorValue);
        return this;
    }
    
    /**
     * @param colorCommand - command for obtaining the frame color from the model object
     * @return this
     */
    public AbstractNodePainter<T> frameColor(IObjectCommand<? super T,? extends Number> colorCommand){
        this.frameColorCommand = colorCommand;
        return this;
    }
    public AbstractNodePainter<T> frameColor(IObjectCommand<? super T, Boolean> colorGuard, Double colorValue) {
        this.frameColorCommand = new CaseCommand<T, Number>(this.frameColorCommand).newCase(colorGuard, colorValue);
        return this;
    }
    public AbstractNodePainter<T> frameColor(IObjectCommand<? super T, Boolean> colorGuard, IObjectCommand<? super T, ? extends Number> colorValue) {
        this.frameColorCommand = new CaseCommand<T, Number>(this.frameColorCommand).newCase(colorGuard, colorValue);
        return this;
    }
    
    public AbstractNodePainter<T> x(IObjectCommand<? super T,? extends Number> xCommand){
        this.xCommand = xCommand;
        return this;
    }

    public AbstractNodePainter<T> y(IObjectCommand<? super T,? extends Number> yCommand){
		this.yCommand = yCommand;
		return this;
    }

    public <N extends Number> AbstractNodePainter<T> x(N xValue){
        return x(new Command.Constant<T, N>(xValue));
    }
    public <N extends Number> AbstractNodePainter<T> y(N yValue){
        return y(new Command.Constant<T, N>(yValue));
    }

    public double getX(T entity){
       return xCommand.setReceiver(entity).execute().doubleValue();
    }

    public double getY(T entity){
       return yCommand.setReceiver(entity).execute().doubleValue();
    }

    public AbstractNodePainter<T> label(IObjectCommand<? super T, String> textCommand) {
        this.textCommand = textCommand;
        return this;
    }

    public AbstractNodePainter<T> name(IObjectCommand<? super T, String> nameCommand) {
        this.nameCommand = nameCommand;
        return this;
    }
    
    public String toString() {
        String desc = "";
        if(!nameCommand.toString().equals("")) {
            desc+="["+nameCommand.toString()+"] ";
        }
        if(!xCommand.toString().equals("")) {
            desc+="x["+xCommand.toString()+"] ";
        }
        if(!yCommand.toString().equals("")) {
            desc+="y["+yCommand.toString()+"] ";
        }
        if(!widthCommand.toString().equals("")) {
            desc+="width["+widthCommand.toString()+"] ";
        }
        if(!heightCommand.toString().equals("")) {
            desc+="height["+heightCommand.toString()+"] ";
        }
        if(!colorCommand.toString().equals("")) {
            desc+="color["+colorCommand.toString()+"] ";
        }
        return desc;
    }

    public abstract void paint(ViewRendererInterface window, T entity, double x1Bias, double y1Bias, boolean last);
    
    
    @Deprecated
    public AbstractNodePainter<T> width(lrg.jMondrian.commands.AbstractNumericalCommand widthCommand) {
    	return width(widthCommand.boxed());
    }

    @Deprecated
    public AbstractNodePainter<T> height(lrg.jMondrian.commands.AbstractNumericalCommand heightCommand){
    	return height(heightCommand.boxed());
    }
    
    @Deprecated
    public AbstractNodePainter<T> color(lrg.jMondrian.commands.AbstractNumericalCommand colorCommand){
    	return color(colorCommand.boxed());
    }

    @Deprecated
    public AbstractNodePainter<T> frameColor(lrg.jMondrian.commands.AbstractNumericalCommand colorCommand){
    	return frameColor(colorCommand.boxed());
    }
    
    @Deprecated
    public AbstractNodePainter<T> x(lrg.jMondrian.commands.AbstractNumericalCommand xCommand){
    	return x(xCommand.boxed());
    }
    
    @Deprecated
    public AbstractNodePainter<T> y(lrg.jMondrian.commands.AbstractNumericalCommand yCommand){
    	return y(yCommand.boxed());
    }
    
}
