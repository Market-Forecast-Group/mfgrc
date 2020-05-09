package com.marketforecastgroup.dfsa.ui.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;
import com.mfg.utils.ui.IViewWithTable;
import com.mfg.utils.ui.actions.CopyStructuredSelectionAction;

public class MaturityBarsView extends ViewPart implements IViewWithTable {

	// private static class CacheContentProvider implements ILazyContentProvider
	// {
	//
	// private IBarCache cache;
	// private TableViewer viewer;
	//
	// public CacheContentProvider(IBarCache cache1) {
	// this.cache = cache1;
	// }
	//
	// @Override
	// public void dispose() {
	// }
	//
	// @Override
	// public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	// {
	// this.viewer = (TableViewer) viewer;
	// this.viewer.setItemCount(cache.size());
	// }
	//
	// @Override
	// public void updateElement(int index) {
	// Bar bar;
	// try {
	// bar = cache.getBar(index);
	// viewer.replace(bar, index);
	// } catch (DFSException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	public static final String ID = "com.marketforecastgroup.dfsa.ui.views.maturityBarsView"; //$NON-NLS-1$
	Table _table;
	TableViewer _tableViewer;
	final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private Text textPage;
	IBarCache _cache;
	int _page;
	Text _barWidthText;
	BarType _barType;
	Maturity _maturity;
	String _symbol;
	int _numOfBars;

