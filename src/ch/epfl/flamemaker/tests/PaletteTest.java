package ch.epfl.flamemaker.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;

public abstract class PaletteTest {

	public static double	DELTA	= 0.000000001;

	private List<Color>	array;

	@Before
	public void setUp() {
		final ArrayList<Color> arrayBuilder = new ArrayList<Color>();
		arrayBuilder.add(new Color(1, 0, 0));
		arrayBuilder.add(new Color(0, 1, 0));
		arrayBuilder.add(new Color(0, 0, 1));
		this.array = arrayBuilder;
	}

	@Test
	public abstract void testColorForIndex();

	@Test(expected = IllegalArgumentException.class)
	public void testColorForIndexTooBig() {
		final Palette p = this.newPalette(this.array);
		p.colorForIndex(10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorForIndexTooSmall() {
		final Palette p = this.newPalette(this.array);
		p.colorForIndex(-1);
	}

	@Test
	public void testPalette() {
		this.newPalette(this.array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPaletteSize0() {
		this.newPalette(new ArrayList<Color>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPaletteSize1() {
		final ArrayList<Color> arrayBuilder = new ArrayList<Color>();
		arrayBuilder.add(new Color(0, 0, 0));
		this.newPalette(arrayBuilder);
	}

	// will have to be subclassed to test both instance of palette
	abstract Palette newPalette(List<Color> colors);
}
