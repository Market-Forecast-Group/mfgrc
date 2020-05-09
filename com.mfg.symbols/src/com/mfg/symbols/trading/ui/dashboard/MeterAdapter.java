package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.MeterFigure;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MeterAdapter extends WidgetFigureAdapter<MeterFigure> {

	private static final String K_NEEDLE_COLOR = "needleColor";
	private static final String K_GRADIENT = "gradient";

	public MeterAdapter(DashboardCanvas canvas) {
		super(new MeterFigure(), canvas);
	}

	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);

		MeterFigure fig = getFigure();

		s.key("name");
		s.value("meter");

		s.key(K_NEEDLE_COLOR);
		s.value(jsonString(fig.getNeedleColor()));

		s.key(K_GRADIENT);
		s.value(fig.isGradient());
	}

	@Override
	public void updateFromJSON(JSONObject obj) throws JSONException {
		super.updateFromJSON(obj);

		MeterFigure fig = getFigure();

		if (obj.has(K_NEEDLE_COLOR)) {
			fig.setNeedleColor(parseColor(obj.getString(K_NEEDLE_COLOR)));
			fig.setGradient(obj.getBoolean(K_GRADIENT));
		}
	}
}
