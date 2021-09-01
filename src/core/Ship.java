package core;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import gamecore.Game;
import gamecore.World;
import util.Order;
import util.Vector;

public class Ship implements Targetable{
	
	public static final double THRUST_MAG = 1000;
	public static final double TURNING_SPEED = 100;
	
	public static final double MAX_VEL = 500;
	public static final double VELOCITY_DECAY_MAG = 50;
	
	public static final int SHOT_TICK_DELAY = 10;
	public static final int MISSILE_TICK_DELAY = 75;
	public static final int SHIP_SHIELD_HIT_RADIUS = 10;
	public static final int SHIP_BODY_HIT_RADIUS = 7;
	public static final int SHIELDS = 10000;
	public static final int HULL = 7500;
	public static final double RANGE = 0.07;
	
	public static final int TICKS_TO_PREDICT = 5;
	
	private Vector loc, vel, acc;
	
	private boolean thrusting;
	private boolean firing;
	private boolean destroyed;
	private boolean shieldsup;
	
	private double angle_facing;
	private Vector facing;
	private Queue<Order> orders;
	private Order current_order;
	
	private Targetable target, attacking;
	private World in;
	private ShipAI ai;
	
	private int health;
	private int shields;
	private int shot_type;
	
	private int team;
	private String id;
	
	private int spawn_time;
	
	private Color color;
	
	public Ship(Vector loc, World in, double angle) {
		
		thrusting = false;
		firing = false;
		destroyed = false;
		shieldsup = true;
		
		spawn_time = in.getTicks();
		
		this.setLoc(loc);
		this.setAngleFacing(angle);
		facing = new Vector(Math.cos(angle_facing), Math.sin(angle_facing));
		
		vel = new Vector(0,0);
		acc = new Vector(0,0);
		
		orders = new LinkedList<Order>();
		
		this.health = HULL;
		this.shields = SHIELDS;
		
		this.in = in;
		
		ai = new ShipAI(this);
		
		shot_type = (int)(Math.random() * Shot.SHOT_TYPES);
		
	}
	
	public void step() {
		
		int damage = in.getDamage(this);
		
		if(shieldsup) {
			if(shields < damage) {
				damage -= shields;
				shields = 0;
				health -= damage;
			} else {
				shields -= damage;
			}
		} else {
			health -= damage;
		}
		
		if(getShields() > 0) {
			shieldsup = true;
		} else {
			shieldsup = false;
		}
		
		if(getHealth() <= 0) {
			this.setDestroyed(true);
			in.remove(this);
		}
		
		if(!this.isDestroyed())
			ai.step();
		
		current_order = orders.poll();
		
		if(current_order != null) {
			target = current_order.getTarget();
			attacking = current_order.getTarget();
		} else {
			current_order = new Order(Order.DO_NOTHING, null);
		}
		
		
		if(current_order.getOrder() == Order.ATTACK) {
			Vector to_face = target.getLoc(TICKS_TO_PREDICT).add(loc.mult(-1));
			double angleToFace = Math.atan2(to_face.getY(), to_face.getX());
			
			if(ShipAI.isWithin(angleToFace, angle_facing, RANGE)) {
				fire();
			} else {
				stopFiring();
			}
			
			turnTo(angleToFace);
			
			
		} else if(current_order.getOrder() == Order.DEFEND) {
			
			if(target instanceof Shipyard) {
				Shipyard tgt = (Shipyard) target;
				
				HashSet<Targetable> hits = in.getHits(tgt);
				HashSet<Targetable> hitsthis = in.getHits(this);
				
				attacking = null;
				
				if(hitsthis.isEmpty()) {
					for(Targetable hit: hits) {
						attacking = hit;
						if(attacking.exists()) break;
					}
				} else {
					for(Targetable hit: hitsthis) {
						attacking = hit;
						if(attacking.exists()) break;
					}
				}
					
				if(attacking != null && attacking.exists()) {
					Vector to_face = attacking.getLoc(TICKS_TO_PREDICT).add(loc.mult(-1));
					double angleToFace = Math.atan2(to_face.getY(), to_face.getX());
					
					if(ShipAI.isWithin(angleToFace, angle_facing, RANGE)) {
						fire();
					} else {
						stopFiring();
					}
					
					turnTo(angleToFace);
				} else {
					stopFiring();
				}
				
			}
			
		} else if(current_order.getOrder() == Order.HOLD_POSITION) {
	
			double angleToFace = Math.atan2(-vel.getY(), -vel.getX());
			
			stopFiring();
			
			if(ShipAI.isWithin(angleToFace, angle_facing, RANGE)) {
				thrust();
			} else {
				stopThrusting();
			}
			
			turnTo(angleToFace);
			
			
		} else if(current_order.getOrder() == Order.MOVE_TO) {
			Vector to_face = target.getLoc(TICKS_TO_PREDICT).add(loc.mult(-1));
			double angleToFace = Math.atan2(to_face.getY(), to_face.getX());
			
			stopFiring();
			
			if(ShipAI.isWithin(angleToFace, angle_facing, RANGE)) {
				thrust();
			} else {
				stopThrusting();
			}
			
			turnTo(angleToFace);
		} else {
			double angleToFace = Math.atan2(-vel.getY(), -vel.getX());
			
			stopFiring();
			
			if(ShipAI.isWithin(angleToFace, angle_facing, RANGE)) {
				thrust();
			} else {
				stopThrusting();
			}
			
			turnTo(angleToFace);
		}
		
		if(isThrusting()) {
			acc = (facing.mult(THRUST_MAG));
		} else {
			acc = new Vector(0,0);
		}
		
		if(isFiring() && ((in.getTicks() - spawn_time) % SHOT_TICK_DELAY * (shot_type) * 10) == 0) {
			Shot fired = new Shot(this, this.getAngleFacing(), loc, vel, in, shot_type);
			fired.setTarget(attacking);
			in.add(fired);
			
		}
	
		Vector decay = vel.mult(-1);
		decay = decay.mult(VELOCITY_DECAY_MAG);
		decay = decay.mult(Game.DT);
		
		if(vel.mag() < MAX_VEL) {
			vel = vel.add(acc.mult(Game.DT));
		} else {
			vel = vel.add(decay);
		}
		
		loc = loc.add(vel.mult(Game.DT));
	}
	
