/*
 * Copyright (c) 2006-2015 Mihai Tarce, Petru-Florin Mihancea
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

import java.util.*;

public class CrossReductionTreeLayout extends AbstractLayout {
    public enum CrossReduction { ADJACENT_EXCHANGE, MEDIAN_METHOD, BARYCENTER_METHOD, MEDIAN_ADJACENT, MEDIAN_ADJECT_REPEATABLE };
    public enum LayerStrategy { CLASSIC, HEURISTIC };

    private int xDist = 20, yDist = 20;
    private int maxX = 0, maxY = 0;
    private final int dummyWidth = 20;
    private CrossReduction cr = CrossReduction.MEDIAN_ADJECT_REPEATABLE;
    private LayerStrategy ls = LayerStrategy.HEURISTIC;
    private boolean DEBUG = false;
    private boolean reverse = false;    

    private class MNode {
        private Node<?> n;
        private boolean dummy;

        public MNode() {
            this.n = null;
            this.dummy = false;
        }

        public MNode(boolean dummy) {
            this();
            this.dummy = dummy;
        }

        public MNode(Node<?> n) {
            this();
            this.n = n;
        }

        public boolean isDummy() {
            return this.dummy;
        }

        public Node<?> getNode() {
            return this.n;
        }
    }

    private class MEdge {
        private MNode from, to;
        private EdgeFigure<?> edge;
        private boolean reverse = false;

        public MEdge(MNode from, MNode to) {
            this.from = from;
            this.to = to;
            this.edge = null;
        }

        public MEdge(MNode from, MNode to, EdgeFigure<?> e) {
            this(from, to);
            this.edge = e;
        }

        public void setReverse(boolean value) {
            reverse = value;
        }

        public void reverse() {
            reverse = !reverse;
        }

        public MNode getFrom() {
            return !reverse ? from : to;
        }

        public MNode getTo() {
            return !reverse ? to : from;
        }

        public EdgeFigure<?> getEdge() {
            return edge;
        }

        public boolean isDummy() {
            return (edge == null);
        }
    }

    public CrossReductionTreeLayout() {}

    public CrossReductionTreeLayout(boolean reverse) {
        this(20,20);
        this.reverse = reverse;
    }    

    public CrossReductionTreeLayout(double xDist, double yDist) {
        this.xDist = (int)xDist;
        this.yDist = (int)yDist;
    }

    public CrossReductionTreeLayout(CrossReduction cr) {
        this.cr = cr;
    }

    public CrossReductionTreeLayout(boolean DEBUG, boolean reverse) {
        this.DEBUG = DEBUG;
        this.reverse = reverse;
    }

    public CrossReductionTreeLayout(double xDist, double yDist, boolean reverse) {
        this.xDist = (int)xDist;
        this.yDist = (int)yDist;
        this.reverse = reverse;
    }

    public CrossReductionTreeLayout(double xDist, double yDist, CrossReduction cr, boolean DEBUG) {
        this(xDist, yDist);
        this.cr = cr;
        this.DEBUG = DEBUG;
    }

    private List<MNode> getSinkNodes(List<MNode> mNodes, List<MEdge> mEdges) {
        List<MNode> sinkNodes = new ArrayList<MNode>();
        Iterator<MNode> itNode = mNodes.iterator();
        Iterator<MEdge> itEdge;
        MNode node;
        MEdge edge;
        boolean isSink;

        while(itNode.hasNext()){
            node = itNode.next();
            isSink = true;
            itEdge = mEdges.iterator();
            while(itEdge.hasNext()){
                edge = itEdge.next();
                if(edge.getTo().equals(node)) {
                    isSink = false;
                }
            }
            if(isSink){
                sinkNodes.add(node);
            }
        }

        return sinkNodes;
    }

    private List<MNode> getParents(MNode node, List<MEdge> edgeList){
        List<MNode> parentNodes = new ArrayList<MNode>();
        Iterator<MEdge> itEdge = edgeList.iterator();
        MEdge edge;

        while(itEdge.hasNext()){
            edge = itEdge.next();
            if(edge.getFrom().equals(node)) {
                if (!edge.isDummy()) {
                	edge.getEdge().setConnectionStyle(
            			!reverse ? EdgeFigure.UP_MIDDLE : EdgeFigure.DOWN_MIDDLE, 
            			!reverse ? EdgeFigure.DOWN_MIDDLE : EdgeFigure.UP_MIDDLE
            		);
                }
                parentNodes.add(edge.getTo());
            }
        }

        return parentNodes;
    }

    private int findNodeInLayers(MNode node, List<List<MNode>> layers) {
        int i;

        for (i = layers.size() - 1; i >= 0; i--) {
            List<MNode> l = layers.get(i);

            if (l.contains(node))
                break;
        }

        return i;
    }

    private void printTreeInfo(List<List<MNode>> layers, List<MEdge> mEdges) {
        int i = 0;
        for (List<MNode> layer : layers) {
            System.out.println("ii layer " + i++ + ":");
            for (MNode n : layer) {
                System.out.print("ii  [parents:");
                for (MEdge e : mEdges) {
                    if (e.getFrom().equals(n)) {
                        printNodeInfo(e.getTo());
                    }
                }
                System.out.print("]");

                printNodeInfo(n);

                System.out.print(" [children:");
                for (MEdge e : mEdges) {
                    if (e.getTo().equals(n)) {
                        printNodeInfo(e.getFrom());
                    }
                }
                System.out.println("]");
            }
        }
    }

    public void printNodeInfo(MNode n) {
        if (n.isDummy()) {
            System.out.print(" (dummy)");
        } else {
            System.out.print(" (" + n.getNode().getWidth() + ", " + n.getNode().getHeight() + ")");
        }
    }

    public <N,E> void generateTree(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList, ControlXY xCmd, ControlXY yCmd) {
        List<MNode> mNodes = new ArrayList<MNode>();
        List<MEdge> mEdges = new ArrayList<MEdge>();

        Map<Node<?>, MNode> hash = new HashMap<Node<?>, MNode>();

        List<MNode> mSinkNodes;
        List<List<MNode>> layers = new ArrayList<List<MNode>>();

        MNode m;

        /* Generate internal list (using MNode and MEdge) */
        for (Node<?> n : nodeList) {
            m = new MNode(n);
            mNodes.add(m);
            hash.put(n, m);
        }

        for (EdgeFigure<?> e : edgeList) {
            MEdge tmpEdge = new MEdge(hash.get(e.getFrom()), hash.get(e.getTo()), e);
            tmpEdge.setReverse(reverse);
            mEdges.add(tmpEdge);            
        }

        /* use only internal representation */

        //Step 1: Layer assignment (and dummy vertices)
        mSinkNodes = getSinkNodes(mNodes, mEdges); // Start from the sinks
        switch(ls) {
            case CLASSIC:
                assignLayers(layers, mSinkNodes, mEdges, 0);
                break;
            case HEURISTIC:
                layers = assignLayers(mNodes,mEdges,mSinkNodes);                
                break;
        }

        if (DEBUG)
            printTreeInfo(layers, mEdges);

        addDummyVertices(layers, mNodes, mEdges);
        if (DEBUG)
            printTreeInfo(layers, mEdges);

        // Step 2: Crossing reduction
        switch (cr) {
            case ADJACENT_EXCHANGE:
               adjacentExchange(layers, mEdges);
                break;
            case BARYCENTER_METHOD:
                barycenterMethod(layers, mEdges);
                break;
            case MEDIAN_METHOD:
                medianMethod(layers, mEdges);
                break;
            case MEDIAN_ADJACENT:
                medianMethod(layers, mEdges);
                adjacentExchange(layers, mEdges);
                break;
            case MEDIAN_ADJECT_REPEATABLE:
                boolean stop;
                int count1 = globalCrossing(layers, mEdges), count2;
                do {

                    medianMethod(layers, mEdges);
                    adjacentExchange(layers, mEdges);

                    Collections.reverse(layers);
                    for(MEdge e : mEdges) {
                        e.reverse();
                    }
                    medianMethod(layers, mEdges);
                    adjacentExchange(layers, mEdges);
                    Collections.reverse(layers);
                    for(MEdge e : mEdges) {
                        e.reverse();
                    }

                    count2 = globalCrossing(layers, mEdges);
                    //System.out.println(count1 + " " + count2);
                    stop = count2 == count1;
                    count1 = count2;

                } while (!stop);
                //System.out.println(globalCrossing(layers, mEdges));
        }

        if (DEBUG)                               
            printTreeInfo(layers, mEdges);

        // Reverse layer order (parents at the top)
        Collections.reverse(layers);

        // Step 3: Horizontal coordinate assignment
        plotTree(layers, mNodes, mEdges, xCmd, yCmd);
    }

    private int globalCrossing(List<List<MNode>> layers, List<MEdge> edges) {
        int total = 0;
        for (int i = layers.size() - 2; i >= 0; i--) {
            for(int j = 0; j < layers.get(i).size(); j++) {
                for(int k = j + 1; k < layers.get(i).size(); k++)
                    total += countCrossings(layers.get(i+1),layers.get(i),edges, j, k);
            }
        }
        return total;
    }
    
    private MNode findRoot(List<MNode> mNodes, List<MEdge> mEdges) {
    	ArrayList<MNode> roots = new ArrayList<MNode>();
        for(MNode itNode : mNodes) {
            boolean isRoot = true;
            for(MEdge itEdge : mEdges) {
                if(itEdge.getFrom().equals(itNode)) {
                    isRoot = false;
                    break;
                }
            }
            if(isRoot) {
                roots.add(itNode);
            }
        }
        if(roots.size() == 1) {	
        	return roots.get(0);
        } else if(roots.size() > 1) {
            MNode dummyNode =  new MNode(true);
            mNodes.add(dummyNode);
            for(MNode itNode : roots) {
                MEdge dummyEdge = new MEdge(itNode,dummyNode);
                mEdges.add(dummyEdge);
            }
            return dummyNode;
        }
        throw new RuntimeException("Cannot find a root node!");
    }

    private List<MNode> findDirectChildren(MNode base, List<MEdge> mEdges) {
        List<MNode> res = new ArrayList<MNode>();
        for(MEdge itEdge : mEdges) {
            if(itEdge.getTo().equals(base)) {
                res.add(itEdge.getFrom());
            }
        }
        return res;
    }

    private List<List<MNode>> assignLayers(List<MNode> mNodes, List<MEdge> mEdges, List<MNode> mSinks) {
        
    	HashMap<MNode,Integer> map2level = new HashMap<MNode,Integer>();
        LinkedList<MNode> workingList = new LinkedList<MNode>();
        
        MNode root = findRoot(mNodes, mEdges);
        for(MNode itNode : mNodes) {
            map2level.put(itNode,0);
        }
        
        workingList.add(root);
        workingList.add(null);
        int level = 0;
        while(workingList.size() != 0) {
            MNode current = workingList.removeFirst();
            if(current == null) {
                level++;
                if(workingList.size() != 0) {
                    workingList.add(null);
                }
                continue;
            }
            if(map2level.get(current) < level) {
               map2level.put(current, level);
            }
            workingList.addAll(findDirectChildren(current,mEdges));
        }

        List<List<MNode>> res = new ArrayList<List<MNode>>();
        for(MNode itNode : map2level.keySet()) {
            int l = map2level.get(itNode);
            while(res.size() <= l) {
                res.add(new ArrayList<MNode>());
            }
            res.get(l).add(itNode);
        }

        // Euristic - add a dummy onto the n + 1 layer
        MNode dummyNode =  new MNode(true);
        res.add(new ArrayList<MNode>());
        res.get(res.size() - 1).add(dummyNode);
        mNodes.add(dummyNode);
        for(MNode itNode : mSinks) {
            MEdge dummyEdge = new MEdge(dummyNode,itNode);
            mEdges.add(dummyEdge);
        }
        
        Collections.reverse(res);
        
        //Euristic - add a dummy node (into the last layer) for each sink node that is not placed into the last layer
        //for(MNode itNode : mSinks) {
        //    if(!res.get(0).contains(itNode)) {
        //        MNode dummyNode = new MNode(true);
        //        res.get(0).add(dummyNode);
        //        mNodes.add(dummyNode);
        //        MEdge dummyEdge = new MEdge(dummyNode,itNode);
        //        mEdges.add(dummyEdge);
        //    }
        //}

        for(MEdge itEdge: mEdges) {
            if(! itEdge.isDummy()) {
                itEdge.getEdge().setConnectionStyle(
                	!reverse ? EdgeFigure.UP_MIDDLE : EdgeFigure.DOWN_MIDDLE,
                	!reverse ? EdgeFigure.DOWN_MIDDLE : EdgeFigure.UP_MIDDLE
                );
            }
        }
        return res;
    }

    

    public void assignLayers(List<List<MNode>> layers, List<MNode> mNodes, List<MEdge> mEdges, int level) {
        boolean exists;
        List<MNode> parents;
        int i;

        if (mNodes.size() > 0 && layers.size() < level + 1) {
            layers.add(level, new ArrayList<MNode>());
        }

        for (MNode n : mNodes) {
            exists = false;

            for (i = 0; i < layers.size(); i++) {
                List<MNode> l = layers.get(i);
                if (l.contains(n)) {
                    if (DEBUG)
                        System.out.print("!! found (" + n.getNode().getWidth()
                                + ", " + n.getNode().getHeight() + ") on level " + i + "...");

                    if (i < level) {
                        if (DEBUG)
                            System.out.println("removing");
                        l.remove(n);
                    } else {
                        if (DEBUG)
                            System.out.println("keeping");
                        exists = true;
                    }
                    break;
                }
            }

            if (i == layers.size()) {
                if (DEBUG)
                    System.out.println("!! not found ("
                        + n.getNode().getWidth() + ", "
                        + n.getNode().getHeight() + ")...");
            }

            if (!exists) {
                if (DEBUG)
                    System.out.println("!! adding "
                        + n.getNode().getWidth() + ", "
                        + n.getNode().getHeight() + " to level " + level);
                layers.get(level).add(n);
            }

            parents = getParents(n, mEdges);
            assignLayers(layers, parents, mEdges, level + 1);
        }
    }

    public void addDummyVertices(List<List<MNode>> layers, List<MNode> mNodes, List<MEdge> mEdges) {
        int parentLevel, childLevel;
        
        List<MEdge> tempAddEdges = new ArrayList<MEdge>(), tempDelEdges = new ArrayList<MEdge>();
        List<MNode> tempAddNodes = new ArrayList<MNode>();
        List<List<MNode>> tempAddLayers = new ArrayList<List<MNode>>();

        for (List<MNode> layer : layers) {
            tempAddLayers.add(new ArrayList<MNode>());
        }

        for (int i = 0; i < layers.size(); i++) {
            List<MNode> list = layers.get(i);

            for (MNode node : list) {
                for (MEdge edge : mEdges) {
                    if (edge.getFrom().equals(node)) {
                        if (DEBUG)
                            System.out.println("## found edge (" + node.getNode().getWidth()
                                    + ", "
                                    + node.getNode().getHeight()
                                    + ") <- ("
                                    + edge.getTo().getNode().getWidth()
                                    + ", "
                                    + edge.getTo().getNode().getHeight()
                                    + ")");

                        parentLevel = findNodeInLayers(edge.getTo(), layers);
                        childLevel = i;

                        /* Edge length is > 1, need to add at least 1 dummy node */
                        if (parentLevel - childLevel > 1) {
                            if (DEBUG)
                                System.out.println("## insert dummy vertices from (" +
                                    node.getNode().getWidth() + ", "
                                    + node.getNode().getHeight() + ", level "
                                    + childLevel + ") to (" +
                                    edge.getTo().getNode().getWidth()
                                    + ", "
                                    + edge.getTo().getNode().getHeight()
                                    + ", level " + parentLevel
                                    + ")");

                            MNode dummyNode, prevDummy = node;
                            MEdge dummyEdge;

                            for (int j = childLevel; j < parentLevel - 1; j++) {
                                if (DEBUG)
                                    System.out.println("  ## dummyNode (" + (j+1) + ")");

                                dummyNode = new MNode(true);
                                dummyEdge = new MEdge(prevDummy, dummyNode);

                                tempAddNodes.add(dummyNode);
                                tempAddLayers.get(j+1).add(dummyNode);

                                tempAddEdges.add(dummyEdge);

                                prevDummy = dummyNode;
                            }

                            tempAddEdges.add(new MEdge(prevDummy, edge.getTo()));
                            tempDelEdges.add(edge);
                        }
                    }
                }
            }
        }

        mNodes.addAll(tempAddNodes); // add all dummy nodes
        mEdges.addAll(tempAddEdges); // add all dummy node related edges

        mEdges.removeAll(tempDelEdges);

        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).addAll( tempAddLayers.get(i) ); // add dummy nodes into layers array
        }
    }

    private void medianMethod(List<List<MNode>> layers, List<MEdge> mEdges) {
        for (int i = 0; i < layers.size() - 1; i++) {
            medianMethodInternal(layers.get(i+1), layers.get(i), mEdges);
        }
    }

    private void medianMethodInternal(List<MNode> parentLayer, List<MNode> childLayer, List<MEdge> mEdges) {
        List<MEdge> edges = new ArrayList<MEdge>();

        for (MEdge e : mEdges) {
            if (parentLayer.contains(e.getTo()) && childLayer.contains(e.getFrom())) {
                edges.add(e);
            }
        }

        List<Integer> med = new ArrayList<Integer>();
        List<Double> bar = new ArrayList<Double>();
        for (int i = 0; i < parentLayer.size(); i++) {
            MNode p = parentLayer.get(i);
            double sum = 0;
            List<Integer> xpos = new ArrayList<Integer>();

            for (int j = 0; j < childLayer.size(); j++) {
                for (MEdge e : edges) {
                    if (e.getFrom().equals(childLayer.get(j)) && e.getTo().equals(p)) {
                        xpos.add(j);
                        sum += j;
                    }
                }
            }
            bar.add(sum / xpos.size());

            if (DEBUG) {
                System.out.print("(node " + i);
                printNodeInfo(p);
                System.out.print(", " + childLayer.size() + " children) xpos[ ");
                for (Integer x : xpos) {
                    System.out.print(x + " ");
                }
            }

            int medval = xpos.get(xpos.size() / 2);

            if (DEBUG)
                System.out.println("] (m " + medval + ") ");

            med.add(medval);
        }

        if (DEBUG) {
            System.out.print("2) layer = " + parentLayer.size() + " med[]: ");
            for (Integer m : med) {
                System.out.print(m + " ");
            }
            System.out.println();
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (int i = 0; i < parentLayer.size() - 1; i++) {
                if (med.get(i) > med.get(i+1)) {
                    Collections.swap(parentLayer, i, i+1);
                    Collections.swap(med, i, i+1);
                    Collections.swap(bar, i, i+1);
                    changed = true;
                } else if (med.get(i).equals(med.get(i+1))) {
                    if (bar.get(i) > bar.get(i+1)) {
                        Collections.swap(parentLayer, i, i+1);
                        Collections.swap(med, i, i+1);
                        Collections.swap(bar, i, i+1);
                        changed = true;
                    }
                }
            }
        }
    }

    private void barycenterMethod(List<List<MNode>> layers, List<MEdge> mEdges) {
        for (int i = 0; i < layers.size() - 1; i++) {
            barycenterMethodInternal(layers.get(i+1), layers.get(i), mEdges);
        }
    }

    private void barycenterMethodInternal(List<MNode> parentLayer, List<MNode> childLayer, List<MEdge> mEdges) {
        List<MEdge> edges = new ArrayList<MEdge>();

        for (MEdge e : mEdges) {
            if (parentLayer.contains(e.getTo()) && childLayer.contains(e.getFrom())) {
                edges.add(e);
            }
        }

        int sum = 0;
        List<Integer> bar = new ArrayList<Integer>();
        for (int i = 0; i < parentLayer.size(); i++) {
            MNode p = parentLayer.get(i);

            for (int j = 0; j < childLayer.size(); j++) {
                for (MEdge e : edges) {
                    if (e.getFrom().equals(childLayer.get(j)) && e.getTo().equals(p)) {
                        sum += j * 10;
                    }
                }

                bar.add(sum / childLayer.size());
            }
        }

        if (DEBUG) {
            for (Integer b : bar) {
                System.out.print(b + " ");
            }
            System.out.println();
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (int i = 0; i < parentLayer.size() - 1; i++) {
                if (bar.get(i) > bar.get(i+1)) {
                    Collections.swap(parentLayer, i, i+1);
                    Collections.swap(bar, i, i+1);
                    changed = true;
                }
            }
        }
    }

    private void adjacentExchange(List<List<MNode>> layers, List<MEdge> mEdges) {
        for (int i = layers.size() - 2; i >= 0; i--) {
            if (DEBUG) {
                System.out.println("reduce: processing L" + i + "-L" + (i+1));
            }
            adjacentExchangeInternal(layers.get(i+1), layers.get(i), mEdges); // parent layer, child layer
        }
    }

    private void adjacentExchangeInternal(List<MNode> parentLayer, List<MNode> childLayer, List<MEdge> mEdges) {
        boolean changed;
        int cross1, cross2;
        List<MEdge> edges = new ArrayList<MEdge>();

        for (MEdge e : mEdges) {
            if (parentLayer.contains(e.getTo()) && childLayer.contains(e.getFrom())) {
                if (DEBUG) {
                    System.out.print("reduce: added ");
                    printNodeInfo(e.getFrom());
                    System.out.print("->");
                    printNodeInfo(e.getTo());
                    System.out.println(" to edgelist");
                }

                edges.add(e);
            }
        }

        do {
            changed = false;

            /* using the Adjacent-Exchange method */
            for (int i = 0; i < childLayer.size() - 1; i++) {
                cross1 = countCrossings(parentLayer, childLayer, edges, i, i+1);

                java.util.Collections.swap(childLayer, i, i+1);
                cross2 = countCrossings(parentLayer, childLayer, edges, i, i+1);

                if (cross1 > cross2) {
                    changed = true;
                    if (DEBUG) {
                        System.out.print("$$ swapping ");
                        printNodeInfo(childLayer.get(i));
                        System.out.print(" with ");
                        printNodeInfo(childLayer.get(i+1));
                        System.out.println();
                        System.out.println("$$ changed: reduced crossings from " + cross1 + " to " + cross2);
                    }
                } else {
                    if (DEBUG)
                        System.out.println("$$ swapping back");
                    Collections.swap(childLayer, i, i+1); // swap back
                }
            }
        } while (changed);
    }

    private int countCrossings(List<MNode> parentLayer, List<MNode> childLayer, List<MEdge> edges, int u, int v) {
        int cr = 0;

        for (MEdge e : edges) {
            if (e.getFrom().equals(childLayer.get(u))) {
                for (MEdge f : edges) {
                    if (f.getFrom().equals(childLayer.get(v))) {
                        if (u < v && parentLayer.indexOf(e.getTo()) > parentLayer.indexOf(f.getTo())) {
                            cr++;
                        } else if (u > v && parentLayer.indexOf(e.getFrom()) < parentLayer.indexOf(f.getFrom())) {
                            if (DEBUG)
                                System.out.println("count: (shouldn't happen)");
                            cr++;
                        } else if (u == v) {
                            if (DEBUG)
                                System.out.println("count: Cuu == 0");
                            return 0; // Cuu = 0
                        }
                    }
                }
            }
        }

        if (DEBUG)
            System.out.println("count: returning " + cr + " on ("
                    + u + ": "
                    + (childLayer.get(u).isDummy() ? "dummy"
                        : (childLayer.get(u).getNode().getWidth() + ", " + childLayer.get(u).getNode().getHeight()))
                    + ") vs ("
                    + v + ": "
                    + (childLayer.get(v).isDummy() ? "dummy"
                        : (childLayer.get(v).getNode().getWidth() + ", " + childLayer.get(v).getNode().getHeight()))
                    + ")");

        return cr;
    }

    private void plotTree(List<List<MNode>> layers, List<MNode> mNodes, List<MEdge> mEdges, ControlXY xCmd, ControlXY yCmd) {
        int maxwidth = 0;
        List<Integer> heights = new ArrayList<Integer>(), widths = new ArrayList<Integer>();

        for (List<MNode> layer : layers) {
            int h = 0, w = 0;

            for (MNode n : layer) {
                if (!n.isDummy()) {
                    if (n.getNode().getHeight() > h)
                        h = (int) n.getNode().getHeight();
                    
                    w += xDist + n.getNode().getWidth();
                } else {
                    w += xDist + dummyWidth;
                }
            }

            w -= xDist;

            heights.add(h);
            widths.add(w);

            if (w > maxwidth) maxwidth = w;
        }
        /*
         * maxwidth: maximum width from all layers
         * 
         * heights: maximum height of each layer
         * widths: total width of each layer 
         */

        if (DEBUG)
            System.out.println("MAXWIDTH: " + maxwidth);

        int xPos, yPos = yDist;

        maxX = 0;
        for (int i = 0; i < layers.size(); i++) {
            List<MNode> l = layers.get(i);

            xPos = xDist;

            for (MNode n : l) {
                int nodeWidth = (maxwidth - widths.get(i)) / l.size();

                if (DEBUG)
                    System.out.println("width = " + nodeWidth);

                if (!n.isDummy()) {
                    xCmd.link(n.getNode(), xPos + nodeWidth / 2);
                    yCmd.link(n.getNode(), yPos);

                    if (DEBUG)
                        System.out.println("link(" + (xPos + nodeWidth / 2) + ", " + yPos + ")");

                    xPos += nodeWidth + n.getNode().getWidth() + xDist;

                    if (DEBUG)
                        System.out.println("Next node will be placed at: " + xPos);
                } else {
                    xPos += nodeWidth + dummyWidth + xDist;
                }
            }
            if (xPos > maxX) maxX = xPos;

            yPos += heights.get(i) + yDist;
        }

        maxY = yPos;

        if (DEBUG)
            System.out.println("max = " + maxX + ", " + maxY);
    }

    protected double[] distributeNodes(List<Node<?>> nodeList, List<EdgeFigure<?>> edgeList) {
        maxX = 0;
        maxY = 0;
        ControlXY xCmd = new ControlXY();
        ControlXY yCmd = new ControlXY();

        generateTree(nodeList, edgeList, xCmd, yCmd);

        for (Node<?> aNodeList : nodeList) {
            aNodeList.translateTo(xCmd, yCmd);
        }

        double[] rez = new double[2];
        rez[0] = maxX;
        rez[1] = maxY;

        return rez;
    }
}
