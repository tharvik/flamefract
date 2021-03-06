package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.extra.Preferences;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;
import ch.epfl.flamemaker.ifs.IFS;

/**
 * Represent a colored fractal of type flame </br> Very similar to {@link IFS},
 * but with a {@link List} of {@link FlameTransformation} in place of
 * {@link AffineTransformation}
 */
public class Flame {
	/**
	 * A incremental builder for {@link Flame}
	 */
	public static class Builder {
		/**
		 * The {@link ArrayList} of {@link FlameAccumulator.Builder}
		 * which represent the current state of the Builder
		 */
		private final ArrayList<FlameTransformation.Builder>	list;

		/**
		 * Copy-construct a new {@link Builder} based on the given one
		 * 
		 * @param copy
		 *                The {@link Builder} to copy
		 */
		public Builder(final Builder copy) {
			this.list = new ArrayList<FlameTransformation.Builder>();
			for (final FlameTransformation.Builder builder : copy.list) {
				final double[] array = new double[6];
				for (int i = 0; i < array.length; i++) {
					array[i] = builder.getVariationWeightValue(i);
				}
				this.list.add(new FlameTransformation.Builder(new FlameTransformation(builder
						.getAffineTransformation(), array)));
			}
		}

		/**
		 * Construct a Flame.Builder with the given {@link Flame}
		 * 
		 * @param flame
		 *                The {@link Flame} to take as base
		 */
		public Builder(final Flame flame) {
			// deep copy of the list of the given Flame
			this.list = new ArrayList<FlameTransformation.Builder>();
			for (final FlameTransformation flameTransformation : flame.transformations) {
				this.list.add(new FlameTransformation.Builder(flameTransformation));
			}
		}

