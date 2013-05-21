package ch.epfl.flamemaker.gui;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.Flame.Builder;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;

/**
 * An observable version of {@link Builder}
 */
public class ObservableFlameBuilder {

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
		void changedBuilder();
	}

	/**
	 * The {@link Builder} used in internal to behave like the
	 * {@link Builder}
	 */
	private final Flame.Builder	builder;

	/**
	 * {@link Set} of the {@link Observer} of the {@link Builder}
	 */
	private final Set<Observer>	observers;

	/**
	 * Construct a {@link ObservableFlameBuilder} with the given
	 * {@link Flame}
	 * 
	 * @param flame
	 *                The {@link Flame} to take as base
	 */
	public ObservableFlameBuilder(Flame flame) {
		this.builder = new Flame.Builder(flame);
		this.observers = new HashSet<ObservableFlameBuilder.Observer>();
	}

	/**
	 * Copy-construct a new {@link ObservableFlameBuilder} based on the
	 * given {@link ObservableFlameBuilder}
	 * 
	 * @param builder
	 *                The {@link ObservableFlameBuilder} to copy
	 */
	public ObservableFlameBuilder(ObservableFlameBuilder builder) {
		this.builder = new Builder(builder.builder);
		this.observers = new HashSet<ObservableFlameBuilder.Observer>();
		for (Observer observer : builder.observers) {
			this.observers.add(observer);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ObservableFlameBuilder other = (ObservableFlameBuilder) obj;
		if (builder == null) {
			if (other.builder != null) {
				return false;
			}
		} else if (!builder.equals(other.builder)) {
			return false;
		}
		if (observers == null) {
			if (other.observers != null) {
				return false;
			}
		} else if (!observers.equals(other.observers)) {
			return false;
		}
		return true;
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
	 * Add a new {@link FlameTransformation} to the end of the list
	 * 
	 * @param transformation
	 *                The {@link FlameTransformation} to add to the end of
	 *                the list
	 */
	public void addTransformation(FlameTransformation transformation) {
		this.builder.addTransformation(transformation);
		this.warnObservers();
	}

	/**
	 * Return the {@link AffineTransformation} of the
	 * {@link FlameTransformation} at the given index in the list
	 * 
	 * @param index
	 *                The index for the {@link FlameTransformation}
	 * 
	 * @return The {@link AffineTransformation} of the
	 *         {@link FlameTransformation} at the given index in the list
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 If the index is less than zero of greater than the
	 *                 max index of the list
	 */
	public AffineTransformation affineTransformation(int index) {
		return this.builder.affineTransformation(index);
	}

	/**
	 * Return a {@link Flame} with the actual state of the Builder
	 * 
	 * @return A {@link Flame} with the actual state of the Builder
	 */
	public Flame build() {
		return this.builder.build();
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
	 * Remove the {@link FlameTransformation} at the given index
	 * 
	 * @param index
	 *                The index in the list to remove
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 If the index is less than zero of greater than the
	 *                 max index of the list
	 */
	public void removeTransformation(int index) {
		this.builder.removeTransformation(index);
		this.warnObservers();
	}

	/**
	 * Set the {@link AffineTransformation} of the
	 * {@link FlameTransformation} at the given index in the list
	 * 
	 * @param index
	 *                The index for the {@link FlameTransformation}
	 * 
	 * @param newTransformation
	 *                The new {@link AffineTransformation}
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 If the index is less than zero of greater than the
	 *                 max index of the list
	 */
	public void setAffineTransformation(int index, AffineTransformation newTransformation) {
		this.builder.setAffineTransformation(index, newTransformation);
		this.warnObservers();
	}

	/**
	 * Set the weight of given {@link Variation} of the
	 * {@link FlameTransformation} at the given index in the list
	 * 
	 * @param index
	 *                The index for the {@link FlameTransformation}
	 * @param variation
	 *                The {@link Variation} which we want to change the
	 *                weight
	 * @param newWeight
	 *                The new weight
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 If the index is less than zero of greater than the
	 *                 max index of the list
	 */
	public void setVariationWeight(int index, Variation variation, double newWeight) {
		this.builder.setVariationWeight(index, variation, newWeight);
		this.warnObservers();
	}

	/**
	 * Return the size of the list
	 * 
	 * @return The size of the list
	 */
	public int transformationCount() {
		return this.builder.transformationCount();
	}

	/**
	 * Get the weight of given {@link Variation} of the
	 * {@link FlameTransformation} at the given index in the list
	 * 
	 * @param index
	 *                The index for the {@link FlameTransformation}
	 * @param variation
	 *                The {@link Variation} of which we want to get the
	 *                weight
	 * @return The weight of the {@link Variation} in the
	 *         {@link FlameTransformation} at the given index in the list
	 * 
	 * @throws IndexOutOfBoundsException
	 *                 If the index is less than zero of greater than the
	 *                 max index of the list
	 */
	public double variationWeight(int index, Variation variation) {
		return this.builder.variationWeight(index, variation);
	}

	/**
	 * Execute changedObservedValue() for every {@link Observer} we have
	 */
	private void warnObservers() {
		for (final Observer observer : this.observers) {
			observer.changedBuilder();
		}
	}
}
