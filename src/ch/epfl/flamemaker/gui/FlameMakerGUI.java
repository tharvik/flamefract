package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.extra.Preferences;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlamePPMMaker;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.FlameTransformation.Builder;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;
import ch.epfl.flamemaker.gui.ObservableFlameBuilder.Observer;

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
	private class AffineTransformationsComponent extends JComponent implements SelectionObserver, Observer {

		/**
		 * The {@link Builder} used to get the needed
		 * {@link AffineTransformation}
		 */
		private final ObservableFlameBuilder	builder;
		/**
		 * The scope of the fractal, used to have the same aspect ratio
		 * as the {@link FlameBuilderPreviewComponent}
		 */
		private final Rectangle			frame;
		/**
		 * The selected transformation (drawn in red in the component)
		 */
		private int				highlightedTransformationIndex;
		/**
		 * A {@link AffineTransformation} used to move a point in the
		 * frame to the {@link JComponent}
		 */
		private AffineTransformation		transformation;

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
		public AffineTransformationsComponent(final ObservableFlameBuilder builder, final Rectangle frame) {
			this.builder = builder;
			this.frame = frame;
			this.highlightedTransformationIndex = 0;

			this.builder.addObserver(this);
		}

		@Override
		public void changedBuilder() {
			this.repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 100);
		}

		@Override
		public void valueChanged() {
			this.highlightedTransformationIndex = FlameMakerGUI.this.getSelectedTransformationIndex();
			this.repaint();
		}

		@Override
		protected void paintComponent(final Graphics g0) {
			final Graphics2D g = (Graphics2D) g0;
			final Rectangle actualFrame = this.frame.expandToAspectRatio((double) this.getWidth()
					/ (double) this.getHeight());
			this.transformation = AffineTransformation.newScaling(this.getWidth() / actualFrame.width(),
					-this.getHeight() / actualFrame.height());
			this.transformation = this.transformation.composeWith(AffineTransformation.newTranslation(
					-actualFrame.left(), -actualFrame.top()));

			// setup the grid first
			this.paintGrid(g);
			g.setColor(java.awt.Color.BLACK);

			for (int i = 0; i < this.builder.transformationCount(); i++) {

				// we skip the highlighted, because we want it
				// to be drawn at the end
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
		private void drawTransformation(final Graphics2D g, final int index) {
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
		private void paintGrid(final Graphics2D g) {

			final Rectangle actualFrame = this.frame.expandToAspectRatio((double) this.getWidth()
					/ (double) this.getHeight());

			for (int i = 0; i < 2; i++) {

				for (int j = (int) actualFrame.left(); j < actualFrame.right(); j++) {

					// Only the center line is white
					if (j == 0) {
						g.setColor(java.awt.Color.WHITE);
					} else {
						g.setColor(new java.awt.Color(new Color(0.9, 0.9, 0.9).asPackedRGB()));
					}

					final Point up = this.transformation.transformPoint(new Point(j, actualFrame
							.top()));
					final Point down = this.transformation.transformPoint(new Point(j, actualFrame
							.bottom()));

					final Line2D.Double line = new Line2D.Double(down.x(), down.y(), up.x(), up.y());
					g.draw(line);
				}

				for (int y = (int) actualFrame.bottom(); y < actualFrame.top(); y++) {

					// Only the y=0 line is white
					if (y == 0) {
						g.setColor(java.awt.Color.WHITE);
					} else {
						g.setColor(new java.awt.Color(new Color(0.9, 0.9, 0.9).asPackedRGB()));
					}

					final Point left = this.transformation.transformPoint(new Point(actualFrame
							.left(), y));
					final Point right = this.transformation.transformPoint(new Point(actualFrame
							.right(), y));

					final Line2D.Double line = new Line2D.Double(left.x(), left.y(), right.x(),
							right.y());
					g.draw(line);
				}
			}
		}
	}

	/**
	 * The part of the GUI showing the {@link AffineTransformation} of the
	 * fractal {@link Flame}
	 */
	@SuppressWarnings("serial")
	private static class FlameBuilderPreviewComponent extends JComponent implements Observer {

		/**
		 * Represent a really simple chronometer, you can start it, stop
		 * it and know the time it took.
		 */
		private static class Chrono {
			/**
			 * The time of start
			 */
			private long	begin;
			/**
			 * The time of end
			 */
			private long	end;

			/**
			 * Construct a {@link Chrono} with the fields as zero
			 */
			public Chrono() {
				this.begin = 0;
				this.end = 0;
			}

			/**
			 * Start the {@link Chrono}
			 */
			public void start() {
				this.begin = System.currentTimeMillis();
			}

			/**
			 * Stop the {@link Chrono}
			 */
			public void stop() {
				this.end = System.currentTimeMillis();
			}

			/**
			 * Return the time measured by the {@link Chrono}
			 * 
			 * @return The time measured by the {@link Chrono}
			 */
			public long time() {
				return this.end - this.begin;
			}
		}

		/**
		 * The {@link ch.epfl.flamemaker.flame.FlameAccumulator.Builder}
		 * use in computation
		 */
		private FlameAccumulator.Builder	accuBuilder;
		/**
		 * The background {@link Color}
		 */
		private final Color			background;
		/**
		 * The {@link Builder} used to get the needed
		 * {@link AffineTransformation}
		 */
		private final ObservableFlameBuilder	builder;

		/**
		 * The builder we use to compute, used to know if we have
		 * something new
		 */
		private ObservableFlameBuilder		computedBuilder;
		/**
		 * The density of the computation
		 */
		private final int			density;

		/**
		 * The scope of the fractal, used to have the same aspect ratio
		 * as the {@link FlameBuilderPreviewComponent}
		 */
		private final Rectangle			frame;
		/**
		 * A {@link Logger} used to warn of too high refresh rate
		 */
		private final Logger			logger;
		/**
		 * The {@link Palette} used to compute
		 */
		private final Palette			palette;

		/**
		 * The refresh time
		 */
		private int				refresh;

		/**
		 * The number of points computed every time
		 */
		private int				step;
		/**
		 * The {@link Timer} used to refresh the GUI
		 */
		private Timer				timer;

		/**
		 * The number of so-far computed points
		 */
		private int				totalDensity;

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
		private FlameBuilderPreviewComponent(final ObservableFlameBuilder builder, final Color background,
				final Palette palette, final Rectangle frame, final int density) {
			this.builder = builder;
			this.background = background;
			this.palette = palette;
			this.frame = frame;
			this.density = density;
			this.refresh = Preferences.values.refresh;
			// Use a low value just to have something to test
			this.step = 1000;
			this.logger = Logger.getLogger(FlameBuilderPreviewComponent.class.getName());

			this.builder.addObserver(this);
			this.computedBuilder = new ObservableFlameBuilder(this.builder);
		}

		@Override
		public void changedBuilder() {

			if (!this.builder.equals(this.computedBuilder)) {
				this.computedBuilder = new ObservableFlameBuilder(this.builder);
				this.timer.stop();
				this.repaint();
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 100);
		}

		@Override
		protected void paintComponent(final Graphics g) {

			final int width = this.getWidth(), height = this.getHeight();

			if (this.timer != null && this.accuBuilder != null) {
				final FlameAccumulator build = this.accuBuilder.build();
				if ((build.width() != this.getWidth() || build.height() != this.getHeight())) {
					this.timer.stop();
				}
			}

			if (this.timer == null || !this.timer.isRunning()) {
				final Rectangle actualFrame = this.frame.expandToAspectRatio(width / (double) height);
				this.accuBuilder = new FlameAccumulator.Builder(actualFrame, width, height);

				this.paintAccuBuilder(g);

				this.totalDensity = 0;
				this.step = 1000;
				this.timer = new Timer(Preferences.values.refresh, new ActionListener() {

					@Override
					public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
						FlameBuilderPreviewComponent.this.repaint();
					}
				});
				this.timer.start();

			} else if (this.totalDensity < this.density * width * height) {
				this.totalDensity += this.step;
				final Chrono clock = new Chrono();
				clock.start();
				this.builder.build().compute(this.step, this.accuBuilder);
				this.paintAccuBuilder(g);
				this.paintLoading(g, (double) this.totalDensity / (this.density * width * height));
				clock.stop();

				// Accurate the step
				this.step *= (this.refresh / (double) clock.time());
				if ((this.refresh / (double) clock.time()) < 1
						&& this.step < Preferences.values.threshold) {

					this.logger.log(Level.WARNING,
							"Your time setting (the refresh rate) is too low, and thus, we can't keep up. We're adjusting it for now, but consider adding an higher value to the preferences.");
					this.refresh += 100;
					this.step = 1000;
				}

			} else {
				this.timer.stop();
				this.paintAccuBuilder(g);
			}
		}

		/**
		 * Paint the actual state of the
		 * {@link ch.epfl.flamemaker.flame.FlameAccumulator.Builder}
		 * 
		 * @param g
		 *                The {@link Graphics} to draw to
		 */
		private void paintAccuBuilder(final Graphics g) {

			if (this.accuBuilder == null) {
				return;
			}

			final FlameAccumulator accu = this.accuBuilder.build();
			final BufferedImage image = new BufferedImage(accu.width(), accu.height(),
					BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < accu.width(); x++) {
				for (int y = 0; y < accu.height(); y++) {
					final Color c = accu.color(this.palette, this.background, x, y);
					final int RGB = c.asPackedRGB();
					image.setRGB(x, accu.height() - 1 - y, RGB);
				}
			}
			g.drawImage(image, 0, 0, null);
		}

		/**
		 * Draw the loading bar while computing the fractal
		 * 
		 * @param g
		 *                The {@link Graphics} to draw to
		 * @param value
		 *                The value, 0 means nothing done, 1 means done
		 */
		private void paintLoading(final Graphics g, final double value) {
			g.setColor(new java.awt.Color(0xFF)); // blue
			g.fillRect(0, this.getHeight() - 3, (int) (value * this.getWidth()), 3);
		}
	}

	/**
	 * The enum describing the menus. It contains the items of the menu too
	 */
	private static enum Menus {

		/**
		 * The file menu, with a readable name
		 */
		FILE("Fichier"),
		/**
		 * The view menu, with a readable name
		 */
		VIEW("Affichage");

		/**
		 * All the items if the menus
		 */
		public static enum Items {
			/**
			 * Reset the fractal to his default state
			 */
			NEW_FRACTAL(Menus.FILE, 0, "Nouveau", KeyEvent.VK_N, 0),
			/**
			 * Save the actual state of the program
			 */
			SAVE_CONF(Menus.FILE, 2, "Sauver", KeyEvent.VK_S, 0),
			/**
			 * Export the resulting image
			 */
			SAVE_IMAGE(Menus.FILE, 3, "Sauver l'image", KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK),
			/**
			 * Exit the program
			 */
			EXIT(Menus.FILE, 5, "Quitter", KeyEvent.VK_Q, 0),
			/**
			 * Put the fractal in full screen
			 */
			FULLSCREEN(Menus.VIEW, 0, "Plein écran", KeyEvent.VK_F, 0);

			/**
			 * The key to react to
			 */
			private final int	keyEvent;

			/**
			 * The {@link Menus} the item should be
			 */
			private final Menus	menu;

			/**
			 * The modifier for the key, to which we add the
			 * CTRL_DOWN_MASK
			 */
			private final int	modifier;

			/**
			 * A readable name
			 */
			private final String	name;

			/**
			 * The position of the item (if there is a gap, let's
			 * put a separator
			 */
			private final int	pos;

			/**
			 * Construct a new menu item with the given values
			 * 
			 * @param menu
			 *                The {@link Menus} the item should be
			 * @param pos
			 *                The position of the item (if there is
			 *                a gap, let's put a separator
			 * @param name
			 *                A readable name
			 * @param keyEvent
			 *                The key to react to
			 * @param modifier
			 *                The modifier for the key, to which we
			 *                add the CTRL_DOWN_MASK
			 */
			private Items(final Menus menu, final int pos, final String name, final int keyEvent,
					final int modifier) {
				this.menu = menu;
				this.name = name;
				this.keyEvent = keyEvent;
				this.pos = pos;
				this.modifier = modifier;
			}

			/**
			 * Return the {@link KeyEvent} to react to
			 * 
			 * @return The {@link KeyEvent} to react to
			 */
			public int getKeyEvent() {
				return this.keyEvent;
			}

			/**
			 * @return The {@link Menus} to which it should be
			 */
			public Menus getMenu() {
				return this.menu;
			}

			/**
			 * Return the modifier for the key
			 * 
			 * @return The modifier for the key
			 */
			public int getModifier() {
				return this.modifier;
			}

			/**
			 * Return the readable name for the item
			 * 
			 * @return The readable name for the item
			 */
			public String getName() {
				return this.name;
			}

			/**
			 * Return the position in the menu, if there is a gap,
			 * then it should have a separator
			 * 
			 * @return The position in the menu
			 */
			public int getPos() {
				return this.pos;
			}
		}

		/**
		 * A readable name
		 */
		private final String	name;

		/**
		 * Construct a new Menus with the given name
		 * 
		 * @param name
		 *                The readable name of the menu
		 */
		private Menus(final String name) {
			this.name = name;
		}

		/**
		 * Return the readable name of the menu
		 * 
		 * @return The readable name of the menu
		 */
		public String getName() {
			return this.name;
		}
	}

	/**
	 * Define the concept of observer: the changedObservedValue function in
	 * every {@link Observer} will be fired by the target every time the
	 * value (given by the context) inside the target is changed
	 */
	private static interface SelectionObserver {
		/**
		 * Will be fired by the target every time the value (given by
		 * the context) inside the target is changed
		 */
		void valueChanged();
	}

	/**
	 * List of transformation used by the scroll bar
	 */
	@SuppressWarnings("serial")
	private class TransformationsListModel extends AbstractListModel<String> {

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
		public TransformationsListModel(final String text) {
			this.text = text;
		}

		/**
		 * Add a new identity {@link FlameTransformation} to the
		 * builder, bubble
		 */
		public void addTransformation() {
			final double[] array = { 1, 0, 0, 0, 0, 0 };
			final FlameTransformation trans = new FlameTransformation(AffineTransformation.IDENTITY, array);
			FlameMakerGUI.this.builder.addTransformation(trans);

			this.fireIntervalAdded(this, this.getSize() - 1, this.getSize());
		}

		@Override
		public String getElementAt(final int index) {
			return this.text + (index + 1);
		}

		@Override
		public int getSize() {
			return FlameMakerGUI.this.builder.transformationCount();
		}

		/**
		 * Remove the {@link FlameTransformation} at the given index,
		 * bubble
		 * 
		 * @param index
		 *                The index of the {@link FlameTransformation}
		 *                to remove
		 */
		public void removeTransformation(final int index) {
			FlameMakerGUI.this.builder.removeTransformation(index);
			FlameMakerGUI.this.setSelectedTransformationIndex(index == FlameMakerGUI.this.builder
					.transformationCount() ? index - 1 : index);

			this.fireIntervalRemoved(this, index, index);
		}
	}

	/**
	 * The {@link Color} of the background we use to build the image
	 */
	private final Color			background;

	/**
	 * The {@link Builder} we are currently working on
	 */
	private final ObservableFlameBuilder	builder;

	/**
	 * The array of the add and remove buttons
	 */
	private JButton[]			buttons;

	/**
	 * The number of iteration
	 */
	private final int			density;

	/**
	 * The scope of the fractal
	 */
	private final Rectangle			frame;

	/**
	 * {@link Set} of the {@link Observer} of the
	 * selectedTransformationIndex
	 */
	private final Set<SelectionObserver>	observers;

	/**
	 * The {@link Palette} we use to build the image
	 */
	private final Palette			palette;

	/**
	 * The {@link FlameTransformation} actually selected in the list of
	 * {@link Transformation}
	 */
	private int				selectedTransformationIndex;

	/**
	 * Return an {@link AffineTransformation} at the given position and with
	 * the given value
	 * 
	 * @param i
	 *                The vertical position in the array
	 * @param j
	 *                The horizontal position in the array
	 * @param value
	 *                The value to use in the {@link AffineTransformation}
	 * 
	 * @return An {@link AffineTransformation} at the given position and
	 *         with the given value
	 */
	static private AffineTransformation getAffineTransformation(final int i, final int j, final double value) {
		final int val = (i << 2) + j;
		switch (val) {

		case (0 << 2) + 0:
		case (0 << 2) + 1:
			return AffineTransformation.newTranslation((val % 2 == 0 ? -value : value), 0);
		case (0 << 2) + 2:
		case (0 << 2) + 3:
			return AffineTransformation.newTranslation(0, (val % 2 == 0 ? -value : value));

		case (1 << 2) + 0:
		case (1 << 2) + 1:
			return AffineTransformation.newRotation((val % 2 == 0 ? value : -value) / 180);

		case (2 << 2) + 0:
		case (2 << 2) + 1:
			return AffineTransformation.newScaling((val % 2 == 0 ? value : 1 / value), 1);
		case (2 << 2) + 2:
		case (2 << 2) + 3:
			return AffineTransformation.newScaling(1, (val % 2 == 0 ? value : 1 / value));

		case (3 << 2) + 0:
		case (3 << 2) + 1:
			return AffineTransformation.newShearX((val % 2 == 0 ? -value : value));
		case (3 << 2) + 2:
		case (3 << 2) + 3:
			return AffineTransformation.newShearY((val % 2 == 0 ? -value : value));

		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Construct a {@link FlameMakerGUI} with the values in the
	 * {@link Preferences}
	 */
	public FlameMakerGUI() {
		this.builder = new ObservableFlameBuilder(Preferences.values.builder);
		this.background = Preferences.values.background;
		this.palette = Preferences.values.palette;
		this.frame = Preferences.values.frame;
		this.density = Preferences.values.density;

		this.selectedTransformationIndex = 0;
		this.observers = new HashSet<SelectionObserver>();
	}

	/**
	 * Add a new {@link SelectionObserver} to the set of known
	 * {@link SelectionObserver}
	 * 
	 * @param observer
	 *                The {@link SelectionObserver} to add
	 */
	public void addObserver(final SelectionObserver observer) {
		this.observers.add(observer);
	}

	/**
	 * Return the index of the actually selected {@link Transformation}
	 * 
	 * @return The index of the actually selected {@link Transformation}
	 */
	public int getSelectedTransformationIndex() {
		return this.selectedTransformationIndex;
	}

	/**
	 * Remove the given {@link SelectionObserver} of the set of known
	 * {@link SelectionObserver}
	 * 
	 * @param observer
	 *                The {@link SelectionObserver} to remove
	 */
	public void removeObserver(final SelectionObserver observer) {
		this.observers.remove(observer);
	}

	/**
	 * Select a new {@link Transformation}
	 * 
	 * @param selectedTransformationIndex
	 *                The {@link Transformation} to select
	 */
	public void setSelectedTransformationIndex(final int selectedTransformationIndex) {
		this.selectedTransformationIndex = selectedTransformationIndex;

		for (final SelectionObserver observer : this.observers) {
			observer.valueChanged();
		}
	}

	/**
	 * Generate the GUI, used by {@link FlameMaker}
	 */
	public void start() {
		final JFrame frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(this.getUpPanel(), BorderLayout.CENTER);
		frame.getContentPane().add(this.getDownPanel(), BorderLayout.PAGE_END);

		frame.setJMenuBar(this.getMenuBar());

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Return an {@link ActionListener} for the given {@link Menus.Items}
	 * 
	 * @param m
	 *                The {@link Menus.Items} to retrieve
	 * 
	 * @return a new {@link ActionListener} to use at the given place in the
	 *         array
	 */
	private ActionListener getActionListener(final Menus.Items m) {

		class Compute extends SwingWorker<Void, Void> {

			private final File	file;
			private final JFrame	window;

			public Compute(final File file, final JFrame window) {
				this.file = file;
				this.window = window;
			}

			@Override
			protected Void doInBackground() {
				final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				final int m = d.height * d.width * FlameMakerGUI.this.density;

				final JProgressBar bar = new JProgressBar(0, m);
				bar.setString("Calcul de l'image");
				bar.setStringPainted(true);

				final JPanel panel = new JPanel();
				panel.add(bar);

				this.window.setTitle("Sauvegarde de l'image");
				this.window.add(panel);
				this.window.pack();
				this.window.setVisible(true);

				final Rectangle actualFrame = FlameMakerGUI.this.frame.expandToAspectRatio(d.width
						/ (double) d.height);
				final FlameAccumulator.Builder accuBuilder = new FlameAccumulator.Builder(actualFrame,
						d.width, d.height);
				final Flame flame = FlameMakerGUI.this.builder.build();

				for (int total = 0; total < m; total += 5000) {
					flame.compute(5000, accuBuilder);
					bar.setValue(total / 2);
				}

				try {
					bar.setString("Écriture de l'image");
					final PrintStream stream = new PrintStream(this.file);
					final FlameAccumulator accu = accuBuilder.build();

					bar.setMaximum(accu.height() * 2);

					for (int i = 0; i < accu.height(); i++) {
						FlamePPMMaker.writeToPPMIncremental(accu, stream, i);
						bar.setValue(accu.height() + i);
					}

				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				}

				return null;
			}

		}

		switch (m) {

		case NEW_FRACTAL:
			return new ActionListener() {

				@Override
				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {

					while (FlameMakerGUI.this.builder.transformationCount() > 1) {
						FlameMakerGUI.this.buttons[1].getActionListeners()[0]
								.actionPerformed(null);
					}

					for (int k = 0; k < Preferences.defaults.builder.transformationCount(); k++) {

						final double[] array = new double[6];
						for (int l = 0; l < 6; l++) {
							array[l] = Preferences.defaults.builder.variationWeight(k,
									Variation.ALL_VARIATIONS.get(l));
						}
						final AffineTransformation affine = Preferences.defaults.builder
								.affineTransformation(k);

						final FlameTransformation trans = new FlameTransformation(affine, array);

						FlameMakerGUI.this.builder.addTransformation(trans);
					}

					FlameMakerGUI.this.buttons[1].getActionListeners()[0].actionPerformed(null);
					FlameMakerGUI.this.setSelectedTransformationIndex(0);

					if (Preferences.defaults.builder.transformationCount() > 1) {
						for (final JButton button : FlameMakerGUI.this.buttons) {
							button.setEnabled(true);
						}
					}
				}
			};

		case SAVE_CONF:
			return new ActionListener() {

				@Override
				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
					final Preferences pref = new Preferences(FlameMakerGUI.this.background,
							FlameMakerGUI.this.builder, FlameMakerGUI.this.density,
							FlameMakerGUI.this.frame, FlameMakerGUI.this.palette,
							Preferences.defaults.threads, Preferences.defaults.refresh,
							Preferences.defaults.step, Preferences.defaults.threshold,
							Preferences.defaults.path);

					try {
						final PrintStream file = new PrintStream(pref.path);
						pref.writeConfiguration(file);
						file.close();
					} catch (final Exception e1) {
						e1.printStackTrace();
					}

				}
			};

		case SAVE_IMAGE:
			return new ActionListener() {

				@Override
				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
					final JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setMultiSelectionEnabled(false);
					chooser.setName("FlameFract");
					chooser.setFileFilter(new FileFilter() {

						@Override
						public boolean accept(final File f) {
							if (f.isDirectory()) {
								return true;
							}

							final String ext = f.getName().replaceAll(".*\\.", "");
							switch (ext) {
							case "ppm":
								return true;

							default:
								return false;
							}

						}

						@Override
						public String getDescription() {
							return "Only images";
						}
					});

					if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						final File file = chooser.getSelectedFile();

						final JFrame frame = new JFrame();
						final Compute comp = new Compute(file, frame);
						comp.execute();
						comp.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(final PropertyChangeEvent evt) {
								if (evt.getPropertyName() == "state") {
									if ("DONE".compareTo(evt.getNewValue()
											.toString()) == 0) {
										frame.setVisible(false);
									}
								}
							}
						});
					}

				}
			};

		case EXIT:
			return new ActionListener() {

				@Override
				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
					System.exit(0);
				}
			};

		case FULLSCREEN:
			return new ActionListener() {

				@Override
				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
					final JFrame window = new JFrame();
					window.add(new FlameBuilderPreviewComponent(FlameMakerGUI.this.builder,
							FlameMakerGUI.this.background, FlameMakerGUI.this.palette,
							FlameMakerGUI.this.frame, FlameMakerGUI.this.density));
					window.pack();
					window.setVisible(true);

					window.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(@SuppressWarnings("unused") KeyEvent e) {
							window.setVisible(false);
						}

						@Override
						public void keyReleased(@SuppressWarnings("unused") KeyEvent e) {
						}

						@Override
						public void keyPressed(@SuppressWarnings("unused") KeyEvent e) {
						}
					});

					final GraphicsEnvironment ge = GraphicsEnvironment
							.getLocalGraphicsEnvironment();
					final GraphicsDevice gd = ge.getDefaultScreenDevice();
					gd.setFullScreenWindow(window);
				}
			};

		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Return a {@link JPanel} with all the {@link JButton} to rotate, move,
	 * ...
	 * 
	 * @return A {@link JPanel} with all the {@link JButton} to rotate,
	 *         move, ...
	 */
	private JPanel getAffineButtons() {

		final JPanel panel = new JPanel();
		final GroupLayout layout = new GroupLayout(panel);

		final JComponent[][] components = new JComponent[4][6];
		final String[][] labels = { { "Translation", "←", "→", "↑", "↓" }, { "Rotation", "⟲", "⟳" },
				{ "Dilatation", "+ ↔", "- ↔", "+ ↕", "- ↕" }, { "Transvection", "←", "→", "↑", "↓" } };
		final double[] values = { 0.1, 15, 1.05, 0.1 };

		// Groups
		final GroupLayout.SequentialGroup H = layout.createSequentialGroup();
		final GroupLayout.SequentialGroup V = layout.createSequentialGroup();

		final GroupLayout.ParallelGroup[] Hs = new GroupLayout.ParallelGroup[6];
		final GroupLayout.ParallelGroup[] Vs = new GroupLayout.ParallelGroup[4];

		for (int i = 0; i < Hs.length; i++) {
			Hs[i] = layout.createParallelGroup();
			H.addGroup(Hs[i]);
		}

		for (int i = 0; i < Vs.length; i++) {
			Vs[i] = layout.createParallelGroup();
			V.addGroup(Vs[i]);
		}

		// Add every components to the array
		for (int i = 0; i < components.length; i++) {

			final JFormattedTextField field = new JFormattedTextField(new DecimalFormat("#0.##"));
			field.setHorizontalAlignment(SwingConstants.RIGHT);
			field.setValue(values[i]);
			field.setInputVerifier(new InputVerifier() {

				@SuppressWarnings("unused")
				@Override
				public boolean verify(final JComponent input) {

					final AbstractFormatter formatter = field.getFormatter();

					try {
						// get the current value
						final Number value = (Number) formatter.stringToValue(field.getText());

						if (value.doubleValue() == 0) {
							throw new ParseException("Haha, bubble the exception!", 0);
						}

						field.setValue(value);

					} catch (final ParseException e) {
						// restore old value
						field.setValue(field.getValue());
					}

					return true;
				}
			});

			final JLabel label = new JLabel(labels[i][0]);

			components[i][0] = label;
			components[i][1] = field;

			for (int j = 1; j < labels[i].length; j++) {

				final int a = i, b = j;

				final JButton button = new JButton();
				button.setText(labels[i][j]);
				button.addActionListener(new ActionListener() {

					@SuppressWarnings("unused")
					@Override
					public void actionPerformed(final ActionEvent e) {
						final double value = ((Number) field.getValue()).doubleValue();
						final int index = FlameMakerGUI.this.getSelectedTransformationIndex();

						AffineTransformation trans = FlameMakerGUI.this.builder
								.affineTransformation(index);
						trans = FlameMakerGUI.getAffineTransformation(a, b - 1, value)
								.composeWith(trans);

						FlameMakerGUI.this.builder.setAffineTransformation(index, trans);
					}
				});

				components[i][j + 1] = button;
			}
		}

		for (int i = 0; i < components.length; i++) {

			for (int j = 0; j < labels[i].length + 1; j++) {
				Vs[i].addComponent(components[i][j], Alignment.CENTER);
				Hs[j].addComponent(components[i][j], Alignment.TRAILING);
			}
		}

		layout.setVerticalGroup(V);
		layout.setHorizontalGroup(H);

		panel.setLayout(layout);

		return panel;
	}

	/**
	 * Return a {@link JPanel} with the part to modify a
	 * {@link Transformation}
	 * 
	 * @return A {@link JPanel} with the part to modify a
	 *         {@link Transformation}
	 */
	private JPanel getAffineEdition() {
		final JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Transformation courante"));

		panel.add(this.getAffineButtons());
		panel.add(new JSeparator());
		panel.add(this.getAffineWeights());
		return panel;
	}

	/**
	 * Return the line with the weight of the selected
	 * {@link Transformation}
	 * 
	 * @return The line with the weight of the selected
	 *         {@link Transformation}
	 */
	private JPanel getAffineWeights() {

		final JPanel panel = new JPanel();
		final GroupLayout layout = new GroupLayout(panel);

		final JComponent[][] components = new JComponent[6][2];

		// Groups
		final GroupLayout.SequentialGroup H = layout.createSequentialGroup();
		final GroupLayout.SequentialGroup V = layout.createSequentialGroup();

		final GroupLayout.ParallelGroup[] Hs = new GroupLayout.ParallelGroup[6];
		final GroupLayout.ParallelGroup[] Vs = new GroupLayout.ParallelGroup[2];

		for (int i = 0; i < Hs.length; i++) {
			Hs[i] = layout.createParallelGroup();

			if (i % 2 == 0) {
				H.addPreferredGap(ComponentPlacement.UNRELATED);
			}

			H.addGroup(Hs[i]);
		}

		for (int i = 0; i < Vs.length; i++) {
			Vs[i] = layout.createParallelGroup();
			V.addGroup(Vs[i]);
		}

		// Add every components to the array
		for (int i = 0; i < components.length; i++) {

			final Variation variation = Variation.ALL_VARIATIONS.get(i);

			final JFormattedTextField field = new JFormattedTextField(new DecimalFormat("#0.##"));
			field.setHorizontalAlignment(SwingConstants.RIGHT);
			field.setValue(this.builder.variationWeight(this.getSelectedTransformationIndex(), variation));

			final JLabel label = new JLabel(variation.name());

			this.addObserver(new SelectionObserver() {

				@Override
				public void valueChanged() {

					try {
						final double value = FlameMakerGUI.this.builder.variationWeight(
								FlameMakerGUI.this.getSelectedTransformationIndex(),
								variation);
						field.setValue(value);
					} catch (final IndexOutOfBoundsException e) {
						// Swing..
					}
				}
			});

			field.addPropertyChangeListener("value", new PropertyChangeListener() {

				@SuppressWarnings("unused")
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					final double value = ((Number) field.getValue()).doubleValue();
					FlameMakerGUI.this.builder.setVariationWeight(
							FlameMakerGUI.this.getSelectedTransformationIndex(), variation,
							value);
				}
			});

			components[(i * 2) % 6][i / 3] = label;
			components[(i * 2 + 1) % 6][i / 3] = field;
		}

		for (int i = 0; i < components.length; i++) {

			for (int j = 0; j < components[0].length; j++) {
				Vs[j].addComponent(components[i][j], Alignment.CENTER);
				Hs[i].addComponent(components[i][j], Alignment.TRAILING);

			}
		}

		layout.setVerticalGroup(V);
		layout.setHorizontalGroup(H);
		panel.setLayout(layout);

		return panel;
	}

	/**
	 * Return a {@link JPanel} with all the down part of the GUI
	 * 
	 * @return A {@link JPanel} with all the down part of the GUI
	 */
	private JPanel getDownPanel() {
		final JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(this.getTransformationEdition());
		panel.add(this.getAffineEdition());

		return panel;
	}

	/**
	 * Return a {@link JPanel} containing the representation of the fractal
	 * 
	 * @return A {@link JPanel} containing the representation of the fractal
	 */
	private JPanel getFractal() {
		final JPanel panel = new JPanel();

		final Border border = BorderFactory.createTitledBorder("Fractale");
		panel.setBorder(border);

		panel.setLayout(new BorderLayout());
		panel.add(new FlameBuilderPreviewComponent(this.builder, this.background, this.palette, this.frame,
				this.density));

		return panel;
	}

	/**
	 * Return the {@link JMenuBar} with the menus
	 * 
	 * @return The {@link JMenuBar} with the menus
	 */
	private JMenuBar getMenuBar() {
		final JMenuBar bar = new JMenuBar();

		int h = Integer.MIN_VALUE;
		final int w = Menus.values().length;
		final int array[] = new int[w];
		for (final Menus.Items item : Menus.Items.values()) {
			final int pos = item.getMenu().ordinal();
			array[pos]++;
			if (array[pos] > h) {
				h = array[pos];
			}
		}

		final JMenu[] menus = new JMenu[w];
		for (final Menus menu : Menus.values()) {
			menus[menu.ordinal()] = new JMenu(menu.getName());
		}

		Menus.Items old = null;
		for (final Menus.Items item : Menus.Items.values()) {
			final int pos = item.getMenu().ordinal();

			if (item.getPos() > 0 && item.getPos() - old.getPos() > 1) {
				menus[pos].addSeparator();
			}

			final JMenuItem place = new JMenuItem(item.getName(), item.getKeyEvent());
			place.setMnemonic(item.getKeyEvent());
			place.setAccelerator(KeyStroke.getKeyStroke(item.getKeyEvent(), InputEvent.CTRL_DOWN_MASK
					| item.getModifier()));
			place.addActionListener(this.getActionListener(item));

			menus[pos].add(place);
			old = item;
		}

		for (final JMenu menu : menus) {
			bar.add(menu);
		}

		return bar;
	}

	/**
	 * Return a {@link JPanel} with the buttons to remove or add a
	 * {@link Transformation}
	 * 
	 * @param list
	 *                The {@link JList} to select a new
	 *                {@link Transformation}
	 * @param model
	 *                The {@link TransformationsListModel} to act upon
	 * 
	 * @return A {@link JPanel} with the buttons to remove or add a
	 *         {@link Transformation}
	 */
	private JPanel getTransformationButtons(final JList<String> list, final TransformationsListModel model) {
		final JPanel panel = new JPanel();
		this.buttons = new JButton[2];

		this.buttons[0] = new JButton("Ajouter");
		this.buttons[1] = new JButton("Supprimer");

		this.buttons[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				model.addTransformation();
				list.setSelectedIndex(FlameMakerGUI.this.builder.transformationCount() - 1);

				if (!FlameMakerGUI.this.buttons[1].isEnabled()) {
					FlameMakerGUI.this.buttons[1].setEnabled(true);
				}
			}
		});

		this.buttons[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				final int old = FlameMakerGUI.this.getSelectedTransformationIndex();
				model.removeTransformation(FlameMakerGUI.this.getSelectedTransformationIndex());
				list.setSelectedIndex(old == FlameMakerGUI.this.builder.transformationCount() ? old - 1
						: old);

				if (FlameMakerGUI.this.builder.transformationCount() == 1) {
					FlameMakerGUI.this.buttons[1].setEnabled(false);
				}
			}
		});

		panel.setLayout(new GridLayout(1, 2));
		panel.add(this.buttons[0]);
		panel.add(this.buttons[1]);

		return panel;
	}

	/**
	 * Return a {@link JPanel} with the part to selected and add/remove a
	 * {@link Transformation}
	 * 
	 * @return A {@link JPanel} with the part to selected and add/remove a
	 *         {@link Transformation}
	 */
	private JPanel getTransformationEdition() {
		final JPanel panel = new JPanel();
		final TransformationsListModel model = new TransformationsListModel("Transformation n°");
		final JList<String> list = this.getTransformationList(model);

		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(this.getTransformationButtons(list, model), BorderLayout.PAGE_END);

		return panel;
	}

	/**
	 * Return the {@link JList} build with the given
	 * {@link TransformationsListModel}
	 * 
	 * @param model
	 *                The {@link TransformationsListModel} to build the list
	 *                with
	 * @return The {@link JList} build with the given
	 *         {@link TransformationsListModel}
	 */
	private JList<String> getTransformationList(final TransformationsListModel model) {
		final JList<String> list = new JList<String>(model);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(3);
		list.setSelectedIndex(this.selectedTransformationIndex);

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(@SuppressWarnings("unused") final ListSelectionEvent e) {
				FlameMakerGUI.this.setSelectedTransformationIndex(list.getSelectedIndex());
			}
		});

		return list;
	}

	/**
	 * Return a {@link JPanel} with the part to selected and add/remove a
	 * {@link Transformation} with a border
	 * 
	 * @return A {@link JPanel} with the part to selected and add/remove a
	 *         {@link Transformation} with a border
	 */
	private JPanel getTransformations() {
		final JPanel panel = new JPanel();

		final Border border = BorderFactory.createTitledBorder("Transformations affines");
		panel.setBorder(border);

		final AffineTransformationsComponent affine = new AffineTransformationsComponent(this.builder,
				this.frame);
		this.addObserver(affine);

		panel.setLayout(new BorderLayout());
		panel.add(affine);

		return panel;
	}

	/**
	 * Return a {@link JPanel} with the up part of the GUI
	 * 
	 * @return A {@link JPanel} with the up part of the GUI
	 */
	private JPanel getUpPanel() {
		final JPanel panel = new JPanel();

		panel.setLayout(new GridLayout());
		panel.add(this.getTransformations());
		panel.add(this.getFractal());

		return panel;
	}
}