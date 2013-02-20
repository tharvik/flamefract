package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {
	private static double DELTA = 0.000000001;

	@Test
	public void testPoint() {
		new Point(0, 0);
	}

	@Test
	public void testX() {
		Point p = new Point(10, 0);
		assertEquals(10, p.x(), DELTA);
	}

	@Test
	public void testY() {
		Point p = new Point(0, 10);
		assertEquals(10, p.y(), DELTA);
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
