package com.mfg.logger.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;
import com.mfg.logger.ILoggerListener;
import com.mfg.logger.memory.MemoryLoggerManager;

public abstract class AbstractLoggerViewControl {

	public static final String ANY = "<ANY>";

	private List<ILogClient> clients = new ArrayList<>();
	private AbstractLogView logView;
	@SuppressWarnings("unused")
	// Used in returned values.
	private ILogRecord currentEvent;
	private long time0;
	private long timeF;
	private long fGUIID;

	public interface IItemListener {
		void handleItem(Object item);
	}

	final List<String> eventTypes;
	private final List<IItemListener> listeners = new ArrayList<>();
	private final List<IItemListener> listenersSel = new ArrayList<>();

	private String event = ANY;

	public AbstractLoggerViewControl() {
		super();
		eventTypes = new ArrayList<>();
		eventTypes.add(ANY);
	}

	public List<String> getEventTypes() {
		return eventTypes;
	}

	public void addEventType(String type) {
		if (!eventTypes.contains(type)) {
			eventTypes.add(type);
			fireNewEventType(type);
		}
	}

	protected ILogRecord getCurrentEvent() {
		int idx = getLogView().getViewer().getTable().getSelectionIndex();
		if (idx >= 0 && getLogView().getModel().getRecordCount() > 0)
			return currentEvent = getLogView().getModel().getRecord(idx);
		return currentEvent = null;
	}

	protected void setSelectedIndex(int index) {
		logView.getAdapter().scrollToIndex(index);
		TableViewer viewer = logView.getViewer();
		Object data = viewer.getTable().getItem(index).getData();
		if (data != null) {
			viewer.setSelection(new StructuredSelection(data), true);
		}
	}

	public abstract void setStartTimeToCurrent();

	public void setStartTime(int aTime) {
		time0 = aTime;
	}

	public int getStartTime() {
		return (int) time0;
	}

	public void setEndTime(int aTime) {
		timeF = aTime;
	}

	public int getEndTime() {
		return (int) timeF;
	}

	public abstract void setEndTimeToCurrent();

	public abstract void gotoStartTime();

	public abstract void gotoEndTime();

	public void gotoNext(String aEventType) {
		int idx = getLogView().getViewer().getTable().getSelectionIndex();
		int n = getLogView().getModel().getRecordCount();
		for (int i = idx + 1; i < n; i++) {
			if (pointIfMatch(aEventType, i)) {
				return;
			}
		}
	}

	public void gotoPrevious(String aEventType) {
		int idx = getLogView().getViewer().getTable().getSelectionIndex();
		for (int i = idx - 1; i >= 0; i--) {
			if (pointIfMatch(aEventType, i)) {
				return;
			}
		}
	}

	protected boolean matchs(String aEventType, ILogRecord message) {
		return aEventType.equals(ANY) || aEventType.equals(getType(message));
	}

	protected void point(long time, long price) {
		for (ILogClient client : getClients()) {
			client.logSelectionChanged(time, price);
		}
	}

	protected abstract boolean pointIfMatch(String aEventType, int i);

	public void addNewEventTypeListener(IItemListener listener) {
		listeners.add(listener);
	}

	public void removeNewEventTypeListener(IItemListener listener) {
		listeners.remove(listener);
	}

	public void addEventTypeSelectedListener(IItemListener listener) {
		listenersSel.add(listener);
	}

	public void removeEventTypeSelectedListener(IItemListener listener) {
		listenersSel.remove(listener);
	}

	public abstract String getType(ILogRecord aRecord);

	public abstract long getTime(ILogRecord aRecord);

	public abstract long getPrice(ILogRecord aRecord);

	@XmlTransient
	public AbstractLogView getLogView() {
		return logView;
	}

	public void setLogView(AbstractLogView aLogView) {
		logView = aLogView;
		fGUIID = aLogView.getGUID();
		connectToLogger();
	}

	public MemoryLoggerManager getTheMemoryLoggerManager() {
		return (MemoryLoggerManager) getLogView().getLogManager();
	}

	@XmlTransient
	public List<ILogClient> getClients() {
		return clients;
	}

	public void setClients(List<ILogClient> aClients) {
		clients = aClients;
	}

	private void connectToLogger() {
		getTheMemoryLoggerManager().addLoggerListener(new ILoggerListener() {
			@Override
			public void logged(ILogger aLogger, ILogRecord aRecord) {
				String type = getType(aRecord);
				if (!eventTypes.contains(type)) {
					eventTypes.add(type);
					if (type.equals("TH"))
						setEvent(type);
					fireNewEventType(type);
					System.out.println("new type " + type);
				}

			}

			@Override
			public void begin(ILogger logger, String msg) {
				// Documenting empty method to avoid warning.
			}

		});
	}

	void fireNewEventType(String aType) {
		for (IItemListener it : listeners) {
			it.handleItem(aType);
		}
	}

	private void fireEventSelectedType(String aType) {
		for (IItemListener it : listenersSel) {
			it.handleItem(aType);
		}
	}

	@XmlElement
	public long getGUIID() {
		return fGUIID;
	}

	public void setGUIID(long aGUIID) {
		fGUIID = aGUIID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fGUIID ^ (fGUIID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractLoggerViewControl other = (AbstractLoggerViewControl) obj;
		if (fGUIID != other.fGUIID)
			return false;
		return true;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String aEvent) {
		event = aEvent;
		fireEventSelectedType(aEvent);
	}

	public void gotoTime(int time) {
		int n = getLogView().getModel().getRecordCount();
		for (int i = 0; i < n; i++) {
			int eTime = (int) getTime(getLogView().getModel().getRecord(i));
			if (eTime >= time) {
				setSelectedIndex(i);
				return;
			}
		}
	}
}
