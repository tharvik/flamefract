package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {

	private boolean[][] isHit;

	public IFSAccumulatorBuilder(Rectangle frame, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		this.isHit = new boolean[width][height];
	}

	public void hit(Point p) {
		if (p.x() >= this.isHit.length || p.y() >= this.isHit[0].length) {
			return;
		}

		// ! Not recommended by instructions (use Math.floor()), same value
		// ! Wrong logic? Unused information in instructions
		this.isHit[(int) (p.x())][(int) (p.y())] = true;
	}

	IFSAccumulator build() {
		return new IFSAccumulator(this.isHit);
	}

}