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

import com.mfg.strategy.builder.model.EventModelNode;




public class SimpleEventPart extends StrategyAppPart {

	@Override
	public boolean getCollapsed() {
		return ((EventModelNode)getModel()).isCollapsed();
	}

}
