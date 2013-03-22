package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class Flame {
	private final List<FlameTransformation>	transformations;
	private final double[]			arrayIndex;

	public Flame(List<FlameTransformation> transformations) {
		this.transformations = new ArrayList<FlameTransformation>(transformations);

		// TODO Ugly!
		this.arrayIndex = new double[this.transformations.size()];
		if (this.arrayIndex.length > 0) {
			arrayIndex[0] = 0;

			if (this.arrayIndex.length > 1) {
				arrayIndex[1] = 1;

				if (this.arrayIndex.length > 2) {
					for (int i = 2; i < arrayIndex.length; i++) {
						arrayIndex[i] = ((i - (Math.pow(2,
								Math.floor(Math.log(i) / Math.log(2))))) * 2 + 1)
								/ (Math.pow(2, Math.ceil(Math.log(i) / Math.log(2))));
					}
				}
			}
		}
	}

	public FlameAccumulator compute(Rectangle frame, int width, int height, int density) {

		Random rand = new Random(2013);
		Point p = Point.ORIGIN;
		int m = density * width * height;
		FlameAccumulator.Builder image = new FlameAccumulator.Builder(frame, width, height);

		if (this.transformations.size() == 0) {
			return image.build();
		}

		double lastColor = 0;
		for (int j = 0; j < 20; j++) {
			int i = rand.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);
			lastColor = (this.arrayIndex[i] + lastColor) / 2.0;
		}

		for (int j = 0; j < m; j++) {
			int i = rand.nextInt(this.transformations.size());
			p = this.transformations.get(i).transformPoint(p);

			// TODO wrong sens
			lastColor = (this.arrayIndex[i] + lastColor) / 2.0;
			image.hit(p, lastColor);
		}

		return image.build();
	}

	public static class Builder {
		private ArrayList<FlameTransformation.Builder>	list;

		public Builder(Flame flame) {
			this.list = new ArrayList<FlameTransformation.Builder>();
			for (FlameTransformation flameTransformation : flame.transformations) {
				this.list.add(new FlameTransformation.Builder(flameTransformation));
			}
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

		public void setVariationWeight(int index, Variation variation, double newWeight) {
			checkIndex(index);
			this.list.get(index).setVariationWeight(variation.index(), newWeight);
		}

		public void removeTransformation(int index) {
			this.checkIndex(index);
			this.list.remove(index);
		}

		private void checkIndex(int index) {
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
