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
package lrg.jMondrian.commands;

import lrg.jMondrian.access.AbstractCommand;
import lrg.jMondrian.access.Command;
import lrg.jMondrian.access.IObjectCommand;

@Deprecated
public abstract class AbstractNumericalCommand extends AbstractCommand<Object> {

    public AbstractNumericalCommand() {
        this("");
    }

    public AbstractNumericalCommand(String prop) {
    	super(prop);
    }

    @Override
    public AbstractNumericalCommand setReceiver(Object receiver) {
    	super.setReceiver(receiver);
    	return this;
    }
    
    @Override
    public String executeResultAsString() {
    	return execute()+"";
    }
    
    public <R> IObjectCommand<R, Double> boxed() {
    	return new Command<R, Double>(name()) {
    		@Override
    		public Double execute() {
    			return AbstractNumericalCommand.this.setReceiver(receiver).execute();
    		}
		};
    }
    
	public abstract double execute();
    
}
