package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import util.Vector;

import javax.swing.JComponent;

import core.Ship;
import core.Shipyard;

public class ShipComponent extends JComponent{
	
	/**
	 * 
	 */
	
	private static final int PIXEL_BUFFER = 1;
	
	private static final long serialVersionUID = 2617705674723534830L;
	private Ship toDraw;
	
	public ShipComponent(Ship toDraw) {
		this.toDraw = toDraw;
		
		Vector toSub = new Vector(-1 * Ship.SHIP_SHIELD_HIT_RADIUS, -1 * Ship.SHIP_SHIELD_HIT_RADIUS);
		
		this.setLocation(toDraw.getLoc().add(toSub).toPoint());
		this.setSize(Ship.SHIP_SHIELD_HIT_RADIUS * 2 + PIXEL_BUFFER * 2, Ship.SHIP_SHIELD_HIT_RADIUS * 2 + PIXEL_BUFFER * 2);
		this.setOpaque(false);
		this.setVisible(true);
		
	}
	
	public void update(Ship toDraw) {
		this.toDraw = toDraw;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		double angle_facing = toDraw.getAngleFacing();
		
		Vector toSub = new Vector(-1 * Ship.SHIP_SHIELD_HIT_RADIUS, -1 * Ship.SHIP_SHIELD_HIT_RADIUS);
		
		this.setLocation(toDraw.getLoc().add(toSub).toPoint());
		
		AffineTransform transform = new AffineTransform();
		transform.rotate(angle_facing, Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);
		
		if(toDraw.isShieldsup()) {
			g2d.setColor(Color.CYAN);
			g2d.drawOval(PIXEL_BUFFER, PIXEL_BUFFER, Ship.SHIP_SHIELD_HIT_RADIUS * 2, Ship.SHIP_SHIELD_HIT_RADIUS * 2);
		}
		
		g2d.setColor(Color.RED);
		g2d.drawOval(Ship.SHIP_SHIELD_HIT_RADIUS - (int)(0.75 * Ship.SHIP_SHIELD_HIT_RADIUS) + PIXEL_BUFFER,Ship.SHIP_SHIELD_HIT_RADIUS - Ship.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER, (int)(0.75 * Ship.SHIP_SHIELD_HIT_RADIUS * 2), Ship.SHIP_BODY_HIT_RADIUS * 2);
		
		g2d.setColor(Color.YELLOW);
		g2d.drawLine(Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, Ship.SHIP_BODY_HIT_RADIUS + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, 0 + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER);
		
		if(toDraw.isThrusting()){
			g2d.setColor(Color.ORANGE);
			g2d.drawOval(0, Ship.SHIP_SHIELD_HIT_RADIUS, 5, 5);
		}
		
		g2d.setTransform(old);
		
	}
	
	public boolean removeable() {
		return toDraw.exists();
	}
	
	public boolean equals(Object other) {
		
		if(other instanceof ShipComponent) {
			return ((ShipComponent) other).toDraw.equals(this.toDraw);
		}
		
		return false;
		
	}
	
	
}
