package com.marketforecastgroup.dfsa.ui.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.marketforecastgroup.dfsa.ui.DFSSymbolsLabelProvider;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.utils.PartUtils;
import com.mfg.utils.ui.UIUtils;

public class MaturitiesView extends ViewPart {

	enum DateFilterOp {
		NO_FILTER {
			@Override
			public String toString() {
				return "";
			}

			@Override
			public boolean select(long filterDate, long date) {
				return true;
			}
		},
		BEFORE {
			@Override
			public boolean select(long filterDate, long date) {
				return date > 0 && date < filterDate;
			}
		},
		AFTER {
			@Override
			public boolean select(long filterDate, long date) {
				return date > 0 && date > filterDate;
			}
		},
		AROUND {
			@SuppressWarnings("deprecation")
			// Necessary use of getYear() function.
			@Override
			public boolean select(long filterDate, long date) {
				return date > 0
						&& new Date(filterDate).getYear() == new Date(date)
								.getYear();
			}
		};

		public abstract boolean select(long filterDate, long date);
	}

	public static final String ID = "com.marketforecastgroup.dfsa.ui.views.maturities"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;
	DfsSymbol _symbol;
	private TableViewer tableViewer;
	private Sorter[] sorters;
	private Text textStartDate;
	private Text textEndDate;
	private Button btnRange;
	private Button btnMinute;
	private Button btnDaily;
	private ComboViewer comboEndDateOp;
	private ComboViewer comboStartDateOp;
	final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:SS");

