package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.swt.SWT;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class GaugeAdapter extends WidgetFigureAdapter<GaugeFigure> {

	private static final String K_GRADIENT = "gradient";
	private static final String K_EFFECT3D = "effect3d";
	private static final String K_NEEDLE_COLOR = "needleColor";
	private static final String K_GAUGE = "gauge";
	private static final String K_NAME = "name";

	public GaugeAdapter(DashboardCanvas canvas) {
		super(new GaugeFigure(), canvas);
		GaugeFigure fig = getFigure();
		fig.setBackgroundColor(canvas.getDisplay().getSystemColor(
				SWT.COLOR_BLACK));
	}

	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);
		GaugeFigure fig = getFigure();

		s.key(K_NAME);
		s.value(K_GAUGE);

		s.key(K_NEEDLE_COLOR);
		s.value(jsonString(fig.getNeedleColor()));

		s.key(K_EFFECT3D);
		s.value(fig.isEffect3D());

		s.key(K_GRADIENT);
		s.value(fig.isGradient());

	}

	@Override
	public void updateFromJSON(JSONObject obj) throws JSONException {
		super.updateFromJSON(obj);
		GaugeFigure fig = getFigure();
		if (obj.has(K_NEEDLE_COLOR)) {
			fig.setNeedleColor(parseColor(obj.getString(K_NEEDLE_COLOR)));
			fig.setEffect3D(obj.getBoolean(K_EFFECT3D));
			fig.setGradient(obj.getBoolean(K_GRADIENT));
		}
	}

}
