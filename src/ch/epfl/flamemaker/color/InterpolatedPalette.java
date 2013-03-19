package ch.epfl.flamemaker.color;

import java.util.Iterator;
import java.util.List;

public class InterpolatedPalette implements Palette {

	private List<Color>	colors;

	public InterpolatedPalette(List<Color> colors) {

		// because, if we do not have two colors, the palette will not
		// been bound
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

		double indexColors = index * (this.colors.size() - 1);
		int low = (int) indexColors;
		double proportion = (indexColors - low) * (this.colors.size() - 1) / 2;
		return this.colors.get(low).mixWith(this.colors.get(low + 1 == this.colors.size() ? low : low + 1),
				proportion);
	}

	// "((0,0,0),(1,1,1))"
	@Override
	public String toString() {
		String returnValue = "(";

		for (Iterator<Color> iterator = this.colors.iterator(); iterator.hasNext();) {
			returnValue += iterator.next().toString();
			returnValue += (iterator.hasNext()) ? "," : "";
		}

		return returnValue + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InterpolatedPalette that = (InterpolatedPalette) obj;
		if (colors == null) {
			if (that.colors != null) {
				return false;
			}
		} else if (!colors.equals(that.colors)) {
			return false;
		}

		// TODO missing individual checks?

		return true;
	}

}
