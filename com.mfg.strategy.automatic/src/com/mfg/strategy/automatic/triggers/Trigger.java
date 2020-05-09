package com.mfg.strategy.automatic.triggers;

import java.awt.Color;
import java.io.Serializable;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.strategy.automatic.eventPatterns.EventAtomScaleTrigger;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.utils.ui.HtmlUtils.IHtmlStringProvider;

/**
 * base class for triggers.
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public abstract class Trigger implements Serializable, Cloneable,
		IHtmlStringProvider {

	protected transient IIndicator fWidget;
	protected boolean fEnabled = true;
	protected boolean fInitialized = false;
	protected boolean internalIsActive;
	private boolean wasTriggered;
	private boolean triggered;
	private boolean oldinternalIsActive;
	private long lastActive;
	// private transient IExecutionLog logger;
	private transient EventAtomScaleTrigger eventAtomTriger;

	public Trigger() {
		super();
	}

	/**
	 * returns true when this trigger is triggered.
	 * <p>
	 * As default behavior a trigger is triggered if it is Active, and in the
	 * previous tick it was not active.
	 * 
	 * @return {@code true} if this trigger is triggered.
	 */
	public final boolean isTriggered() {
		return triggered;
	}

	public boolean wasTriggered() {
		isActive();
		return wasTriggered;
	}

	/**
	 * asks if this trigger is active or not.
	 * 
	 * @return {@code true} if this trigger is active.
	 */
	public boolean isActive() {
		if (!fEnabled) {
			precomp();
		} else {
			long currentTime = fWidget.getCurrentTime();
			if (currentTime > lastActive) {
				precomp();
				lastActive = currentTime;
			}
		}
		return internalIsActive;
	}

	private void precomp() {
		internalIsActive = !fEnabled || (fInitialized && internalIsActive());
		wasTriggered = wasTriggered || internalIsActive;
		triggered = internalIsActive && !oldinternalIsActive;
		oldinternalIsActive = internalIsActive;

	}

	/**
	 * @return the oldinternalIsActive
	 */
	protected boolean isOldinternalIsActive() {
		return oldinternalIsActive;
	}

	/**
	 * @param aOldinternalIsActive
	 *            the oldinternalIsActive to set
	 */
	protected void setOldinternalIsActive(boolean aOldinternalIsActive) {
		oldinternalIsActive = aOldinternalIsActive;
	}

	/**
	 * asks if this trigger is active or not.
	 * <p>
	 * This method is used for internal proposes.
	 * 
	 * @return {@code true} if this trigger is active.
	 */
	protected abstract boolean internalIsActive();

	/**
	 * called to initialize parameters values for this trigger.
	 * 
	 * @param aWidget
	 *            the widget this trigger will use.
	 */
	public void init(IIndicator aWidget) {
		fWidget = aWidget;
		fInitialized = true;
		lastActive = -1;
		oldinternalIsActive = false;
	}

	/**
	 * gets the widget of this trigger.
	 * 
	 * @return a reference to the widget this trigger is using.
	 */
	public IIndicator getWidget() {
		return fWidget;
	}

	/**
	 * asks if this trigger is enabled.
	 * 
	 * @return {@code true} if this trigger is enabled.
	 */
	// @JSON(index = 0)
	// @Param
	// @Label("Enabled")
	public boolean isEnabled() {
		return fEnabled;
	}

	/**
	 * sets if this trigger is enabled.
	 * 
	 * @param aEnabled
	 *            the value to set.
	 */
	public void setEnabled(boolean aEnabled) {
		fEnabled = aEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Trigger clone() {
		try {
			return (Trigger) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (fEnabled ? 1231 : 1237);
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trigger other = (Trigger) obj;
		if (fEnabled != other.fEnabled)
			return false;
		return true;
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return isEnabled() ? "" : aUtil.underline(aUtil.color("Disabled:",
				Color.GRAY.darker()));
	}

	// public IExecutionLog getLogger() {
	// return logger;
	// }
	//
	// public void setLogger(IExecutionLog logger) {
	// this.logger = logger;
	// }

	public EventAtomScaleTrigger getEventAtomTriger() {
		return eventAtomTriger;
	}

	public void setEventAtomTriger(EventAtomScaleTrigger aEventAtomTriger) {
		this.eventAtomTriger = aEventAtomTriger;
	}

}
