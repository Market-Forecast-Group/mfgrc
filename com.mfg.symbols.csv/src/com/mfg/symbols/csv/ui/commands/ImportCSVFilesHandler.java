package com.mfg.symbols.csv.ui.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.connector.csv.CSVPlugin;
import com.mfg.connector.csv.preferences.CSVPrefsPage;
import com.mfg.symbols.csv.CSVSymbolPlugin;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.configurations.CSVConfigurationInfo;
import com.mfg.symbols.csv.configurations.CSVSymbolData2;
import com.mfg.symbols.csv.persistence.CSVStorage;
import com.mfg.ui.UIPlugin;

public class ImportCSVFilesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String dirname;
		boolean tryAgain;
		do {
			dirname = CSVPlugin.getDefault().getPreferenceStore()
					.getString(CSVPrefsPage.PREFERENCE_CSV_DATA_FOLDER);
			tryAgain = dirname == null || dirname.trim().length() == 0
					|| !new File(dirname).exists();
			if (tryAgain) {
				tryAgain = true;
				Shell shell = Display.getDefault().getActiveShell();
				MessageDialog
						.openError(
								shell,
								"Error",
								"The folder "
										+ dirname
										+ " does not exist. Please select a valid CSV Data Folder path.");
				PreferencesUtil.createPreferenceDialogOn(shell,
						CSVPrefsPage.ID, null, null).open();
			}
		} while (tryAgain);

		Set<String> usedNames = new HashSet<>();
		for (CSVConfiguration conf : CSVSymbolPlugin.getDefault()
				.getCSVStorage().getObjects()) {
			usedNames.add(conf.getInfo().getSymbol().getFileName());
		}

		List<String> list = new ArrayList<>();
		File[] files = new File(dirname).listFiles();
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (file.isFile() && name.toLowerCase().endsWith(".csv")
						&& !usedNames.contains(name)) {
					list.add(name);
				}
			}
		}

		Shell shell = HandlerUtil.getActiveShell(event);
		if (list.isEmpty()) {
			MessageDialog.openInformation(shell, "Import CSV Files",
					"There is not any CSV file in the " + dirname + " folder.");
			return null;
		}

		ListSelectionDialog dlg = new ListSelectionDialog(shell, list,
				new ArrayContentProvider(), new LabelProvider(),
				"Select the CSV files:");
		if (dlg.open() != Window.OK) {
			return null;
		}

		List<CSVSymbolData2> newSymbols = new ArrayList<>();

		for (Object item : dlg.getResult()) {
			String fname = (String) item;
			CSVSymbolData2 symbol = new CSVSymbolData2();
			symbol.setFileName(fname);
			newSymbols.add(symbol);
		}

		CSVStorage storage = CSVSymbolPlugin.getDefault().getCSVStorage();

		List<CSVConfiguration> toAdd = new ArrayList<>();
		for (CSVSymbolData2 symbol : newSymbols) {
			boolean add = true;
			for (CSVConfiguration config : storage.getObjects()) {
				CSVSymbolData2 symbol2 = config.getInfo().getSymbol();
				if (symbol.getFileName().equals(symbol2.getFileName())) {
					add = false;
					break;
				}
			}
			if (add) {
				CSVConfiguration newConfig = storage.createDefaultObject();
				CSVConfigurationInfo info = newConfig.getInfo();
				info.setSymbol(symbol);
				toAdd.add(newConfig);
			}
		}
		storage.addAll(toAdd);

		for (CSVConfiguration configuration : toAdd) {
			try {
				UIPlugin.getDefault();
				UIPlugin.openEditor(configuration);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
