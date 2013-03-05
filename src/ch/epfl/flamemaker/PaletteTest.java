package ch.epfl.flamemaker;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.flamemaker.color.Color;

public abstract class PaletteTest {

	abstract Palette newPalette(List<Color> colors);

	public static double	DELTA	= 0.000000001;
	private List<Color>	array;

	@Before
	public void setUp() {
		ArrayList<Color> arrayBuilder = new ArrayList<Color>();
		arrayBuilder.add(new Color(1, 0, 0));
		arrayBuilder.add(new Color(0, 1, 0));
		arrayBuilder.add(new Color(0, 0, 1));
		this.array = arrayBuilder;
	}

	@Test
	public void testPalette() {
		newPalette(this.array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPaletteSize0() {
		newPalette(new ArrayList<Color>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPaletteSize1() {
		ArrayList<Color> arrayBuilder = new ArrayList<Color>();
		arrayBuilder.add(new Color(0, 0, 0));
		newPalette(arrayBuilder);
	}

	@Test
	public abstract void testColorForIndex();

	@Test(expected = IllegalArgumentException.class)
	public void testColorForIndexTooSmall() {
		Palette p = newPalette(this.array);
		p.colorForIndex(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColorForIndexTooBig() {
		Palette p = newPalette(this.array);
		p.colorForIndex(10);
	}

}
