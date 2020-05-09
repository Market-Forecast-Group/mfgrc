package com.mfg.widget.priv;

import java.io.Serializable;

/**
 * base class to represent a set of values that a parameter can have. 
 * @author gardero
 *
 * @param <T> the type of the values in the domain.
 */
public abstract class Domain<T> implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IRandomizer r;
	/**
	 * asks if this domain contains an specific value.
	 * @param value the value we are asking if it is in this domain.
	 * @return true if this domain contains the value.
	 */
	public abstract boolean contains(T value);
	
	/**
	 * randomly gets a value contained in the domain.
	 * @return the randomly generated value.
	 */
	public T getRandomValue(){
		if (isEmpty())
			throw new RuntimeException("Inconsitent Ranges for the parameters");
		return getRandomValue(r);
	}
	
	/**
	 * randomly gets a value contained in the domain using a specific randomizer.
	 * @param aR the randomizer to generate the random value.
	 * @return the randomly generated value.
	 */
	public abstract T getRandomValue(IRandomizer aR);
	/**
	 * changes the value of the randomizer used in this domain to generate random 
	 * values.
	 * @param aR the new randomizer.
	 */
	public void resetRandomizer(IRandomizer aR){
		this.r = aR;
//		if (r!=null)
//		System.out.println("setting randomizer of "+r);
//		else
//			System.out.println(r);
	}
	
	@Override
	public abstract Domain<T> clone();
	
	/**
	 * asks if the domain is empty or not.
	 * @return true if the domain is empty.
	 */
	public abstract boolean isEmpty();
	
	/**
	 * joins two domains.
	 * @param other another domain.
	 * @return the result of joining this domain with the other one.
	 */
	public abstract Domain<T> join(Domain<T> other);
	
	/**
	 * intersects two domains.
	 * @param other another domain.
	 * @return the result of intersect this domain with the other one.
	 */
	public abstract Domain<T> intersect(Domain<T> other);
	
	/**
	 * removes elements from this domain.
	 * @param other domain containing the elements to remove.
	 * @return the result of removing the elements from this domain.
	 */
	public abstract Domain<T> subtract(Domain<T> other);

	public IRandomizer getR() {
		return r;
	}
	
	
}
