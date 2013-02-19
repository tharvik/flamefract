package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Test;

public class AffineTransformationTest {
	private static double DELTA = 0.000000001;

	@Test
	public void testAffineTransformation() {
		new AffineTransformation(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	}

	@Test
	public void testTransformPoint() {
		// Test 1
		AffineTransformation matrice = new AffineTransformation(0.0, 0.0, 0.0,
				0.0, 0.0, 0.0);
		Point point = new Point(1.0, 2.0);
		Point point2 = matrice.transformPoint(point);
		Point ref = new Point(0.0, 0.0);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);

		// Test 2
		matrice = new AffineTransformation(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		point2 = matrice.transformPoint(point);
		ref = new Point(4.0, 4.0);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);

	}

	@Test
	public void testNewTranslation() {
		Point ref = new Point(43, 42);
		AffineTransformation matrice = AffineTransformation.newTranslation(
				42.0, 42.0);

		Point point = new Point(1.0, 0.0);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testNewRotation() {
		Point ref = new Point(-1.0, 1.0);
		AffineTransformation matrice = AffineTransformation
				.newRotation(Math.PI / 2);

		Point point = new Point(1.0, 1.0);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testNewScaling() {
		Point ref = new Point(2.0, 2.0);
		AffineTransformation matrice = AffineTransformation
				.newScaling(2.0, 2.0);

		Point point = new Point(1.0, 1.0);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testnewShearX() {
		Point ref = new Point(3.0, 1.0);
		AffineTransformation matrice = AffineTransformation.newShearX(2.0);

		Point point = new Point(1.0, 1.0);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testnewShearY() {
		Point ref = new Point(1.0, 3.0);
		AffineTransformation matrice = AffineTransformation.newShearY(2.0);

		Point point = new Point(1.0, 1.0);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testTranslationX() {
		double ref = 4.0;
		AffineTransformation matrice = new AffineTransformation(0.0, 0.0, 4.0,
				0.0, 0.0, 0.0);

		assertEquals(ref, matrice.translationX(), DELTA);
	}

	@Test
	public void testTranslationY() {
		double ref = 4.0;
		AffineTransformation matrice = new AffineTransformation(0.0, 0.0, 0.0,
				0.0, 0.0, 4.0);

		assertEquals(ref, matrice.translationY(), DELTA);
	}

	@Test
	public void testComposeWith() {
		AffineTransformation matrice = new AffineTransformation(1.0, 1.0, 1.0,
				0.0, 0.0, 0.0);
		AffineTransformation test = new AffineTransformation(2.0, 2.0, 2.0,
				3.0, 4.0, 5.0);
		Point ref = new Point(19.0, 0.0);

		Point point = new Point(1.0, 1.0);
		test = matrice.composeWith(test);
		Point point2 = test.transformPoint(point);

		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);

	}
}
