package com.mfg.symbols.dfs.ui;

import static java.lang.System.out;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import com.mfg.common.DFSException;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDataFeedController;
import com.mfg.dfs.conn.IQuoteHook;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;
import com.mfg.symbols.dfs.jobs.DFSJob;
import com.mfg.utils.ui.UIUtils;

public class DFSSimulatorControlView extends ViewPart implements IQuoteHook {

	public static final String ID = "com.mfg.symbols.dfs.ui.views.simulator"; //$NON-NLS-1$
	private static final long DEFAULT_INTERVAL = 1000;
	private final FormToolkit _toolkit = new FormToolkit(Display.getCurrent());
	Text _actualTimeText;
	Text _sentTimeText;
	Text _chartTimeText;
	private IDFS _dfs;
	private DFSSpeedComposite _speedComposite;
	private IDataFeedController _controller;
	private Button _constantSpeedButton;
	private Table _table;
	private Section _sectionPrices;
	List<Object[]> _pricesList;
	TableViewer _tableViewer;
	final SimpleDateFormat _dateFormat = new SimpleDateFormat(
			"MMM-dd-yyyy HH:mm:ss");
	DataSpeedModel _speedModel;

	public DFSSimulatorControlView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = _toolkit.createComposite(parent, SWT.NONE);
		_toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(1, false));
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true, 1, 1));
			_toolkit.adapt(composite);
			_toolkit.paintBordersFor(composite);
			{
				Composite composite_1 = new Composite(composite, SWT.NONE);
				GridLayout gl_composite_1 = new GridLayout(1, false);
				gl_composite_1.marginWidth = 0;
				gl_composite_1.marginHeight = 0;
				composite_1.setLayout(gl_composite_1);
				composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, false, 1, 1));
				_toolkit.adapt(composite_1);
				_toolkit.paintBordersFor(composite_1);
				{
					_speedComposite = new DFSSpeedComposite(composite_1,
							SWT.NONE);
					_speedComposite.setLayoutData(new GridData(SWT.FILL,
							SWT.CENTER, true, false, 1, 1));
					_toolkit.adapt(_speedComposite);
					_toolkit.paintBordersFor(_speedComposite);
				}
			}
			{
				_constantSpeedButton = new Button(composite, SWT.CHECK);
				_constantSpeedButton
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								updateConstantSpeedState();
							}
						});
				_toolkit.adapt(_constantSpeedButton, true, true);
				_constantSpeedButton.setText("Constant Speed");
			}
			{
				Group grpPriceInfo = new Group(composite, SWT.NONE);
				grpPriceInfo.setLayout(new GridLayout(2, false));
				grpPriceInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, false, 1, 1));
				grpPriceInfo.setText("Price Info");
				_toolkit.adapt(grpPriceInfo);
				_toolkit.paintBordersFor(grpPriceInfo);
				{
					Label lblActualTime = new Label(grpPriceInfo, SWT.NONE);
					lblActualTime.setLayoutData(new GridData(SWT.RIGHT,
							SWT.CENTER, false, false, 1, 1));
					_toolkit.adapt(lblActualTime, true, true);
					lblActualTime.setText("Actual Time");
				}
				{
					_actualTimeText = new Text(grpPriceInfo, SWT.BORDER);
					_actualTimeText.setLayoutData(new GridData(SWT.FILL,
							SWT.CENTER, true, false, 1, 1));
					_toolkit.adapt(_actualTimeText, true, true);
				}
				{
					Label lblSentTime = new Label(grpPriceInfo, SWT.NONE);
					_toolkit.adapt(lblSentTime, true, true);
					lblSentTime.setText("Sent Time");
				}
				{
					_sentTimeText = new Text(grpPriceInfo, SWT.BORDER);
					_sentTimeText.setLayoutData(new GridData(SWT.FILL,
							SWT.CENTER, true, false, 1, 1));
					_toolkit.adapt(_sentTimeText, true, true);
				}
				{
					Label lblChartTime = new Label(grpPriceInfo, SWT.NONE);
					_toolkit.adapt(lblChartTime, true, true);
					lblChartTime.setText("Chart Time");
				}
				{
					_chartTimeText = new Text(grpPriceInfo, SWT.BORDER);
					_chartTimeText.setLayoutData(new GridData(SWT.FILL,
							SWT.CENTER, true, false, 1, 1));
					_toolkit.adapt(_chartTimeText, true, true);
				}
			}
			{
				_sectionPrices = _toolkit.createSection(composite,
						ExpandableComposite.TWISTIE
								| ExpandableComposite.TITLE_BAR);
				_sectionPrices.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						false, true, 1, 1));
				_toolkit.paintBordersFor(_sectionPrices);
				_sectionPrices.setText("Prices List");
				{
					Composite composite_1 = _toolkit.createComposite(
							_sectionPrices, SWT.NONE);
					_toolkit.paintBordersFor(composite_1);
					_sectionPrices.setClient(composite_1);
					composite_1.setLayout(new GridLayout(1, false));
					{
						_tableViewer = new TableViewer(composite_1, SWT.BORDER
								| SWT.FULL_SELECTION);
						_table = _tableViewer.getTable();
						_table.setHeaderVisible(true);
						_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
								true, true, 1, 1));
						_toolkit.paintBordersFor(_table);
						{
							TableViewerColumn tableViewerColumn = new TableViewerColumn(
									_tableViewer, SWT.NONE);
							tableViewerColumn
									.setLabelProvider(new ColumnLabelProvider() {
										@Override
										public String getText(Object element) {
											Object obj = ((Object[]) element)[0];
											return super.getText(obj);
										}
									});
							TableColumn tblclmnChartTime_1 = tableViewerColumn
									.getColumn();
							tblclmnChartTime_1.setWidth(100);
							tblclmnChartTime_1.setText("Price Value");
						}
						{
							TableViewerColumn tableViewerColumn = new TableViewerColumn(
									_tableViewer, SWT.NONE);
							tableViewerColumn
									.setLabelProvider(new ColumnLabelProvider() {
										@Override
										public String getText(Object element) {
											Object obj = ((Object[]) element)[1];
											return super.getText(obj);
										}
									});
							TableColumn tblclmnActualTime = tableViewerColumn
									.getColumn();
							tblclmnActualTime.setWidth(99);
							tblclmnActualTime.setText("Actual Time");
						}
						{
							TableViewerColumn tableViewerColumn = new TableViewerColumn(
									_tableViewer, SWT.NONE);
							tableViewerColumn
									.setLabelProvider(new ColumnLabelProvider() {

										@Override
										public String getText(Object element) {
											Object obj = ((Object[]) element)[2];
											return super.getText(obj);
										}
									});
							TableColumn tblclmnSentTime = tableViewerColumn
									.getColumn();
							tblclmnSentTime.setWidth(100);
							tblclmnSentTime.setText("Sent Time");
						}
						{
							TableViewerColumn tableViewerColumn = new TableViewerColumn(
									_tableViewer, SWT.NONE);
							tableViewerColumn
									.setLabelProvider(new ColumnLabelProvider() {

										@Override
										public String getText(Object element) {
											Object obj = ((Object[]) element)[3];
											return super.getText(obj);
										}
									});
							TableColumn tblclmnChartTime = tableViewerColumn
									.getColumn();
							tblclmnChartTime.setWidth(100);
							tblclmnChartTime.setText("Chart Time");
						}
						_tableViewer
								.setContentProvider(new ArrayContentProvider());
					}
				}
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreateWidgets();
	}

	protected void updateConstantSpeedState() {
		boolean constant = _constantSpeedButton.getSelection();
		if (constant) {
			long interval = (long) (DEFAULT_INTERVAL / _speedModel.getDelay());
			out.println("Constant interval speed " + interval);
			_controller.playAtConstantInterval(interval);
		} else {
			_controller.playAtConstantInterval(0); // normal behavior
		}

	}

	private void afterCreateWidgets() {
		_sectionPrices.setExpanded(true);
		_pricesList = new ArrayList<>();
		_tableViewer.setInput(_pricesList);
		try {
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(final IDFS dfs) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							updateFromDFS(dfs);
						}
					});
				}

				@Override
				public void notReady() {
					setNotReady();
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected void setNotReady() {
		setPartName("DFS Simulator (Waiting for DFS...)");
		UIUtils.enableAll(_sectionPrices.getParent().getParent(), false);
	}

	void updateFromDFS(IDFS dfs) {
		setPartName("DFS Simulator");
		_dfs = dfs;
		_controller = _dfs.getController();
		if (_controller == null) {
			UIUtils.enableAll(_sectionPrices.getParent().getParent(), false);
		} else {
			_dfs.addGlobalQuoteHook(this);
			_speedModel = new DataSpeedModel();
			_speedModel.setState(DataSpeedControlState.PLAYING);
			_speedModel.setDelay(1);

			_speedComposite.setModel(_speedModel);
			_speedModel.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					String prop = evt.getPropertyName();
					switch (prop) {
					case DataSpeedModel.PROP_STATE:
						updateFromModelState(_speedModel.getState());
						break;
					case DataSpeedModel.PROP_DELAY:
						updateFromModelDelay();
						updateConstantSpeedState();
						break;

					default:
						break;
					}
				}
			});
			updateFromModelDelay();
		}
	}

	void updateFromModelDelay() {
		_controller.replayFactor(_speedModel.getDelay());
		out.println("Set replay factor " + _speedModel.getDelay());
	}

	void updateFromModelState(DataSpeedControlState state) {
		out.println("Set state " + state);
		switch (state) {
		case PAUSED:
			_controller.pause();
			break;
		case PLAYING:
			_controller.play();
			break;
		case STEP:
			_controller.step();
			break;
		case DISABLED:
		case FAST_FORWARDING:
		case INITIAL:
		case STOPPED:
		default:
			break;
		}
	}

	@Override
	public void dispose() {
		if (_dfs != null) {
			_dfs.removeGlobalQuoteHook(this);
		}
		_toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		_speedComposite.setFocus();
	}

	// DFS Observer methods:

	@Override
	public void onNewQuote(String symbol, final long datetime,
			final long timeStampLocal, String quote) {
		final String actualTime = _dateFormat.format(new Date(datetime));
		final String sentTime = _dateFormat.format(new Date(timeStampLocal));
		String chartTime = "?";

		Job[] jobs = Job.getJobManager().find(DFSJob.SIMULATOR_JOB_KEY);

		if (jobs.length > 0) {
			Assert.isTrue(jobs.length == 1,
					"Only one DFS simulator job is allowed");
			DFSJob job = (DFSJob) jobs[0];
			Date time = job.getLastTime();
			if (time != null) {
				chartTime = _dateFormat.format(time);
			}
			_pricesList.add(new Object[] { quote, actualTime, sentTime,
					chartTime });
		}

		final String fchartTime = chartTime;

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if (!_tableViewer.getTable().isDisposed()) {
					_tableViewer.refresh();
					if (!_pricesList.isEmpty()) {
						Object[] last = _pricesList.get(_pricesList.size() - 1);
						_tableViewer.reveal(last);
					}

					_actualTimeText.setText(actualTime);
					_sentTimeText.setText(sentTime);
					_chartTimeText.setText(fchartTime);
				}
			}
		});
	}

}
