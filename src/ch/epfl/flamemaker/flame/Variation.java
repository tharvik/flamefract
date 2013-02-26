package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

public abstract class Variation implements Transformation {
        private final String name;
        private final int index;

        private Variation(int index, String name) {
		this.name = name;
		this.index = index;
        }

        public String name () {
                return this.name;
        }

        public int index() {
                return this.index;
        }

        abstract public Point transformPoint(Point p);

        public final static List<Variation> ALL_VARIATIONS =
                Arrays.asList(new Variation(0, "Linear") {
                        public Point transformPoint(Point p) {
                                // TODO
                        }
                },
                // TODO
                );
}
