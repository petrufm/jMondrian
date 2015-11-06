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

import lrg.jMondrian.access.Command;
import lrg.jMondrian.access.IObjectCommand;
import lrg.jMondrian.util.CommandColor;
import lrg.jMondrian.view.ViewRendererInterface;

public class CubicCurveEdgePainter<E> extends LineEdgePainter<E> {
    private IObjectCommand<? super E,? extends Number> duplicationIndexCommand;

	public CubicCurveEdgePainter(IObjectCommand<? super E,?> fromCommand, IObjectCommand<? super E,?> toCommand){
		super(fromCommand,toCommand,false);
		duplicationIndexCommand = new Command.Constant<E, Integer>(0);
    }

    public CubicCurveEdgePainter(IObjectCommand<? super E,?> fromCommand, IObjectCommand<? super E,?> toCommand, boolean oriented){
        super(fromCommand,toCommand,oriented);
		duplicationIndexCommand = new Command.Constant<E, Integer>(0);
    }

    public AbstractEdgePainter<E> duplicationIndex(IObjectCommand<? super E, ? extends Number> duplicationIndexCommand) {
        this.duplicationIndexCommand = duplicationIndexCommand;
        return this;
    }

    public void paint(ViewRendererInterface window, E entity, double x1Bias, double y1Bias, double x2Bias, double y2Bias) {
        try {
            colorCommand.setReceiver(entity);
            fromCommand.setReceiver(entity);
            toCommand.setReceiver(entity);
            nameCommand.setReceiver(entity);
            duplicationIndexCommand.setReceiver(entity);
            
            int color = colorCommand.execute().intValue();
            int dupIndex = duplicationIndexCommand.execute().intValue();
            
            if(fromCommand.execute()==toCommand.execute()) {
            	int phase = dupIndex % 4;
            	int ampl = 1 + (dupIndex / 4);
            	double loop = Math.sqrt(ampl)*28, spread = ampl*14;
    			double cx1,cy1,cx2,cy2;
    			double x1,y1,x2,y2;
            	double w=20, h=20;
    			
    			//		
    			// 		 +y
    			//      x,y  +x
    			//
            	switch(phase) {
            	case 0:
            		x1 =x1Bias;			y1 =y1Bias-1-h/2;
            		x2 =x2Bias+w/2;		y2 =y2Bias;
        			cx1=x1+loop-spread; 	cy1=y1-loop-spread;
        			cx2=x2+loop+spread; 	cy2=y2-loop+spread;
        			break;
            	case 1:
            		x1 =x1Bias+1+w/2;	y1 =y1Bias;
            		x2 =x2Bias;			y2 =y2Bias+h/2;
        			cx1=x1+loop+spread; 	cy1=y1+loop-spread;
        			cx2=x2+loop-spread; 	cy2=y2+loop+spread;
        			break;
            	case 2:
            		x1 =x1Bias;			y1 =y1Bias+1+h/2;
            		x2 =x2Bias-w/2-1;	y2 =y2Bias;
        			cx1=x1-loop+spread; 	cy1=y1+loop+spread;
        			cx2=x2-loop-spread; 	cy2=y2+loop-spread;
        			break;
            	default:
            		x1 =x1Bias-1-w/2;	y1 =y1Bias;
            		x2 =x2Bias;			y2 =y2Bias-1-h/2;
        			cx1=x1-loop-spread; 	cy1=y1-loop+spread;
        			cx2=x2-loop+spread; 	cy2=y2-loop-spread;
        			break;
            	}
    			
            	window.getShapeFactory().addCubicCurve(entity, this.toString(), x1, y1, cx1, cy1, cx2,cy2, x2, y2, color, isOriented());
            } else {
            	double dx = x1Bias - x2Bias, dy = y1Bias-y2Bias;
            	double belly = (dupIndex+1)*0.2*Math.sqrt(dx*dx + dy*dy);
    			double 
    				cx1=x2Bias + 0.5*dx,
    				cy1=y2Bias + 0.5*dy;
    			if (x1Bias<=x2Bias && y1Bias<=y2Bias) { cx1 -= belly; cy1 += belly; } else
    			if (x1Bias>=x2Bias && y1Bias>=y2Bias) { cx1 += belly; cy1 -= belly; } else
    			if (x1Bias>=x2Bias && y1Bias<=y2Bias) { cx1 += belly; cy1 += belly; } else 
    			if (x1Bias<=x2Bias && y1Bias>=y2Bias) { cx1 -= belly; cy1 -= belly; }
    			
            	window.getShapeFactory().addQuadCurve(entity, this.toString(), x1Bias, y1Bias, cx1, cy1, x2Bias, y2Bias, color, isOriented());
            }
        } catch(CommandColor.InvisibleException e) {
            return;
        }
    }
}
