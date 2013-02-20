package ch.epfl.flamemaker.ifs;

import static org.junit.Assert.*;
import org.junit.*;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilderTest {

	private Rectangle frame;

	@Before
	public void setup() {
		Point centerFrame = new Point(0, 0);
		this.frame = new Rectangle(centerFrame, 1, 1);
	}

	@Test
	public final void testIFSAccumulatorBuilder() {
		new IFSAccumulatorBuilder(this.frame, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testIFSAccumulatorBuilderNulWidth() {
		new IFSAccumulatorBuilder(this.frame, 0, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testIFSAccumulatorBuilderNulHeight() {
		new IFSAccumulatorBuilder(this.frame, 1, 0);
	}

	@Test
	public final void testHit() {
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(frame, 1, 1);
		
		builder.hit(new Point(-1, -1));
		builder.hit(new Point(0, 0));
		builder.hit(new Point(1, 1));
	}

	@Test
	public final void testBuild() {
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(frame, 3, 4);

		assertFalse(builder.build().isHit(0, 0));
		assertFalse(builder.build().isHit(2, 3));
		
		builder.hit(new Point(-1, -1));
		builder.hit(new Point(0, 0));
		builder.hit(new Point(1, 1));

		assertTrue(builder.build().isHit(0, 0));
		assertTrue(builder.build().isHit(1, 1));
	}

}
