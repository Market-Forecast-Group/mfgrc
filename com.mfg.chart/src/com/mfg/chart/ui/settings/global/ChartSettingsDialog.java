package com.mfg.chart.ui.settings.global;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;

public class ChartSettingsDialog extends Dialog {
	private TreeViewer _treeViewer;
	private Chart _chart;
	private ChartSettingsEditorProvider _editorProvider;
	private Map<Object, IChartSettingsEditor> _editorMap;
	private Composite _editorStack;
	private StackLayout _stackLayout;
	private Label _titleLabel;
	private Object _lastSelection;
	private Object _context;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ChartSettingsDialog(Shell parentShell) {
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

		SashForm sashForm = new SashForm(container, SWT.NONE);

		Composite leftComp = new Composite(sashForm, SWT.BORDER);
		leftComp.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		leftComp.setLayout(new GridLayout(1, false));

		PatternFilter filter = new ChartSettingsPatternFilter();
		FilteredTree filteredTree = new FilteredTree(leftComp, SWT.None,
				filter, true);
		filteredTree
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_treeViewer = filteredTree.getViewer();
		_treeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						Object firstElement = ((StructuredSelection) event
								.getSelection()).getFirstElement();
						selection(firstElement);
					}
				});
		Tree tree = _treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_treeViewer.setContentProvider(new ChartSettingsContentProvider());
		_treeViewer.setLabelProvider(new ChartSettingsLabelProvider());

		Composite composite = new Composite(sashForm, SWT.BORDER);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		_titleLabel = new Label(composite, SWT.NONE);
		GridData gd_titleLabel = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_titleLabel.verticalIndent = 5;
		gd_titleLabel.horizontalIndent = 5;
		_titleLabel.setLayoutData(gd_titleLabel);
		_titleLabel
				.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		_titleLabel.setText("Title");

		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1);
		gd_label.verticalIndent = 5;
		label.setLayoutData(gd_label);

		_editorStack = new Composite(composite, SWT.NONE);
		_editorStack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		_editorStack.setLayout(new StackLayout());
		sashForm.setWeights(new int[] { 191, 314 });

		afterCreateWidgets();

		return container;
	}

	private void afterCreateWidgets() {
		Object sel = _lastSelection;
		_editorProvider = new ChartSettingsEditorProvider();
		_editorMap = new HashMap<>();
		_stackLayout = (StackLayout) _editorStack.getLayout();
		_treeViewer.setInput(new ChartSettingsInput(_chart));
		_treeViewer.expandAll();
		if (sel != null) {
			_treeViewer.setSelection(new StructuredSelection(sel));
			getShell().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					resizeShell();
				}
			});
		}
	}

	protected void selection(Object node) {
		_lastSelection = node;
		IChartSettingsEditor editor = _editorMap.get(node);
		if (editor == null) {
			editor = _editorProvider.createEditor(_editorStack, _chart, node,
					_context);
			_editorMap.put(node, editor);
		}
		LabelProvider labelProvider = (LabelProvider) _treeViewer
				.getLabelProvider();
		_titleLabel.setText(labelProvider.getText(node));
		_stackLayout.topControl = editor.getUI();
		_editorStack.layout();
		resizeShell();
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	void resizeShell() {
		Point s1 = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point s2 = getShell().getSize();
		Point s = new Point(s2.x, s2.y);
		if (s1.x > s.x) {
			s.x = s1.x;
		}
		if (s1.y > s.y) {
			s.y = s1.y;
		}
		getShell().setSize(s);
		getShell().layout();
	}

	public Object getLastSelection() {
		return _lastSelection;
	}

	public void setLastSelection(Object lastSelection) {
		_lastSelection = lastSelection;
	}

	@Override
	protected void okPressed() {
		applyChanges();
		super.okPressed();
	}

	private void applyChanges() {
		for (IChartSettingsEditor editor : _editorMap.values()) {
			editor.applyChanges();
		}
		_chart.fireRangeChanged();
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
		return new Point(703, 534);
	}

	public void setChart(Chart chart) {
		_chart = chart;
	}

	public void setContext(Object context) {
		_context = context;
	}

}
