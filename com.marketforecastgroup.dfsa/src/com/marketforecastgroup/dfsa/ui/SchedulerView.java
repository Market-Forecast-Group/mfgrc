package com.marketforecastgroup.dfsa.ui;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.common.DFSException;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.DfsSchedulingTimes;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSObserver;
import com.mfg.utils.ui.DoubleClickEditingSupport;
import com.mfg.utils.ui.UIUtils;

public class SchedulerView extends ViewPart implements IDFSObserver {
	public static final String ID = "com.marketforecastgroup.dfsa.ui.scheduler"; //$NON-NLS-1$
	Action _addAction;
	Table _table;
	TableViewer _tableViewer;
	private List<Time> _times;
	private Action _removeAction;
	IDFS _dfs;
	Action _manualScheduler;
	Composite _parent;

	public SchedulerView() {
	}

	public static class Time implements Comparable<Time> {
		private int _ss;
		private int _mm;
		private int _hh;

		public Time(int ss, int mm, int hh) {
			super();
			_ss = ss;
			_mm = mm;
			_hh = hh;
		}

		public int getSs() {
			return _ss;
		}

		public void setSs(int ss) {
			_ss = ss % 60;
			setMm(_mm + ss / 60);
		}

		public int getMm() {
			return _mm;
		}

		public void setMm(int mm) {
			_mm = mm % 60;
			setHh(_hh + mm / 60);
		}

		public int getHh() {
			return _hh;
		}

		public void setHh(int hh) {
			_hh = hh % 24;
		}

		@Override
		public int compareTo(Time o) {
			long v1 = TimeUnit.HOURS.toSeconds(_hh)
					+ TimeUnit.MINUTES.toSeconds(_mm) + _ss;
			long v2 = TimeUnit.HOURS.toSeconds(o._hh)
					+ TimeUnit.MINUTES.toSeconds(o._mm) + o._ss;
			return Long.compare(v1, v2);
		}

		public static Time parse(String s) {
			String[] split = s.split(":");
			return new Time(Integer.parseInt(split[2]),
					Integer.parseInt(split[1]), Integer.parseInt(split[0]));
		}

		@Override
		public String toString() {
			return _hh + ":" + _mm + ":" + _ss;
		}
	}

	public static class TimeValidator implements ICellEditorValidator {
		private final int _from;
		private final int _to;

		public TimeValidator(int from, int to) {
			super();
			_from = from;
			_to = to;
		}

		@Override
		public String isValid(Object value) {
			try {
				int i = Integer.parseInt((String) value);
				return i < _from || i > _to ? "Invalid range." : null;
			} catch (Exception e) {
				return e.getClass().getSimpleName() + ":" + e.getMessage();
			}
		}

	}

	public class SpinnerCellEditor extends CellEditor {

		Spinner _spinner;

