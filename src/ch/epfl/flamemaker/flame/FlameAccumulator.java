package ch.epfl.flamemaker.flame;

import sun.awt.SunToolkit.InfiniteLoop;
import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlameAccumulator {

	private final int[][]		hitCount;
	private final double[][]	colorIndexSum;
	private final double		denominator;

	public FlameAccumulator(int[][] hitCount, double[][] colorIndexSum) {
		this.hitCount = new int[hitCount.length][hitCount[0].length];
		this.colorIndexSum = new double[colorIndexSum.length][colorIndexSum[0].length];

		// TODO test same size

		// deep copy of the hitCount array
		for (int x = 0; x < this.hitCount.length; x++) {
			this.hitCount[x] = hitCount[x].clone();
		}

		// deep copy of the colorIndexSum array
		for (int x = 0; x < this.colorIndexSum.length; x++) {
			this.colorIndexSum[x] = colorIndexSum[x].clone();
		}

		// get max value and calculate denominator
		int max = 0;
		for (int[] x : hitCount) {
			for (int value : x) {
				if (max < value) {
					max = value;
				}
			}
		}
		this.denominator = Math.log(max + 1);
	}

	public int width() {
		return this.hitCount.length;
	}

	public int height() {
		return this.hitCount[0].length;
	}

	double intensity(int x, int y) {
		if (x < 0 || y < 0 || x > this.width() || y > this.height()) {
			throw new IndexOutOfBoundsException();
		}

		return (Math.log(this.hitCount[x][y] + 1) / this.denominator);
	}

	public Color color(Palette palette, Color background, int x, int y) {
		if (x < 0 || y < 0 || x > this.width() || y > this.height()) {
			throw new IndexOutOfBoundsException();
		}
		System.out.println(palette.colorForIndex(this.colorIndexSum[x][y]));
		System.out.println(this.intensity(x, y));
		System.out.println(background.mixWith(palette.colorForIndex(this.colorIndexSum[x][y]), this.intensity(x, y)));
		System.out.println("---");
		return background.mixWith(palette.colorForIndex(this.colorIndexSum[x][y]), this.intensity(x, y));
	}

	public static class Builder {
		private int[][]			hitCount;
		private double[][]		colorIndexSum;
		private Rectangle		frame;
		private AffineTransformation	transformation;

		public Builder(Rectangle frame, int width, int height) {
			if (width <= 0 || height <= 0) {
				throw new IllegalArgumentException();
			}

			this.frame = new Rectangle(frame.center(), frame.width(), frame.height());
			this.transformation = AffineTransformation.newScaling(width / frame.width(),
					height / frame.height());
			this.transformation = this.transformation.composeWith(AffineTransformation.newTranslation(
					-frame.left(), -frame.bottom()));

			this.hitCount = new int[width][height];
			this.colorIndexSum = new double[width][height];
		}

		public void hit(Point p, double colorIndex) {
			if (!this.frame.contains(p)) {
				return;
			}

			// we transform the point in our system
			p = this.transformation.transformPoint(p);
			int x = (int) (p.x()), y = (int) (p.y());
			this.hitCount[x][y]++;

			this.colorIndexSum[x][y] = colorIndex;
		}

		public FlameAccumulator build() {
			return new FlameAccumulator(this.hitCount, this.colorIndexSum);
		}
	}
}