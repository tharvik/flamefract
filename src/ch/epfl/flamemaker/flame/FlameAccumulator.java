package ch.epfl.flamemaker.flame;

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

		// Deep copy of the hitCount array
		for (int x = 0; x < this.hitCount.length; x++) {
			this.hitCount[x] = hitCount[x];
		}

		// Deep copy of the colorIndexSum array
		for (int x = 0; x < this.colorIndexSum.length; x++) {
			this.colorIndexSum[x] = colorIndexSum[x];
		}

		// Get max value and calculate denominator
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

	public static class Builder {
		private int[][]			hitCount;
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
		}

		public void hit(Point p) {
			if (!this.frame.contains(p)) {
				return;
			}

			// We transform the point in our system
			p = this.transformation.transformPoint(p);
			this.hitCount[(int) (p.x())][(int) (p.y())]++;
		}

		public FlameAccumulator build() {
			// TODO update to new FlameAccumulator
			return new FlameAccumulator(this.hitCount, null);
		}
	}

	// Because we want some COLOR!
	public Color color(Palette palette, Color bg, int x, int y) {
		// i.e logically bg stands for background...
		// TODO
		return null;
	}
}