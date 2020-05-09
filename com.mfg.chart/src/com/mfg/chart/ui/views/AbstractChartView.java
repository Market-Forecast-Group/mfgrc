/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.chart.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.PriceChartCanvas_OpenGL;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.ISyntheticModel;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.actions.RedoChartAction;
import com.mfg.chart.ui.actions.UndoChartAction;

@SuppressWarnings("unused")
// Some variables just used to work with profile actions
public abstract class AbstractChartView extends ViewPart implements IChartView {

	private static final String PREF_AUTORANGE = "autorange";
	private static final String PREF_SHOW_MENU = "showMenu";
	private static final String PREF_PRICE_RANGE_UPPER = "priceRange.upper";
	private static final String PREF_PRICE_RANGE_LOWER = "priceRange.lower";
	private static final String PREF_TIME_RANGE_UPPER = "timeRange.upper";
	private static final String PREF_TIME_RANGE_LOWER = "timeRange.lower";
	private static final String PREF_PROFILE_NAME = "profileName";
	private static final String PREF_PROFILE_XML = "profileXML";

	public static String CONTEXT_LAYER_MENU_ID = "com.mfg.chart.contexts.chartView.layerMenu";

	Chart _chart;
	PriceChartCanvas_OpenGL chartComposite;
	private IPropertyChangeListener prefsListener;
	private Action zoomOutAction;
	private PlotRange initTimeRange;
	private PlotRange initPriceRange;
	private Boolean initAutorange;

	protected abstract ChartType getChartType();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
	 */
	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
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

