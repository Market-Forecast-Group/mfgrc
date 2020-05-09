package com.mfg.symbols.trading.ui.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.navigator.CommonViewer;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.symbols.ui.views.SymbolNavigator;

public class SelectGlobalCommandConfigurationsDialog extends Dialog {

	private Composite _container;
	private Set<Object> _checkedElements;
	private CommonViewer _viewer;
	private List<Object> _result;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SelectGlobalCommandConfigurationsDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		_container = (Composite) super.createDialogArea(parent);
		_container.setLayout(new FillLayout(SWT.HORIZONTAL));

		afterCreateWidgets();
		return _container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("Select Configurations");
		super.configureShell(newShell);
	}

	private void afterCreateWidgets() {
		createViewer();
	}

	void createViewer() {
		_viewer = new CommonViewer("com.mfg.symbols.ui.views.navigator",
				_container, SWT.CHECK | SWT.BORDER);
		_viewer.setInput(SymbolNavigator.ROOT_NODE);
		_viewer.setFilters(new ViewerFilter[] { createFilter() });
		Tree tree = _viewer.getTree();
		tree.addListener(SWT.Selection, createTreeListener());
		tree.setRedraw(false);
		_viewer.expandAll();
		for (TreeItem i : tree.getItems()) {
			check(i);
		}
		for (TreeItem i : tree.getItems()) {
			if (i.getText().equals("DFS")) {
				collapse(i);
			}
		}
		tree.setRedraw(true);
	}

	static Listener createTreeListener() {
		return new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					checkAllChildren(item, item.getChecked());
				}
			}

			private void checkAllChildren(TreeItem item, boolean check) {
				for (TreeItem i2 : item.getItems()) {
					i2.setChecked(check);
					checkAllChildren(i2, check);
				}
			}
		};
	}

	void check(TreeItem i) {
		Object data = i.getData();
		if (i.getText().equals("Single Contracts")) {
			_viewer.collapseToLevel(data, 1);
		}

		if (_checkedElements.contains(data)) {
			checkParents(i.getParentItem());
			i.setChecked(true);
		}
		for (TreeItem i2 : i.getItems()) {
			check(i2);
		}
	}

	private void checkParents(TreeItem i) {
		if (i != null && !i.getChecked()) {
			i.setChecked(true);
			checkParents(i.getParentItem());
		}
	}

	void collapse(TreeItem i) {
		Object data = i.getData();
		if (i.getText().equals("Single Contracts")) {
			_viewer.collapseToLevel(data, 1);
		} else {
			for (TreeItem i2 : i.getItems()) {
				collapse(i2);
			}
		}
	}

	private static ViewerFilter createFilter() {
		// this is an ugly work around, the best is to create a filter in the
		// DFS symbols plugin.
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer aViewer, Object parentElement,
					Object element) {
				return !element.getClass().getSimpleName()
						.equals("IntervalInfo");
			}
		};
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			_result = new ArrayList<>();
			for (TreeItem i : _viewer.getTree().getItems()) {
				addToResult(i);
			}
		}
		super.buttonPressed(buttonId);
	}

	public List<Object> getResult() {
		return _result;
	}

	private void addToResult(TreeItem i) {
		if (i.getChecked()) {
			Object data = i.getData();
			if (data instanceof BaseConfiguration) {
				_result.add(data);
			}
		}
		for (TreeItem i2 : i.getItems()) {
			addToResult(i2);
		}
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
		return new Point(461, 430);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setCheckedElements(List<Object> checkedElements) {
		_checkedElements = new HashSet(checkedElements);
	}

}
