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
package com.mfg.utils.ui.views;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.part.ViewPart;

import com.mfg.utils.jobs.IMFGJob;
import com.mfg.utils.ui.IViewWithTable;
import com.mfg.utils.ui.actions.CopyStructuredSelectionAction;

/**
 * @author arian
 * 
 */
@SuppressWarnings("restriction")
public class ChronoView extends ViewPart implements IViewWithTable {

	public static final String ID = "com.mfg.utils.ui.views.ChronoView2"; //$NON-NLS-1$
	private Button taskButton;
	Table table;
	Text taskNameText;
	TableViewer tableViewer;
	ArrayList<ChronoRecord> records;
	private Job refreshJob;
	Button listenForMfgCheckbox;
	private IJobChangeListener jobListener;
	private Action clearAction;

	public ChronoView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		{
			taskButton = new Button(container, SWT.NONE);
			taskButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					startTask();
				}
			});
			taskButton.setText("Start");
		}
		{
			taskNameText = new Text(container, SWT.BORDER);
			taskNameText.setText("Task 1");
			taskNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
		}
		{
			listenForMfgCheckbox = new Button(container, SWT.CHECK);
			listenForMfgCheckbox.setSelection(true);
			listenForMfgCheckbox.setLayoutData(new GridData(SWT.LEFT,
					SWT.CENTER, false, false, 2, 1));
			listenForMfgCheckbox.setText("Listen for MFG jobs");
		}
		{
			tableViewer = new TableViewer(container, SWT.BORDER
					| SWT.FULL_SELECTION | SWT.MULTI);
			table = tableViewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
					1));
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setEditingSupport(new EditingSupport(
						tableViewer) {
					@Override
					protected boolean canEdit(Object element) {
						return !((ChronoRecord) element).isRunning();
					}

					@Override
					protected CellEditor getCellEditor(Object element) {
						return new TextCellEditor(table);
					}

					@Override
					protected Object getValue(Object element) {
						return ((ChronoRecord) element).getTask();
					}

					@Override
					protected void setValue(Object element, Object value) {
						((ChronoRecord) element).setTask(value.toString());
					}
				});
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Color getForeground(Object element) {
						return null;
					}

					@Override
					public Image getImage(Object element) {
						return ((ChronoRecord) element).isRunning() ? WorkbenchImages
								.getImage(ISharedImages.IMG_ELCL_STOP) : null;

					}

					@Override
					public String getText(Object element) {
						return ((ChronoRecord) element).getTask();
					}
				});
				TableColumn tblclmnTaskName = tableViewerColumn.getColumn();
				tblclmnTaskName.setWidth(100);
				tblclmnTaskName.setText("Task Name");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						long time = ((ChronoRecord) element).getConsumedTime();
						long hours = time / 1000 / 60 / 60;
						long min = time / 1000 / 60 % 60;
						long secs = time / 1000 % 60;

						return hours + ":" + min + ":" + secs;
					}
				});
				TableColumn tblclmnTime = tableViewerColumn.getColumn();
				tblclmnTime.setWidth(100);
				tblclmnTime.setText("Time");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return ((ChronoRecord) element).getConsumedMem() + "M";
					}
				});
				TableColumn tblclmnConsumedMem = tableViewerColumn.getColumn();
				tblclmnConsumedMem.setWidth(100);
				tblclmnConsumedMem.setText("Consumed Mem");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return ((ChronoRecord) element).getInitMem() + "M";
					}
				});
				TableColumn tblclmnStartMem = tableViewerColumn.getColumn();
				tblclmnStartMem.setWidth(100);
				tblclmnStartMem.setText("Initial Mem");
			}
			{
				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null;
					}

					@Override
					public String getText(Object element) {
						return ((ChronoRecord) element).getFinalMem() + "M";
					}
				});
				TableColumn tblclmnEndMem = tableViewerColumn.getColumn();
				tblclmnEndMem.setWidth(100);
				tblclmnEndMem.setText("Final Mem");
			}
			tableViewer.setContentProvider(new ArrayContentProvider());
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		afterCreatedWidgets();
	}

	private void afterCreatedWidgets() {
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				new CopyStructuredSelectionAction());
		actionBars.updateActionBars();

		records = new ArrayList<>();
		tableViewer.setInput(records);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection sel = (StructuredSelection) event
						.getSelection();
				if (!sel.isEmpty()) {
					ChronoRecord record = (ChronoRecord) sel.getFirstElement();
					if (record.isRunning()) {
						record.update();
						record.setRunning(false);
						record.setJob(null);
						tableViewer.setSelection(StructuredSelection.EMPTY);
					}
				}
			}
		});

		refreshJob = new Job("Refresh Chrono") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				while (!monitor.isCanceled()) {
					try {
						Thread.sleep(1000);
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								updateStats();
							}
						});

					} catch (InterruptedException e) {
						// Adding a comment to avoid empty block warning.
					}
				}
				return Status.OK_STATUS;
			}

			@Override
			protected void canceling() {
				getThread().interrupt();
			}
		};
		refreshJob.setSystem(true);

		jobListener = new JobChangeAdapter() {

			@Override
			public void aboutToRun(final IJobChangeEvent event) {
				@SuppressWarnings("unused")
				final Job job = event.getJob();
				try {
					Display diaply = Display.getDefault();
					diaply.asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								if (listenForMfgCheckbox.getSelection()
										&& event.getJob().belongsTo(
												IMFGJob.class)) {
									Job newJob = event.getJob();
									taskNameText.setText(event.getJob()
											.getName());
									ChronoRecord record = startTask();
									record.setJob(newJob);
								}
							} catch (SWTException e) {
								// Adding a comment to avoid empty block
								// warning.
							}
						}

					});
				} catch (SWTException e) {
					// Adding a comment to avoid empty block warning.
				}
			}

			@Override
			public void done(IJobChangeEvent event) {
				Job job = event.getJob();
				done(job);
			}

			void done(Job job) {
				for (ChronoRecord record : records) {
					if (record.getJob() == job) {

						record.setRunning(false);
						record.setJob(null);
					}
				}
			}
		};
		Job.getJobManager().addJobChangeListener(jobListener);
	}

	@Override
	public void dispose() {
		Job.getJobManager().removeJobChangeListener(jobListener);
		refreshJob.cancel();
		try {
			refreshJob.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.dispose();
	}

	protected ChronoRecord startTask() {
		ChronoRecord record = new ChronoRecord();
		record.setTask(taskNameText.getText());
		record.start();
		records.add(record);
		refreshJob.schedule();
		updateStats();
		return record;
	}

	void updateStats() {
		for (ChronoRecord record : records) {
			if (record.isRunning()) {
				record.update();
			}
		}
		if (!tableViewer.getTable().isDisposed()) {
			if (!tableViewer.isCellEditorActive()) {
				tableViewer.refresh();
			}
		}
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		clearAction = new Action("Remove all the tasks") {
			{
				setImageDescriptor(WorkbenchImages
						.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
			}

			@Override
			public void run() {
				records.clear();
				tableViewer.refresh();
			}
		};
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(clearAction);
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
		// Set the focus
	}

	@Override
	public Table getTable() {
		return tableViewer.getTable();
	}

}
