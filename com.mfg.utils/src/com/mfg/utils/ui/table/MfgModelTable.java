package com.mfg.utils.ui.table;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.swt.IFocusService;

public class MfgModelTable extends Composite {

	IMfgTableModel model;
	TableModelMiddleMan mm;
	TableViewer tableViewer;
	private Table table;

	public MfgModelTable(Composite aParent, IMfgTableModel aModel) {
		super(aParent, SWT.NONE);
		this.model = aModel;
		mm = new TableModelMiddleMan();
		mm.setModel(aModel);
		createTable(this);
	}

	private void createTable(Composite aParent) {
		aParent.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite = new Composite(aParent, SWT.NONE);
		composite.setBackground(new org.eclipse.swt.graphics.Color(
				getDisplay(), 255, 0, 0));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		// composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true, 1, 1));
		TableColumnLayout tcl_composite_1 = new TableColumnLayout();
		composite_1.setLayout(tcl_composite_1);

		tableViewer = new TableViewer(composite_1, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (int i = 0; i < mm.getColumnCount(); i++) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(
					tableViewer, SWT.NONE);
			tableViewerColumn.setLabelProvider(mm.getColumn(i));
			TableColumn tblclmnName = tableViewerColumn.getColumn();
			tcl_composite_1.setColumnData(tblclmnName, new ColumnWeightData(1,
					ColumnWeightData.MINIMUM_WIDTH, true));
			tblclmnName.setText(mm.getColumnName(i));
		}
		tableViewer.setContentProvider(new ArrayContentProvider());
		// set the table data here
		tableViewer.setInput(mm.getIndexesInput());

	}

	public void trackFocus(IWorkbenchSite site) {
		IFocusService service = (IFocusService) site
				.getService(IFocusService.class);
		service.addFocusTracker(table, "com.mfg.utils.ui.table.MfgModelTable");
	}

	public void untrackFocus(IWorkbenchSite site) {
		IFocusService service = (IFocusService) site
				.getService(IFocusService.class);
		service.removeFocusTracker(table);
	}

	public void addSelectionListener(SelectionListener l) {
		table.addSelectionListener(l);
	}

	public int getSelectedIndex() {
		return table.getSelectionIndex();
	}

	public IMfgTableModel getModel() {
		return model;
	}

	public CopyFromMFGModelTableAction getCopyAction() {
		return new CopyFromMFGModelTableAction(this);
	}

	public void setModel(IMfgTableModel aModel) {
		model = aModel;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				mm.setModel(model);
				tableViewer.setInput(mm.getIndexesInput());
			}
		});

	}

	public void refresh() {
		// table.redraw();
		tableViewer.refresh();
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public String getRowText(int index) {
		String res = "";
		for (int i = 0; i < mm.getColumnCount(); i++) {
			res += (model.getContent(index, i).toString() + "\t");
		}
		return res + System.getProperty("line.separator");
	}
}
