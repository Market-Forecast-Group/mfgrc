package com.mfg.widget.ui;

import org.eclipse.core.databinding.conversion.Converter;

public class Model100ToSpinnerConverter extends Converter {

	public Model100ToSpinnerConverter() {
		super(int.class, int.class);
	}

	@Override
	public Object convert(Object fromObject) {
		int from = ((Integer) fromObject).intValue();
		Integer result = Integer.valueOf(from / 10);
		return result;
	}

}