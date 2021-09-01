package gamecore;

import java.awt.Color;

import core.Shipyard;
import graphics.WorldPanel;
import util.Vector;

public class Game{
	public static final double DT = 0.001;
	public static final int MAX_SHIPYARDS = 10;
	
	private World current_world;
	public WorldPanel world_panel;
	
	public Game() {
		current_world = new World();
		world_panel = new WorldPanel(current_world);
		
		initialize();
		
	}
	
	public void initialize() {
		
		int shipyard = (int) (Math.random() * MAX_SHIPYARDS) + 1;
		
		for(int i = 0; i < shipyard; i++) {
			
			Vector location = new Vector(Math.random() * World.WORLD_WIDTH, Math.random() * World.WORLD_HEIGHT);
			
			double angle_facing = Math.random() * Math.PI * 2;
			
			Shipyard yard = new Shipyard(location, current_world, i, angle_facing);
			
			yard.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
			
			current_world.add(yard);
			
		}
		
		current_world.step();
		
		world_panel.update(current_world);
		
	}
	
	public void reinitialize() {
		current_world = new World();
		initialize();
	}
	
	public void step() {
		current_world.step();
		
		int winner = current_world.checkWinner();
		
		if(winner >= 0) {
			System.out.println("Winner: " + winner);
			
			reinitialize();
		
		}
	}
	
	/**
	 * @return the current_world
	 */
	public World getCurrentWorld() {
		return current_world;
	}

	/**
	 * @param current_world the current_world to set
	 */
	public void setCurrentWorld(World current_world) {
		this.current_world = current_world;
	}
}
