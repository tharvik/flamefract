package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * Builder for {@link IFSAccumulator}
 */
public class IFSAccumulatorBuilder {

	/**
	 * Bidimensional array of booleans that <i>is</i> the accumulator
	 */
	private boolean[][]		isHit;
	/**
	 * The scope relevant to the fractal
	 */
	private Rectangle		frame;
	/**
	 * Specific transformation used to <i>move</i> the point given in
	 * hit(Point) to the actual position in our array
	 */
	private AffineTransformation	transformation;

	/**
	 * Build an accumulator with the given scope (frame) and resolution
	 * (width and height)
	 * 
	 * @param frame
	 *                The region into which the builder will build
	 * @param width
	 *                Width of the accumulator
	 * @param height
	 *                Height of the accumulator
	 * 
	 * @throws IllegalArgumentException
	 *                 if the widht or height are less or equal to zero
	 */
	public IFSAccumulatorBuilder(Rectangle frame, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		this.frame = new Rectangle(frame.center(), frame.width(), frame.height());
		this.transformation = AffineTransformation.newScaling(width / frame.width(), height / frame.height());
		this.transformation = this.transformation.composeWith(AffineTransformation.newTranslation(
				-frame.left(), -frame.bottom()));

		this.isHit = new boolean[width][height];
	}

	/**
	 * Sets the given point as hit (which is <i>true</i> in our array)
	 * 
	 * @param p
	 *                The point to hit
	 */
	public void hit(Point p) {
		if (!this.frame.contains(p)) {
			return;
		}

		// We transform the point in our system
		p = this.transformation.transformPoint(p);
		// A nice cast
		int px = (int) Math.floor(p.x());
		int py = (int) Math.floor(p.y());
		this.isHit[px][py] = true;
	}

	/**
	 * Returns an accumulator with the current points
	 * 
	 * @return An accumulator with the current points
	 */
	public IFSAccumulator build() {
		return new IFSAccumulator(this.isHit);
	}

}
