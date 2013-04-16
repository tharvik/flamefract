package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.Flame.Builder;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;

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
		// the window
		JFrame frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setVisible(true);

		// fractal
		JPanel panelFract = new JPanel();
		panelFract.setLayout(new BorderLayout());
		FlameBuilderPreviewComponent fractal = new FlameBuilderPreviewComponent(this.builder, this.background,
				this.palette, this.frame, this.density);
		panelFract.add(fractal, BorderLayout.CENTER);
		panelFract.setBorder(BorderFactory.createTitledBorder("Fractale"));

		// affine transformation
		JPanel panelAffine = new JPanel();
		panelAffine.setLayout(new BorderLayout());
		AffineTransformationsComponent transformations = new AffineTransformationsComponent(this.builder,
				this.frame);
		panelAffine.add(transformations, BorderLayout.CENTER);
		panelAffine.setBorder(BorderFactory.createTitledBorder("Transformations affines"));

		// transformation list
		JScrollPane transList = new JScrollPane();

		// buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));

		// settings
		JPanel settings = new JPanel();
		settings.setLayout(new BorderLayout());
		settings.add(transList);
		settings.add(buttons);

		// layout
		JPanel upPanel = new JPanel();
		upPanel.setLayout(new GridLayout(1, 2));
		upPanel.add(panelAffine);
		upPanel.add(panelFract);

		JPanel downPanel = new JPanel();
		downPanel.setLayout(new BoxLayout(downPanel, BoxLayout.LINE_AXIS));
		downPanel.add(settings);

		frame.getContentPane().add(upPanel, BorderLayout.CENTER);
		frame.getContentPane().add(downPanel, BorderLayout.PAGE_END);
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
		public Dimension getPreferredSize() {
			return new Dimension(200, 100);
		}

		@Override
		protected void paintComponent(Graphics g0) {
			// TODO seem to be wrong (stretched)
			BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			Rectangle actualFrame = frame.expandToAspectRatio(this.getWidth() / (double) this.getHeight());
			FlameAccumulator accu = this.builder.build().compute(actualFrame, this.getWidth(),
					this.getHeight(), this.density);

			for (int x = 0; x < accu.width(); x++) {
				for (int y = 0; y < accu.height(); y++) {
					final Color c = accu.color(palette, this.background, x, y);
					final int RGB = c.asPackedRGB();
					image.setRGB(x, accu.height() - 1 - y, RGB);
				}
			}

			g0.drawImage(image, 0, 0, null);
		}
	}

	/**
	 * The part of the GUI showing the {@link AffineTransformation} of the
	 * fractal {@link Flame}
	 */
	static class AffineTransformationsComponent extends JComponent {
		private Flame.Builder		builder;
		private Rectangle		frame;
		/**
		 * The selected transformation (draw in red in the component)
		 */
		private int			highlightedTransformationIndex;
		/**
		 * A {@link AffineTransformation} used to move a point in the
		 * frame to the {@link JComponent}
		 */
		private AffineTransformation	transformation;

		/**
		 * Construct an {@link AffineTransformationsComponent} with the
		 * given {@link Builder} to retrieve the needed
		 * {@link AffineTransformation} and the frame to have a correct
		 * sized graph
		 * 
		 * @param builder
		 *                Used to retrieve the needed
		 *                {@link AffineTransformation}
		 * @param frame
		 *                Used to have a correct sized graph
		 */
		public AffineTransformationsComponent(Builder builder, Rectangle frame) {
			this.builder = builder;
			this.frame = frame;
		}

		/**
		 * Return the index of the highlighted
		 * {@link AffineTransformation}
		 * 
		 * @return The index of the highlighted
		 *         {@link AffineTransformation}
		 */
		public int getHighlightedTransformationIndex() {
			return highlightedTransformationIndex;
		}

		public void setHighlightedTransformationIndex(int highlightedTransformationIndex) {
			if (highlightedTransformationIndex < 0
					|| highlightedTransformationIndex >= this.builder.transformationCount()) {
				throw new NoSuchElementException();
			}
			this.highlightedTransformationIndex = highlightedTransformationIndex;
			this.repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 100);
		}

		private void paintGrid(Graphics2D g) {
			for (int x = (int) this.frame.left(); x < this.frame.right(); x++) {

				// Only the x=0 line is white
				if (x == 0) {
					g.setColor(java.awt.Color.WHITE);
				} else {
					g.setColor(new java.awt.Color(new Color(0.9, 0.9, 0.9).asPackedRGB()));
				}

				Point up = new Point(x, this.frame.top());
				Point down = new Point(x, this.frame.bottom());

				up = this.transformation.transformPoint(up);
				down = this.transformation.transformPoint(down);

				Line2D.Double line = new Line2D.Double(down.x(), down.y(), up.x(), up.y());
				g.draw(line);
			}

			for (int y = (int) this.frame.bottom(); y < this.frame.top(); y++) {

				// Only the y=0 line is white
				if (y == 0) {
					g.setColor(java.awt.Color.WHITE);
				} else {
					g.setColor(new java.awt.Color(new Color(0.9, 0.9, 0.9).asPackedRGB()));
				}

				Point left = new Point(this.frame.left(), y);
				Point right = new Point(this.frame.right(), y);

				left = this.transformation.transformPoint(left);
				right = this.transformation.transformPoint(right);

				Line2D.Double line = new Line2D.Double(left.x(), left.y(), right.x(), right.y());
				g.draw(line);
			}
		}

		private void drawTransformation(Graphics2D g, int index) {
			// set some points
			Point xDown = this.builder.affineTransformation(index).transformPoint(new Point(-1, 0));
			Point xUp = this.builder.affineTransformation(index).transformPoint(new Point(1, 0));
			Point xArrowLeft = this.builder.affineTransformation(index).transformPoint(new Point(0.9, 0.1));
			Point xArrowRight = this.builder.affineTransformation(index).transformPoint(
					new Point(0.9, -0.1));

			Point yDown = this.builder.affineTransformation(index).transformPoint(new Point(0, -1));
			Point yUp = this.builder.affineTransformation(index).transformPoint(new Point(0, 1));
			Point yArrowLeft = this.builder.affineTransformation(index)
					.transformPoint(new Point(-0.1, 0.9));
			Point yArrowRight = this.builder.affineTransformation(index)
					.transformPoint(new Point(0.1, 0.9));

			// Change the points in our system
			xDown = this.transformation.transformPoint(xDown);
			xUp = this.transformation.transformPoint(xUp);
			xArrowLeft = this.transformation.transformPoint(xArrowLeft);
			xArrowRight = this.transformation.transformPoint(xArrowRight);

			yDown = this.transformation.transformPoint(yDown);
			yUp = this.transformation.transformPoint(yUp);
			yArrowLeft = this.transformation.transformPoint(yArrowLeft);
			yArrowRight = this.transformation.transformPoint(yArrowRight);

			Line2D.Double line = new Line2D.Double(xDown.x(), xDown.y(), xUp.x(), xUp.y());
			g.draw(line);
			line = new Line2D.Double(xUp.x(), xUp.y(), xArrowLeft.x(), xArrowLeft.y());
			g.draw(line);
			line = new Line2D.Double(xUp.x(), xUp.y(), xArrowRight.x(), xArrowRight.y());
			g.draw(line);

			line = new Line2D.Double(yDown.x(), yDown.y(), yUp.x(), yUp.y());
			g.draw(line);
			line = new Line2D.Double(yUp.x(), yUp.y(), yArrowLeft.x(), yArrowLeft.y());
			g.draw(line);
			line = new Line2D.Double(yUp.x(), yUp.y(), yArrowRight.x(), yArrowRight.y());
			g.draw(line);
		}

		@Override
		protected void paintComponent(Graphics g0) {
			Graphics2D g = (Graphics2D) g0;

			// TODO wrong! doesn't gave the right values (but more
			// or less right)
			this.transformation = AffineTransformation.newTranslation(
					this.frame.center().x() + this.getWidth() / 2.0,
					this.frame.center().y() + this.getHeight() / 2.0);
			this.transformation = this.transformation.composeWith(AffineTransformation.newScaling(
					this.getWidth() / this.frame.width(), -this.getHeight() / this.frame.height()));

			// setup the grid first
			this.paintGrid(g);
			g.setColor(java.awt.Color.BLACK);

			for (int i = 0; i < this.builder.transformationCount(); i++) {

				// we skip the highlighted, because we want to
				// be draw at the end
				if (i == this.highlightedTransformationIndex) {
					continue;
				}

				this.drawTransformation(g, i);
			}

			// draw the last arrow (should be on top)
			g.setColor(java.awt.Color.RED);
			this.drawTransformation(g, this.highlightedTransformationIndex);
		}
	}

	/**
	 * List of transformation used by the scroll bar
	 */
	class TransformationsListModel extends AbstractListModel {

		/**
		 * Used as a prefix to each {@link FlameTransformation}
		 */
		String	text;

		/**
		 * Construct a {@link TransformationsListModel} with the given
		 * text as a prefix to {@link FlameTransformation}
		 * 
		 * @param text
		 *                Used as a prefix to each
		 *                {@link FlameTransformation}
		 */
		public TransformationsListModel(String text) {
			this.text = text;
		}

		@Override
		public Object getElementAt(int index) {
			return text + index;
		}

		@Override
		public int getSize() {
			return FlameMakerGUI.this.builder.transformationCount();
		}

		/**
		 * Add a new identity {@link FlameTransformation} to the
		 * builder, bubble
		 */
		public void addTransformation() {
			double[] array = { 1, 0, 0, 0, 0, 0 };
			FlameTransformation t = new FlameTransformation(AffineTransformation.IDENTITY, array);
			FlameMakerGUI.this.builder.addTransformation(t);
			this.fireIntervalAdded(this, this.getSize() - 1, this.getSize());
		}

		/**
		 * Remove the {@link FlameTransformation} at the given index,
		 * bubble
		 * 
		 * @param i
		 *                The index of the {@link FlameTransformation}
		 *                to remove
		 */
		public void removeTransformation(int i) {
			FlameMakerGUI.this.builder.removeTransformation(i);
			this.fireIntervalAdded(this, this.getSize(), this.getSize() + 1);
		}
	}
}
