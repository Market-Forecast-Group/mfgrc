package com.mfg.utils.ui.bindings;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.Converter;

public class LongToDateStringConverter extends Converter {

	private DateFormat format;

	public LongToDateStringConverter(DateFormat aFormat) {
		super(long.class, String.class);
		this.format = aFormat;
	}

	@SuppressWarnings("boxing")
	@Override
	public Object convert(Object fromObject) {
		return format.format(new Date((long) fromObject));
	}

}