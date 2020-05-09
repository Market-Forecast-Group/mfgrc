package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.ProgressBarFigure;
import org.json.JSONException;
import org.json.JSONStringer;

public class ProgressBarAdapter extends WidgetFigureAdapter<ProgressBarFigure> {

	public ProgressBarAdapter(DashboardCanvas canvas) {
		super(new ProgressBarFigure(), canvas);
		getFigure().setHorizontal(true);
	}

	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);

		s.key("name");
		s.value("progress-bar");
	}

}
