package ch.epfl.flamemaker;

import java.util.ArrayList;
import java.util.Random;

import ch.epfl.flamemaker.color.Color;

public class RandomPalette implements Palette{

	private Palette palette;
	
	public RandomPalette(int numberOfRandomColor) {
		Random random = new Random();
		ArrayList<Color> list = new ArrayList<Color>();
		for (int i = 0; i < numberOfRandomColor; i++) {
			list.add(new Color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
		}
		this.palette = new InterpolatedPalette(list);
	}
	
	@Override
	public Color colorForIndex(double index) {
		return this.palette.colorForIndex(index);
	}
}
