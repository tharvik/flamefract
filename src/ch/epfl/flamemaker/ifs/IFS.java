package ch.epfl.flamemaker.ifs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.*;

public final class IFS {
	private final List<AffineTransformation> transformations;
	
	/**
	 * Creates a fractal with the given transformations
	 * @param transformations The list of the transformations of the fractal
	 */
	public IFS(List<AffineTransformation> transformations) {
		this.transformations = new ArrayList<AffineTransformation>(transformations);
	}
	
	public IFSAccumulator compute(Rectangle frame, int width, int height, int density) {
		
		Random random = new Random();
		Point p = Point.ORIGIN;
		int m = density * width * height;
		IFSAccumulatorBuilder image = new IFSAccumulatorBuilder(frame, width, height);
		
		if (this.transformations.size() == 0) {
			return image.build();
		}
		
		for (int j = 0; j < 20; j++) {
			int i = random.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
		}
		
		for (int j = 0; j < m; j++) {
			int i = random.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
			image.hit(p);
		}
		
		return image.build();
	}
}
