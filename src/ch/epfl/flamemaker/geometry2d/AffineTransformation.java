package ch.epfl.flamemaker.geometry2d;

public class AffineTransformation implements Transformation {
	final private double a, b, c, d, e, f;
	static final AffineTransformation IDENTITY = new AffineTransformation(1, 0, 0, 0, 1, 0);
	
	public AffineTransformation(double a, double b, double c, double d, double e, double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}

	public static AffineTransformation newTranslation (double dx, double dy) {
		return new AffineTransformation(1, 0, dx, 0, 1, dy);
	}
	
	public static AffineTransformation newRotation (double theta) {
		double sin = Math.sin(theta);
		double cos = Math.cos(theta);
		
		return new AffineTransformation(cos, -sin, 0.0, sin, cos, 0.0);
	}
	
	public static AffineTransformation newScaling (double sx, double sy) {
		return new AffineTransformation(sx, 0.0, 0.0, 0.0, sy, 0.0);
	}
	
	public static AffineTransformation newShearX (double sx) {
		return new AffineTransformation(1, sx, 0, 0, 1, 0);
	}
	
	public static AffineTransformation newShearY (double sy) {
		return new AffineTransformation(1, 0, 0, sy, 1, 0);
	}

	public Point transformPoint (Point p) {
		double newx = p.x()*a + p.y()*b + c;
		double newy = p.x()*d + p.y()*e + f;
		
		return new Point(newx, newy);
	}
	
	public double translationX() {
		return c;
	}
	
	public double translationY() {
		return f;
	}
	
	public AffineTransformation composeWith(AffineTransformation that) {
		double newa = a*that.a + b*that.d;
		double newb = a*that.b + b*that.e;
		double newc = a*that.c + b*that.f + c;
		double newd = d*that.a + e*that.d;
		double newe = d*that.b + e*that.e;
		double newf = d*that.c + e*that.f + f;

		return new AffineTransformation(newa, newb, newc, newd, newe, newf);
	}
}
