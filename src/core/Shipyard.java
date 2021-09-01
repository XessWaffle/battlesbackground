package core;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import gamecore.Game;
import gamecore.World;
import util.Order;
import util.Vector;

public class Shipyard implements Targetable{
	public static final int SHIELDS = 100000;
	public static final int HULL = 400000;
	public static final int SPAWN_RATE = 1000;
	public static final int SPAWN_RADIUS = 100;
	public static final int SHIP_SHIELD_HIT_RADIUS = 30;
	public static final int SHIP_BODY_HIT_RADIUS = 15;
	
	public static final int MAC_FIRE_DELAY = 1;
	public static final int TICKS_TO_PREDICT = 8;
	
	
	private int ships_spawned = 0;
	
	private Vector loc;
	
	private HashSet<Ship> ships;
	
	private boolean destroyed;
	private boolean shieldsup;
	
	private double angle_facing;
	private Vector facing;
	private Queue<Order> orders;
	private Order current_order;
	
	private Targetable target;
	private Targetable attacking;
	
	private ShipyardAI ai;
	
	private World in;
	private int health;
	private int shields;
	
	private int team;
	private String id;
	
	private boolean firing = false;
	
	private int spawn_time;
	
	private Color color;
	
	public Shipyard(Vector loc, World in, int team, double angle_facing) {
		this.setDestroyed(false);
		this.setShieldsup(true);
		
		this.loc = loc;
		this.angle_facing = angle_facing;
		this.in = in;
		
		ships = new HashSet<>();
		
		orders = new LinkedList<Order>();
		
		spawn_time = in.getTicks();
		
		id = "Shipyard " + team;
		
		this.team = team;
		this.in = in;
		
		this.health = HULL;
		this.shields = SHIELDS;
		
		ai = new ShipyardAI(this);	
	}
	
	public void step() {
		
		if((in.getTicks() - spawn_time) % SPAWN_RATE == 0 || Math.random() > 0.997) {
			spawn();
		}
		
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
		
		if(this.exists()) ai.step();
		
		current_order = orders.poll();
		
		if(current_order != null) {
			setTarget(current_order.getTarget());
			setAttacking(current_order.getTarget());
		} else {
			current_order = new Order(Order.DO_NOTHING, null);
		}	
		
		double shot_angle = this.getAngleFacing();
		
		if(current_order.getOrder() == Order.ATTACK) {
			Vector angle = attacking.getLoc(TICKS_TO_PREDICT).add(this.getLoc().mult(-1));
			shot_angle = Math.atan2(angle.getY(), angle.getX());
			
			if(attacking.exists()) {
				fire();
			} else {
				stopFiring();
			}
			
		} else if(current_order.getOrder() == Order.DEFEND) {
			HashSet<Targetable> hits = in.getHits(this);
				
			attacking = null;
			
			for(Targetable hit: hits) {
				attacking = hit;
				if(hit.exists()) break;
			}
			
			if(attacking != null && attacking.exists()) {
				
				Vector angle = attacking.getLoc(TICKS_TO_PREDICT).add(this.getLoc().mult(-1));
				shot_angle = Math.atan2(angle.getY(), angle.getX());
				
				fire();
			} else {	
				stopFiring();
			}
		} 
		
		if(isFiring() && (in.getTicks() - spawn_time) % MAC_FIRE_DELAY == 0) {
			Shot mac = new Shot(this, shot_angle, this.getLoc(), this.getVel(), this.getWorld(), Shot.MAC);
			mac.setTarget(attacking);
			in.add(mac);
		}
		
		
		if(!this.isDestroyed()) {
		
			HashSet<Ship> toRemove = new HashSet<>();
			
			for(Ship ship: ships) {
				
				if(ship.isDestroyed()) {
					toRemove.add(ship);
				}
			}
		
			ships.removeAll(toRemove);	
		}
		
	
	}
	
	private boolean isFiring() {
		// TODO Auto-generated method stub
		return firing;
	}

	public void spawn() {
		
		double angle = (Math.random() - 0.5) * 4 * Math.PI;
		double radius = (Math.random()) * SPAWN_RADIUS;
		
		Vector disp = new Vector(radius * Math.cos(angle), radius * Math.sin(angle));
		
		Vector spawn_loc = disp.add(loc);
		
		Ship spawn = new Ship(spawn_loc, in, angle_facing);
		spawn.setTeam(team);
		spawn.setId("Ship " + team + " " + ships_spawned);
		spawn.setColor(this.getColor());
		
		ships.add(spawn);
		in.add(spawn);
		
		ships_spawned++;
		
	}
	
	public void fire() {
		firing = true;
	}
	
	public void stopFiring() {
		firing = false;
	}
	
	public void queueOrder(Order enq) {
		this.orders.add(enq);
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
	
	public World getWorld() {
		return in;
	}
	
	public void setId(String string) {
		// TODO Auto-generated method stub
		this.id = string;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Shipyard) {
			Shipyard o_ship = (Shipyard) other;		
			if(o_ship.getTeam() == this.getTeam() && this.id.equals(o_ship.id)) return true;
		}
		
		return false;
		
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return in.contains(this);
	}

	public HashSet<Ship> getShips() {
		// TODO Auto-generated method stub
		return ships;
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

	@Override
	public Vector getVel() {
		// TODO Auto-generated method stub
		return new Vector(0,0);
	}

	@Override
	public Vector getAcc() {
		// TODO Auto-generated method stub
		return new Vector(0,0);
	}

	/**
	 * @return the current_order
	 */
	public Order getCurrentOrder() {
		return current_order;
	}

	/**
	 * @param current_order the current_order to set
	 */
	public void setCurrentOrder(Order current_order) {
		this.current_order = current_order;
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
	public void setTarget(Targetable target) {
		this.target = target;
	}

	/**
	 * @return the attacking
	 */
	public Targetable getAttacking() {
		return attacking;
	}

	/**
	 * @param attacking the attacking to set
	 */
	public void setAttacking(Targetable attacking) {
		this.attacking = attacking;
	}
	
	@Override
	public Vector getLoc(int ticks) {
		// TODO Auto-generated method stub
		return this.getLoc();
	}

	@Override
	public Vector getVel(int ticks) {
		// TODO Auto-generated method stub
		return this.getVel();
	}
	
}
