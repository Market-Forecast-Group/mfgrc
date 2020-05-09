package com.mfg.symbols.trading.ui.dashboard;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.chart.layers.PolylineLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.jobs.InputPipe;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.dashboard.PolylineEventGenerator.EventArg;
import com.mfg.symbols.trading.ui.dashboard.PolylineWidgetModel.RowInfo;
import com.mfg.utils.PartUtils;
import com.mfg.widget.arc.gui.IndicatorParamBean;

public class PolylineAdapter extends FigureAdapter<PolylineFigure> implements
		PolylineEventGenerator.IListener {
	private PolylineWidgetModel _model;
	private TradingConfiguration _tradingConf;
	private InputConfiguration _inputConf;
	private IPartListener _partListener;

	public PolylineAdapter(DashboardCanvas canvas) {
		super(new PolylineFigure(), canvas);
		setDynamic(true);
		getFigure().setAdapter(this);
		// create 10 scales by default
		_model = new PolylineWidgetModel(10);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.addPartListener(getPartListener());

	}

	private IPartListener getPartListener() {
		if (_partListener == null) {
			_partListener = new IPartListener() {

				@Override
				public void partOpened(IWorkbenchPart part) {
					updateChartsLater(false);
				}

				@Override
				public void partDeactivated(IWorkbenchPart part) {
					//
				}

				@Override
				public void partClosed(IWorkbenchPart part) {
					//
				}

				@Override
				public void partBroughtToTop(IWorkbenchPart part) {
					//
				}

				@Override
				public void partActivated(IWorkbenchPart part) {
					//
				}
			};
		}
		return _partListener;
	}

	@Override
	public void close() {
		updateCharts(false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.removePartListener(getPartListener());
	}

	public PolylineWidgetModel getModel() {
		return _model;
	}

	public void setModel(PolylineWidgetModel model) {
		_model = model;

		if (_model != null) {
			_model.buildMap();
		}

		getFigure().build(model);

		updateCharts(false);
		configureGenerator();
	}

	void updateCharts(boolean removeLines) {
		PolylineEventGenerator generator = getGenerator();

		if (_tradingConf != null && _inputConf != null) {
			List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
			for (ChartView view : views) {
				Object viewContent = view.getContent();
				if (viewContent == _tradingConf || viewContent == _inputConf) {
					for (ScaleLayer scaleLayer : view.getChart()
							.getIndicatorLayer().getScales()) {
						PolylineLayer polylineLayer = scaleLayer
								.getPolylineLayer();
						boolean enable = false;

						if (!removeLines) {
							for (RowInfo info : _model.getRows()) {
								if (info.scale == scaleLayer.getLevel()
										&& info.include && info.show) {
									enable = true;
									break;
								}
							}
						}
						polylineLayer.setEnabled(enable);
						if (generator != null) {
							EquationType[] types = EquationType.values();
							boolean[] visibility = new boolean[types.length];
							for (EquationType type : types) {
								RowInfo info = _model.getInfo(0,
										scaleLayer.getLevel(), type);
								visibility[type.ordinal()] = info.show;
							}

							polylineLayer.setLine(generator
									.getPolyline(scaleLayer.getLevel()),
									visibility);
						}
					}
				}
			}
		}
	}

	public void setConfiguration(TradingConfiguration conf) {
		_tradingConf = conf;

		UUID inputId = conf.getInfo().getInputConfiguratioId();
		InputConfiguration input = SymbolsPlugin.getDefault()
				.getInputsStorage().findById(inputId);

		_inputConf = input;

		IndicatorParamBean params = input.getInfo().getIndicatorParams();
		int numberOfScales = params.getIndicatorNumberOfScales();
		if (_model == null) {
			_model = new PolylineWidgetModel(numberOfScales);
		} else {
			_model.adjustScales(numberOfScales);
		}
		// clean model from events
		for (RowInfo r : _model.getRows()) {
			r.status = null;
		}
		getFigure().build(_model);

		Job[] jobs = Job.getJobManager().find(conf);
		if (jobs.length > 0) {
			SymbolJob<?> job = (SymbolJob<?>) jobs[0];
			TradingPipe tradingPipe = job.getTradingPipe(conf);
			InputPipe inputPipe = tradingPipe.getInputPipe();
			Map<Object, Object> addons = inputPipe.getAddons();

			PolylineEventGenerator generator;

			if (!addons.containsKey(PolylineEventGenerator.class)) {
				try {
					generator = new PolylineEventGenerator(inputPipe);
					addons.put(PolylineEventGenerator.class, generator);
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				generator = (PolylineEventGenerator) addons
						.get(PolylineEventGenerator.class);
			}

			generator.addListener(this);

			configureGenerator();
		}

		updateChartsLater(false);
	}

	private PolylineEventGenerator getGenerator() {
		Job[] jobs = Job.getJobManager().find(_tradingConf);
		if (jobs.length > 0) {
			SymbolJob<?> job = (SymbolJob<?>) jobs[0];
			TradingPipe tradingPipe = job.getTradingPipe(_tradingConf);
			InputPipe inputPipe = tradingPipe.getInputPipe();
			Map<Object, Object> addons = inputPipe.getAddons();

			if (addons.containsKey(PolylineEventGenerator.class)) {
				PolylineEventGenerator generator = (PolylineEventGenerator) addons
						.get(PolylineEventGenerator.class);

				return generator;
			}
		}
		return null;
	}

	public void configureGenerator() {
		PolylineEventGenerator generator = getGenerator();
		if (generator != null) {
			generator.configure(getModel());
		}
	}

	void updateChartsLater(boolean removeLines) {
		// wait a second before to connect with the chart,
		// the chart should be create the model first.
		new Thread(new Runnable() {

			@Override
			public void run() {
				//
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//
				}
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						updateCharts(removeLines);
					}
				});
			}
		}).start();
	}

	@Override
	public void toJSON(JSONStringer s) throws JSONException {
		super.toJSON(s);
		s.key("name");
		s.value("polyline");

		s.key("model");
		s.object();
		_model.toJSON(s);
		s.endObject();
	}

	@Override
	public void updateFromJSON(JSONObject obj) throws JSONException {
		super.updateFromJSON(obj);
		JSONObject obj2 = obj.getJSONObject("model");
		if (obj2 == null) {
			_model = null;
		} else {
			_model = new PolylineWidgetModel();
			_model.updateFromJSON(obj2);
		}
		getFigure().build(_model);
	}

	@Override
	public void onEvent(EventArg arg) {
		if (_model.updateFromEvent(arg)) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					// TODO: not working I don't know why!!!!!! fuck!!!!
					//
					// getFigure().updateFromEvent(arg);
					//
					// so I do a complete build
					getFigure().build(getModel());
				}
			});
		}
	}

}
