package ch.epfl.flamemaker;

import java.util.Iterator;
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

			lowIndex += (1.0 / (this.colors.size() - 1));
			if (lowIndex > index) {
				double proportion = 1 - (lowIndex - index) * (this.colors.size() - 1);
				Color mixWith = this.colors.get((low + 1 == this.colors.size()) ? low : low + 1);
				return this.colors.get(low).mixWith(mixWith, proportion);
			}
		}

		return null;
	}
	
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
