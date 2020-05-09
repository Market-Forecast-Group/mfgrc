/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.ui.databinding;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author arian
 * 
 */
public class RealTickSizeValidator implements IValidator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.validation.IValidator#validate(java.lang
	 * .Object)
	 */
	@Override
	public IStatus validate(Object value) {
		if (value != null && !"".equals(value)) {
			String str = (String) value;
			try {
				Double.parseDouble(str);
			} catch (NumberFormatException e) {
				return ValidationStatus.error("Invalid format.");
			}
		}
		return Status.OK_STATUS;
	}
}
