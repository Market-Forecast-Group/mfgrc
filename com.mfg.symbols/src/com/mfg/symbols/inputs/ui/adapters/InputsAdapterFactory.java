package com.mfg.symbols.inputs.ui.adapters;

import java.util.UUID;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.chart.ui.views.IAlternativeChartContent;
import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.ui.UIPlugin;
import com.mfg.ui.editors.IEditable;

@SuppressWarnings("rawtypes")
public class InputsAdapterFactory implements IAdapterFactory {

	private static final Class[] adapterList = { IChartContentAdapter.class,
			IEditable.class };

	@Override
	public Object getAdapter(final Object adaptableObject, Class adapterType) {
		if (adapterType == IAlternativeChartContent.class) {
			return new IAlternativeChartContent() {

				@Override
				public Object getAlternativeContent(Object arg) {
					if (arg instanceof IMemento) {
						IMemento memento = (IMemento) arg;
						Boolean b = memento
								.getBoolean(SyntheticChartAdapter.MEMENTO_SYNTH_CHART);
						if (b != null && b.booleanValue()) {
							return new SyntheticInput(
									(InputConfiguration) adaptableObject);
						}
					} else if (arg instanceof SyntheticInput) {
						return ((SyntheticInput) arg).getConfiguration();
					}
					return adaptableObject;
				}
			};
		} else if (adapterType == IChartContentAdapter.class) {
			if (adaptableObject instanceof InputConfiguration) {
				return new InputChartAdapter(
						(InputConfiguration) adaptableObject);
			} else if (adaptableObject instanceof SyntheticInput) {
				return new SyntheticChartAdapter(
						((SyntheticInput) adaptableObject).getConfiguration());
			}
		} else if (adapterType == IEditable.class) {
			if (adaptableObject instanceof InputConfiguration) {
				final InputConfiguration configuration = (InputConfiguration) adaptableObject;
				final UUID symbolUUID = configuration.getInfo().getSymbolId();
				final SymbolConfiguration<?, ?> symbol = SymbolsPlugin
						.getDefault().findSymbolConfiguration(symbolUUID);
				return new IEditable() {

					@Override
					public IEditorPart openEditor() throws PartInitException {
						UIPlugin.getDefault();
						IEditorPart editor = UIPlugin.openEditor(symbol);
						if (editor != null) {
							((FormEditor) editor).setActivePage(configuration
									.getUUID().toString());
						}
						return editor;
					}
				};
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return adapterList;
	}

}
