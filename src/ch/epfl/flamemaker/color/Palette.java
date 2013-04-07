package ch.epfl.flamemaker.color;

/**
 * Model of a Palette which contains some {@link Color} and a way to retrieved
 * these based on index
 */
public interface Palette {
	/**
	 * Return a {@link Color} for the given index
	 * 
	 * @param index
	 *                The index for the wanted {@link Color}
	 * 
	 * @return a {@link Color} for the given index
	 * 
	 * @throws IllegalArgumentException
	 *                 if the index is not between 0 and 1 (both included)
	 */
	Color colorForIndex(double index);
}
