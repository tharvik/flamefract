package ch.epfl.flamemaker.geometry2d;

public class Rectangle {

	final private Point center;
	final private double width, height;

	Rectangle(Point center, double width, double height) {

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}

		this.center = center;
		this.width = width;
		this.height = height;
	}

	public double left() {
		return this.center.x() - this.width() / 2;
	}

	public double right() {
		return this.center.x() + this.width() / 2;
	}

	public double bottom() {
		return this.center.y() - this.height() / 2;
	}

	public double top() {
		return this.center.y() + this.height() / 2;
	}

	public double width() {
		return this.width;
	}

	public double height() {
		return this.height;
	}

	public Point center() {
		return this.center;
	}

	public boolean contain(Point p) {
		return p.x() >= this.left() && p.x() < this.right()
				|| p.y() >= this.bottom() && p.y() < this.top();
	}

	public double aspectRatio() {
		return this.width() / this.height();
	}

	public Rectangle expandToAspectRatio(double aspectRatio) {
		if (aspectRatio <= 0) {
			throw new IllegalArgumentException();
		}

		if (aspectRatio < this.aspectRatio()) {
			return new Rectangle(center, this.width(), this.height
					/ aspectRatio);
		} else if (aspectRatio > this.aspectRatio()) {
			return new Rectangle(center, this.width() * aspectRatio,
					this.height);
		} else {
			return new Rectangle(center, this.width(), this.height);
		}
	}

	public String toString() {
		return ("(" + this.center().toString() + "," + this.width() + ","
				+ this.height + ")");
	}
}
