package ch.epfl.flamemaker.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class RectangleTest {
	private static double	DELTA	= 0.000000001;
	
	@Test
	public void testRectangle() {
		new Rectangle(Point.ORIGIN, 1, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNulWidth() {
		new Rectangle(Point.ORIGIN, 0, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNulHeight() {
		new Rectangle(Point.ORIGIN, 0, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNegativeWidth() {
		new Rectangle(Point.ORIGIN, -0.1, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRectangleNegativeHeight() {
		new Rectangle(Point.ORIGIN, 10, -0.1);
	}
	
	@Test
	public void testWidth() {
		Rectangle r = new Rectangle(Point.ORIGIN, 10, 1);
		assertEquals(10, r.width(), DELTA);
	}
	
	@Test
	public void testHeight() {
		Rectangle r = new Rectangle(Point.ORIGIN, 1, 10);
		assertEquals(10, r.height(), DELTA);
	}
	
	@Test
	public void testCenter() {
		Rectangle r = new Rectangle(new Point(1, 2), 1, 1);
		Point c = r.center();
		assertEquals(1, c.x(), DELTA);
		assertEquals(2, c.y(), DELTA);
	}
	
	@Test
	public void testLeft() {
		Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		assertEquals(-1, r.left(), DELTA);
	}
	
	@Test
	public void testRight() {
		Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		assertEquals(1, r.right(), DELTA);
	}
	
	@Test
	public void testBottom() {
		Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		assertEquals(-1, r.bottom(), DELTA);
	}
	
	@Test
	public void testTop() {
		Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		assertEquals(1, r.top(), DELTA);
	}
	
	@Test
	public void testAspectRatio() {
		Rectangle r = new Rectangle(Point.ORIGIN, 16, 9);
		assertEquals(16.0 / 9.0, r.aspectRatio(), DELTA);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testExpandToAspectRatioZeroRatio() {
		(new Rectangle(Point.ORIGIN, 1, 1)).expandToAspectRatio(0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testExpandToAspectRatioNegativeRatio() {
		(new Rectangle(Point.ORIGIN, 1, 1)).expandToAspectRatio(-12);
	}
	
	@Test
	public void testExpandToAspectRatio() {
		Rectangle r = new Rectangle(Point.ORIGIN, 1, 1);
		double[] newAspectRatios = new double[] { 0.5, 1, 2 };
		
		for (double newAR : newAspectRatios) {
			Rectangle r1 = r.expandToAspectRatio(newAR);
			
			// The expanded rectangle must:
			// 1. have the requested aspect ratio
			assertEquals(r1.aspectRatio(), newAR, DELTA);
			
			// 2. have the same center as the original
			assertEquals(r.center().x(), r1.center().x(), DELTA);
			assertEquals(r.center().y(), r1.center().y(), DELTA);
			
			// 3. have at least one side of the same length
			assertTrue(Math.abs(r.width() - r1.width()) <= DELTA
					|| Math.abs(r.height() - r1.height()) <= DELTA);
			
			// 4. not have any side of a shorter length.
			assertFalse(r1.width() < r.width());
			assertFalse(r1.height() < r.height());
		}
	}
	
	@Test
	public void testContains() {
		Rectangle r = new Rectangle(Point.ORIGIN, 2, 2);
		for (double x = -1; x < 1; x += 0.1)
			for (double y = -1; y < 1; y += 0.1)
				assertTrue(r.contains(new Point(x, y)));
		assertTrue(r.contains(new Point(0.99999999, 0.99999999)));
		for (double x = -2; x <= 2; x += 0.1)
			assertFalse(r.contains(new Point(x, 1)));
		for (double y = -2; y <= 2; y += 0.1)
			assertFalse(r.contains(new Point(1, y)));
	}
	
	// Note: we do not test toString, since the string representation of
	// floating-point numbers is hard to predict.
}
