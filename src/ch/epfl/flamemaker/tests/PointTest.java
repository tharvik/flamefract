package ch.epfl.flamemaker.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;

public class PointTest {
	private static double DELTA = 0.000000001;

	@Test
	public void testPoint() {
		for (double x = -10; x < 10; x += 0.1) {
			for (double y = -10; y < 10; y += 0.1) {
				new Point(x, y);
			}
		}
	}

	@Test
	public void testX() {
		for (double x = -10; x < 10; x += 0.1) {
			Point p = new Point(x, 0);
			assertEquals(x, p.x(), DELTA);
		}
	}

	@Test
	public void testY() {
		for (double y = -10; y < 10; y += 0.1) {
			Point p = new Point(0, y);
			assertEquals(y, p.y(), DELTA);
		}
	}

	@Test
	public void testR() {
		Point p = new Point(3, 4);
		assertEquals(5, p.r(), DELTA);

		p = new Point(0, 0);
		assertEquals(0, p.r(), DELTA);

		p = new Point(-3, -4);
		assertEquals(5, p.r(), DELTA);
	}

	@Test
	public void testTheta() {
		Point p = new Point(10, 10);
		assertEquals(Math.PI / 4, p.theta(), DELTA);

		p = new Point(0, 0);
		assertEquals(0, p.theta(), DELTA);

		p = new Point(-10, -10);
		assertEquals(Math.PI / 4 - Math.PI, p.theta(), DELTA);
	}

	// Note: we do not test toString, since the string representation of
	// floating-point numbers is hard to predict.
}
