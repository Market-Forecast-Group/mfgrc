/**
 * 
 */

package com.mfg.strategy.automatic.triggers;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.utils.ui.HtmlUtils;

/**
 * this class represents Trigger with a value that reaches an specific cut point to activate it.
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public class RCLevelTrigger extends ScaleSpecificTrigger {

	public RCLevelTrigger() {
		super();
		fWidgetScale = 3;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.marketforecastgroup.priv.strategy.triggers.Trigger#init(com. marketforecastgroup.priv.indicator.PivotsIndicatorWidget)
	 */
	@Override
	public void init(IIndicator aWidget) {
		super.init(aWidget);
		compute();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#isActive()
	 */
	@Override
	protected boolean internalIsActive() {
		if (!fWidget.isLevelInformationPresent(fWidgetScale))
			return false;
		return fWidget.isThereANewRC(fWidgetScale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#getWidget()
	 */
	@Override
	public IIndicator getWidget() {
		return super.getWidget();
	}


	protected void compute() {
		//DO NOTHING
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#clone()
	 */
	@Override
	public RCLevelTrigger clone() {
		return (RCLevelTrigger) super.clone();
	}


	public static int getDelay() {
		return 0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
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
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCLevelTrigger [getWidgetScale()=" + getWidgetScale() + "]";
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "RC{" + super.getHtmlBody(aUtil) + "}";
	}

}