		/**
		 * Add a new {@link FlameTransformation} to the end of the list
		 * 
		 * @param transformation
		 *                The {@link FlameTransformation} to add to the
		 *                end of the list
		 */
		public void addTransformation(final FlameTransformation transformation) {
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
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 If the index is less than zero of greater
		 *                 than the max index of the list
		 */
		public AffineTransformation affineTransformation(final int index) {
			this.checkIndex(index);
			return this.list.get(index).getAffineTransformation();
		}

		/**
		 * Return a {@link Flame} with the actual state of the Builder
		 * 
		 * @return A {@link Flame} with the actual state of the Builder
		 */
		public Flame build() {

			// build every FlameTransformation.Builder of the list
			// and add it to the return list
			final ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
			for (final FlameTransformation.Builder builder : this.list) {
				transformations.add(builder.build());
			}

			return new Flame(transformations);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final Builder other = (Builder) obj;
			if (this.list == null) {
				if (other.list != null) {
					return false;
				}
			} else if (!this.list.equals(other.list)) {
				return false;
			}

			final Iterator<FlameTransformation.Builder> i = this.list.iterator(), o = other.list.iterator();
			for (; i.hasNext() && o.hasNext();) {
				if (!i.next().equals(o.next())) {
					return false;
				}
			}

			return true;
		}

		/**
		 * Remove the {@link FlameTransformation} at the given index
		 * 
		 * @param index
		 *                The index in the list to remove
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 If the index is less than zero of greater
		 *                 than the max index of the list
		 */
		public void removeTransformation(final int index) {
			this.checkIndex(index);
			this.list.remove(index);
		}

		/**
		 * Set the {@link AffineTransformation} of the
		 * {@link FlameTransformation} at the given index in the list
		 * 
		 * @param index
		 *                The index for the {@link FlameTransformation}
		 * 
		 * @param newTransformation
		 *                The new {@link AffineTransformation}
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 If the index is less than zero of greater
		 *                 than the max index of the list
		 */
		public void setAffineTransformation(final int index, final AffineTransformation newTransformation) {
			this.checkIndex(index);
			this.list.get(index).setAffineTransformation(newTransformation);
		}

		/**
		 * Set the weight of given {@link Variation} of the
		 * {@link FlameTransformation} at the given index in the list
		 * 
		 * @param index
		 *                The index for the {@link FlameTransformation}
		 * @param variation
		 *                The {@link Variation} which we want to change
		 *                the weight
		 * @param newWeight
		 *                The new weight
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 If the index is less than zero of greater
		 *                 than the max index of the list
		 */
		public void setVariationWeight(final int index, final Variation variation, final double newWeight) {
			this.checkIndex(index);
			this.list.get(index).setVariationWeight(variation.index(), newWeight);
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
		 * Get the weight of given {@link Variation} of the
		 * {@link FlameTransformation} at the given index in the list
		 * 
		 * @param index
		 *                The index for the {@link FlameTransformation}
		 * @param variation
		 *                The {@link Variation} of which we want to get
		 *                the weight
		 * @return The weight of the {@link Variation} in the
		 *         {@link FlameTransformation} at the given index in the
		 *         list
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 If the index is less than zero of greater
		 *                 than the max index of the list
		 */
		public double variationWeight(final int index, final Variation variation) {
			this.checkIndex(index);
			return this.list.get(index).getVariationWeightValue(variation.index());
		}

		/**
		 * Check if an index is in the {@link List}
		 * 
		 * @param index
		 *                The index to check
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 If the index is less than zero of greater
		 *                 than the max index of the list
		 */
		private void checkIndex(final int index) {
			if (index < 0 || index >= this.transformationCount()) {
				throw new IndexOutOfBoundsException();
			}
		}
	}

	/**
	 * An array of index of color for each {@link Transformation}
	 */
	private final double[]			arrayIndex;

	/**
	 * The list of the {@link FlameTransformation} to use in the computation
	 */
	private final List<FlameTransformation>	transformations;

	/**
	 * Construct a {@link Flame} with the given {@link List} of
	 * {@link FlameTransformation}
	 * 
	 * @param transformations
	 *                The {@link FlameTransformation} to use to generate the
	 *                fractal
	 */
	public Flame(final List<FlameTransformation> transformations) {
		this.transformations = new ArrayList<FlameTransformation>(transformations);

		this.arrayIndex = new double[this.transformations.size()];
		switch (this.arrayIndex.length) {

		case 3:
			this.arrayIndex[2] = 0.5;

		case 2:
			this.arrayIndex[1] = 1;

		case 1:
			this.arrayIndex[0] = 0;
			break;

		case 0:
			break;

		default:
			this.arrayIndex[0] = 0;
			this.arrayIndex[1] = 1;
			this.arrayIndex[2] = 0.5;
			for (int i = 3; i < this.arrayIndex.length; i++) {
				final double powLog2 = Math.pow(2, Math.ceil(Math.log(i) / Math.log(2)));
				final double powLogI = Math.pow(2, Math.floor(Math.log(i - 1) / Math.log(2)));
				this.arrayIndex[i] = ((i - 1 - powLogI) * 2 + 1) / powLog2;
			}
			break;
		}
	}

	/**
	 * Compute the fractal, with the given definition (width and height) and
	 * the number of points, and hit the given
	 * {@link ch.epfl.flamemaker.flame.FlameAccumulator.Builder}
	 * 
	 * @param points
	 *                The average number of points to hit
	 * @param image
	 *                The
	 *                {@link ch.epfl.flamemaker.flame.FlameAccumulator.Builder}
	 *                to hit
	 */
	public void compute(final int points, final FlameAccumulator.Builder image) {

		final Random rand = new Random();

		final int totalThreads = Preferences.values.threads;
		final Thread[] threads = new Thread[totalThreads];
		for (int i = 0; i < threads.length; i++) {

			threads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					Point p = Point.ORIGIN;

					// Randomize the point 20 times
					double lastColor = 0;
					for (int j = 0; j < 20; j++) {
						final int i = rand.nextInt(Flame.this.transformations.size());
						p = Flame.this.transformations.get(i).transformPoint(p);
						lastColor = (Flame.this.arrayIndex[i] + lastColor) / 2.0;
					}

					// Actually hit the accumulator
					for (int j = 0; j < points / totalThreads; j++) {
						final int i = rand.nextInt(Flame.this.transformations.size());
						p = Flame.this.transformations.get(i).transformPoint(p);

						lastColor = (Flame.this.arrayIndex[i] + lastColor) / 2.0;

						image.hit(p, lastColor);
					}
				}
			});
		}

		// If we do not have list, just return the image.build()
		if (this.transformations.size() == 0) {
			return;
		}

		for (final Thread thread : threads) {
			thread.start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
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
	public FlameAccumulator compute(final Rectangle frame, final int width, final int height, final int density) {

		final Random rand = new Random();
		final int m = density * width * height;
		final FlameAccumulator.Builder image = new FlameAccumulator.Builder(frame, width, height);

		final int totalThreads = Preferences.values.threads;
		final Thread[] threads = new Thread[totalThreads];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					Point p = Point.ORIGIN;

					// Randomize the point 20 times
					double lastColor = 0;
					for (int j = 0; j < 20; j++) {
						final int i = rand.nextInt(Flame.this.transformations.size());
						p = Flame.this.transformations.get(i).transformPoint(p);
						lastColor = (Flame.this.arrayIndex[i] + lastColor) / 2.0;
					}

					// Actually hit the accumulator
					for (int j = 0; j < m / totalThreads; j++) {
						final int i = rand.nextInt(Flame.this.transformations.size());
						p = Flame.this.transformations.get(i).transformPoint(p);

						lastColor = (Flame.this.arrayIndex[i] + lastColor) / 2.0;

						image.hit(p, lastColor);
					}
				}
			});
		}

		// If we do not have list, just return the image.build()
		if (this.transformations.size() == 0) {
			return image.build();
		}

		for (final Thread thread : threads) {
			thread.start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		return image.build();
	}
}
