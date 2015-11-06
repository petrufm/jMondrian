/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package lrg.jMondrian.layouts;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import lrg.jMondrian.figures.EdgeFigure;
import lrg.jMondrian.figures.Node;

/**
 * Code borrowed from Jung Project (jung-algorithms-2.0.1), BSD License
 * 
 * Implements the Fruchterman-Reingold force-directed algorithm for node layout.
 * 
 * <p>Behavior is determined by the following settable parameters:
 * <ul>
 * <li/>attraction multiplier: how much edges try to keep their vertices together
 * <li/>repulsion multiplier: how much vertices try to push each other apart
 * <li/>maximum iterations: how many iterations this algorithm will use before stopping
 * </ul>
 * Each of the first two defaults to 0.75; the maximum number of iterations defaults to 700.
 *
 * @see "Fruchterman and Reingold, 'Graph Drawing by Force-directed Placement'"
 * @see "http://i11www.ilkd.uni-karlsruhe.de/teaching/SS_04/visualisierung/papers/fruchterman91graph.pdf"
 * @author Scott White, Yan-Biao Boey, Danyel Fisher
 * @author Mihai Balint
 */
public class FRLayout extends AbstractLayout {

    private double forceConstant;

    private double temperature;

    private int currentIteration;
    private int mMaxIterations = 700;

    private FRVertexData[] frVertexData;

    private double attraction_multiplier = 0.75;
    private double attraction_constant;

    private double repulsion_multiplier = 0.75;
    private double repulsion_constant;

    private double max_dimension;
    private Dimension size;

    
    private double[] x,y;	
    private int[][] edges;
    
	@Override
	protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
        initialize(nodeList, edgeList);
        
		while(!done())
			step(x,y,edges);
		
