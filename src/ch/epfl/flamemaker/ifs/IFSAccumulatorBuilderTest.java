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
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testBuild() {
		fail("Not yet implemented"); // TODO
	}

}
