package com.mfg.symbols.trading.ui.dashboard;

import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.mfg.chart.layers.IndicatorLayer.PivotReference;
import com.mfg.chart.ui.interactive.Polyline;
import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.common.QueueTick;
import com.mfg.dm.TickAdapter;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.symbols.jobs.InputPipe;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.trading.ui.dashboard.PolylineWidgetModel.RowInfo;
import com.mfg.ui.UIPlugin;
import com.mfg.widget.arc.math.geom.PolyEvaluator;
import com.mfg.widget.arc.strategy.IFreehandIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

public class PolylineEventGenerator {
	public enum EventColor {
		GREEN, RED
	}

	public static class EventArg {
		private EventColor _eventColor;
		private EquationType _polyline;
		private int _scale;
		private int _dataLayer;
		private long _anchor1_time;
		private long _anchor1_price;
		private long _anchor2_time;
		private long _anchor2_price;
		private double _params;

		public EventArg() {
		}

		public EventArg(EventColor eventColor, EquationType polyline,
				int scale, int dataLayer, long anchor1_time,
				long anchor1_price, long anchor2_time, long anchor2_price,
				double params) {
			super();
			_eventColor = eventColor;
			_polyline = polyline;
			_scale = scale;
			_dataLayer = dataLayer;
			_anchor1_time = anchor1_time;
			_anchor1_price = anchor1_price;
			_anchor2_time = anchor2_time;
			_anchor2_price = anchor2_price;
			_params = params;
		}

		public EventColor getEventColor() {
			return _eventColor;
		}

		public void setEventColor(EventColor eventColor) {
			_eventColor = eventColor;
		}

		public EquationType getPolyline() {
			return _polyline;
		}

		public void setPolyline(EquationType polyline) {
			_polyline = polyline;
		}

		public int getScale() {
			return _scale;
		}

		public void setScale(int scale) {
			_scale = scale;
		}

		public int getDataLayer() {
			return _dataLayer;
		}

		public void setDataLayer(int dataLayer) {
			_dataLayer = dataLayer;
		}

		public long getAnchor1_time() {
			return _anchor1_time;
		}

		public void setAnchor1_time(long anchor1_time) {
			_anchor1_time = anchor1_time;
		}

		public long getAnchor1_price() {
			return _anchor1_price;
		}

		public void setAnchor1_price(long anchor1_price) {
			_anchor1_price = anchor1_price;
		}

		public long getAnchor2_time() {
			return _anchor2_time;
		}

		public void setAnchor2_time(long anchor2_time) {
			_anchor2_time = anchor2_time;
		}

		public long getAnchor2_price() {
			return _anchor2_price;
		}

		public void setAnchor2_price(long anchor2_price) {
			_anchor2_price = anchor2_price;
		}

		public double getParams() {
			return _params;
		}

		public void setParams(double params) {
			_params = params;
		}
	}

	public interface IListener {
		public void onEvent(EventArg arg);
	}

	private MultiscaleIndicator _indicator;
	protected Polyline[] _polylineMap;
	/**
	 * map [polyline][scale] = eventColor
	 */
	protected Object[][] _eventMap;
	/**
	 * LAYER x SCALE x POLY-EQUATION
	 */
	private InputPipe _pipe;
	private PriceMDBSession _session;
	private PriceMDB[] _mdbMap;
	private TickAdapter[] _tickListeners;
	private List<IListener> _generatorListeners;
	private PolylineWidgetModel _model;
	protected PolylineWarningDialog _warningDlg;

