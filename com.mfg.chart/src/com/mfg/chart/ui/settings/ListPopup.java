package com.mfg.chart.ui.settings;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ListPopup extends Dialog {

	protected Object _result;
	protected Shell _shell;

	private Object _input;
	private ILabelProvider _labelProvider;
	private IContentProvider _contentProvider;
	private TableViewer _viewer;
	private Object _initialSelection;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ListPopup(Shell parent) {
		super(parent, SWT.TOOL);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		_shell.open();
		_shell.layout();
		Display display = getParent().getDisplay();
		while (!_shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return _result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		_shell = new Shell(getParent(), getStyle());
		_shell.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				shellFocusLost();
			}
		});
		_shell.setSize(450, 300);
		_shell.setText(getText());
		_shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		_viewer = new TableViewer(_shell, SWT.NONE);
		Table table = _viewer.getTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				tableMouseDown(e);
			}
		});
		table.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				tableFocusLost();
			}
		});

		afterCreateWidgets();
	}

	protected void tableMouseDown(MouseEvent e) {
		TableItem item = _viewer.getTable().getItem(new Point(e.x, e.y));
		if (item != null) {
			selected(item.getData());
		}
	}

	protected void selected(Object item) {
		_result = item;
		_shell.close();
	}

	protected void tableFocusLost() {
		_shell.close();
	}

	private void afterCreateWidgets() {
		_shell.setLocation(_shell.getDisplay().getCursorLocation());
		_viewer.setLabelProvider(getLabelProvider());
		_viewer.setContentProvider(getContentProvider());
		_viewer.setInput(getInput());

		if (getInitialSelection() != null) {
			_viewer.setSelection(new StructuredSelection(getInitialSelection()));
		}

		_shell.pack(true);
	}

	public Object getResult() {
		return _result;
	}

	public Object getInitialSelection() {
		return _initialSelection;
	}

	public void setInitialSelection(Object initialSelection) {
		_initialSelection = initialSelection;
	}

	public ILabelProvider getLabelProvider() {
		return _labelProvider;
	}

	public void setLabelProvider(ILabelProvider labelProvider) {
		_labelProvider = labelProvider;
	}

	public IContentProvider getContentProvider() {
		return _contentProvider;
	}

	public void setContentProvider(IContentProvider contentProvider) {
		_contentProvider = contentProvider;
	}

	public Object getInput() {
		return _input;
	}

	public void setInput(Object input) {
		_input = input;
	}

	void shellFocusLost() {
		_viewer.getTable().forceFocus();
	}
}
