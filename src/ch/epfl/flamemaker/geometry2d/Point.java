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

	public Point clone() {
		return new Point(this.x(), this.y());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Point that = (Point) obj;
		if (this.x() != that.x()) {
			return false;
		}
		if (this.y() != that.y()) {
			return false;
		}
		return true;
	}

}
