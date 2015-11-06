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
package lrg.jMondrian.view;

import lrg.jMondrian.util.MenuReaction;
import lrg.jMondrian.util.StaticFigures;

import java.util.*;
import java.util.List;

import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

import javax.swing.*;
import javax.imageio.ImageIO;

public class ViewRenderer extends JPanel implements ActionListener, ViewRendererInterface {

    private ShapeJavaFactory factory = new ShapeJavaFactory();
    private double zoom = 1;
    private String title;
    private static MenuReaction common;

    public static void setMenuReaction(MenuReaction listener) {
        common = listener;
    }

    private final class ShapeJavaFactory implements ShapeElementFactory {

        private List<Shape> shapes = new ArrayList<Shape>();
        private List<Color> colors = new ArrayList<Color>();
        private List<String> labels = new ArrayList<String>();
        private List<Integer> xLabel = new ArrayList<Integer>();
        private List<Integer> yLabel = new ArrayList<Integer>();
        private List<Object> m = new ArrayList<Object>();
        private List<String> d = new ArrayList<String>();

        private ShapeJavaFactory() {}

        public void addRectangle(Object ent, String descr, int x1, int y1, int width, int heigth, int color, boolean border) {
            shapes.add(new Rectangle2D.Double(x1,y1,width,heigth));
            colors.add(new Color(color));
            m.add(ent);
            d.add(descr);
            if(border) {
                shapes.add(new Line2D.Double(x1,y1,x1+width,y1));
                colors.add(Color.BLACK);
                m.add(null);
                d.add(null);
                shapes.add(new Line2D.Double(x1,y1,x1,y1+heigth));
                colors.add(Color.BLACK);
                m.add(null);
                d.add(null);
                shapes.add(new Line2D.Double(x1+width,y1,x1+width,y1+heigth));
                colors.add(Color.BLACK);
                m.add(null);
                d.add(null);
                shapes.add(new Line2D.Double(x1,y1+heigth,x1+width,y1+heigth));
                colors.add(Color.BLACK);
                m.add(null);
                d.add(null);
            }
        }

        public void addRectangle(Object ent, String descr, int x1, int y1, int width, int heigth, int color, int frameColor) {
            shapes.add(new Rectangle2D.Double(x1,y1,width,heigth));
            colors.add(new Color(color));
            m.add(ent);
            d.add(descr);
            shapes.add(new Line2D.Double(x1,y1,x1+width,y1));
            colors.add(new Color(frameColor));
            m.add(null);
            d.add(null);
            shapes.add(new Line2D.Double(x1,y1,x1,y1+heigth));
            colors.add(new Color(frameColor));
            m.add(null);
            d.add(null);
            shapes.add(new Line2D.Double(x1+width,y1,x1+width,y1+heigth));
            colors.add(new Color(frameColor));
            m.add(null);
            d.add(null);
            shapes.add(new Line2D.Double(x1,y1+heigth,x1+width,y1+heigth));
            colors.add(new Color(frameColor));
            m.add(null);
            d.add(null);
        }

        public void addEllipse(Object ent, String descr, int x1, int y1, int width, int heigth, int color, boolean border) {
            if(border) {
                shapes.add(new Ellipse2D.Double(x1,y1,width,heigth));
                colors.add(Color.BLACK);
                m.add(null);
                d.add(null);
            }
            shapes.add(new Ellipse2D.Double(x1+1,y1+1,width-2,heigth-2));
            colors.add(new Color(color));
            m.add(ent);
            d.add(descr);
        }

        public void addEllipse(Object ent, String descr, int x1, int y1, int width, int heigth, int color, int frameColor) {
            shapes.add(new Ellipse2D.Double(x1,y1,width,heigth));
            colors.add(new Color(frameColor));
            m.add(null);
            d.add(null);
            shapes.add(new Ellipse2D.Double(x1+1,y1+1,width-2,heigth-2));
            colors.add(new Color(color));
            m.add(ent);
            d.add(descr);
        }

        public void addLine(Object ent, String descr, int x1, int y1, int x2, int y2, int color, boolean oriented) {
            if(oriented) {
        		double arrow[] = StaticFigures.getArrow(x1, y1, x2, y2);
        		if (arrow!=null) {
        			shapes.add(new Line2D.Double(arrow[0],arrow[1],arrow[2],arrow[3]));
        			shapes.add(new Line2D.Double(arrow[4],arrow[5],arrow[6],arrow[7]));
                    colors.add(new Color(color));
                    colors.add(new Color(color));
                    m.add(ent);
                    m.add(ent);
                    d.add(descr);
                    d.add(descr);
        		}
            }
            shapes.add(new Line2D.Double(x1,y1,x2,y2));
            colors.add(new Color(color));
            m.add(ent);
            d.add(descr);
        }

