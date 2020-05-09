package com.mfg.logger.ui;

import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;

public abstract class AbstractLogTableModel implements ILogTableModel {
	private final ILogReader reader;

	public AbstractLogTableModel(ILogReader aReader) {
		this.reader = aReader;
	}

	@Override
	public ILogRecord getRecord(int index) {
		return reader.read(index);
	}

	@Override
	public int getRecordCount() {
		return reader.getRecordCount();
	}

	public ILogReader getReader() {
		return reader;
	}
}
