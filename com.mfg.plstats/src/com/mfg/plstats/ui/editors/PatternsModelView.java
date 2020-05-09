package com.mfg.plstats.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;

public class PatternsModelView extends ViewPart implements
		ISelectionChangedListener, ObjectListener<DistributionsContainer> {

	public static final String ID = "com.mfg.plstats.ui.editors.PatternsModelView"; //$NON-NLS-1$
	StepDefinition step = new StepDefinition(0.01);
	ElementsPatterns[] list;
	TreeViewer treeViewer;
	ElementsPatterns root;

	public static final String[] _COLUMNS = new String[] { "ID", "Ratio",
			"L(>)", "U(<=)", "Max Sw0R", "Elem", "T1" };

	public PatternsModelView() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
		setList(getElements());
	}

	private ElementsPatterns[] getElements() {
		DistributionsContainer distributionsContainer = WidgetPlugin
				.getDefault().getProbabilitiesManager()
				.getDistributionsContainer();
		System.out.println("patterns from " + distributionsContainer);
		if (distributionsContainer == null
				|| distributionsContainer.getConfiguration() == null) {
			root = null;
			return new ElementsPatterns[] {};
		}
		setPartName("Patterns [scale="
				+ distributionsContainer.getConfiguration().getDefaultScale()
				+ "]");
		root = distributionsContainer.getElementsPatternsRoot();
		List<ElementsPatterns> leaves = root
				.getLeaves(new ArrayList<ElementsPatterns>());
		System.out.println("patterns " + leaves);
		return leaves.toArray(new ElementsPatterns[] {});
	}

	public ElementsPatterns[] getList() {
		return list;
	}

	public void setList(ElementsPatterns[] aList) {
		list = aList;
		// getModel().setList(list);
	}

	static class PatternsModel implements IMfgTableModel {
		private ElementsPatterns[] pattList;

		public ElementsPatterns[] getList() {
			return pattList;
		}

		public void setList(ElementsPatterns[] aList) {
			pattList = aList;
		}

		@Override
		public int getRowCount() {
			if (pattList == null) {
				return 0;

			}
			return pattList.length;
		}

		@Override
		public Object getContent(int row, int column) {
			if (row > pattList.length - 1)
				return null;
			ElementsPatterns k = pattList[row];
			switch (column) {
			case 0:
				return Integer.valueOf(k.getLeafID());
			case 1:
				return Double.valueOf(k.getLowerBound());
			case 2:
				return Double.valueOf(k.getUpperBound());
				// case 3 :
				// return step.round(k.getMaxSw0Ratio());
			case 4:
				return Integer.valueOf(k.getSize());
			case 5:
				return Double.valueOf(k.getFirstTarget());
			default:
				return null;
			}
		}

		@Override
		public String[] getColumnNames() {
			return _COLUMNS;
		}

		@Override
		public boolean isEnabled(int aRow, int aColumn) {
			return true;
		}

		@Override
		public int getHighLight(int aRow, int aColumn) {
			return 0;
		}

	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@SuppressWarnings("synthetic-access")
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		// mfgTable = new MfgModelTable(container, getModel());
		// mfgTable.addSelectionListener(this);

		this.treeViewer = new TreeViewer(container);
		treeViewer.addSelectionChangedListener(this);
		Tree tree = this.treeViewer.getTree();

		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.treeViewer.getControl().setLayoutData(gridData);
		this.treeViewer.setUseHashlookup(true);

		/*** Tree table specific code starts ***/

		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		for (String col : _COLUMNS) {
			TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
			treeColumn.setText(col);
		}

		TableLayout layout = new TableLayout();
		int nColumns = _COLUMNS.length;
		int weight = 50;
		for (int i = 0; i < nColumns; i++) {
			layout.addColumnData(new ColumnWeightData(weight));
		}

		tree.setLayout(layout);

		/*** Tree table specific code ends ***/

		this.treeViewer.setContentProvider(new MyContentProvider());
		this.treeViewer.setLabelProvider(new MyLabelProvider());
		this.treeViewer.setInput(this.root);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	private class MyLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(Object aElement, int aColumnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object aElement, int aColumnIndex) {
			ElementsPatterns k = (ElementsPatterns) aElement;
			switch (aColumnIndex) {
			case 0:
				return k.getLeafID() > 0 ? k.getLeafID() + "" : "";
			case 1:
				return getRatioText(k);
			case 2:
				return k.getLowerBound() + "";
			case 3:
				return k.getUpperBound() + "";
			case 4:
				return step.round(k.getMaxSw0Ratio()) + "";
			case 5:
				return k.getSize() + "";
			case 6:
				return k.getFirstTarget() + "";
			default:
				return null;
			}
		}

		public String getRatioText(ElementsPatterns k) {
			int ratioLevel = k.getParent().getRatioLevel();
			switch (ratioLevel) {
			case 0:
				return "s(0')/s(-1)";
			default:
				return "s(" + (-ratioLevel) + ")/s(" + (-ratioLevel - 1) + ")";
			}
		}
	}

	private class MyContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// Documenting empty method to avoid warning.
		}

		@Override
		public void inputChanged(Viewer aViewer, Object aOldInput,
				Object aNewInput) {
			// Documenting empty method to avoid warning.
		}

		@Override
		public Object[] getElements(Object aInputElement) {
			return getChildren(aInputElement);
		}

		@Override
		public Object[] getChildren(Object aParentElement) {
			if (aParentElement instanceof ElementsPatterns) {
				ElementsPatterns ep = (ElementsPatterns) aParentElement;
				List<ElementsPatterns> children = ep.getChildren();
				if (children != null)
					return children.toArray();
			}
			return new Object[] {};
		}

		@Override
		public Object getParent(Object aElement) {
			if (aElement instanceof ElementsPatterns) {
				ElementsPatterns ep = (ElementsPatterns) aElement;
				return ep.getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object aElement) {
			if (aElement instanceof ElementsPatterns) {
				ElementsPatterns ep = (ElementsPatterns) aElement;
				return !ep.isLeaf();
			}
			return false;
		}

	}

	// private PatternsModel getModel() {
	// if (model == null) {
	// model = new PatternsModel();
	// }
	// return model;
	// }

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
		// Set the focus
	}

	@Override
	public void handle(DistributionsContainer newDist) {
		setList(getElements());
		// model.setList(list);
		System.out.println("new patterns " + Arrays.toString(list));
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				treeViewer.setInput(root);
				treeViewer.refresh();
				treeViewer.expandToLevel(3);
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.removeObjectListener(this);

	}

	@Override
	public void selectionChanged(SelectionChangedEvent aEvent) {
		Object selection = ((IStructuredSelection) treeViewer.getSelection())
				.getFirstElement();
		if (selection instanceof ElementsPatterns) {
			ElementsPatterns s = ((ElementsPatterns) selection);
			if (s.isLeaf()) {
				WidgetPlugin.getDefault().getProbabilitiesManager()
						.setSelectedPattern(s.getLeafID());
			}
		}
	}
}