        public void addPolyLine(Object ent, String descr, List<Integer> x, List<Integer> y, int color, boolean oriented, boolean closed) {
            for(int i = 1; i < x.size(); i++) {
                shapes.add(new Line2D.Double(x.get(i-1),y.get(i-1),x.get(i),y.get(i)));
                colors.add(new Color(color));
                m.add(ent);
                d.add(descr);
            }

            if (closed) {
                shapes.add(new Line2D.Double(x.get(0),y.get(0),x.get(x.size()-1),y.get(y.size()-1)));
                colors.add(new Color(color));
                m.add(ent);
                d.add(descr);            
            }
            
            if(oriented && x.size()>1) {
        		double arrow[] = StaticFigures.getArrow(x.get(x.size()-2),y.get(y.size()-2), x.get(x.size()-1),y.get(y.size()-1));
        		if (arrow!=null) {
        			shapes.add(new Line2D.Double(arrow[0],arrow[1],arrow[2],arrow[3]));
                    colors.add(new Color(color));
                    m.add(ent);
                    d.add(descr);            
        			
        			shapes.add(new Line2D.Double(arrow[4],arrow[5],arrow[6],arrow[7]));
                    colors.add(new Color(color));
                    m.add(ent);
                    d.add(descr);            
        		}
            }
            
        }
        
        public void addPolyLine(Object ent, String descr, List<Integer> x, List<Integer> y) {
    		this.addPolyLine(ent, descr, x, y, Color.LIGHT_GRAY.getRGB(), false, true);
        }
        
        private void addShapeOutline(Object entity, String descr, Shape curve, int color, boolean oriented) {
        	// mostly used for curve approximation
        	List<Integer> x= new ArrayList<Integer>(), y=new ArrayList<Integer>();
        	PathIterator pi = curve.getPathIterator(null,0.7);
        	
        	while(!pi.isDone()) {
        		double point[] = new double[6];
        		int ret = pi.currentSegment(point);
        		pi.next();
        		if (ret==PathIterator.SEG_LINETO || ret==PathIterator.SEG_MOVETO) {
        			x.add((int)point[0]);
        			y.add((int)point[1]);
        		} else if (ret==PathIterator.SEG_CLOSE) {
        			break;
        		} else {
        			throw new RuntimeException("I don't know this seg type: "+ret);
        		}
        	}
        	addPolyLine(entity, descr, x, y, color, oriented, false);
        }
        
        public void addQuadCurve(Object ent, String descr, double x1, double y1, double cx1, double cy1, double x2, double y2, int color, boolean oriented) {
        	addShapeOutline(ent, descr, new QuadCurve2D.Double(x1,y1, cx1,cy1, x2,y2), color, oriented);
    	}
        public void addCubicCurve(Object ent, String descr, double x1, double y1, double cx1, double cy1, double cx2, double cy2, double x2, double y2, int color, boolean oriented) {
        	addShapeOutline(ent, descr, new CubicCurve2D.Double(x1,y1, cx1,cy1, cx2,cy2, x2,y2), color, oriented);
        }
        

        public void addText(Object ent, String descr, String text, int x1, int y1, int color) {
            labels.add(text);
            xLabel.add(x1);
            yLabel.add(y1);
        }

        public void update(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            Iterator<Shape> it = shapes.iterator();
            Iterator<Color> it1 = colors.iterator();
            Shape s;
            AffineTransform transf = new AffineTransform(zoom,0,0,zoom,0,0);

            g2.setColor(Color.WHITE);
            g2.fillRect(0,0,getWidth(), getHeight());
            while(it.hasNext()){
                s = transf.createTransformedShape(it.next());
                Color c = it1.next();
                g2.setPaint(c);
                g2.draw(s);
                g2.setPaint(c);
                g2.fill(s);
            }

            g2.setPaint(Color.BLACK);
            Iterator<String> it2 = labels.iterator();
            double point[] = new double[2];
            double trans[] = new double[2];
            int i = 0;
            while(it2.hasNext()) {
                point[0] = xLabel.get(i);
                point[1] = yLabel.get(i);
                transf.transform(point,0,trans,0,1);
                g2.drawString(it2.next(),(int)trans[0],(int)trans[1]);
                i++;
            }
        }

