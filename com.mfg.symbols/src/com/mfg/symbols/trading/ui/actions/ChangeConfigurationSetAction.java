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
package com.mfg.symbols.trading.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.IWorkspaceStorageListener;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.symbols.ui.ConfigurationSetLabelProvider;
import com.mfg.symbols.ui.ConfigurationSetsManager;

/**
 * @author arian
 * 
 */
public class ChangeConfigurationSetAction extends Action {
	private ITradingView _view;
	private IWorkspaceStorageListener _storageListener;
	private boolean _showOnlyUsedSets;

	public ChangeConfigurationSetAction(ITradingView aView) {
		this(aView, true);
	}

	public ChangeConfigurationSetAction(ITradingView aView,
			boolean aShowOnlyUsedSets) {
		super("Change Configuration Set");
		this._showOnlyUsedSets = aShowOnlyUsedSets;
		// because WB designer
		if (aView == null) {
			return;
		}
		this._view = aView;
		updateIcon();
		_storageListener = new WorkspaceStorageAdapter() {
			@Override
			public void storageChanged(IWorkspaceStorage storage) {
				updateIcon();
			}
		};
		SymbolsPlugin.getDefault().getTradingStorage()
				.addStorageListener(_storageListener);
	}

	public void dispose() {
		SymbolsPlugin.getDefault().getTradingStorage()
				.removeStorageListener(_storageListener);
	}

	public void updateIcon() {
		int set = _view.getConfigurationSet();
		SymbolsPlugin.getDefault().getSetsManager();
		Image img = ConfigurationSetsManager.getImage(set);
		if (!img.isDisposed()) {
			ImageDescriptor imgDecr = ImageDescriptor.createFromImage(img);
			setImageDescriptor(imgDecr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (runAction(_showOnlyUsedSets, _view)) {
			updateIcon();
		}
	}

	public static boolean runAction(boolean showOnlyUsedSets, ITradingView view) {
		SymbolsPlugin plugin = SymbolsPlugin.getDefault();
		List<Integer> input;
		if (showOnlyUsedSets) {
			input = new ArrayList<>();
			for (int set : plugin.getSetsManager().getSetList()) {
				List<TradingConfiguration> tradings = plugin
						.getTradingStorage().findBySet(set);
				if (!tradings.isEmpty()) {
					input.add(Integer.valueOf(set));
				}
			}
		} else {
			input = plugin.getSetsManager().getSetList();
		}
		ListDialog dialog = new ListDialog(Display.getDefault()
				.getActiveShell());
		dialog.setTitle("Change Configuration Set");
		dialog.setMessage("Select a new set.");
		dialog.setContentProvider(new ArrayContentProvider());
		dialog.setLabelProvider(new ConfigurationSetLabelProvider());
		dialog.setInput(input);

		dialog.setInitialSelections(new Object[] {});

		if (dialog.open() == Window.OK) {
			Integer newSet = (Integer) dialog.getResult()[0];
			view.setConfigurationSet(newSet.intValue());
			List<TradingConfiguration> tradings = plugin.getTradingStorage()
					.findBySet(newSet.intValue());

			if (view.getConfiguration() != null
					&& view.getConfiguration().getInfo().getConfigurationSet() == newSet
							.intValue()) {
				// test the current configuration first
				tradings.add(0, view.getConfiguration());
			}
			// find the configuration of the same set that is running
			IJobManager jobManager = Job.getJobManager();
			boolean set = false;
			for (TradingConfiguration trading : tradings) {
				Job[] jobs = jobManager.find(trading);
				if (jobs.length > 0) {
					set = true;
					view.setConfiguration(trading);
					break;
				}
			}
			if (!set) {
				if (tradings.isEmpty()) {
					view.setConfiguration(null);
				} else {
					view.setConfiguration(tradings.get(tradings.size() - 1));
				}
			}
			if (view.getConfiguration() != null) {
				return true;
			}
		}
		return false;
	}
}
