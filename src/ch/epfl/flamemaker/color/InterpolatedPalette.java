package ch.epfl.flamemaker.color;

import java.util.Iterator;
import java.util.List;

/**
 * {@link Palette} which interpolate between the color given in the list
 */
public class InterpolatedPalette implements Palette {

	/**
	 * List of {@link Color} to interpolate with
	 */
	private final List<Color>	colors;

	/**
	 * Construct an InterpolatedPalette with the given {@link List} of
	 * {@link Color}
	 * 
	 * @param colors
	 *                List of {@link Color} to interpolate
	 * 
	 * @throws IllegalArgumentException
	 *                 if the size of the given {@link List} is less than
	 *                 two
	 */
	public InterpolatedPalette(List<Color> colors) {

		// because, if we do not have two colors, the palette will not
		// been bound and thus not usable
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

		final double indexColors = index * (this.colors.size() - 1);

		// get the index of the first color
		final int low = (int) indexColors;
		final Color color = this.colors.get(low);

		// get the proportion to mix the color with
		final double proportion = (indexColors - low) * (this.colors.size() - 1) / 2;

		// if we got the last color, do not mix, just return it
		if (this.colors.size() == low + 1) {
			return color;
		}

		return color.mixWith(this.colors.get(low + 1), proportion);
	}

	// "((0,0,0),(1,1,1))"
	@Override
	public String toString() {
		String returnValue = "(";

		for (final Iterator<Color> iterator = this.colors.iterator(); iterator.hasNext();) {
			returnValue += iterator.next().toString();
			returnValue += (iterator.hasNext()) ? "," : "";
		}

		return returnValue + ")";
	}
}
