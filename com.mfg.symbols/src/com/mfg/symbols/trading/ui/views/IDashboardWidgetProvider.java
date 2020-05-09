package com.mfg.symbols.trading.ui.views;

import org.eclipse.draw2d.IFigure;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.ITradeMessage;

public interface IDashboardWidgetProvider {
	public IFigure createFigure();

	public void repaint(IFigure figure, ITradeMessage msg, boolean closedAccount,
			EAccountRouting routing);
}
