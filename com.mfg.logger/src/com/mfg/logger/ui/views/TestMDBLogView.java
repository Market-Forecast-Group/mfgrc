package com.mfg.logger.ui.views;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.LogLevel;
import com.mfg.logger.LogRecord;
import com.mfg.logger.mdb.IRecordConverter;
import com.mfg.logger.mdb.LogMDB;
import com.mfg.logger.mdb.LogMDB.Appender;
import com.mfg.logger.mdb.LogMDB.Record;
import com.mfg.logger.mdb.LoggerMDBSession;
import com.mfg.logger.mdb.LoggerManager_MDB;
import com.mfg.logger.ui.ILogTableModel;
import com.mfg.logger.ui.LogTableModel;

public class TestMDBLogView extends AbstractLogView {

	private static ILoggerManager manager;

	public TestMDBLogView() {

	}

	@Override
	public String getName() {
		return "Test MDB Log";
	}

	@Override
	protected ILogTableModel createLogModel() {
		return new LogTableModel(getLogManager().getReader());
	}

	@Override
	public ILoggerManager getLogManager() {
		if (manager == null) {
			createManager();
		}
		return manager;
	}

	// @Override
	// protected void fillMenu(IMenuManager menuManager) {
	// super.fillMenu(menuManager);
	// for (int i = 1; i < 10; i++) {
	// final int f = i;
	// menuManager.add(new Action("Set filter %" + f + " = 0") {
	// @Override
	// public void run() {
	// setFilters(new ILogFilter() {
	//
	// @Override
	// public boolean accept(ILogRecord record) {
	// int d = (Integer) record.getMessage();
	// return d % f == 0;
	// }
	// });
	// }
	// });
	// }
	// }

	public static void createManager() {
		try {
			LoggerMDBSession session = new LoggerMDBSession("", new File(
					"c:/test_mdb_log"));
			manager = new LoggerManager_MDB("Test MDB Log Manager", session,
					new IRecordConverter() {

						@Override
						public ILogRecord getLogRecord(Record mdbRecord) {
							return new LogRecord(mdbRecord.ID, LogLevel.ANY, 0,
									"Source", Integer.valueOf(mdbRecord.ID));
						}

						@Override
						public void fillMDBRecord(ILogRecord logRecord,
								Record mdbRecord) {
							// DO NOTHING
						}
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param args
	 *            Maybe used on inner classes.
	 * @throws TimeoutException
	 */
	public static void main_(String[] args) throws IOException,
			TimeoutException {
		LoggerMDBSession session = new LoggerMDBSession("", new File(
				"c:/test_mdb_log"));
		session.getRoot().mkdirs();
		LogMDB mdb = session.connectTo_LogMDB("log.mdb");

		Appender app = mdb.appender();
		for (int i = 0; i < 800000; i++) {
			app.ID = i;
			app.append();
		}
		try {
			session.close();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// MDBSession session = new MDBSession("", new File("c:/test_mdb_log"));
		// LogMDB mdb = session.connectTo_LogMDB(new File(session.getRoot(),
		// "log.mdb"), new File(session.getRoot(), "log.amdb"));
		//
		// for (int i = 0; i < mdb.size(); i++) {
		// out.println(mdb.record(i));
		// }

		// Cursor c = mdb.cursor();
		// while (c.next()) {
		// out.println(c);
		// }
		//
		// c.close();
		session.close();

	}
}
