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
package com.mfg.logger.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;
import com.mfg.logger.ILoggerListener;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.memory.MemoryLoggerManager;
import com.mfg.logger.ui.actions.ClearLogAction2;
import com.mfg.logger.ui.actions.LinkedToLastMessageAction2;
import com.mfg.utils.concurrent.SimpleAnimator;

/**
 * @author arian
 * 
 */
public class LogViewerManager {
	/**
	 * 
	 */
	private static final MemoryLoggerManager EMPTY_MANAGER = new MemoryLoggerManager(
			"EMPTY", true) {
		@Override
		public void addLoggerListener(ILoggerListener listener) {
			//
		}
	};
	private final String logViewerId;
	TableViewer viewer;
	private ILoggerManager logManager;
	private boolean linkedToLastMessage;
	private final LinkedToLastMessageAction2 linkToLastMessageAction;
	private final ClearLogAction2 clearLogAction;
	private ILoggerListener logListener;
	private final Map<String, TableViewerColumn> columnsMap;
	private Runnable _updateRun;
	protected int _token;
	private SimpleAnimator _animator;

	/**
	 * @param manager
	 *            unused, to be removed
	 */
	public LogViewerManager(Composite parent, int style, String logViewerId1,
			ILoggerManager manager) throws CoreException {
		super();
		this.logViewerId = logViewerId1;
		linkedToLastMessage = true;

		columnsMap = new HashMap<>();

		createViewer(parent, style | SWT.VIRTUAL | SWT.FULL_SELECTION
				| SWT.SELECTED);

		setLogManager(logManager);

		clearLogAction = new ClearLogAction2(this);
		linkToLastMessageAction = new LinkedToLastMessageAction2(this);

		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}

	/**
	 * @return the manager
	 */
	public ILoggerManager getLogManager() {
		return logManager;
	}

	/**
	 * @param logManager
	 */
	public void setLogManager(ILoggerManager newLogManagerPar) {
		ILoggerManager newLogManager = newLogManagerPar;
		if (newLogManager == null) {
			newLogManager = EMPTY_MANAGER;
		}

		if (logListener == null) {
			initLogListener();
		} else {
			if (logManager != null) {
				logManager.removeLogListener(logListener);
			}
		}
		newLogManager.addLoggerListener(logListener);
		viewer.setInput(newLogManager.getReader());

		logManager = newLogManager;
		scrollToIndex(0);
	}

	/**
	 * 
	 */
	public void initLogListener() {
		_token = 0;
		if (_updateRun == null) {
			_updateRun = new Runnable() {
				int _lastToken = 0;

				@Override
				public void run() {
					if (_lastToken != _token) {
						Display display = Display.getDefault();
						if (!display.isDisposed()) {

							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									if (!viewer.getTable().isDisposed()) {
										viewer.refresh();
										scrollToIndex(0);
									}
								}
							});
						}
						_lastToken = _token;
					}
				}
			};
		}
		if (_animator == null) {
			_animator = new SimpleAnimator(_updateRun);
		}
		_animator.start();

		logListener = new ILoggerListener() {

			@Override
			public void logged(ILogger logger, ILogRecord record) {
				_token++;
			}

			@Override
			public void begin(ILogger logger, String msg) {
				Display display = Display.getDefault();
				if (!display.isDisposed()) {

					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							if (!viewer.getTable().isDisposed()) {
								clearTable();
							}
						}
					});
				}

			}
		};

	}

	/**
	 * Action to clear the log.
	 * 
	 * @return the clearLogAction
	 */
	public ClearLogAction2 getClearLogAction() {
		return clearLogAction;
	}

	/**
	 * Action to keep linked (or not) to the last message.
	 * 
	 * @return the linkToLastMessageAction
	 */
	public LinkedToLastMessageAction2 getLinkToLastMessageAction() {
		return linkToLastMessageAction;
	}

	public void scrollToEnd() {
		if (isLinkedToLastMessage()) {
			Table table = viewer.getTable();
			int i = logManager.getReader().getRecordCount() + 1
					- table.getClientArea().height / table.getItemHeight();
			table.setTopIndex(i);
		}
	}

	public boolean isLinkedToLastMessage() {
		return linkedToLastMessage;
	}

	public void setLinkedToLastMessage(boolean linkedToLastMessage1) {
		this.linkedToLastMessage = linkedToLastMessage1;
	}

	public void scrollToIndex(int index) {
		Table table = viewer.getTable();
		table.setTopIndex(index);
	}

	public void clearTable() {
		((LogContentProvider3) viewer.getContentProvider()).clear();
		scrollToEnd();
	}

	private void createViewer(Composite parent, int style) throws CoreException {
		IConfigurationElement[] elems = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						LoggerPlugin.LOGGER_VIEWER_EXTENSION_POINT);
		for (IConfigurationElement elem : elems) {
			String id = elem.getAttribute("id");
			if (id != null && id.equals(logViewerId)) {
				viewer = new TableViewer(parent, style);
				Table table = viewer.getTable();
				table.setHeaderVisible(true);
				table.setLinesVisible(true);
				viewer.setContentProvider(new LogContentProvider3());

				IConfigurationElement[] colElems = elem.getChildren();
				table.setRedraw(false);
				for (IConfigurationElement colElem : colElems) {
					if (colElem.getName().equals("viewerColumn")) {
						createColumnViewer(colElem);
					}
				}

				table.pack();
				viewer.getTable().setRedraw(true);
				break;
			}
		}
	}

	/**
	 * @return the viewer
	 */
	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * @return the logViewerId
	 */
	public String getLogViewerId() {
		return logViewerId;
	}

	/**
	 * @param viewer
	 * @param colElem
	 * @throws CoreException
	 */
	private void createColumnViewer(IConfigurationElement colElem)
			throws CoreException {
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		LogColumnLabelProvider labelProvider = (LogColumnLabelProvider) colElem
				.createExecutableExtension("labelProvider");
		String label = colElem.getAttribute("label");
		column.getColumn().setText(label);
		column.getColumn().setWidth(100);
		column.setLabelProvider(labelProvider);
		String id = colElem.getAttribute("id");
		columnsMap.put(id, column);
	}

	/**
	 * @return the columnsMap
	 */
	public Map<String, TableViewerColumn> getColumnsMap() {
		return columnsMap;
	}

	/**
	 * 
	 */
	public void dispose() {
		_animator.stop();
	}

}
