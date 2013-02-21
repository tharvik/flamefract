package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {

	private boolean[][] isHit;
	private Rectangle frame;
	private AffineTransformation transformation;

	public IFSAccumulatorBuilder(Rectangle frame, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		this.frame = new Rectangle(frame.center(), frame.width(),
				frame.height());

		// Convert from general point-system, to local, easy to use by array
		this.transformation = AffineTransformation.newTranslation(
				-frame.left(), -frame.bottom());
		this.transformation = this.transformation
				.composeWith(AffineTransformation.newScaling(
						width / frame.width(), height / frame.height()));

		this.isHit = new boolean[width][height];
	}

	public void hit(Point p) {
		if (!this.frame.contains(p)) {
			System.out.println(p);
			return;
		}

		System.out.print(p + " ");
		p = this.transformation.transformPoint(p);
		System.out.println(p);

		this.isHit[(int) (p.x())][(int) (p.y())] = true;
	}

	IFSAccumulator build() {
		return new IFSAccumulator(this.isHit);
	}

}