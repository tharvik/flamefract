package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class Flame {
	private final List<FlameTransformation>	transformations;

	public Flame(List<FlameTransformation> transformations) {
		this.transformations = new ArrayList<FlameTransformation>(transformations);
	}

	public FlameAccumulator compute(Rectangle frame, int width, int height, int density) {

		Random random = new Random();
		Point p = Point.ORIGIN;
		int m = density * width * height;
		FlameAccumulator.Builder image = new FlameAccumulator.Builder(frame, width, height);

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

	public static class Builder {
		private ArrayList<FlameTransformation.Builder>	list;

		public Builder(Flame flame) {
			this.list = new ArrayList<FlameTransformation.Builder>();
		}

		public int transformationCount() {
			return this.list.size();
		}

		public void addTransformation(FlameTransformation transformation) {
			this.list.add(new FlameTransformation.Builder(transformation));
		}

		public AffineTransformation affineTransformation(int index) {
			this.checkIndex(index);
			return this.list.get(index).getAffineTransformation();
		}

		public void setAffineTransformation(int index, AffineTransformation newTransformation) {
			this.checkIndex(index);
			this.list.get(index).setAffineTransformation(newTransformation);
		}

		private int getVariationIndex (Variation variation) {
			return variation.index();
		}

		public void setVariationWeight(int index, Variation variation, double newWeight) {
			// TODO
		}

		public void removeTransformation(int index) {
			this.checkIndex(index);
			this.list.remove(index);
		}
		
		private void checkIndex (int index) {
			if (index < 0 || index > this.transformationCount()) {
				throw new IndexOutOfBoundsException();
			}
		}

		public Flame build() {
			ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
			for (FlameTransformation.Builder builder : this.list) {
				transformations.add(builder.build());
			}
			
			return new Flame(transformations);
		}
	}
}
