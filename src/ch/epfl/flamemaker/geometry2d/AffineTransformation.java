package ch.epfl.flamemaker.geometry2d;

public class AffineTransformation implements Transformation {
	final private double a, b, c, d, e, f;
	public static final AffineTransformation IDENTITY = new AffineTransformation(1, 0,
			0, 0, 1, 0);

	public AffineTransformation(double a, double b, double c, double d,
			double e, double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}

	public static AffineTransformation newTranslation(double dx, double dy) {
		return new AffineTransformation(1, 0, dx, 0, 1, dy);
	}

	public static AffineTransformation newRotation(double theta) {
		double sin = Math.sin(theta);
		double cos = Math.cos(theta);

		return new AffineTransformation(cos, -sin, 0.0, sin, cos, 0.0);
	}

	public static AffineTransformation newScaling(double sx, double sy) {
		return new AffineTransformation(sx, 0.0, 0.0, 0.0, sy, 0.0);
	}

	public static AffineTransformation newShearX(double sx) {
		return new AffineTransformation(1, sx, 0, 0, 1, 0);
	}

	public static AffineTransformation newShearY(double sy) {
		return new AffineTransformation(1, 0, 0, sy, 1, 0);
	}

	public Point transformPoint(Point p) {
		double newx = p.x() * a + p.y() * b + c;
		double newy = p.x() * d + p.y() * e + f;

		return new Point(newx, newy);
	}

	public double translationX() {
		return c;
	}

	public double translationY() {
		return f;
	}

	public AffineTransformation composeWith(AffineTransformation that) {
		double newA = this.a * that.a + this.b * that.d;
		double newB = this.a * that.b + this.b * that.e;
		double newC = this.a * that.c + this.b * that.f + this.c;
		double newD = this.d * that.a + this.e * that.d;
		double newE = this.d * that.b + this.e * that.e;
		double newF = this.d * that.c + this.e * that.f + this.f;

		return new AffineTransformation(newA, newB, newC, newD, newE, newF);
	}
}
