package ch.epfl.flamemaker.flame;

import java.util.Arrays;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

/**
 * A Transformation with an array of variation weight and an
 * AffineTransformation used to transform point
 */
public class FlameTransformation implements Transformation {
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
		private final double[]		variationWeight;

		/**
		 * Check given index which must be between 0 and 5 (both
		 * inclusive)
		 * 
		 * @param index
		 *                The index to check
		 * 
		 * @throws IndexOutOfBoundsException
		 *                 if the given index is not between 0 and 5
		 *                 (both included)
		 */
		private static void checkIndex(final int index) {
			if (index < 0 || index >= 6) {
				throw new IndexOutOfBoundsException();
			}
		}

		/**
		 * Construct a new FlameTransformation.Builder with the given
		 * FlameTransformation
		 * 
		 * @param affineTransformation
		 *                The AffineTransformation to use
		 * @param variationWeight
		 *                The weight of every variation
		 */
		public Builder(final AffineTransformation affineTransformation, final double[] variationWeight) {

			FlameTransformation.checkValue(variationWeight);
			this.affineTransformation = affineTransformation;

			if (variationWeight == null || variationWeight.length != 6) {
				throw new IllegalArgumentException();
			}
			this.variationWeight = variationWeight.clone();
		}

		/**
		 * Construct a new FlameTransformation.Builder with the given
		 * FlameTransformation
		 * 
		 * @param flameTransformation
		 *                The FlameTransformation to build upon
		 */
		public Builder(final FlameTransformation flameTransformation) {
			this.affineTransformation = flameTransformation.affineTransformation;
			this.variationWeight = flameTransformation.variationWeight;
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
			if (this.affineTransformation == null) {
				if (other.affineTransformation != null) {
					return false;
				}
			} else if (!this.affineTransformation.equals(other.affineTransformation)) {
				return false;
			}
			if (!Arrays.equals(this.variationWeight, other.variationWeight)) {
				return false;
			}
			return true;
		}

		/**
		 * Return the current AffineTransformation
		 * 
		 * @return The current AffineTransformation
		 */
		public AffineTransformation getAffineTransformation() {
			return this.affineTransformation;
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
		 *                 (both included)
		 */
		public double getVariationWeightValue(final int index) {
			Builder.checkIndex(index);
			return this.variationWeight[index];
		}

		/**
		 * Set the AffineTransformation
		 * 
		 * @param affineTransformation
		 *                The new AffineTransformation
		 */
		public void setAffineTransformation(final AffineTransformation affineTransformation) {
			this.affineTransformation = affineTransformation;
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
		 *                 (both included)
		 */
		public void setVariationWeight(final int index, final double value) {
			Builder.checkIndex(index);
			this.variationWeight[index] = value;
		}
	}

	/**
	 * The AffineTransformation used in every transformPoint
	 */
	private final AffineTransformation	affineTransformation;

	/**
	 * The array of weight for every variations
	 */
	private final double[]			variationWeight;

	/**
	 * Check the given array of variation
	 * 
	 * @param variationWeight
	 *                The weight for every variations
	 * 
	 * @throws IllegalArgumentException
	 *                 if the given variationWeight.lenght != 6 if any value
	 *                 in variationWeight is not between 0 and 1 (both
	 *                 included)
	 * 
	 */
	private static void checkValue(final double[] variationWeight) {
		if (variationWeight == null || variationWeight.length != 6) {
			throw new IllegalArgumentException();
		}
	}

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
	 *                 included)
	 */
	public FlameTransformation(final AffineTransformation affineTransformation, final double[] variationWeight) {
		this.affineTransformation = affineTransformation;

		FlameTransformation.checkValue(variationWeight);

		this.variationWeight = variationWeight.clone();
	}

	@Override
	public Point transformPoint(Point p) {

		final Point initial = new Point(p.x(), p.y());
		Point sum = new Point(0, 0);
		for (int i = 0; i < 6; i++) {
			final double w = this.variationWeight[i];
			if (w != 0) {
				p = this.affineTransformation.transformPoint(initial);
				p = Variation.ALL_VARIATIONS.get(i).transformPoint(p);
				sum = new Point(sum.x() + p.x() * w, sum.y() + p.y() * w);
			}
		}

		return sum;
	}
}
