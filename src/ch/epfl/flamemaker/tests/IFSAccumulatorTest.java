package ch.epfl.flamemaker.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.flamemaker.ifs.IFSAccumulator;

public class IFSAccumulatorTest {

	private boolean[][]	isHit;

	@Before
	public void setup() {
		this.isHit = new boolean[2][2];

		this.isHit[1][0] = true;
		this.isHit[1][1] = true;
	}

	@Test
	public final void testHeight() {
		final IFSAccumulator accu = new IFSAccumulator(this.isHit);
		Assert.assertEquals(2, accu.height());
	}

	@Test
	public final void testIFSAccumulator() {
		new IFSAccumulator(this.isHit);
	}

	@Test(expected = NullPointerException.class)
	public final void testIFSAccumulatorNull() {
		new IFSAccumulator(null);
	}

	@Test
	public final void testIsHit() {
		final IFSAccumulator accu = new IFSAccumulator(this.isHit);

		Assert.assertFalse(accu.isHit(0, 0));
		Assert.assertFalse(accu.isHit(0, 1));
		Assert.assertTrue(accu.isHit(1, 0));
		Assert.assertTrue(accu.isHit(1, 1));
	}

	@Test
	public final void testWidth() {
		final IFSAccumulator accu = new IFSAccumulator(this.isHit);
		Assert.assertEquals(2, accu.width());
	}

}
