package com.mfg.plstats.ui.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.mfg.connector.csv.CSVPlugin;
import com.mfg.connector.csv.preferences.CSVPrefsPage;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.persist.PLStatsCSVConfiguration;
import com.mfg.plstats.persist.PLStatsCSVStorage;
import com.mfg.ui.UIPlugin;

public class ImportCSVHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String seriesDir = CSVPlugin.getDefault().getPreferenceStore()
				.getString(CSVPrefsPage.PREFERENCE_CSV_DATA_FOLDER);

		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.OPEN | SWT.MULTI);
		dialog.setFilterPath(seriesDir);
		dialog.setFilterExtensions(new String[] { "*.csv" });
		dialog.open();

		List<PLStatsCSVConfiguration> newList = new ArrayList<>();

		for (String path : dialog.getFileNames()) {
			File file = new File(dialog.getFilterPath(), path);
			newList.add(new PLStatsCSVConfiguration(file));
		}

		PLStatsCSVStorage storage = PLStatsPlugin.getDefault().getCSVStorage();

		List<PLStatsCSVConfiguration> toAdd = new ArrayList<>();
		for (PLStatsCSVConfiguration csv : newList) {
			boolean add = true;
			for (PLStatsCSVConfiguration config : storage.getObjects()) {
				if (csv.getFile().equals(config.getFile())) {
					add = false;
					break;
				}
			}
			if (add) {
				toAdd.add(csv);
			}
		}
		storage.addAll(toAdd);

		for (PLStatsCSVConfiguration csv : toAdd) {
			try {
				UIPlugin.openEditor(csv);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
