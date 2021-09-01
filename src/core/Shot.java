package core;

import gamecore.Game;
import gamecore.World;
import util.Vector;

public class Shot implements Targetable{
	
	public static final int SHOT_VELOCITY = 10;
	public static final int SHOT_ACCELERATION = 200000;
	public static final int SHOT_DAMAGE = 1000;
	public static final double BULLET_SPREAD = 25;
	
	public static final int SHOT_TYPES = 2;
	public static final int BULLET = 0;
	public static final int MISSILE = 1;
	
	public static final int SHIPYARD_SHOT_TYPES = -1;
	public static final int MAC = -1;
	
	public static final int BULLET_LIFE = 75;
	public static final int MISSILE_LIFE = 250;
	public static final int MAC_LIFE = 350;
	
	
	private Vector loc, vel, acc;
	private Targetable fired_by;	
	private World in;
	
	private Targetable target;
	
	private double shot_angle;
	private double damage;
	private boolean hit;
	
	private String id;
	
	private int spawn_time;
	private int shot_type;
	
	private int team = -1;
	private int shot_life;
	
	public Shot(Targetable fired_by, double angle, Vector loc, Vector vel, World in, int shot_type) {
		this.setLoc(loc);
		this.setDamage(SHOT_DAMAGE/5);
		this.setFiringTargetable(fired_by);
		
		spawn_time = in.getTicks();
		
		this.shot_angle = angle;
		
		this.shot_angle += (Math.random() - 0.5)/BULLET_SPREAD;
		
		this.shot_type = shot_type;
		
		Vector unit = new Vector(Math.cos(shot_angle), Math.sin(shot_angle));
		
		int velocity = (int) (SHOT_VELOCITY * (Math.random() * 100));
		
		Vector perp = new Vector(-1 * unit.getY(), unit.getX());
			
		this.setAcc(unit.mult(SHOT_ACCELERATION));
		this.setId("Shot: " + in.getTicks());
		this.in = in;
		
		if(this.shot_type == BULLET) {
			
			this.setVel(vel.add(unit.mult(velocity)));
			
		} else if(this.shot_type == MAC) {
			
			unit = new Vector(Math.cos(angle), Math.sin(angle));
			
			this.setVel(vel.add(unit.mult(velocity)));
			this.setAcc(unit.mult(SHOT_ACCELERATION * 1.5));
			this.setDamage(SHOT_DAMAGE/2);
			
		} else if(this.shot_type == MISSILE){
			
			this.setVel(Math.random() > 0.5 ? vel.add(perp.mult(SHOT_VELOCITY * 500)) : vel.add(perp.mult(-SHOT_VELOCITY * 500)));
			this.setDamage(SHOT_DAMAGE);
			
		}
		
	}
	
	public void step() {
		
		if(shot_type == Shot.MISSILE) {
			
			shot_life = MISSILE_LIFE;
			
			Vector prev_vel = new Vector(vel.getX(), vel.getY());
			
			Vector to_target = target.getLoc().add(this.getLoc().mult(-1));
			double target_angle = Math.atan2(to_target.getY(), to_target.getX());
			shot_angle = Math.atan2(vel.getY(), vel.getX());
			
			Vector n_acc = (new Vector(Math.cos(target_angle), Math.sin(target_angle))).add(new Vector(-Math.cos(shot_angle),-Math.sin(shot_angle)));
			
			double acc_angle = Math.atan2(n_acc.getY(), n_acc.getX());
			
			Vector unit = new Vector(Math.cos(acc_angle), Math.sin(acc_angle));
			
			unit = unit.add(new Vector(Math.cos(target_angle), Math.sin(target_angle)));
			
			this.setAcc(unit.mult(SHOT_ACCELERATION));
			
			if(!target.exists()) {
				this.setVel(prev_vel);
				this.setAcc(new Vector(0,0));
			}
			
			if(hit || in.getTicks() - spawn_time >= MISSILE_LIFE) {
				damage = 0;
				in.remove(this);
			}
			
		} else if(this.shot_type == BULLET){
			
			shot_life = BULLET_LIFE;
			
			if(hit || in.getTicks() - spawn_time >= BULLET_LIFE) {
				damage = 0;
				in.remove(this);
			}
		} else if(this.shot_type == MAC) {
			
			shot_life = MAC_LIFE;
			
			if(hit || in.getTicks() - spawn_time >= MAC_LIFE) {
				damage = 0;
				in.remove(this);
			}
			
			/*if((in.getTicks() - spawn_time) % MAC_LIFE/50 == 0) {
				Shot fired = new Shot(fired_by, fired_by.getAngleFacing(), this.getLoc(), this.getVel(), in, MISSILE);
				fired.setTarget(target);
				in.add(fired);
				
				fired = new Shot(fired_by, fired_by.getAngleFacing(), this.getLoc(), this.getVel(), in, MISSILE);
				fired.setTarget(target);
				in.add(fired);
				
				fired = new Shot(fired_by, fired_by.getAngleFacing(), this.getLoc(), this.getVel(), in, MISSILE);
				fired.setTarget(target);
				in.add(fired);
				
				fired = new Shot(fired_by, fired_by.getAngleFacing(), this.getLoc(), this.getVel(), in, MISSILE);
				fired.setTarget(target);
				in.add(fired);
			}*/
		}
		
		vel = vel.add(acc.mult(Game.DT));
		loc = loc.add(vel.mult(Game.DT));
		
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
	 * @return the damage
	 */
	public double getDamage() {
		return damage;
	}

	/**
	 * @param damage the damage to set
	 */
	public void setDamage(double damage) {
		this.damage = damage;
	}

	/**
	 * @return the hit
	 */
	public boolean isHit() {
		return hit;
	}

	/**
	 * @param hit the hit to set
	 */
	public void setHit(boolean hit) {
		this.hit = hit;
	}

	/**
	 * @return the fired_by
	 */
	public Targetable getFiringTargetable() {
		return fired_by;
	}

	/**
	 * @param fired_by the fired_by to set
	 */
	public void setFiringTargetable(Targetable fired_by) {
		this.fired_by = fired_by;
	}
	
	public double getShotAngle() {
		return shot_angle;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Shot) {
			Shot o_ship = (Shot) other;		
			if(this.id.equals(o_ship.id)) return true;
		}
		
		return false;
		
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return !(isHit() || in.getTicks() - spawn_time >= shot_life);
	}

	/**
	 * @return the shot_type
	 */
	public int getShotType() {
		return shot_type;
	}

	/**
	 * @param shot_type the shot_type to set
	 */
	public void setShotType(int shot_type) {
		this.shot_type = shot_type;
		
		if(shot_type == BULLET) {
			this.setDamage(SHOT_DAMAGE);
		} else {
			this.setDamage(10 * SHOT_DAMAGE);
		}
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

	@Override
	public double getAngleFacing() {
		// TODO Auto-generated method stub
		return shot_angle;
	}

	@Override
	public int getTeam() {
		// TODO Auto-generated method stub
		return -1;
	}

	/**
	 * @return the shot_life
	 */
	public int getShotLife() {
		return shot_life;
	}

	/**
	 * @param shot_life the shot_life to set
	 */
	public void setShotLife(int shot_life) {
		this.shot_life = shot_life;
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
