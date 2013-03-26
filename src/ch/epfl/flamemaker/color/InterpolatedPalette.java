package ch.epfl.flamemaker.color;

import java.util.Iterator;
import java.util.List;

/**
 * Palette which interpolate between the color given in the list
 */
public class InterpolatedPalette implements Palette {

	/**
	 * List of colors to interpolate with
	 */
	private List<Color>	colors;

	/**
	 * Construct an InterpolatedPalette with the given list of Color
	 * 
	 * @param colors
	 *                List of Color to interpolate
	 * @throws IllegalArgumentException
	 *                 if the size of the given list is less than two
	 */
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
}
