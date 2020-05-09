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
package com.mfg.strategy.builder.part;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.ScalableRootEditPart;

import com.mfg.strategy.builder.figure.AppEditLayoutPolicy;
import com.mfg.strategy.builder.figure.EventCanvasFigure;

public class EventsCanvasPart extends StrategyAppPart {
	
	public static String ID = "com.mfg.strategy.builder.part.EventsCanvasPart";

	@Override
	protected IFigure createFigure() {
		IFigure aFigure = new EventCanvasFigure(
				((ScalableRootEditPart) this.getRoot()).getZoomManager());
		return aFigure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
	}
	
	@Override
	protected void myRefreshChildren() {
		super.refreshChildren();
	}


	@Override
	protected void refreshVisuals() {
		//DO NOTHING
	}

	
	
}
