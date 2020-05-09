package com.mfg.symbols.dfs.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.jobs.DFSJob;
import com.mfg.symbols.dfs.ui.editors.DFSEditor;
import com.mfg.symbols.jobs.ISymbolJobFactory;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobConfig;
import com.mfg.ui.editors.Editable;
import com.mfg.ui.editors.IEditable;
import com.mfg.ui.editors.StorageObjectEditorInput;

@SuppressWarnings("rawtypes")
public class DFSConfigurationAdapterFactory implements IAdapterFactory {

	private static Class[] adapters = { IEditable.class,
			ISymbolJobFactory.class };

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IEditable.class) {
			IStorageObject conf = (IStorageObject) adaptableObject;
			return new Editable(DFSEditor.EDITOR_ID,
					new StorageObjectEditorInput(conf));
		} else if (adapterType == ISymbolJobFactory.class) {
			return new ISymbolJobFactory() {

				@Override
				public SymbolJob<?> createSymbolJob(SymbolJobConfig<?> config) {
					try {
						return new DFSJob(
								(SymbolJobConfig<DFSConfiguration>) config);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			};
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return adapters;
	}

}
