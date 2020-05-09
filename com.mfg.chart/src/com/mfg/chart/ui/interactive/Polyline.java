package com.mfg.chart.ui.interactive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mfg.chart.layers.IndicatorLayer.PivotReference;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.chart.ui.interactive.PolylineTool.TrackPoint;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.widget.arc.strategy.IFreehandIndicator;
import com.mfg.widget.arc.strategy.LinearRegFreeIndicator;
import com.mfg.widget.arc.strategy.PolyFreehandIndicator;

public class Polyline {
	private PivotReference _anchor1;
	private PivotReference _anchor2;
	private Map<EquationType, IFreehandIndicator> _indicatorMap;
	public Map<EquationType, float[]> colorsMap;
	public Map<EquationType, Integer> lineTypeMap;
	public Map<EquationType, Integer> widthMap;
	public Map<EquationType, Boolean> mirrorMap;
	public List<EquationType> types;
	public Map<EquationType, List<TrackPoint>> trackLinesMap;
	public boolean realtime;
	public boolean realtimeFollowPivot;
	private double _maxRightAnchor;
	private long _realtimeX;

	public Polyline() {
		realtime = false;
		realtimeFollowPivot = true;

		types = new ArrayList<>();
		types.add(EquationType.AVG);
		types.add(EquationType.POLY_2);

		colorsMap = new HashMap<>();
		widthMap = new HashMap<>();
		lineTypeMap = new HashMap<>();
		trackLinesMap = new HashMap<>();
		mirrorMap = new HashMap<>();

		for (EquationType type : EquationType.values()) {
			colorsMap.put(type, PolylineTool.DEFAULT_COLORS[type.ordinal()]);
			widthMap.put(type, Integer.valueOf(1));
			lineTypeMap.put(type, Integer.valueOf(0));
			trackLinesMap.put(type, Collections.EMPTY_LIST);
			mirrorMap.put(type, Boolean.valueOf(type == EquationType.AVG));
		}

		_indicatorMap = new HashMap<>();
	}

	public int getMaxLevel() {
		int a = getAnchor1().getLevel();
		int b = getAnchor2().getLevel();
		return Math.max(a, b);
	}

	public void updateFromProfile(Profile profile) {
		types = new ArrayList<>();
		colorsMap = new HashMap<>();
		widthMap = new HashMap<>();
		lineTypeMap = new HashMap<>();

		// real-time properties are not part of the profile anymore.
		// realtime = profile.getBoolean(KEY_REALTIME, false);
		// realtimeFollowPivot = profile.getBoolean(K_REALTIME_FOLLOW_PIVOT,
		// true);

		for (EquationType type : EquationType.values()) {
			String tname = type.name();

			boolean hasType = profile.getBoolean("HAS_TYPE_" + tname,
					type.ordinal() < 2);
			if (hasType) {
				types.add(type);
			}

			float[] defColor = PolylineTool.DEFAULT_COLORS[type.ordinal()];
			String colorKey = "COLOR_" + tname;
			colorsMap.put(type, profile.getFloatArray(colorKey, defColor));

			String widthKey = "LINE_WIDTH_" + tname;
			int width = profile.getInt(widthKey, 1);
			widthMap.put(type, Integer.valueOf(width));

			String lineTypeKey = "LINE_TYPE_" + tname;
			int linetype = profile.getInt(lineTypeKey, 0);
			lineTypeMap.put(type, Integer.valueOf(linetype));

			String lineMirrorKey = "LINE_MIRROR_" + tname;
			boolean mirror = profile.getBoolean(lineMirrorKey,
					type == EquationType.AVG);
			mirrorMap.put(type, Boolean.valueOf(mirror));
		}

	}

	public void fillProfile(Profile profile) {
		// real-time properties are not part of the profile anymore.
		// profile.putBoolean(KEY_REALTIME, realtime);
		// profile.putBoolean(K_REALTIME_FOLLOW_PIVOT, realtimeFollowPivot);

		for (EquationType type : EquationType.values()) {
			String tname = type.name();

			boolean hasType = types.contains(type);
			profile.putBoolean("HAS_TYPE_" + tname, hasType);

			String colorKey = "COLOR_" + tname;
			profile.putFloatArray(colorKey, colorsMap.get(type));

			String widthKey = "LINE_WIDTH_" + tname;
			profile.putInt(widthKey, widthMap.get(type).intValue());

			String lineTypeKey = "LINE_TYPE_" + tname;
			profile.putInt(lineTypeKey, lineTypeMap.get(type).intValue());

			String lineMirrorKey = "LINE_MIRROR_" + tname;
			profile.putBoolean(lineMirrorKey, mirrorMap.get(type)
					.booleanValue());
		}

	}

	public void updateFromLine(Polyline other) {
		types = new ArrayList<>(other.types);
		colorsMap = new HashMap<>(other.colorsMap);
		widthMap = new HashMap<>(other.widthMap);
		lineTypeMap = new HashMap<>(other.lineTypeMap);
		mirrorMap = new HashMap<>(other.mirrorMap);
		realtime = other.realtime;
		realtimeFollowPivot = other.realtimeFollowPivot;
	}

	public IFreehandIndicator getIndicator(EquationType type) {
		return _indicatorMap.get(type);
	}

	public void forgetIndicators() {
		_indicatorMap = new HashMap<>();
	}

	public boolean isTouchTop(EquationType type) {
		IFreehandIndicator indicator = _indicatorMap.get(type);
		if (realtimeFollowPivot) {
			return indicator.isTopTouching()
					|| indicator.getMinimumTopTouch() != Integer.MAX_VALUE;
		}
		return indicator.isTopTouching();
	}

