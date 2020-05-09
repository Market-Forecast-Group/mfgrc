package com.marketforecastgroup.dfsa.ui.editors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.common.BarType;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.dfs.data.MaturityStats;

public class MaturityEditorPage extends FormPage {
	private Table table;
	private TableViewer tableViewer;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public MaturityEditorPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public MaturityEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Maturity");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		managedForm.getForm().getBody().setLayout(new GridLayout(1, false));

		Section sctnIntervals = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(), ExpandableComposite.TITLE_BAR);
		sctnIntervals.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnIntervals);
		sctnIntervals.setText("Intervals");

		tableViewer = new TableViewer(managedForm.getForm().getBody(),
				SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(table);
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}
			@Override
			public String getText(Object element) {
				return element == null ? "" : element.toString();
			}
		});
		TableColumn tblclmnBarType = tableViewerColumn_3.getColumn();
		tblclmnBarType.setWidth(125);
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
				return Integer.toString(getInterval(element).numBars);
			}
		});
		TableColumn tblclmnOfBars = tableViewerColumn.getColumn();
		tblclmnOfBars.setWidth(145);
		tblclmnOfBars.setText("# of Bars");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return new Date(getInterval(element).startDate).toString();
			}
		});
		TableColumn tblclmnStartDate = tableViewerColumn_1.getColumn();
		tblclmnStartDate.setWidth(242);
		tblclmnStartDate.setText("Start Date");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return new Date(getInterval(element).endDate).toString();
			}
		});
		TableColumn tblclmnEndDate = tableViewerColumn_2.getColumn();
		tblclmnEndDate.setWidth(100);
		tblclmnEndDate.setText("End Date");
		tableViewer.setContentProvider(new ArrayContentProvider());

		Composite composite = managedForm.getToolkit().createComposite(
				managedForm.getForm().getBody(), SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		MaturityEditorInput input = (MaturityEditorInput) getEditorInput();
		List<BarType> list = new ArrayList<>();
		for (BarType t : BarType.values()) {
			if (input.getMaturity()._map.containsKey(t)) {
				list.add(t);
			}
		}
		tableViewer.setInput(list);
	}

	public DfsIntervalStats getInterval(Object element) {
		Object type = element;
		MaturityStats stats = ((MaturityEditorInput) getEditorInput())
				.getMaturity();
		return stats._map.get(type);
	}
}
