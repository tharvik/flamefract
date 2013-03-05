package ch.epfl.flamemaker;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.epfl.flamemaker.color.Color;

public class InterpolatedPaletteTest extends PaletteTest {

	@Override
	Palette newPalette(List<Color> colors) {
		return new InterpolatedPalette(colors);
	}

	@Test
	public void testColorForIndex() {
		ArrayList<Color> arrayBuilder = new ArrayList<Color>();
		arrayBuilder.add(new Color(1, 0, 0));
		arrayBuilder.add(new Color(0, 1, 0));
		arrayBuilder.add(new Color(0, 0, 1));
		
		Palette p = newPalette(arrayBuilder);

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
}
