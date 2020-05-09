package com.mfg.widget.priv;

import java.io.Serializable;
import java.util.ArrayList;


public class ElementsMarker<T> implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<T> auxPossibleValues;
	protected int disabledCant;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ElementsMarker<T> clone(){
		try {
			ElementsMarker<T> res = (ElementsMarker<T>)super.clone();
			res.disabledCant = 0;
			res.auxPossibleValues = (ArrayList<T>)auxPossibleValues.clone();
			return res;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean allMarked(){
		return disabledCant == auxPossibleValues.size();
	}
	
	public ElementsMarker() {
		super();
		auxPossibleValues = new ArrayList<>();
		disabledCant = 0;
	}

	public int getDisabledCant() {
		return disabledCant;
	}
	
	public T getRandomValue(IRandomizer r) {
		int n = auxPossibleValues.size() - disabledCant;
		if (n==0){
			throw new IllegalArgumentException("disabled "+disabledCant+" from "+auxPossibleValues);
		}
		int p = r.nextInt(n);
		return auxPossibleValues.get(p);
	}

	@SuppressWarnings("unchecked")
	public void resetRandomizer(SetDomain<T> d){
		auxPossibleValues = (ArrayList<T>)d.possibleValues.clone();
		disabledCant = 0;
	}
	
	public void disableValue(T value) {
		int p = auxPossibleValues.indexOf(value);
		if (p==-1)
			return;
		int end = auxPossibleValues.size() - disabledCant -1;
		auxPossibleValues.set(p,auxPossibleValues.get(end));
		auxPossibleValues.set(end, value);
		disabledCant++;
	}
}