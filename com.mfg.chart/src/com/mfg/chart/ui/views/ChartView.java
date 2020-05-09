/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.chart.ui.views;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.IPriceModel.LayerProjection;
import com.mfg.chart.ui.ChartType;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.utils.ImageUtils;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class ChartView extends AbstractChartView {
	public static final String VIEW_ID = "com.mfg.chart.ui.views.ChartView";

	public static List<ChartView> findByContent(Object content) {
		List<ChartView> list = new ArrayList<>();
		List<ChartView> list2 = PartUtils.getOpenViews(ChartView.VIEW_ID);
		for (ChartView view : list2) {
			if (view.getContent() == content) {
				list.add(view);
			}
		}
		return list;

	}

	IChartContentAdapter _adapter;
	private Object _content;

	private Object _initialObj;
	private boolean _closeInitObjectIsNotAvailable;
	@Deprecated
	/**
	 * @deprecated I should implement this in the same way I implement the equity. An adapter is always initialized with the same old adapter and it also contributes to the view memento.
	 */
	private boolean _usePhysicalTimes;
	private Thread _callerThread;
	private IMemento _initState;
	private final static String MEMENTO_OBJECT_KEY = "com.mfg.chart.chartView.objKey";
	private final static String MEMENTO_STORAGE_KEY = "com.mfg.chart.chartView.storageKey";
	private static final String MEMENTO_USE_PHYSICAL_TIMES_KEY = "com.mfg.chart.chartView.usePhysicalTimes";
	private static final String MEMENTO_CHART_BUNDLE_PATH = "com.mfg.chart.chartView.bundlepath";

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		_usePhysicalTimes = false;
		_closeInitObjectIsNotAvailable = false;
		if (memento != null) {
			String uuid = memento.getString(MEMENTO_OBJECT_KEY);
			String storageId = memento.getString(MEMENTO_STORAGE_KEY);
			if (uuid != null && storageId != null) {
				IWorkspaceStorage storage = PersistInterfacesPlugin
						.getDefault().getStorage(storageId);
				if (storage != null && storage instanceof SimpleStorage) {
					_initialObj = ((SimpleStorage<?>) storage).findById(UUID
							.fromString(uuid));
					_closeInitObjectIsNotAvailable = _initialObj == null;
					IAlternativeChartContent altContent = (IAlternativeChartContent) Platform
							.getAdapterManager().getAdapter(_initialObj,
									IAlternativeChartContent.class);
					if (altContent != null) {
						_initialObj = altContent.getAlternativeContent(memento);
					}
				}
			} else {
				String path = memento.getString(MEMENTO_CHART_BUNDLE_PATH);
				if (path != null) {
					File f = new File(path);
					if (f.exists()) {
						_initialObj = f;
					}
				}
			}
			{
				String str = memento.getString(MEMENTO_USE_PHYSICAL_TIMES_KEY);
				if (str != null) {
					_usePhysicalTimes = Boolean.parseBoolean(str);
				}
			}

			_initState = memento;
		}
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		if (_content != null) {
			if (_content instanceof IStorageObject) {
				IStorageObject storageObj = (IStorageObject) _content;
				saveState(memento, storageObj);
			} else if (_content instanceof File) {
				memento.putString(MEMENTO_OBJECT_KEY, null);
				memento.putString(MEMENTO_STORAGE_KEY, null);
				memento.putString(MEMENTO_CHART_BUNDLE_PATH,
						((File) _content).getAbsolutePath());
			} else {
				IAlternativeChartContent altContent = (IAlternativeChartContent) Platform
						.getAdapterManager().getAdapter(_content,
								IAlternativeChartContent.class);
				if (altContent != null) {
					Object content2 = altContent
							.getAlternativeContent(_content);
					if (content2 instanceof IStorageObject) {
						saveState(memento, (IStorageObject) content2);
					}
				}
			}
		}
		memento.putString(MEMENTO_USE_PHYSICAL_TIMES_KEY,
				Boolean.toString(_usePhysicalTimes));
		if (_adapter != null) {
			_adapter.saveState(memento);
		}
	}

	private static void saveState(IMemento memento, IStorageObject storageObj) {
		memento.putString(MEMENTO_OBJECT_KEY, storageObj.getUUID().toString());
		memento.putString(MEMENTO_STORAGE_KEY, storageObj.getStorage()
				.getStorageId());
		memento.putString(MEMENTO_CHART_BUNDLE_PATH, null);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		if (_closeInitObjectIsNotAvailable) {
			// the object attached to this chart does not exist anymore
			// we should close the chart
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							getViewSite().getPage().hideView(ChartView.this);
						}
					});
				}
			}.start();

		} else {
			setContent(_initialObj);
		}
	}

	@Override
	public void setContent(Object content) {
		setContent(content, null);
	}

	public void setContent(Object content, final ChartConfig chartConfig) {
		Object newContent = content;
		if (_content instanceof IAlternativeChartContent) {
			// try to keep the alternative content
			Object alt = ((IAlternativeChartContent) _content)
					.getAlternativeContent(newContent);
			if (alt == newContent) {
				newContent = _content;
			}
		}

		Thread currentThread = Thread.currentThread();
		if (_callerThread != null && _callerThread != currentThread) {
			throw new ConcurrentModificationException(
					"The chart content is set by many threads. First "
							+ _callerThread.getName() + " and now "
							+ currentThread.getName());
		}
		_callerThread = currentThread;

		final Chart masterChart = _chart.getMasterChart();
		if (masterChart != null) {
			masterChart.removeMirror(_chart);
		}

		if (newContent == null) {
			_content = null;
			if (_adapter != null) {
				_adapter.dispose(this);
			}
			_adapter = null;

			setChart(new Chart("Empty", IChartModel.EMPTY, ChartType.EMPTY,
					newContent));
			getSite().getShell().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					initViewAsEmpty();
				}
			});
		} else {
			IChartContentAdapter newAdapter = (IChartContentAdapter) Platform
					.getAdapterManager().getAdapter(newContent,
							IChartContentAdapter.class);
			if (_initState != null) {
				newAdapter.initState(_initState);
				_initState = null;
			}
			if (newAdapter != null) {
				if (_adapter != null) {
					_adapter.dispose(this);
				}
				IChartContentAdapter oldAdapter = _adapter;
				_content = newContent;
				_adapter = newAdapter;
				_adapter.init(_usePhysicalTimes, oldAdapter);
				setChart(_adapter.getChart());
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						_adapter.configure(ChartView.this, chartConfig);
						finishConfigutration(chartConfig, masterChart);
					}
				});
				return;
			}
		}
		finishConfigutration(chartConfig, masterChart);
	}

	void finishConfigutration(final ChartConfig chartConfig,
			final Chart masterChart) {
		List<Chart> mirrors = _chart.getMirrors();

		for (Chart mirror : mirrors) {
			_chart.addMirror(mirror);
		}

		getSite().getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (masterChart != null && _adapter != null) {
					masterChart.addMirror(_chart);
					masterChart.update(masterChart.isAutoRangeEnabled());
				} else {
					if (chartConfig == null) {
						getChart().zoomOutAll(true);
					} else {
						getChart().configure(chartConfig);
					}
				}
			}
		});
		_callerThread = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.AbstractChartView#dispose()
	 */
	@Override
	public void dispose() {
		if (_adapter != null) {
			_adapter.dispose(this);
		}
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#setTitleImage(org.eclipse.swt.graphics
	 * .Image)
	 */
	@Override
	public void setTitleImage(Image titleImage) {
		super.setTitleImage(titleImage);
	}

	/**
	 * @return the usePhysicalTimes
	 */
	public boolean isUsePhysicalTimes() {
		return _usePhysicalTimes;
	}

	/**
	 * @param usePhysicalTimes
	 *            the usePhysicalTimes to set
	 */
	public void setUsePhysicalTimes(boolean usePhysicalTimes) {
		this._usePhysicalTimes = usePhysicalTimes;
	}

	/**
	 * @return the content
	 */
	@Override
	public Object getContent() {
		return _content;
	}

	/**
	 * @return the chartContent
	 */
	@Override
	public IChartContentAdapter getContentAdapter() {
		return _adapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.AbstractChartView#getChartType()
	 */
	@Override
	protected ChartType getChartType() {
		return _adapter == null ? ChartType.EMPTY : _adapter.getChart()
				.getType();
	}

	void initViewAsEmpty() {
		setPartName("Chart");
		setTitleImage(ImageUtils.getBundledImage(ChartPlugin.getDefault(),
				ChartPlugin.CHART_ICON_PATH));
	}

	@Deprecated
	@Override
	protected void updateActionBars(IActionBars bars) {
		// this class was migrated to get all contributions by extension.
	}

	public void swapFakePhysicalTimes() {
		int dataLayer = _chart.getDataLayer();
		setUsePhysicalTimes(!isUsePhysicalTimes());
		final Object content = getContent();
		if (content != null) {
			LayerProjection proj = _chart
					.getModel()
					.getPriceModel()
					.getLayerProjection(_chart.getDataLayer(),
							_chart.getLowerRangeTime(),
							_chart.getUpperRangeTime(), _chart.getDataLayer());

			IPriceModel priceModel = _chart.getModel().getPriceModel();
			long lower;
			long upper;
			if (isUsePhysicalTimes()) {
				long lowerTime = priceModel
						.getLowerPhysicalTime_from_DisplayTime(dataLayer, 0);
				lower = proj.getLowerDate() - lowerTime;
				upper = proj.getUpperDate() - lowerTime;
			} else {
				long lowerTime = priceModel.getLowerDisplayTime(dataLayer);
				lower = priceModel.getFakeTime_from_PhysicalTime(
						_chart.getDataLayer(), proj.getLowerDate() + lowerTime);
				upper = priceModel.getFakeTime_from_PhysicalTime(
						_chart.getDataLayer(), proj.getUpperDate() + lowerTime);
			}
			double percent = ChartPlugin
					.getDefault()
					.getPreferenceStore()
					.getDouble(
							ChartPlugin.PREFERENCES_ZOOM_OUT_ALL_BLANK_PERCENT);
			if (proj.isLowestTime()) {
				lower -= (upper - lower) / percent;
			}
			if (proj.isHighestTime()) {
				upper += (upper - lower) / percent;
			}

			final PlotRange range = new PlotRange(lower, upper);

			final ChartConfig config = new ChartConfig(range,
					_chart.getDataLayer(), _chart.getScrollingMode(),
					_chart.isAutoDataLayer(), _chart.getType());

			Shell shell = getViewSite().getShell();
			shell.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					setContent(content, config);
				}
			});
		}
	}

	public void shuttingDown() {
		if (_adapter != null) {
			_adapter.shuttingDown();
		}
	}
}
