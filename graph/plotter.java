 
	//********************************************************************
	//plotter.java       Author: John Lie
	//
	//capstone plotter
	//converted from eclipse
	//********************************************************************

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.lang.Double;
import java.lang.Math;
import java.lang.String;

class gCanvas extends Canvas {
  plotter gp; 
	gCanvas(plotter p) {
		gp = p;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(640,480);
	}
	
	public void update(Graphics g) {
		gp.panelUpdate(g);
	}

	public void paint (Graphics g) {
		gp.panelUpdate(g);
	}
};

class plotterLayout implements LayoutManager {
	public plotterLayout() {}
	public void addLayoutComponent(String name, Component c) {}
	public void removeLayoutComponent(Component c) {}
	public Dimension preferredLayoutSize(Container target) {
		return new Dimension(500,500);
	}
	public Dimension minimumLayoutSize(Container target) {
		return new Dimension(100,100);
	}
	public void layoutContainer(Container t) {
		int cw = (int)((double)(t.size().width) * (3.0/4.0));
	  t.getComponent(0).move(t.size().width-cw,0);
	  t.getComponent(0).resize(cw, t.size().height);
	  t.getComponent(1).move(0,0);
	  t.getComponent(1).resize(t.size().width-cw, t.size().height);
	}
}

class Polynomial {
  public double [] coeff_arr, power_arr;

	Polynomial(double[] i_coeff_arr, double[] i_power_arr) {
		coeff_arr=i_coeff_arr;
		power_arr=i_power_arr;
	}
}

public class plotter extends Applet implements ComponentListener

{
	Panel units;
	TextField equation = new TextField("11.1  -  10*x^3-9x^1 - 4*x^4 +9x- 2x^0", 10);
	TextField coeff3 = new TextField("2.3", 5);
	TextField coeff2 = new TextField("1.8", 5);
	TextField coeff1 = new TextField("0.6", 5);
	TextField coeff0 = new TextField("0.2", 5);
	TextField xmin = new TextField("-10", 5);
	TextField xmax = new TextField("10", 5);
	TextField ymin = new TextField("-10", 5);
	TextField ymax = new TextField("10", 5);
  TextField intervals = new TextField("1", 5);


	Canvas gcv;
  Image dbimage;

	public void init()
	{
    setLayout(new plotterLayout());


		gcv = new gCanvas(this);
		gcv.setSize(340,380);
		gcv.setBackground(new Color(0,0,0));
		
		gcv.addComponentListener(this);
		add(gcv);

		units = new Panel();
		units.setLayout(new GridLayout(12,1,0,0));
		units.add(new Label("equation"));
		units.add(equation);
		units.add(new Label("xmin"));
		units.add(xmin);
		units.add(new Label("xmax"));
		units.add(xmax);
		units.add(new Label("ymin"));
		units.add(ymin);
		units.add(new Label("ymax"));
		units.add(ymax);
		units.add(new Label("intervals"));
		units.add(intervals);
		add(units);
		
	  Dimension d = gcv.getSize();
  	dbimage = createImage(d.width,d.height);

	}

	public boolean action(Event evt, Object whatAction)
	{
		if((evt.target instanceof TextField))
		{ 
			eventRepaint();
			return true;
		}
		return false;
	}
	
    public void componentHidden(ComponentEvent e) {
      Dimension d = gcv.getSize(null);	
			dbimage = createImage(d.width,d.height);

		}

    public void componentMoved(ComponentEvent e) {
      Dimension d = gcv.getSize(null);	
			dbimage = createImage(d.width,d.height);

    }

		public void componentResized(ComponentEvent e) {
      Dimension d = gcv.getSize(null);	
			dbimage = createImage(d.width,d.height);


		}
		public void componentShown(ComponentEvent e) {
      Dimension d = gcv.getSize(null);	
			dbimage = createImage(d.width,d.height);

    }

	public void eventRepaint() {
		gcv.repaint();
	}

