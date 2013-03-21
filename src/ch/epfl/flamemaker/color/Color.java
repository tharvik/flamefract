package ch.epfl.flamemaker.color;

public final class Color {

	// the three colors
	private final double		r, g, b;

	// some default values for class
	public static final Color	BLACK	= new Color(0, 0, 0);
	public static final Color	WHITE	= new Color(1, 1, 1);
	public static final Color	RED	= new Color(1, 0, 0);
	public static final Color	GREEN	= new Color(0, 1, 0);
	public static final Color	BLUE	= new Color(0, 0, 1);

	public Color(double r, double g, double b) {

		// if r,g,b not in [0,1]
		if (r < 0 || g < 0 || b < 0 || r > 1 || g > 1 || b > 1) {
			throw new IllegalArgumentException();
		}

		this.r = r;
		this.g = g;
		this.b = b;
	}

	public double red() {
		return this.r;
	}

	public double green() {
		return this.g;
	}

	public double blue() {
		return this.b;
	}

	public Color mixWith(Color that, double proportion) {
		if (proportion < 0 || proportion > 1) {
			throw new IllegalArgumentException();
		}

		return new Color((1 - proportion) * this.red() + proportion * that.red(), (1 - proportion)
				* this.green() + proportion * that.green(), (1 - proportion) * this.blue() + proportion
				* that.blue());
	}

	// return color as int, a value by byte
	public int asPackedRGB() {
		int color = (int) (this.red() * 255);
		color = color << 8;
		color += (int) (this.green() * 255);
		color = color << 8;
		color += (int) (this.blue() * 255);

		return color;
	}

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