	public boolean isTouchBottom(EquationType type) {
		IFreehandIndicator indicator = _indicatorMap.get(type);
		if (realtimeFollowPivot) {
			return indicator.isBottomTouching()
					|| indicator.getMinimumBottomTouch() != Integer.MAX_VALUE;
		}
		return indicator.isBottomTouching();
	}

	public PivotReference getAnchor1() {
		return _anchor1;
	}

	public void setAnchor1(PriceMDBSession session, int dataLayer,
			PivotReference anchor1) {
		try {
			this._anchor1 = anchor1;
			if (anchor1 == null) {
				_indicatorMap.clear();
			}
			if (_anchor1 != null) {
				for (EquationType type : types) {
					IFreehandIndicator indicator = _indicatorMap.get(type);
					if (indicator == null) {
						indicator = createIndicator(session, dataLayer, type,
								_anchor1.getTime());
						_indicatorMap.put(type, indicator);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public PivotReference getAnchor2() {
		return _anchor2;
	}

	public long getRealtimeX() {
		return _realtimeX;
	}

	public void setAnchor2(PriceMDBSession session, int dataLayer,
			long rtPivotTime, long rtPivotPrice, long lastTime,
			PivotReference anchor) {
		PivotReference anchorToSet = anchor;
		if (anchorToSet != null) {
			long x = anchorToSet.getTime();
			if (realtime) {
				int level = _anchor1.getLevel();
				if (realtimeFollowPivot) {
					// get the RT pivot
					x = rtPivotTime;
					long y = rtPivotPrice;
					anchorToSet = new PivotReference(x, y, level);
				} else {
					// get the current time
					x = lastTime;
				}
			}

			if (_anchor1 != null && x != _anchor1.getTime()) {
				setRightAnchor(session, dataLayer, x);
			}
		}
		this._anchor2 = anchorToSet;
	}

	/**
	 * A method just to update the anchor 2. It does not perform any side
	 * opperation like setAnchor2().
	 * 
	 * @param time
	 * @param price
	 * @param level
	 */
	public void forceAnchor2(long time, long price, int level) {
		_anchor2 = new PivotReference(time, price, level);
	}

	/**
	 * Set the right anchor of the line and update the touching.
	 * 
	 * @param indicator
	 * @param x2
	 * @throws IOException
	 */
	public void setRightAnchor(PriceMDBSession session, int dataLayer,
			double anchorX) {
		try {
			int anchor = (int) anchorX;
			_realtimeX = anchor;
			for (EquationType type : types) {
				IFreehandIndicator indicator = null;
				if (anchorX > _maxRightAnchor) {
					indicator = _indicatorMap.get(type);
				}
				if (indicator == null) {
					indicator = createIndicator(session, dataLayer, type,
							_anchor1.getTime());
					_indicatorMap.put(type, indicator);
				}
				indicator.setRightAnchor(anchor);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (anchorX > _maxRightAnchor) {
			_maxRightAnchor = anchorX;
		}
	}

	public long getTopDistance(EquationType type) {
		IFreehandIndicator indicator = _indicatorMap.get(type);
		long dist = (long) indicator.getTopDistance();

		if (realtime && realtimeFollowPivot) {
			// when follow the pivot, we should recompute the distance
			// to the touching point.
			int touchTime = indicator.getMinimumTopTouch();
			// out.println();
			// out.println("touch time " + touchTime);

			if (touchTime == Integer.MAX_VALUE) {
				// there is not a touch after the pivot.
				return dist;
			}
			// there is a touch after the pivot, then compute the new
			// distance

			// compute touching price
			double delta = indicator.getGlobalTopDistanceRight();
			// out.println("top delta " + 0);
			dist = (long) (delta + dist);
			// out.println("New bottom distance (" + dist + ") from ("
			// + indicator.getBottomDistance() + ")");
		}
		return dist;
	}

	public long getBottomDistance(EquationType type) {
		IFreehandIndicator indicator = _indicatorMap.get(type);
		long dist = (long) indicator.getBottomDistance();
		if (realtime && realtimeFollowPivot) {
			// when follow the pivot, we should recompute the distance
			// to the touching point.
			int touchTime = indicator.getMinimumBottomTouch();
			if (touchTime == Integer.MAX_VALUE) {
				// there is not a touch after the pivot.
				return dist;
			}
			// there is a touch after the pivot, then compute the new
			// distance

			// compute touching price
			double delta = indicator.getGlobalBottomDistanceRight();
			//dist += delta;
			dist = (long) (delta - dist);
			// out.println("New bottom distance (" + dist + ") from ("
			// + indicator.getBottomDistance() + ")");

		}
		return dist;
	}

	public static IFreehandIndicator createIndicator(PriceMDBSession session,
			int dataLayer, EquationType type, double x1) throws IOException {
		PriceMDB mdb = session.connectTo_PriceMDB(dataLayer);

		IFreehandIndicator indicator;
		switch (type) {
		case POLY_2:
			indicator = new PolyFreehandIndicator(mdb, (int) x1, 2);
			break;
		case POLY_3:
			indicator = new PolyFreehandIndicator(mdb, (int) x1, 3);
			break;
		case POLY_4:
			indicator = new PolyFreehandIndicator(mdb, (int) x1, 4);
			break;
		case AVG:
		default:
			indicator = new LinearRegFreeIndicator(mdb, (int) x1);
			break;
		}
		return indicator;
	}
}