	public PolylineEventGenerator(InputPipe pipe) throws IOException {
		_pipe = pipe;
		_session = _pipe.getSymbolJob().getMdbSession();

		int dataLayersCount = _session.getDataLayersCount();

		_mdbMap = new PriceMDB[dataLayersCount];

		for (int i = 0; i < dataLayersCount; i++) {
			_mdbMap[i] = _session.connectTo_PriceMDB(i);
		}

		// for(int layer = 0; layer < dataLayesCount; layer++) {
		// only use layer 0 for now
		final int layer = 0;

		_indicator = pipe.getIndicator().getLayers().get(layer);
		// TODO: we have to create an array of indicators per layer, but let's
		// use one layer for now
		int scaleCount = _indicator.getChscalelevels();
		_polylineMap = new Polyline[scaleCount + 1];
		_eventMap = new Object[EquationType.values().length][scaleCount + 1];

		for (int scale = 2; scale <= scaleCount; scale++) {
			Polyline line = new Polyline();
			reset(line, scale);
			_polylineMap[scale] = line;
		}

		_tickListeners = new TickAdapter[dataLayersCount];

		SymbolJob<?> job = pipe.getSymbolJob();

		TickAdapter listener = new TickAdapter() {
			@Override
			public void onNewTick(QueueTick qt) {
				if (job.isWarmupCompleted()) {
					newTick(layer, qt);
				}
			}
		};
		_tickListeners[layer] = listener;

		job.getDataSource().addTickListener(layer, listener);

		_generatorListeners = new ArrayList<>();
	}

	public Polyline getPolyline(int scale) {
		return _polylineMap[scale];
	}

	private void resetLine(int scale) {
		Pivot pivot = _indicator.getLastPivot(0, scale);
		if (pivot != null) {
			Polyline line = _polylineMap[scale];

			reset(line, scale);

			PivotReference anchor = new PivotReference(pivot.getPivotTime(),
					pivot.getPivotPrice(), scale);
			line.setAnchor1(_session, 0, anchor);
		}
	}

	private void reset(Polyline line, int scale) {
		List<EquationType> types = new ArrayList<>();
		if (_model != null) {
			for (EquationType type : EquationType.values()) {
				RowInfo info = _model.getInfo(0, scale, type);
				if (info.include) {
					types.add(type);
				}
			}
		}
		line.realtime = true;
		line.types = types;
		for (EquationType type : types) {
			line.mirrorMap.put(type, Boolean.FALSE);
		}
		line.forgetIndicators();
	}

	public void configure(PolylineWidgetModel model) {
		_model = model;
		resetAllScales();
	}

	private void resetAllScales() {
		for (int scale = 2; scale <= _indicator.getChscalelevels(); scale++) {
			boolean included = false;
			for (EquationType type : EquationType.values()) {
				RowInfo info = _model.getInfo(0, scale, type);
				if (info.include) {
					included = true;
					break;
				}
			}

			if (included) {
				resetLine(scale);
			}
		}
	}

	public PolylineWidgetModel getModel() {
		return _model;
	}

	public void addListener(IListener l) {
		_generatorListeners.add(l);
	}

	public void removeListener(IListener l) {
		_generatorListeners.remove(l);
	}

	protected void fireListeners(EventArg arg) {
		if (arg.getEventColor() != _eventMap[arg.getPolyline().ordinal()][arg
				.getScale()]) {
			ArrayList<IListener> list = new ArrayList<>(_generatorListeners);
			for (IListener l : list) {
				l.onEvent(arg);
			}
		}
	}

