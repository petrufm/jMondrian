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
import lrg.jMondrian.util.CommandColor;

public abstract class AbstractEdgePainter<E> {

    protected IObjectCommand<? super E,?> fromCommand;
    protected IObjectCommand<? super E,?> toCommand;
    protected IObjectCommand<? super E,? extends Number> colorCommand;
    protected IObjectCommand<? super E,String> nameCommand;

    public AbstractEdgePainter(IObjectCommand<? super E,?> fromCommand, IObjectCommand<? super E,?> toCommand) {
        this.fromCommand = fromCommand;
        this.toCommand = toCommand;
        colorCommand = CommandColor.BLACK;
        nameCommand = new Command.Constant<E,String>("");
    }

    public AbstractEdgePainter<E> color(IObjectCommand<? super E, ? extends Number> colorCommand) {
        this.colorCommand = colorCommand;
        return this;
    }
    public AbstractEdgePainter<E> color(IObjectCommand<? super E, Boolean> colorGuard, Double colorValue) {
        this.colorCommand = new CaseCommand<E, Number>(this.colorCommand).newCase(colorGuard, colorValue);
        return this;
    }
    public AbstractEdgePainter<E> color(IObjectCommand<? super E, Boolean> colorGuard, IObjectCommand<? super E, ? extends Number> colorValue) {
        this.colorCommand = new CaseCommand<E, Number>(this.colorCommand).newCase(colorGuard, colorValue);
        return this;
    }
    
    @Deprecated
    public AbstractEdgePainter<E> color(lrg.jMondrian.commands.AbstractNumericalCommand colorCommand){
        return color(colorCommand.boxed());
    }

    public AbstractEdgePainter<E> name(IObjectCommand<? super E,String> nameCommand){
        this.nameCommand = nameCommand;
        return this;
    }

    public abstract void paint(ViewRendererInterface window, E entity, double x1Bias, double y1Bias, double x2Bias, double y2Bias);

    public final Object getFrom(E entity) {
        fromCommand.setReceiver(entity);
        return fromCommand.execute();
    }

    public final Object getTo(E entity) {
        toCommand.setReceiver(entity);
        return toCommand.execute();
    }

    public String toString() {
        String desc = "";
        if(!nameCommand.toString().equals("")) {
            desc+="["+nameCommand.toString()+"] ";
        }
        if(!fromCommand.toString().equals("")) {
            desc+="from["+fromCommand.toString()+"] ";
        }
        if(!toCommand.toString().equals("")) {
            desc+="to["+toCommand.toString()+"] ";
        }
        if(!colorCommand.toString().equals("")) {
            desc+="color["+colorCommand.toString()+"] ";
        }
        return desc;
    }

}
