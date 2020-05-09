package com.mfg.plstats;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ui.views.AbstractLoggerViewControl;

public class ProbabilitiesLoggerControl extends AbstractLoggerViewControl {
	/**
	 * 
	 */
	private long price0;
	private long priceF;
	private int startIndex;
	private int endIndex;

	public ProbabilitiesLoggerControl(ProbabilitiesCalcLogView aView) {
		super();
		setLogView(aView);
		setClients(((ProbabilitiesCalcLogView) getLogView()).getLoggerClients());
		addEventType("TH");
		addEventType("TARGET");
		addEventType("COMMENT");
		addEventType("SCT");
		setEvent("TH");
	}
	public ProbabilitiesLoggerControl() {
		super();
	}

	@Override
	public void setStartTimeToCurrent() {
		ILogRecord currentEvent = getCurrentEvent();
		if (currentEvent == null)
			return;
		ISimpleLogMessage msg = (ISimpleLogMessage) currentEvent.getMessage();
		setStartTime((int) msg.getTime());
		price0 = msg.getPrice();
		startIndex = getLogView().getViewer().getTable().getSelectionIndex();
	}

	@Override
	public void setEndTimeToCurrent() {
		ILogRecord currentEvent = getCurrentEvent();
		if (currentEvent == null)
			return;
		ISimpleLogMessage msg = (ISimpleLogMessage) currentEvent.getMessage();
		setEndTime((int) msg.getTime());
		priceF = msg.getPrice();
		endIndex = getLogView().getViewer().getTable().getSelectionIndex();
	}

	@Override
	public void gotoStartTime() {
		if (startIndex == -1)
			return;
		point(getStartTime(), price0);
		setSelectedIndex(startIndex);
	}

	@Override
	public void gotoEndTime() {
		if (endIndex == -1)
			return;
		point(getEndTime(), priceF);
		setSelectedIndex(endIndex);
	}

	@Override
	protected boolean pointIfMatch(String aEventType, int i) {
		ILogRecord msg = getLogView().getModel().getRecord(i);
		ISimpleLogMessage message = (ISimpleLogMessage) msg.getMessage();
		if (matchs(aEventType, msg)) {
			point(message.getTime(), message.getPrice());
			setSelectedIndex(i);
			return true;
		}
		return false;
	}

	@Override
	public String getType(ILogRecord aRecord) {
		return ((ISimpleLogMessage) aRecord.getMessage()).getCategory();
	}

	@Override
	public long getTime(ILogRecord aRecord) {
		return ((ISimpleLogMessage) aRecord.getMessage()).getTime();
	}

	@Override
	public long getPrice(ILogRecord aRecord) {
		return ((ISimpleLogMessage) aRecord.getMessage()).getPrice();
	}

}
