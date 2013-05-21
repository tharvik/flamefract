package ch.epfl.flamemaker.geometry2d;

/**
 * Represent a point
 */
public final class Point {

	/**
	 * The point of origin, i.e (0,0)
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
	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Return the length of the vector from the origin to the point (x, y)
	 * 
	 * @return The length of the vector from the origin to the point (x, y)
	 */
	public double r() {
		return Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.y, 2.0));
	}

	/**
	 * Return the angle between the vector r and the x axis
	 * 
	 * @return The angle between the vector r and the x axis
	 */
	public double theta() {
		return Math.atan2(this.y, this.x);
	}

	/**
	 * Return a textual representation of our point
	 * 
	 * @return A textual representation of our point of the form "(x, y)"
	 */
	@Override
	public String toString() {
		return "(" + this.x + "," + this.y + ")";
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
