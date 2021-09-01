package util;

import java.awt.Point;

public class Vector {
	private double x, y;
	
	public Vector(double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.getX(), y + v.getY());
	}
	
	public Vector mult(double co) {
		return new Vector(x * co, y * co);
	}
	
	public double mag() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Point toPoint() {
		return new Point((int)x, (int)y);
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	
}
