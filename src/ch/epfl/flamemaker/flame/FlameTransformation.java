package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

/**
 * A Transformation with an array of variation weight and a AffineTransformation
 * used to transform point
 */
public class FlameTransformation implements Transformation {
	/**
	 * The AffineTransformation used in every transformPoint
	 */
	private final AffineTransformation	affineTransformation;

	/**
	 * The array of weight for every variations
	 */
	private final double[]			variationWeight;

	/**
	 * Construct a new FlameTransformation with the given affine
	 * transformation and the weight of every variation
	 * 
	 * @param affineTransformation
	 *                The affine transformation used in every computation
	 * 
	 * @param variationWeight
	 *                The weight for every variations
	 * 
	 * @throws IllegalArgumentException
	 *                 if the given variationWeight.lenght != 6 if any value
	 *                 in variationWeight is not between 0 and 1 (both
	 *                 inclusive)
	 */
	public FlameTransformation(AffineTransformation affineTransformation, double[] variationWeight) {
		this.affineTransformation = affineTransformation;

		FlameTransformation.checkValue(variationWeight);

		this.variationWeight = variationWeight.clone();
	}

	/**
	 * Check the given array of variation
	 * 
	 * @param variationWeight
	 *                The weight for every variations
	 * 
	 * @throws IllegalArgumentException
	 *                 if the given variationWeight.lenght != 6 if any value
	 *                 in variationWeight is not between 0 and 1 (both
	 *                 inclusive)
	 * 
	 */
	private static void checkValue(double[] variationWeight) {
		for (double d : variationWeight) {
			if (d < 0 || d > 1) {
				throw new IllegalArgumentException();
			}
		}

		if (variationWeight == null || variationWeight.length != 6) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Point transformPoint(Point p) {

		Point initial = new Point(p.x(), p.y());
		Point sum = new Point(0, 0);
		for (int i = 0; i < 6; i++) {
			double w = this.variationWeight[i];
			p = this.affineTransformation.transformPoint(initial);
			p = Variation.ALL_VARIATIONS.get(i).transformPoint(p);
			sum = new Point(sum.x() + p.x() * w, sum.y() + p.y() * w);
		}

		return sum;
	}

	/**
	 * Build for FlameTransformation which allow to change parts of the
	 * transformations
	 */
	public static class Builder {

		/**
		 * The AffineTransformation used in every transformPoint
		 */
		private AffineTransformation	affineTransformation;

		/**
		 * The array of weight for every variations
		 */
		private double[]		variationWeight;

		/**
		 * Construct a new FlameTransformation.Builder with the given
		 * FlameTransformation
		 * 
		 * @param flameTransformation
		 *                The FlameTransformation to build upon
		 */
		public Builder(FlameTransformation flameTransformation) {
			this.affineTransformation = flameTransformation.affineTransformation;
			this.variationWeight = flameTransformation.variationWeight;
		}

		/**
		 * Construct a new FlameTransformation.Builder with the given
		 * FlameTransformation
		 * 
		 * @param affineTransformation
		 *                The AffineTransformation to use
		 * @param variationWeight
		 *                The weigth of every variation
		 */
		public Builder(AffineTransformation affineTransformation, double[] variationWeight) {

			FlameTransformation.checkValue(variationWeight);
			this.affineTransformation = affineTransformation;

			if (variationWeight == null || variationWeight.length != 6) {
				throw new IllegalArgumentException();
			}
			this.variationWeight = variationWeight.clone();
		}

		/**
		 * Check given index which must be between 0 and 5 (both
		 * inclusive)
		 * 
		 * @param index
		 *                The index to check
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 if the given index is not between 0 and 5
		 *                 (both inclusive)
		 */
		private static void checkIndex(int index) {
			if (index < 0 || index >= 6) {
				throw new IndexOutOfBoundsException();
			}
		}

		/**
		 * Set the weight in the array of variation at the given index
		 * 
		 * @param index
		 *                The index in the array of variation
		 * 
		 * @param value
		 *                The new weight
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 if the given index is not between 0 and 5
		 *                 (both inclusive)
		 * 
		 * @throws IllegalArgumentException
		 *                 if the given value is not between 0 and 1
		 *                 (both inclusive)
		 */
		public void setVariationWeight(int index, double value) {
			checkIndex(index);

			if (value < 0 || value > 1) {
				throw new IllegalArgumentException();
			}

			this.variationWeight[index] = value;
		}

		/**
		 * Set the AffineTransformation
		 * 
		 * @param affineTransformation
		 *                The new AffineTransformation
		 */
		public void setAffineTransformation(AffineTransformation affineTransformation) {
			this.affineTransformation = affineTransformation;
		}

		/**
		 * Return the current AffineTransformation
		 * 
		 * @return The current AffineTransformation
		 */
		public AffineTransformation getAffineTransformation() {
			return affineTransformation;
		}

		/**
		 * Return the weight in the variation array at the given index
		 * 
		 * @param index
		 *                The index to get
		 * 
		 * @return The weight in the variation array at the given index
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 if the given index is not between 0 and 5
		 *                 (both inclusive)
		 */
		public double getVariationWeightValue(int index) {
			checkIndex(index);
			return variationWeight[index];
		}

		/**
		 * Return a FlameTransformation with the current state of the
		 * Builder
		 * 
		 * @return A FlameTransformation with the current state of the
		 *         Builder
		 */
		public FlameTransformation build() {
			return new FlameTransformation(this.affineTransformation, this.variationWeight);
		}
	}
}
