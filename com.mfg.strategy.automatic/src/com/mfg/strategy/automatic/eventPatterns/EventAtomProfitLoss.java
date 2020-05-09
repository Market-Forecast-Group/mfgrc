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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

/**
 * allows us to consider another event like a TH only if trades that are still
 * open are in profit or loss.
 * 
 * @author gardero
 * 
 */
public class EventAtomProfitLoss extends EventAtom {

	private boolean limitToSwingZero;
	private int baseScale;
	private int ths;
	private int positions;
	private double offset;
	private boolean relative;

	protected ArrayList<EventAtomEntry> collectEntries(boolean aGlobal) {
		ArrayList<EventAtomEntry> _entries = new ArrayList<>();
		getParentEvent().getEntriesTo(this, _entries, fEntries, aGlobal);
		return _entries;
	}

	protected void collectEntries() {
		if (currentFilter == LSFilterType.Auto) {
			entries = collectEntries(false);
			if (entries.size() > 0) {
				EventAtomEntry myEntry = entries.get(0);
				if (myEntry.getOrder() != null) {
					if (myEntry.getOrder().isLong())
						currentFilter = LSFilterType.Long;
					else
						currentFilter = LSFilterType.Short;
				}
			}
		}
		if (simplePL) {
			entries = collectEntries(isAveragingGain());
		} else {
			entries = new ArrayList<>();
			if (entries.size() > 0) {
				for (int fID : fEntries) {
					List<EventAtomEntry> list = getEventsDealer()
							.getEntriesIDTable().get(Integer.valueOf(fID));
					if (list != null) {
						entries.addAll(list);
					}
				}
			} else {
				entries = getEventsDealer().getFilledEntriesList();
			}
		}
	}

	private double getSingleGain(EventAtomEntry e) {
		if (isConsideringQ()) {
			return e.getGain();
		}
		return e.getPlainGain();
	}

