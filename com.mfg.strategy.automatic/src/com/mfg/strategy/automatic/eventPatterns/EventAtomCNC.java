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

import java.util.Arrays;

import org.json.JSONArray;
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
public class EventAtomCNC extends EventAtom {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private static final String CONTR = null;
	private static final String LIMITEDTOS0 = null;
	private static final String NONCONTR = null;
	private int baseScale;
	private int[] contrarianScales;
	private int[] nonContrarianScales;
	private boolean limitToSwingZero;
	private int ths;

	public EventAtomCNC() {
		super();
		baseScale = 3;
		contrarianScales = new int[0];
		nonContrarianScales = new int[0];
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		if (aDealer != null) {
			baseScale = getParentEvent().getScaleTo(this);
			logScale();
			ths = aDealer.getWidget().getCurrentPivotsCount(baseScale);
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
		boolean bdir = widget.isSwingDown(baseScale);
		boolean res = widget.getCurrentPivotsCount(baseScale) > 0;
		boolean isNew = widget.isThereANewPivot(baseScale);
		if (res) {
			for (int i = 0; i < contrarianScales.length; i++) {
				if (bdir == widget.isSwingDown(contrarianScales[i])) {
					res = false;
					break;
				}
				isNew |= widget.isThereANewPivot(contrarianScales[i]);
			}
		}
		if (res) {
			for (int i = 0; i < nonContrarianScales.length; i++) {
				if (bdir != widget.isSwingDown(nonContrarianScales[i])) {
					res = false;
					break;
				}
				isNew |= widget.isThereANewPivot(nonContrarianScales[i]);
			}
		}
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
		return
		// "scale "
		// + baseScale
		// + " "
		"{"
				+ HtmlUtils.getText(nonContrarianScales.length > 0, "NC="
						+ Arrays.toString(nonContrarianScales), "")
				+ HtmlUtils.getText(contrarianScales.length > 0
						&& nonContrarianScales.length > 0, " / ", "")
				+ HtmlUtils.getText(contrarianScales.length > 0,
						"C=" + Arrays.toString(contrarianScales), "") + "}"
				+ (limitToSwingZero ? ", On Sw0" : "");
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

	/**
	 * @return the contrarianScales
	 */
	// @JSON
	public int[] getContrarianScales() {
		return contrarianScales;
	}

	/**
	 * @param aContrarianScales
	 *            the contrarianScales to set
	 */
	public void setContrarianScales(int[] aContrarianScales) {
		contrarianScales = aContrarianScales;
	}

	/**
	 * @return the nonContrarianScales
	 */
	// @JSON
	public int[] getNonContrarianScales() {
		return nonContrarianScales;
	}

	/**
	 * @param aNonContrarianScales
	 *            the nonContrarianScales to set
	 */
	public void setNonContrarianScales(int[] aNonContrarianScales) {
		nonContrarianScales = aNonContrarianScales;
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return -1;
	}

	@Override
	public int getBigEntryScale() {
		return -1;
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(CONTR);
		stringer.array();
		for (int i = 0; i < contrarianScales.length; i++) {
			stringer.value(contrarianScales[i]);
		}
		stringer.endArray();
		stringer.key(NONCONTR);
		stringer.array();
		for (int i = 0; i < nonContrarianScales.length; i++) {
			stringer.value(nonContrarianScales[i]);
		}
		stringer.endArray();
		stringer.key(LIMITEDTOS0);
		stringer.value(isLimitToSwingZero());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		JSONArray a = json.getJSONArray(CONTR);
		contrarianScales = new int[a.length()];
		for (int i = 0; i < a.length(); i++) {
			contrarianScales[i] = Integer.parseInt(a.get(i).toString());
		}
		a = json.getJSONArray(NONCONTR);
		contrarianScales = new int[a.length()];
		for (int i = 0; i < a.length(); i++) {
			nonContrarianScales[i] = Integer.parseInt(a.get(i).toString());
		}
		setLimitToSwingZero(json.getBoolean(LIMITEDTOS0));
	}

}
