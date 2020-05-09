package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.swt.widgets.Display;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.ITradeMessage;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.symbols.trading.ui.views.IDashboardWidgetProvider;
import com.mfg.utils.Utils;

public class StrategyAdapter extends FigureAdapter<StrategyFigure> {

	IDashboardWidgetProvider _strategy;

	public StrategyAdapter(DashboardCanvas canvas) {
		super(new StrategyFigure(), canvas);
		setDynamic(true);
	}

	public void show(IDashboardWidgetProvider strategy) {
		_strategy = strategy;
		getFigure().updateContent(strategy);
	}

	@Override
	public void handleLogMessage(final ITradeMessage msg,
			final PortfolioStrategy portfolio) {
		final StrategyFigure fig = getFigure();
		final boolean longClosed = portfolio == null ? true : portfolio
				.getLongOpenedOrdersTotal().isEmpty();
		final boolean shortClosed = portfolio == null ? true : portfolio
				.getShortOpenedOrdersTotal().isEmpty();

		Utils.debug_id(3459932, "Long Closed: " + longClosed
				+ ", Short Closed: " + shortClosed);

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				_strategy.repaint(fig.getContLong(), msg, longClosed,
						EAccountRouting.LONG_ACCOUNT);
				_strategy.repaint(fig.getContShort(), msg, shortClosed,
						EAccountRouting.SHORT_ACCOUNT);
			}
		});
	}

	@Override
	public void updateFromJSON(JSONObject obj) throws JSONException {
		super.updateFromJSON(obj);
	}

	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);

		s.key("name");
		s.value("strategy");
	}

}
