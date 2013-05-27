package ch.epfl.flamemaker.geometry2d;

/**
 * A {@link Transformation} with a given matrix which will transform any given
 * point with his matrix
 * <p>
 * It has a lot of static methods to generate an {@link AffineTransformation}
 * containing the right matrix for e.g. to translate a point with the wanted
 * values
 * </p>
 * <p>
 * Variables to express our matrix:
 * </p>
 * </br> a b c</br> d e f</br> 0 0 1</br>
 */
public final class AffineTransformation implements Transformation {

	/**
	 * <p>
	 * The identity matrix
	 * </p>
	 * 
	 * <p>
	 * If multiply a matrix M by the identity (or we multiply the identity
	 * by M), the result will be M.
	 * </p>
	 * </br> 1 0 0</br> 0 1 0</br> 0 0 1</br>
	 * 
	 */
	public static final AffineTransformation	IDENTITY	= new AffineTransformation(1, 0, 0, 0, 1, 0);

	/**
	 * The 1,1 element of the matrix
	 */
	final private double				a;
	/**
	 * The 1,2 element of the matrix
	 */
	final private double				b;
	/**
	 * The 1,3 element of the matrix
	 */
	final private double				c;
	/**
	 * The 2,1 element of the matrix
	 */
	final private double				d;
	/**
	 * The 2,2 element of the matrix
	 */
	final private double				e;
	/**
	 * The 2,3 element of the matrix
	 */
	final private double				f;

	/**
	 * The transformation we need to rotate a vector
	 * 
	 * @param theta
	 *                The angle of the rotation (in radians)
	 * @return A matrix we use to rotate a vector
	 */
	public static AffineTransformation newRotation(final double theta) {
		final double sin = Math.sin(theta);
		final double cos = Math.cos(theta);

		return new AffineTransformation(cos, -sin, 0.0, sin, cos, 0.0);
	}

	/**
	 * The transformation we need to scale a vector
	 * 
	 * @param sX
	 *                The dilation factor on the x-axis
	 * @param sY
	 *                The dilation factor on the y-axis
	 * @return A matrix we use to scale a vector
	 */
	public static AffineTransformation newScaling(final double sX, final double sY) {
		return new AffineTransformation(sX, 0.0, 0.0, 0.0, sY, 0.0);
	}

	/**
	 * The transformation we need to shear a vector on the x-axis
	 * 
	 * @param sX
	 *                The factor of shearing
	 * @return A matrix we use to shear a vector
	 */
	public static AffineTransformation newShearX(final double sX) {
		return new AffineTransformation(1, sX, 0, 0, 1, 0);
	}

	/**
	 * The transformation we need to shear a vector on the y-axis
	 * 
	 * @param sY
	 *                The factor of shearing
	 * @return A matrix we use to shear a vector
	 */
	public static AffineTransformation newShearY(final double sY) {
		return new AffineTransformation(1, 0, 0, sY, 1, 0);
	}

	/**
	 * The transformation we need to translate a vector
	 * 
	 * @param dX
	 *                The delta between the actual vector and the
	 *                transformed vector on the x-axis
	 * @param dY
	 *                The delta between the actual vector and the
	 *                transformed vector on the y-axis
	 * @return A matrix we use to translate a vector
	 */
	public static AffineTransformation newTranslation(final double dX, final double dY) {
		return new AffineTransformation(1, 0, dX, 0, 1, dY);
	}

	/**
	 * Construct the matrix we need for the transformation
	 * 
	 * @param a
	 *                The 1,1 element of the matrix
	 * @param b
	 *                The 1,2 element of the matrix
	 * @param c
	 *                The 1,3 element of the matrix
	 * @param d
	 *                The 2,1 element of the matrix
	 * @param e
	 *                The 2,2 element of the matrix
	 * @param f
	 *                The 2,3 element of the matrix
	 */
	public AffineTransformation(final double a, final double b, final double c, final double d, final double e,
			final double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}

	/**
	 * Gives us a matrix that represents the compounded function of two
	 * transformation
	 * 
	 * @param that
	 *                A linear transformation
	 * @return A matrix that represents the compounded function of two
	 *         transformation
	 */
	public AffineTransformation composeWith(final AffineTransformation that) {
		final double newA = this.a * that.a + this.b * that.d;
		final double newB = this.a * that.b + this.b * that.e;
		final double newC = this.a * that.c + this.b * that.f + this.c;
		final double newD = this.d * that.a + this.e * that.d;
		final double newE = this.d * that.b + this.e * that.e;
		final double newF = this.d * that.c + this.e * that.f + this.f;

		return new AffineTransformation(newA, newB, newC, newD, newE, newF);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AffineTransformation other = (AffineTransformation) obj;
		if (this.a != other.a || this.b != other.b || this.c != other.c || this.d != other.d
				|| this.e != other.e || this.f != other.f) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "((" + this.a + "," + this.b + "," + this.c + "),(" + this.d + "," + this.e + "," + this.f
				+ "))";
	}

	@Override
	public Point transformPoint(final Point p) {
		final double newX = p.x() * this.a + p.y() * this.b + this.c;
		final double newY = p.x() * this.d + p.y() * this.e + this.f;

		return new Point(newX, newY);
	}

	/**
	 * Returns the horizontal component of the translation
	 * 
	 * @return The horizontal component of the translation
	 */
	public double translationX() {
		return this.c;
	}

	/**
	 * Returns the vertical component of the translation
	 * 
	 * @return The vertical component of the translation
	 */
	public double translationY() {
		return this.f;
	}
}