	private double getFactor(EventAtomEntry e) {
		if (isConsideringQ() && isAveragingGain()) {
			return Math.abs(e.getQuantity());
		}
		return 1;
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

	public enum ProfitLoss {

		Profit {

			@Override
			public boolean match(double aGain, double TH) {
				return aGain >= TH;
			}
		},
		Loss {

			@Override
			public boolean match(double aGain, double TH) {
				return aGain <= -TH;
			}
		};

		public abstract boolean match(double aGain, double TH);
	}

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private ProfitLoss type;
	private LSFilterType filterType = LSFilterType.Auto;
	private LSFilterType currentFilter = LSFilterType.Auto;
	private boolean consideringQ;
	private boolean averagingGain;
	private List<EventAtomEntry> entries;
	private int[] fEntries;
	private int fTicksTH = 1;
	private long deltaTH;
	private double gain;
	private boolean simplePL;
	private boolean firstFlag;

	public EventAtomProfitLoss() {
		super();
		type = ProfitLoss.Profit;
		fEntries = new int[0];
		setTicksTH(0);
		consideringQ = true;
		averagingGain = true;
	}

	public EventAtomProfitLoss(ProfitLoss aType, boolean aConsideringQ,
			boolean aAveragingGain, int aDeltaTH, LSFilterType aFilterType) {
		this();
		this.type = aType;
		this.consideringQ = aConsideringQ;
		this.averagingGain = aAveragingGain;
		setTicksTH(aDeltaTH);
		this.filterType = aFilterType;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		if (aDealer != null) {
			collectEntries();
			if (!entries.isEmpty()) {
				baseScale = entries.get(0).getWidgetScale();
				ths = aDealer.getWidget().getCurrentPivotsCount(baseScale);
				logScale();
			}
		}
		firstFlag = true;
		int tickSize = getEventsDealer().getTickSize();
		deltaTH = fTicksTH * tickSize;
		checkGain();
	}

	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		Arrays.sort(this.fEntries);
		simplePL = !inside(GlobalAveragingRule.class);
		if (!simplePL) {
			getRoot().turnAveragingOn(filterType);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " G=" + gain;
	}

	private LSFilterType getCurrentFilter() {
		return currentFilter;
	}

	private boolean checkGain() {
		gain = 0;
		int divF = 0;
		positions = 0;
		for (EventAtomEntry e : entries) {
			if (e.isTriggered() && e.isFilled()
					&& getCurrentFilter().matchEntry(e.getOrder().getType())) {
				double singleGain = getSingleGain(e);
				gain += singleGain;
				divF += getFactor(e);
				positions++;
				if (firstFlag && isRelative()) {
					offset = -singleGain;
					firstFlag = false;
				}
				if (!isAveragingGain()) {
					if (!type.match(singleGain + offset, deltaTH))
						return false;
				}
			}
		}
		if (isAveragingGain() && divF != 0 && positions > 1) {
			gain = gain / divF;
			if (firstFlag && isRelative()) {
				offset = -gain;
				firstFlag = false;
			}
		}
		if (isAveragingGain()) {
			if (!type.match(gain + offset, deltaTH))
				return false;
		}
		return (simplePL && positions >= 1) || (!simplePL && positions > 1);
	}

	@Override
	public void logDetails() {
		if (isAveragingGain()) {
			for (EventAtomEntry e : entries) {
				if (e.isTriggered()
						&& getEventsDealer().getFilledEntries().contains(
								e.getOrder())
						&& getCurrentFilter()
								.matchEntry(e.getOrder().getType())) {
					double singleGain = getSingleGain(e);
					logGain("Element " + e.getOrder() + " ", singleGain);
				}
			}
			logGain("Mean ", gain);
		}
	}

	/**
	 * @param str
	 * @param aGain
	 */
	protected void logGain(String str, double aGain) {
		// not used
		// IExecutionLog llogger = getEventsDealer().getLogger();
		// if (llogger != null) {
		// // if (llogger.isEnabled(EMessageType.Comment)) {
		// // PatternStrategyMessage m = new
		// // PatternStrategyMessage(getBirthID(),
		// // StrategyMessageType.HTMLComment, str + " Gain=" + gain,
		// // "Automatic");
		// // llogger.log(m);
		// // }
		// }
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		if (aDealer.isThereAnewEntry())
			collectEntries();
		boolean res = checkGain();
		boolean sw0flg = isOnRightSwing0();
		setTriggered(res && sw0flg);
		setActive(res && sw0flg);
		setDiscarded(!sw0flg);
		return res;
	}

	public boolean isStrillOnSwing0() {
		return ths == getEventsDealer().getWidget().getCurrentPivotsCount(
				baseScale);
	}

	public boolean isOnRightSwing0() {
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
		return type.toString() + getRest();
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return (fTicksTH != 0 ? (fTicksTH + " ticks ") : "")
				+ aUtil.bold(HtmlUtils.getText(type == ProfitLoss.Profit,
						aUtil.color(type.toString(), Color.green.darker()),
						aUtil.color(type.toString(), Color.red.darker())))
				+ getRest() + (isConsideringQ() ? ", considerQ" : "")
				+ (isAveragingGain() ? ", Averaging" : "")
				+ (limitToSwingZero ? ", On Sw0" : "");
	}

	private String getRest() {
		return (fEntries.length == 0 ? "*" : (" of " + Arrays
				.toString(fEntries)))
				+ (getFilterType() == LSFilterType.Auto ? ""
						: (" " + getFilterType()));
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return -1;
	}

	@Override
	public int getBigEntryScale() {
		return -1;
	}

	/**
	 * @return the entries
	 */
	// @JSON
	public int[] getEntries() {
		return fEntries;
	}

	/**
	 * @param aEntries
	 *            the entries to set
	 */
	public void setEntries(int[] aEntries) {
		fEntries = aEntries;
		Arrays.sort(fEntries);
	}

	/**
	 * @return the type
	 */
	// @JSON
	public ProfitLoss getType() {
		return type;
	}

	/**
	 * @param aType
	 *            the type to set
	 */
	public void setType(ProfitLoss aType) {
		type = aType;
	}

	/**
	 * @return the ticksTH
	 */
	// @JSON
	public int getTicksTH() {
		return fTicksTH;
	}

	/**
	 * @param aTicksTH
	 *            the ticksTH to set
	 */
	public void setTicksTH(int aTicksTH) {
		fTicksTH = aTicksTH;
	}

	// @JSON
	public boolean isConsideringQ() {
		return consideringQ;
	}

	public void setConsideringQ(boolean aConsideringQ) {
		this.consideringQ = aConsideringQ;
	}

	// @JSON
	public LSFilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(LSFilterType aFilterType) {
		this.filterType = aFilterType;
	}

	// @JSON
	public boolean isAveragingGain() {
		return averagingGain;
	}

	public void setAveragingGain(boolean aAveragingGain) {
		this.averagingGain = aAveragingGain;
	}

	@Override
	public boolean needsToBeSplited() {
		return filterType == LSFilterType.Auto && !isMixingLS();
	}

	@Override
	public void setBasedOn(LSFilterType filter) {
		this.setFilterType(filter);
	}

	private boolean mixingLS;

	// @JSON
	public boolean isMixingLS() {
		return mixingLS;
	}

	public void setMixingLS(boolean aMixingLS) {
		this.mixingLS = aMixingLS;
	}

	public boolean isRelative() {
		return relative;
	}

	public void setRelative(boolean aRelative) {
		this.relative = aRelative;
	}

}
