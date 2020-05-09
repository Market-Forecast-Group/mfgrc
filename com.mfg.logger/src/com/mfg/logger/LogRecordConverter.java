package com.mfg.logger;

import java.util.HashMap;

import com.mfg.logger.mdb.IRecordConverter;
import com.mfg.logger.mdb.LogMDB.Record;

public class LogRecordConverter implements IRecordConverter {

	private final HashMap<Double, LogLevel> logLevelRecords = new HashMap<>();
	private final HashMap<Integer, String> messageSourceRecordsIS = new HashMap<>();
	private final HashMap<String, Integer> messageSourceRecordsSI = new HashMap<>();

	@Override
	public void fillMDBRecord(ILogRecord aLogRecord, Record aMdbRecord) {
		aMdbRecord.ID = aLogRecord.getID();
		aMdbRecord.timeGeneral = aLogRecord.getTimeMillis();
		aMdbRecord.priority = aLogRecord.getLevel().getPriority();
		aMdbRecord.source = 0;// TODO: this send a null-pointer -->
								// messageSourceRecordsSI.get(aLogRecord.getSource());
		fillMDBRecord(aLogRecord.getMessage(), aMdbRecord);
	}

	@Override
	public ILogRecord getLogRecord(Record aMdbRecord) {
		return new LogRecord(aMdbRecord.ID, logLevelRecords.get(Float
				.valueOf(aMdbRecord.priority)), aMdbRecord.timeGeneral,
				messageSourceRecordsIS.get(Integer.valueOf(aMdbRecord.source)),
				getMessage(aMdbRecord));
	}

	/**
	 * @param aMdbRecord
	 *            Used on inner classes
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes
	public Object getMessage(Record aMdbRecord) {
		return null;
	}

	/**
	 * @param aMessage
	 *            Used on inner classes.
	 * @param aMdbRecord
	 *            Used on inner classes.
	 */
	public void fillMDBRecord(Object aMessage, Record aMdbRecord) {
		// Adding a comment to avoid empty block warning.
	}

	public void addLogLevel(LogLevel level) {
		logLevelRecords.put(Double.valueOf(level.getPriority()), level);
	}

	public void addMessageSource(String source) {
		if (!messageSourceRecordsSI.containsKey(source)) {
			int id = messageSourceRecordsIS.size();
			messageSourceRecordsSI.put(source, Integer.valueOf(id));
			messageSourceRecordsIS.put(Integer.valueOf(id), source);
		}
	}

}
