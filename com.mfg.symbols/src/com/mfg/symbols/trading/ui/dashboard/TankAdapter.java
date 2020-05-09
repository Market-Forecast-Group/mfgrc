package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.TankFigure;
import org.eclipse.swt.SWT;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class TankAdapter extends WidgetFigureAdapter<TankFigure> {

	private static final String K_FILL_BG = "fillBg";
	private static final String K_FILL_COLOR = "fillColor";
	private static final String K_EFFECT3D = "effect3d";
	private static final String K_NAME = "name";

	public TankAdapter(DashboardCanvas canvas) {
		super(new TankFigure(), canvas);
		TankFigure fig = getFigure();
		fig.setBackgroundColor(canvas.getDisplay().getSystemColor(
				SWT.COLOR_BLACK));
	}

	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);
		TankFigure fig = getFigure();
		s.key(K_NAME);
		s.value("tank");

		s.key(K_EFFECT3D);
		s.value(fig.isEffect3D());

		s.key(K_FILL_COLOR);
		s.value(jsonString(fig.getFillColor()));

		s.key(K_FILL_BG);
		s.value(jsonString(fig.getFillBackgroundColor()));
	}

	@Override
	public void updateFromJSON(JSONObject obj) throws JSONException {
		super.updateFromJSON(obj);
		TankFigure fig = getFigure();
		if (obj.has(K_EFFECT3D)) {
			fig.setEffect3D(obj.getBoolean(K_EFFECT3D));
			fig.setFillColor(parseColor(obj.getString(K_FILL_COLOR)));
			fig.setFillBackgroundColor(parseColor(obj.getString(K_FILL_BG)));
		}
	}
}
