package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import core.Ship;
import core.Shipyard;
import core.Shot;
import util.Vector;

public class ShotComponent extends JComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7172352096507709695L;
	public static final int SHOT_WIDTH = 5;
	public static final int SHOT_HEIGHT = 5;
	
	private Shot toDraw;
	
	public ShotComponent(Shot toDraw) {
		this.toDraw = toDraw;
		
		this.setOpaque(false);
		this.setLocation(toDraw.getLoc().toPoint());
		this.setSize(SHOT_WIDTH, SHOT_HEIGHT);
		this.setVisible(true);
		
	}
	
	public void update(Shot toDraw) {
		this.toDraw = toDraw;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		this.setLocation(toDraw.getLoc().toPoint());
		
		double angle_facing = toDraw.getShotAngle();
		
		AffineTransform transform = new AffineTransform();
		transform.rotate(angle_facing, 0, 0);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);
		
		g2d.setColor(Color.YELLOW);
		g2d.drawLine(0, 0, SHOT_WIDTH, 0);
		
		
		
		
	}
	
	public boolean removeable() {
		return toDraw.exists();
	}
	
	public boolean equals(Object other) {
		
		if(other instanceof ShotComponent) {
			return ((ShotComponent) other).toDraw.equals(this.toDraw);
		}
		
		return false;
		
	}
}
