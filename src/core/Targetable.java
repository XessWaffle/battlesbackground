package core;

import util.Vector;

public interface Targetable {
	
	public Vector getLoc();
	public Vector getVel();
	public Vector getAcc();
	public Vector getLoc(int ticks);
	public Vector getVel(int ticks);
	
	public boolean exists();
	public void setId(String id);
	public String getId();
	public double getAngleFacing();
	public int getTeam();
	
}
