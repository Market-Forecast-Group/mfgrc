package com.mfg.widget.probabilities.logger;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ui.AbstractLogTableModel;

public class ProbabilitiesCalcLogTableModel extends AbstractLogTableModel {

	private static String[] COLUMN_NAMES = { "Time", "Price", "THTime",
			"TH Price", "Target On", "Type", "Event" };

	public ProbabilitiesCalcLogTableModel(ILogReader reader) {
		super(reader);
	}

	@Override
	public String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	@SuppressWarnings("boxing")
	@Override
	public Object[] recordToArray(ILogRecord record) {
		ISimpleLogMessage msg = (ISimpleLogMessage) record.getMessage();

		// if (msg.getType() == TradeMessage.WHITE_COMMENT) {
		// return new Object[] { null, null, null, null, null, null, null,
		// null, msg.getEvent(), msg.getSource(), null, null };
		// } else {
		return new Object[] { msg.getTime(), msg.getPrice(), msg.getTHTime(),
				msg.getTHPrice(),
				msg.getTargetPrice() >= 0 ? msg.getTargetPrice() : "",
				msg.getCategory(), msg.getMessage() };
		// }
	}

}
