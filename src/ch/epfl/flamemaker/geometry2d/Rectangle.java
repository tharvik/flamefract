package ch.epfl.flamemaker.geometry2d;

/**
 * A rectangle with a position
 */
public class Rectangle {

	/**
	 * The center
	 */
	final private Point	center;

	/**
	 * The width
	 */
	final private double	width;

	/**
	 * The height
	 */
	final private double	height;

	/**
	 * Construct a rectangle with the given value
	 * 
	 * @param center
	 *                The center
	 * 
	 * @param width
	 *                The widht
	 * 
	 * @param height
	 *                The height
	 * 
	 * @throws IllegalArgumentException
	 *                 if the width or the height <= 0
	 */
	public Rectangle(Point center, double width, double height) {

		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		this.center = center;
		this.width = width;
		this.height = height;
	}

	/**
	 * Return the left side value
	 * 
	 * @return The left side value
	 */
	public double left() {
		return this.center.x() - this.width() / 2;
	}

	/**
	 * Return the right side value
	 * 
	 * @return The right side value
	 */
	public double right() {
		return this.center.x() + this.width() / 2;
	}

	/**
	 * Return the bottom side value
	 * 
	 * @return The bottom side value
	 */
	public double bottom() {
		return this.center.y() - this.height() / 2;
	}

	/**
	 * Return the top side value
	 * 
	 * @return The top side value
	 */
	public double top() {
		return this.center.y() + this.height() / 2;
	}

	/**
	 * Return the width
	 * 
	 * @return The width
	 */
	public double width() {
		return this.width;
	}

	/**
	 * Return the height
	 * 
	 * @return The height
	 */
	public double height() {
		return this.height;
	}

	/**
	 * Return the center
	 * 
	 * @return The center
	 */
	public Point center() {
		return this.center;
	}

	/**
	 * Return true if the given point is in the rectangle
	 * 
	 * @param p
	 *                The point to check
	 * 
	 * @return True if the given point is in the rectangle
	 */
	public boolean contains(Point p) {
		return (p.x() >= this.left() && p.x() < this.right() && p.y() >= this.bottom() && p.y() < this.top());
	}

	/**
	 * Return the ration width/height
	 * 
	 * @return The ration width/height
	 */
	public double aspectRatio() {
		return this.width() / this.height();
	}

	/**
	 * Return a new Rectangle expanded to have to correct ratio
	 * 
	 * @param aspectRatio
	 *                The ratio to obtain
	 * 
	 * @return A new rectangle expanded to the correct ratio
	 * 
	 * @throws IllegalArgumentException
	 *                 if the ratio <= 0
	 */
	public Rectangle expandToAspectRatio(double aspectRatio) {
		if (aspectRatio <= 0) {
			throw new IllegalArgumentException();
		}

		if (aspectRatio < this.aspectRatio()) {
			return new Rectangle(center, this.width(), this.height / aspectRatio);
		} else if (aspectRatio > this.aspectRatio()) {
			return new Rectangle(center, this.width() * aspectRatio, this.height);
		} else {
			return new Rectangle(center, this.width(), this.height);
		}
	}

	@Override
	public String toString() {
		return ("(" + this.center().toString() + "," + this.width() + "," + this.height + ")");
	}
}
