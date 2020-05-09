package com.mfg.symbols.inputs.ui.adapters;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.ISyntheticModel;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.views.ChartConfig;
import com.mfg.chart.ui.views.IChartView;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.ui.chart.SynthInfoDialog;
import com.mfg.utils.ImageUtils;

public class SyntheticChartAdapter extends InputChartAdapter {

	public static final String MEMENTO_SYNTH_CHART = "com.mfg.syntheticChart";

	public SyntheticChartAdapter(InputConfiguration conf) {
		super(conf, ChartType.SYNTHETIC);
	}

	@Override
	protected void fillToolbar(IToolBarManager manager) {
		super.fillToolbar(manager);

		final ISyntheticModel synthModel = getChart().getModel()
				.getSyntheticModel();
		int scaleCount = synthModel.getScaleCount();
		if (scaleCount > 0) {
			manager.add(new Action("", ImageUtils.getBundledImageDescriptor(
					ChartPlugin.PLUGIN_ID, "icons/fake-icon.gif")) {
				@Override
				public void run() {
					//
				}
			});
			int zzScales = synthModel.getHigherZZScale();
			manager.add(createSpinnerItem("com.mfg.chart.synthScales",
					"Scales (" + scaleCount + ")", zzScales, 1, scaleCount,
					140, new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							Spinner spinner = (Spinner) e.getSource();
							int sel = spinner.getSelection();
							synthModel.setHigherZZScale(sel);
						}
					}));

			int zzSwings = synthModel.getZZSwings();
			manager.add(createSpinnerItem("com.mfg.chart.synthSwings",
					"HHS Swings", zzSwings, 1, 100, 140,
					new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							Spinner spinner = (Spinner) e.getSource();
							int sel = spinner.getSelection();
							synthModel.setZZSwings(sel);
						}
					}));
			manager.add(new Action("m", IAction.AS_CHECK_BOX) {
				{
					setChecked(true);
				}
				@Override
				public void run() {
					synthModel.setMatchPivots(!synthModel.isMatchPivots());
					getChart().update();
				}
			});
		}
	}

	@Override
	public void fillMenuBar(IMenuManager menu) {
		super.fillMenuBar(menu);
		boolean empty = _chart.getModel() == IChartModel.EMPTY;
		if (!empty) {
			menu.add(new Separator());
			menu.add(new Action("Input Chart") {
				@Override
				public void run() {
					final IChartView view = getChartView();
					view.setContent(null);
					view.setContent(getConfiguration());
				}
			});
			menu.add(new Action("Info") {
				@Override
				public void runWithEvent(Event event) {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					SynthInfoDialog dlg = new SynthInfoDialog(shell);
					dlg.setChart(getChart());
					dlg.open();
				}
			});
		}
	}

	private static ControlContribution createSpinnerItem(String id,
			final String text, final int value, final int min, final int max,
			final int width, final SelectionListener listener) {
		return new ControlContribution(id) {

			@Override
			protected Control createControl(Composite parent) {
				Composite comp = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout(2, false);
				layout.marginBottom = 0;
				layout.marginLeft = 0;
				layout.marginRight = 5;
				layout.marginTop = 0;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				comp.setLayout(layout);

				Label label = new Label(comp, SWT.NONE);
				label.setText(text);
				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
						false, 1, 1));

				Spinner spinner = new Spinner(comp, SWT.BORDER);
				spinner.setMinimum(min);
				spinner.setMaximum(max);
				spinner.setSelection(value);
				spinner.addSelectionListener(listener);
				spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
						false, 1, 1));
				return comp;
			}

			@Override
			protected int computeWidth(Control control) {
				return width;
			}
		};
	}

	@Override
	public void configure(IChartView chartView, ChartConfig chartConfig) {
		super.configure(chartView, chartConfig);
		chartView.setPartName(chartView.getPartName() + " (Synthetic)");
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(MEMENTO_SYNTH_CHART, true);
	}
}
