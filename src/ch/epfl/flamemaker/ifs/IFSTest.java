package ch.epfl.flamemaker.ifs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import ch.epfl.flamemaker.geometry2d.*;

public class IFSTest {
	
	ArrayList<AffineTransformation>	transformations;
	Rectangle			frame;
	
	@Before
	public void setup() {
		this.transformations = new ArrayList<AffineTransformation>();
		this.frame = new Rectangle(new Point(0, 0), 10, 10);
	}
	
	@Test
	public final void testIFS() {
		this.transformations.add(AffineTransformation.IDENTITY);
		new IFS(this.transformations);
	}
	
	@Test
	public final void testIFSEmpty() {
		new IFS(this.transformations);
	}
	
	@Test(expected = NullPointerException.class)
	public final void testIFSNull() {
		new IFS(null);
	}
	
	@Test
	public final void testCompute() {
		this.transformations.add(AffineTransformation.IDENTITY);
		IFS ifs = new IFS(this.transformations);
		
		IFSAccumulator accu = ifs.compute(this.frame, 10, 10, 1);
		
		assertTrue(accu.isHit(0, 0));
		assertFalse(accu.isHit(0, 1));
		assertFalse(accu.isHit(1, 0));
		assertFalse(accu.isHit(9, 9));
		assertFalse(accu.isHit(5, 5));
		
		// ! Need some more test
	}
	
}
