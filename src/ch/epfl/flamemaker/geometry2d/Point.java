package ch.epfl.flamemaker.geometry2d;

public class Point {

	final double x, y;

	static final Point ORIGIN = new Point(0, 0);

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	double x() {
		return this.x;
	}

	double y() {
		return this.y;
	}

	double r() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
	}
}