	protected void newTick(int layer, QueueTick tick) {
		// for now we only monitor layer 0
		if (layer != 0) {
			return;
		}

		for (int scale = 2; scale <= _indicator.getChscalelevels(); scale++) {
			for (EquationType type : EquationType.values()) {
				RowInfo info = _model.getInfo(0, scale, type);
				if (info.include) {
					EventArg arg = new EventArg();
					arg.setDataLayer(layer);
					arg.setPolyline(type);
					arg.setScale(scale);

					// start a new cycle if there is a new pivot
					boolean startCycle = _indicator.isThereANewPivot(scale);

					// if it is the first time for this line
					// then start a new cycle with the last pivot.
					// the last pivot is taken from the restLine() method.
					Polyline polyline = _polylineMap[scale];
					if (polyline.getAnchor1() == null) {
						startCycle = true;
					}

					// new TH, we need to build a new free-indicator
					// we send a green signal
					if (startCycle) {
						out.println("New Pivot [" + scale + "] GREEN");

						if (type == EquationType.AVG) {
							arg.setEventColor(EventColor.GREEN);
							resetLine(scale);
							fireListeners(arg);
						} else {
							resetLine(scale);
						}
					} else {
						Polyline line = _polylineMap[scale];

						if (line != null && line.getAnchor1() != null) {

							// get the (time, price)

							int time;
							long price;

							if (info.updateType == RowInfo.UPDATE_LL_HH) {
								// move the indicator second anchor to the HH/LL
								int hh = _indicator.getHHTime(scale);
								int ll = _indicator.getLLTime(scale);
								if (hh > ll) {
									time = hh;
									price = _indicator.getHHPrice(scale);
								} else {
									time = ll;
									price = _indicator.getLLPrice(scale);
								}
								time = hh > ll ? hh : ll;

							} else {
								// move the indicator second anchor to the last
								// time
								time = tick.getFakeTime();
								price = tick.getPrice();
							}

							if (time != -1) {
								PriceMDBSession session = _pipe.getSymbolJob()
										.getMdbSession();

								line.setRightAnchor(session, layer, time);
								line.forceAnchor2(time, price, scale);

								IFreehandIndicator indicator = line
										.getIndicator(type);

								if (indicator == null) {
									out.println("[987987] null indicator");
									break;
								}

								double[] indCoef = indicator
										.getCenterLineCoefficients();
								if (indCoef == null) {
									out.println("[765753] null coeficients");
									return;
								}

								if (type == EquationType.AVG) {
									processPolyline_1(scale, type, arg, line);
								} else {
									processPolyline_N(scale, arg, time, indCoef);
								}

								// emit signals
								if (type == EquationType.AVG) {
									if (arg.getEventColor() != null) {
										fireListeners(arg);
									}
								} else {
									fireListeners(arg);
								}

								emitWarnings(scale, info, arg);
							}
						}
					}

					_eventMap[arg.getPolyline().ordinal()][scale] = arg
							.getEventColor();
				}
			}
		}
	}

	private void processPolyline_N(int scale, EventArg arg, int time,
			double[] indCoef) {

		// if the slope defined by comparing the last value of the central line
		// (the value corresponding to the last price = T0) with the next value
		// of the central line (T1) is NON-CONTRARIAN to the swing direction, we
		// are in the green zone. If the slope is CONTRARIAN, we are in the red
		// zone. The opposite is true for a DOWN swing.

		// Look doc Polylines Widget for Dashboard

		double T0 = PolyEvaluator.evaluate(indCoef, time - 1);
		double T1 = PolyEvaluator.evaluate(indCoef, time);

		if (_indicator.isSwingDown(scale)) {
			if (T1 < T0) {
				arg.setEventColor(EventColor.GREEN);
			} else {
				arg.setEventColor(EventColor.RED);
			}
		} else {
			if (T1 > T0) {
				arg.setEventColor(EventColor.GREEN);
			} else {
				arg.setEventColor(EventColor.RED);
			}
		}
	}

	private void processPolyline_1(int scale, EquationType type, EventArg arg,
			Polyline line) {
		// check for touches
		if (line.isTouchBottom(type) || line.isTouchTop(type)) {
			out.println("Touch [" + scale + "] RED");
			arg.setEventColor(EventColor.RED);
		}

		if (_indicator.isThereANewTentativePivot(scale)) {
			out.println("New RT Pivot [" + scale + "] GREEN");
			arg.setEventColor(EventColor.GREEN);
		}
	}

	private void emitWarnings(int scale, RowInfo info, EventArg arg) {
		if (arg.getEventColor() == EventColor.RED
				&& arg.getEventColor() != _eventMap[arg.getPolyline().ordinal()][scale]) {
			if (info.include) {
				if (info.textWarning) {
					int fscale = scale;
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							if (_warningDlg != null) {
								_warningDlg.close();
							}
							_warningDlg = new PolylineWarningDialog(Display
									.getDefault().getActiveShell());
							_warningDlg.setMessage("Touching Scale " + fscale
									+ ", Polyline 1.");
							_warningDlg.open();
						}
					});
				}

				if (info.soundWarning) {
					UIPlugin.getDefault().playSound(UIPlugin.SOUND_ALERT);
				}
			}
		} else {
			if (_warningDlg != null) {
				Display.getDefault().asyncExec(_warningDlg::close);
			}
		}
	}
}
