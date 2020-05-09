package com.mfg.plstats.ui.handlers;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.IndicatorManager;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.persist.PLStatsCSVConfiguration;
import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.plstats.persist.PLStatsIndicatorStorage;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.io.IO;
import com.mfg.widget.IndicatorConfiguration;
import com.mfg.widget.arc.gui.IndicatorParamBean;

public class NewIndicatorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		PLStatsCSVConfiguration csv = (PLStatsCSVConfiguration) sel
				.getFirstElement();

		PLStatsIndicatorStorage storage = PLStatsPlugin.getDefault()
				.getIndicatorStorage();
		String name = storage.createNewName("Indicator",
				storage.findIndicatorsBySymbolId(csv.getUUID()));

		IIndicatorConfiguration indicatorConfig = new IndicatorConfiguration(
				name);
		indicatorConfig.setIndicatorSettings(new IndicatorParamBean());
		indicatorConfig.getIndicatorSettings().setIndicatorNumberOfScales(6);
		indicatorConfig.setSymbol(new CSVSymbolData(csv.getFile()));

		PLStatsIndicatorConfiguration config = new PLStatsIndicatorConfiguration();
		config.setName(name);
		config.setIndicator(indicatorConfig);
		config.setSymbolId(csv.getUUID());

		PLStatsPlugin.getDefault().getIndicatorManager();
		File dbDir = IndicatorManager.getIndicatorDatabaseDir(config
				.getIndicator());
		if (dbDir.exists()) {
			MessageDialog
					.openWarning(
							HandlerUtil.getActiveShell(event),
							"Create Indicator Configuration",
							"There is an Indicator database that match with this configuration. Probabily it is there because an error. It will be removed right now.");
			try {
				IO.deleteFile(dbDir);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		storage.add(config);

		try {
			UIPlugin.openEditor(config);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return null;
	}

}
