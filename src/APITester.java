public class APITester {

	private static ClassLoader	classLoader	= APITester.class.getClassLoader();

	public static void main(String[] args) {
		System.out.println("Welcome to the API tester for the PTI project.");
		System.out.println("For any issue with the tool, please contact Michele Catasta "
				+ "(michele.catasta@epfl.ch).\n");

		// Replicating JUnit functionalities just to avoid the classpath
		// dependency...
		try {
			APITester.testStage1();
			APITester.testStage2();
			APITester.testStage3();
			APITester.testStage4();
			System.out.println("All tests passed!");
		} catch (final ClassNotFoundException e) {
			System.err.println("The API tester failed. If you still want to submit your project "
					+ "with errors, use the \"jar-emergency\" Ant target.");
			System.exit(42);
		}
	}

	public static void testStage1() throws ClassNotFoundException {
		APITester.testClasses(new String[] { "geometry2d.Point", "geometry2d.Rectangle",
				"geometry2d.Transformation", "geometry2d.AffineTransformation" });
	}

	public static void testStage2() throws ClassNotFoundException {
		// no mandatory classes
	}

	public static void testStage3() throws ClassNotFoundException {
		APITester.testClasses(new String[] { "flame.Flame", "flame.FlameTransformation",
				"flame.FlameAccumulator", "flame.Variation" });
	}

	public static void testStage4() throws ClassNotFoundException {
		APITester.testClasses(new String[] { "color.Color", "color.Palette", "color.InterpolatedPalette",
				"color.RandomPalette", "flame.FlamePPMMaker" });
	}

	private static void testClasses(String[] classNames) throws ClassNotFoundException {
		for (final String clazz : classNames) {
			try {
				APITester.classLoader.loadClass("ch.epfl.flamemaker." + clazz);
			} catch (final ClassNotFoundException e) {
				System.err.println("Could not find the following class: ch.epfl.flamemaker." + clazz);
				throw e;
			}
		}
	}
}
