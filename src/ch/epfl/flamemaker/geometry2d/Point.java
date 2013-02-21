package ch.epfl.flamemaker.geometry2d;

public class Point {
	
	final private double		x, y;
	static final public Point	ORIGIN	= new Point(0, 0);
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}
	
	public double r() {
		return Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.y, 2.0));
	}
	
	public double theta() {
		return Math.atan2(y, x);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