	public MaturityBarsView() {
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
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayout(new GridLayout(8, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
			{
				Label lblBarWidth = new Label(composite, SWT.NONE);
				lblBarWidth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
				lblBarWidth.setText("Bar Width");
			}
			{
				_barWidthText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
				_barWidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, false, 1, 1));
			}
			{
				Button btnChange = new Button(composite, SWT.NONE);
				btnChange.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							changeBarWidth();
						} catch (DFSException e1) {
							e1.printStackTrace();
							throw new RuntimeException(e1);
						}
					}
				});
				btnChange.setText("Change");
			}

			Button btnFirst = new Button(composite, SWT.NONE);
			btnFirst.setToolTipText("Go to first page.");
			btnFirst.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					try {
						loadPage(0);
					} catch (DFSException e) {
						e.printStackTrace();
					}
				}
			});
			btnFirst.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
					false, 1, 1));
			btnFirst.setImage(ResourceManager.getPluginImage(
					"com.marketforecastgroup.dfsa", "icons/first.png"));

			Button btnPrev = new Button(composite, SWT.NONE);
			btnPrev.setToolTipText("Go to previous page.");
			btnPrev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					try {
						loadPage(_page - 1);
					} catch (DFSException e) {
						e.printStackTrace();
					}
				}
			});
			btnPrev.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			btnPrev.setImage(ResourceManager.getPluginImage(
					"com.marketforecastgroup.dfsa", "icons/previous.png"));

			textPage = new Text(composite, SWT.BORDER | SWT.READ_ONLY
					| SWT.CENTER);
			textPage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));

			Button btnNext = new Button(composite, SWT.NONE);
			btnNext.setToolTipText("Go to next page.");
			btnNext.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					try {
						loadPage(_page + 1);
					} catch (DFSException e) {
						e.printStackTrace();
					}
				}
			});
			btnNext.setImage(ResourceManager.getPluginImage(
					"com.marketforecastgroup.dfsa", "icons/next.png"));

			Button btnLast = new Button(composite, SWT.NONE);
			btnLast.setToolTipText("Go to last page.");
			btnLast.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					try {
						loadPage(_cache.size() / 100);
					} catch (DFSException e) {
						e.printStackTrace();
					}
				}
			});
			btnLast.setImage(ResourceManager.getPluginImage(
					"com.marketforecastgroup.dfsa", "icons/last.png"));
		}
		{
			_tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
			_table = _tableViewer.getTable();
			_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
					1, 1));
			_table.setLinesVisible(true);
			_table.setHeaderVisible(true);
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public void update(ViewerCell cell) {
						super.update(cell);
						TableItem item = (TableItem) cell.getViewerRow()
								.getItem();
						int i = _table.indexOf(item);
						cell.setText(Integer.toString(i));
					}
				});
				TableColumn tblclmnIndex = tableViewerColumn.getColumn();
				tblclmnIndex.setWidth(100);
				tblclmnIndex.setText("Index");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return dateFormat.format(new Date(((Bar) element)
								.getTime()));
					}
				});
				TableColumn tblclmnTime = tableViewerColumn.getColumn();
				tblclmnTime.setWidth(100);
				tblclmnTime.setText("Time");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return Integer.toString(((Bar) element).getOpen());
					}
				});
				TableColumn tblclmnOpen = tableViewerColumn.getColumn();
				tblclmnOpen.setWidth(100);
				tblclmnOpen.setText("Open");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return Integer.toString(((Bar) element).getHigh());
					}
				});
				TableColumn tblclmnHigh = tableViewerColumn.getColumn();
				tblclmnHigh.setWidth(100);
				tblclmnHigh.setText("High");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return Integer.toString(((Bar) element).getLow());
					}
				});
				TableColumn tblclmnLow = tableViewerColumn.getColumn();
				tblclmnLow.setWidth(100);
				tblclmnLow.setText("Low");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return Integer.toString(((Bar) element).getClose());
					}
				});
				TableColumn tblclmnClose = tableViewerColumn.getColumn();
				tblclmnClose.setWidth(100);
				tblclmnClose.setText("Close");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						_tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return Integer.toString(((Bar) element).getVolume());
					}
				});
				TableColumn tblclmnVolume = tableViewerColumn.getColumn();
				tblclmnVolume.setWidth(100);
				tblclmnVolume.setText("Volume");
			}
			_tableViewer.setContentProvider(new ArrayContentProvider());
		}

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	protected void changeBarWidth() throws DFSException {
		int nunits = 0;
		// ask to the user for the bar width
		int max = 0;
		switch (_barType) {
		case DAILY:
			max = 30;
			break;
		case MINUTE:
			max = 240;
			break;
		case RANGE:
			nunits = 1;
		}
		if (nunits == 0) {
			final int fmax = max;
			InputDialog dlg = new InputDialog(getViewSite().getShell(),
					"Bar Width", "Enter the bar width for the mautrity "
							+ _maturity.toFileString(),
					_barWidthText.getText(), new IInputValidator() {

						@Override
						public String isValid(String newText) {
							try {
								int n = Integer.parseInt(newText);
								if (n <= 0) {
									return "The minimum bar width is 1";
								}
								if (n > fmax) {
									return "The maximum bar width for the bar "
											+ _barType + " is " + fmax;
								}
							} catch (Exception e) {
								return e.getMessage();
							}
							return null;
						}
					});
			if (dlg.open() == Window.OK) {
				nunits = Integer.parseInt(dlg.getValue());
			}
		}
		if (nunits > 0) {
			final int fnunits = nunits;
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(final IDFS dfs) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							updateContent(_symbol, _maturity, _barType,
									_numOfBars, fnunits, dfs);
							_barWidthText.setText(Integer.toString(fnunits));
							_tableViewer.refresh();
						}
					});
				}

				@Override
				public void notReady() {
					// nothing
				}
			});

		}
	}

	public void updateContent(String symbol, Maturity maturity, BarType type,
			int numOfBars, int numberOfUnits, IDFS dfs) {
		_symbol = symbol;
		_numOfBars = numberOfUnits;
		_barType = type;
		_maturity = maturity;

		_barWidthText.setText(Integer.toString(numberOfUnits));

		// Lino, the cache should be closed
		if (_cache != null) {
			try {
				_cache.close();
			} catch (DFSException e) {
				// ARIANTODO: maybe you could make a warning sign
				e.printStackTrace();
			}
		}

		try {
			setPartName("Maturity Bars (" + symbol + " - "
					+ maturity.toFileString() + " - " + type + ")");
			if (numOfBars > 0) {
				_cache = dfs.getCache(symbol, maturity, type, numberOfUnits);
			} else {
				_cache = null;
			}
			loadPage(0);
		} catch (DFSException e) {
			e.printStackTrace();
		}
	}

	void loadPage(int page) throws DFSException {
		if (page < 0) {
			return;
		}

		int size = _cache.size();

		if (page * 100 >= size) {
			return;
		}

		this._page = page;

		if (_page < 0) {
			_page = 0;
		}

		int first = _page * 100;
		int last = (_page + 1) * 100;

		if (first < 0) {
			first = 0;
		}
		Bar[] input;
		if (_cache == null || size == 0 || first >= size) {
			input = new Bar[0];
		} else {
			if (last > size) {
				last = size;
			}
			if (first >= last) {
				first = last - 1;
			}

			input = new Bar[last - first];

			for (int i = first; i < last; i++) {
				input[i - first] = _cache.getBar(i);
			}
		}
		textPage.setText(Integer.toString(_page));
		_tableViewer.setInput(input);
	}

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
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				new CopyStructuredSelectionAction());
		actionBars.updateActionBars();
	}

	@Override
	public void setFocus() {
		_tableViewer.getTable().setFocus();
	}

	@Override
	public Table getTable() {
		return _tableViewer.getTable();
	}
}
