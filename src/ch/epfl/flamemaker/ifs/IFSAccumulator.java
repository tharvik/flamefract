package ch.epfl.flamemaker.ifs;

public final class IFSAccumulator {
	/**
	 * Accumulator contains the pseudo-image of a fractal
	 */
	private final boolean[][] isHit;

	/**
	 * Creates an accumulator
	 * @param isHit bidimensional array of booleans
	 */
	public IFSAccumulator(boolean[][] isHit) {
		this.isHit = new boolean[isHit.length][isHit[0].length];

		// Deep copy of the array
		for (int x = 0; x < isHit.length; x++) {
			for (int y = 0; y < isHit[0].length; y++) {
				this.isHit[x][y] = isHit[x][y];
			}
		}
	}

	/**
	 * Returns the width of the accumulator
	 * @return The width of the accumulator
	 */
	public int width() {
		return isHit.length;
	}

	/**
	 * Returns the height of the accumulator
	 * @return The height of the accumulator
	 */
	public int height() {
		return isHit[0].length;
	}

	/**
	 * Tells if there is there is a point of S in a specific "box"
	 * @param x The x coordinate of the box
	 * @param y The y coordinate of the box
	 * @return True if there is a point of S in the box, false otherwise
	 */
	public boolean isHit(int x, int y) {
		if (x > this.width() || x < 0 || y > this.height() || y < 0) {
			throw new IndexOutOfBoundsException();
		}

		return (this.isHit[x][y]);
	}

}
