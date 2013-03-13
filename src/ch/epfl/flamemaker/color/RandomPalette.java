package ch.epfl.flamemaker.color;

import java.util.ArrayList;
import java.util.Random;

public class RandomPalette implements Palette {

	private Palette	palette;

	public RandomPalette(int numberOfRandomColor) {
		Random random = new Random();
		ArrayList<Color> list = new ArrayList<Color>();
		for (int i = 0; i < numberOfRandomColor; i++) {
			// TODO random.nextDouble() in [0,1]
			list.add(new Color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
		}
		this.palette = new InterpolatedPalette(list);
	}

	@Override
	public Color colorForIndex(double index) {
		return this.palette.colorForIndex(index);
	}

	@Override
	public boolean equals(Object obj) {
		return this.palette.equals(obj);
	}

	@Override
	public String toString() {
		return this.palette.toString();
	}
}
