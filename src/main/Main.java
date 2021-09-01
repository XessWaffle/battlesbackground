package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;

import gamecore.Game;
import gamecore.World;
import graphics.WorldPanel;
import util.Vector;

public class Main {

	private JFrame frame;
	private Game world;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		frame = new JFrame();
		frame.setBounds(0, 0, (int)dim.getWidth(), (int)dim.getHeight());
		frame.setUndecorated(true);
		frame.setOpacity(0.8f);
		frame.setBackground(new Color(0,0,0,0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		world = new Game();
		
		world.world_panel.setBounds(0,0, World.WORLD_WIDTH, World.WORLD_HEIGHT);
		world.world_panel.setVisible(true);
		frame.add(world.world_panel);
		
		WorldZoomPan wzp = new WorldZoomPan();
		
		world.world_panel.addMouseListener(wzp);
		world.world_panel.addMouseMotionListener(wzp);
		world.world_panel.addMouseWheelListener(wzp);
		
		Thread game = new Thread() {
			public void run() {
				while(true) {
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					world.step();
					//wp.update(world.getCurrentWorld());
					world.world_panel.repaint();
					
					world.world_panel.setMiddle(new Vector(frame.getWidth()/2, frame.getHeight()/2));
				}
			}
		};
		
		game.start();
		
	}

}

class WorldZoomPan extends MouseAdapter{
	
	private Vector current_location;
	private Vector mouse_location;
	private double zoomLevel;
	
	public WorldZoomPan() {
		current_location = new Vector(0,0);
		mouse_location = new Vector(0, 0);
		
		zoomLevel = 1;
	}
	
	public void mousePressed(MouseEvent e) {
		mouse_location = new Vector(e.getX(), e.getY());
	}
	
	public void mouseDragged(MouseEvent e) {		
	
		Vector start = new Vector(e.getX(), e.getY());
		Vector to_add = (mouse_location).add(start.mult(-1));
		to_add = to_add.mult(-0.01/zoomLevel);
		
		current_location = current_location.add(to_add);
		
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		double oldZoom = getZoom();
        double amount = Math.pow(1.1, e.getScrollAmount());
        if (e.getWheelRotation() > 0) {
            //zoom in (amount)
            setZoom(oldZoom * amount);
        } else {
            //zoom out (amount)
            setZoom(oldZoom / amount);
        }
	}
	
	public double getZoom() {
		return zoomLevel;
	}
	
	public void setZoom(double zoom) {
		this.zoomLevel = zoom;
	}
	
	public Vector getCurrentLocation() {
		return current_location;
	}
	
}
