package com.mfg.symbols.inputs.persistence;

import java.util.List;
import java.util.UUID;

import com.mfg.chart.ui.views.ChartView;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.IWorkspaceStorageInitiator;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.utils.PartUtils;

public class StoragesInitiator implements IWorkspaceStorageInitiator {

	public StoragesInitiator() {
	}

	@Override
	public void intitialize(IWorkspaceStorage storage) {
		storage.addStorageListener(new WorkspaceStorageAdapter() {
			@Override
			public void objectAboutToRemove(IWorkspaceStorage aStorage,
					Object obj) throws RemoveException {
				InputsStorage inputsStorage = SymbolsPlugin.getDefault()
						.getInputsStorage();
				if (obj instanceof SymbolConfiguration) {
					IStorageObject symbol = (IStorageObject) obj;
					UUID uuid = symbol.getUUID();
					List<InputConfiguration> inputs = inputsStorage
							.findBySymbolId(uuid);
					for (InputConfiguration input : inputs) {
						inputsStorage.remove(input);
					}
				} else if (obj instanceof InputConfiguration) {
					SymbolsPlugin.getDefault().getTradingStorage()
							.removeByInput((InputConfiguration) obj);
				}
			}

			@Override
			public void objectRemoved(IWorkspaceStorage aStorage, Object obj) {
				// TODO: Yet this is ugly, this is a too general rule to be
				// placed here. I moved this from the com.mfg.ui plugin because
				// it is shared by DFS system too. What should be done is to
				// create an extension point to get all the storage objects
				// viewers: editors and views.
				List<ChartView> views = PartUtils
						.getOpenViews(ChartView.VIEW_ID);
				for (ChartView view : views) {
					if (view.getContent() == obj) {
						view.setContent(null);
					}
				}
			}
		});
	}
}
