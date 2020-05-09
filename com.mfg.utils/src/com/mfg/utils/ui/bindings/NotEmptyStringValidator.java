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
public class NotEmptyStringValidator implements IValidator {
	@Override
	public IStatus validate(Object value) {
		return value == null || value.toString().trim().length() == 0 ? ValidationStatus.error("Invalid input") : ValidationStatus.ok();
	}

}
