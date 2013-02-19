package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {

	public IFSAccumulatorBuilder(Rectangle frame, int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException();
		}
	}

	public void hit(Point p) {

	}

}