package ch.epfl.flamemaker.geometry2d;

/**
 * Represent a point
 */
public class Point {

	/**
	 * The point of origin
	 */
	public static final Point	ORIGIN	= new Point(0, 0);

	/**
	 * The x value
	 */
	private final double		x;

	/**
	 * The y value
	 */
	private final double		y;

	/**
	 * Construct a new point with the given values
	 * 
	 * @param x
	 *                The x value
	 * 
	 * @param y
	 *                The y value
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Return the length of the vector to this point
	 * 
	 * @return The length of the vector to this point
	 */
	public double r() {
		return Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.y, 2.0));
	}

	/**
	 * Return the angle between y = 0 and this point
	 * 
	 * @return The angle between y = 0 and this point
	 */
	public double theta() {
		return Math.atan2(y, x);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	/**
	 * Return the x value
	 * 
	 * @return The x value
	 */
	public double x() {
		return this.x;
	}

	/**
	 * Return the y value
	 * 
	 * @return The y value
	 */
	public double y() {
		return this.y;
	}
}
