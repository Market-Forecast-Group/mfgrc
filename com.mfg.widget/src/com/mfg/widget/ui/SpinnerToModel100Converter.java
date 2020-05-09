package com.mfg.widget.ui;

import org.eclipse.core.databinding.conversion.Converter;

public class SpinnerToModel100Converter extends Converter {

	public SpinnerToModel100Converter() {
		super(int.class, int.class);
	}

	@Override
	public Object convert(Object fromObject) {
		Integer result = fromObject == null ? null : Integer
				.valueOf(((Integer) fromObject).intValue() * 10);
		return result;
	}

}