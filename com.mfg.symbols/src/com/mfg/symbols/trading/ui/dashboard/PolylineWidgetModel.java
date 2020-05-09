package com.mfg.symbols.trading.ui.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.symbols.trading.ui.dashboard.PolylineEventGenerator.EventArg;

public class PolylineWidgetModel implements Cloneable {
	private int _numberOfScales;
	private List<RowInfo> _rows;
	private RowInfo[][][] _map;

	public static class RowInfo implements Cloneable {
		public static final String UPDATE_LL_HH = "LLS/HHS";
		public static final String UPDATE_PRICE = "New Price";
		public static final String[] UPDATE_TYPES = { UPDATE_PRICE,
				UPDATE_LL_HH };

		public PolylineEventGenerator.EventColor status;
		public int scale;
		public EquationType polyline;
		public boolean show;
		public boolean include;
		public boolean textWarning;
		public boolean soundWarning;
		public String updateType;

		public RowInfo() {
		}

		public RowInfo(PolylineEventGenerator.EventColor aStatus, int aScale,
				EquationType aType, boolean aShow, boolean aInclude,
				boolean aTextWarning, boolean aSoundWarning, String aUpdateType) {
			super();
			this.status = aStatus;
			this.scale = aScale;
			this.polyline = aType;
			this.show = aShow;
			this.include = aInclude;
			this.textWarning = aTextWarning;
			this.soundWarning = aSoundWarning;
			this.updateType = aUpdateType;
		}

		@Override
		public RowInfo clone() {
			try {
				return (RowInfo) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		public void toJSON(JSONStringer s) throws JSONException {
			s.key("scale");
			s.value(scale);

			s.key("polyline");
			s.value(polyline.name());

			s.key("show");
			s.value(show);

			s.key("include");
			s.value(include);

			s.key("textWarning");
			s.value(textWarning);

			s.key("soundWarning");
			s.value(soundWarning);

			s.key("updateType");
			s.value(updateType);
		}

		public void updateFromJSON(JSONObject obj) throws JSONException {
			scale = obj.getInt("scale");
			polyline = EquationType.valueOf(obj.getString("polyline"));
			show = obj.getBoolean("show");
			include = obj.getBoolean("include");
			textWarning = obj.getBoolean("textWarning");
			soundWarning = obj.getBoolean("soundWarning");
			updateType = obj.getString("updateType");

			if (updateType.equals(UPDATE_LL_HH)) {
				updateType = UPDATE_LL_HH;
			} else {
				updateType = UPDATE_PRICE;
			}
		}

		public boolean updateFromEvent(EventArg arg) {
			// TODO: use data layer 0 for now.
			if (include && scale == arg.getScale() && arg.getDataLayer() == 0
					&& polyline == arg.getPolyline()) {
				status = arg.getEventColor();
				return true;
			}
			return false;
		}
	}

	public PolylineWidgetModel() {
		_rows = new ArrayList<>();
		_map = new RowInfo[3][40][EquationType.values().length];
		_numberOfScales = 0;
	}

	public PolylineWidgetModel(int numberOfScales) {
		this();
		adjustScales(numberOfScales);
	}

	public void adjustScales(int numberOfScales) {
		_numberOfScales = numberOfScales;

		// get only the rows with a bigger scale
		_rows = _rows.stream().filter(r -> r.scale <= _numberOfScales)
				.collect(Collectors.toList());

		// generates rows based on the number of scales
		for (int scale = 2; scale < _numberOfScales; scale++) {
			for (EquationType type : EquationType.values()) {
				final int fscale = scale;
				boolean exist = _rows.stream().anyMatch(
						r -> r.scale == fscale && r.polyline == type);
				if (!exist) {
					_rows.add(new RowInfo(
							PolylineEventGenerator.EventColor.GREEN, scale,
							type, false, false, false, false,
							RowInfo.UPDATE_PRICE));
				}
			}
		}

		buildMap();
	}

	public void buildMap() {
		for (RowInfo info : _rows) {
			_map[0][info.scale][info.polyline.ordinal()] = info;
		}
	}

	@Override
	public PolylineWidgetModel clone() {
		try {
			PolylineWidgetModel clone = (PolylineWidgetModel) super.clone();
			clone._rows = new ArrayList<>();
			for (RowInfo row : _rows) {
				clone._rows.add(row.clone());
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public RowInfo getInfo(int layer, int scale, EquationType polyline) {
		return _map[layer][scale][polyline.ordinal()];
	}

	public List<RowInfo> getRows() {
		return _rows;
	}

	public int getNumberOfScales() {
		return _numberOfScales;
	}

	public void toJSON(JSONStringer s) throws JSONException {
		s.key("numberOfScales");
		s.value(_numberOfScales);

		s.key("rows");
		s.array();
		for (RowInfo row : _rows) {
			s.object();
			row.toJSON(s);
			s.endObject();
		}
		s.endArray();
	}

	public void updateFromJSON(JSONObject obj) throws JSONException {
		_numberOfScales = obj.getInt("numberOfScales");
		_rows = new ArrayList<>();
		JSONArray arr = obj.getJSONArray("rows");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj2 = arr.getJSONObject(i);
			RowInfo info = new RowInfo();
			info.updateFromJSON(obj2);
			_rows.add(info);
		}
	}

	public boolean updateFromEvent(EventArg arg) {
		boolean changed = false;
		List<RowInfo> list = new ArrayList<>(_rows);
		for (RowInfo row : list) {
			if (row.updateFromEvent(arg)) {
				changed = true;
			}
		}
		return changed;
	}
}