	@Override
	public void createPartControl(final Composite parent) {
		// activate context for key bindings
		/*
		 * final IContextService service = (IContextService)
		 * getSite().getService( IContextService.class);
		 */

		// chart
		_chart = new Chart(getTitle(), IChartModel.EMPTY, ChartType.EMPTY, null);

		final GLData data = new GLData();
		data.doubleBuffer = true;
		chartComposite = new PriceChartCanvas_OpenGL(parent, data, _chart);

		final IPreferenceStore preferenceStore = ChartPlugin.getDefault()
				.getPreferenceStore();
		String defaultProfileName = preferenceStore
				.getString(ChartPlugin.PREFERENCES_PROFILES_DEFAULT);
		final Profile defaultProfile = ChartPlugin.getDefault().getProfiles()
				.findProfile(defaultProfileName);
		// help

		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(chartComposite, "com.mfg.help.chartContext");

		final IActionBars bars = getViewSite().getActionBars();
		fillActionBars(bars);

		// listeners

		prefsListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				preferenceStoreChanged(preferenceStore);

			}
		};
		preferenceStore.addPropertyChangeListener(prefsListener);
		preferenceStoreChanged(preferenceStore);

	}

	/**
	 * @param bars
	 */
	protected void fillActionBars(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				new RedoChartAction());
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				new UndoChartAction());

		fillToolBar(bars.getToolBarManager());
		fillMenuBar(bars.getMenuManager());

		updateActionBars(bars);
	}

	/**
	 * @deprecated This should be removed in future, all bar contributions
	 *             should be done with extensions.
	 * @param bars
	 */
	@SuppressWarnings("static-method")
	// Overloaded on inner classes.
	@Deprecated
	protected void updateActionBars(IActionBars bars) {
		bars.updateActionBars();
	}

	protected boolean restoreInitialChartValues() {
		boolean restore = initTimeRange != null || initPriceRange != null
				|| initAutorange != null;
		if (initTimeRange != null) {
			_chart.setXRange(initTimeRange);
		}

		if (initPriceRange != null) {
			_chart.setYRange(initPriceRange);
		}

		if (initAutorange != null) {
			_chart.setAutoRangeEnabled(initAutorange.booleanValue());
		}

		_chart.fireRangeChanged();

		initTimeRange = null;
		initPriceRange = null;
		initAutorange = null;

		return restore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);

		if (memento != null) {
			PlotRange xRange = _chart.getXRange();
			PlotRange yRange = _chart.getYRange();

			memento.putFloat(PREF_TIME_RANGE_LOWER, (float) xRange.lower);
			memento.putFloat(PREF_TIME_RANGE_UPPER, (float) xRange.upper);
			memento.putFloat(PREF_PRICE_RANGE_LOWER, (float) yRange.lower);
			memento.putFloat(PREF_PRICE_RANGE_UPPER, (float) yRange.upper);

			memento.putBoolean(PREF_AUTORANGE, _chart.isAutoRangeEnabled());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException {
		super.init(site, memento);
		if (memento != null) {
			Float tlower = memento.getFloat(PREF_TIME_RANGE_LOWER);
			Float tupper = memento.getFloat(PREF_TIME_RANGE_UPPER);
			Float plower = memento.getFloat(PREF_PRICE_RANGE_LOWER);
			Float pupper = memento.getFloat(PREF_PRICE_RANGE_UPPER);

			if (tlower != null && tupper != null && plower != null
					&& pupper != null) {
				initTimeRange = new PlotRange(tlower.doubleValue(),
						tupper.doubleValue());
				initPriceRange = new PlotRange(plower.doubleValue(),
						pupper.doubleValue());
			}

			initAutorange = memento.getBoolean(PREF_AUTORANGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		getChart().dispose();
		ChartPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(prefsListener);
	}

	/**
	 * @return the chartComposite
	 */
	public PriceChartCanvas_OpenGL getChartComposite() {
		return chartComposite;
	}

	/**
	 * @return the zoomOutAction
	 */
	public Action getZoomOutAction() {
		return zoomOutAction;
	}

	/**
	 * @param manager
	 * @param menuManager
	 */
	protected void fillMenuBar(final IMenuManager manager) {
		// manager.add(zoomOutAction);
		// manager.add(selectMaxVisibleScalesAction);
		// manager.add(selectMaxVisibleBandsAction);
		// manager.add(browseDataAction);
		// manager.add(new Separator());
		// manager.add(newProfileAction);
		// manager.add(selectProfileAction);
		// manager.add(setProfileAsDefaultAction);
		// manager.add(deleteProfileAction);
		// manager.add(saveProfileAction);

		// CommandContributionItemParameter params = new
		// CommandContributionItemParameter(
		// getViewSite(), "redoChartRange",
		// ActionFactory.REDO.getCommandId(),
		// CommandContributionItem.STYLE_PUSH);
		// params.label = "Chart Redo Range";
		// CommandContributionItem item = new CommandContributionItem(params);
		// manager.add(item);
	}

	protected void fillToolBar(final IToolBarManager manager) {
		// Documenting empty method to avoid warning.
	}

	/**
	 * @return the chart
	 */
	@Override
	public Chart getChart() {
		return _chart;
	}
	
	/**
	 * @param chart
	 *            the chart to set
	 */
	@Override
	public void setChart(final Chart chart) {
		if (this._chart != null) {
			if (chart != this._chart) {
				if (!chart.isDisposed()) {
					this._chart.dispose();
				}
			}

		}

		this._chart = chart;
		chart.setView(this);
		if (!chartComposite.isDisposed()) {
			chartComposite.getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					if (!chartComposite.isDisposed()) {
						chartComposite.setChart(chart);
						chartComposite.redraw();
					}
				}
			});
		}
	}

	@Override
	public void setFocus() {
		chartComposite.setFocus();
	}

	public void zoomIn() {
		Chart chart = getChart();
		if (chart.getType() == ChartType.SYNTHETIC) {
			// XXX: Giulio does not like this
			// ISyntheticModel model = chart.getModel().getSyntheticModel();
			// int n = model.getZZSwings();
			// if (n > 1) {
			// model.setZZSwings(n - 1);
			// }
		} else {
			chart.zoom(1);
		}
	}

	public void zoomOut() {
		Chart chart = getChart();
		if (chart.getType() == ChartType.SYNTHETIC) {
			// XXX: Giulio does not like this
			// ISyntheticModel model = chart.getModel().getSyntheticModel();
			// int n = model.getZZSwings();
			// model.setZZSwings(n + 1);
		} else {
			chart.zoom(-1);
		}
	}

	/**
	 * @param store
	 * 
	 */
	public void preferenceStoreChanged(IPreferenceStore store) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				getChart().update(getChart().isAutoRangeEnabled());
			}
		});
	}
}
