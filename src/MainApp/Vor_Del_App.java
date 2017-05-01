package MainApp;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;

import hlstructs.Triangulation;
import ds.Point;
import ds.Triangle;

public class Vor_Del_App extends javax.swing.JApplet
		implements Runnable, ActionListener, MouseListener {
	
	private Component mouseIn = null;
	
	private static String title = "Voronoi-Delaunay Diagrams";
	private JRadioButton vorButton = new JRadioButton("Voronoi Diagram");
	private JRadioButton delButton = new JRadioButton("Delaunay Triangulation");
	private JLabel circleLab = new JLabel("Show Empty Circles");
    private JLabel delLab = new JLabel("Show Delaunay Edges");
    private JLabel vorLab = new JLabel("Show Voronoi Edges");
	
	private JButton clear = new JButton("Clear Screen");
	private DelPanel delCanvas= new DelPanel(this);
	
	public static void main (String[] args) {
		Vor_Del_App applet = new Vor_Del_App();
		applet.init();
		JFrame window = new JFrame();
		window.setSize(800, 600);
		window.setTitle(title);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(applet, "Center");
		window.setVisible(true);
	}

	public void init() {
		try {SwingUtilities.invokeAndWait(this);}
        catch (Exception e) {System.err.println("Initialization failure");}
    }
	
	public void run() {
		setLayout(new BorderLayout());
		
		ButtonGroup group = new ButtonGroup();
		group.add(vorButton);
		group.add(delButton);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(vorButton);
		buttonPanel.add(delButton);
		buttonPanel.add(clear);
		this.add(buttonPanel, "North");
		
		JPanel labPanel = new JPanel();
		labPanel.add(circleLab);
		labPanel.add(new Label("     "));
		labPanel.add(vorLab);
		labPanel.add(new Label("     "));
		labPanel.add(delLab);
		this.add(labPanel, "South");
		
		//delaunay window
		delCanvas.setBackground(Color.gray);
		this.add(delCanvas, "Center");
		
		vorButton.addActionListener(this);
		delButton.addActionListener(this);
		clear.addActionListener(this);

		delCanvas.addMouseListener(this);
		circleLab.addMouseListener(this);
		delLab.addMouseListener(this);
		vorLab.addMouseListener(this);
		
		vorButton.doClick();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseIn = e.getComponent();
		if(mouseIn instanceof JLabel)
			delCanvas.repaint();
		else
			mouseIn = null;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseIn = null;
		if(e.getComponent() instanceof JLabel)
			delCanvas.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() != delCanvas)
			return;
		Point point = new Point(e.getX(), e.getY());
		delCanvas.addPoint(point);
		delCanvas.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == clear)
			delCanvas.clear();
		delCanvas.repaint();
		
	}
	
	/*Voronoi mode*/
    public boolean isVoronoi() {
        return vorButton.isSelected();
    }

    public boolean showCircles() {
        return mouseIn == circleLab;
    }

    public boolean showDelaunay() {
        return mouseIn == delLab;
    }

    public boolean showVoronoi() {
        return mouseIn == vorLab;
    }
}
	
class DelPanel extends JPanel {
		
	private Vor_Del_App app;
	private Triangulation delTri;
	private Triangle startTri;
	private static int triSize = 10000;
	private Graphics g;
	
	public static Color vorColor = Color.pink;
	public static Color delColor = Color.cyan;
	public static int pRad = 4;
	
	public DelPanel(Vor_Del_App app) {
		this.app = app;
		startTri = new Triangle(
				new Point(-triSize, -triSize),
				new Point( triSize, -triSize),
				new Point(		 0,  triSize)
				);
		delTri = new Triangulation(startTri);
	}
	
	public void addPoint(Point pt) {
		delTri.delaunayAdd(pt);
	}
	
	public void clear() {
		delTri = new Triangulation(startTri);
	}
	
	/*Low level draw methods*/
	
	//draw point
	public void draw(Point pt) {
		int r = pRad;
		int x = (int) pt.coord(0);
		int y = (int) pt.coord(1);
		g.fillOval(x-r, y-r, 2*r, 2*r);
	} 
	
	//draw circle
	public void draw(Point c, double rad) {
		int x = (int) c.coord(0);
		int y = (int) c.coord(1);
		int r = (int) rad;
		g.drawOval(x-r, y-r, 2*r, 2*r);
	}
	
	//Draw polygon
	public void draw(Point[] poly) {
		int[] x = new int[poly.length];
		int[] y = new int[poly.length];
		for(int i = 0; i<poly.length; i++) {
			x[i] = (int) poly[i].coord(0);
			y[i] = (int) poly[i].coord(1);
		}
        g.drawPolygon(x, y, poly.length);
	}
	
	/*Painting panel*/
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;
		
		Color t = g.getColor();
		if(!app.isVoronoi())
			g.setColor(delColor);
		else if(delTri.contains(startTri))
			g.setColor(this.getBackground());
		else
			g.setColor(vorColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(t);
		
		if(app.isVoronoi())
			drawVoronoi(true);
		else
			drawDelaunay();
		
		t = g.getColor();
		g.setColor(Color.white);
		if(app.showCircles())
			drawCircles();
		if(app.showVoronoi())
			drawVoronoi(false);
		if(app.showDelaunay())
			drawDelaunay();
		g.setColor(t);
	}
	
	//Draw Voronoi Diagram
	public void drawVoronoi(boolean drawV) {
		HashSet<Point> tri = new HashSet<Point>(startTri);
		for(Triangle tr: delTri) {
			for(Point v: tr) {
				if(tri.contains(v))
					continue;
				List<Triangle> adjTri = delTri.adjTriangles(v, tr);
				Point[] vertices = new Point[adjTri.size()];
				int i = 0;
				for(Triangle t: adjTri)
					vertices[i++] = t.getCircumcenter();
				draw(vertices);
				if (drawV) draw(v);
			}
		}
	}
	
	//Draw Delaunay triangulation
	public void drawDelaunay() {
		for(Triangle tr: delTri) {
			Point[] vertices = tr.toArray(new Point[0]);
			draw(vertices);
		}
	}
	
	//Draw empty circles
	public void drawCircles() {
		for(Triangle tr: delTri) {
			if(tr.containsAny(startTri))
				continue;
			Point c = tr.getCircumcenter();
			double rad = c.subtract(tr.get(0)).magnitude();
			draw(c, rad);
		}
	}
	
}
