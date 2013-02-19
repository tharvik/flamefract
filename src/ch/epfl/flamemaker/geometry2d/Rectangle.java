package ch.epfl.flamemaker.geometry2d;

public class Rectangle {

	public Rectangle(Point center, double width, double height) {
		if (width < 0 || height < 0) {
			throw IllegalArgumentException;
		}
	}

}
