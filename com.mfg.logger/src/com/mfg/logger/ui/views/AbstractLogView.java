/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */

package com.mfg.logger.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.swt.IFocusService;

import com.mfg.logger.ILogFilter;
import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;
import com.mfg.logger.ILoggerListener;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.ui.ILogTableModel;
import com.mfg.logger.ui.LogContentProvider;
import com.mfg.logger.ui.LogViewerAdapter;
import com.mfg.logger.ui.actions.ClearLogAction;
import com.mfg.logger.ui.actions.LinkToLastMessageAction;
import com.mfg.utils.concurrent.TwoElementsRequestQueue;

public abstract class AbstractLogView extends ViewPart implements ILogView {

	private static final String MEMENTO_CONTROL_ID = "com.mfg.logger.ui.views.AbstractLogView.ControlID";
	protected ILogTableModel _model;
	private TableViewer viewer;
	private Action scrollToEndAction;
	private Action clearAction;
	LogViewerAdapter adapter;
	TwoElementsRequestQueue queue;
	private final List<ILogClient> clients = new ArrayList<>();
	private CopyFromLogAction copy;
	private long fControlID;
	private AbstractLoggerViewControl control;

	public AbstractLogView() {
		fControlID = System.currentTimeMillis();
	}

	protected abstract ILogTableModel createLogModel();

	public abstract ILoggerManager getLogManager();

	public ILogTableModel getModel() {
		return _model;
	}

	public void setModel(ILogTableModel model) {
		this._model = model;
		viewer.setInput(model);
	}

	public void copy() {
		copy.run();
	}

	@Override
	public void createPartControl(Composite parent) {
		_model = createLogModel();
		adapter = new LogViewerAdapter(parent, _model, createContentProvider());
		viewer = adapter.getViewer();
		queue = new TwoElementsRequestQueue("Logger View Refresher"
				+ getPartName());

		getLogManager().addLoggerListener(new ILoggerListener() {

			@Override
			public void logged(ILogger logger, ILogRecord record) {
				queue.addRequest(new Runnable() {
					@Override
					public void run() {
						Display display = Display.getDefault();
						if (!display.isDisposed()) {

							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									if (!adapter.getViewer().getTable()
											.isDisposed()) {
										adapter.refresh();
										adapter.scrollToEnd();
									}
								}
							});
						}
					}
				});

			}

			@Override
			public void begin(ILogger logger, String msg) {
				queue.addRequest(new Runnable() {
					@Override
					public void run() {
						Display display = Display.getDefault();
						if (!display.isDisposed()) {

							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									if (!adapter.getViewer().getTable()
											.isDisposed()) {
										adapter.clearTable();
									}
								}
							});
						}
					}
				});
			}
		});

		createActions();
		IActionBars actionBars = getViewSite().getActionBars();
		fillMenuBar(actionBars.getMenuManager());
		fillToolBar(actionBars.getToolBarManager());
		IFocusService service = (IFocusService) getSite().getService(
				IFocusService.class);
		service.addFocusTracker(viewer.getTable(),
				"com.mfg.logger.ui.views.AbstractLogView");
		getSite().setSelectionProvider(viewer);
	}

	protected static IContentProvider createContentProvider() {
		return new LogContentProvider();
	}

	protected void createActions() {
		copy = new CopyFromLogAction(getControl());
		clearAction = new ClearLogAction(adapter);
		scrollToEndAction = new LinkToLastMessageAction(adapter);
		scrollToEndAction.setChecked(true);
	}

	protected void fillMenuBar(IMenuManager menuManager) {
		menuManager.add(copy);
		menuManager.add(clearAction);
	}

	@Override
	public void dispose() {
		for (ILogClient client : clients) {
			client.logDisposed(this);
		}
		IFocusService service = (IFocusService) getSite().getService(
				IFocusService.class);
		service.removeFocusTracker(viewer.getTable());
		queue.close();
		super.dispose();
	}

	/**
	 * A shortcut to {@link ILogReader#setFilters(ILogFilter...)}. After set the
	 * filters the view is refreshed.
	 * 
	 * @see ILogReader#setFilters(ILogFilter...)
	 * @param filters
	 */
	public void setFilters(ILogFilter... filters) {
		getLogManager().getReader().setFilters(filters);
		setModel(_model);
	}

	@Override
	public ILogRecord getSelectedRecord() {
		StructuredSelection sel = (StructuredSelection) viewer.getSelection();
		ILogRecord item = (ILogRecord) sel.getFirstElement();
		return item;
	}

	protected void fillToolBar(IToolBarManager toolBar) {
		if (getControl() != null) {
			toolBar.add(new ActionContributionDDL(new PrevLogAction(
					getControl())));
			toolBar.add(new ActionContributionDDL(new NextLogAction(
					getControl())));
			toolBar.add(new LogMenuContributionGotoTime(getControl()));
			toolBar.add(copy);
			getViewSite().getActionBars().setGlobalActionHandler(copy.getId(),
					copy);
		}
		toolBar.add(clearAction);
		toolBar.add(scrollToEndAction);
		// toolBar.add(new Action("Chart") {
		// @Override
		// public void run() {
		// ILogRecord sel = getSelectedRecord();
		// long time = getControl().getTime(sel);
		// long price = getControl().getPrice(sel);
		// for (ILoggerClient client : getLoggerClients()) {
		// client.clientGotoPrice(time, price);
		// }
		// }
		// });

	}

	public Action getClearAction() {
		return clearAction;
	}

	public Action getScrollToEndAction() {
		return scrollToEndAction;
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public LogViewerAdapter getAdapter() {
		return adapter;
	}

	@Override
	public List<ILogClient> getLoggerClients() {
		return clients;
	}

	@Override
	public void connectToClient(final ILogClient client) {
		if (!clients.contains(client)) {
			clients.add(client);
			client.logConnected(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.logger.ui.views.ILogView#disconnectClient(com.mfg.logger.ui.views
	 * .ILoggerClient)
	 */
	@Override
	public void disconnectClient(ILogClient client) {
		clients.remove(client);
	}

	@Override
	public AbstractLoggerViewControl getControl() {
		return control;
	}

	@Override
	public void setControl(AbstractLoggerViewControl aControl) {
		control = aControl;
		fControlID = control.getGUIID();
		// System.out.println("------Log's Control " + fControlID);
	}

	@Override
	public void init(IViewSite aSite, IMemento aMemento)
			throws PartInitException {
		super.init(aSite, aMemento);
		if (aMemento != null) {
			String id = aMemento.getString(MEMENTO_CONTROL_ID);
			if (id != null) {
				fControlID = new Long(id).longValue();
				getControl();
			}
		}
	}

	@Override
	public void saveState(IMemento aMemento) {
		super.saveState(aMemento);
		aMemento.putString(MEMENTO_CONTROL_ID, fControlID + "");
	}

	public long getGUID() {
		return fControlID;
	}

}
