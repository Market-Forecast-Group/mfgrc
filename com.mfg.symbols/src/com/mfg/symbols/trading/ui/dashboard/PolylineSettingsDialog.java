package com.mfg.symbols.trading.ui.dashboard;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.symbols.trading.ui.dashboard.PolylineWidgetModel.RowInfo;

public class PolylineSettingsDialog extends Dialog {
	private static final String YES = "Yes";
	private static final String NO = "No";
	private PolylineAdapter _figure;
	private PolylineWidgetModel _editingModel;
	Table _table;
	CheckboxTableViewer _tableViewer;

	public class YesNoEditingSupport extends EditingSupport {
		public YesNoEditingSupport() {
			super(_tableViewer);
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new ComboBoxCellEditor(_table, new String[] { YES, NO },
					SWT.READ_ONLY);
		}

		@Override
		protected final Object getValue(Object element) {
			return getBooleanValue((RowInfo) element) ? Integer.valueOf(0)
					: Integer.valueOf(1);
		}

		@Override
		protected final void setValue(Object element, Object value) {
			setBooleanValue((RowInfo) element,
					((Integer) value).intValue() == 0);
			getViewer().refresh(element);
		}

		@SuppressWarnings("unused")
		protected void setBooleanValue(RowInfo row, boolean b) {
			//
		}

		protected boolean getBooleanValue(
				@SuppressWarnings("unused") RowInfo row) {
			return true;
		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public PolylineSettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		_tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		_table = _tableViewer.getTable();
		_table.setLinesVisible(true);
		_table.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RowInfo row = (RowInfo) element;
				return row.polyline == EquationType.AVG ? "Scale " + row.scale
						: "";
			}
		});
		TableColumn tblclmnScale = tableViewerColumn.getColumn();
		tblclmnScale.setWidth(100);
		tblclmnScale.setText("Scale");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "Polyline "
						+ (((RowInfo) element).polyline.ordinal() + 1);
			}
		});
		TableColumn tblclmnEvent = tableViewerColumn_1.getColumn();
		tblclmnEvent.setWidth(100);
		tblclmnEvent.setText("Event");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_2.setEditingSupport(new YesNoEditingSupport() {
			@Override
			protected boolean getBooleanValue(RowInfo row) {
				return row.show;
			}

			@Override
			protected void setBooleanValue(RowInfo row, boolean b) {
				row.show = b;
			}
		});
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RowInfo) element).show ? "Yes" : "No";
			}
		});
		TableColumn tblclmnShowChart = tableViewerColumn_2.getColumn();
		tblclmnShowChart.setWidth(70);
		tblclmnShowChart.setText("Show\nChart");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_3.setEditingSupport(new YesNoEditingSupport() {
			@Override
			protected boolean getBooleanValue(RowInfo row) {
				return row.textWarning;
			}

			@Override
			protected void setBooleanValue(RowInfo row, boolean b) {
				row.textWarning = b;
			}
		});
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RowInfo) element).textWarning ? "Yes" : "No";
			}
		});
		TableColumn tblclmnTextWarning = tableViewerColumn_3.getColumn();
		tblclmnTextWarning.setWidth(78);
		tblclmnTextWarning.setText("Text\nWarning");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_4.setEditingSupport(new YesNoEditingSupport() {
			@Override
			protected boolean getBooleanValue(RowInfo row) {
				return row.soundWarning;
			}

			@Override
			protected void setBooleanValue(RowInfo row, boolean b) {
				row.soundWarning = b;
			}
		});
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RowInfo) element).soundWarning ? "Yes" : "No";
			}
		});
		TableColumn tblclmnSoundWarning = tableViewerColumn_4.getColumn();
		tblclmnSoundWarning.setWidth(76);
		tblclmnSoundWarning.setText("Sound\nWarning");

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_5.setEditingSupport(new EditingSupport(_tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				ComboBoxCellEditor editor = new ComboBoxCellEditor(_table,
						RowInfo.UPDATE_TYPES, SWT.READ_ONLY);
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return Integer.valueOf(Arrays.asList(RowInfo.UPDATE_TYPES)
						.indexOf(((RowInfo) element).updateType));
			}

			@Override
			protected void setValue(Object element, Object value) {
				((RowInfo) element).updateType = RowInfo.UPDATE_TYPES[((Integer) value)
						.intValue()];
				_tableViewer.refresh(element);
			}
		});
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RowInfo) element).updateType;
			}
		});
		TableColumn tblclmnUpdateType = tableViewerColumn_5.getColumn();
		tblclmnUpdateType.setWidth(100);
		tblclmnUpdateType.setText("Update\nType");
		_tableViewer.setContentProvider(new ArrayContentProvider());

		afterCreateWidgets();

		return container;
	}

	private void afterCreateWidgets() {
		PolylineWidgetModel model = _figure.getModel();
		_editingModel = model.clone();
		_tableViewer.setCheckStateProvider(new ICheckStateProvider() {

			@Override
			public boolean isGrayed(Object element) {
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				return ((RowInfo) element).include;
			}
		});
		_tableViewer.setInput(_editingModel.getRows());
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Polyline Widget Settings");
	}

	@Override
	protected void okPressed() {
		_editingModel.getRows().stream().forEach(r -> {
			r.include = _tableViewer.getChecked(r);
		});
		_figure.setModel(_editingModel);
		super.okPressed();
	}

	public void setFigure(PolylineAdapter figure) {
		_figure = figure;
	}

	public PolylineAdapter getFigure() {
		return _figure;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(562, 477);
	}

}
