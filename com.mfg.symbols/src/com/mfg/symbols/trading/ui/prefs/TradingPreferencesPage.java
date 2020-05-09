package com.mfg.symbols.trading.ui.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.ui.UIPlugin;

public class TradingPreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public TradingPreferencesPage() {
		super(GRID);
		setPreferenceStore(SymbolsPlugin.getDefault().getPreferenceStore());
		setDescription("Trading");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(
				SymbolsPlugin.PREF_PLAY_SOUND_ON_ORDER_FILLED,
				"Play a sound when an order is filled", getFieldEditorParent()));
		String[][] pairs = new String[UIPlugin.SOUNDS.length][];
		for (int i = 0; i < UIPlugin.SOUNDS.length; i++) {
			String snd = UIPlugin.SOUNDS[i];
			pairs[i] = new String[] { snd, snd };
		}
		addField(new ComboFieldEditor(SymbolsPlugin.PREF_SOUND_ON_ORDER_FILLED,
				"Sound to play", pairs, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing
	}

}
