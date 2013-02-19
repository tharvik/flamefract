package ch.epfl.flamemaker.ifs;

import java.util.ArrayList;
import java.util.List;
import ch.epfl.flamemaker.geometry2d.*;
import java.util.Random;

public final class IFS {
	private final ArrayList<AffineTransformation> transformations;

	public IFS(List<AffineTransformation> transformations) {
		this.transformations = new ArrayList<AffineTransformation>(
				transformations);
	}

	public IFSAccumulator compute(Rectangle frame, int width, int height,
			int density) {
		Random random = new Random();
		Point p = new Point(0, 0);

		for (int j = 0; j < 20; j++) {
			int i = random.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
		}

		for (int j = 0; j < density; j++) {
			int i = random.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
		}
	}
}
