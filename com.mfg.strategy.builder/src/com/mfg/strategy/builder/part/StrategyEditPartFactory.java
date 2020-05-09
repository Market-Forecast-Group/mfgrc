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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.mfg.strategy.builder.model.BoundsModel;
import com.mfg.strategy.builder.model.CollectionEventModel;
import com.mfg.strategy.builder.model.CommandEventModel;
import com.mfg.strategy.builder.model.ConditionalCommandEventModel;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.NOTEventModel;
import com.mfg.strategy.builder.model.SimpleEventModel;

public class StrategyEditPartFactory implements EditPartFactory {

	@SuppressWarnings("null")//TODO Review if part could be null in last assignment. There we can add an if statement to check it.
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		StrategyAppPart part = null;
		if (model instanceof CommandEventModel) {
			part = new SimpleEventPart();
			part.setBackgroundColor(ColorConstants.red);
		} else if (model instanceof BoundsModel) {
			part = new BoundsPart();
			BoundsModel cmodel = (BoundsModel)model;
			part.setBackgroundColor(
					cmodel.isLower()?ColorConstants.lightBlue:ColorConstants.red);
		}else if (model instanceof EventsCanvasModel) {
			part = new EventsCanvasPart();
		} else if (model instanceof ConditionalCommandEventModel) {
			part = new CollectionPart();
			part.setBackgroundColor(ColorConstants.lightBlue);
		} else if (model instanceof NOTEventModel) {
			part = new CollectionPart();
			part.setBackgroundColor(ColorConstants.lightGray);
		}  else if (model instanceof SimpleEventModel) {
			part = new SimpleEventPart();
		} else if (model instanceof CollectionEventModel) {
			part = new CollectionPart();
		} 
		part.setModel(model);
		return part;
	}
}