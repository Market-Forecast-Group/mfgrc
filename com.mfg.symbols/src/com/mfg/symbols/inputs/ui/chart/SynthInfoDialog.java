package com.mfg.symbols.inputs.ui.chart;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.ISyntheticModel;

public class SynthInfoDialog extends Dialog {
	private Table _table;
	private TableViewer _scalesTable;
	Chart _chart;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SynthInfoDialog(Shell parentShell) {
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
		container.setLayout(new GridLayout(1, false));

		_scalesTable = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		_table = _scalesTable.getTable();
		_table.setLinesVisible(true);
		_table.setHeaderVisible(true);
		_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				_scalesTable, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((ScaleInfo) element).synthScale);
			}
		});

		tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ScaleInfo info = (ScaleInfo) cell.getElement();
				cell.setText(Integer.toString(info.synthScale));
				float[] color = _chart.getSyntheticLayer().getScaleColor(
						info.synthScale);
				out.println(info.synthScale + " " + Arrays.toString(color));
				int r = (int) (color[0] * 255);
				int g = (int) (color[1] * 255);
				int b = (int) (color[2] * 255);
				cell.setBackground(SWTResourceManager.getColor(r, g, b));
			}
		});

		TableColumn tblclmnSScale = tableViewerColumn.getColumn();
		tblclmnSScale.setWidth(118);
		tblclmnSScale.setText("Synth Scale");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				_scalesTable, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((ScaleInfo) element).dataLayer);
			}
		});
		TableColumn tblclmnLayer = tableViewerColumn_1.getColumn();
		tblclmnLayer.setWidth(127);
		tblclmnLayer.setText("Data Layer");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				_scalesTable, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((ScaleInfo) element).indicatorScale);
			}
		});
		TableColumn tblclmnIScale = tableViewerColumn_2.getColumn();
		tblclmnIScale.setWidth(100);
		tblclmnIScale.setText("Indicator Scale");
		_scalesTable.setContentProvider(new ArrayContentProvider());

		afterCreateWidgets();

		return container;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Synthetic Chart Info");
	}

	class ScaleInfo {
		public int synthScale;
		public int dataLayer;
		public int indicatorScale;
	}

	private void afterCreateWidgets() {
		updateScalesTable();
	}

	private void updateScalesTable() {
		ISyntheticModel model = _chart.getModel().getSyntheticModel();
		List<ScaleInfo> list = new ArrayList<>();
		for (int scale = 1; scale <= model.getScaleCount(); scale++) {
			ScaleInfo info = new ScaleInfo();
			info.synthScale = scale;
			info.dataLayer = model.getDataLayer(scale);
			info.indicatorScale = model.getIndicatorScale(scale);
			list.add(info);
		}
		_scalesTable.setInput(list);
	}

	public void setChart(Chart chart) {
		_chart = chart;
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
		return new Point(450, 300);
	}

}
