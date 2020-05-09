package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.events.ITradeMessage;
import com.mfg.strategy.PortfolioStrategy;

public abstract class FigureAdapter<T extends Figure> {
	protected static final String K_H = "h";
	protected static final String K_W = "w";
	protected static final String K_Y = "y";
	protected static final String K_X = "x";

	private T _figure;
	private DashboardCanvas _canvas;
	private boolean _dynamic;

	public FigureAdapter(T figure, DashboardCanvas canvas) {
		super();
		_figure = figure;
		_canvas = canvas;
		_dynamic = false;
	}

	public void setDynamic(boolean dynamic) {
		_dynamic = dynamic;
	}

	/**
	 * Dynamic adapters change the content from time to time and need to be
	 * re-layout.
	 * 
	 * @return
	 */
	public boolean isDynamic() {
		return _dynamic;
	}

	public T getFigure() {
		return _figure;
	}

	public DashboardCanvas getCanvas() {
		return _canvas;
	}

	public void updateFromJSON(JSONObject obj) throws JSONException {
		int x = obj.getInt(K_X);
		int y = obj.getInt(K_Y);
		int w = obj.getInt(K_W);
		int h = obj.getInt(K_H);

		_figure.setBounds(new Rectangle(x, y, w, h));
	}

	public void toJSON(JSONStringer s) throws JSONException {
		T fig = getFigure();
		Rectangle b = fig.getBounds();
		s.key(K_X);
		s.value(b.x);
		s.key(K_Y);
		s.value(b.y);
		s.key(K_W);
		s.value(b.width);
		s.key(K_H);
		s.value(b.height);
	}

	/**
	 * Handle the strategy message.
	 * 
	 * @param msg
	 *            Message received from the strategy.
	 * @param portfolio
	 */
	public void handleLogMessage(ITradeMessage msg, PortfolioStrategy portfolio) {
		// nothing
	}

	public void close() {
		//
	}

}
