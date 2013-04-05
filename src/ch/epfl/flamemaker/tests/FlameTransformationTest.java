package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.flamemaker.flame.FlameTransformation;

public class FlameTransformationTest {

	@Test
	public void testFlameTransformation() {
		final double[] variationWeight = { 1, 1, 1, 1, 1, 1 };
		new FlameTransformation(null, variationWeight);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFlameTransformationArrayTooBig() {
		final double[] variationWeight = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		new FlameTransformation(null, variationWeight);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFlameTransformationArrayTooSmall() {
		final double[] variationWeight = { 1 };
		new FlameTransformation(null, variationWeight);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFlameTransformationNull() {
		new FlameTransformation(null, null);
	}

	@Test
	public void testTransformPoint() {
		Assert.fail("Not yet implemented");
	}

}
