package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;
import ch.epfl.flamemaker.ifs.IFS;

/**
 * Represent a colored fractal of type flame
 * <p>
 * Very similar to {@link IFS}, but with a {@link List} of
 * {@link FlameTransformation} in place of {@link AffineTransformation}
 * </p>
 */
public class Flame {
	/**
	 * The list of the {@link FlameTransformation} to use in the computation
	 */
	private final List<FlameTransformation>	transformations;
	/**
	 * An array of index of color for each {@link Transformation}
	 */
	private final double[]			arrayIndex;

	/**
	 * Construct a {@link Flame} with the given {@link List} of
	 * {@link FlameTransformation}
	 * 
	 * @param transformations
	 *                The {@link FlameTransformation} to use to generate the
	 *                fractal
	 */
	public Flame(List<FlameTransformation> transformations) {
		this.transformations = new ArrayList<FlameTransformation>(transformations);

		this.arrayIndex = new double[this.transformations.size()];
		switch (this.arrayIndex.length) {
		case 2:
			arrayIndex[1] = 1;

		case 1:
			arrayIndex[0] = 0;
			break;

		case 0:
			break;

		default:
			arrayIndex[0] = 0;
			arrayIndex[1] = 1;
			for (int i = 2; i < arrayIndex.length; i++) {
				double log2I = Math.log(i) / Math.log(2);
				arrayIndex[i] = ((i - (Math.pow(2, Math.floor(log2I)))) * 2 + 1)
						/ (Math.pow(2, Math.ceil(log2I)));
			}
			break;
		}
	}

	/**
	 * Compute the fractal, with the given scope (frame), the definition
	 * (width and height) and the accuracy (density)
	 * 
	 * @param frame
	 *                The scope of the fractal, used in the
	 *                {@link FlameAccumulator}
	 * @param width
	 *                The width of the {@link FlameAccumulator}
	 * @param height
	 *                The height of the {@link FlameAccumulator}
	 * @param density
	 *                A constant representing the number of wanted iteration
	 *                (the more, the better the fractal will be but the
	 *                longer it will take to generate)
	 * 
	 * @return A {@link FlameAccumulator} with the generate fractal
	 */
	public FlameAccumulator compute(Rectangle frame, int width, int height, int density) {

		Random rand = new Random(2013);
		Point p = Point.ORIGIN;
		int m = density * width * height;
		FlameAccumulator.Builder image = new FlameAccumulator.Builder(frame, width, height);

		if (this.transformations.size() == 0) {
			return image.build();
		}

		double lastColor = 0;
		for (int j = 0; j < 20; j++) {
			int i = rand.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
			lastColor = (this.arrayIndex[i] + lastColor) / 2.0;
		}

		for (int j = 0; j < m; j++) {
			int i = rand.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);

			lastColor = (this.arrayIndex[i] + lastColor) / 2.0;
			image.hit(p, lastColor);
		}

		return image.build();
	}

	/**
	 * A incremental builder for {@link Flame}
	 */
	public static class Builder {
		/**
		 * The {@link ArrayList} of {@link FlameAccumulator.Builder}
		 * which represent the current state of the Builder
		 */
		private ArrayList<FlameTransformation.Builder>	list;

		/**
		 * Construct a Flame.Builder with the given {@link Flame}
		 * 
		 * @param flame
		 *                The {@link Flame} to take as base
		 */
		public Builder(Flame flame) {
			this.list = new ArrayList<FlameTransformation.Builder>();
			for (FlameTransformation flameTransformation : flame.transformations) {
				this.list.add(new FlameTransformation.Builder(flameTransformation));
			}
		}

		/**
		 * Return the size of the list
		 * 
		 * @return The size of the list
		 */
		public int transformationCount() {
			return this.list.size();
		}

		/**
		 * Add a new {@link FlameTransformation} to the end of the list
		 * 
		 * @param transformation
		 *                The {@link FlameTransformation} to add to the
		 *                end of the list
		 */
		public void addTransformation(FlameTransformation transformation) {
			this.list.add(new FlameTransformation.Builder(transformation));
		}

		/**
		 * Return the {@link AffineTransformation} of the
		 * {@link FlameTransformation} at the given index in the list
		 * 
		 * @param index
		 *                The index for the {@link FlameTransformation}
		 *                
		 * @return The {@link AffineTransformation} of the
		 *         {@link FlameTransformation} at the given index in the
		 *         list
		 */
		public AffineTransformation affineTransformation(int index) {
			this.checkIndex(index);
			return this.list.get(index).getAffineTransformation();
		}

		public void setAffineTransformation(int index, AffineTransformation newTransformation) {
			this.checkIndex(index);
			this.list.get(index).setAffineTransformation(newTransformation);
		}

		public void setVariationWeight(int index, Variation variation, double newWeight) {
			checkIndex(index);
			this.list.get(index).setVariationWeight(variation.index(), newWeight);
		}

		public void removeTransformation(int index) {
			this.checkIndex(index);
			this.list.remove(index);
		}

		private void checkIndex(int index) {
			if (index < 0 || index > this.transformationCount()) {
				throw new IndexOutOfBoundsException();
			}
		}

		public Flame build() {
			ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
			for (FlameTransformation.Builder builder : this.list) {
				transformations.add(builder.build());
			}

			return new Flame(transformations);
		}
	}
}