  public Polynomial parseEquation(String str) {

		String clean_str= str.replaceAll(" ", "");
		clean_str = clean_str.replaceAll("\\*", "");

		clean_str = clean_str.replaceAll("\\+","\\~+");
		clean_str = clean_str.replaceAll("\\-","\\~-");

		
		String[] isolate_str = clean_str.split("\\~");
		String[] coeff_str = new String[isolate_str.length];
		String[] power_str = new String[isolate_str.length];
		double[] coeff_arr = new double[coeff_str.length];
		double[] power_arr = new double[power_str.length];
		for (int i=0; i < isolate_str.length; i++) {
			coeff_str[i] = isolate_str[i].replaceAll("(.*)x.*", "$1");
			if (coeff_str[i].equals("")) {
				coeff_str[i] = "1";
			}
		  power_str[i] = isolate_str[i].replaceAll(".*(x.*)", "$1");
			if (-1==power_str[i].indexOf("x")) {
				power_str[i] = "0";
			}
		  power_str[i] = power_str[i].replaceAll("x", "");
		  power_str[i] = power_str[i].replaceAll("\\^", "");
			if (power_str[i].equals("")) {
				power_str[i] = "1";
			}

		  coeff_arr[i] = Double.parseDouble(coeff_str[i]);
		  power_arr[i] = Double.parseDouble(power_str[i]);
		
		}
			return new Polynomial(coeff_arr, power_arr);
	}

	public void panelUpdate(Graphics realg) {
		Graphics g = dbimage.getGraphics();
		g.setColor(Color.black);
		Dimension d = gcv.getSize();
		g.fillRect(0,0,d.width,d.height);
		g.setColor(Color.white);
		g.drawString("y=m*x+b", 0, 0);
		drawAxes(g);
		Polynomial p=parseEquation(equation.getText());
		poly_plot(
				g,
				p);

		realg.drawImage(dbimage, 0, 0, this);
	}

  public void poly_plot(Graphics g, Polynomial p) {
   Dimension d = gcv.getSize();
		Point2D.Double pxl_pt = new Point2D.Double(0.0, 0.0);
		Point2D.Double m_pt = new Point2D.Double(0.0, 0.0);
		Point last_pxl_pt = new Point(0,0);
	   
		for (int pxl_x=0; pxl_x < d.width; pxl_x+=1) {
			pxl_pt.x=pxl_x;
			pxl_pt.y=0;	
			m_pt = p2m(pxl_pt);
			m_pt.y=0.0;

			for (int i=0; i < p.coeff_arr.length; i+=1) {
				m_pt.y += p.coeff_arr[i]*Math.pow(m_pt.x, p.power_arr[i]); 
			}

			pxl_pt = m2p(m_pt);
			g.setColor(Color.white);

			if (pxl_x > 0) {
				g.drawLine(last_pxl_pt.x, last_pxl_pt.y, pxl_x, (int) pxl_pt.y);
			}
			last_pxl_pt.x = pxl_x;
			last_pxl_pt.y = (int)pxl_pt.y;

		}		
	 
	}

  public void pxl_line_plot(Graphics g, double i_slope, double i_y_intercept) {
	  Dimension d = gcv.getSize();
		Point2D.Double pxl_pt = new Point2D.Double(0.0, 0.0);
		Point2D.Double m_pt = new Point2D.Double(0.0, 0.0);
		Point last_pxl_pt = new Point(0,0);
	   
		for (int pxl_x=0; pxl_x < d.width; pxl_x+=1) {
			pxl_pt.x=pxl_x;
			pxl_pt.y=0;	
			m_pt = p2m(pxl_pt);
			m_pt.y = (i_slope*m_pt.x)+i_y_intercept;
			pxl_pt = m2p(m_pt);
			g.setColor(Color.white);

			if (pxl_x > 0) {
				g.drawLine(last_pxl_pt.x, last_pxl_pt.y, pxl_x, (int) pxl_pt.y);
			}
			last_pxl_pt.x = pxl_x;
			last_pxl_pt.y = (int)pxl_pt.y;

		}		
	}

