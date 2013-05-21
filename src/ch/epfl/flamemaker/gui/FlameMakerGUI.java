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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.extra.Preferences;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.Flame.Builder;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.gui.FlameMakerGUI.TransformationsListModel;
import ch.epfl.flamemaker.gui.ObservableFlameBuilder.Observer;

public class FlameMakerGUI {

	private static class FlameBuilderPreviewComponent extends JComponent implements Observer {

		private class Clock {
			private long	begin;
			private long	end;

			public long time() {
				return end - begin;
			}

			public void start() {
				begin = System.currentTimeMillis();
			}

			public void stop() {
				end = System.currentTimeMillis();
			}
		}

		private ObservableFlameBuilder		builder;
		private Color				background;
		private Palette				palette;
		private Rectangle			frame;
		private int				density;

		private Timer				timer;
		private FlameAccumulator.Builder	accuBuilder;
		private int				totalDensity;

		int					step;
		int					time;

		private ObservableFlameBuilder		computedBuilder;

		private FlameBuilderPreviewComponent(ObservableFlameBuilder builder, Color background, Palette palette,
				Rectangle frame, int density) {
			this.builder = builder;
			this.background = background;
			this.palette = palette;
			this.frame = frame;
			this.density = density;
			this.time = Preferences.values.refresh;
			// Use a low value just to have something to test
			this.step = 1000;

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

		private void paintAccuBuilder(Graphics g) {

			if (accuBuilder == null) {
				return;
			}

			final FlameAccumulator accu = accuBuilder.build();
			final BufferedImage image = new BufferedImage(accu.width(), accu.height(),
					BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < accu.width(); x++) {
				for (int y = 0; y < accu.height(); y++) {
					final Color c = accu.color(palette, background, x, y);
					final int RGB = c.asPackedRGB();
					image.setRGB(x, accu.height() - 1 - y, RGB);
				}
			}
			g.drawImage(image, 0, 0, null);
		}

		@Override
		protected void paintComponent(final Graphics g) {

			final int width = getWidth(), height = getHeight();

			if (this.timer != null && accuBuilder != null) {
				final FlameAccumulator build = accuBuilder.build();
				if ((build.width() != getWidth() || build.height() != getHeight())) {
					this.timer.stop();
				}
			}

			if (this.timer == null || !this.timer.isRunning()) {
				final Rectangle actualFrame = frame.expandToAspectRatio(width / (double) height);
				accuBuilder = new FlameAccumulator.Builder(actualFrame, width, height);

				paintAccuBuilder(g);

				this.totalDensity = 0;
				this.step = 1000;
				this.timer = new Timer(Preferences.values.refresh, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						repaint();
					}
				});
				this.timer.start();

			} else if (totalDensity < this.density * width * height) {
				totalDensity += step;
				final Clock clock = new Clock();
				clock.start();
				builder.build().compute(step, accuBuilder);
				paintAccuBuilder(g);
				clock.stop();

				// Accurate the step
				this.step *= (this.time / (double) clock.time());
				if ((this.time / (double) clock.time()) < 1 && this.step < Preferences.values.threshold) {
					System.out.println("Your time setting (the refresh rate) is too low, and thus,");
					System.out.println("we can't keep up. We're adjusting it for now, but");
					System.out.println("consider adding an higher value to the preferences.");
					this.time += 100;
					this.step = 1000;
				}

			} else {
				this.timer.stop();
				paintAccuBuilder(g);
			}
		}
	}

	private class AffineTransformationsComponent extends JComponent implements SelectionObserver, Observer {

		private ObservableFlameBuilder	builder;
		private Rectangle		frame;

		private int			highlightedTransformationIndex;
		private AffineTransformation	transformation;

		@Override
		public void valueChanged() {
			this.highlightedTransformationIndex = getSelectedTransformationIndex();
			this.repaint();
		}

		@Override
		public void changedBuilder() {
			this.repaint();
		}

		public AffineTransformationsComponent(ObservableFlameBuilder builder, Rectangle frame) {
			this.builder = builder;
			this.frame = frame;
			this.highlightedTransformationIndex = 0;

			this.builder.addObserver(this);
		}

		public void setHighlightedTransformationIndex(int highlightedTransformationIndex) {
			this.highlightedTransformationIndex = highlightedTransformationIndex;
		}

		public int getHighlightedTransformationIndex() {
			return highlightedTransformationIndex;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 100);
		}

		@Override
		protected void paintComponent(Graphics g0) {
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

		private void paintGrid(Graphics2D g) {

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

	public class TransformationsListModel extends AbstractListModel<String> {

		private final String	text;

		public TransformationsListModel(String text) {
			this.text = text;
		}

		@Override
		public int getSize() {
			return builder.transformationCount();
		}

		@Override
		public String getElementAt(int index) {
			return this.text + (index + 1);
		}

		public void addTransformation() {
			final double[] array = { 1, 0, 0, 0, 0, 0 };
			final FlameTransformation trans = new FlameTransformation(AffineTransformation.IDENTITY, array);
			builder.addTransformation(trans);

			this.fireIntervalAdded(this, getSize() - 1, getSize());
		}

		public void removeTransformation(int index) {
			builder.removeTransformation(index);
			setSelectedTransformationIndex(index == builder.transformationCount() ? index - 1 : index);

			this.fireIntervalRemoved(this, index, index);
		}
	}

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

	private ObservableFlameBuilder	builder;
	private Color			background;
	private Palette			palette;
	private Rectangle		frame;
	private int			density;

	private int			selectedTransformationIndex;
	private Set<SelectionObserver>	observers;

	public FlameMakerGUI() {
		this.builder = Preferences.values.builder;
		this.background = Preferences.values.background;
		this.palette = Preferences.values.palette;
		this.frame = Preferences.values.frame;
		this.density = Preferences.values.density;

		this.selectedTransformationIndex = 0;
		this.observers = new HashSet<SelectionObserver>();
	}

	public void setSelectedTransformationIndex(int selectedTransformationIndex) {
		this.selectedTransformationIndex = selectedTransformationIndex;

		for (SelectionObserver observer : this.observers) {
			observer.valueChanged();
		}
	}

	public int getSelectedTransformationIndex() {
		return selectedTransformationIndex;
	}

	private static interface SelectionObserver {
		void valueChanged();
	}

	public void addObserver(SelectionObserver observer) {
		this.observers.add(observer);
	}

	public void removeObserver(SelectionObserver observer) {
		this.observers.remove(observer);
	}

	private JPanel getTransformations() {
		final JPanel panel = new JPanel();

		final Border border = BorderFactory.createTitledBorder("Transformations affines");
		panel.setBorder(border);

		final AffineTransformationsComponent affine = new AffineTransformationsComponent(builder, frame);
		addObserver(affine);

		panel.setLayout(new BorderLayout());
		panel.add(affine);

		return panel;
	}

	private JPanel getFractal() {
		final JPanel panel = new JPanel();

		final Border border = BorderFactory.createTitledBorder("Fractale");
		panel.setBorder(border);

		panel.setLayout(new BorderLayout());
		panel.add(new FlameBuilderPreviewComponent(builder, background, palette, frame, density));

		return panel;
	}

	private JPanel getUpPanel() {
		final JPanel panel = new JPanel();

		panel.setLayout(new GridLayout());
		panel.add(getTransformations());
		panel.add(getFractal());

		return panel;
	}

	private JPanel getDownPanel() {
		final JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(getTransformationEdition());
		panel.add(getAffineEdition());

		return panel;
	}

	private JPanel getAffineEdition() {
		final JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Transformation courante"));

		panel.add(getAffineButtons());
		panel.add(new JSeparator());
		panel.add(getAffineWeights());
		return panel;
	}

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
				public boolean verify(JComponent input) {

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
					public void actionPerformed(ActionEvent e) {
						final double value = ((Number) field.getValue()).doubleValue();
						final int index = getSelectedTransformationIndex();

						AffineTransformation trans = builder.affineTransformation(index);
						trans = getAffineTransformation(a, b - 1, value).composeWith(trans);

						builder.setAffineTransformation(index, trans);
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

	static private AffineTransformation getAffineTransformation(int i, int j, double value) {
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

	private JPanel getTransformationEdition() {
		final JPanel panel = new JPanel();
		final TransformationsListModel model = new TransformationsListModel("Transformation n°");
		final JList<String> list = getTransformationList(model);

		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(getTransformationButtons(model, list), BorderLayout.PAGE_END);

		return panel;
	}

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

			addObserver(new SelectionObserver() {

				@Override
				public void valueChanged() {

					try {
						final double value = builder.variationWeight(
								getSelectedTransformationIndex(), variation);
						field.setValue(value);
					} catch (IndexOutOfBoundsException e) {
						// Swing..
					}
				}
			});

			field.addPropertyChangeListener("value", new PropertyChangeListener() {

				@SuppressWarnings("unused")
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					final double value = ((Number) field.getValue()).doubleValue();
					builder.setVariationWeight(getSelectedTransformationIndex(), variation, value);
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

	private JPanel getTransformationButtons(final TransformationsListModel model, final JList<String> list) {
		final JPanel panel = new JPanel();
		final JButton buttons[] = new JButton[2];

		buttons[0] = new JButton("Ajouter");
		buttons[1] = new JButton("Supprimer");

		buttons[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.addTransformation();
				list.setSelectedIndex(builder.transformationCount() - 1);

				if (!buttons[1].isEnabled()) {
					buttons[1].setEnabled(true);
				}
			}
		});

		buttons[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final int old = getSelectedTransformationIndex();
				model.removeTransformation(getSelectedTransformationIndex());
				list.setSelectedIndex(old == builder.transformationCount() ? old - 1 : old);

				if (builder.transformationCount() == 1) {
					buttons[1].setEnabled(false);
				}
			}
		});

		panel.setLayout(new GridLayout(1, 2));
		panel.add(buttons[0]);
		panel.add(buttons[1]);

		return panel;
	}

	private JList<String> getTransformationList(TransformationsListModel model) {
		final JList<String> list = new JList<String>(model);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(3);
		list.setSelectedIndex(this.selectedTransformationIndex);

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				setSelectedTransformationIndex(list.getSelectedIndex());
			}
		});

		return list;
	}

	public void start() {
		final JFrame frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// TODO needed to a layout?
		frame.getContentPane().add(getUpPanel(), BorderLayout.CENTER);
		frame.getContentPane().add(getDownPanel(), BorderLayout.PAGE_END);

		frame.pack();
		frame.setVisible(true);
	}
}