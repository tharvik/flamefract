package ch.epfl.flamemaker.geometry2d;

/**
 * Implements the concept of transformation
 */
public interface Transformation {
	/**
	 * @param p
	 *                The point to transform
	 * 
	 * @return A new point that is p transformed
	 */
	Point transformPoint(Point p);
}
