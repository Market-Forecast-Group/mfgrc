package com.mfg.strategy.logger;

import com.mfg.broker.events.TradeMessage;
import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ui.AbstractLogTableModel;

public class StrategyLogTableModel extends AbstractLogTableModel {

	private static String[] COLUMN_NAMES = { "Time", "Price", "Equity Price",
			"Long $ Equity", "Short $ Equity", "Open L.", "Open S.", "Type", "Event",
			"Source", "OrderID", "Strategy Name", "Account Name" };

	public StrategyLogTableModel(ILogReader reader) {
		super(reader);
	}

	@Override
	public String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	@Override
	public Object[] recordToArray(ILogRecord record) {
		TradeMessageWrapper msg = (TradeMessageWrapper) record.getMessage();

		if (msg.getType() == TradeMessage.WHITE_COMMENT) {
			return new Object[] { null, null, null, null, null, null, null,
					null, msg.getEvent(), msg.getSource(), null, null, null };
		}
		int tickScale = msg.getTradeMessage().getTickScale();
		return new Object[] {
				Long.valueOf(msg.getFakeTime()),
				TradeMessage.formatPriceWithScale(msg.getPrice(), tickScale),
				Long.valueOf(msg.getEquity()), Double.valueOf(msg.getLongCapital()),
				Double.valueOf(msg.getShortCapital()), Integer.valueOf(msg.getLongQuantity()),
				Integer.valueOf(msg.getShortQuantity()), msg.getType(), msg.getEvent(),
				msg.getSource(), Integer.valueOf(msg.getOrderID()), msg.getStrategyName(),
				msg.getAccountName() };
	}
}
