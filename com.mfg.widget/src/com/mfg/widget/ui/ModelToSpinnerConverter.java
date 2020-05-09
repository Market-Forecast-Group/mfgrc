package com.mfg.widget.ui;

import org.eclipse.core.databinding.conversion.Converter;

public class ModelToSpinnerConverter extends Converter {

	public ModelToSpinnerConverter() {
		super(Double.class, Integer.class);
	}

	@Override
	public Object convert(Object fromObject) {
		// assumes the spinner digits is 2
		Integer result = fromObject == null ? null : new Integer(
				(int) (((Double) fromObject).doubleValue() * 10));
		return result;
	}

}