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

import java.awt.Color;
import java.util.ArrayList;

import lrg.jMondrian.access.Command;
import lrg.jMondrian.figures.Figure;
import lrg.jMondrian.layouts.ScatterPlotLayout;
import lrg.jMondrian.painters.EllipseNodePainter;
import lrg.jMondrian.painters.RectangleNodePainter;
import lrg.jMondrian.view.OrganicViewRenderer;

public class ICSM2010 {
	
	private static class EllipseModel {
		private int x,y;
		private int rx,ry;
		private int color;
		public EllipseModel(int x,int y, int rx, int ry, int color) {
			this.x = x;
			this.y = y;
			this.rx = rx;
			this.ry = ry;
			this.color = color;
		}
	}

	private static class RectangleModel {
		private int x,y;
		private int width,height;
		public RectangleModel(int x,int y,int width, int height) {
			this.x = x;
			this.y = y;
			this.height = height;
			this.width = width;
		}
	}

	public static void main(String argv[]) {
		
		//Prepare model
		ArrayList<EllipseModel> nodes = new ArrayList<EllipseModel>();
		nodes.add(new EllipseModel(50,50,20,40,Color.RED.getRGB()));
		nodes.add(new EllipseModel(25,90,60,100,Color.BLUE.getRGB()));
		nodes.add(new EllipseModel(75,70,45,40,Color.BLUE.getRGB()));
		nodes.add(new EllipseModel(95,85,17,15,Color.WHITE.getRGB()));
		nodes.add(new EllipseModel(75,110,60,100,Color.RED.getRGB()));
		nodes.add(new EllipseModel(35,120,35,60,Color.WHITE.getRGB()));
		nodes.add(new EllipseModel(95,130,30,60,Color.WHITE.getRGB()));

		ArrayList<RectangleModel> hiddingNodes = new ArrayList<RectangleModel>();
		hiddingNodes.add(new RectangleModel(25,144,52,50));
		hiddingNodes.add(new RectangleModel(76,159,60,51));
		
		//Prepare figure
		Figure<EllipseModel> f = new Figure<EllipseModel>();
		f.nodesUsing(nodes, new EllipseNodePainter<EllipseModel>(false)
				.x(new Command<EllipseModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.x;
					}})
				.y(new Command<EllipseModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.y;
					}})
				.width(new Command<EllipseModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.rx;
					}})
				.height(new Command<EllipseModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.ry;
					}})
				.color(new Command<EllipseModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.color;
					}})
		);
		f.nodesUsing(hiddingNodes, new RectangleNodePainter<RectangleModel>(false)
				.x(new Command<RectangleModel,Integer>() {
					public Integer execute() {
						return receiver.x;
					}})
				.y(new Command<RectangleModel,Integer>() {
					public Integer execute() {
						return receiver.y;
				}})
				.width(new Command<RectangleModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.width;
					}})
				.height(new Command<RectangleModel,Integer>() {
					@Override
					public Integer execute() {
						return receiver.height;
				}})
		);
		f.layout(new ScatterPlotLayout());
		
		//View
		OrganicViewRenderer r = new OrganicViewRenderer();
		f.renderOn(r);
		r.open();
	}

}
