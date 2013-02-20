package ch.epfl.flamemaker.ifs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IFSAccumulatorTest {

	private boolean[][] isHit;

	@Before
	public void setup() {
		this.isHit = new boolean[2][2];

		this.isHit[1][0] = true;
		this.isHit[1][1] = true;
	}

	@Test
	public final void testIFSAccumulator() {
		new IFSAccumulator(isHit);
	}

	@Test(expected = NullPointerException.class)
	public final void testIFSAccumulatorNull() {
		new IFSAccumulator(null);
	}

	@Test
	public final void testWidth() {
		IFSAccumulator accu = new IFSAccumulator(isHit);
		assertEquals(2, accu.width());
	}

	@Test
	public final void testHeight() {
		IFSAccumulator accu = new IFSAccumulator(isHit);
		assertEquals(2, accu.height());
	}

	@Test
	public final void testIsHit() {
		IFSAccumulator accu = new IFSAccumulator(isHit);

		assertFalse(accu.isHit(0, 0));
		assertFalse(accu.isHit(0, 1));
		assertTrue(accu.isHit(1, 0));
		assertTrue(accu.isHit(1, 1));
	}

}
