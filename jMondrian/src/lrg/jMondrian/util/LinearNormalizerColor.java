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
package lrg.jMondrian.util;

import lrg.jMondrian.access.Command;
import lrg.jMondrian.access.IObjectCommand;

import java.util.List;
import java.awt.*;


public class LinearNormalizerColor<T> extends Command<T, Double> {

    private double min,max;
    private IObjectCommand<? super T, ? extends Number> command;

    @Deprecated
	public LinearNormalizerColor(List<T> context, lrg.jMondrian.commands.AbstractNumericalCommand cmd) {
    	this(context,cmd.boxed());
    }
    
    public LinearNormalizerColor(List<T> context, IObjectCommand<? super T, ? extends Number> cmd) {
        command = cmd;
        command.setReceiver(context.get(0));
        min = max = cmd.execute().doubleValue();
        for(int i = 1; i < context.size(); i++) {
        	command.setReceiver(context.get(i));
            double tmp = cmd.execute().doubleValue();
            if(tmp > max) max = tmp;
            if(tmp < min) min = tmp;
        }
    }

    public Double execute() {
        command.setReceiver(receiver);
        double aux = command.execute().doubleValue();
        double pas = max - min;
        int r,g,b;
        r = g = b =(int)(255 -  ((aux - min)*  255 / pas));
        return Double.valueOf(new Color(r,g,b).getRGB());
    }

    public String toString() {
        command.setReceiver(receiver);
        return command.toString();
    }
    
}
