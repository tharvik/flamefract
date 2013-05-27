package ch.epfl.flamemaker.geometry2d;

/**
 * A rectangle parallel to the x-axis and the y-axis (not rotated) with a center
 * as a {@link Point}
 */
public final class Rectangle {

	/**
	 * The center of the rectangle
	 */
	private final Point	center;

	/**
	 * The height of the rectangle
	 */
	private final double	height;

	/**
	 * The width of the rectangle
	 */
	private final double	width;

	/**
	 * Construct a rectangle with the given values
	 * 
	 * @param center
	 *                The center
	 * 
	 * @param width
	 *                The width
	 * 
	 * @param height
	 *                The height
	 * 
	 * @throws IllegalArgumentException
	 *                 if the width or the height <= 0 (mathematically
	 *                 impossible)
	 */
	public Rectangle(final Point center, final double width, final double height) {

		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		this.center = center;
		this.width = width;
		this.height = height;
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
	 * Return the height (y) of the bottom of the rectangle
	 * 
	 * @return The height (y) of the bottom of the rectangle
	 */
	public double bottom() {
		return this.center.y() - this.height() / 2;
	}

	/**
	 * Return the center of the rectangle
	 * 
	 * @return The center of the rectangle
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
	public boolean contains(final Point p) {
		return (p.x() >= this.left() && p.x() < this.right() && p.y() >= this.bottom() && p.y() < this.top());
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
	public Rectangle expandToAspectRatio(final double aspectRatio) {
		if (aspectRatio <= 0) {
			throw new IllegalArgumentException();
		}

		if (aspectRatio < this.aspectRatio()) {
			return new Rectangle(this.center, this.width(), this.width / aspectRatio);
		} else if (aspectRatio > this.aspectRatio()) {
			return new Rectangle(this.center, this.height() * aspectRatio, this.height);
		} else {
			return new Rectangle(this.center, this.width(), this.height);
		}
	}

	/**
	 * Return the height of the rectangle
	 * 
	 * @return The height of the rectangle
	 */
	public double height() {
		return this.height;
	}

	/**
	 * Return the distance from the y-axis to the left side of the rectangle
	 * 
	 * @return The distance from the y-axis to the left side of the
	 *         rectangle
	 */
	public double left() {
		return this.center.x() - this.width() / 2;
	}

	/**
	 * Return the distance from the y-axis to the right side of the
	 * rectangle
	 * 
	 * @return The distance from the y-axis to the right side of the
	 *         rectangle
	 */
	public double right() {
		return this.center.x() + this.width() / 2;
	}

	/**
	 * Return the height (y) of the top of the rectangle
	 * 
	 * @return The height (y) of the top of the rectangle
	 */
	public double top() {
		return this.center.y() + this.height() / 2;
	}

	/**
	 * Return a textual representation of the rectangle
	 * 
	 * @return A textual representation of the rectangle of the form
	 *         "(center), width, height"
	 */
	@Override
	public String toString() {
		return ("(" + this.center() + "," + this.width() + "," + this.height + ")");
	}

	/**
	 * Return the width of the rectangle
	 * 
	 * @return The width of the rectangle
	 */
	public double width() {
		return this.width;
	}
}
