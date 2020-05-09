/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gadero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

/**
 * represents the threshold event (TH). It is the simplest event we can define
 * and it occurs when the Indicator detects at a certain scale that the swing is
 * changing the direction. The event is triggered at that moment. The TH
 * parameters are:
 * <ol>
 * <li>The Widget scale parameter defines at which scale to consider TH.
 * <li>TH to Skip allows to consider future TH.
 * </ol>
 * <p>
 * The second parameter means that the event can also be set to be triggered
 * after a specific number of skipped thresholds. So if we set 2 in the TH to
 * Skip parameter, the event will be triggered after it skips 2 thresholds, that
 * is on the 3rd one we get during the check process.
 * </p>
 * 
 * @author gardero
 */
public class EventAtomTH extends EventAtom {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private static final String SCALE = "SCALE";

	private static final String TH2SKIP = "TH2SKIP";

	private int fTHtoSkip, thCount;

	public EventAtomTH() {
		super();
		fWidgetScale = 3;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		thCount = 0;
	}

	private int fWidgetScale;

	/**
	 * checks if it in a TH after skipping all the THs that it needs to skip, it
	 * will never be discarded because we always keep the hope to reach the n-th
	 * TH.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return {@code true} if there is a new pivot.
	 */
	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		if (!aDealer.getWidget().isLevelInformationPresent(fWidgetScale))
			return false;
		boolean isNewPivot = aDealer.getWidget().isThereANewPivot(fWidgetScale);
		if (isNewPivot)
			thCount++;
		setTriggered(isNewPivot && fTHtoSkip < thCount);
		return isNewPivot;
	}

	// private void logTH(EventsDealer aDealer, boolean thereANewPivot) {
	// IExecutionLog logger = aDealer.getLogger();
	// StrategyMessage msg = new StrategyMessage(StrategyMessageType.Comment,
	// "new th="+thereANewPivot+", thCount="+thCount, "Automatic");
	// logger.log(msg);
	// }
	//
	// private void logTH(EventsDealer aDealer) {
	// IExecutionLog logger = aDealer.getLogger();
	// StrategyMessage msg = new StrategyMessage(StrategyMessageType.NewTH,
	// "<html><body>" + "Activated " + this + "</body></html>", "Automatic");
	// logger.log(msg);
	// }

	/**
	 * @return the widgetScale
	 */
	// @JSON
	public int getWidgetScale() {
		return fWidgetScale;
	}

	/**
	 * @param aWidgetScale
	 *            the widgetScale to set
	 */
	public void setWidgetScale(int aWidgetScale) {
		fWidgetScale = aWidgetScale;
	}

	/**
	 * @return the number of TH to skip
	 */
	// @JSON
	public int getTHtoSkip() {
		return fTHtoSkip;
	}

	/**
	 * @param aTHtoSkip
	 *            the number of TH to skip to set
	 */
	public void setTHtoSkip(int aTHtoSkip) {
		fTHtoSkip = aTHtoSkip;
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(SCALE);
		stringer.value(getWidgetScale());
		stringer.key(TH2SKIP);
		stringer.value(getTHtoSkip());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		setWidgetScale(json.getInt(SCALE));
		setTHtoSkip(json.getInt(TH2SKIP));
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "TH(scale=" + getWidgetScale() + thToSkipText() + ")";
	}

	private String thToSkipText() {
		if (fTHtoSkip > 0)
			return ", Skip " + fTHtoSkip;
		return "";
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventAtomTH other = (EventAtomTH) obj;
		if (fWidgetScale != other.fWidgetScale)
			return false;
		return true;
	}

	@Override
	public String getLabel() {
		return getHtmlBody(HtmlUtils.Plain);
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return fWidgetScale;
	}

	@Override
	public void setPresentScales(boolean[] scales) {
		scales[getWidgetScale()] = true;
	}

	@Override
	public int getBigEntryScale() {
		return fWidgetScale;
	}

	private boolean _requiresConfirmation = false;
	private boolean _playSound = false;
	private String _soundPath;
	private boolean _speak = false;

	public boolean isRequiresConfirmation() {
		return _requiresConfirmation;
	}

	public void setRequiresConfirmation(boolean requiresConfirmation) {
		_requiresConfirmation = requiresConfirmation;
	}

	public boolean isPlaySound() {
		return _playSound;
	}

	public void setPlaySound(boolean playSound) {
		_playSound = playSound;
	}
	
	public void setSoundPath(String path) {
		_soundPath = path; 
	}
	
	public String getSoundPath() {
		return _soundPath;
	}
	
	public boolean isSpeak() {
		return _speak;
	}
	
	public void setSpeak(boolean speak) {
		_speak = speak;
	}
	
}
