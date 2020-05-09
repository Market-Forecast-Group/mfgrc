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

import com.mfg.strategy.automatic.eventPatterns.EventAtomTicks;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class TicksEventModel extends LimitedEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;

	private int ticksTH = 1;
	private boolean contrarian;

	@Override
	public String getLabel() {
		String res = "Ticks{" + ((!isLimitedToSwing0()) ? "" : ("on Sw0, "));
		res+=("TickTH="+ticksTH);
		res+=(contrarian?", C":", NC");
		return res + "}";
	}

	public int getTicksTH() {
		return ticksTH;
	}

	public boolean isContrarian() {
		return contrarian;
	}

	public void setTicksTH(int aTicksTH) {
		if (this.ticksTH != aTicksTH) {
			this.ticksTH = aTicksTH;
			firePropertyChange(PropertiesID.PROPERTY_TICKSTOWAIT, null, Integer.valueOf(aTicksTH));
		}
	}

	public void setContrarian(boolean aContrarian) {
		if (this.contrarian != aContrarian) {
			this.contrarian = aContrarian;
			firePropertyChange(PropertiesID.PROPERTY_TICKSCONTRARIAN, null, Boolean.valueOf(aContrarian));
		}
	}



	@Override
	public EventGeneral exportMe() {
		EventAtomTicks res = new EventAtomTicks();
		res.setContrarian(isContrarian());
		res.setTicksTH(getTicksTH());
		res.setLimitToSwingZero(isLimitedToSwing0());
		return res;
	}

}
