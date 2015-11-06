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

import java.awt.Color;

import lrg.jMondrian.access.Command;

public class CommandColor {
	private static class MondrianColor extends Command.Constant<Object, Double> {
		public MondrianColor(Color c) {
			super((double)c.getRGB());
		}
	}
	
	private static class Invisible extends Command<Object,Double> {
        public Double execute() {
            throw new InvisibleException();
        }
    }

    public static class InvisibleException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvisibleException() {
            super("Invisible color!");
        }
    }	

	public final static Command<Object, Double> BLACK = new MondrianColor(java.awt.Color.BLACK);
	public final static Command<Object, Double> BLUE = new MondrianColor(java.awt.Color.BLUE);
	public final static Command<Object, Double> CYAN = new MondrianColor(java.awt.Color.CYAN);
	public final static Command<Object, Double> DARK_GRAY = new MondrianColor(java.awt.Color.DARK_GRAY);
	public final static Command<Object, Double> GRAY = new MondrianColor(java.awt.Color.GRAY);
	public final static Command<Object, Double> GREEN = new MondrianColor(java.awt.Color.GREEN);
	public final static Command<Object, Double> LIGHT_GRAY = new MondrianColor(java.awt.Color.LIGHT_GRAY);
	public final static Command<Object, Double> MAGENTA = new MondrianColor(java.awt.Color.MAGENTA);
	public final static Command<Object, Double> ORANGE = new MondrianColor(java.awt.Color.ORANGE);
	public final static Command<Object, Double> PINK = new MondrianColor(java.awt.Color.PINK);
	public final static Command<Object, Double> RED = new MondrianColor(java.awt.Color.RED);
	public final static Command<Object, Double> WHITE = new MondrianColor(java.awt.Color.WHITE);
	public final static Command<Object, Double> YELLOW = new MondrianColor(java.awt.Color.YELLOW);
	public final static Command<Object, Double> INVISIBLE = new Invisible();
	public final static Command<Object, Double> LIGHT_RED = new MondrianColor(new Color(255,225,225));
	public final static Command<Object, Double> LIGHT_BLUE = new MondrianColor(new Color(225,225,255));
	public final static Command<Object, Double> LIGHT_LILA = new MondrianColor(new Color(240,220,255));
	public final static Command<Object, Double> VERY_LIGHT_GRAY = new MondrianColor(new Color(240,240,240));
	
    public static boolean isColorTransparent(int color) {
    	return (color & 0xFF000000) == 0;
    }
    public static int transparentColor() {
    	return 0x00FFFFFF; // white with alpha set to completely transparent
    }
}
