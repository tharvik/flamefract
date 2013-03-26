package ch.epfl.flamemaker.color;

import java.util.ArrayList;
import java.util.Random;

/**
 * A {@link Palette} with a random set of color
 */
public class RandomPalette implements Palette {

	/**
	 * {@link InterpolatedPalette} used in internal
	 */
	private Palette	palette;

	/**
	 * Construct a {@link RandomPalette} with the given number of wanted {@link Color}
	 * 
	 * @param numberOfRandomColor
	 *                Total number of wanted {@link Color}
	 */
	public RandomPalette(int numberOfRandomColor) {
		Random random = new Random();
		ArrayList<Color> list = new ArrayList<Color>();
		for (int i = 0; i < numberOfRandomColor; i++) {
			// move the range of the random from [0,1[ to [0,1]
			double[] array = new double[3];
			array[0] = random.nextInt(Integer.MAX_VALUE) / (Integer.MAX_VALUE - 1);
			array[1] = random.nextInt(Integer.MAX_VALUE) / (Integer.MAX_VALUE - 1);
			array[2] = random.nextInt(Integer.MAX_VALUE) / (Integer.MAX_VALUE - 1);
			list.add(new Color(array[0], array[1], array[2]));
		}
		this.palette = new InterpolatedPalette(list);
	}

	@Override
	public Color colorForIndex(double index) {
		return this.palette.colorForIndex(index);
	}

	@Override
	public String toString() {
		return this.palette.toString();
	}
}
