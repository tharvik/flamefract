package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {

	private boolean[][] isHit;

	// ? get width/height from frame
	public IFSAccumulatorBuilder(Rectangle frame, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		this.isHit = new boolean[width][height];
	}

	public void hit(Point p) {
		p = new Point(p.x() * this.isHit.length, p.y() * this.isHit[0].length);

		if (p.x() >= this.isHit.length || p.y() >= this.isHit[0].length
				|| p.x() < 0 || p.y() < 0) {
			return;
		}

		this.isHit[(int) (p.x())][(int) (p.y())] = true;
	}

	IFSAccumulator build() {
		return new IFSAccumulator(this.isHit);
	}

}