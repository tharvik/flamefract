package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.flamemaker.color.Color;

public class ColorTest {

	// delta use for assertEqual(double)
	public static double	DELTA	= 0.000000001;

	@Test
	public void testAsPackedRGB() {
		Color c = new Color(0, 0, 0);
		Assert.assertEquals(0, c.asPackedRGB());

		// 0.5 * 255 raise 0x7F (0.5 * 256 will raise 0x80)
		c = new Color(0.5, 0.5, 0.5);
		Assert.assertEquals(0x808080, c.asPackedRGB());

		c = new Color(1, 1, 1);
		Assert.assertEquals(0xFFFFFF, c.asPackedRGB());

		c = new Color(0, 0x80 / (double) 0xFF, 1);
		Assert.assertEquals(0x0080FF, c.asPackedRGB());
	}

	@Test
	public void testBlue() {
		final Color c = new Color(0, 0, 1);
		Assert.assertEquals(1, c.blue(), ColorTest.DELTA);
	}

	// only work if the class is defined
	@Test
	public void testColor() {
		new Color(0, 0, 0);
		new Color(0.5, 0.5, 0.5);
		new Color(1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorNegativeB() {
		new Color(0, 0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorNegativeG() {
		new Color(0, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorNegativeR() {
		new Color(-1, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorTooBigB() {
		new Color(0, 0, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorTooBigG() {
		new Color(0, 2, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorTooBigR() {
		new Color(2, 0, 0);
	}

	@Test
	public void testGreen() {
		final Color c = new Color(0, 1, 0);
		Assert.assertEquals(1, c.green(), ColorTest.DELTA);
	}

	@Test
	public void testMixWith() {
		final Color c = new Color(1, 1, 1);
		final Color mix = c.mixWith(new Color(0, 1, 0), 0.6);

		Assert.assertEquals(mix.red(), 0.4, ColorTest.DELTA);
		Assert.assertEquals(mix.green(), 1, ColorTest.DELTA);
		Assert.assertEquals(mix.blue(), 0.4, ColorTest.DELTA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMixWithNegativeProportion() {
		final Color c = new Color(0, 0, 0);
		c.mixWith(new Color(0, 0, 0), -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMixWithTooBigProportion() {
		final Color c = new Color(0, 0, 0);
		c.mixWith(new Color(0, 0, 0), 2);
	}

	@Test
	public void testRed() {
		final Color c = new Color(1, 0, 0);
		Assert.assertEquals(1, c.red(), ColorTest.DELTA);
	}

	@Test
	public void testSRGBEncode() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		Color c = new Color(0, 0, 0);
		Assert.assertEquals("(0.0,0.0,0.0)", c.toString());

		c = new Color(0.5, 0.5, 0.5);
		Assert.assertEquals("(0.5,0.5,0.5)", c.toString());

		c = new Color(1, 1, 1);
		Assert.assertEquals("(1.0,1.0,1.0)", c.toString());

		c = new Color(0, 0.5, 1);
		Assert.assertEquals("(0.0,0.5,1.0)", c.toString());
	}
}
