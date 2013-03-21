package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

public class FlameTransformation implements Transformation {
	private final AffineTransformation	affineTransformation;
	private final double[]			variationWeight;

	public FlameTransformation(AffineTransformation affineTransformation, double[] variationWeight) {
		this.affineTransformation = affineTransformation;

		if (variationWeight == null || variationWeight.length != 6) {
			throw new IllegalArgumentException();
		}
		this.variationWeight = variationWeight.clone();
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

	public static class Builder {
		private AffineTransformation	affineTransformation;
		private double[]		variationWeight;

		public Builder(FlameTransformation flameTransformation) {
			this.affineTransformation = flameTransformation.affineTransformation;
			this.variationWeight = flameTransformation.variationWeight;
		}

		public Builder(AffineTransformation affineTransformation, double[] variationWeight) {
			this.affineTransformation = affineTransformation;

			if (variationWeight == null || variationWeight.length != 6) {
				throw new IllegalArgumentException();
			}
			this.variationWeight = variationWeight.clone();
		}

		private void checkIndex(int index) {
			if (index < 0 || index > 6) {
				throw new IndexOutOfBoundsException();
			}
		}

		public void setVariationWeight(int index, double value) {

			// TODO negative value?
			checkIndex(index);

			this.variationWeight[index] = value;
		}

		public void setAffineTransformation(AffineTransformation affineTransformation) {
			this.affineTransformation = affineTransformation;
		}

		public AffineTransformation getAffineTransformation() {
			return affineTransformation;
		}

		public double getVariationWeightValue(int index) {
			checkIndex(index);
			return variationWeight[index];
		}

		public FlameTransformation build() {
			return new FlameTransformation(this.affineTransformation, this.variationWeight);
		}
	}
}
