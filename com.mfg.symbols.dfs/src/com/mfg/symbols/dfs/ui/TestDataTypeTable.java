package com.mfg.symbols.dfs.ui;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class TestDataTypeTable extends Shell {
	private Table table;
	private Text text_1;
	private Text text;
	private TableViewer tableViewer;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Text text_6;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			TestDataTypeTable shell = new TestDataTypeTable(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public TestDataTypeTable(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new GridLayout(1, false));

		Label lblOverview = new Label(this, SWT.NONE);
		lblOverview.setText("Overview");

		Composite composite_1 = new Composite(this, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1);
		gd_composite_1.heightHint = 200;
		composite_1.setLayoutData(gd_composite_1);
		TableColumnLayout tcl_composite_1 = new TableColumnLayout();
		composite_1.setLayout(tcl_composite_1);

		tableViewer = new TableViewer(composite_1, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((Object[]) element)[0].toString();
			}
		});
		TableColumn tblclmnBarDays = tableViewerColumn_1.getColumn();
		tcl_composite_1.setColumnData(tblclmnBarDays, new ColumnPixelData(105,
				true, true));
		tblclmnBarDays.setText("# of Bars");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((Object[]) element)[1].toString();
			}
		});
		TableColumn tblclmnOfBarsdays = tableViewerColumn_2.getColumn();
		tcl_composite_1.setColumnData(tblclmnOfBarsdays, new ColumnPixelData(
				83, true, true));
		tblclmnOfBarsdays.setText("# of Days");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((Object[]) element)[2].toString();
			}
		});
		TableColumn tblclmnBarType = tableViewerColumn_3.getColumn();
		tcl_composite_1.setColumnData(tblclmnBarType, new ColumnPixelData(99,
				true, true));
		tblclmnBarType.setText("Bar Type");

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((Object[]) element)[3].toString();
			}
		});
		TableColumn tblclmnScale = tableViewerColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnScale, new ColumnPixelData(85,
				true, true));
		tblclmnScale.setText("Scale");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((Object[]) element)[4].toString();
			}
		});
		TableColumn tblclmnGap = tableViewerColumn_4.getColumn();
		tcl_composite_1.setColumnData(tblclmnGap, new ColumnPixelData(74, true,
				true));
		tblclmnGap.setText("Gap");
		tableViewer.setContentProvider(new ArrayContentProvider());

		Group grpDetails = new Group(this, SWT.NONE);
		grpDetails.setLayout(new GridLayout(5, false));
		grpDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		grpDetails.setText("Details");

		Button btnStartDate = new Button(grpDetails, SWT.CHECK);
		btnStartDate.setSelection(true);
		btnStartDate.setText("Start Date");

		DateTime dateTime_2 = new DateTime(grpDetails, SWT.BORDER);
		dateTime_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		dateTime_2.setEnabled(false);

		Label lblMinDate = new Label(grpDetails, SWT.NONE);
		lblMinDate.setText("Min. Date");

		text_6 = new Text(grpDetails, SWT.BORDER);
		text_6.setText("1/27/2013");
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Button btnSet = new Button(grpDetails, SWT.NONE);
		btnSet.setText("Set");

		Button btnEndDate = new Button(grpDetails, SWT.CHECK);
		btnEndDate.setText("End Date");

		DateTime dateTime_3 = new DateTime(grpDetails, SWT.BORDER);
		dateTime_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		Label lblAvailableDates = new Label(grpDetails, SWT.NONE);
		lblAvailableDates.setText("Max. Date");

		text = new Text(grpDetails, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		text.setText("8/27/2013");

		Button button = new Button(grpDetails, SWT.NONE);
		button.setText("Set");

		Label lblOfDates = new Label(grpDetails, SWT.NONE);
		lblOfDates.setText("# of Days");

		text_4 = new Text(grpDetails, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		text_4.setText("100");

		Label lblAvailableDays = new Label(grpDetails, SWT.NONE);
		lblAvailableDays.setText("Total Days");

		text_2 = new Text(grpDetails, SWT.BORDER);
		text_2.setText("412");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		Button button_1 = new Button(grpDetails, SWT.NONE);
		button_1.setText("Set");

		Label lblOfBars = new Label(grpDetails, SWT.NONE);
		lblOfBars.setText("# of Bars");

		text_5 = new Text(grpDetails, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		text_5.setText("150");

		Label lblAvailableBars = new Label(grpDetails, SWT.NONE);
		lblAvailableBars.setText("Total Bars");

		text_3 = new Text(grpDetails, SWT.BORDER);
		text_3.setText("284");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		Button button_2 = new Button(grpDetails, SWT.NONE);
		button_2.setText("Set");

		Label label = new Label(grpDetails, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5,
				1));

		Label lblBarType = new Label(grpDetails, SWT.NONE);
		lblBarType.setText("Bar Type");

		Combo combo_1 = new Combo(grpDetails, SWT.READ_ONLY);
		GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gd_combo_1.widthHint = 120;
		combo_1.setLayoutData(gd_combo_1);
		combo_1.setItems(new String[] { "DAILY", "MINUTE", "RANGE" });
		combo_1.select(0);
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");

		Label lblScale = new Label(grpDetails, SWT.NONE);
		lblScale.setText("Scale");

		Combo combo = new Combo(grpDetails, SWT.READ_ONLY);
		combo.setItems(new String[] { "Price", "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "10" });
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		combo.select(0);
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");

		Label lblGap = new Label(grpDetails, SWT.NONE);
		lblGap.setText("Gap");

		text_1 = new Text(grpDetails, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		text_1.setText("0.0");
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(508, 526);
		tableViewer.setInput(new Object[] {
				new Object[] { Integer.valueOf(200), Integer.valueOf(10),
						"DAILY", "Price", Integer.valueOf(0) },
				new Object[] { Integer.valueOf(200), Integer.valueOf(10),
						"DAILY", "Price", Integer.valueOf(0) },
				new Object[] { Integer.valueOf(200), Integer.valueOf(10),
						"DAILY", "Price", Integer.valueOf(0) } });
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
