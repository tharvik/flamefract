package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	 * The part of the GUI showing the {@link AffineTransformation} of the
	 * fractal {@link Flame}
	 */
	@SuppressWarnings("serial")
	class AffineTransformationsComponent extends JComponent implements Observer {
		/**
		 * The {@link Builder} used to get the needed
		 * {@link AffineTransformation}
		 */
		private final Flame.Builder	builder;
		/**
		 * The scope of the fractale, used to have the same aspect ratio
		 * as the {@link FlameBuilderPreviewComponent}
		 */
		private final Rectangle		frame;
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

		@Override
		public void changedObservedValue() {
			this.setHighlightedTransformationIndex(FlameMakerGUI.this.getSelectedTransformationIndex());
			this.repaint();
		}

		/**
		 * Return the index of the highlighted
		 * {@link AffineTransformation}
		 * 
		 * @return The index of the highlighted
		 *         {@link AffineTransformation}
		 */
		public int getHighlightedTransformationIndex() {
			return this.highlightedTransformationIndex;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 100);
		}

		/**
		 * Set the index of the highlighted {@link AffineTransformation}
		 * 
		 * @param highlightedTransformationIndex
		 *                The index of the newly selected
		 *                {@link AffineTransformation}, as given in the
		 *                {@link Builder} as construction
		 * 
		 * @throws NoSuchElementException
		 *                 if the highlightedTransformationIndex is less
		 *                 than zero or bigger than the max index
		 */
		public void setHighlightedTransformationIndex(int highlightedTransformationIndex) {
			this.highlightedTransformationIndex = highlightedTransformationIndex;
			this.repaint();
		}

		@Override
		protected void paintComponent(Graphics g0) {
			final Graphics2D g = (Graphics2D) g0;
			final Rectangle actualFrame = this.frame.expandToAspectRatio((double) this.getWidth()
					/ (double) this.getHeight());

			// FIXME wrong! doesn't gave the right values (but more
			// or less right) or is it coming from the frame?
			// this.transformation =
			// AffineTransformation.newTranslation(-actualFrame.left(),
			// -actualFrame.top());
			// this.transformation = this.transformation
			// .composeWith(AffineTransformation.newScaling(
			// this.getWidth() / actualFrame.width(),
			// -this.getHeight()
			// / actualFrame.height()));

			this.transformation = AffineTransformation.newTranslation(-actualFrame.left(),
					-actualFrame.top());

			this.transformation = AffineTransformation.newScaling(this.getWidth() / actualFrame.width(),
					-this.getHeight() / actualFrame.height());
			this.transformation = this.transformation.composeWith(AffineTransformation.newTranslation(
					-actualFrame.left(), -actualFrame.top()));

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

		/**
		 * Draw the set of arrows for the {@link Transformation} given
		 * by the index
		 * 
		 * @param g
		 *                The {@link Graphics2D} to draw to
		 * @param index
		 *                The index of the {@link Transformation} to
		 *                draw
		 */
		private void drawTransformation(Graphics2D g, int index) {
			final AffineTransformation buildAffine = this.builder.affineTransformation(index);
			final AffineTransformation rotate = AffineTransformation.newRotation(Math.PI / 2);

			for (int i = 0; i < 2; i++) {

				// [0] = bottom of arrow
				// [1] = top of arrow
				// [2] = left of arrow
				// [3] = right of arrow
				final Point[] points = new Point[4];
				points[0] = new Point(-1, 0);
				points[1] = new Point(1, 0);
				points[2] = new Point(0.9, -0.1);
				points[3] = new Point(0.9, 0.1);

				// rotate the points
				for (int j = 0; j < i; j++) {
					for (int k = 0; k < points.length; k++) {
						points[k] = rotate.transformPoint(points[k]);
					}
				}

				// actually transform the points to look as the
				// should be
				for (int j = 0; j < points.length; j++) {
					points[j] = buildAffine.transformPoint(points[j]);
				}

				// change the points in our system
				for (int j = 0; j < points.length; j++) {
					points[j] = this.transformation.transformPoint(points[j]);
				}

				for (int j = 0, k = 1; k < points.length; k++) {
					final Line2D.Double line = new Line2D.Double(points[j].x(), points[j].y(),
							points[k].x(), points[k].y());
					g.draw(line);
					j = 1;
				}

			}
		}

		/**
		 * Paint the grid in the given {@link Graphics2D}
		 * 
		 * @param g
		 *                The {@link Graphics2D} to draw to
		 */
		private void paintGrid(Graphics2D g) {

			for (int i = 0; i < 2; i++) {

				for (int j = (int) this.frame.left(); j < this.frame.right(); j++) {

					// Only the center line is white
					if (j == 0) {
						g.setColor(java.awt.Color.WHITE);
					} else {
						g.setColor(new java.awt.Color(new Color(0.9, 0.9, 0.9).asPackedRGB()));
					}

					final Point up = this.transformation.transformPoint(new Point(j, this.frame
							.top()));
					final Point down = this.transformation.transformPoint(new Point(j, this.frame
							.bottom()));

					final Line2D.Double line = new Line2D.Double(down.x(), down.y(), up.x(), up.y());
					g.draw(line);
				}

				for (int y = (int) this.frame.bottom(); y < this.frame.top(); y++) {

					// Only the y=0 line is white
					if (y == 0) {
						g.setColor(java.awt.Color.WHITE);
					} else {
						g.setColor(new java.awt.Color(new Color(0.9, 0.9, 0.9).asPackedRGB()));
					}

					final Point left = this.transformation.transformPoint(new Point(this.frame
							.left(), y));
					final Point right = this.transformation.transformPoint(new Point(this.frame
							.right(), y));

					final Line2D.Double line = new Line2D.Double(left.x(), left.y(), right.x(),
							right.y());
					g.draw(line);
				}
			}
		}
	}

	/**
	 * The fractal part of the GUI
	 */
	@SuppressWarnings("serial")
	class FlameBuilderPreviewComponent extends JComponent {

		/**
		 * The {@link Color} of the background we use to build the image
		 */
		private final Color		background;
		/**
		 * The {@link Builder} we are currently working on
		 */
		private final Flame.Builder	builder;
		/**
		 * The number of iteration
		 */
		private final int		density;
		/**
		 * The scope of the fractal
		 */
		private final Rectangle		frame;
		/**
		 * The {@link Palette} we use to build the image
		 */
		private final Palette		palette;

		/**
		 * Construct a new {@link FlameBuilderPreviewComponent} with the
		 * given {@link Builder}, background {@link Color},
		 * {@link Palette}, scope of the fractal and the density of
		 * computation
		 * 
		 * @param builder
		 *                The {@link Builder} containing the fractal
		 * @param background
		 *                The background {@link Color} of the fractal
		 * @param palette
		 *                The {@link Palette} to compute the fractal
		 *                with
		 * @param frame
		 *                The scope of the fractal
		 * @param density
		 *                The density of computation for the fractal
		 */
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
			final BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			// FIXME seem to be wrong (stretched)
			final Rectangle actualFrame = this.frame.expandToAspectRatio(this.getWidth()
					/ (double) this.getHeight());
			final FlameAccumulator accu = this.builder.build().compute(actualFrame, this.getWidth(),
					this.getHeight(), this.density);

			for (int x = 0; x < accu.width(); x++) {
				for (int y = 0; y < accu.height(); y++) {
					final Color c = accu.color(this.palette, this.background, x, y);
					final int RGB = c.asPackedRGB();
					image.setRGB(x, accu.height() - 1 - y, RGB);
				}
			}

			g0.drawImage(image, 0, 0, null);
		}
	}

	/**
	 * Define the concept of observer: the changedObservedValue function in
	 * every {@link Observer} will be fired by the target every time the
	 * value (given by the context) inside the target is changed
	 */
	static interface Observer {
		/**
		 * Will be fired by the target every time the value (given by
		 * the context) inside the target is changed
		 */
		void changedObservedValue();
	}

	/**
	 * List of transformation used by the scroll bar
	 */
	@SuppressWarnings("serial")
	class TransformationsListModel extends AbstractListModel<String> {

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

		/**
		 * Add a new identity {@link FlameTransformation} to the
		 * builder, bubble
		 */
		public void addTransformation() {
			final double[] array = { 1, 0, 0, 0, 0, 0 };
			final FlameTransformation t = new FlameTransformation(AffineTransformation.IDENTITY, array);
			FlameMakerGUI.this.builder.addTransformation(t);
			this.fireIntervalAdded(this, this.getSize() - 2, this.getSize() - 1);
		}

		@Override
		public String getElementAt(int index) {
			return (this.text + (index + 1));
		}

		@Override
		public int getSize() {
			return FlameMakerGUI.this.builder.transformationCount();
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
			this.fireIntervalRemoved(this, i, i + 1);
		}
	}

	/**
	 * The {@link Color} of the background we use to build the image
	 */
	private final Color		background;

	/**
	 * The {@link Builder} we are currently working on
	 */
	private final Flame.Builder	builder;

	/**
	 * The number of iteration
	 */
	private final int		density;

	/**
	 * The scope of the fractal
	 */
	private final Rectangle		frame;

	/**
	 * {@link Set} of the {@link Observer} of the
	 * selectedTransformationIndex
	 */
	private final Set<Observer>	observers;

	/**
	 * The {@link Palette} we use to build the image
	 */
	private final Palette		palette;

	/**
	 * The {@link FlameTransformation} actually selected in the list of
	 * {@link Transformation}
	 */
	private int			selectedTransformationIndex;

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
	 * Construct a {@link FlameMakerGUI} with the default value
	 */
	public FlameMakerGUI() {
		this.builder = FlameMakerGUI.generateSharkFin();
		this.background = Color.BLACK;
		this.palette = new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE));
		this.frame = new Rectangle(new Point(-0.25, 0), 5, 4);
		this.density = 50;
		this.selectedTransformationIndex = 0;
		this.observers = new HashSet<FlameMakerGUI.Observer>();
	}

	/**
	 * Add a new {@link Observer}
	 * 
	 * @param observer
	 *                An {@link Observer} to add to set of known
	 *                {@link Observer}
	 */
	public void addObserver(Observer observer) {
		this.observers.add(observer);
	}

	/**
	 * Return the index of the selected {@link FlameTransformation}
	 * 
	 * @return return The index of the selected {@link FlameTransformation}
	 */
	public int getSelectedTransformationIndex() {
		return this.selectedTransformationIndex;
	}

	/**
	 * Remove the given {@link Observer}
	 * 
	 * @param observer
	 *                An {@link Observer} to remove from the set of known
	 *                {@link Observer}
	 */
	public void removeObserver(Observer observer) {
		this.observers.remove(observer);
	}

	/**
	 * Select a new {@link FlameTransformation}
	 * 
	 * @param selectedTransformationIndex
	 *                The index of the wanted {@link FlameTransformation} as
	 *                in the {@link Builder} we got at construction
	 * 
	 * @throws NoSuchElementException
	 *                 if the index is less than zero or larger than the max
	 *                 index
	 */
	public void setSelectedTransformationIndex(int selectedTransformationIndex) {
		if (selectedTransformationIndex < -1
				|| selectedTransformationIndex > this.builder.transformationCount()) {
			throw new NoSuchElementException();
		}
		this.selectedTransformationIndex = selectedTransformationIndex;

		// warn the observers
		for (final Observer observer : this.observers) {
			observer.changedObservedValue();
		}
	}

	/**
	 * Return a {@link JPanel} with the representation of the
	 * {@link Transformation}
	 * 
	 * @return The {@link JPanel} with the representation of the
	 *         {@link Transformation}
	 */
	private JPanel getPanelAffine() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		final AffineTransformationsComponent transformations = new AffineTransformationsComponent(this.builder,
				this.frame);
		panel.add(transformations, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Transformations affines"));
		this.addObserver(transformations);

		return panel;
	}

	/**
	 * Build a fractal and return the {@link JPanel} containing it
	 * 
	 * @return A {@link JPanel} with the fractal
	 */
	private JPanel getPanelFractal() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		final FlameBuilderPreviewComponent fractal = new FlameBuilderPreviewComponent(this.builder,
				this.background, this.palette, this.frame, this.density);
		panel.add(fractal, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Fractale"));

		return panel;
	}

	/**
	 * Return a {@link JList} build with the given
	 * {@link TransformationsListModel}
	 * 
	 * @param model
	 *                The {@link TransformationsListModel} to use to
	 *                construct the {@link JList}
	 * 
	 * @return A {@link JList} build with the given
	 *         {@link TransformationsListModel}
	 */
	private JList<String> getList(TransformationsListModel model) {
		final JList<String> list = new JList<String>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(3);
		list.setSelectedIndex(this.selectedTransformationIndex);
		list.addListSelectionListener(new ListSelectionListener() {

			@SuppressWarnings("unused")
			@Override
			public void valueChanged(ListSelectionEvent e) {
				FlameMakerGUI.this.setSelectedTransformationIndex(list.getSelectedIndex());
			}
		});

		return list;
	}

	/**
	 * Return the {@link JPanel} with the {@link JButton} to remove or add a
	 * fractal
	 * 
	 * @param list
	 *                The {@link JList} build with the
	 *                {@link TransformationsListModel}
	 * 
	 * @return The {@link JPanel} with the {@link JButton} to remove or add
	 *         a fractal
	 */
	private JPanel getButtons(final JList<String> list) {
		final JButton remove = new JButton("Supprimer");
		final JButton add = new JButton("Ajouter");
		final TransformationsListModel model = (TransformationsListModel) list.getModel();

		remove.addActionListener(new ActionListener() {

			@SuppressWarnings("unused")
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = FlameMakerGUI.this.getSelectedTransformationIndex();
				model.removeTransformation(index);
				list.setSelectedIndex(index == model.getSize() ? index - 1 : index);

				if (model.getSize() == 1) {
					remove.setEnabled(false);
				}
			}
		});

		add.addActionListener(new ActionListener() {

			@SuppressWarnings("unused")
			@Override
			public void actionPerformed(ActionEvent e) {
				model.addTransformation();
				list.setSelectedIndex(model.getSize() - 1);

				if (!remove.isEnabled()) {
					remove.setEnabled(true);
				}
			}
		});

		final JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		buttons.add(remove);
		buttons.add(add);

		return buttons;
	}

	/**
	 * Return the {@link JPanel} with all the settings
	 * 
	 * @return The {@link JPanel} with all the settings
	 */
	private JPanel getSettings() {

		final TransformationsListModel model = new TransformationsListModel("Transformation n° ");
		final JList<String> list = this.getList(model);

		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(this.getButtons(list), BorderLayout.PAGE_END);

		return panel;
	}

	/**
	 * Return the {@link JPanel} containing the {@link JPanel} with the
	 * {@link AffineTransformationsComponent} and
	 * {@link FlameBuilderPreviewComponent}
	 * 
	 * @return The {@link JPanel} containing the {@link JPanel} with the
	 *         {@link AffineTransformationsComponent} and
	 *         {@link FlameBuilderPreviewComponent}
	 */
	private JPanel getUpPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.add(this.getPanelAffine());
		panel.add(this.getPanelFractal());

		return panel;
	}

	/**
	 * Return the {@link JPanel} containing mainly the settings
	 * 
	 * @return The {@link JPanel} containing mainly the settings
	 */
	private JPanel getDownPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(this.getSettings());

		return panel;
	}

	/**
	 * Generate the GUI, used by {@link FlameMaker}
	 */
	public void start() {
		final JFrame frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		JPanel upPanel = this.getUpPanel();
		JPanel downPanel = this.getDownPanel();

		frame.getContentPane().add(upPanel, BorderLayout.CENTER);
		frame.getContentPane().add(downPanel, BorderLayout.PAGE_END);

		frame.pack();
		frame.setVisible(true);
	}
}