	public MaturitiesView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(1, false));
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayout(new GridLayout(13, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
			toolkit.adapt(composite);
			toolkit.paintBordersFor(composite);
			{
				btnRange = new Button(composite, SWT.CHECK);
				btnRange.setSelection(true);
				toolkit.adapt(btnRange, true, true);
				btnRange.setText("Range");
			}
			{
				btnMinute = new Button(composite, SWT.CHECK);
				btnMinute.setSelection(true);
				toolkit.adapt(btnMinute, true, true);
				btnMinute.setText("Minute");
			}
			{
				btnDaily = new Button(composite, SWT.CHECK);
				btnDaily.setSelection(true);
				toolkit.adapt(btnDaily, true, true);
				btnDaily.setText("Daily");
			}
			{
				Label label = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
				GridData gd_label = new GridData(SWT.RIGHT, SWT.CENTER, false,
						false, 1, 1);
				gd_label.heightHint = 20;
				label.setLayoutData(gd_label);
				toolkit.adapt(label, true, true);
			}
			new Label(composite, SWT.NONE).setText("");
			{
				Label lblStartDate = new Label(composite, SWT.NONE);
				lblStartDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
				toolkit.adapt(lblStartDate, true, true);
				lblStartDate.setText("Start Date");
			}
			{
				comboStartDateOp = new ComboViewer(composite, SWT.READ_ONLY);
				Combo combo = comboStartDateOp.getCombo();
				toolkit.paintBordersFor(combo);
				comboStartDateOp.setContentProvider(new ArrayContentProvider());
				comboStartDateOp.setLabelProvider(new LabelProvider());
			}
			{
				textStartDate = new Text(composite, SWT.BORDER);
				toolkit.adapt(textStartDate, true, true);
			}
			{
				Label lblEndDate = new Label(composite, SWT.NONE);
				lblEndDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
				toolkit.adapt(lblEndDate, true, true);
				lblEndDate.setText("End Date");
			}
			{
				comboEndDateOp = new ComboViewer(composite, SWT.READ_ONLY);
				Combo combo = comboEndDateOp.getCombo();
				toolkit.paintBordersFor(combo);
				comboEndDateOp.setLabelProvider(new LabelProvider());
				comboEndDateOp.setContentProvider(new ArrayContentProvider());
			}
			{
				textEndDate = new Text(composite, SWT.BORDER);
				toolkit.adapt(textEndDate, true, true);
			}
			{
				Label label = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
				GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false,
						false, 1, 1);
				gd_label.heightHint = 20;
				label.setLayoutData(gd_label);
				toolkit.adapt(label, true, true);
			}
			{
				Button btnFilter = new Button(composite, SWT.NONE);
				btnFilter.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						doFilter();
					}
				});
				toolkit.adapt(btnFilter, true, true);
				btnFilter.setText("Filter");
			}
		}
		{
			tableViewer = new TableViewer(container, SWT.BORDER
					| SWT.FULL_SELECTION);
			table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					1));
			toolkit.paintBordersFor(table);
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return DFSSymbolsLabelProvider
								.getMaturityStateImage(((Row) element).interval.state);
					}

					@Override
					public String getText(Object element) {
						return ((Row) element).maturity;
					}
				});
				final TableColumn tblclmnMaturity = tableViewerColumn
						.getColumn();
				tblclmnMaturity.setWidth(100);
				tblclmnMaturity.setText("Maturity");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return ((Row) element).type.name();
					}
				});
				final TableColumn tblclmnType = tableViewerColumn.getColumn();
				tblclmnType.setWidth(100);
				tblclmnType.setText("Type");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return ((Row) element).interval.numBars + "";
					}
				});
				TableColumn tblclmnOfBars = tableViewerColumn.getColumn();
				tblclmnOfBars.setWidth(100);
				tblclmnOfBars.setText("# of Bars");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						long date = ((Row) element).interval.startDate;
						return date < 0 ? "N/A" : dateFormat.format(new Date(
								date));
					}
				});
				TableColumn tblclmnStartDate = tableViewerColumn.getColumn();
				tblclmnStartDate.setWidth(100);
				tblclmnStartDate.setText("Start Date");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						long date = ((Row) element).interval.endDate;
						return date < 0 ? "N/A" : dateFormat.format(new Date(
								date));
					}
				});
				TableColumn tblclmnEndDate = tableViewerColumn.getColumn();
				tblclmnEndDate.setWidth(100);
				tblclmnEndDate.setText("End Date");
			}
			tableViewer.setContentProvider(new ArrayContentProvider());
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreateWidgets();
	}

	static class TypeFilter extends ViewerFilter {

		Set<BarType> _set;

		public TypeFilter(List<BarType> set) {
			super();
			this._set = new HashSet<>(set);
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			return _set.contains(((Row) element).type);
		}

	}

	enum DateEdge {
		START, END;

		public long getDate(Row row) {
			return this == START ? row.interval.startDate
					: row.interval.endDate;
		}
	}

	static class DateFilter extends ViewerFilter {
		DateEdge _edge;
		DateFilterOp _op;
		long _filterDate;

		public DateFilter(DateEdge edge, DateFilterOp op, long filterDate) {
			super();
			this._edge = edge;
			this._op = op;
			this._filterDate = filterDate;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			long date = _edge.getDate((Row) element);
			return _op.select(_filterDate, date);
		}

	}

	void doFilter() {
		List<ViewerFilter> filters = new ArrayList<>();

		List<BarType> list = new ArrayList<>();
		if (btnRange.getSelection()) {
			list.add(BarType.RANGE);
		}
		if (btnMinute.getSelection()) {
			list.add(BarType.MINUTE);
		}
		if (btnDaily.getSelection()) {
			list.add(BarType.DAILY);
		}
		filters.add(new TypeFilter(list));

		DateFilterOp startDateOp = (DateFilterOp) ((StructuredSelection) comboStartDateOp
				.getSelection()).getFirstElement();
		DateFilterOp endDateOp = (DateFilterOp) ((StructuredSelection) comboEndDateOp
				.getSelection()).getFirstElement();
		try {
			DateFilter startDateFilter = new DateFilter(DateEdge.START,
					startDateOp, parseDate(textStartDate.getText()).getTime());
			filters.add(startDateFilter);

		} catch (ParseException e) {
			//
		}

		try {
			DateFilter endDateFilter = new DateFilter(DateEdge.END, endDateOp,
					parseDate(textEndDate.getText()).getTime());
			filters.add(endDateFilter);
			Row row = new Row();
			row.interval = new DfsIntervalStats(EVisibleState.COMPLETE);
		} catch (ParseException e) {
			//
		}

		tableViewer
				.setFilters(filters.toArray(new ViewerFilter[filters.size()]));

	}

	private static Date parseDate(String str) throws ParseException {
		String[] split = str.trim().split("-");
		// YYYY-MM-DD
		SimpleDateFormat f;
		int len = split.length;
		if (len == 1) {
			f = new SimpleDateFormat("yyyy");
		} else if (len == 2) {
			f = new SimpleDateFormat("yyyy-MM");
		} else if (len == 3) {
			f = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			throw new NumberFormatException("Invalid argument exception");
		}

		Date date = f.parse(str.trim());
		return date;
	}

	// public static void main(String[] args) throws ParseException {
	// out.println(parseDate("2012"));
	// out.println(parseDate("2012-07-12"));
	// }

	protected void setSortColumn(TableColumn col) {
		table.setSortColumn(col);
		Sorter sorter = sorters[table.indexOf(col)];
		int dir = -sorter.getDir();
		sorter.setDir(dir);
		table.setSortDirection(dir > 0 ? SWT.UP : SWT.DOWN);
		tableViewer.setSorter(null);
		tableViewer.setSorter(sorter);
	}

	@SuppressWarnings("rawtypes")
	class Sorter extends ViewerSorter {
		private final Comparator _comparator;
		private int _dir = 1;

		public Sorter(Comparator<Row> comparator) {
			super();
			this._comparator = comparator;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return _dir * _comparator.compare(e1, e2);
		}

		public int getDir() {
			return _dir;
		}

		public void setDir(int dir) {
			this._dir = dir;
		}
	}

	private void afterCreateWidgets() {
		UIUtils.enableAll(table.getParent(), false);
		comboStartDateOp.setInput(DateFilterOp.values());
		comboEndDateOp.setInput(DateFilterOp.values());
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				openSelection((Row) ((StructuredSelection) event.getSelection())
						.getFirstElement());
			}
		});

		Sorter matSort = new Sorter(new Comparator<Row>() {

			@Override
			public int compare(Row o1, Row o2) {
				return o1.maturity.compareTo(o2.maturity);
			}
		}

		);
		Sorter typeSort = new Sorter(new Comparator<MaturitiesView.Row>() {

			@Override
			public int compare(Row o1, Row o2) {
				return Integer.valueOf(o1.type.ordinal()).compareTo(
						Integer.valueOf(o2.type.ordinal()));
			}
		});
		Sorter numSort = new Sorter(new Comparator<MaturitiesView.Row>() {

			@Override
			public int compare(Row o1, Row o2) {
				return (Integer.valueOf(o1.interval.numBars)).compareTo(Integer
						.valueOf(o2.interval.numBars));
			}
		});

		Sorter startDateSort = new Sorter(new Comparator<MaturitiesView.Row>() {

			@Override
			public int compare(Row o1, Row o2) {
				return (Long.valueOf(o1.interval.startDate)).compareTo(Long
						.valueOf(o2.interval.startDate));
			}
		});

		Sorter endDateSort = new Sorter(new Comparator<MaturitiesView.Row>() {

			@Override
			public int compare(Row o1, Row o2) {
				return (Long.valueOf(o1.interval.endDate)).compareTo(Long
						.valueOf(o2.interval.endDate));
			}
		});

		sorters = new Sorter[] { matSort, typeSort, numSort, startDateSort,
				endDateSort };

		for (final TableColumn col : table.getColumns()) {
			col.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					setSortColumn(col);
				}
			});
		}
	}

	void openSelection(final Row row) {
		if (row.interval.numBars == 0) {
			MessageDialog.openInformation(getSite().getShell(),
					"Open Bars View", "No data available for maturity "
							+ row.maturity);
		} else {
			int nunits = 0;
			// ask to the user for the bar width
			int max = 0;
			switch (row.type) {
			case DAILY:
				max = 30;
				break;
			case MINUTE:
				max = 240;
				break;
			case RANGE:
				nunits = 1;
			}
			if (nunits == 0) {
				final int fmax = max;
				InputDialog dlg = new InputDialog(getViewSite().getShell(),
						"Bar Width", "Enter the bar width for the mautrity "
								+ row.stats.getMaturity().toFileString(), "1",
						new IInputValidator() {

							@Override
							public String isValid(String newText) {
								try {
									int n = Integer.parseInt(newText);
									if (n <= 0) {
										return "The minimum bar width is 1";
									}
									if (n > fmax) {
										return "The maximum bar width for the bar "
												+ row.type + " is " + fmax;
									}
								} catch (Exception e) {
									return e.getMessage();
								}
								return null;
							}
						});
				if (dlg.open() == Window.OK) {
					nunits = Integer.parseInt(dlg.getValue());
				}
			}
			if (nunits > 0) {
				final MaturityBarsView view = PartUtils
						.openView(MaturityBarsView.ID);
				final int fnunits = nunits;
				try {
					DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

						@Override
						public void run(IDFS dfs) {
							view.updateContent(_symbol.prefix,
									row.stats.getMaturity(), row.type,
									row.interval.numBars, fnunits, dfs);
						}

						@Override
						public void notReady() {
							// TODO Auto-generated method stub

						}
					});
				} catch (DFSException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

			}
		}
	}

	@Override
	public void dispose() {
		toolkit.dispose();
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
		table.setFocus();
	}

	public void setSymbol(DfsSymbol symbol) {
		this._symbol = symbol;
		UIUtils.enableAll(table.getParent(), true);
		setPartName("Maturities (" + symbol.prefix + ")");
		try {
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(final IDFS dfs) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							updateTableContent(dfs);
						}
					});

				}

				@Override
				public void notReady() {
					// nothing
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private static class Row {
		public Row() {
			// TODO Auto-generated constructor stub
		}

		String maturity;
		DfsIntervalStats interval;
		BarType type;
		MaturityStats stats;
	}

	void updateTableContent(IDFS dfs) {
		List<Object> input = new ArrayList<>();
		try {
			DfsSymbolStatus stats = dfs.getStatusForSymbol(_symbol.prefix);

			for (MaturityStats mat : stats.maturityStats) {
				for (Entry<BarType, DfsIntervalStats> entry : mat._map
						.entrySet()) {
					Row r = new Row();
					r.stats = mat;
					r.maturity = mat.getMaturity().toFileString();
					r.interval = entry.getValue();
					r.type = entry.getKey();

					// TODO: just for now, Lino has to fix the startDate.
					// if (r.interval.numBars == 0) {
					// r.interval.startDate = -1;
					// }
					input.add(r);
				}
			}
		} catch (DFSException e) {
			e.printStackTrace();
		}
		tableViewer.setInput(input);
	}

	public DfsSymbol getSymbol() {
		return _symbol;
	}
}
