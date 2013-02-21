package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Test;

public class AffineTransformationTest {
	private static double DELTA = 0.000000001;

	@Test
	public void testAffineTransformation() {
		new AffineTransformation(0, 0, 0, 0, 0, 0);
	}

	@Test
	public void testTransformPoint() {
		// Test 1
		AffineTransformation matrice = new AffineTransformation(0, 0, 0, 0, 0,
				0);
		Point point = new Point(1, 2);
		Point point2 = matrice.transformPoint(point);
		Point ref = new Point(0, 0);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);

		// Test 2
		matrice = new AffineTransformation(1, 1, 1, 1, 1, 1);
		point2 = matrice.transformPoint(point);
		ref = new Point(4, 4);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);

	}

	@Test
	public void testNewTranslation() {
		Point ref = new Point(43, 42);
		AffineTransformation matrice = AffineTransformation.newTranslation(42,
				42);

		Point point = new Point(1, 0);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testNewRotation() {
		Point ref = new Point(-1, 1);
		AffineTransformation matrice = AffineTransformation
				.newRotation(Math.PI / 2);

		Point point = new Point(1, 1);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testNewScaling() {
		Point ref = new Point(2, 2);
		AffineTransformation matrice = AffineTransformation.newScaling(2, 2);

		Point point = new Point(1, 1);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testnewShearX() {
		Point ref = new Point(3, 1);
		AffineTransformation matrice = AffineTransformation.newShearX(2);

		Point point = new Point(1, 1);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testnewShearY() {
		Point ref = new Point(1, 3);
		AffineTransformation matrice = AffineTransformation.newShearY(2);

		Point point = new Point(1, 1);
		Point point2 = matrice.transformPoint(point);
		assertEquals(ref.x(), point2.x(), DELTA);
		assertEquals(ref.y(), point2.y(), DELTA);
	}

	@Test
	public void testTranslationX() {
		double ref = 4;
		AffineTransformation matrice = new AffineTransformation(0, 0, 4, 0, 0,
				0);

		assertEquals(ref, matrice.translationX(), DELTA);
	}

	@Test
	public void testTranslationY() {
		double ref = 4;
		AffineTransformation matrice = new AffineTransformation(0, 0, 0, 0, 0,
				4);

		assertEquals(ref, matrice.translationY(), DELTA);
	}

	@Test
	public void testComposeWith() {
		AffineTransformation matrice = new AffineTransformation(1, 1, 1, 0, 0,
				0);
		AffineTransformation test = new AffineTransformation(2, 2, 2, 3, 4, 5);

		Point point = new Point(1, 1);
		test = matrice.composeWith(test);
		point = test.transformPoint(point);

		assertEquals(19, point.x(), DELTA);
		assertEquals(0, point.y(), DELTA);

		// Test with identity
		matrice = AffineTransformation.IDENTITY;
		test = AffineTransformation.IDENTITY;
		
		test = matrice.composeWith(test);		
		point = test.transformPoint(new Point(1, 1));

		assertEquals(1, point.x(), DELTA);
		assertEquals(1, point.y(), DELTA);
	}
}