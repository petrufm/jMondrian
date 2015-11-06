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
import lrg.jMondrian.util.CommandColor;
import lrg.jMondrian.view.ViewRendererInterface;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RectangleNodePainter<T> extends AbstractNodePainter<T> {

    private static class Rectangle {
        int x1,y1,x2,y2;
        Rectangle(int x1, int y1, int x2, int y2) {
            this.x1 = x1; this.y1 = y1;
            this.x2 = x2; this.y2 = y2;
        }
    }

    private boolean withBorders;
    private boolean cumulativeOutline;
    private List<Rectangle> list = new ArrayList<Rectangle>();

    public RectangleNodePainter(boolean withBorders){
        this(0,0,withBorders);
    }

    public RectangleNodePainter(final double width, final double height,boolean withBorders, boolean cumulativeOutline) {
        this(width,height,withBorders);
        this.cumulativeOutline = cumulativeOutline;
    }

    public RectangleNodePainter(final double width, final double height, boolean withBorders) {

        this.withBorders = withBorders;
        this.cumulativeOutline = false;

        nameCommand = new Command.Constant<T, String>("");

        widthCommand = new Command.Constant<T, Double>(width);

        heightCommand = new Command.Constant<T, Double>(height);

        textCommand = new Command.Constant<T, String>("");

        frameColorCommand = CommandColor.BLACK;
        colorCommand = CommandColor.WHITE;
    }

    public void paint(ViewRendererInterface window, T entity, double x1Bias, double y1Bias, boolean last){

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
            if(!invisibleBorder) {
                window.getShapeFactory().addRectangle(entity,this.toString(),(int)x,(int)y,(int)width,(int)height,(int)color, (int)frameColor);
            } else {
                window.getShapeFactory().addRectangle(entity,this.toString(),(int)x,(int)y,(int)width,(int)height,(int)color, false);                
            }
        } else {
            window.getShapeFactory().addRectangle(entity,this.toString(),(int)x,(int)y,(int)width,(int)height,(int)color, false);
        }

        textCommand.setReceiver(entity);
        window.getShapeFactory().addText(entity,this.toString(),textCommand.execute(), (int)x + 5, (int)y - 5, (int)Color.BLACK.getRGB());

        if(cumulativeOutline) {
            list.add(new Rectangle((int)x,(int)y,(int)(x + width - 1),(int)(y + height - 1)));
        }

        if(cumulativeOutline && last) {
            List<Rectangle> tmpOrder = new ArrayList<Rectangle>();
            List<Integer> px = new LinkedList<Integer>();
            List<Integer> py = new LinkedList<Integer>();
            tmpOrder.addAll(list);
            Collections.sort(tmpOrder,new Comparator<Rectangle>() {
                public int compare(Rectangle a, Rectangle b) {
                    if(a.y1 < b.y1) return -1;
                    if(a.y1 > b.y1) return 1;
                    if(a.x1 < b.x1) return -1;
                    if(a.x1 > b.x1) return 1;
                    return 0;
                }
            });
            for(int i = 0; i < tmpOrder.size(); i++) {
                if(i == 0) {
                    px.add(tmpOrder.get(0).x1);
                    py.add(tmpOrder.get(0).y2+1);
                    px.add(tmpOrder.get(0).x1);
                    py.add(tmpOrder.get(0).y1);
                } else if(tmpOrder.get(i-1).y1 != tmpOrder.get(i).y1) {
                    if(tmpOrder.get(i-1).y2 + 1 != tmpOrder.get(i).y1) {
                        px.add(tmpOrder.get(i-1).x2);
                        py.add(py.get(py.size()-1));
                        px.add(tmpOrder.get(i-1).x2);
                        py.add(tmpOrder.get(i-1).y2+1);
                        window.getShapeFactory().addPolyLine(null,null,px,py);
                        px.clear();
                        py.clear();
                        px.add(tmpOrder.get(i).x1);
                        py.add(tmpOrder.get(i).y2+1);
                        px.add(tmpOrder.get(i).x1);
                        py.add(tmpOrder.get(i).y1);                        
                    } else {
                        int j = i + 1;
                        while(j < tmpOrder.size() && tmpOrder.get(j).y1 == tmpOrder.get(i).y1) j++;
                        if(j - 1 < tmpOrder.size() && (tmpOrder.get(j-1).x2 < px.get(0) || tmpOrder.get(i-1).x2 < tmpOrder.get(i).x1)) {
                            px.add(tmpOrder.get(i-1).x2);
                            py.add(py.get(py.size()-1));
                            px.add(tmpOrder.get(i-1).x2);
                            py.add(tmpOrder.get(i-1).y2+1);
                            window.getShapeFactory().addPolyLine(null,null,px,py);
                            px.clear();
                            py.clear();
                            px.add(tmpOrder.get(i).x1);
                            py.add(tmpOrder.get(i).y2+1);
                            px.add(tmpOrder.get(i).x1);
                            py.add(tmpOrder.get(i).y1);
                        } else {
                            px.add(tmpOrder.get(i-1).x2);
                            py.add(py.get(py.size()-1));
                            px.add(tmpOrder.get(i-1).x2);
                            py.add(tmpOrder.get(i-1).y2+1);
                            px.add(0,tmpOrder.get(i).x1);
                            py.add(0,tmpOrder.get(i).y1);
                            px.add(0,tmpOrder.get(i).x1);
                            py.add(0,tmpOrder.get(i).y2+1);
                        }
                    }
                }
                if(i == tmpOrder.size()-1) {
                    px.add(0,tmpOrder.get(i).x2);
                    py.add(0,tmpOrder.get(i).y2+1);
                    px.add(0,tmpOrder.get(i).x2);
                    py.add(0,tmpOrder.get(i).y1);
                }
            }
            window.getShapeFactory().addPolyLine(null,null,px,py);
            list.clear();
        }
    }

}
