package MainApp;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;

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
		//delCanvas.addPoint(point);
		delCanvas.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == clear)
			//delCanvas.clear();
		delCanvas.repaint();
		
	}
	
	/**
     * @return true iff doing Voronoi diagram.
     */
    public boolean isVoronoi() {
        return vorButton.isSelected();
    }

    /**
     * @return true iff within circle switch
     */
    public boolean showCircles() {
        return mouseIn == circleLab;
    }

    /**
     * @return true iff within delaunay switch
     */
    public boolean showDelaunay() {
        return mouseIn == delLab;
    }

    /**
     * @return true iff within voronoi switch
     */
    public boolean showVoronoi() {
        return mouseIn == vorLab;
    }
}
	
class DelPanel extends JPanel {
		
	private Vor_Del_App app;
	
	public DelPanel(Vor_Del_App app) {
		this.app = app;
	}
	
}
