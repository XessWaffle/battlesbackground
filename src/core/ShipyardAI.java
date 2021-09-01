package core;

import java.util.HashSet;

import gamecore.World;
import util.Order;

public class ShipyardAI {

	private Shipyard controlling;
	private World in;
	
	private Targetable target;
	
	public ShipyardAI(Shipyard controlling) {
		this.setControlling(controlling);
		this.setWorld(controlling.getWorld());
	}
	
	public void step() {
		if(target == null) {
			HashSet<Targetable> pot_targets = in.getHits(controlling);
			
			for(Targetable target: pot_targets) {
				this.target = target;
				if(target.exists() && Math.random() > 0.8) break;
			}	
		}
		
		Order next = new Order(Order.DO_NOTHING, null);
		
		if(target != null) {
			next = new Order(Order.ATTACK, target);
		} 
		
		if(target != null && !target.exists()) {
			target = null;
		}
		
		controlling.queueOrder(next);
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
	 * @return the controlling
	 */
	public Shipyard getControlling() {
		return controlling;
	}

	/**
	 * @param controlling the controlling to set
	 */
	public void setControlling(Shipyard controlling) {
		this.controlling = controlling;
	}

	/**
	 * @return the in
	 */
	public World getWorld() {
		return in;
	}

	/**
	 * @param in the in to set
	 */
	public void setWorld(World in) {
		this.in = in;
	}
}