	public void fire() {
		firing = true;
	}
	
	public void stopFiring() {
		firing = false;
	}
	
	public void thrust() {
		thrusting = true;
	}
	
	public void stopThrusting() {
		thrusting = false;
	}
	
	public void turnTo(double angle) {
		
		double angle_delta = angle_facing - angle;
		
		angle_facing -= angle_delta * Game.DT * TURNING_SPEED;
		facing = new Vector(Math.cos(angle_facing), Math.sin(angle_facing));
	}
	
	/**
	 * @return the loc
	 */
	public Vector getLoc() {
		return loc;
	}

	/**
	 * @param loc the loc to set
	 */
	public void setLoc(Vector loc) {
		this.loc = loc;
	}

	/**
	 * @return the vel
	 */
	public Vector getVel() {
		return vel;
	}

	/**
	 * @param vel the vel to set
	 */
	public void setVel(Vector vel) {
		this.vel = vel;
	}

	/**
	 * @return the acc
	 */
	public Vector getAcc() {
		return acc;
	}

	/**
	 * @param acc the acc to set
	 */
	public void setAcc(Vector acc) {
		this.acc = acc;
	}

	/**
	 * @return the target
	 */
	public Targetable getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Ship target) {
		this.target = target;
	}

	/**
	 * @return the thrusting
	 */
	public boolean isThrusting() {
		return thrusting;
	}

	/**
	 * @return the firing
	 */
	public boolean isFiring() {
		return firing;
	}

	/**
	 * @return the facing
	 */
	public Vector getFacing() {
		return facing;
	}

	/**
	 * @param facing the facing to set
	 */
	public void setFacing(Vector facing) {
		this.facing = facing;
	}

	/**
	 * @return the angle_facing
	 */
	public double getAngleFacing() {
		return angle_facing;
	}

	/**
	 * @param angle_facing the angle_facing to set
	 */
	public void setAngleFacing(double angle_facing) {
		this.angle_facing = angle_facing;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the shields
	 */
	public int getShields() {
		return shields;
	}

	/**
	 * @param shields the shields to set
	 */
	public void setShields(int shields) {
		this.shields = shields;
	}

	/**
	 * @return the destroyed
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * @param destroyed the destroyed to set
	 */
	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	/**
	 * @return the team
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(int team) {
		this.team = team;
	}

	/**
	 * @return the shieldsup
	 */
	public boolean isShieldsup() {
		return shieldsup;
	}

	/**
	 * @param shieldsup the shieldsup to set
	 */
	public void setShieldsup(boolean shieldsup) {
		this.shieldsup = shieldsup;
	}
	
	public void setId(String string) {
		// TODO Auto-generated method stub
		this.id = string;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Ship) {
			Ship o_ship = (Ship) other;		
			if(o_ship.getTeam() == this.getTeam() && this.id.equals(o_ship.id)) return true;
		}
		
		return false;
		
	}

	/**
	 * @return the current_order
	 */
	public Order getOrder() {
		return current_order;
	}

	/**
	 * @param current_order the current_order to set
	 */
	public void setOrder(Order current_order) {
		this.current_order = current_order;
	}

	public void queueOrder(Order enq) {
		orders.add(enq);
	}
	
	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return in.contains(this);
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	public World getWorld() {
		// TODO Auto-generated method stub
		return in;
	}

	@Override
	public Vector getLoc(int ticks) {
		// TODO Auto-generated method stub
		Vector veldt = vel.mult(ticks * Game.DT);
		return loc.add(veldt);
	}

	@Override
	public Vector getVel(int ticks) {
		// TODO Auto-generated method stub
		
		Vector accdt = acc.mult(ticks * Game.DT);
		return vel.add(accdt);
	}
}
