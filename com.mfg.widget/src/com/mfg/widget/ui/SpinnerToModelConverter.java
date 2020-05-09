package com.mfg.widget.ui;

import org.eclipse.core.databinding.conversion.Converter;

public class SpinnerToModelConverter extends Converter {

	public SpinnerToModelConverter() {
		super(Integer.class, Double.class);
	}

	@Override
	public Object convert(Object fromObject) {
		// assumes the spinner digits is 1
		Double result = fromObject == null ? null : new Double(
				(((Integer) fromObject).doubleValue()) / 10);
		return result;
	}

}