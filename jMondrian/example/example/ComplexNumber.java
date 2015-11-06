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
package example;

import java.util.ArrayList;
import java.util.List;

//We are going to represent ComplexNumbers

public class ComplexNumber {
    public int x,y;
    public ComplexNumber(int x, int y) {
        this.x = x;
        this.y = y;
    }
	public String toString() {
		return x + "i*"+y;
	}

	public double modulo1() {
		return x*x + y*y;
	}
	
	public double sum() {
		return x+y;
	}
	

	public List<Integer> getComponents() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		//If a.equels(b) == true then a and b cannot have two distinct visual representations!
		list.add(new Integer(x));
		list.add(new Integer(y*2));
		return list;
	}
}
