package ch.epfl.flamemaker.ifs;

import java.util.List;

import ch.epfl.flamemaker.color.Color;

public class InterpolatedPalette implements Palette {

	private List<Color>	colors;

	public InterpolatedPalette(List<Color> colors) {
		if (colors.size() < 2) {
			throw new IllegalArgumentException();
		}

		this.colors = colors;
	}

	@Override
	public Color colorForIndex(double index) {
		if (index > 1 || index < 0) {
			throw new IllegalArgumentException();
		}

		int low = 0;
		for (double lowIndex = 0; low < this.colors.size(); low++) {

			System.out.println(low + "," + lowIndex);

			lowIndex += (1.0 / this.colors.size());
			if (lowIndex > index) {
				System.out.println("proportion: " + (index - lowIndex + (1.0 / this.colors.size())) * this.colors.size());
				
				return this.colors.get(low).mixWith(this.colors.get(low + 1),
						(index - lowIndex + (1.0 / this.colors.size())) * this.colors.size());
			}
		}

		return null;
	}
}
