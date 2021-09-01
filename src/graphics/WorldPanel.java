package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.HashSet;

import javax.swing.JPanel;

import core.Ship;
import core.Shipyard;
import core.Shot;
import core.Targetable;
import gamecore.Game;
import gamecore.World;
import util.Vector;

public class WorldPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1086189340971491421L;
	
	private static final int PIXEL_BUFFER = 1;

	private World toDraw;
	
	private Vector toDrawFrom;
	private Vector middle;
	private double zoomLevel;
	
	public WorldPanel(World toDraw) {
		this.toDraw = toDraw;
		
		this.setSize(World.WORLD_WIDTH, World.WORLD_HEIGHT);
		this.setLocation(0, 0);
		this.setOpaque(false);
		
		toDrawFrom = new Vector(0,0);
		zoomLevel = 1;
		
	}
	
	public void update(World toDraw) {
		this.toDraw = toDraw;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		Vector preftoDrawFrom = new Vector(0,0);
		
		int yards = 0;
		
		Vector range_max = new Vector(middle.getX(), middle.getY());
		Vector range_min = new Vector(middle.getX(), middle.getY());
		
		for(Shipyard s: toDraw.getShipyards()) {
			preftoDrawFrom = preftoDrawFrom.add(s.getLoc());
			
			Vector loc = s.getLoc();
			
			if(range_max.getX() < loc.getX()) {
				range_max.setX(loc.getX());
			} else if(range_min.getX() > loc.getX()) {
				range_min.setX(loc.getX());
			}
			
			if(range_max.getY() < loc.getY()) {
				range_max.setY(loc.getY());
			} else if(range_min.getY() > loc.getY()) {
				range_min.setY(loc.getY());
			}
			
			yards++;
		}
		
		for(Ship s: toDraw.getShips()) {
			
			Vector loc = s.getLoc();
			
			if(range_max.getX() < loc.getX()) {
				range_max.setX(loc.getX());
			} else if(range_min.getX() > loc.getX()) {
				range_min.setX(loc.getX());
			}
			
			if(range_max.getY() < loc.getY()) {
				range_max.setY(loc.getY());
			} else if(range_min.getY() > loc.getY()) {
				range_min.setY(loc.getY());
			}
		}
		
		Vector radii = new Vector(Shipyard.SHIP_SHIELD_HIT_RADIUS*2, Shipyard.SHIP_SHIELD_HIT_RADIUS*2);
		Vector range = (range_max.add(range_min.mult(-1)));
		range = range.add(radii);
		
		double prefzoomLevel = (middle.getX()/range.getX()) > (middle.getY()/range.getY()) ? (middle.getY()/range.getY()) * 1.5:(middle.getX()/range.getX());
		double delta = prefzoomLevel-zoomLevel;
		
		zoomLevel += delta * Game.DT * 10;
		
		preftoDrawFrom = preftoDrawFrom.mult(1.0/yards);
		
		Vector v_delta = preftoDrawFrom.add(toDrawFrom.mult(-1));
		
		toDrawFrom = toDrawFrom.add(v_delta.mult(Game.DT * 10));
		
		AffineTransform trans = new AffineTransform();
		trans.translate(-toDrawFrom.getX() + middle.getX(), -toDrawFrom.getY() + middle.getY());
		
		g2d.transform(trans);
		
		AffineTransform tx = new AffineTransform();
		
		//tx.translate(toUse.getX(), toUse.getY());
		tx.translate(toDrawFrom.getX(), toDrawFrom.getY());
		tx.scale(zoomLevel, zoomLevel);
		tx.translate((-toDrawFrom.getX()), (-toDrawFrom.getY()));
		//tx.translate(retranslate.getX(), retranslate.getY());
		
		AffineTransform old = g2d.getTransform();
		
		g2d.transform(tx);
		
		drawShipyards(g2d);
		drawShips(g2d);
		drawShots(g2d);
		
		g2d.setTransform(old);
		
		/*g2d.setColor(Color.RED);
		g2d.drawRect((int)(middle.getX() - 5), (int)(middle.getY() - 5), 10, 10);
		g2d.setColor(Color.YELLOW);
		g2d.drawRect((int)(toDrawFrom.getX() - 5), (int)(toDrawFrom.getY() - 5), 10, 10);(*/
		
	}
	
	
	private Color detColor(int maxval, int minval, int val, Color col1, Color col2) {
		float[] color1 = col1.getColorComponents(null);
		float[] color2 = col2.getColorComponents(null);
		
		float[] diff = new float[3];
		diff[0] = (color2[0] - color1[0]);
		diff[1] = (color2[1] - color1[1]);
		diff[2] = (color2[2] - color1[2]);
		
		int valtouse = val - minval;
		int maxvaltouse = maxval - minval;
		
		double diffs = (double)valtouse/maxvaltouse;
		
		diff[0] *= diffs;
		diff[1] *= diffs;
		diff[2] *= diffs;
		
		return new Color(color1[0] + diff[0], color1[1] + diff[1], color1[2] + diff[2]);
		
	}

	@SuppressWarnings("unchecked")
	private void drawShots(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
		for(Shot s: (Iterable<Shot>) toDraw.getShots().clone()) {
			drawShot(s, g2d);
		}
		
		/*HashSet<ShotComponent> toRemove = new HashSet<>();
		
		for(ShotComponent sc: shots) {
			if(sc.removeable()) {
				toRemove.add(sc);
				this.remove(sc);
			} else {
				sc.repaint();
			}
		}
		
		shots.removeAll(toRemove);*/
		
	}

	private void drawShot(Shot s, Graphics2D g2d) {
		// TODO Auto-generated method stub
		
		int shot_length = (int) (s.getVel().mag()/750);
		int shot_width = (int) (25000.0/s.getVel().mag());

		double angle_facing = s.getShotAngle();
			
		Point startLoc = s.getLoc().toPoint();
	
		AffineTransform transform = new AffineTransform();
		//transform.translate((toDrawFrom.getX())/zoomLevel,(toDrawFrom.getY())/zoomLevel);
		transform.rotate(angle_facing, startLoc.x, startLoc.y);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);
			
		if(s.getShotType() == Shot.BULLET) {
			g2d.setColor(Color.RED);
			g2d.drawLine(startLoc.x - shot_length + PIXEL_BUFFER, startLoc.y + PIXEL_BUFFER, startLoc.x + PIXEL_BUFFER, startLoc.y + PIXEL_BUFFER);
		
		} else if(s.getShotType() == Shot.MISSILE){
			g2d.setColor(Color.MAGENTA);
			g2d.drawLine((int)(startLoc.x - shot_length * 1.5) + PIXEL_BUFFER, startLoc.y + PIXEL_BUFFER, startLoc.x + PIXEL_BUFFER, startLoc.y + PIXEL_BUFFER);
		
		} else if(s.getShotType() == Shot.MAC) {
			g2d.setColor(Color.YELLOW);
			g2d.fillOval(startLoc.x - shot_length * 3 + PIXEL_BUFFER, startLoc.y - shot_width/2 + PIXEL_BUFFER, shot_length * 3, shot_width);
		}
		
		g2d.setTransform(old);
	}

	@SuppressWarnings("unchecked")
	private void drawShips(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
		for(Ship s: (Iterable<Ship>) toDraw.getShips().clone()) {
			drawShip(s, g2d);
		}
		
		/*HashSet<ShipComponent> toRemove = new HashSet<>();
		
		for(ShipComponent sc: ships) {
			if(sc.removeable()) {
				toRemove.add(sc);
				this.remove(sc);
			} else {
				sc.repaint();
			}
		}
		
		ships.removeAll(toRemove);*/
		
		
	}

	private void drawShip(Ship s, Graphics2D g2d) {
		// TODO Auto-generated method stub
		double angle_facing = s.getAngleFacing();
		
		Vector toSub = new Vector(-1 * Ship.SHIP_SHIELD_HIT_RADIUS, -1 * Ship.SHIP_SHIELD_HIT_RADIUS);
		
		//this.setLocation(s.getLoc().add(toSub).toPoint());
		
		Point startLoc = s.getLoc().add(toSub).toPoint();
		
		AffineTransform transform = new AffineTransform();
		//transform.translate((toDrawFrom.getX())/zoomLevel,(toDrawFrom.getY())/zoomLevel);
		transform.rotate(angle_facing, startLoc.x + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, startLoc.y +  Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);
		
		if(s.isShieldsup()) {
			g2d.setColor(this.detColor(Ship.SHIELDS, 0, s.getShields(), Color.RED, Color.CYAN));
			g2d.drawOval(startLoc.x + PIXEL_BUFFER, 
					startLoc.y + PIXEL_BUFFER, 
					Ship.SHIP_SHIELD_HIT_RADIUS * 2, 
					Ship.SHIP_SHIELD_HIT_RADIUS * 2);
		}
		
		g2d.setColor(s.getColor());
		g2d.drawOval(startLoc.x + Ship.SHIP_SHIELD_HIT_RADIUS - (int)(0.9 * Ship.SHIP_SHIELD_HIT_RADIUS) + PIXEL_BUFFER,
				startLoc.y + Ship.SHIP_SHIELD_HIT_RADIUS - Ship.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER, 
				(int)(0.9 * Ship.SHIP_SHIELD_HIT_RADIUS * 2), 
				Ship.SHIP_BODY_HIT_RADIUS * 2);
		
		g2d.setColor(Color.YELLOW);
		g2d.drawLine(startLoc.x + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, 
				startLoc.y + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, 
				startLoc.x + Ship.SHIP_BODY_HIT_RADIUS + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, 
				startLoc.y + Ship.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER);
		
		if(s.isThrusting()){
			g2d.setColor(Color.ORANGE);
			g2d.drawOval(startLoc.x - 5, startLoc.y + Ship.SHIP_SHIELD_HIT_RADIUS - 3, 8, 4);
		}
		
		g2d.setTransform(old);
	}

	@SuppressWarnings("unchecked")
	private void drawShipyards(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
		for(Shipyard s: (Iterable<Shipyard>) toDraw.getShipyards().clone()) {
			drawShipyard(s, g2d);
		}
		
		
		/*HashSet<ShipyardComponent> toRemove = new HashSet<>();
		
		for(ShipyardComponent sc: shipyards) {
			if(sc.removeable()) {
				toRemove.add(sc);
				this.remove(sc);
			} else {
				sc.repaint();
			}
		}
		
		shipyards.removeAll(toRemove);*/
		
	}

	private void drawShipyard(Shipyard s, Graphics2D g2d) {
		// TODO Auto-generated method stub
		double angle_facing = s.getAngleFacing();
		
		Vector toSub = new Vector(-1 * Shipyard.SHIP_SHIELD_HIT_RADIUS, -1 * Shipyard.SHIP_SHIELD_HIT_RADIUS);
		
		//this.setLocation(s.getLoc().add(toSub).toPoint());
		
		Point startLoc = s.getLoc().add(toSub).toPoint();
		
		AffineTransform transform = new AffineTransform();
		//transform.translate((toDrawFrom.getX())/zoomLevel,(toDrawFrom.getY())/zoomLevel);
		transform.rotate(angle_facing, startLoc.x + Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, startLoc.y + Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);
		
		
		if(s.isShieldsup()) {
			g2d.setColor(this.detColor(Shipyard.SHIELDS, 0, s.getShields(), Color.RED, Color.CYAN));
			g2d.drawOval(startLoc.x + PIXEL_BUFFER, 
					startLoc.y + PIXEL_BUFFER, 
					Shipyard.SHIP_SHIELD_HIT_RADIUS * 2, 
					Shipyard.SHIP_SHIELD_HIT_RADIUS * 2);
		}
		
		g2d.setColor(this.detColor(Shipyard.HULL, 0, s.getHealth(), Color.GRAY, s.getColor()));
		g2d.drawOval(startLoc.x + Shipyard.SHIP_SHIELD_HIT_RADIUS - Shipyard.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER, 
				startLoc.y + Shipyard.SHIP_SHIELD_HIT_RADIUS - Shipyard.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER, 
				Shipyard.SHIP_BODY_HIT_RADIUS * 2, 
				Shipyard.SHIP_BODY_HIT_RADIUS * 2);
		
		g2d.drawOval(startLoc.x + Shipyard.SHIP_SHIELD_HIT_RADIUS - Shipyard.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER - 2, 
				startLoc.y + Shipyard.SHIP_SHIELD_HIT_RADIUS - Shipyard.SHIP_BODY_HIT_RADIUS + PIXEL_BUFFER - 2, 
				Shipyard.SHIP_BODY_HIT_RADIUS * 2 + 4, 
				Shipyard.SHIP_BODY_HIT_RADIUS * 2 + 4);
		
		
		g2d.setColor(Color.YELLOW);
		g2d.drawLine(startLoc.x + Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, 
				startLoc.y + Shipyard.SHIP_SHIELD_HIT_RADIUS + PIXEL_BUFFER, 
				startLoc.x + Shipyard.SHIP_BODY_HIT_RADIUS + Shipyard.SHIP_SHIELD_HIT_RADIUS, 
				startLoc.y + Shipyard.SHIP_SHIELD_HIT_RADIUS);
		
		g2d.setTransform(old);

	}

	/**
	 * @return the toDrawFrom
	 */
	public Vector getToDrawFrom() {
		return toDrawFrom;
	}

	/**
	 * @param toDrawFrom the toDrawFrom to set
	 */
	public void setToDrawFrom(Vector toDrawFrom) {
		this.toDrawFrom = toDrawFrom;
	}

	/**
	 * @return the zoomLevel
	 */
	public double getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * @param zoomLevel the zoomLevel to set
	 */
	public void setZoomLevel(double zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	/**
	 * @return the middle
	 */
	public Vector getMiddle() {
		return middle;
	}

	/**
	 * @param middle the middle to set
	 */
	public void setMiddle(Vector middle) {
		this.middle = middle;
	}
	
	
	
	
}
