package ch.epfl.flamemaker.flame;

public class Flame{
	private final List<FlameTransformation> transformations;

	public Flame(List<FlameTransformation> transformations) {
		this.transformations = transformations.clone();
	}

	public FlameAccumulator compute(Rectangle frame, int width, int height,
			int density) {

		Random random = new Random();
		Point p = Point.ORIGIN;
		int m = density * width * height;
		// Copied
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
