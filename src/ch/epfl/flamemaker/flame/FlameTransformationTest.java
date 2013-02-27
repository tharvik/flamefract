package ch.epfl.flamemaker.flame;

import static org.junit.Assert.*;

import java.awt.geom.AffineTransform;

import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;

public class FlameTransformationTest {

	@Test
	public void testFlameTransformation() {
		double[] variationWeight = { 1, 1, 1, 1, 1, 1 };
		new FlameTransformation(null, variationWeight);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFlameTransformationArrayTooSmall() {
		double[] variationWeight = { 1 };
		new FlameTransformation(null, variationWeight);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFlameTransformationArrayTooBig() {
		double[] variationWeight = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		new FlameTransformation(null, variationWeight);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFlameTransformationNull() {
		new FlameTransformation(null, null);
	}

	@Test
	public void testTransformPoint() {
		fail("Not yet implemented");
	}

}
