package ch.epfl.flamemaker.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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
	@Test
	public void testColorForIndex() {
		final Palette p = this.newPalette(this.array);

		for (double i = 0; i <= 1; i += 0.001) {
			final Color c = p.colorForIndex(i);
			Assert.assertEquals((-2 * i + 1) > 0 ? (-2 * i + 1) : 0, c.red(), PaletteTest.DELTA);
			Assert.assertEquals((0.5 - Math.abs(i - 0.5)) * 2, c.green(), PaletteTest.DELTA);
			Assert.assertEquals((2 * i - 1) > 0 ? (2 * i - 1) : 0, c.blue(), PaletteTest.DELTA);
		}
	}

	@Test
	public void testToString() {
		final Palette p = this.newPalette(this.array);

		Assert.assertEquals("((1.0,0.0,0.0),(0.0,1.0,0.0),(0.0,0.0,1.0))", p.toString());
	}

	@Override
	Palette newPalette(List<Color> colors) {
		return new InterpolatedPalette(colors);
	}
}
