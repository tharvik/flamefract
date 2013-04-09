package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.Flame.Builder;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * The GUI, use much of {@link Flame} in internal
 * 
 * @see Flame
 */
public class FlameMakerGUI {
	/**
	 * The {@link Builder} we are currently working on
	 */
	private Flame.Builder	builder;
	/**
	 * The {@link Color} of the background we use to build the image
	 */
	private Color		background;
	/**
	 * The {@link Palette} we use to build the image
	 */
	private Palette		palette;
	/**
	 * The scope of the fractal
	 */
	private Rectangle	frame;
	/**
	 * The number of iteration
	 */
	private int		density;

	/**
	 * Construct a {@link FlameMakerGUI} with the default value
	 */
	public FlameMakerGUI() {
		this.builder = FlameMakerGUI.generateSharkFin();
		this.background = Color.BLACK;
		this.palette = new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE));
		this.frame = new Rectangle(new Point(-0.25, 0), 5, 4);
		this.density = 50;
	}

	/**
	 * Generate the GUI, used by {@link FlameMaker}
	 */
	public void start() {
		JFrame frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setVisible(true);

		JPanel panelFract = new JPanel();
		panelFract.setLayout(new BorderLayout());
		JLabel label = new JLabel("(ici viendront les transformations)");
		label.setBorder(BorderFactory.createTitledBorder("Transformations affines"));
		panelFract.add(label);

		JPanel panelAffine = new JPanel();
		panelAffine.setLayout(new BorderLayout());
		label = new JLabel("(ici viendra la fractale)");
		label.setBorder(BorderFactory.createTitledBorder("Fractale"));

		panelAffine.add(label);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		panel.add(panelAffine);
		panel.add(panelFract);

		frame.getContentPane().add(panel, BorderLayout.CENTER);

		frame.pack();
	}

	/**
	 * Generate the Shark Fin fractal
	 * 
	 * @return A {@link Flame} containing the fractal
	 */
	private static Flame.Builder generateSharkFin() {
		final Flame.Builder builder = new Flame.Builder(new Flame(new ArrayList<FlameTransformation>()));
		final double[][] array = { { 1, 0.1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0.8, 1 }, { 1, 0, 0, 0, 0, 0 } };

		AffineTransformation affine = new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795,
				-0.4113508, 0.8);
		builder.addTransformation(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2);
		builder.addTransformation(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9);
		builder.addTransformation(new FlameTransformation(affine, array[2]));

		return builder;
	}

	/**
	 * The fractal part of the GUI
	 */
	static class FlameBuilderPreviewComponent extends JComponent {

		/**
		 * The {@link Builder} we are currently working on
		 */
		private Flame.Builder	builder;
		/**
		 * The {@link Color} of the background we use to build the image
		 */
		private Color		background;
		/**
		 * The {@link Palette} we use to build the image
		 */
		private Palette		palette;
		/**
		 * The scope of the fractal
		 */
		private Rectangle	frame;
		/**
		 * The number of iteration
		 */
		private int		density;

		public FlameBuilderPreviewComponent(Flame.Builder builder, Color background, Palette palette,
				Rectangle frame, int density) {
			this.builder = builder;
			this.background = background;
			this.palette = palette;
			this.frame = frame;
			this.density = density;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphic = (Graphics2D) g;
			
			
		}
	}
}
