package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.ifs.IFSAccumulatorBuilder;

public class IFSAccumulatorBuilderTest {

	private Rectangle	frame;

	@Before
	public void setup() {
		final Point centerFrame = new Point(5, 5);
		this.frame = new Rectangle(centerFrame, 10, 10);
	}

	@Test
	public final void testBuild() {
		final IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(this.frame, 1, 1);
		builder.build();
	}

	@Test
	public final void testHit() {

		// Test using a using a frame containing the origin
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(this.frame, 10, 10);

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				Assert.assertFalse(builder.build().isHit(x, y));
			}
		}

		builder.hit(new Point(-1, -1));
		builder.hit(new Point(2, 2));
		builder.hit(new Point(8, 8));
		builder.hit(new Point(10, 10));

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				builder.hit(new Point(x, y));
			}
		}

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				Assert.assertTrue(builder.build().isHit(x, y));
			}
		}

		// Test using not origine-centered frame
		this.frame = new Rectangle(new Point(5, 5), 4, 4);
		builder = new IFSAccumulatorBuilder(this.frame, 8, 8);

		for (int x = 3; x < 7; x++) {
			for (int y = 3; y < 7; y++) {
				Assert.assertFalse(builder.build().isHit(x, y));
			}
		}

		// Some points, just for behavior
		builder.hit(new Point(-1, -1));
		builder.hit(new Point(2, 2));
		builder.hit(new Point(8, 8));
		builder.hit(new Point(10, 10));

		for (double x = 3; x < 7; x += 0.5) {
			for (double y = 3; y < 7; y += 0.5) {
				builder.hit(new Point(x, y));
			}
		}

		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				Assert.assertTrue(builder.build().isHit(x, y));
			}
		}
	}

	@Test
	public final void testIFSAccumulatorBuilder() {
		new IFSAccumulatorBuilder(this.frame, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testIFSAccumulatorBuilderNulHeight() {
		new IFSAccumulatorBuilder(this.frame, 1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testIFSAccumulatorBuilderNulWidth() {
		new IFSAccumulatorBuilder(this.frame, 0, 1);
	}
}
