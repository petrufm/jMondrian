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
import java.util.Iterator;
import java.util.ArrayList;

public class TreeLayout extends AbstractLayout{

    private double xDist, yDist;
    private int maxX = 0, maxY = 0;

    public TreeLayout(){
        this(5,5);
    }

    public TreeLayout(double xDist, double yDist) {
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
            	edge.setConnectionStyle(EdgeFigure.UP_MIDDLE, EdgeFigure.DOWN_MIDDLE);
                childrenNodes.add(edge.getFrom());
            }
        }

        return childrenNodes;
    }

    private <N,E> double layoutLayer(List<Node<?>> nodeList,List<EdgeFigure<?>> edgeList, double xTo, double yTo, double betweenX, double betweenY, ControlXY xCmd, ControlXY yCmd) {

        Iterator<Node<?>> itNode = nodeList.iterator();
        List<Node<?>> children;
        Node<?> node;
        double treeWidth = 0, childrenY = 0, xPoz, yPoz, childrenMiddle = 0, width = 0;

        xPoz = betweenX;
        yPoz = betweenY;

        while(itNode.hasNext()) {
            node = itNode.next();
            children = getChildren(node, edgeList);
            childrenY = yPoz + node.getHeight() +  yDist;
            if(childrenY >= maxY) maxY = (int) childrenY;
            treeWidth = layoutLayer(children,edgeList,node.getWidth() / 2,yPoz + node.getHeight(),xPoz,childrenY,xCmd,yCmd);
            if(node.getWidth() > treeWidth) treeWidth = node.getWidth();
            childrenMiddle = xPoz + (treeWidth / 2);

            xCmd.link(node,childrenMiddle - node.getWidth() / 2);
            yCmd.link(node,yPoz);
            node.translateTo(xCmd,yCmd);

            xPoz += treeWidth +  xDist;
            if(xPoz >= maxX) maxX = (int)xPoz;
        }


        width =  xPoz - betweenX - xDist;
        if((width / 2) >= xTo) xTo += (width / 2) +  betweenX - xTo;
        else xTo += betweenX;

        return width;
    }

    protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
        List<Node<?>> rootNodes;
        maxX = 0;
        maxY = 0;
        ControlXY xCmd = new ControlXY();
        ControlXY yCmd = new ControlXY();

        rootNodes = getRootNodes(nodeList, edgeList);
        layoutLayer(rootNodes, edgeList, 0, 0, xDist, yDist,xCmd,yCmd);

        for(int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).translateTo(xCmd,yCmd);
        }

        double[] rez = new double[2];
        rez[0] = maxX;
        rez[1] = maxY;

        return rez;
    }

}