		public SpinnerCellEditor(Composite parent, final int col) {
			super(parent);
			_spinner.setMinimum(0);
			_spinner.setMaximum(Integer.MAX_VALUE);
			_spinner.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					StructuredSelection sel = (StructuredSelection) _tableViewer
							.getSelection();
					int selValue = ((Spinner) e.widget).getSelection();
					int newValue = handleSpinnerSelection(
							sel.getFirstElement(), selValue, col);
					if (newValue != _spinner.getSelection()) {
						_spinner.setSelection(newValue);
					}
				}
			});
			_spinner.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.keyCode == SWT.KEYPAD_CR || e.keyCode == 13) {
						_table.forceFocus();
					}
				}
			});
		}

		@Override
		protected Control createControl(Composite parent) {
			_spinner = new Spinner(parent, SWT.WRAP);
			return _spinner;
		}

		@Override
		protected Object doGetValue() {
			return Integer.valueOf(_spinner.getSelection());
		}

		@Override
		protected void doSetFocus() {
			_spinner.setFocus();
		}

		@Override
		protected void doSetValue(Object value) {
			_spinner.setSelection(((Integer) value).intValue());
		}

	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			_tableViewer = new TableViewer(container, SWT.BORDER
					| SWT.FULL_SELECTION);
			_tableViewer
					.addSelectionChangedListener(new ISelectionChangedListener() {
						@Override
						public void selectionChanged(SelectionChangedEvent event) {
							validateActions();
						}
					});
			_table = _tableViewer.getTable();
			_table.setHeaderVisible(true);
			_table.setLinesVisible(true);
			_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
					1, 1));
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn
						.setEditingSupport(new DoubleClickEditingSupport(
								_tableViewer) {

							@Override
							protected CellEditor getCellEditor(
									final Object element) {
								return new SpinnerCellEditor(_table, 0);
							}

							@Override
							protected Object getValue(Object element) {
								return Integer.valueOf(((Time) element).getHh());
							}

							@Override
							protected void setValue(Object element, Object value) {
								((Time) element).setHh(((Integer) value)
										.intValue());
								try {
									refreshTable();
								} catch (DFSException e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						});
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return format(((Time) element).getHh());
					}
				});
				TableColumn tblclmnHours = tableViewerColumn.getColumn();
				tblclmnHours.setWidth(74);
				tblclmnHours.setText("HH");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn
						.setEditingSupport(new DoubleClickEditingSupport(
								_tableViewer) {

							@Override
							protected CellEditor getCellEditor(
									final Object element) {
								return new SpinnerCellEditor(_table, 1);
							}

							@Override
							protected Object getValue(Object element) {
								return Integer.valueOf(((Time) element).getMm());
							}

							@Override
							protected void setValue(Object element, Object value) {
								((Time) element).setMm(((Integer) value)
										.intValue());
								try {
									refreshTable();
								} catch (DFSException e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						});
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return format(((Time) element).getMm());
					}
				});
				TableColumn tblclmnMinutes = tableViewerColumn.getColumn();
				tblclmnMinutes.setWidth(90);
				tblclmnMinutes.setText("MM");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn
						.setEditingSupport(new DoubleClickEditingSupport(
								_tableViewer) {

							@Override
							protected CellEditor getCellEditor(
									final Object element) {
								return new SpinnerCellEditor(_table, 2);
							}

							@Override
							protected Object getValue(Object element) {
								return Integer.valueOf(((Time) element).getSs());
							}

							@Override
							protected void setValue(Object element, Object value) {
								((Time) element).setSs(((Integer) value)
										.intValue());
								try {
									refreshTable();
								} catch (DFSException e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						});
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return format(((Time) element).getSs());
					}
				});
				TableColumn tblclmnSeconds = tableViewerColumn.getColumn();
				tblclmnSeconds.setWidth(86);
				tblclmnSeconds.setText("SS");
			}
			_tableViewer.setContentProvider(new ArrayContentProvider());
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreateWidgets();
	}

	int handleSpinnerSelection(final Object element, int value, int col) {
		setItemValue(element, col, value);
		int i = _times.indexOf(element);
		TableItem item = _table.getItem(i);
		for (int j = 0; j < 3; j++) {
			if (j != col) {
				item.setText(j, format(getItemValue(element, j)));
			}
		}
		return getItemValue(element, col);
	}

	private static void setItemValue(final Object element, int col, int v) {
		Time t = (Time) element;
		switch (col) {
		case 0:
			t.setHh(v);
			break;
		case 1:
			t.setMm(v);
			break;
		case 2:
			t.setSs(v);
		}
	}

	private static int getItemValue(Object obj, int col) {
		Time t = (Time) obj;
		switch (col) {
		case 0:
			return t.getHh();
		case 1:
			return t.getMm();
		case 2:
			return t.getSs();
		}
		return 0;
	}

	protected void refreshTable() throws DFSException {
		// sort times
		Collections.sort(_times);

		// refresh scheduler model

		ArrayList<String> list = new ArrayList<>();
		HashSet<Time> repeatedTimes = new HashSet<>();
		Time lastTime = null;
		for (Time t : _times) {
			if (lastTime == null || lastTime.compareTo(t) != 0) {
				list.add(t.toString());
			} else {
				repeatedTimes.add(t);
			}
			lastTime = t;
		}
		DfsSchedulingTimes times = new DfsSchedulingTimes();
		times.schedulings = list;
		out.println("Setting scheduling times: ");
		out.println(times);

		_dfs.setSchedulingTimes(times);
		// out.println("Write:\n" + Arrays.toString(list.toArray()));

		// refresh GUI

		_tableViewer.refresh();

		Color colorNormal = getViewSite().getShell().getDisplay()
				.getSystemColor(SWT.COLOR_BLACK);
		Color colorError = getViewSite().getShell().getDisplay()
				.getSystemColor(SWT.COLOR_RED);
		Color color;
		for (TableItem item : _table.getItems()) {
			boolean repeated = repeatedTimes.contains(item.getData());
			color = repeated ? colorError : colorNormal;
			item.setForeground(color);
		}

		StructuredSelection sel = (StructuredSelection) _tableViewer
				.getSelection();
		Object obj = sel.getFirstElement();
		if (obj != null) {
			_tableViewer.reveal(obj);
		}
		validateActions();
	}

	protected static String format(int n) {
		return n < 10 ? "0" + n : Integer.toString(n);
	}

	@Override
	public void dispose() {
		if (_dfs != null) {
			_dfs.removeObserver(this);
		}
		super.dispose();
	}

	private void afterCreateWidgets() {
		try {
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(IDFS dfs) {
					_dfs = dfs;
					_dfs.addObserver(SchedulerView.this);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							setReady();
						}
					});

				}

				@Override
				public void notReady() {
					setWaiting();
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
		}

	}

	ArrayList<Time> readTimes() {
		ArrayList<Time> list = new ArrayList<>();
		DfsSchedulingTimes times = _dfs.getSchedulingTimes();
		for (String s : times.schedulings) {
			Time t = Time.parse(s);
			list.add(t);
		}
		// out.println("Read:\n" + Arrays.toString(list.toArray()));
		return list;
	}

	public List<Time> getTimes() {
		return _times;
	}

	public void setTimes(List<Time> times) {
		_times = times;
		_tableViewer.setInput(_times);
		validateActions();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			_addAction = new Action("Add") {
				@Override
				public void run() {
					try {
						addTime();
					} catch (DFSException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			};
			_addAction.setToolTipText("Add new time 00:00:00");
			_addAction.setDescription("Add Time");
			_addAction.setImageDescriptor(ResourceManager
					.getPluginImageDescriptor("org.eclipse.ui",
							"/icons/full/obj16/add_obj.gif"));
		}
		{
			_removeAction = new Action("Delete") {
				@Override
				public void run() {
					try {
						removeTime();
					} catch (DFSException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			};
			_removeAction.setToolTipText("Delete selected Time");
			_removeAction.setDescription("Delete Time");
			_removeAction.setImageDescriptor(ResourceManager
					.getPluginImageDescriptor("org.eclipse.ui",
							"/icons/full/obj16/delete_obj.gif"));
		}
		{
			_manualScheduler = new Action("") {
				@Override
				public void run() {
					manualScheduler();
				}
			};
			_manualScheduler.setToolTipText("Manual Scheduler");
			_manualScheduler.setImageDescriptor(ResourceManager
					.getPluginImageDescriptor("com.marketforecastgroup.dfsa",
							"icons/play.gif"));
		}
	}

	protected void manualScheduler() {
		_manualScheduler.setEnabled(false);
		_dfs.manualScheduling();
	}

	void validateActions() {
		_removeAction.setEnabled(!_tableViewer.getSelection().isEmpty());
	}

	protected void removeTime() throws DFSException {
		StructuredSelection sel = (StructuredSelection) _tableViewer
				.getSelection();
		Object obj = sel.getFirstElement();
		if (obj != null) {
			int i = _times.indexOf(obj);
			_times.remove(obj);
			if (i == _times.size()) {
				i--;
			}
			if (!_times.isEmpty()) {
				obj = _times.get(i);
				_tableViewer.setSelection(new StructuredSelection(obj));
			}
			refreshTable();
		}
	}

	protected void addTime() throws DFSException {
		Time t = new Time(0, 0, 0);
		_times.add(t);
		refreshTable();
		_tableViewer.reveal(t);
		_tableViewer.editElement(t, 0);
		_tableViewer.editElement(t, 0);
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(_addAction);
		toolbarManager.add(_removeAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(_manualScheduler);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
		menuManager.add(new Separator());
	}

	@Override
	public void setFocus() {
		_table.setFocus();
	}

	@Override
	public void onSymbolInitializationEnded(String symbol) {
		//
	}

	@Override
	public void onSchedulerStartRunning() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				_manualScheduler.setEnabled(false);
			}
		});
	}

	@Override
	public void onSchedulerEndedCycle() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				_manualScheduler.setEnabled(true);
			}
		});
	}

	void setReady() {
		_addAction.setEnabled(true);
		_removeAction.setEnabled(true);
		_manualScheduler.setEnabled(true);
		setPartName("Scheduler");
		UIUtils.enableAll(_parent, true);
		if (_dfs.isSchedulerRunning()) {
			_manualScheduler.setEnabled(false);
		}
		setTimes(readTimes());
	}

	void setWaiting() {
		setPartName("Scheduler (Waiting for DFS)");
		_addAction.setEnabled(false);
		_removeAction.setEnabled(false);
		_manualScheduler.setEnabled(false);
		UIUtils.enableAll(_parent, false);
	}
}
