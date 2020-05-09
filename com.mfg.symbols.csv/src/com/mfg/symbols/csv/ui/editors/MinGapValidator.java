package com.mfg.symbols.csv.ui.editors;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class MinGapValidator implements IValidator {

	@Override
	public IStatus validate(Object value) {
		int n = ((Integer) value).intValue();
		if (n < 2) {
			return ValidationStatus.error("Min value is 2");
		}
		if (n > 100) {
			return ValidationStatus.error("Max value is 100");
		}
		return Status.OK_STATUS;
	}

}