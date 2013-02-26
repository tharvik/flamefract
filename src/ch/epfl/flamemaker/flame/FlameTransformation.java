package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

public class FlameTransformation implements Transformation {
	private final AffineTransformation affineTransformation;
	private final double[] variationWeight;

	public FlameTransformation(AffineTransformation affineTransformation,
			doulbe[] variationWeight) {
		this.affineTransformation = affineTransfromation;

		if (variationWeight.length != 6) {
			throw new IllegalArgumentException();
		}
		this.variationWeight[i] = variationWeight.clone();
	}
}
