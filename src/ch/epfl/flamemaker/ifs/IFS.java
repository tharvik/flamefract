package ch.epfl.flamemaker.ifs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Represent a colored fractal of type IFS
 * <p>
 * Very similar to {@link Flame}, but with a {@link List} of
 * {@link AffineTransformation} in place of {@link FlameTransformation}
 * </p>
 */
public final class IFS {
	/**
	 * The list of the {@link AffineTransformation} to use in the
	 * computation
	 */
	private final List<AffineTransformation>	transformations;

	/**
	 * Construct a {@link IFS} with the given {@link List} of
	 * {@link AffineTransformation}
	 * 
	 * @param transformations
	 *                The {@link AffineTransformation} to use to generate
	 *                the fractal
	 */
	public IFS(List<AffineTransformation> transformations) {
		this.transformations = new ArrayList<AffineTransformation>(transformations);
	}

	/**
	 * Compute the fractal, with the given scope (frame), the definition
	 * (width and height) and the accuracy (density)
	 * 
	 * @param frame
	 *                The scope of the fractal, used in the
	 *                {@link IFSAccumulator}
	 * @param width
	 *                The width of the {@link IFSAccumulator}
	 * @param height
	 *                The height of the {@link IFSAccumulator}
	 * @param density
	 *                A constant representing the <i>accuracy</i> (the more,
	 *                the more accurate the fractal will be but the longer
	 *                it will take to generate)
	 * 
	 * @return A {@link IFSAccumulator} with the generate fractal
	 */
	public IFSAccumulator compute(Rectangle frame, int width, int height, int density) {

		final Random random = new Random();
		Point p = Point.ORIGIN;
		final int m = density * width * height;
		final IFSAccumulatorBuilder image = new IFSAccumulatorBuilder(frame, width, height);

		// Speed up if we do not have any transformation
		if (this.transformations.size() == 0) {
			return image.build();
		}

		// Randomize the point
		for (int j = 0; j < 20; j++) {
			final int i = random.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
		}

		// Do the actual computation and hit the accumulator
		for (int j = 0; j < m; j++) {
			final int i = random.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
			image.hit(p);
		}

		return image.build();
	}
}
