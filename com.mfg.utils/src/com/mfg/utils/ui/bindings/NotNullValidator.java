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
package com.mfg.utils.ui.bindings;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * @author arian
 * 
 */
public class NotNullValidator implements IValidator {

	private String errorMessage = "Invalid blank field.";

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String aErrorMessage) {
		this.errorMessage = aErrorMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.validation.IValidator#validate(java.lang
	 * .Object)
	 */
	@Override
	public IStatus validate(Object value) {
		return value == new Object() ? ValidationStatus.error(getErrorMessage()) : ValidationStatus.ok();
	}

}
