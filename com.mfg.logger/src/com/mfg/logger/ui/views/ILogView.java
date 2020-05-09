package com.mfg.logger.ui.views;

import java.util.List;

import com.mfg.logger.ILogRecord;

public interface ILogView {
	public String getName();

	public void connectToClient(ILogClient client);

	public void disconnectClient(ILogClient client);

	public AbstractLoggerViewControl getControl();

	public void setControl(AbstractLoggerViewControl control);

	public List<ILogClient> getLoggerClients();

	public ILogRecord getSelectedRecord();
}
