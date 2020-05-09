package com.marketforecastgroup.dfsa.ui.views;

import java.util.Date;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.marketforecastgroup.dfsa.ui.SymbolsContentProvider;
import com.marketforecastgroup.dfsa.ui.SymbolsContentProvider.IntervalInfo;
import com.mfg.ui.UIPlugin;

public class SymbolsNavigator extends CommonNavigator {

	public static final String ID = "com.marketforecastgroup.dfsa.ui.views.symbols";

	@Override
	protected CommonViewer createCommonViewerObject(Composite aParent) {
		CommonViewer viewer = super.createCommonViewerObject(aParent);
		Tree treeTable = viewer.getTree();
		treeTable.setHeaderVisible(true);
		TreeColumn column = new TreeColumn(treeTable, SWT.SINGLE);
		column.setText("Name");
		column.setWidth(200);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("# of Bars");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SymbolsContentProvider.IntervalInfo) {
					IntervalInfo info = (SymbolsContentProvider.IntervalInfo) element;
					return Integer.toString(info.interval.numBars);
				}
				return "";
			}
		});

		col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("Start Date");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SymbolsContentProvider.IntervalInfo) {
					IntervalInfo info = (SymbolsContentProvider.IntervalInfo) element;
					return new Date(info.interval.startDate).toString();
				}
				return "";
			}
		});

		col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("End Date");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SymbolsContentProvider.IntervalInfo) {
					IntervalInfo info = (SymbolsContentProvider.IntervalInfo) element;
					return new Date(info.interval.endDate).toString();
				}
				return "";
			}
		});

		viewer.setData("mfg.sort", Integer.valueOf(0));

		return viewer;
	}

	@Override
	protected void initListeners(TreeViewer viewer) {
		super.initListeners(viewer);
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				try {
					UIPlugin.openEditor(((StructuredSelection) event
							.getSelection()).getFirstElement());
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void init(IViewSite aSite, IMemento aMemento)
			throws PartInitException {
		super.init(aSite, aMemento);

		// we have to activate this context because some commands are connected
		// to this that
		IContextService serv = (IContextService) getViewSite().getService(
				IContextService.class);
		serv.activateContext("com.mfg.ui.navigatorContext");

	}

	@Override
	protected Object getInitialInput() {
		return SymbolsContentProvider.ROOT;
	}

	@Override
	protected ActionGroup createCommonActionGroup() {
		// if we want to see the filter actions, then use this group
		// return new FilterActionGroup(getCommonViewer());
		// else, use an empty group
		return new ActionGroup() {
			// empty group
		};
	}
}
