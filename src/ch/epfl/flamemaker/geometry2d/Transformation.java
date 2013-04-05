package ch.epfl.flamemaker.geometry2d;

///**
// * Interface for the transformation, which
// */

/**
 * Implements the concept of transformation
 */
public interface Transformation {
	/**
	 * @param p
	 *                is the point that is going to be transformed
	 * 
	 * @return A new point that has been transformed
	 */
	Point transformPoint(Point p);
}
