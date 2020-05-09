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

import java.util.Hashtable;
import java.util.List;

import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventCommandContainer extends EventGeneral {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private EventGeneral precondition;
	private EventAtomCommand command;
	private transient boolean flag;

	@Override
	public EventGeneral[] getChildren() {
		return new EventGeneral[] { precondition, command };
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		precondition.init(aDealer);
		myInit();
	}

	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		precondition.preinit(aDealer);
		command.preinit(aDealer);
	}

	private void myInit() {
		flag = true;
	}

	@Override
	protected void collectMyFilledEntries(
			Hashtable<IOrderFilledListener, OrderImpl> table,
			List<OrderImpl> entries) {
		if (table.containsKey(command)) {
			entries.add(table.get(command));
		}
		precondition.collectMyFilledEntries(table, entries);
	}

	@Override
	public void reset() {
		super.reset();
		myInit();
		precondition.reset();
		command.reset();
	}

	// @Override
	// public void addAllEventAtoms(EventsDealer aEventsDealer) {
	// precondition.addAllEventAtoms(aEventsDealer);
	// }

	@Override
	public void cancelThisEvent() {
		precondition.cancelThisEvent();
		command.cancelThisEvent();
	}

	/**
	 * we start checking the precondition event and when it is triggered we
	 * initialize the command event and check if it is triggered. The event is
	 * triggered when the command is triggered but usually commands are
	 * triggered once we check them, so the action of initializing them and
	 * asking for the status will result on triggering the command and so this
	 * COND CMD event. If on the process the precondition or the command is
	 * discarded, then we discard this event.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return {@code true} when the precondition check returns {@code true} or
	 *         when the event gets triggered.
	 */
	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		boolean counted = checkEvent(precondition, aDealer);
		setMatchingEvents(precondition.isMatchingEvents());
		boolean triggered = precondition.isTriggered();
		if (precondition.isDiscarded()) {
			setDiscarded(true);
		}
		if (triggered && flag) {
			flag = false;
			if (command.ready2BChecked()) {
				precondition.logDetails();
				command.init(aDealer);
				command.checkIFTriggered(aDealer);
				if (command.isDiscarded()) {
					setDiscarded(true);
				}
			} else {
				aDealer.logDiscarding(this, "not ready to enter");
				command.ready2BChecked();
				setDiscarded(true);
			}
		}
		setTriggered(command.isTriggered());
		if (command instanceof EventAtomEntry) {
			EventAtomEntry t = (EventAtomEntry) command;
			if (!aDealer.isOkToEnter(t) && !t.isMultipleEntries()) {
				return false;
			}
		}
		return counted || (triggered && flag);
	}

	// @Override
	// protected void setDiscarded(boolean aDiscarded) {
	// super.setDiscarded(aDiscarded);
	// }

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return precondition.getHtmlBody(aUtil) + " then "
				+ command.getHtmlBody(aUtil);
	}

	/**
	 * @return the command
	 */
	// @JSON
	// @Expand
	public EventAtomCommand getCommand() {
		return command;
	}

	/**
	 * @param aCommand
	 *            the command to set
	 */
	public void setCommand(EventAtomCommand aCommand) {
		if (aCommand != null) {
			command = aCommand;
			command.setCommandContainer(this);
			command.setParentEvent(this);
		}
	}

	/**
	 * @return the precondition
	 */
	// @JSON
	// @Expand
	public EventGeneral getPrecondition() {
		return precondition;
	}

	/**
	 * @param aPrecondition
	 *            the precondition to set
	 */
	public void setPrecondition(EventGeneral aPrecondition) {
		if (aPrecondition != null) {
			precondition = aPrecondition;
			precondition.setParentEvent(this);
		}
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
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result
				+ ((precondition == null) ? 0 : precondition.hashCode());
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
		EventCommandContainer other = (EventCommandContainer) obj;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		if (precondition == null) {
			if (other.precondition != null)
				return false;
		} else if (!precondition.equals(other.precondition))
			return false;
		return true;
	}

	@Override
	public EventCommandContainer clone() {
		EventCommandContainer clone = (EventCommandContainer) super.clone();
		clone.setCommand(command.clone());
		clone.setPrecondition(precondition.clone());
		return clone;
	}

	@Override
	public String getLabel() {
		return "COND CMD";
	}

	@Override
	public void getEntriesTo(EventGeneral aRequester,
			List<EventAtomEntry> aEntries, int[] IDs, boolean global) {
		if (precondition != aRequester)
			precondition.getEntriesTo(this, aEntries, IDs, global);
		command.getEntriesTo(this, aEntries, IDs, global);
		if (getParentEvent() != null && getParentEvent() != aRequester)
			getParentEvent().getEntriesTo(this, aEntries, IDs, global);
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return precondition.getScaleTo(aRequester);
	}

	@Override
	public int getBigEntryScale() {
		if (command instanceof EventAtomEntry)
			return precondition.getBigEntryScale();
		return -1;
	}

	@Override
	public boolean gotEntry() {
		return command.gotEntry() || precondition.gotEntry();
	}

	@Override
	public void getDelays(int[] delays) {
		precondition.getDelays(delays);
		command.getDelays(delays);
	}

	@Override
	public void setPresentScales(boolean[] scales) {
		precondition.setPresentScales(scales);
		command.setPresentScales(scales);
	}

	@Override
	public void turnAveragingOn(LSFilterType filter) {
		// isExitAveraging = !(command instanceof EventAtomEntry);
		if (command instanceof EventAtomExit) {
			EventAtomExit ex = (EventAtomExit) command;
			ex.turnAveragingOn(filter);
		}
	}

	@Override
	public boolean needsToBeSplited() {
		return precondition.needsToBeSplited() || command.needsToBeSplited();
	}

	@Override
	public void setBasedOn(LSFilterType filter) {
		precondition.setBasedOn(filter);
		command.setBasedOn(filter);
	}

	@Override
	public void suggestChildren(OrderImpl aEntry) {
		precondition.suggestChildren(aEntry);
	}

	@Override
	public boolean isPure(boolean entry) {
		return precondition.isPure(entry) && command.isPure(entry);
	}

}
