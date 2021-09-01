package core;

import java.util.HashSet;

import gamecore.World;
import util.Order;
import util.Vector;

public class ShipAI {
	
	public static final int RANGE = 250;
	
	private Ship controlling;
	private World in;
	
	private Mode mode;
	private Mode pref_mode;
	
	private Targetable target;
	
	private int opponent_team = -1;
	private boolean lock_attack = false;
	
	public ShipAI(Ship controlling) {
		this.setControlling(controlling);
		in = this.controlling.getWorld();
		
		mode = Mode.PASSIVE;
		
		pref_mode = Math.random() > 0.35 ? Mode.AGGRESSIVE : Mode.DEFENSIVE;
	}
	
	public void step() {
		
		Order order = new Order(Order.DO_NOTHING, null);
		
		decideOpponentTeam();
	
		if(mode == Mode.PASSIVE) {
			if(!in.getHits(controlling).isEmpty()) {
				
				HashSet<Targetable> hits = in.getHits(controlling);
				
				if(target == null)	
					for(Targetable hit: hits) {
						target = hit;
						if(target.exists()) break;
					}
					
				double distance = target.getLoc().add(controlling.getLoc().mult(-1)).mag();
				
				double vel_mag = controlling.getVel().add(target.getVel().mult(-1)).mag();
				
				if(distance > RANGE) {
					order = new Order(Order.MOVE_TO, target);
					lock_attack = false;
				} else {
					if(vel_mag < 10 && !lock_attack) {
						order = (new Order(Order.HOLD_POSITION, null));
					} else {
						order = new Order(Order.ATTACK, target);
						lock_attack = true;
					}
				}
				
			} else {
				target = null;
				mode = pref_mode;
				lock_attack = false;
			}
			
		} else if(mode == Mode.AGGRESSIVE) {
			
			if(!in.getHits(controlling).isEmpty()) {
				mode = Mode.PASSIVE;
				target = null;
			} else {
				
				if(target == null) {
					
					if(in.get(opponent_team) == null) {
						decideOpponentTeam();
					}
					
					HashSet<Ship> ships = in.get(opponent_team).getShips();
					
					for(Ship s: ships) {
						target = s;
						if(Math.random() > 0.75 && s.exists()) break;
					}
					
					if(Math.random() > 0.8) {
						target = in.get(opponent_team);
					}
					
				}
				
				if(target != null) {
					double distance = target.getLoc().add(controlling.getLoc().mult(-1)).mag();
					
					double vel_mag = controlling.getVel().add(target.getVel().mult(-1)).mag();
					
					if(distance > RANGE) {
						order = new Order(Order.MOVE_TO, target);
						lock_attack = false;
					} else {
						if(vel_mag > Ship.MAX_VEL/2 && !lock_attack) {
							order = new Order(Order.HOLD_POSITION, null);
						} else {
							order = new Order(Order.ATTACK, target);
							lock_attack = true;
						}
					}
				} else {
					mode = Mode.PASSIVE;
					lock_attack = false;
				}
			}
			
		} else if(mode == Mode.DEFENSIVE) {
			
			if(in.get(controlling.getTeam()) == null) {
				mode = Mode.PASSIVE;
				target = null;
			} else {
				
				if(target == null) {
					target = in.get(controlling.getTeam());
					
					Shipyard tgt = (Shipyard) target;
					
					HashSet<Targetable> ships = in.getHits(tgt);
					
					for(Targetable s: ships) {
						opponent_team = s.getTeam();
						if(Math.random() > 0.75 && in.get(opponent_team) != null) break;
					}
				}
				
				if(target != null) {
				
					double distance = target.getLoc().add(controlling.getLoc().mult(-1)).mag();
					
					if(distance > RANGE) {
						order = new Order(Order.MOVE_TO, target);
					} else {
						if(isWithin(controlling.getVel().mag(), 0, 0.01)) {
							order = new Order(Order.DEFEND, target);
						} else {
							order = new Order(Order.HOLD_POSITION, null);
						}
					}
				}
			}
			
		}
		
		if(target != null && !target.exists()) target = null;
		
		controlling.queueOrder(order);
		
	}
	
	private void decideOpponentTeam() {
		// TODO Auto-generated method stub
		if(opponent_team < 0 || in.get(opponent_team) == null) {
			do {
				int select = (int) (Math.random() * in.getTeams().size());
				opponent_team = in.getTeams().get(select);
			} while(in.getTeams().size() > 1 && (opponent_team == controlling.getTeam() || in.get(opponent_team) == null));
		}
		
	}

	/**
	 * @return the controlling
	 */
	public Ship getControlling() {
		return controlling;
	}

	/**
	 * @param controlling the controlling to set
	 */
	public void setControlling(Ship controlling) {
		this.controlling = controlling;
	}
	
	public static boolean isWithin(double value, double expected, double range) {
		return value >= expected - range && value <= expected + range;
	}
	
	
}

