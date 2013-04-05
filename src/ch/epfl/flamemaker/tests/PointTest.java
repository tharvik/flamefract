package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;

public class PointTest {
	private static double	DELTA	= 0.000000001;

	@Test
	public void testPoint() {
		for (double x = -10; x < 10; x += 0.1) {
			for (double y = -10; y < 10; y += 0.1) {
				new Point(x, y);
			}
		}
	}

	@Test
	public void testR() {
		Point p = new Point(3, 4);
		Assert.assertEquals(5, p.r(), PointTest.DELTA);

		p = new Point(0, 0);
		Assert.assertEquals(0, p.r(), PointTest.DELTA);

		p = new Point(-3, -4);
		Assert.assertEquals(5, p.r(), PointTest.DELTA);
	}

	@Test
	public void testTheta() {
		Point p = new Point(10, 10);
		Assert.assertEquals(Math.PI / 4, p.theta(), PointTest.DELTA);

		p = new Point(0, 0);
		Assert.assertEquals(0, p.theta(), PointTest.DELTA);

		p = new Point(-10, -10);
		Assert.assertEquals(Math.PI / 4 - Math.PI, p.theta(), PointTest.DELTA);
	}

	@Test
	public void testX() {
		for (double x = -10; x < 10; x += 0.1) {
			final Point p = new Point(x, 0);
			Assert.assertEquals(x, p.x(), PointTest.DELTA);
		}
	}

	@Test
	public void testY() {
		for (double y = -10; y < 10; y += 0.1) {
			final Point p = new Point(0, y);
			Assert.assertEquals(y, p.y(), PointTest.DELTA);
		}
	}

	// Note: we do not test toString, since the string representation of
	// floating-point numbers is hard to predict.
}
