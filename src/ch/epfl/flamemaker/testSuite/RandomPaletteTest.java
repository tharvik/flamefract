package ch.epfl.flamemaker.testSuite;

import java.util.List;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.color.RandomPalette;

public class RandomPaletteTest extends PaletteTest {

	@Override
	Palette newPalette(List<Color> colors) {
		return new RandomPalette(colors.size());
	}

	@Override
	public void testColorForIndex() {
		// nothing, because we can not predict what will happened
	}
}