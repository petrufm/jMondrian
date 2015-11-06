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

import java.util.ArrayList;
import java.util.List;

import lrg.jMondrian.access.Command;
import lrg.jMondrian.access.IObjectCommand;
import lrg.jMondrian.figures.EdgeFigure;
import lrg.jMondrian.figures.Node;

public class BalloonTreeLayout extends AbstractLayout {
	private IObjectCommand<Object, Boolean> edgeFilter;
	
	public BalloonTreeLayout(IObjectCommand<?, Boolean> edgeFilter) {
		// TODO this is a hack, the layout needs to be parametrized.
		this.edgeFilter = (IObjectCommand)edgeFilter;
	}
	
	public BalloonTreeLayout() {
		this(new Command.Constant<Object, Boolean>(true));
	}
	
	private <N,E> List<Node<?>> getRootNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList){		   
		List<Node<?>> rootNodes = new ArrayList<Node<?>>();
		boolean isRoot;
	    for(Node<?> aNode: nodeList) {
	    	isRoot = true;
	        for(EdgeFigure<?> anEdge : edgeList) {
	        	if(anEdge.getFrom().equals(aNode)) {
	        		isRoot = false;
	        	}
	        }
	        if(isRoot) {
	        	rootNodes.add(aNode);
	        }
	    }    
	    return rootNodes;
	}

	private <N,E> List<Node<?>> getChildren(Node<?> node, List<EdgeFigure<?>> edgeList){
		List<Node<?>> childrenNodes = new ArrayList<Node<?>>();
	    if(node == fakeGlobalRoot) {
	    	childrenNodes.addAll(rootNodes);
	    } else {
	    	for(EdgeFigure<?> anEdge : edgeList){
	    		if(anEdge.getTo().equals(node)){
	    			childrenNodes.add(anEdge.getFrom());
	        	}
	        }
	    }
	    return childrenNodes;
	}
	
	private static final double M_RAD = 10;
	
	private Object[] distribute(Node<?> aRoot, List<EdgeFigure<?>> edgeList) {

		List<Node<?>> allChildrens = getChildren(aRoot, edgeList);

		if(allChildrens.size() == 0) {
			ArrayList<Node<?>> nodes = new ArrayList<Node<?>>();
			ArrayList<Double> x = new ArrayList<Double>();
			ArrayList<Double> y = new ArrayList<Double>();
			nodes.add(aRoot);
			x.add(0d);
			y.add(0d);
			double h = aRoot.getWidth();
			double w = aRoot.getHeight();
			return new Object[]{h > w ? h : w, nodes, x, y};
		}

		Object[][] childrenRes = new Object[allChildrens.size()][];
		double max_child_radius = 0;
		double inner_sum = 0;
		int i = 0;
		for(Node<?> aChild : allChildrens) {
			childrenRes[i++] = distribute(aChild, edgeList);
			if(((Double)childrenRes[i-1][0]) > max_child_radius) max_child_radius = ((Double)childrenRes[i-1][0]);
			inner_sum += ((Double)childrenRes[i-1][0]);
		}
		inner_sum = 2 * inner_sum;
		double current_rad;
		if(inner_sum < 2 * Math.PI * max_child_radius) {
			current_rad = max_child_radius;
		} else {
			current_rad = inner_sum / (2 * Math.PI);
		}
		double out_rad = current_rad + max_child_radius;		
		double currentAngle = 0;
		ArrayList<Node<?>> nodes = new ArrayList<Node<?>>();
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
		for(i = 0; i < allChildrens.size(); i++) {
			if(i == 0) {
				currentAngle += ((Double)childrenRes[allChildrens.size() - 1][0] + (Double)childrenRes[0][0]) / (current_rad);
			} else {
				currentAngle += ((Double)childrenRes[i - 1][0] + (Double)childrenRes[i][0]) / (current_rad);				
			}
			//Compute position of the child
			double newRootX = (current_rad) * Math.cos(currentAngle);
			double newRootY = (current_rad) * Math.sin(currentAngle);
			//Translate the balloon of the child
			for(int k = 0; k < ((ArrayList<Node<?>>)childrenRes[i][1]).size(); k++) {
				nodes.add(((ArrayList<Node<?>>)childrenRes[i][1]).get(k));
				x.add(newRootX + ((ArrayList<Double>)childrenRes[i][2]).get(k));
				y.add(newRootY + ((ArrayList<Double>)childrenRes[i][3]).get(k));
			}
		}
		nodes.add(aRoot);
		x.add(0d);
		y.add(0d);
		return new Object[]{out_rad, nodes, x, y};	
	}
	 	
	private Node<?> fakeGlobalRoot = new Node(null,null);
	private List<Node<?>> rootNodes; 
	
	@Override
	protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> actualEdgeList) {
		
        ControlXY xCmd = new ControlXY();
        ControlXY yCmd = new ControlXY();
        
        List<EdgeFigure<?>> edgeList = new ArrayList<EdgeFigure<?>>();
        for(EdgeFigure<?> e : actualEdgeList) 
        	if (edgeFilter.setReceiver(e.getEntity()).execute())
        		edgeList.add(e);
        
        rootNodes = getRootNodes(nodeList, edgeList);
        
        Object[] tmp = distribute(fakeGlobalRoot, edgeList);
        
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for(int i = 0; i < ((ArrayList<Double>)tmp[2]).size(); i++) {
        	double x = ((ArrayList<Double>)tmp[2]).get(i);
        	double y = ((ArrayList<Double>)tmp[3]).get(i);
        	if(minX > x) minX = x;
        	if(minY > y) minY = y;
        	if(maxX < x) maxX = x;
        	if(maxY < y) maxY = y;
        }
        
        maxX = maxX - minX;
        maxY = maxY - minY;
        
        int i = 0;
        for(Node<?> aNode : ((ArrayList<Node<?>>)tmp[1])) {
        	if(aNode == fakeGlobalRoot) {
        		i++;
        		continue;
        	}
        	xCmd.link(aNode, ((ArrayList<Double>)tmp[2]).get(i) - minX);
        	yCmd.link(aNode, ((ArrayList<Double>)tmp[3]).get(i) - minY);
        	i++;
        }
        
        for(i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).translateTo(xCmd,yCmd);
        }
        
        return new double[] {maxX + M_RAD , maxY + M_RAD};
 	}

}
