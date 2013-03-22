package ch.epfl.flamemaker.testSuite;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import ch.epfl.flamemaker.geometry2d.*;
import ch.epfl.flamemaker.ifs.IFS;
import ch.epfl.flamemaker.ifs.IFSAccumulator;

public class IFSTest {

	ArrayList<AffineTransformation> transformations;
	Rectangle frame;

	@Before
	public void setUp() {
		this.transformations = new ArrayList<AffineTransformation>();
		this.frame = new Rectangle(new Point(5, 5), 10, 10);
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

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if (x != 0 && y != 0) {
					assertFalse(accu.isHit(x, y));
				}
			}
		}
	}

	@Test
	public final void testComputeEmptyList() {
		IFS ifs = new IFS(this.transformations);

		IFSAccumulator accu = ifs.compute(this.frame, 10, 10, 1);

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				assertFalse(accu.isHit(x, y));
			}
		}
	}

}
