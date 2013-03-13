package ch.epfl.flamemaker.color;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class InterpolatedPaletteTest extends PaletteTest {

	List<Color>	array;

	@Before
	public void setUpInterpolatedPalette() {
		this.array = new ArrayList<Color>();
		this.array.add(new Color(1, 0, 0));
		this.array.add(new Color(0, 1, 0));
		this.array.add(new Color(0, 0, 1));
	}

	@Override
	Palette newPalette(List<Color> colors) {
		return new InterpolatedPalette(colors);
	}

	@Test
	public void testColorForIndex() {
		Palette p = newPalette(this.array);

		Color c = p.colorForIndex(0);
		assertEquals(1, c.red(), PaletteTest.DELTA);
		assertEquals(0, c.green(), PaletteTest.DELTA);
		assertEquals(0, c.blue(), PaletteTest.DELTA);

		c = p.colorForIndex(0.25);
		assertEquals(0.5, c.red(), PaletteTest.DELTA);
		assertEquals(0.5, c.green(), PaletteTest.DELTA);
		assertEquals(0, c.blue(), PaletteTest.DELTA);

		c = p.colorForIndex(0.5);
		assertEquals(0, c.red(), PaletteTest.DELTA);
		assertEquals(1, c.green(), PaletteTest.DELTA);
		assertEquals(0, c.blue(), PaletteTest.DELTA);

		c = p.colorForIndex(0.75);
		assertEquals(0, c.red(), PaletteTest.DELTA);
		assertEquals(0.5, c.green(), PaletteTest.DELTA);
		assertEquals(0.5, c.blue(), PaletteTest.DELTA);

		c = p.colorForIndex(1);
		assertEquals(0, c.red(), PaletteTest.DELTA);
		assertEquals(0, c.green(), PaletteTest.DELTA);
		assertEquals(1, c.blue(), PaletteTest.DELTA);

		p.colorForIndex(0.5);
		p.colorForIndex(1);
	}

	@Test
	public void testToString() {
		Palette p = newPalette(this.array);

		assertEquals("((1.0,0.0,0.0),(0.0,1.0,0.0),(0.0,0.0,1.0))", p.toString());
	}

	@Test
	public void testEquals() {
		Palette p1 = newPalette(this.array);
		Palette p2 = newPalette(this.array);

		assertTrue(p1.equals(p2));
	}
}
