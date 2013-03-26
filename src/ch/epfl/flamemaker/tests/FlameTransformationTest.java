package ch.epfl.flamemaker.tests;

import static org.junit.Assert.fail;

import org.junit.Test;

import ch.epfl.flamemaker.flame.FlameTransformation;

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
