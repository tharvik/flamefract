package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;

public class AffineTransformationTest {
	private static double	DELTA	= 0.000000001;

	@Test
	public void testAffineTransformation() {
		new AffineTransformation(0, 0, 0, 0, 0, 0);
	}

	@Test
	public void testComposeWith() {
		AffineTransformation matrice = new AffineTransformation(1, 1, 1, 0, 0, 0);
		AffineTransformation test = new AffineTransformation(2, 2, 2, 3, 4, 5);

		Point point = new Point(1, 1);
		test = matrice.composeWith(test);
		point = test.transformPoint(point);

		Assert.assertEquals(19, point.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(0, point.y(), AffineTransformationTest.DELTA);

		// Test with identity
		matrice = AffineTransformation.IDENTITY;
		test = AffineTransformation.IDENTITY;

		test = matrice.composeWith(test);
		point = test.transformPoint(new Point(1, 1));

		Assert.assertEquals(1, point.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(1, point.y(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testNewRotation() {
		final Point ref = new Point(-1, 1);
		final AffineTransformation matrice = AffineTransformation.newRotation(Math.PI / 2);

		final Point point = new Point(1, 1);
		final Point point2 = matrice.transformPoint(point);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testNewScaling() {
		final Point ref = new Point(2, 2);
		final AffineTransformation matrice = AffineTransformation.newScaling(2, 2);

		final Point point = new Point(1, 1);
		final Point point2 = matrice.transformPoint(point);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testnewShearX() {
		final Point ref = new Point(3, 1);
		final AffineTransformation matrice = AffineTransformation.newShearX(2);

		final Point point = new Point(1, 1);
		final Point point2 = matrice.transformPoint(point);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testnewShearY() {
		final Point ref = new Point(1, 3);
		final AffineTransformation matrice = AffineTransformation.newShearY(2);

		final Point point = new Point(1, 1);
		final Point point2 = matrice.transformPoint(point);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testNewTranslation() {
		final Point ref = new Point(43, 42);
		final AffineTransformation matrice = AffineTransformation.newTranslation(42, 42);

		final Point point = new Point(1, 0);
		final Point point2 = matrice.transformPoint(point);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testTransformPoint() {
		// Test 1
		AffineTransformation matrice = new AffineTransformation(0, 0, 0, 0, 0, 0);
		final Point point = new Point(1, 2);
		Point point2 = matrice.transformPoint(point);
		Point ref = new Point(0, 0);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);

		// Test 2
		matrice = new AffineTransformation(1, 1, 1, 1, 1, 1);
		point2 = matrice.transformPoint(point);
		ref = new Point(4, 4);
		Assert.assertEquals(ref.x(), point2.x(), AffineTransformationTest.DELTA);
		Assert.assertEquals(ref.y(), point2.y(), AffineTransformationTest.DELTA);

	}

	@Test
	public void testTranslationX() {
		final double ref = 4;
		final AffineTransformation matrice = new AffineTransformation(0, 0, 4, 0, 0, 0);

		Assert.assertEquals(ref, matrice.translationX(), AffineTransformationTest.DELTA);
	}

	@Test
	public void testTranslationY() {
		final double ref = 4;
		final AffineTransformation matrice = new AffineTransformation(0, 0, 0, 0, 0, 4);

		Assert.assertEquals(ref, matrice.translationY(), AffineTransformationTest.DELTA);
	}
}