		return saveLayout(nodeList, x, y);
	}
    
	/**
	 * Sets the attraction multiplier.
	 */
	public void setAttractionMultiplier(double attraction) {
        this.attraction_multiplier = attraction;
    }

	/**
	 * Sets the repulsion multiplier.
	 */
    public void setRepulsionMultiplier(double repulsion) {
        this.repulsion_multiplier = repulsion;
    }

    private void initialize(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
    	int nodeCount = nodeList.size();
        int radius = 20, space = 50;
        int sideLen = 1 + (int)Math.floor(Math.sqrt(nodeCount));
        sideLen = sideLen*(radius+space) - space;
        
        size = new Dimension(sideLen, sideLen);
        max_dimension = Math.max(sideLen, sideLen);

		currentIteration = 0;
		temperature = size.getWidth() / 10;

		forceConstant =
			Math.sqrt(size.getHeight() * size.getWidth() / nodeCount);

		attraction_constant = attraction_multiplier * forceConstant;
		repulsion_constant = repulsion_multiplier * forceConstant;
		
		frVertexData = new FRVertexData[nodeCount];
		
		Random random = new Random(new Date().getTime());
		x = new double[nodeCount];
		y = new double[nodeCount];
		edges = new int[edgeList.size()][];
		
		HashMap<Object,Integer> nodeIndex = new HashMap<Object, Integer>();
		int i=0;
		for(Node<?> n : nodeList) {
			nodeIndex.put(n, i);
			x[i] = random.nextDouble() * size.getWidth();
			y[i] = random.nextDouble() * size.getHeight();
			i++;
		}
		
		i=0;
		for(EdgeFigure<?> e : edgeList) {
			edges[i]= new int[]{nodeIndex.get(e.getFrom()), nodeIndex.get(e.getTo())};
			e.setConnectionStyle(EdgeFigure.AUTO);
			i++;
		}
		nodeIndex.clear();
    }

	private static double[] saveLayout(List<Node<?>> nodeList, double[] x, double[] y) {
		ControlXY xCmd = new ControlXY();
        ControlXY yCmd = new ControlXY();
        double minX=x[0], maxX=x[0], minY=y[0], maxY=y[0];
        
		int i=0;
		for(Node<?> n:nodeList) {
			double width = n.getWidth();
			double height = n.getHeight();
			xCmd.link(n, x[i]); yCmd.link(n, y[i]);
			if(i == 0) {
				minX = x[i];
				maxX = x[i]+1.5*width;
				minY = y[i];
				maxY = y[i]+1.5*height;
			}
			if (x[i]<minX) minX = x[i];
			if (x[i]+1.5*width>maxX) maxX = x[i]+1.5*width;
			if (y[i]<minY) minY = y[i];
			if (y[i]+1.5*height>maxY) maxY = y[i]+1.5*height;
			n.translateTo(xCmd, yCmd);
			i++;
		}

		return new double[]{maxX-minX, maxY-minY};
	}	    
    
    private double EPSILON = 0.000001D;

    /**
     * Moves the iteration forward one notch, calculation attraction and
     * repulsion between vertices and edges and cooling the temperature.
     */
    public synchronized void step(double[] x, double[] y, int[][] edges) {
        currentIteration++;
        int nodeCount = x.length;
        /**
         * Calculate repulsion
         */
    	for(int i=0;i<nodeCount;i++)
    		calcRepulsion(x, y, i);

        /**
         * Calculate attraction
         */
        for(int[] e : edges) {
            calcAttraction(x,y,e[0],e[1]);
        }

    	for(int i=0;i<nodeCount;i++)
    		calcPositions(x, y, i);
        cool();
    }

    protected synchronized void calcPositions(double[] x, double y[], int v) {
        FRVertexData fvd = getFRData(v);

        double deltaLength = Math.max(EPSILON, fvd.norm());

        double newXDisp = fvd.getX() / deltaLength
                * Math.min(deltaLength, temperature);

        if (Double.isNaN(newXDisp)) {
        	throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [xdisp]"); }

        double newYDisp = fvd.getY() / deltaLength
                * Math.min(deltaLength, temperature);
        x[v] += newXDisp; y[v] += newYDisp;

        double borderWidth = size.getWidth() / 50.0;
        double newXPos = x[v];
        if (newXPos < borderWidth) {
            newXPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newXPos > (size.getWidth() - borderWidth)) {
            newXPos = size.getWidth() - borderWidth - Math.random()
                    * borderWidth * 2.0;
        }

        double newYPos = y[v];
        if (newYPos < borderWidth) {
            newYPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newYPos > (size.getHeight() - borderWidth)) {
            newYPos = size.getHeight() - borderWidth
                    - Math.random() * borderWidth * 2.0;
        }

        x[v] = newXPos; y[v] = newYPos;
    }

    protected void calcAttraction(double[] x, double y[], int v1, int v2) {
        boolean v1_locked = isLocked(v1);
        boolean v2_locked = isLocked(v2);

        if(v1_locked && v2_locked) {
        	// both locked, do nothing
        	return;
        }
        
        double xDelta = x[v1] - x[v2];
        double yDelta = y[v1] - y[v2];

        double deltaLength = Math.max(EPSILON, Math.sqrt((xDelta * xDelta)
                + (yDelta * yDelta)));

        double force = (deltaLength * deltaLength) / attraction_constant;

        if (Double.isNaN(force)) { throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [force]"); }

        double dx = (xDelta / deltaLength) * force;
        double dy = (yDelta / deltaLength) * force;
        
        if(v1_locked == false) {
        	FRVertexData fvd1 = getFRData(v1);
        	fvd1.offset(-dx, -dy);
        }
        if(v2_locked == false) {
        	FRVertexData fvd2 = getFRData(v2);
        	fvd2.offset(dx, dy);
        }
    }

    protected void calcRepulsion(double[] x, double y[], int v1) {
        FRVertexData fvd1 = getFRData(v1);
        if(fvd1 == null)
            return;
        fvd1.setLocation(0, 0);
        boolean v1_locked = isLocked(v1);

    	for(int v2=0;v2<x.length;v2++) {
            boolean v2_locked = isLocked(v2);
        	if (v1_locked && v2_locked) continue;
        	
            if (v1 != v2) {
                double xDelta = x[v1] - x[v2];
                double yDelta = y[v1] - y[v2];

                double deltaLength = Math.max(EPSILON, Math
                        .sqrt((xDelta * xDelta) + (yDelta * yDelta)));

                double force = (repulsion_constant * repulsion_constant) / deltaLength;

                if (Double.isNaN(force)) { throw new RuntimeException(
                "Unexpected mathematical result in FRLayout:calcPositions [repulsion]"); }

                fvd1.offset((xDelta / deltaLength) * force,
                        (yDelta / deltaLength) * force);
            }
        }
    }

    private void cool() {
        temperature *= (1.0 - currentIteration / (double) mMaxIterations);
    }

    /**
     * Sets the maximum number of iterations.
     */
    public void setMaxIterations(int maxIterations) {
        mMaxIterations = maxIterations;
    }

    protected FRVertexData getFRData(int v) {
    	FRVertexData vd = frVertexData[v];
    	if (null==vd) {
    		vd = new FRVertexData();
    		frVertexData[v] = vd;
    	}
        return vd;
    }
    
    private boolean isLocked(int v) {
    	return false;
    }

    /**
     * This one is an incremental visualization.
     */
    public boolean isIncremental() {
        return true;
    }

    /**
     * Returns true once the current iteration has passed the maximum count,
     * <tt>MAX_ITERATIONS</tt>.
     */
	public boolean done() {
		if (currentIteration > mMaxIterations || temperature < 1.0 / max_dimension) {
			return true;
		}
		return false;
	}

	protected static class FRVertexData extends Point2D.Double {
		private static final long serialVersionUID = 1L;

		protected void offset(double x, double y) {
			this.x += x;
			this.y += y;
		}

		protected double norm() {
			return Math.sqrt(x * x + y * y);
		}
	}
}