package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import core.Ship;
import core.Shipyard;
import util.Vector;

public class ShipyardComponent extends JComponent{
	/**
	 * 
	 */
	
	public static final int PIXEL_BUFFER = 1;
	
	private static final long serialVersionUID = -8464602144849619521L;
	private Shipyard toDraw;
	
	public ShipyardComponent(Shipyard toDraw) {
		this.toDraw = toDraw;
		
		Vector toSub = new Vector(-1 * Shipyard.SHIP_SHIELD_HIT_RADIUS, -1 * Shipyard.SHIP_SHIELD_HIT_RADIUS);
		
		this.setLocation(toDraw.getLoc().add(toSub).toPoint());
		this.setOpaque(false);
		this.setSize(Shipyard.SHIP_SHIELD_HIT_RADIUS * 2 + PIXEL_BUFFER * 2, Shipyard.SHIP_SHIELD_HIT_RADIUS * 2 + PIXEL_BUFFER * 2);
		this.setVisible(true);
		
	}
	
	public void update(Shipyard toDraw) {
		this.toDraw = toDraw;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		double angle_facing = toDraw.getAngleFacing();
		
		Vector toSub = new Vector(-1 * Shipyard.SHIP_SHIELD_HIT_RADIUS, -1 * Shipyard.SHIP_SHIELD_HIT_RADIUS);
		
		this.setLocation(toDraw.getLoc().add(toSub).toPoint());
		
		AffineTransform transform = new AffineTransform();
		transform.rotate(angle_facing, Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);
		
		
		if(toDraw.isShieldsup()) {
			g2d.setColor(Color.CYAN);
			g2d.drawOval(PIXEL_BUFFER, PIXEL_BUFFER, Shipyard.SHIP_SHIELD_HIT_RADIUS * 2, Shipyard.SHIP_SHIELD_HIT_RADIUS * 2);
		}
		
		g2d.setColor(Color.BLUE);
		g2d.drawOval(Shipyard.SHIP_SHIELD_HIT_RADIUS - Shipyard.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER, Shipyard.SHIP_SHIELD_HIT_RADIUS - Shipyard.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER, Shipyard.SHIP_BODY_HIT_RADIUS * 2, Shipyard.SHIP_BODY_HIT_RADIUS * 2);
		
		g2d.setColor(Color.YELLOW);
		g2d.drawLine(Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, Shipyard.SHIP_BODY_HIT_RADIUS + Shipyard.SHIP_SHIELD_HIT_RADIUS, Shipyard.SHIP_SHIELD_HIT_RADIUS);
		
		g2d.setTransform(old);

	}
	
	public boolean removeable() {
		return toDraw.exists();
	}
	
	public boolean equals(Object other) {
		
		if(other instanceof ShipyardComponent) {
			return ((ShipyardComponent) other).toDraw.equals(this.toDraw);
		}
		
		return false;
		
	}
	
}
