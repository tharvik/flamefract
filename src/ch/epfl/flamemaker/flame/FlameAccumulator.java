package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Accumulator contains the pseudo-image of a flame fractal, with colors
 */
public class FlameAccumulator {

	/**
	 * Builder for FlameAccumulator
	 */
	public static class Builder {

		/**
		 * Array containing the sum of the index for every pixel from
		 * the fractal
		 */
		private final double[][]	colorIndexSum;

		/**
		 * Rectangle where the hit point is counted
		 */
		private final Rectangle		frame;

		/**
		 * Array containing the hit count of every pixel from the
		 * fractal
		 */
		private final int[][]		hitCount;

		/**
		 * A transformation used in hit to map the given point to out
		 * system
		 */
		private AffineTransformation	transformation;

		/**
		 * Construct a builder of FlameAccumulator
		 * 
		 * @param frame
		 *                Rectangle where the hit point is counted
		 * @param width
		 *                Width of the accumulator
		 * @param height
		 *                Height of the accumulator
		 * @throws IllegalArgumentException
		 *                 if the height or width are smaller or equal
		 *                 to one
		 */
		public Builder(final Rectangle frame, final int width, final int height) {
			if (width <= 0 || height <= 0) {
				throw new IllegalArgumentException();
			}

			this.frame = new Rectangle(frame.center(), frame.width(), frame.height());
			this.transformation = AffineTransformation.newScaling(width / frame.width(),
					height / frame.height());
			this.transformation = this.transformation.composeWith(AffineTransformation.newTranslation(
					-frame.left(), -frame.bottom()));

			this.hitCount = new int[width][height];
			this.colorIndexSum = new double[width][height];
		}

		/**
		 * Return an Accumulator with the actual state of the builder
		 * 
		 * @return An Accumulator with the actual state of the builder
		 */
		public FlameAccumulator build() {
			return new FlameAccumulator(this.hitCount, this.colorIndexSum);
		}

		/**
		 * Update the accumulator if the given point is in the wanted
		 * rectangle
		 * 
		 * @param p
		 *                The point which we hit
		 * @param colorIndex
		 *                The index of the color for this point
		 */
		public synchronized void hit(Point p, final double colorIndex) {
			if (!this.frame.contains(p)) {
				return;
			}

			// we transform the point in our system
			p = this.transformation.transformPoint(p);
			final int x = (int) (p.x()), y = (int) (p.y());
			this.colorIndexSum[x][y] += colorIndex;
			this.hitCount[x][y]++;
		}
	}

	/**
	 * Array containing the sum of the index for every pixel from the
	 * fractal
	 */
	private final double[][]	colorIndexSum;

	/**
	 * Used to increase speed of intensity() method
	 */
	private final double		denominator;

	/**
	 * Array containing the hit count of every pixel from the fractal
	 */
	private final int[][]		hitCount;

	/**
	 * Construct a FlameAccumulator with the given array of hit count and
	 * array of sum of color
	 * 
	 * @param hitCount
	 *                Array of hit count per pixel
	 * @param colorIndexSum
	 *                Array of the sum of index of color per pixel
	 * 
	 * @throws IllegalArgumentException
	 *                 if the given arrays have not the same size
	 */
	private FlameAccumulator(final int[][] hitCount, final double[][] colorIndexSum) {
		this.hitCount = new int[hitCount.length][hitCount[0].length];
		this.colorIndexSum = new double[colorIndexSum.length][colorIndexSum[0].length];

		if (hitCount.length != colorIndexSum.length || hitCount[0].length != colorIndexSum[0].length) {
			throw new IllegalArgumentException();
		}

		// deep copy of the hitCount array
		for (int x = 0; x < this.hitCount.length; x++) {
			this.hitCount[x] = hitCount[x].clone();
		}

		// deep copy of the colorIndexSum array
		for (int x = 0; x < this.colorIndexSum.length; x++) {
			this.colorIndexSum[x] = colorIndexSum[x].clone();
		}

		// get max value and calculate denominator
		int max = 0;
		for (final int[] x : hitCount) {
			for (final int value : x) {
				if (max < value) {
					max = value;
				}
			}
		}
		this.denominator = Math.log(max + 1);
	}

	/**
	 * Return the color of the wanted pixel, mixed with the background
	 * depending of the intensity
	 * 
	 * @param palette
	 *                The {@link Palette} where to retrieve the
	 *                {@link Color} to mix with
	 * @param background
	 *                The {@link Color} of the background
	 * @param x
	 *                The x value of the wanted pixel
	 * @param y
	 *                The y value of the wanted pixel
	 * 
	 * @return The color of the wanted pixel
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 if x or y are not in the accumulator
	 */
	public Color color(final Palette palette, final Color background, final int x, final int y) {
		if (x < 0 || y < 0 || x > this.width() || y > this.height()) {
			throw new IndexOutOfBoundsException();
		}

		return background.mixWith(palette.colorForIndex(this.colorIndexSum[x][y] / this.hitCount[x][y]),
				this.intensity(x, y));
	}

	/**
	 * Return the width of the accumulator
	 * 
	 * @return The height of the accumulator
	 */
	public int height() {
		return this.hitCount[0].length;
	}

	/**
	 * Return the width of the accumulator
	 * 
	 * @return The width of the accumulator
	 */
	public int width() {
		return this.hitCount.length;
	}

	/**
	 * Return the intensity at the given pixel
	 * 
	 * @param x
	 *                The x value of the wanted pixel
	 * @param y
	 *                The y value of the given pixel
	 * 
	 * @return The intensity at the given pixel
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 if x or y are not in the accumulator
	 */
	double intensity(final int x, final int y) {
		if (x < 0 || y < 0 || x > this.width() || y > this.height()) {
			throw new IndexOutOfBoundsException();
		}

		return (Math.log(this.hitCount[x][y] + 1) / this.denominator);
	}
}
