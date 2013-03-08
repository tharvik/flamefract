package ch.epfl.flamemaker.color;

import static org.junit.Assert.*;

import org.junit.Test;

public class ColorTest {

	public static double	DELTA	= 0.000000001;

	@Test
	public void testColor() {
		new Color(0, 0, 0);
		new Color(0.5, 0.5, 0.5);
		new Color(1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorNegativeR() {
		new Color(-1, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorNegativeG() {
		new Color(0, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorNegativeB() {
		new Color(0, 0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorTooBigR() {
		new Color(2, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorTooBigG() {
		new Color(0, 2, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorTooBigB() {
		new Color(0, 0, 2);
	}

	@Test
	public void testRed() {
		Color c = new Color(1, 0, 0);
		assertEquals(1, c.red(), ColorTest.DELTA);
	}

	@Test
	public void testGreen() {
		Color c = new Color(0, 1, 0);
		assertEquals(1, c.green(), ColorTest.DELTA);
	}

	@Test
	public void testBlue() {
		Color c = new Color(0, 0, 1);
		assertEquals(1, c.blue(), ColorTest.DELTA);
	}

	@Test
	public void testMixWith() {
		Color c = new Color(1, 1, 1);
		Color mix = c.mixWith(new Color(0, 1, 0), 0.6);

		assertEquals(mix.red(), 0.4, ColorTest.DELTA);
		assertEquals(mix.green(), 1, ColorTest.DELTA);
		assertEquals(mix.blue(), 0.4, ColorTest.DELTA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMixWithNegativeProportion() {
		Color c = new Color(0, 0, 0);
		c.mixWith(new Color(0, 0, 0), -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMixWithTooBigProportion() {
		Color c = new Color(0, 0, 0);
		c.mixWith(new Color(0, 0, 0), 2);
	}

	@Test
	public void testAsPackedRGB() {
		Color c = new Color(0, 0, 0);
		assertEquals(0, c.asPackedRGB());

		// TODO Not working by double ?
		c = new Color(0.5, 0.5, 0.5);
		assertEquals(0x808080, c.asPackedRGB());

		c = new Color(1, 1, 1);
		assertEquals(0xFFFFFF, c.asPackedRGB());

		c = new Color(0, 0.5, 1);
		assertEquals(0x0080FF, c.asPackedRGB());
	}

	@Test
	public void testSRGBEncode() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		Color c = new Color(0, 0, 0);
		assertEquals("(0.0,0.0,0.0)", c.toString());

		c = new Color(0.5, 0.5, 0.5);
		assertEquals("(0.5,0.5,0.5)", c.toString());

		c = new Color(1, 1, 1);
		assertEquals("(1.0,1.0,1.0)", c.toString());

		c = new Color(0, 0.5, 1);
		assertEquals("(0.0,0.5,1.0)", c.toString());
	}

	@Test
	public void testEqualsObject() {

		for (double r = 0; r <= 1; r += 0.1) {
			for (double g = 0; g <= 1; g += 0.1) {
				for (double b = 0; b <= 1; b += 0.1) {
					Color c1 = new Color(r, g, b);
					Color c2 = new Color(r, g, b);

					assertTrue(c1.equals(c2));
					assertTrue(c2.equals(c1));
				}
			}
		}
	}
}
