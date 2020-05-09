
package com.mfg.strategy.builder.model;

import com.mfg.strategy.builder.model.psource.PropertiesID;

public abstract class ScaledEventModel extends LimitedEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int widgetScale = 3;


	public ScaledEventModel() {
		super();
	}


	/**
	 * @return the widgetScale
	 */
	public int getWidgetScale() {
		return widgetScale;
	}


	/**
	 * @param aWidgetScale
	 *            the widgetScale to set
	 */
	public void setWidgetScale(int aWidgetScale) {
		if (widgetScale != aWidgetScale) {
			widgetScale = aWidgetScale;
			firePropertyChange(PropertiesID.PROPERTY_WSCALE, null, Integer.valueOf(aWidgetScale));
		}
	}

}
