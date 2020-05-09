package com.mfg.widget.priv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * represents a set of values, usually stored in the internal representation of
 * the object.
 * 
 * @author gardero
 * 
 * @param <T>
 *            the type of the values in the domain.
 */
public class SetDomain<T> extends Domain<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the list of possible values
	 */
	protected ArrayList<T> possibleValues;

	protected ElementsMarker<T> elementsMarker;

	@Override
	public SetDomain<T> clone() {
		SetDomain<T> res = new SetDomain<>(possibleValues);
		res.r = r;
		return res;
	}

	public SetDomain() {
		super();
		possibleValues = new ArrayList<>();
		elementsMarker = new ElementsMarker<>();
		resetRandomizer(null);
	}

	/**
	 * constructs an a set with an initial collection of values.
	 * 
	 * @param c
	 *            the collection of initial values.
	 */
	public SetDomain(Collection<? extends T> c) {
		super();
		possibleValues = new ArrayList<>(c.size());
		elementsMarker = new ElementsMarker<>();
		setPossibleValues(c);
	}

	@Override
	public boolean contains(T value) {
		return possibleValues.contains(value);
	}

	/**
	 * gets the elements marker used to avoid generating twice the same value of
	 * this set.
	 * 
	 * @return the current elements marker.
	 */
	public ElementsMarker<T> getElementsMarker() {
		return elementsMarker;
	}

	/**
	 * sets the elements marker used to avoid generating twice the same value of
	 * this set.
	 * 
	 * @param aElementsMarker
	 *            the new elements marker.
	 */
	public void setElementsMarker(ElementsMarker<T> aElementsMarker) {
		this.elementsMarker = aElementsMarker;
	}

	@Override
	public T getRandomValue(IRandomizer aR) {
		return elementsMarker.getRandomValue(aR);
	}

	@Override
	public void resetRandomizer(IRandomizer aR) {
		super.resetRandomizer(aR);
		elementsMarker.resetRandomizer(this);
	}

	/**
	 * sets the set of possible values.
	 * 
	 * @param c
	 *            collection of possible values.
	 */
	public void setPossibleValues(Collection<? extends T> c) {
		possibleValues.clear();
		possibleValues.addAll(c);
		resetRandomizer(r);
	}

	/**
	 * adds a new value to the set of possible values.
	 * 
	 * @param c
	 *            the new value added to the domain.
	 */
	public void add(T c) {
		possibleValues.add(c);
		resetRandomizer(r);
	}

	/**
	 * removes a value from the set of possible values.
	 * 
	 * @param c
	 *            value to remove.
	 */
	public void remove(T c) {
		possibleValues.remove(c);
		resetRandomizer(r);
	}

	/**
	 * removes all values from the set.
	 */
	public void clear() {
		possibleValues.clear();
		resetRandomizer(r);
	}

	/**
	 * gets the list of possible values.
	 * 
	 * @return the list of possible values.
	 */
	// @JSON(include=true, index = 10)
	public ArrayList<T> getPossibleValues() {
		return possibleValues;
	}

	/**
	 * sets the list of possible values.
	 * 
	 * @param aPossibleValues
	 */
	public void setPossibleValues(ArrayList<T> aPossibleValues) {
		this.possibleValues = aPossibleValues;
	}

	@Override
	public SetDomain<T> intersect(Domain<T> other) {
		SetDomain<T> res = clone();
		for (Iterator<T> iterator = res.possibleValues.iterator(); iterator
				.hasNext();) {
			T value = iterator.next();
			if (!other.contains(value))
				iterator.remove();
		}
		res.resetRandomizer(r);
		return res;
	}

	@Override
	public SetDomain<T> join(Domain<T> other) {
		SetDomain<T> other1 = (SetDomain<T>) other;
		SetDomain<T> res = clone();
		for (Iterator<T> iterator = other1.possibleValues.iterator(); iterator
				.hasNext();) {
			T value = iterator.next();
			if (!res.contains(value))
				res.possibleValues.add(value);
		}
		res.resetRandomizer(r);
		return res;
	}

	@Override
	public Domain<T> subtract(Domain<T> other) {
		SetDomain<T> res = clone();
		for (Iterator<T> iterator = res.possibleValues.iterator(); iterator
				.hasNext();) {
			T value = iterator.next();
			if (other.contains(value))
				iterator.remove();
		}
		res.resetRandomizer(r);
		return res;
	}

	@Override
	public String toString() {
		return "{"
				+ (possibleValues != null ? "" + withcomas(possibleValues) : "")
				+ "}";
	}

	private String withcomas(ArrayList<T> possibleValues2) {
		String res = "" + possibleValues2.get(0);
		for (int i = 1; i < possibleValues2.size(); i++) {
			res += (", " + possibleValues2.get(i));
		}
		return res;
	}

	@Override
	public boolean isEmpty() {
		return possibleValues.isEmpty();
	}

}