        public String findStatusInformation(int x, int y) {
            AffineTransform transf = new AffineTransform(zoom,0,0,zoom,0,0);
            for(int i = shapes.size() - 1; i >=0; i--) {
                Shape newShape = transf.createTransformedShape(shapes.get(i));
                if(newShape.contains(x,y,1,1) && m.get(i)!=null) {
                    return d.get(i);
                }
            }
            return "";
        }

        public Object findEntity(int x, int y) {
            AffineTransform transf = new AffineTransform(zoom,0,0,zoom,0,0);
            for(int i = shapes.size() - 1; i >=0; i--) {
                Shape newShape = transf.createTransformedShape(shapes.get(i));
                if(newShape.contains(x,y,1,1) && m.get(i)!=null) {
                    return m.get(i);
                }
            }
            return null;
        }

    }

    public ShapeElementFactory getShapeFactory() {
        return factory;
    }

    public void setPreferredSize(int width, int height) {
        this.setPreferredSize(new Dimension(width,height));        
    }

    public ViewRenderer(String title) {
        this.title = title;
    }

    public ViewRenderer() {
        this("lrg.jMondrian");
    }

    public void update(Graphics g) {
        factory.update(g);
    }

    public void paint(Graphics g){
        update(g);
        this.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        this.revalidate();
    }

    public void open() {

        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);

        final JFrame f = new JFrame(title);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("Save");
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,java.awt.event.InputEvent.CTRL_MASK));
        menu.add(item);
        item = new JMenuItem("Close");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.dispose();    
            }
        });
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,java.awt.event.InputEvent.CTRL_MASK));
        menu.add(item);
        menuBar.add(menu);
        menu = new JMenu("Zoom");
        item = new JMenuItem("Zoom In");
        item.setAccelerator(KeyStroke.getKeyStroke('+'));
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Zoom Out");
        item.setAccelerator(KeyStroke.getKeyStroke('-'));
        item.addActionListener(this);
        menu.add(item);
        menuBar.add(menu);

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {f.dispose();}
        });

        JScrollPane scroll = new JScrollPane(this);
        JPanel statusBar = new JPanel(new FlowLayout());
        final JLabel statusText = new JLabel(title);
        statusBar.add(statusText);
        JPanel content = new JPanel(new BorderLayout());

        if(common != null) {
            this.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    Object ent = factory.findEntity(e.getX(),e.getY());
                    if(ent != null) {
                        common.buildFor(ent,e);
                    }
                }
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });
        }

        this.addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                String info = factory.findStatusInformation((int)e.getPoint().getX(),(int)e.getPoint().getY());
                if(!info.equals("")) {
                    statusText.setText(info);
                }
            }
        });

        content.setOpaque(true);
        content.add(scroll, BorderLayout.CENTER);
        content.add(statusBar, BorderLayout.SOUTH);
        f.setPreferredSize(new Dimension(800,500));
        f.setJMenuBar(menuBar);
        f.setContentPane(content);
        f.pack();
        f.setLocation(25,25);
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem item  = (JMenuItem) e.getSource();
        if(item.getText().equals("Zoom In")){
            zoom += 0.2;
            this.setPreferredSize(new Dimension((int) (this.getWidth() * 1.2), (int)(this.getHeight() * 1.2)));
            this.revalidate();
            repaint();
        }
        if(item.getText().equals("Zoom Out")){
            if(zoom >= 0.4){
                zoom -= 0.2;
                this.setPreferredSize(new Dimension((int) (this.getWidth() / 1.2), (int)(this.getHeight() / 1.2)));
                this.revalidate();
                repaint();
            }
        }
        if(item.getText().equals("Save")) {
            try {
                FileOutputStream out = new FileOutputStream(title + ".png");
                BufferedImage img = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();
                g.setClip(0,0,this.getWidth(),this.getHeight());
                g.setColor(Color.WHITE);
                g.fillRect(0,0,this.getWidth(),this.getHeight());
                this.paint(g);
                g.dispose();
                ImageIO.write(img,"png",out);
                out.close();
            } catch(Exception ex) {
                System.out.println("jMondrian : Error saving the image - " + ex);
            }
        }
    }
}
