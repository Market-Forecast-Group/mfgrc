package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.AbstractMarkedWidgetFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.SWTResourceManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public abstract class WidgetFigureAdapter<T extends AbstractMarkedWidgetFigure>
		extends FigureAdapter<T> {
	private static final String K_LOLO_SHOW = "lolo.show";

	private static final String K_LO_SHOW = "lo.show";

	private static final String K_HI_SHOW = "hi.show";

	private static final String K_HIHI_SHOW = "hihi.show";

	private static final String K_LOLO_LEVEL = "lolo.level";

	private static final String K_LO_LEVEL = "lo.level";

	private static final String K_HI_LEVEL = "hi.level";

	private static final String K_HIHI_LEVEL = "hihi.level";

	private static final String K_LOLO_COLOR = "lolo.color";

	private static final String K_LO_COLOR = "lo.color";

	private static final String K_HI_COLOR = "hi.color";

	private static final String K_HIHI_COLOR = "hihi.color";

	private static final String K_FG = "fg";

	private static final String K_BG = "bg";

	private static final String K_LOG_SCALE = "logScale";

	public WidgetFigureAdapter(T figure, DashboardCanvas canvas) {
		super(figure, canvas);

		figure.setHihiColor(SWTResourceManager
				.getColor(XYGraphMediaFactory.COLOR_RED));
		figure.setHiColor(SWTResourceManager
				.getColor(XYGraphMediaFactory.COLOR_ORANGE));
		figure.setLoColor(SWTResourceManager
				.getColor(XYGraphMediaFactory.COLOR_ORANGE));
		figure.setLoloColor(SWTResourceManager
				.getColor(XYGraphMediaFactory.COLOR_RED));
	}

	@Override
	public void updateFromJSON(JSONObject obj) throws JSONException {
		super.updateFromJSON(obj);
		T fig = getFigure();
		if (obj.has(K_BG)) {
			fig.setBackgroundColor(parseColor(obj.getString(K_BG)));
			fig.setForegroundColor(parseColor(obj.getString(K_FG)));
		}

		if (obj.has(K_HIHI_COLOR)) {
			fig.setLogScale(obj.getBoolean(K_LOG_SCALE));

			fig.setHihiColor(parseColor(obj.getString(K_HIHI_COLOR)));
			fig.setHiColor(parseColor(obj.getString(K_HI_COLOR)));
			fig.setLoColor(parseColor(obj.getString(K_LO_COLOR)));
			fig.setLoloColor(parseColor(obj.getString(K_LOLO_COLOR)));

			fig.setHihiLevel(obj.getDouble(K_HIHI_LEVEL));
			fig.setHiLevel(obj.getDouble(K_HI_LEVEL));
			fig.setLoLevel(obj.getDouble(K_LO_LEVEL));
			fig.setLoloLevel(obj.getDouble(K_LOLO_LEVEL));

			fig.setShowHihi(obj.getBoolean(K_HIHI_SHOW));
			fig.setShowHi(obj.getBoolean(K_HI_SHOW));
			fig.setShowLo(obj.getBoolean(K_LO_SHOW));
			fig.setShowLolo(obj.getBoolean(K_LOLO_SHOW));
		}

	}
	
	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);
		T fig = getFigure();

		s.key(K_BG);
		s.value(jsonString(fig.getBackgroundColor()));
		s.key(K_FG);
		s.value(jsonString(fig.getForegroundColor()));
		s.key(K_LOG_SCALE);
		s.value(fig.isLogScale());

		s.key(K_HIHI_COLOR);
		s.value(jsonString(fig.getHihiColor()));
		s.key(K_HI_COLOR);
		s.value(jsonString(fig.getHiColor()));
		s.key(K_LO_COLOR);
		s.value(jsonString(fig.getLoColor()));
		s.key(K_LOLO_COLOR);
		s.value(jsonString(fig.getLoloColor()));

		s.key(K_HIHI_LEVEL);
		s.value(fig.getHihiLevel());
		s.key(K_HI_LEVEL);
		s.value(fig.getHiLevel());
		s.key(K_LO_LEVEL);
		s.value(fig.getLoLevel());
		s.key(K_LOLO_LEVEL);
		s.value(fig.getLoloLevel());

		s.key(K_HIHI_SHOW);
		s.value(fig.isShowHihi());
		s.key(K_HI_SHOW);
		s.value(fig.isShowHi());
		s.key(K_LO_SHOW);
		s.value(fig.isShowLo());
		s.key(K_LOLO_SHOW);
		s.value(fig.isShowLolo());
	}

	protected static String jsonString(Color color) {
		RGB rgb = color.getRGB();
		return rgb.red + "," + rgb.green + "," + rgb.blue;
	}

	protected static Color parseColor(String jsonStr) {
		String[] split = jsonStr.split(",");
		RGB rgb = new RGB(Integer.parseInt(split[0]),
				Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		return SWTResourceManager.getColor(rgb);
	}
}
