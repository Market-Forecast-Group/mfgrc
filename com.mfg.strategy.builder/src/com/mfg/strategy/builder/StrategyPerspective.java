/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class StrategyPerspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = "com.mfg.strategy.builder.app.perspective"; //$NON-NLS-1$


	@Override
	public void createInitialLayout(IPageLayout layout) {
		//
	}
}
