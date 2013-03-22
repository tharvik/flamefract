package ch.epfl.flamemaker.color;

/**
 * RGB representation of a color
 */
public final class Color {

	/**
	 * The red value of the color
	 */
	private final double		r;
	/**
	 * The green value of the color
	 */
	private final double		g;
	/**
	 * The blue value of the color
	 */
	private final double		b;

	// some default values for class
	/**
	 * A Color with the value representing black
	 */
	public static final Color	BLACK	= new Color(0, 0, 0);
	/**
	 * A Color with the value representing white
	 */
	public static final Color	WHITE	= new Color(1, 1, 1);
	/**
	 * A Color with the value representing red
	 */
	public static final Color	RED	= new Color(1, 0, 0);
	/**
	 * A Color with the value representing green
	 */
	public static final Color	GREEN	= new Color(0, 1, 0);
	/**
	 * A Color with the value representing blue
	 */
	public static final Color	BLUE	= new Color(0, 0, 1);

	/**
	 * Construct a color with the three value given
	 * 
	 * @param r
	 *                The red value
	 * @param g
	 *                The green value
	 * @param b
	 *                The blue value
	 * @throws IllegalArgumentException
	 *                 if the value is not between 0 and 1 (both included)
	 */
	public Color(double r, double g, double b) {

		// if r,g,b not in [0,1]
		if (r < 0 || g < 0 || b < 0 || r > 1 || g > 1 || b > 1) {
			throw new IllegalArgumentException();
		}

		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Return the red value
	 * 
	 * @return The red value
	 */
	public double red() {
		return this.r;
	}

	/**
	 * Return the green value
	 * 
	 * @return The green value
	 */
	public double green() {
		return this.g;
	}

	/**
	 * Return the blue value
	 * 
	 * @return The blue value
	 */
	public double blue() {
		return this.b;
	}

	/**
	 * Return a new Color created by mixing the color with another color in
	 * given proportion
	 * 
	 * @param that
	 *                The color to mix with
	 * @param proportion
	 *                The proportion to mix the given color with this color
	 *                <p>
	 *                If 0, then the returned color is the <i>same</i>
	 *                </p>
	 *                <p>
	 *                if 1, then the returned color is the <i>given
	 *                color</i>
	 *                </p>
	 * @return The new Color mix
	 * @throws IllegalArgumentException
	 *                 if the proportion is not between 0 and 1 (both
	 *                 included)
	 */
	public Color mixWith(Color that, double proportion) {
		if (proportion < 0 || proportion > 1) {
			throw new IllegalArgumentException();
		}

		return new Color((1 - proportion) * this.red() + proportion * that.red(), (1 - proportion)
				* this.green() + proportion * that.green(), (1 - proportion) * this.blue() + proportion
				* that.blue());
	}

	// return color as int, a value by byte
	/**
	 * Return the colors value as int, a value by byte, in red, green, blue
	 * order
	 * 
	 * @return Colors as int, a value by byte in red, green, blue order
	 */
	public int asPackedRGB() {
		int color = (int) (this.red() * 255);
		color = color << 8;
		color += (int) (this.green() * 255);
		color = color << 8;
		color += (int) (this.blue() * 255);

		return color;
	}

	/**
	 * @param v
	 *                Value to encode in sRGB
	 * @param max
	 *                The maximum value to return
	 * @return The value encode in sRGB multiplied by max
	 */
	public static int sRGBEncode(double v, int max) {
		if (v <= 0.0031308) {
			return (int) (12.92 * v * max);
		} else {
			return (int) ((1.055 * Math.pow(v, 1 / 2.4) - 0.055) * max);
		}
	}

	// "(0,0,0)"
	@Override
	public String toString() {
		return ("(" + this.red() + "," + this.green() + "," + this.blue() + ")");
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
		Color that = (Color) obj;
		if (this.asPackedRGB() != that.asPackedRGB()) {
			return false;
		}

		return true;
	}

}
