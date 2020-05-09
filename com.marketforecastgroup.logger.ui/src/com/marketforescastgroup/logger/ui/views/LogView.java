package com.marketforescastgroup.logger.ui.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.marketforecastgroup.logger.ui.LoggerUIPlugin;
import com.marketforescastgroup.logger.ILogManagerListener;
import com.marketforescastgroup.logger.Log;
import com.marketforescastgroup.logger.LogManager;

public class LogView extends ViewPart implements ILogManagerListener {
	public LogView() {
	}

	public static final String ID = "com.marketforescastgroup.logger.views.logview";

	public static final LogManager logger = LogManager.getInstance();

	private ISelection _selection;

	private Table _table;

	TableViewer _tableViewer;

	final String[] _colNames = new String[] { "Date", "Message" };

	Image _logIcon;

	private Action _clearAll;

	private Action _clearSelected;

	private Action _scrollEnabled;

	@Override
	public void createPartControl(final Composite parent) {

		final Composite container = new Composite(parent, SWT.NULL);

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(14, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));

		_table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI);

		final TableColumn tcDate = new TableColumn(_table, SWT.NULL);
		tcDate.setText(_colNames[0]);

		final TableColumn tcMessage = new TableColumn(_table, SWT.NULL);
		tcMessage.setText(_colNames[1]);

		tcDate.setWidth(140);
		tcMessage.setWidth(600);

		_tableViewer = new TableViewer(_table);

		_tableViewer.getTable().setLinesVisible(true);
		_tableViewer.getTable().setHeaderVisible(true);
		final GridData gd_table = new GridData(GridData.FILL_BOTH);
		gd_table.verticalSpan = 3;
		_tableViewer.getTable().setLayoutData(gd_table);

		_tableViewer.setContentProvider(new IStructuredContentProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(final Object inputElement) {
				final List<Log> log = (List<Log>) inputElement;
				final org.eclipse.swt.graphics.Color infoColor = Display
						.getDefault().getSystemColor(SWT.COLOR_WHITE);
				final org.eclipse.swt.graphics.Color warningColor = Display
						.getDefault().getSystemColor(SWT.COLOR_YELLOW);
				final org.eclipse.swt.graphics.Color errorColor = Display
						.getDefault().getSystemColor(SWT.COLOR_RED);
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {

						for (int i = 0; i < log.size(); i++) {
							final org.eclipse.swt.graphics.Color color;
							switch (log.get(i).getMessageType()) {
							case INFO:
								color = infoColor;
								break;
							case WARNING:
								color = warningColor;
								break;

							case ERROR:
								color = errorColor;
								break;

							default:
								color = infoColor;
								break;
							}
							_tableViewer.getTable().getItem(i)
									.setBackground(color);
						}
					}
				});

				return log.toArray();
			}

			@Override
			public void dispose() {
				logger.INFO("disposing ...");
			}

			@Override
			public void inputChanged(final Viewer viewer,
					final Object oldInput, final Object newInput) {
				logger.INFO("input changed: old=" + oldInput + ", new="
						+ newInput);
			}
		});

		// Sets the label provider.
		_tableViewer.setLabelProvider(new ITableLabelProvider() {
			@Override
			public Image getColumnImage(final Object element,
					final int columnIndex) {
				if (_logIcon == null) {
					_logIcon = LoggerUIPlugin.getImageDescriptor(
							"icons/logviewer.png").createImage();
				}
				if (columnIndex == 0) {
					return _logIcon;
				}
				return null;
			}

			@Override
			public String getColumnText(final Object element,
					final int columnIndex) {
				final Log log = (Log) element;
				switch (columnIndex) {
				case 0:
					return log.getDate();
				case 1:
					return log.getMessage();
				}
				return null;
			}

			@Override
			public void addListener(final ILabelProviderListener listener) {
				//
			}

			@Override
			public void dispose() {
				//
			}

			@Override
			public boolean isLabelProperty(final Object element,
					final String property) {
				return false;
			}

			@Override
			public void removeListener(final ILabelProviderListener listener) {
				//
			}
		});

		_tableViewer.setColumnProperties(_colNames);

		// LogManager.getInstance().restore();

		_tableViewer.setInput(LogManager.getInstance().getLogs());

		container.setLayout(new GridLayout(1, false));

		initialize();

		makeActions();
		contributeToActionBars();
		LogManager.getInstance().setLogManagerListener(this);
	}

	private void initialize() {
		if (_selection != null && _selection.isEmpty() == false
				&& _selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) _selection;
			if (ssel.size() > 1) {
				return;
				// Object obj = ssel.getFirstElement();
				// if (obj instanceof IResource) {
				// IContainer container;
				// if (obj instanceof IContainer)
				// container = (IContainer) obj;
				// else
				// container = ((IResource) obj).getParent();
				// }
			}
		}
	}

	class SymbolSorter extends ViewerSorter {
		private int propertyIndex;

		public SymbolSorter(final String sortByProperty) {
			for (int i = 0; i < _colNames.length; i++) {
				if (_colNames[i].equals(sortByProperty)) {
					this.propertyIndex = i;
					return;
				}
			}

			throw new IllegalArgumentException("Unrecognized property: "
					+ sortByProperty);
		}

		@Override
		public int compare(final Viewer viewer, final Object obj1,
				final Object obj2) {
			final Log log_1 = (Log) obj1;
			final Log log_2 = (Log) obj2;

			switch (propertyIndex) {
			case 0:
				return log_1.getDate().compareTo(log_2.getDate());
			case 1:
				return log_1.getMessage().compareTo(log_2.getMessage());
			default:
				return 0;
			}
		}
	}

	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_clearAll);
		manager.add(_clearSelected);
		manager.add(_scrollEnabled);
	}

	private void makeActions() {
		_clearAll = new Action() {
			@Override
			public void run() {
				LogManager.getInstance().clear();
				_tableViewer.refresh();
			}
		};
		_clearAll.setText("Clear All");
		_clearAll.setToolTipText("Clear All");
		_clearAll.setImageDescriptor(LoggerUIPlugin
				.getImageDescriptor("/icons/clearall.png"));

		_clearSelected = new Action() {
			@Override
			public void run() {
				final ISelection selection1 = _tableViewer.getSelection();
				final Iterator<Object> sels = ((IStructuredSelection) selection1)
						.iterator();
				while (sels.hasNext()) {
					final Object obj = sels.next();
					if (obj instanceof Log) {
						final Log log = (Log) obj;
						LogManager.getInstance().removeLog(log);
						_tableViewer.refresh();
					}
				}

			}
		};
		_clearSelected.setText("Clear Selected");
		_clearSelected.setToolTipText("Clear Selected");
		_clearSelected.setImageDescriptor(LoggerUIPlugin
				.getImageDescriptor("/icons/clear.png"));

		_scrollEnabled = new Action("Show Last Message", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				// nothing
			}
		};
		_scrollEnabled.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		_scrollEnabled.setChecked(true);

	}

	@Override
	public void setFocus() {
		//
	}

	@Override
	public void logAdded(final Log log) {
		_tableViewer.refresh();
		if (_scrollEnabled.isChecked()) {
			_table.setTopIndex(_table.getItemCount());
		}
	}

	@Override
	public void logRemoved(final Log log) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				_tableViewer.refresh();
			}
		});
	}

	@Override
	public void dispose() {
		LogManager.getInstance().setLogManagerListener(null);
		super.dispose();
	}
}
