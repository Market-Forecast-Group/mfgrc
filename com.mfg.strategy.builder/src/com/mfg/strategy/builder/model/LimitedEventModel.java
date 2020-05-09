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

package com.mfg.strategy.builder.model;

import com.mfg.strategy.builder.model.psource.PropertiesID;

public abstract class LimitedEventModel extends SimpleEventModel implements ILimitedToSW0 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean limitedToSwing0;


	public LimitedEventModel() {
		super();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.ILimitedToSW0#isLimitedToSwing0()
	 */
	@Override
	public boolean isLimitedToSwing0() {
		return limitedToSwing0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.ILimitedToSW0#setLimitedToSwing0(boolean)
	 */
	@Override
	public void setLimitedToSwing0(boolean aLimitedToSwing0) {
		if (limitedToSwing0 != aLimitedToSwing0) {
			limitedToSwing0 = aLimitedToSwing0;
			firePropertyChange(PropertiesID.PROPERTY_LSW0, null, Boolean.valueOf(aLimitedToSwing0));
		}
	}

}
