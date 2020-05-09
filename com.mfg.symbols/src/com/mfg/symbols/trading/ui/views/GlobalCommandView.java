/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.trading.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.persistence.ISymbolStorageReference;
import com.mfg.symbols.jobs.InputPipeChangeEvent;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobChangeAdapter;
import com.mfg.symbols.jobs.SymbolJobChangeEvent;
import com.mfg.symbols.jobs.TradingPipeChangeEvent;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.ui.ConfigurationSetLabelProvider;
import com.mfg.symbols.ui.ConfigurationSetsManager;
import com.mfg.symbols.ui.WorkbenchSymbolsSelectionListener;
import com.mfg.symbols.ui.widgets.ConfigurationFullnameComparator;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.ImageUtils;

/**
 * @author arian
 * 
 */
public class GlobalCommandView extends ViewPart {
	private static class Sorter extends ViewerSorter {
		ConfigurationFullnameComparator comp = new ConfigurationFullnameComparator();

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return comp.compare(e1, e2);
		}
	}

	/**
	 * 
	 */
	private static final String MEMENTO_SELECTED_CONFIGURATION_IDS = "selectedConfigurationIds";

	/**
	 * 
	 */
	private static final String MEMENTO_SELECTED_ITEMS = "selectedItems";

	/**
	 * 
	 */
	private static final String ICON_WARMING_UP_FINISHED_PATH = "icons/warming-up-finished.png";

	/**
	 * 
	 */
	private static final String ICON_WARMING_UP_PATH = "icons/warming-up.png";

	private static final int PLAY_STOP_BUTTON_COL = 4;

	private static final String ICON_STOP_PATH = "icons/stop.gif";
	private static final String ICON_PLAY_PATH = "icons/play.gif";
	static final String ICON_PLAY_DISABLE_PATH = "icons/play-disable.gif";

	public static final String ID = "com.mfg.symbols.trading.ui.views.GlobalCommandView"; //$NON-NLS-1$

	private static final int TRADING_COLOR_COL = 1;

	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	Table table;
	private WorkspaceStorageAdapter _storageListener;
	TableViewer tableViewer;
	private SymbolJobChangeAdapter jobListener;
	private Button btnStartStop;
	private Button btnStopAll;
	private List<SimpleStorage<?>> _Symbol_Input_Trading_Storages;

	private WorkbenchSymbolsSelectionListener workbenchSelectionListener;

	private ArrayList<Integer> initialCheckedIndexes;

	private ArrayList<String> initialSelectedConfigurationIds;
	Composite tableComposite;

	private final HashMap<Object, Boolean> checkMap;

	private List<Control> editorControls;

	public GlobalCommandView() {
		checkMap = new HashMap<>();
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@SuppressWarnings("synthetic-access")
	// Direct access to Sorter object.
	@Override
	public void createPartControl(Composite parent) {
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				new WorkbenchJob(Display.getDefault(), "") {

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						tableComposite.layout();
						table.layout();
						return Status.OK_STATUS;
					}
				}.schedule(10);
			}
		});
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.marginWidth = 0;
		gl_parent.verticalSpacing = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);

		Composite composite = formToolkit.createComposite(parent, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		formToolkit.paintBordersFor(composite);
		{
			tableComposite = new Composite(composite, SWT.NONE);
			tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true, 1, 1));
			formToolkit.adapt(tableComposite);
			formToolkit.paintBordersFor(tableComposite);
			TableColumnLayout tcl_tableComposite = new TableColumnLayout();
			tableComposite.setLayout(tcl_tableComposite);
			{
				tableViewer = new TableViewer(tableComposite, SWT.BORDER
						| SWT.FULL_SELECTION);
				tableViewer.setSorter(new Sorter());
				table = tableViewer.getTable();
				table.setLinesVisible(true);
				table.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseUp(MouseEvent e) {
						Point point = new Point(e.x, e.y);
						handleClickedTableAt(point, e.button);
					}
				});
				formToolkit.paintBordersFor(table);
				{
					TableViewerColumn tableViewerColumn = new TableViewerColumn(
							tableViewer, SWT.NONE);
					tableViewerColumn
							.setLabelProvider(new ColumnLabelProvider() {
								@Override
								public Image getImage(Object element) {
									return null;
								}

								@Override
								public String getText(Object element) {
									return "";
								}

							});
					TableColumn tblclmnSelected = tableViewerColumn.getColumn();
					tcl_tableComposite.setColumnData(tblclmnSelected,
							new ColumnWeightData(1, 27, false));
					tblclmnSelected.setResizable(false);
					tblclmnSelected.setAlignment(SWT.CENTER);
					tblclmnSelected.setText("Selected");
				}
				{
					TableViewerColumn tableViewerColumn = new TableViewerColumn(
							tableViewer, SWT.NONE);
					tableViewerColumn
							.setLabelProvider(new ColumnLabelProvider() {
								@Override
								public Image getImage(Object element) {
									if (element instanceof TradingConfiguration) {
										return getConfigurationSetIcon(element);
									}
									return null;
								}

								@Override
								public String getText(Object element) {
									return null;
								}
							});
					TableColumn tblclmnSetcolor = tableViewerColumn.getColumn();
					tcl_tableComposite.setColumnData(tblclmnSetcolor,
							new ColumnWeightData(1, 20, false));
					tblclmnSetcolor.setResizable(false);
					tblclmnSetcolor.setAlignment(SWT.CENTER);
					tblclmnSetcolor.setText("setColor");
				}
				{
					TableViewerColumn tableViewerColumn = new TableViewerColumn(
							tableViewer, SWT.NONE);
					tableViewerColumn
							.setLabelProvider(new ColumnLabelProvider() {
								// private Font symbolFont;

								@Override
								public Image getImage(Object element) {
									return null;
								}

								@Override
								public String getText(Object element) {
									return getConfigurationName(element);
								}

								// @Override
								// public Color getBackground(Object element) {
								// return getConfigurationBackground(element);
								// }

								// @Override
								// public Font getFont(Object element) {
								// if (symbolFont == null) {
								// FontData fontData = table.getFont()
								// .getFontData()[0];
								// symbolFont = new Font(Display
								// .getDefault(), fontData
								// .getName(),
								// (int) fontData.height, SWT.BOLD);
								// }
								// return element instanceof SymbolConfiguration
								// ? symbolFont
								// : super.getFont(element);
								// }
								//
								// @Override
								// public void dispose() {
								// super.dispose();
								// if (symbolFont != null) {
								// symbolFont.dispose();
								// }
								// }
							});
					TableColumn tblclmnConfiguration = tableViewerColumn
							.getColumn();
					tcl_tableComposite.setColumnData(tblclmnConfiguration,
							new ColumnWeightData(1000, 60, false));
					tblclmnConfiguration.setText("Configuration");
				}
				{
					TableViewerColumn tableViewerColumn = new TableViewerColumn(
							tableViewer, SWT.NONE);
					tableViewerColumn
							.setLabelProvider(new ColumnLabelProvider() {
								@Override
								public Image getImage(Object element) {
									if (SymbolJob
											.isConfigurationRunning(element)) {
										Job[] jobs = Job.getJobManager().find(
												element);
										SymbolJob<?> job = (SymbolJob<?>) jobs[0];
										if (job.getDataSource().isInWarmUp()) {
											return ImageUtils.getBundledImage(
													SymbolsPlugin.getDefault(),
													ICON_WARMING_UP_PATH);
										}
										return ImageUtils.getBundledImage(
												SymbolsPlugin.getDefault(),
												ICON_WARMING_UP_FINISHED_PATH);
									}
									return null;
								}

								@Override
								public String getText(Object element) {
									return "";
								}
							});
					TableColumn tblclmnWarmingup = tableViewerColumn
							.getColumn();
					tcl_tableComposite.setColumnData(tblclmnWarmingup,
							new ColumnWeightData(1, 20, false));
					tblclmnWarmingup.setText("WarmingUp");
				}
				{
					TableViewerColumn tableViewerColumn = new TableViewerColumn(
							tableViewer, SWT.CENTER);
					tableViewerColumn
							.setLabelProvider(new ColumnLabelProvider() {
								@Override
								public Image getImage(Object element) {
									return getConfgurationButtonIcon(element);
								}

								@Override
								public String getText(Object element) {
									return "";
								}
							});
					TableColumn tblclmnCommands = tableViewerColumn.getColumn();
					tcl_tableComposite.setColumnData(tblclmnCommands,
							new ColumnWeightData(1, 20, false));
					tblclmnCommands.setResizable(false);
					tblclmnCommands.setAlignment(SWT.CENTER);
					tblclmnCommands.setText("Commands");
				}
				tableViewer.setContentProvider(new ArrayContentProvider());
			}
		}
		{
			Composite composite_1 = formToolkit.createComposite(composite,
					SWT.NONE);
			composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
			formToolkit.paintBordersFor(composite_1);
			composite_1.setLayout(new GridLayout(3, false));
			{
				btnStartStop = formToolkit.createButton(composite_1, "",
						SWT.NONE);
				btnStartStop.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						playAll();
					}
				});
				btnStartStop.setImage(ResourceManager.getPluginImage(
						"com.mfg.symbols", "icons/play.gif"));
			}
			{
				btnStopAll = new Button(composite_1, SWT.NONE);
				btnStopAll.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						stopAll();
					}
				});
				btnStopAll.setImage(ResourceManager.getPluginImage(
						"com.mfg.symbols", "icons/stop.gif"));
				formToolkit.adapt(btnStopAll, true, true);
			}
			{
				Button btnConfigure = formToolkit.createButton(composite_1,
						"Configure", SWT.NONE);
				btnConfigure.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						handleConfigureContent();
					}
				});
				btnConfigure.setImage(null);
				btnConfigure.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						true, false, 1, 1));
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreateWidgets();
	}

	/**
	 * 
	 */
	protected void handleConfigureContent() {
		@SuppressWarnings("unchecked")
		final List<Object> selected = (List<Object>) tableViewer.getInput();
		Shell shell = getSite().getShell();
		SelectGlobalCommandConfigurationsDialog dlg = new SelectGlobalCommandConfigurationsDialog(
				shell);
		dlg.setCheckedElements(selected);

		if (dlg.open() == Window.OK) {
			tableViewer.setInput(dlg.getResult());
			updateCheckboxes();
		}
	}

	void stopAll() {
		List<Object> list = new ArrayList<>();
		TableItem[] items = tableViewer.getTable().getItems();

		for (TableItem i : items) {
			Object elem = i.getData();
			if (isChecked(elem)) {
				list.add(elem);
			}
		}
		SymbolJob.stopConfigurations(list);
	}

	void playAll() {
		TableItem[] items = tableViewer.getTable().getItems();
		List<Object> list = new ArrayList<>();
		for (TableItem i : items) {
			Object element = i.getData();
			if (isChecked(element)) {
				if (!SymbolJob.isConfigurationRunning(element)) {
					list.add(element);
				}
			}
		}
		SymbolJob.runConfigurations(list, null);
	}

	private void afterCreateWidgets() {
		workbenchSelectionListener = new WorkbenchSymbolsSelectionListener(
				false);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection sel = (StructuredSelection) event
						.getSelection();
				for (Object obj : sel.toArray()) {
					try {
						UIPlugin.getDefault();
						UIPlugin.openEditor(obj);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});

		List<SimpleStorage<?>> symbolStorages = new ArrayList<>();
		List<IWorkspaceStorageReference> references = PersistInterfacesPlugin
				.getDefault().getStorageRefrences();
		for (IWorkspaceStorageReference ref : references) {
			if (ref instanceof ISymbolStorageReference) {
				symbolStorages.add(ref.getStorage());
			}
		}

		tableViewer.setInput(getWorkspaceConfigurations(symbolStorages));
		restoreCheckedItems();

		_storageListener = new WorkspaceStorageAdapter() {
			@Override
			public void objectRemoved(IWorkspaceStorage storage, Object obj) {
				List<?> list = new ArrayList<>((List<?>) tableViewer.getInput());
				if (list.remove(obj)) {
					tableViewer.setInput(list);
					updateCheckboxes();
				}
			}

			@Override
			public void storageChanged(IWorkspaceStorage storage) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (!tableViewer.getControl().isDisposed()) {
							tableViewer.refresh();
						}
					}
				});

			}
		};
		jobListener = new SymbolJobChangeAdapter() {
			@Override
			public void running(IJobChangeEvent event) {
				refreshEvent(event);
			}

			@Override
			public void done(IJobChangeEvent event) {
				refreshEvent(event);
			}

			@Override
			public void tradingStopped(TradingPipeChangeEvent event) {
				refreshTable();
			}

			@Override
			public void tradingRestarted(TradingPipeChangeEvent event) {
				refreshTable();
			}

			@Override
			public void warmingUpFinished(SymbolJobChangeEvent event) {
				refreshTable();
			}

			@Override
			public void inputStopped(InputPipeChangeEvent event) {
				refreshTable();
			}

			private void refreshEvent(IJobChangeEvent event) {
				if (event.getJob() instanceof SymbolJob) {
					refreshTable();
				}
			}
		};
		_Symbol_Input_Trading_Storages = new ArrayList<>();
		_Symbol_Input_Trading_Storages.addAll(symbolStorages);
		_Symbol_Input_Trading_Storages.add(SymbolsPlugin.getDefault()
				.getInputsStorage());
		_Symbol_Input_Trading_Storages.add(SymbolsPlugin.getDefault()
				.getTradingStorage());

		for (SimpleStorage<?> s : _Symbol_Input_Trading_Storages) {
			s.addStorageListener(_storageListener);
		}

		SymbolJob.getManager().addJobChangeListener(jobListener);

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		table.setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);
	}

	boolean isChecked(Object obj) {
		return checkMap.containsKey(obj) && checkMap.get(obj).booleanValue();
	}

	void setChecked(Object obj, boolean checked) {
		checkMap.put(obj, Boolean.valueOf(checked));
	}

	/**
	 * 
	 */
	void updateCheckboxes() {
		if (editorControls == null) {
			editorControls = new ArrayList<>();
		} else {
			for (Control b : editorControls) {
				if (!b.isDisposed()) {
					b.dispose();
				}
			}
			editorControls.clear();
		}
		for (final TableItem item : table.getItems()) {
			Composite control = new Composite(table, SWT.None);
			FillLayout layout = new FillLayout();
			layout.marginWidth = 5;
			control.setLayout(layout);
			control.setBackground(table.getBackground());
			final Button b = new Button(control, SWT.CHECK);
			b.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setChecked(item.getData(), !isChecked(item.getData()));
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// Documenting empty method to avoid warning.
				}
			});
			b.setSelection(isChecked(item.getData()));
			b.setBackground(table.getBackground());
			b.pack();
			editorControls.add(control);
			TableEditor editor = new TableEditor(table);
			Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			editor.minimumWidth = size.x;
			editor.minimumHeight = size.y;
			editor.horizontalAlignment = SWT.RIGHT;
			editor.verticalAlignment = SWT.CENTER;
			editor.setEditor(control, item, 0);
		}
	}

	/**
	 * 
	 */
	private void restoreCheckedItems() {
		List<Object> input = new ArrayList<>();
		for (String id : initialSelectedConfigurationIds) {
			try {
				IStorageObject config = PersistInterfacesPlugin.getDefault()
						.findById(UUID.fromString(id));
				if (config != null) {
					input.add(config);
					setChecked(input, true);
				}
			} catch (IllegalArgumentException e) {
				// Documenting empty method to avoid warning.
			}
		}
		if (!input.isEmpty()) {
			tableViewer.setInput(input);
		}

		for (int i : initialCheckedIndexes) {
			try {
				TableItem item = table.getItem(i);
				setChecked(item.getData(), true);
			} catch (Exception e) {
				// maybe there is a corrupted workspace state.
			}
		}
		updateCheckboxes();
	}

	void refreshTable() {
		Display display = Display.getDefault();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					try {
						if (!tableViewer.getControl().isDisposed()) {
							tableViewer.refresh();
						}
					} catch (NullPointerException e) {
						// aync updates always has surprises.
					}
				}
			});
		}
	}

	private static List<Object> getWorkspaceConfigurations(
			List<SimpleStorage<?>> symbolStorages) {
		ArrayList<Object> tableInput = new ArrayList<>();
		for (SimpleStorage<?> storage : symbolStorages) {
			SimpleStorage<?> storage2 = storage;
			for (IStorageObject symbol : storage2.getObjects()) {
				tableInput.add(symbol);
				InputConfiguration[] inputs = SymbolsPlugin.getDefault()
						.getInputsStorage()
						.findBySymbol((SymbolConfiguration<?, ?>) symbol);
				for (InputConfiguration input : inputs) {
					tableInput.add(input);
					tableInput.addAll(SymbolsPlugin.getDefault()
							.getTradingStorage().findByInput(input));
				}
			}
		}
		return tableInput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		// TODO: remember to change this to use only the symbols, inputs and
		// trading storages.
		for (IWorkspaceStorage s : _Symbol_Input_Trading_Storages) {
			if (s instanceof SimpleStorage) {
				s.removeStorageListener(_storageListener);
			}
		}
		SymbolJob.getManager().removeJobChangeListener(jobListener);
		super.dispose();
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
		@SuppressWarnings("unused")
		// toolbarManager is used in commented code, maybe it'll be needed some
		// time.
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		// toolbarManager
		// .add(new OpenTradingViewAction(null,
		// AccountManagerView2.VIEW_ID, "Account Manager",
		// SymbolsPlugin.PLUGIN_ID,
		// SymbolsPlugin.STRATEGY_LOG_IMAGE_PATH) {
		// @Override
		// public TradingConfiguration getConfiguration() {
		// StructuredSelection sel = (StructuredSelection) tableViewer
		// .getSelection();
		// return (TradingConfiguration) (sel.isEmpty() ? null
		// : sel.getFirstElement());
		// }
		// });
		// toolbarManager.add(new ShowTradingInChartAction(null) {
		// @Override
		// public TradingConfiguration getConfiguration() {
		// StructuredSelection sel = (StructuredSelection) tableViewer
		// .getSelection();
		// return (TradingConfiguration) (sel.isEmpty() ? null : sel
		// .getFirstElement());
		// }
		// });
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		tableViewer.getTable().setFocus();
	}

	static Image getConfigurationSetIcon(Object element) {
		SymbolsPlugin.getDefault().getSetsManager();
		return ConfigurationSetsManager
				.getImage(((TradingConfiguration) element).getInfo()
						.getConfigurationSet());
	}

	static String getConfigurationName(Object element) {

		String name = SymbolsPlugin.getDefault().getFullConfigurationName(
				(IStorageObject) element);
		String prefix = "";
		// if (element instanceof InputConfiguration) {
		// prefix = "  ";
		// } else if (element instanceof TradingConfiguration) {
		// prefix = "    ";
		// }
		return prefix + name;
	}

	static Image getConfgurationButtonIcon(Object element) {
		String iconPath;
		if (SymbolJob.isConfigurationRunning(element)) {
			iconPath = ICON_STOP_PATH;
		} else {
			iconPath = SymbolJob.canRunConfiguration(element) ? ICON_PLAY_PATH
					: ICON_PLAY_DISABLE_PATH;
		}
		return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(), iconPath);
	}

	/**
	 * @param point
	 * @param button
	 */
	void handleClickedTableAt(Point point, int button) {
		ViewerCell cell = tableViewer.getCell(point);
		if (button == 1) {
			if (cell == null) {
				TableItem item = tableViewer.getTable().getItem(point);
				checkItem(item);
			} else if (cell.getColumnIndex() == PLAY_STOP_BUTTON_COL) {
				Object element = cell.getElement();
				playStopElement(element);
			} else if (cell.getColumnIndex() == TRADING_COLOR_COL
					&& cell.getElement() instanceof TradingConfiguration) {
				showConfigurationSetDialog((TradingConfiguration) cell
						.getElement());
			} else {
				workbenchSelectionListener
						.selectionChanged(new SelectionChangedEvent(
								tableViewer, tableViewer.getSelection()));
				setFocus();
			}
		}
	}

	/**
	 * @param element
	 */
	private static void showConfigurationSetDialog(TradingConfiguration trading) {
		ListDialog dialog = new ListDialog(Display.getDefault()
				.getActiveShell());
		dialog.setTitle("Change Configuration Set");
		dialog.setMessage("Select a new set.");
		dialog.setContentProvider(new ArrayContentProvider());
		dialog.setLabelProvider(new ConfigurationSetLabelProvider());
		dialog.setInput(SymbolsPlugin.getDefault().getSetsManager()
				.getSetList());
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length > 0) {
				Integer set = (Integer) result[0];
				trading.getInfo().setConfigurationSet(set.intValue());
			}
		}
	}

	/**
	 * @param item
	 */
	private void checkItem(TableItem item) {
		if (item != null && isChecked(item.getData())) {
			Object element = item.getData();
			if (element instanceof InputConfiguration) {
				InputConfiguration input = (InputConfiguration) element;
				checkInput(input);
			} else if (element instanceof SymbolConfiguration) {
				SymbolConfiguration<?, ?> symbol = (SymbolConfiguration<?, ?>) element;
				checkSymbol(symbol);
			}
		}
	}

	/**
	 * @param element
	 */
	private void playStopElement(Object element) {
		if (SymbolJob.canRunConfiguration(element)) {
			// play
			Set<Object> set = new HashSet<>();
			set.add(element);
			if (element instanceof SymbolConfiguration) {
				addInputsUnderSymbol(set, (SymbolConfiguration<?, ?>) element);
			}
			SymbolJob.runConfigurations(set, element);
		} else {
			boolean stop = true;
			if (!(element instanceof TradingConfiguration)
					&& SymbolJob.isConfigurationTrading(element)) {
				stop = MessageDialog
						.openConfirm(getViewSite().getShell(), "Stop Trading",
								"There is a trading session open, do you want to stop it?");
			}
			if (stop) {
				SymbolJob.stopConfigurations(Arrays.asList(element));
			}

		}
	}

	/**
	 * @param symbol
	 * @param set
	 */
	private void addInputsUnderSymbol(Set<Object> set,
			SymbolConfiguration<?, ?> symbol) {
		List<?> tableInput = (List<?>) tableViewer.getInput();
		for (Object elem2 : tableInput) {
			if (elem2 instanceof InputConfiguration
					&& ((InputConfiguration) elem2)
							.getInfo()
							.getSymbolId()
							.equals(((SymbolConfiguration<?, ?>) symbol)
									.getUUID())) {
				set.add(elem2);
				addTradingsUnderInput(set, (InputConfiguration) elem2);
			}
		}
	}

	private void addTradingsUnderInput(Set<Object> set, InputConfiguration input) {
		List<?> tableInput = (List<?>) tableViewer.getInput();
		for (Object elem3 : tableInput) {
			if (elem3 instanceof TradingConfiguration
					&& ((TradingConfiguration) elem3).getInfo()
							.getInputConfiguratioId().equals((input).getUUID())) {
				set.add(elem3);
			}
		}
	}

	/**
	 * @param symbol
	 */
	private void checkSymbol(SymbolConfiguration<?, ?> symbol) {
		for (TableItem i : tableViewer.getTable().getItems()) {
			if (i.getData() instanceof InputConfiguration) {
				InputConfiguration input = (InputConfiguration) i.getData();
				if (input.getInfo().getSymbolId().equals(symbol.getUUID())) {
					setChecked(input, true);
					checkInput(input);
				}
			}
		}
		updateCheckboxes();
	}

	/**
	 * @param input
	 */
	private void checkInput(InputConfiguration input) {
		for (TableItem i : tableViewer.getTable().getItems()) {
			if (i.getData() instanceof TradingConfiguration) {
				TradingConfiguration trading = (TradingConfiguration) i
						.getData();
				if (trading.getInfo().getInputConfiguratioId()
						.equals(input.getUUID())) {
					setChecked(input, true);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (TableItem item : table.getItems()) {
			if (isChecked(item.getData())) {
				sb.append(i + " ");
			}
			i++;
		}
		memento.putString(MEMENTO_SELECTED_ITEMS, sb.toString());

		sb = new StringBuilder();
		for (Object obj : (List<?>) tableViewer.getInput()) {
			BaseConfiguration<?> config = (BaseConfiguration<?>) obj;
			sb.append(config.getUUID() + "\n");
		}
		memento.putString(MEMENTO_SELECTED_CONFIGURATION_IDS, sb.toString());

	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		initialCheckedIndexes = new ArrayList<>();
		initialSelectedConfigurationIds = new ArrayList<>();

		if (memento != null) {
			String str = memento.getString(MEMENTO_SELECTED_ITEMS);
			if (str != null) {
				for (String str2 : str.trim().split(" ")) {
					try {
						int i = Integer.parseInt(str2);
						initialCheckedIndexes.add(Integer.valueOf(i));
					} catch (NumberFormatException e) {
						// Documenting empty method to avoid warning.
					}
				}
			}
			str = memento.getString(MEMENTO_SELECTED_CONFIGURATION_IDS);
			if (str != null) {
				for (String str2 : str.trim().split("\n")) {
					initialSelectedConfigurationIds.add(str2);
				}
			}
		}

		// we have to activate this context because some commands are connected
		// to this that
		IContextService serv = (IContextService) getViewSite().getService(
				IContextService.class);
		serv.activateContext("com.mfg.ui.navigatorContext");
	}

	/**
	 * @param element
	 * @return
	 */
	static Color getConfigurationBackground(Object element) {
		if (element instanceof SymbolConfiguration) {
			return Display.getDefault().getSystemColor(SWT.COLOR_CYAN);
		}
		if (element instanceof InputConfiguration) {
			return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
		}
		return null;
	}
}
