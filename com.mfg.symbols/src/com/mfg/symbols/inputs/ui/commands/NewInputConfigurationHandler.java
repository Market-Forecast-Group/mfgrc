package com.mfg.symbols.inputs.ui.commands;

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.ui.commands.AbstractNewStorageObjectHandler;

public class NewInputConfigurationHandler extends
		AbstractNewStorageObjectHandler<InputConfiguration> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.ui.commands.AbstractNewStorageObjectHandler#getStorage()
	 */
	@Override
	protected SimpleStorage<InputConfiguration> getStorage() {
		return SymbolsPlugin.getDefault().getInputsStorage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewStorageObjectHandler#getInitialObjectName
	 * ()
	 */
	@Override
	protected String getInitialObjectName() {
		return "Input";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewStorageObjectHandler#createNewName(com
	 * .mfg.persist.interfaces.SimpleStorage)
	 */
	@Override
	protected String createNewName(SimpleStorage<InputConfiguration> storage) {
		// the name is set when the object is created.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewStorageObjectHandler#createObject(org.
	 * eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected InputConfiguration createObject(ExecutionEvent event) {
		InputConfiguration input = super.createObject(event);

		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		Assert.isTrue(!sel.isEmpty());

		SymbolConfiguration<?, ?> symbol = (SymbolConfiguration<?, ?>) Platform
				.getAdapterManager().getAdapter(sel.getFirstElement(),
						SymbolConfiguration.class);
		InputConfigurationInfo inputInfo = input.getInfo();
		inputInfo.setSymbolId(symbol.getUUID());

		InputConfiguration[] inputs = SymbolsPlugin.getDefault()
				.getInputsStorage().findBySymbol(symbol);
		String name = getStorage()
				.createNewName("Input", Arrays.asList(inputs));
		input.setName(name);

		SymbolConfiguration<?, ?> defaultSymbol = (SymbolConfiguration<?, ?>) symbol
				.getStorage().createDefaultObject();
		HistoricalDataInfo historicalDataInfo = ((SymbolConfigurationInfo<?>) defaultSymbol
				.getInfo()).getHistoricalDataInfo();
		inputInfo.setHistoricalDataInfo(historicalDataInfo);
		return input;
	}
}
