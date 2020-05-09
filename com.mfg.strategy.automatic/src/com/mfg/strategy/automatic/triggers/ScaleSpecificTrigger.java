/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.triggers;

import com.mfg.utils.ui.HtmlUtils;

/**
 * base class for the triggers based on specific scales.
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public abstract class ScaleSpecificTrigger extends Trigger {

	protected int fWidgetScale;


	/**
	 * gets the widget scale of this trigger.
	 * 
	 * @return the widgetScale
	 */
	// @JSON(index=100)
	// @Param
	// @Label("Widget Scale")
	public int getWidgetScale() {
		return fWidgetScale;
	}


	/**
	 * sets the widget scale of this trigger.
	 * 
	 * @param aWidgetScale
	 *            the new widget scale to set
	 */
	public void setWidgetScale(int aWidgetScale) {
		fWidgetScale = aWidgetScale;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#clone()
	 */
	@Override
	public ScaleSpecificTrigger clone() {
		return (ScaleSpecificTrigger) super.clone();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + fWidgetScale;
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScaleSpecificTrigger other = (ScaleSpecificTrigger) obj;
		if (fWidgetScale != other.fWidgetScale)
			return false;
		return true;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return super.getHtmlBody(aUtil) + "scale=" + fWidgetScale;
	}
}
