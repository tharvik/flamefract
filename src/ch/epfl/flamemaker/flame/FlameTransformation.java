package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

public class FlameTransformation implements Transformation {
	private final AffineTransformation	affineTransformation;
	private final double[]			variationWeight;

	public FlameTransformation(AffineTransformation affineTransformation, double[] variationWeight) {
		this.affineTransformation = affineTransformation;

		if (variationWeight.length != 6) {
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
}
