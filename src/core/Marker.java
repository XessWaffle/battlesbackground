package core;

import util.Vector;

public class Marker implements Targetable{
	
	private static int mrk_cnt = 0;
	
	private Vector loc;
	private String id;
	private boolean required = true;
	
	
	public Marker(Vector loc) {
		this.loc = loc;
		id = "MRK " + mrk_cnt;
		mrk_cnt++; 
	}
	
	@Override
	public Vector getLoc() {
		// TODO Auto-generated method stub
		return loc;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return required;
	}
	
	public void setRequired(boolean req) {
		required = req;
	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
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

	@Override
	public double getAngleFacing() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTeam() {
		// TODO Auto-generated method stub
		return -1;
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
