package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

/**
 * Class containing the six variations used by the flame fractals
 */
public abstract class Variation implements Transformation {

	/**
	 * The actual index of the Variation, to compute the needed index by
	 * {@link Variation}
	 */
	private static int			total		= 0;

	/**
	 * A list of all the variations
	 */
	public final static List<Variation>	ALL_VARIATIONS	= Arrays.asList(new Variation(total++, "Linear") {
									@Override
									public Point transformPoint(Point p) {
										return p;
									}
								}, new Variation(total++, "Sinusoidal") {
									@Override
									public Point transformPoint(Point p) {
										final double x = Math.sin(p.x());
										final double y = Math.sin(p.y());
										return new Point(x, y);
									}
								}, new Variation(total++, "Spherical") {
									@Override
									public Point transformPoint(Point p) {
										final double x = p.x()
												/ Math.pow(p.r(), 2);
										final double y = p.y()
												/ Math.pow(p.r(), 2);
										return new Point(x, y);
									}
								}, new Variation(total++, "Swirl") {
									@Override
									public Point transformPoint(Point p) {
										final double r = Math.pow(p.r(), 2);
										final double x = p.x() * Math.sin(r)
												- p.y() * Math.cos(r);
										final double y = p.x() * Math.cos(r)
												+ p.y() * Math.sin(r);
										return new Point(x, y);
									}
								}, new Variation(total++, "Horseshoe") {
									@Override
									public Point transformPoint(Point p) {
										final double x = (p.x() - p.y())
												* (p.x() + p.y())
												/ p.r();
										final double y = 2 * p.x() * p.y()
												/ p.r();
										return new Point(x, y);
									}
								}, new Variation(total++, "Bubble") {
									@Override
									public Point transformPoint(Point p) {
										final double r = Math.pow(p.r(), 2) + 4;
										final double x = 4 * p.x() / (r);
										final double y = 4 * p.y() / (r);
										return new Point(x, y);
									}
								});
	/**
	 * The index of the variation
	 */
	private final int			index;
	/**
	 * The name of the variation
	 */
	private final String			name;

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
	 * Return the index of the variation
	 * 
	 * @return The index of the variation
	 */
	public int index() {
		return this.index;
	}

	/**
	 * Return the name of the variation
	 * 
	 * @return The name of the variation
	 */
	public String name() {
		return this.name;
	}

	@Override
	abstract public Point transformPoint(Point p);
}
