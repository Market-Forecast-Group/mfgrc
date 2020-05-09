package com.mfg.symbols.csv.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.jobs.CSVJob;
import com.mfg.symbols.csv.ui.editors.CSVEditor;
import com.mfg.symbols.jobs.ISymbolJobFactory;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobConfig;
import com.mfg.ui.editors.Editable;
import com.mfg.ui.editors.IEditable;
import com.mfg.ui.editors.StorageObjectEditorInput;

@SuppressWarnings("rawtypes")
public class CSVConfigurationAdapterFactory implements IAdapterFactory {

	private static Class[] adapters = { IEditable.class,
			ISymbolJobFactory.class };

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof CSVConfiguration) {
			if (adapterType == IEditable.class) {
				CSVConfiguration config = (CSVConfiguration) adaptableObject;
				StorageObjectEditorInput<CSVConfiguration> input = new StorageObjectEditorInput<>(
						config);
				return new Editable(CSVEditor.EDITOR_ID, input);
			}
			if (adapterType == ISymbolJobFactory.class) {
				return new ISymbolJobFactory() {

					@SuppressWarnings("unchecked")
					@Override
					public SymbolJob<?> createSymbolJob(
							SymbolJobConfig<?> config) {
						try {
							return new CSVJob(
									(SymbolJobConfig<CSVConfiguration>) config);
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				};
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return adapters;
	}

}
