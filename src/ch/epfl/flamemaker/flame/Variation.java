package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

/**
 * Class containing the six variations used by the fractals flames
 */
public abstract class Variation implements Transformation {
	/**
	 * The name of the variation
	 */
	private final String	name;
	/**
	 * The index of the variation
	 */
	private final int	index;

	/**
	 * Construct a Variation with the given name and index
	 * 
	 * @param index
	 *                The index
	 * @param name
	 *                The name
	 */
	private Variation(int index, String name) {
		this.name = name;
		this.index = index;
	}

	/**
	 * Return the name of the variation
	 * 
	 * @return The name of the variation
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Return the name of the variation
	 * 
	 * @return The index of the variation
	 */
	public int index() {
		return this.index;
	}

	abstract public Point transformPoint(Point p);

	/**
	 * A list of all the variations
	 */
	public final static List<Variation>	ALL_VARIATIONS	= Arrays.asList(new Variation(0, "Linear") {
									public Point transformPoint(Point p) {
										return p;
									}
								}, new Variation(1, "Sinusoidal") {
									public Point transformPoint(Point p) {
										double x = Math.sin(p.x());
										double y = Math.sin(p.y());
										return new Point(x, y);
									}
								}, new Variation(2, "Spherical") {
									public Point transformPoint(Point p) {
										double x = p.x() / Math.pow(p.r(), 2);
										double y = p.y() / Math.pow(p.r(), 2);
										return new Point(x, y);
									}
								}, new Variation(3, "Swirl") {
									public Point transformPoint(Point p) {
										double r = Math.pow(p.r(), 2);
										double x = p.x() * Math.sin(r) - p.y()
												* Math.cos(r);
										double y = p.x() * Math.cos(r) + p.y()
												* Math.sin(r);
										return new Point(x, y);
									}
								}, new Variation(4, "Horseshoe") {
									public Point transformPoint(Point p) {
										double x = (p.x() - p.y())
												* (p.x() + p.y())
												/ p.r();
										double y = 2 * p.x() * p.y() / p.r();
										return new Point(x, y);
									}
								}, new Variation(5, "Bubble") {
									public Point transformPoint(Point p) {
										double r = Math.pow(p.r(), 2) + 4;
										double x = 4 * p.x() / (r);
										double y = 4 * p.y() / (r);
										return new Point(x, y);
									}
								});
}
