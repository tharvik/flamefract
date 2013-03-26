package ch.epfl.flamemaker.testSuite;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.Point;

public class VariationTest {

	private Point	point;

	@Before
	public void setUp() {
		this.point = new Point(1, 1);
	}

	@Test
	public void testVariation() {
		assertEquals(6, Variation.ALL_VARIATIONS.size());
	}

	@Test
	public void testName() {
		for (Variation v : Variation.ALL_VARIATIONS) {
			assertFalse(v.name().isEmpty());
		}
	}

	@Test
	public void testIndex() {
		for (int i = 0; i < 6; i++) {
			assertEquals(i, Variation.ALL_VARIATIONS.get(i).index());
		}
	}

	@Test
	public void testTransformPointLinear() {
		Point p = Variation.ALL_VARIATIONS.get(0).transformPoint(this.point);
		
		assertTrue(this.point.equals(p));
	}
	
	// Arrays.asList(new Variation(0, "Linear") {
	// public Point transformPoint(Point p) {
	// return p;
	// }
	// }, new Variation(1, "Sinusoidal") {
	// public Point transformPoint(Point p) {
	// double x = Math.sin(p.x());
	// double y = Math.sin(p.y());
	// return new Point(x, y);
	// }
	// }, new Variation(2, "Spherical") {
	// public Point transformPoint(Point p) {
	// double x = p.x() / Math.pow(p.r(), 2);
	// double y = p.y() / Math.pow(p.r(), 2);
	// return new Point(x, y);
	// }
	// }, new Variation(3, "Swirl") {
	// public Point transformPoint(Point p) {
	// double x = p.x()
	// * Math.sin(Math.pow(
	// p.r(),
	// 2))
	// - p.y()
	// * Math.cos(Math.pow(
	// p.r(),
	// 2));
	// double y = p.x()
	// * Math.cos(Math.pow(
	// p.r(),
	// 2))
	// + p.y()
	// * Math.sin(Math.pow(
	// p.r(),
	// 2));
	// return new Point(x, y);
	// }
	// }, new Variation(4, "Horseshoe") {
	// public Point transformPoint(Point p) {
	// double x = (p.x() - p.y())
	// * (p.x() + p.y())
	// / p.r();
	// double y = 2 * p.x() * p.y() / p.r();
	// return new Point(x, y);
	// }
	// }, new Variation(5, "Bubble") {
	// public Point transformPoint(Point p) {
	// double x = 4
	// * p.x()
	// / (Math.pow(p.r(), 2) + 4);
	// double y = 4
	// * p.y()
	// / (Math.pow(p.r(), 2) + 4);
	// return new Point(x, y);
	// }
	// });

}
