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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class AppTreeEditPartFactory implements EditPartFactory { 
	 
    @Override 
    public EditPart createEditPart(EditPart context, Object model) { 
		EditPart part = null;
		part = new AppAbstractTreeEditPart();
		//if (part != null)
			part.setModel(model);
		return part;
     } 
} 