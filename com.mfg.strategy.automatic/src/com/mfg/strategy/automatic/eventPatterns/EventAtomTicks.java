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

package com.mfg.strategy.automatic.eventPatterns;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

/**
 * describes in terms of direction the relationship between one scale relative
 * to another one. We call this relation Contrarian or Non-Contrarian.
 * Contrarian means that lets say scale 3 swing is up while scale 5 is down.
 * Non-Contrarian means that they are both up or down.
 * <p>
 * When we associate this event with lets say a TH at scale 3 through the AND
 * condition, we can specify that we want to model only those scale 3 TH that
 * occur C or NC to one or more scales by selecting them in the C/NC parameters.
 * 
 * @author gardero
 * 
 */
public class EventAtomTicks extends EventAtom {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private int ticksTH = 1;
	private boolean contrarian;

	private int baseScale;
	private int ths;
	private boolean limitToSwingZero;

	private long startPrice;

	private long deltaTH;

	private boolean swingDown;

	public EventAtomTicks() {
		super();
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		if (aDealer != null) {
			baseScale = getParentEvent().getScaleTo(this);
			logScale();
			ths = aDealer.getWidget().getCurrentPivotsCount(baseScale);

			startPrice = aDealer.getWidget().getCurrentPrice();
			int tickSize = getEventsDealer().getTickSize();
			deltaTH = ticksTH * tickSize;
			swingDown = aDealer.getWidget().isSwingDown(baseScale);
		}

	}

	private void logScale() {
		// not used
		// IExecutionLog llogger = getEventsDealer().getLogger();
		// if (llogger != null) {
		// // if (llogger.isEnabled(EMessageType.Comment)) {
		// // PatternStrategyMessage m = new
		// PatternStrategyMessage(getBirthID(), StrategyMessageType.HTMLComment,
		// "Assuming basescale=" + baseScale
		// // + " for " + this, "Automatic");
		// // llogger.log(m);
		// // }
		// }
	}

	// @Override
	// public void getDelays(int[] delays) {
	// delays[baseScale] = Math.max(delays[baseScale] , 1);
	// for (int i = 0; i < contrarianScales.length; i++) {
	// int p = contrarianScales[i];
	// delays[p] = Math.max(delays[p] , 1);
	// }
	// for (int i = 0; i < nonContrarianScales.length; i++) {
	// int p = nonContrarianScales[i];
	// delays[p] = Math.max(delays[p] , 1);
	// }
	// }

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		IIndicator widget = aDealer.getWidget();
		if (!widget.isLevelInformationPresent(baseScale))
			return false;
		boolean res = false;
		boolean isNew = widget.isThereANewPivot(baseScale);
		long var = widget.getCurrentPrice() - startPrice;
		if (contrarian)
			var = -var;
		if (swingDown)
			var = -var;
		res = (var >= deltaTH);
		boolean sw0flg = isOnRightSwing0();
		setTriggered(res && sw0flg);
		setActive(res && sw0flg);
		return res && isNew;
	}

	public boolean isStrillOnSwing0() {
		return ths == getEventsDealer().getWidget().getCurrentPivotsCount(
				baseScale);
	}

	public boolean isOnRightSwing0() {
		if (!getEventsDealer().getWidget().isLevelInformationPresent(baseScale))
			return false;
		return !limitToSwingZero || isStrillOnSwing0();
	}

	// @JSON
	public boolean isLimitToSwingZero() {
		return limitToSwingZero;
	}

	public void setLimitToSwingZero(boolean aLimitToSwingZero) {
		this.limitToSwingZero = aLimitToSwingZero;
	}

	@Override
	public String getLabel() {
		return getHtmlBody(HtmlUtils.Plain);
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String res = "Ticks{" + ((!limitToSwingZero) ? "" : ("on Sw0, "));
		res += ("TickTH=" + ticksTH);
		res += (contrarian ? ", C" : ", NC");
		// res+=(limitToSwingZero ? ", On Sw0" : "");
		return res + "}";
	}

	/**
	 * @return the baseScale
	 */
	public int getBaseScale() {
		return baseScale;
	}

	/**
	 * @param aBaseScale
	 *            the baseScale to set
	 */
	public void setBaseScale(int aBaseScale) {
		baseScale = aBaseScale;
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return -1;
	}

	@Override
	public int getBigEntryScale() {
		return -1;
	}

	public int getTicksTH() {
		return ticksTH;
	}

	public boolean isContrarian() {
		return contrarian;
	}

	public void setTicksTH(int aTicksTH) {
		this.ticksTH = aTicksTH;
	}

	public void setContrarian(boolean aContrarian) {
		this.contrarian = aContrarian;
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		// stringer.key(CONTR);
		// stringer.array();
		// for (int i = 0; i < contrarianScales.length; i++) {
		// stringer.value(contrarianScales[i]);
		// }
		// stringer.endArray();
		// stringer.key(NONCONTR);
		// stringer.array();
		// for (int i = 0; i < nonContrarianScales.length; i++) {
		// stringer.value(nonContrarianScales[i]);
		// }
		// stringer.endArray();
		// stringer.key(LIMITEDTOS0);
		// stringer.value(isLimitToSwingZero());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		// JSONArray a = json.getJSONArray(CONTR);
		// contrarianScales = new int[a.length()];
		// for (int i = 0; i < a.length(); i++) {
		// contrarianScales[i] = Integer.parseInt(a.get(i).toString());
		// }
		// a = json.getJSONArray(NONCONTR);
		// contrarianScales = new int[a.length()];
		// for (int i = 0; i < a.length(); i++) {
		// nonContrarianScales[i] = Integer.parseInt(a.get(i).toString());
		// }
		// setLimitToSwingZero(json.getBoolean(LIMITEDTOS0));
	}

}
