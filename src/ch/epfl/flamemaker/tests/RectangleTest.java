package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class RectangleTest {
	private static double	DELTA	= 0.000000001;

	@Test
	public void testAspectRatio() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 16, 9);
		Assert.assertEquals(16.0 / 9.0, r.aspectRatio(), RectangleTest.DELTA);
	}

	@Test
	public void testBottom() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		Assert.assertEquals(-1, r.bottom(), RectangleTest.DELTA);
	}

	@Test
	public void testCenter() {
		final Rectangle r = new Rectangle(new Point(1, 2), 1, 1);
		final Point c = r.center();
		Assert.assertEquals(1, c.x(), RectangleTest.DELTA);
		Assert.assertEquals(2, c.y(), RectangleTest.DELTA);
	}

	@Test
	public void testContains() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		for (double x = -1; x < 1; x += 0.1) {
			for (double y = -1; y < 1; y += 0.1) {
				Assert.assertTrue(r.contains(new Point(x, y)));
			}
		}
		Assert.assertTrue(r.contains(new Point(0.99999999, 0.99999999)));
		for (double x = -2; x <= 2; x += 0.1) {
			Assert.assertFalse(r.contains(new Point(x, 1)));
		}
		for (double y = -2; y <= 2; y += 0.1) {
			Assert.assertFalse(r.contains(new Point(1, y)));
		}
	}

	@Test
	public void testExpandToAspectRatio() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 1, 1);
		final double[] newAspectRatios = new double[] { 0.5, 1, 2 };

		for (final double newAR : newAspectRatios) {
			final Rectangle r1 = r.expandToAspectRatio(newAR);

			// The expanded rectangle must:
			// 1. have the requested aspect ratio
			Assert.assertEquals(r1.aspectRatio(), newAR, RectangleTest.DELTA);

			// 2. have the same center as the original
			Assert.assertEquals(r.center().x(), r1.center().x(), RectangleTest.DELTA);
			Assert.assertEquals(r.center().y(), r1.center().y(), RectangleTest.DELTA);

			// 3. have at least one side of the same length
			Assert.assertTrue(Math.abs(r.width() - r1.width()) <= RectangleTest.DELTA
					|| Math.abs(r.height() - r1.height()) <= RectangleTest.DELTA);

			// 4. not have any side of a shorter length.
			Assert.assertFalse(r1.width() < r.width());
			Assert.assertFalse(r1.height() < r.height());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExpandToAspectRatioNegativeRatio() {
		(new Rectangle(Point.ORIGIN, 1, 1)).expandToAspectRatio(-12);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExpandToAspectRatioZeroRatio() {
		(new Rectangle(Point.ORIGIN, 1, 1)).expandToAspectRatio(0);
	}

	@Test
	public void testHeight() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 1, 10);
		Assert.assertEquals(10, r.height(), RectangleTest.DELTA);
	}

	@Test
	public void testLeft() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		Assert.assertEquals(-1, r.left(), RectangleTest.DELTA);
	}

	@Test
	public void testRectangle() {
		new Rectangle(Point.ORIGIN, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNegativeHeight() {
		new Rectangle(Point.ORIGIN, 10, -0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNegativeWidth() {
		new Rectangle(Point.ORIGIN, -0.1, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNulHeight() {
		new Rectangle(Point.ORIGIN, 0, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNulWidth() {
		new Rectangle(Point.ORIGIN, 0, 10);
	}

	@Test
	public void testRight() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		Assert.assertEquals(1, r.right(), RectangleTest.DELTA);
	}

	@Test
	public void testTop() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		Assert.assertEquals(1, r.top(), RectangleTest.DELTA);
	}

	@Test
	public void testWidth() {
		final Rectangle r = new Rectangle(Point.ORIGIN, 10, 1);
		Assert.assertEquals(10, r.width(), RectangleTest.DELTA);
	}

	// Note: we do not test toString, since the string representation of
	// floating-point numbers is hard to predict.
}