	public void drawAxes(Graphics g) {
    double dxmin = Double.parseDouble(xmin.getText());
		double dxmax = Double.parseDouble(xmax.getText()); 
		double dymin = Double.parseDouble(ymin.getText());
		double dymax = Double.parseDouble(ymax.getText()); 
    double dbl_intervals = Double.parseDouble(intervals.getText());
		
		Point2D.Double tick_mpt = new Point2D.Double(0.0, 0.0);
		Point2D.Double tick_pxl_pt = new Point2D.Double(0.0, 0.0);
		
		g.setColor(Color.darkGray);
		for (double tick_mpx=0.0; tick_mpx<=dxmax; tick_mpx+=dbl_intervals) {
		  tick_mpt.x = tick_mpx;
		  tick_mpt.y = 0.0;	
			tick_pxl_pt = m2p(tick_mpt);
			g.drawLine((int)tick_pxl_pt.x, (int)0, (int)tick_pxl_pt.x, gcv.getSize().height);

		}
		for (double tick_mpx=0.0; tick_mpx>=dxmin; tick_mpx-=dbl_intervals) {
		  tick_mpt.x = tick_mpx;
		  tick_mpt.y = 0.0;	
			tick_pxl_pt = m2p(tick_mpt);
			g.drawLine((int)tick_pxl_pt.x, (int)0, (int)tick_pxl_pt.x, gcv.getSize().height);

		}
		for (double tick_mpy=0.0; tick_mpy<=dymax; tick_mpy+=dbl_intervals) {
		  tick_mpt.x = 0.0;
		  tick_mpt.y = tick_mpy;	
			tick_pxl_pt = m2p(tick_mpt);
			g.drawLine((int)0, (int)tick_pxl_pt.y, (int)gcv.getSize().width, (int)tick_pxl_pt.y);

		}
		for (double tick_mpy=0.0; tick_mpy>=dymin; tick_mpy-=dbl_intervals) {
		  tick_mpt.x = 0.0;
		  tick_mpt.y = tick_mpy;	
			tick_pxl_pt = m2p(tick_mpt);
			g.drawLine((int)0, (int)tick_pxl_pt.y, (int)gcv.getSize().width, (int)tick_pxl_pt.y);

		}

		g.setColor(Color.white);
		Point2D.Double xaxis_p1 = m2p(new Point2D.Double(dxmin, 0.0));
		Point2D.Double xaxis_p2 = m2p(new Point2D.Double(dxmax, 0.0));
		g.drawLine((int)xaxis_p1.x, (int)xaxis_p1.y, (int)xaxis_p2.x, (int)xaxis_p2.y);
		
		Point2D.Double yaxis_p1 = m2p(new Point2D.Double(0.0, dymin));
		Point2D.Double yaxis_p2 = m2p(new Point2D.Double(0.0, dymax));
		g.drawLine((int)yaxis_p1.x, (int)yaxis_p1.y, (int)yaxis_p2.x, (int)yaxis_p2.y);
	}

	public Point2D.Double m2p(Point2D.Double m) {
		double dxmin = Double.parseDouble(xmin.getText());
		double dxmax = Double.parseDouble(xmax.getText()); 
		double dymin = Double.parseDouble(ymin.getText());
		double dymax = Double.parseDouble(ymax.getText()); 
		
		Dimension d = gcv.getSize();
    double km2p_x = d.width/(dxmax-dxmin);
    double km2p_y = d.height/(dymax-dymin);
 		double px1 = (km2p_x*((m.x)-dxmin));
		double py1 = d.height-(km2p_y*(((m.y))-dymin));
	  	
		return new Point2D.Double(px1, py1);
	}
	
	public Point2D.Double p2m(Point2D.Double pxl_pt) {
		double dxmin = Double.parseDouble(xmin.getText());
		double dxmax = Double.parseDouble(xmax.getText()); 
		double dymin = Double.parseDouble(ymin.getText());
		double dymax = Double.parseDouble(ymax.getText()); 
		
		Dimension d = gcv.getSize();
    double kp2m_x = (dxmax-dxmin)/((double)d.width);
    double kp2m_y = (dymax-dymin)/((double)d.height);
 		double mx1 = (kp2m_x*((pxl_pt.x)))+dxmin;
		double my1 = -((kp2m_y*(((pxl_pt.y))))-dymax);
	  	
		return new Point2D.Double(mx1, my1);
	}
	
	public void mline(double x1, double y1, double x2, double y2, Graphics g) {
    Point2D.Double p1=new Point2D.Double(x1, y1);
    Point2D.Double p2=new Point2D.Double(x2, y2);
	  Point2D.Double pp1=m2p(p1);
	  Point2D.Double pp2=m2p(p2);

		g.drawLine((int)pp1.x, (int)pp1.y, (int)pp2.x, (int)pp2.y);
	}
}




