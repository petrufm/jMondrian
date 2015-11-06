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
package lrg.jMondrian.layouts;

import lrg.jMondrian.figures.Node;
import lrg.jMondrian.figures.EdgeFigure;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


public class HTreeLayout extends AbstractLayout{

    private double xDist, yDist;
       private int maxX = 0, maxY = 0;

       public HTreeLayout(){
           this(5,5);
       }

       public HTreeLayout(double xDist, double yDist) {
           this.xDist = xDist;
           this.yDist = yDist;
       }

       private <N,E> List<Node<?>> getRootNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList){
           List<Node<?>> rootNodes = new ArrayList<Node<?>>();
           Iterator<Node<?>> itNode = nodeList.iterator();
           Iterator<EdgeFigure<?>> itEdge;
           Node<?> node;
           EdgeFigure<?> edge;
           boolean isRoot;

           while(itNode.hasNext()){
               node = itNode.next();
               isRoot = true;
               itEdge = edgeList.iterator();
               while(itEdge.hasNext()){
                   edge = itEdge.next();
                   if(edge.getFrom().equals(node)){
                       isRoot = false;
                   }
               }
               if(isRoot){
                   rootNodes.add(node);
               }
           }

           return rootNodes;
       }

       private <N,E> List<Node<?>> getChildren(Node<?> node, List<EdgeFigure<?>> edgeList){
           List<Node<?>> childrenNodes = new ArrayList<Node<?>>();
           Iterator<EdgeFigure<?>> itEdge= edgeList.iterator();
           EdgeFigure<?> edge;

           while(itEdge.hasNext()){
               edge = itEdge.next();
               if(edge.getTo().equals(node)){
                   edge.setConnectionStyle(EdgeFigure.CENTER_LEFT,EdgeFigure.CENTER_RIGHT);
                   childrenNodes.add(edge.getFrom());
               }
           }

           return childrenNodes;
       }

       private <N,E> double layoutLayer(List<Node<?>> nodeList,List<EdgeFigure<?>> edgeList, double xTo, double yTo, double betweenX, double betweenY, ControlXY xCmd, ControlXY yCmd) {

           Iterator<Node<?>> itNode = nodeList.iterator();
           List<Node<?>> children;
           Node<?> node;
           double treeHeight = 0, childrenX = 0, xPoz, yPoz, childrenMiddle = 0, height = 0;

           xPoz = betweenX;
           yPoz = betweenY;

           while(itNode.hasNext()) {
               node = itNode.next();
               children = getChildren(node, edgeList);
               childrenX = xPoz + node.getWidth() +  xDist;
               if(childrenX >= maxX) maxX = (int) childrenX;
               treeHeight = layoutLayer(children,edgeList,xPoz + node.getWidth(),yPoz/2,childrenX,yPoz,xCmd,yCmd);
               if(node.getHeight() > treeHeight) treeHeight = node.getHeight();
               childrenMiddle = yPoz + (treeHeight / 2);

               xCmd.link(node,xPoz);
               yCmd.link(node,childrenMiddle - node.getHeight() /2);
               node.translateTo(xCmd,yCmd);

               yPoz += treeHeight +  yDist;
               if(yPoz >= maxY) maxY = (int)yPoz;
           }


           height =  yPoz - betweenY - yDist;
           if((height / 2) >= yTo) yTo += (height / 2) +  betweenY - yTo;
           else yTo += betweenY;

           return height;
       }

       protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {

           List<Node<?>> rootNodes;
           maxX = 0;
           maxY = 0;
           ControlXY xCmd = new ControlXY();
           ControlXY yCmd = new ControlXY();

           rootNodes = getRootNodes(nodeList, edgeList);
           layoutLayer(rootNodes, edgeList, 0, 0, xDist, yDist,xCmd,yCmd);

           if(nodeList.size() > 0) {
               nodeList.get(0).translateTo(xCmd,yCmd);
           }

           double[] rez = new double[2];
           rez[0] = maxX;
           rez[1] = maxY;

           return rez;
       }

} 