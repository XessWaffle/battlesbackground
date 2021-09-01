package gamecore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import util.Vector;

import core.Ship;
import core.Shipyard;
import core.Shot;
import core.Targetable;

public class World {
	
	public static final int WORLD_WIDTH = 1000;
	public static final int WORLD_HEIGHT = 750;
	
	private HashSet<Ship> ships, r_ships, a_ships;
	private HashSet<Shot> shots, r_shots, a_shots;
	private ArrayList<Shipyard> shipyards, r_yards, a_yards;
	
	private HashMap<Ship, HashSet<Targetable>> ship_shots;
	private HashMap<Shipyard, HashSet<Targetable>> yard_shots;
	private HashMap<Ship, Integer> ship_hits;
	private HashMap<Shipyard, Integer> yard_hits;
	
	private ArrayList<Integer> teams;
	
	private int ticks = 0;
	
	public World() {
		ships = new HashSet<>();
		shots = new HashSet<>();
		shipyards = new ArrayList<>();
		
		ship_shots = new HashMap<>();
		yard_shots = new HashMap<>();
		ship_hits = new HashMap<>();
		yard_hits = new HashMap<>();
		
		r_ships = new HashSet<>();
		r_shots = new HashSet<>();
		r_yards = new ArrayList<>();
	
		a_ships = new HashSet<>();
		a_shots = new HashSet<>();
		a_yards = new ArrayList<>();
		
		teams = new ArrayList<Integer>();
	}
	
	public HashSet<Ship> getShips(){
		return ships;
	}
	
	public HashSet<Shot> getShots(){
		return shots;
	}
	
	public ArrayList<Shipyard> getShipyards(){
		return shipyards;
	}
	
	public void add(Ship e) {
		
		//System.out.println("Adding Ship: " + e.getId());
		
		a_ships.add(e);
	}
	
	public void add(Shot e) {
		
		//System.out.println("Adding Shot: " + e.getShotType());
		
		a_shots.add(e);
	}
	
	public void add(Shipyard e) {
		
		//System.out.println("Adding Shipyard: " + e.getId());
		a_yards.add(e);
		
	}
	
	public Shipyard get(int team) {
		
		for(Shipyard e: shipyards) {
			if(team == e.getTeam()) {
				return e;
			}
		}
		
		return null;
		
		
	}
	
	public void remove(Ship e) {
		r_ships.add(e);
	}
	
	public void remove(Shot e) {
		r_shots.add(e);
	}
	
	public void remove(Shipyard e) {
		//r_ships.addAll(e.getShips());
		r_yards.add(e);
	}
	
	public int getDamage(Ship e){
		ship_hits.putIfAbsent(e, 0);
		return ship_hits.get(e);
	}
	
	public int getDamage(Shipyard e){
		yard_hits.putIfAbsent(e, 0);
		return yard_hits.get(e);
	}
	
	public HashSet<Targetable> getHits(Ship e){
		ship_shots.putIfAbsent(e, new HashSet<Targetable>());
		return ship_shots.get(e);
	}
	
	public HashSet<Targetable> getHits(Shipyard e){
		yard_shots.putIfAbsent(e, new HashSet<Targetable>());
		return yard_shots.get(e);
	}
	
	public int checkWinner() {
		
		if(shipyards.size() == 1) {
			return shipyards.get(0).getTeam();
		}
		
		return -1;
	}
	
	public void step() {
		
		shipyards.addAll(a_yards);
		ships.addAll(a_ships);
		shots.addAll(a_shots);
		
		ships.removeAll(r_ships);
		shots.removeAll(r_shots);
		shipyards.removeAll(r_yards);
		teams.clear();
		
		for(Ship chk: ships) {
			ship_hits.put(chk, 0);
			ship_shots.putIfAbsent(chk, new HashSet<Targetable>());
			
			ship_shots.get(chk).removeAll(r_ships);
			ship_shots.get(chk).removeAll(r_yards);
		}
		
		for(Shipyard chk: shipyards) {
			yard_hits.put(chk, 0);
			yard_shots.putIfAbsent(chk, new HashSet<Targetable>());
			
			yard_shots.get(chk).removeAll(r_ships);
			yard_shots.get(chk).removeAll(r_yards);
			
			teams.add(chk.getTeam());
		}
		
		r_ships.clear();
		r_shots.clear();
		r_yards.clear();
		
		a_ships.clear();
		a_shots.clear();
		a_yards.clear();
		
		for(Shot pot: shots) {
			
			for(Ship chk: ships) {
				Vector mag_check = pot.getLoc().add(chk.getLoc().mult(-1));
				
				if(!chk.isShieldsup()) {
					if(mag_check.mag() < Ship.SHIP_BODY_HIT_RADIUS && pot.getFiringTargetable().getTeam() != chk.getTeam()) {
						ship_hits.put(chk, (int) (ship_hits.get(chk) + pot.getDamage()));
						if(pot.getFiringTargetable().exists()) {
							ship_shots.get(chk).add(pot.getFiringTargetable());
						}
						pot.setHit(true);
					}
				} else {
					if(mag_check.mag() < Ship.SHIP_SHIELD_HIT_RADIUS && pot.getFiringTargetable().getTeam() != chk.getTeam()) {
						ship_hits.put(chk, (int) (ship_hits.get(chk) + pot.getDamage()));
						if(pot.getFiringTargetable().exists()) {
							ship_shots.get(chk).add(pot.getFiringTargetable());
						}
						pot.setHit(true);
					}
				}
			}

			for(Shipyard chk: shipyards) {
				Vector mag_check = pot.getLoc().add(chk.getLoc().mult(-1));
				
				if(!chk.isShieldsup()) {
					if(mag_check.mag() < Shipyard.SHIP_BODY_HIT_RADIUS && pot.getFiringTargetable().getTeam() != chk.getTeam()) {
						yard_hits.put(chk, (int) (yard_hits.get(chk) + pot.getDamage()));
						if(pot.getFiringTargetable().exists()) {
							yard_shots.get(chk).add(pot.getFiringTargetable());
						}
						pot.setHit(true);
					}
				} else {
					if(mag_check.mag() < Shipyard.SHIP_SHIELD_HIT_RADIUS && pot.getFiringTargetable().getTeam() != chk.getTeam()) {
						yard_hits.put(chk, (int) (yard_hits.get(chk) + pot.getDamage()));
						if(pot.getFiringTargetable().exists()) {
							yard_shots.get(chk).add(pot.getFiringTargetable());
						}
						pot.setHit(true);
					}
				}	
			}
		}
		
		
		for(Ship e: ships) {
			e.step();
		}
		
		for(Shipyard e: shipyards) {
			e.step();
		}
		
		for(Shot e: shots) {
			e.step();
		}
		
		ticks++;
		
	}

	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @param ticks the ticks to set
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * @return the teams
	 */
	public ArrayList<Integer> getTeams() {
		return teams;
	}

	public boolean contains(Ship ship) {
		// TODO Auto-generated method stub
		return ships.contains(ship);
	}

	public boolean contains(Shipyard shipyard) {
		// TODO Auto-generated method stub
		return shipyards.contains(shipyard);
	}
	
	
}
