package com.marketforecastgroup.dfsa.ui.views;

import static java.lang.System.out;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import com.marketforecastgroup.dfsa.DFSAPlugin;
import com.mfg.common.DFSException;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.ClientSocket;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.ServerFactory;
import com.mfg.dfs.data.DfsClientsModel;
import com.mfg.dfs.data.IClientStatus;
import com.mfg.dfs.data.IRequestStatus;

public class ClientsView extends ViewPart {
	private static class TreeContentProvider implements ITreeContentProvider,
			Observer {
		private DfsClientsModel model;
		Viewer _viewer;

		public TreeContentProvider() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this._viewer = viewer;
			dispose();
			model = (DfsClientsModel) newInput;
			if (model != null) {
				model.addObserver(this);
			}
		}

		@Override
		public void dispose() {
			if (model != null) {
				model.deleteObserver(this);
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof DfsClientsModel) {
				return ((DfsClientsModel) parent).getClients().toArray();
			}
			if (parent instanceof IClientStatus) {
				return ((IClientStatus) parent).getActiveRequests().toArray();
			}
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			Object[] children = getChildren(element);
			return children != null && children.length > 0;
		}

		@Override
		public void update(Observable o, Object arg) {
			if (_viewer != null) {
				Display display = Display.getDefault();
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						_viewer.refresh();
					}

				});

			}
		}
	}

	public static final String ID = "com.marketforecastgroup.dfsa.ui.views.ClientsView"; //$NON-NLS-1$

	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	TreeViewer _treeViewer;
	private Action _stopAction;

	TreeContentProvider _tcp;

	public ClientsView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			_treeViewer = new TreeViewer(container, SWT.BORDER);
			Tree tree = _treeViewer.getTree();
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					1));
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IClientStatus) {
							IClientStatus s = (IClientStatus) element;
							return s.getLogin() + "@" + s.getRemoteIp();
						}
						return "";
					}
				});
				TreeColumn trclmnClientlogin = treeViewerColumn.getColumn();
				trclmnClientlogin.setWidth(227);
				trclmnClientlogin.setText("Client (Login - IP - Date/Time)");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return ((IRequestStatus) element).getRequest()
									.getSymbol();
						}
						return null;
					}
				});
				TreeColumn trclmnSymbol = treeViewerColumn.getColumn();
				trclmnSymbol.setWidth(118);
				trclmnSymbol.setText("Symbol");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return ((IRequestStatus) element).getRequest()
									.getBarType().name();
						}
						return null;
					}
				});
				TreeColumn trclmnBarType = treeViewerColumn.getColumn();
				trclmnBarType.setWidth(85);
				trclmnBarType.setText("Bar Type");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return ((IRequestStatus) element).getRequest()
									.getNumBarsOrDays() + "";
						}
						return "";
					}
				});
				TreeColumn trclmnBarsRequested = treeViewerColumn.getColumn();
				trclmnBarsRequested.setWidth(117);
				trclmnBarsRequested.setText("Bars Requested");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return ((IRequestStatus) element).getRequest()
									.getBarWidth() + "";
						}
						return null;
					}
				});
				TreeColumn trclmnNumOfBars = treeViewerColumn.getColumn();
				trclmnNumOfBars.setWidth(100);
				trclmnNumOfBars.setText("Bar Width");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return dateFormat.format(new Date(
									((IRequestStatus) element).getRequest()
											.getStartTime()));
						}
						return null;
					}
				});
				TreeColumn trclmnStartTime = treeViewerColumn.getColumn();
				trclmnStartTime.setWidth(162);
				trclmnStartTime.setText("Start Time");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return dateFormat.format(new Date(
									((IRequestStatus) element).getRequest()
											.getEndTime()));
						}
						return "";
					}
				});
				TreeColumn trclmnEndTime = treeViewerColumn.getColumn();
				trclmnEndTime.setWidth(176);
				trclmnEndTime.setText("End Time");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						_treeViewer, SWT.NONE);
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						if (element instanceof IRequestStatus) {
							return ((IRequestStatus) element).getRequest()
									.getReqType().name();
						}
						return null;
					}
				});
				TreeColumn trclmnReqType = treeViewerColumn.getColumn();
				trclmnReqType.setWidth(122);
				trclmnReqType.setText("Req. Type");
			}

			_tcp = new TreeContentProvider();
			_treeViewer.setContentProvider(_tcp);
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		try {
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(IDFS dfs) {
					updateModel(dfs);
				}

				@Override
				public void notReady() {
					// nothing
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	void updateModel(IDFS dfs) {
		DFSAPlugin.getCacheRepo(dfs);
		new Thread(new Runnable() {

			@Override
			public void run() {
				DfsClientsModel model = ServerFactory.getModel();
				while (model == null && !_treeViewer.getTree().isDisposed()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						//
					}
					model = ServerFactory.getModel();
				}

				if (model != null) {
					model.addObserver(_tcp);
				}
				Display display = Display.getDefault();
				if (!display.isDisposed()) {
					final DfsClientsModel fmodel = model;
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							if (!_treeViewer.getTree().isDisposed()) {
								_treeViewer.setInput(fmodel);
							}
						}
					});
				}
			}
		}).start();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			_stopAction = new Action("Stop") {
				@Override
				public void run() {
					killClient();
				}
			};
			_stopAction.setImageDescriptor(ResourceManager
					.getPluginImageDescriptor("org.eclipse.ui",
							"/icons/full/elcl16/stop.gif"));
		}
	}

	protected void killClient() {
		Object sel = ((StructuredSelection) _treeViewer.getSelection())
				.getFirstElement();
		if (sel instanceof ClientSocket) {
			ClientSocket client = (ClientSocket) sel;
			if (MessageDialog.openConfirm(getViewSite().getShell(), "Delete",
					"Do you want to kill client " + client.getLogin() + "?")) {
				out.println("Killing " + client.getRemoteIp());
				ServerFactory.killClient(client.getRemoteIp());
			}
		}
		out.println(sel);
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(_stopAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		_treeViewer.getTree().setFocus();
	}
}
