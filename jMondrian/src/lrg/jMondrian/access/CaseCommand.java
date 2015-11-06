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
package lrg.jMondrian.access;

import java.util.ArrayList;
import java.util.List;

public class CaseCommand<R,T> extends Command<R, T> {
	private List<Case<R, T>> cases = new ArrayList<Case<R,T>>();
	private IObjectCommand<? super R, ? extends T> defaultValue;

	public CaseCommand(T defaultValue) {
		this.defaultValue = new Command.Constant<R, T>(defaultValue);
	}
	public CaseCommand(IObjectCommand<? super R, ? extends T> defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public final T execute() {
		for(Case<R, T> caseWithValue : cases) 
			if (caseWithValue.evaluate(receiver)) 
				return caseWithValue.getValue(receiver);
		return defaultValue.setReceiver(receiver).execute();
	}
	
	public CaseCommand<R,T> newCase(IObjectCommand<? super R,Boolean> term, T value) {
		cases.add(new Case<R,T>(term,value));
		return this;
	}
	public CaseCommand<R,T> newCase(IObjectCommand<? super R,Boolean> term, IObjectCommand<? super R,? extends T> value) {
		cases.add(new Case<R,T>(term,value));
		return this;
	}
}

class Case<R,T> {
	private IObjectCommand<? super R,Boolean> term;
	private IObjectCommand<? super R,? extends T> value;

    public Case(IObjectCommand<? super R,Boolean> term, T value) {
        this.term = term;
        this.value = new Command.Constant<R, T>(value);
    }

    public Case(IObjectCommand<? super R,Boolean> term, IObjectCommand<? super R,? extends T> value) {
        this.term = term;
        this.value = value;
    }
    
    public Boolean evaluate(R receiver) {
    	return term.setReceiver(receiver).execute();
    }
    
    public T getValue(R receiver) {
		return value.setReceiver(receiver).execute();
	}
}