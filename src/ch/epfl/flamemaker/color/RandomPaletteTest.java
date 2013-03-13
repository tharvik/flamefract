package ch.epfl.flamemaker.color;

import java.util.List;

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
