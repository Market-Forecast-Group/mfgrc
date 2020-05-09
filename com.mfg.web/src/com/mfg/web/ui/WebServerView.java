package com.mfg.web.ui;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.web.WebPlugin;
import com.mfg.web.WebServer;

public class WebServerView extends ViewPart implements Listener {

	public static final String ID = "com.mfg.web.ui.webserver"; //$NON-NLS-1$
	private Action _startServerAction;
	private WebServer _server;
	private ArrayList<Object[]> _log;
	private TableViewer _tableViewer;

	public WebServerView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		container.setLayout(gl_container);

		_tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		Table table = _tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		{
			TableViewerColumn tableViewerColumn = new TableViewerColumn(
					_tableViewer, SWT.NONE);
			tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return super.getText(Array.get(element, 0));
				}
			});
			TableColumn tblclmnMessage = tableViewerColumn.getColumn();
			tblclmnMessage.setWidth(461);
			tblclmnMessage.setText("Message");
		}
		{
			TableViewerColumn tableViewerColumn = new TableViewerColumn(
					_tableViewer, SWT.NONE);
			tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					return super.getText(Array.get(element, 1));
				}
			});
			TableColumn tblclmnDate = tableViewerColumn.getColumn();
			tblclmnDate.setWidth(100);
			tblclmnDate.setText("Date");
		}
		_tableViewer.setContentProvider(new ArrayContentProvider());

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		_log = new ArrayList<>();
		_tableViewer.setInput(_log);
		_server = WebPlugin.getDefault().getServer();
		_server.addListener(this);
	}

	@Override
	public void dispose() {
		_server.removeListener(this);
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			_startServerAction = new Action("Start Server") {
				@Override
				public void run() {
					WebPlugin.getDefault().switchServer();
				}
			};
			_startServerAction.setImageDescriptor(ResourceManager
					.getPluginImageDescriptor("com.mfg.symbols",
							"icons/play.gif"));
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(_startServerAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		_tableViewer.getTable().setFocus();
	}

	private void updateButtonStatus() {
		ImageDescriptor playImg = ResourceManager.getPluginImageDescriptor(
				"com.mfg.symbols", "icons/play.gif");
		ImageDescriptor stopImg = ResourceManager.getPluginImageDescriptor(
				"com.mfg.symbols", "icons/stop.gif");
		_startServerAction.setImageDescriptor(_server.isRunning() ? stopImg
				: playImg);
	}

	private void log(String msg) {
		_log.add(new Object[] { msg, LocalTime.now().toString() });
		Display.getDefault().asyncExec(() -> {
			_tableViewer.refresh();
			int index = Math.max(0, _log.size() - 30);
			_tableViewer.getTable().setTopIndex(index);
		});
	}

	@Override
	public void lifeCycleStarting(LifeCycle event) {
		log("Starting server at port " + _server.getPort());
	}

	@Override
	public void lifeCycleStarted(LifeCycle event) {
		Display.getDefault().asyncExec(this::updateButtonStatus);
		int port = _server.getPort();
		log("Server running at port " + port);
		log("There available the following host addresses:");
		try {
			ArrayList<NetworkInterface> list = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface iface : list) {
				ArrayList<InetAddress> addressList = Collections.list(iface
						.getInetAddresses());
				if (!addressList.isEmpty()) {
					for (InetAddress address : addressList) {
						log("    " + address.getHostAddress() + ":" + port
								+ " " + address.getHostName() + " ("
								+ iface.getDisplayName() + ")");
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			log(e.getMessage());
		}
	}

	@Override
	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		log("Error " + (cause == null ? "(unknown)" : cause.getMessage()));
	}

	@Override
	public void lifeCycleStopping(LifeCycle event) {
		log("Stopping server");
	}

	@Override
	public void lifeCycleStopped(LifeCycle event) {
		Display.getDefault().asyncExec(this::updateButtonStatus);
		log("Server stopped");
	}
}
