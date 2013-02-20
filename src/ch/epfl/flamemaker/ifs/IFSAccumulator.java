package ch.epfl.flamemaker.ifs;

public class IFSAccumulator {
	private final boolean[][] isHit;

	public IFSAccumulator(boolean[][] isHit) {
		this.isHit = new boolean[isHit.length][isHit[0].length];

		for (int x = 0; x < isHit.length; x++) {
			for (int y = 0; y < isHit[0].length; y++) {
				this.isHit[x][y] = isHit[x][y];
			}
		}
	}

	public int width() {
		return isHit.length;
	}

	public int height() {
		return isHit[0].length;
	}

	public boolean isHit(int x, int y) {
		if (x > this.width() || x < 0 || y > this.height() || y < 0) {
			throw new IndexOutOfBoundsException();
		}

		return (this.isHit[x][y]);
	}

}
