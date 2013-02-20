package ch.epfl.flamemaker.ifs;

public class IFSAccumulator {
	private final boolean[][] isHit;

	public IFSAccumulator(boolean[][] isHit) {
		this.isHit = isHit.clone(); // ! Deeply copied
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
