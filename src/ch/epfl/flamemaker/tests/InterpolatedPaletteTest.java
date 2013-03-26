package ch.epfl.flamemaker.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;

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

		for (double i = 0; i <= 1; i += 0.001) {
			Color c = p.colorForIndex(i);
			assertEquals((-2 * i + 1) > 0 ? (-2 * i + 1) : 0, c.red(), PaletteTest.DELTA);
			assertEquals((0.5 - Math.abs(i - 0.5)) * 2, c.green(), PaletteTest.DELTA);
			assertEquals((2 * i - 1) > 0 ? (2 * i - 1) : 0, c.blue(), PaletteTest.DELTA);
		}
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
