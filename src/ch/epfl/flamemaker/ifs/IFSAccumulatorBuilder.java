package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {

	/**
	 * Bidimensional array of booleans that <i>is</i> the accumulator
	 */
	private boolean[][]		isHit;
	/**
	 * The region of the whole picture we want to examine
	 */
	private Rectangle		frame;
	/** 
	 * Specific transformation
	 */
	private AffineTransformation	transformation;

	/**
	 * Build an accumulator for the "frame" region
	 * @param frame The region into which the builder will build
	 * @param width Width of the accumulator
	 * @param height Height of the accumulator
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
	 * Sets to true the "box" of the accumulator
	 * @param p A point
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
	 * @return An accumulator with the current points
	 */
	public IFSAccumulator build() {
		return new IFSAccumulator(this.isHit);
	}

}
