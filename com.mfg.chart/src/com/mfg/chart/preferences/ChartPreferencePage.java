package com.mfg.chart.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.chart.ChartPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ChartPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ChartPreferencePage() {
		super(GRID);
		setPreferenceStore(ChartPlugin.getDefault().getPreferenceStore());
		setDescription("Chart values");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		IntegerFieldEditor editor = new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_REALTIME_UPDATE_ON_TICK_SLEEP_VALUE,
				"Update on tick sleep value (millis): ", getFieldEditorParent());
		addField(editor);

		addField(new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_ZOOM_OUT_ALL_BLANK_PERCENT,
				"Full zoom-out black percent: ", getFieldEditorParent()));

		IntegerFieldEditor editor2 = new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_ZOOM_WHEEL_PERCENT, "Zooming percent",
				getFieldEditorParent());
		editor2.setValidRange(1, 99);
		addField(editor2);

		editor = new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_START_SCROLLING_PERCENT,
				"Start auto-scrolling (Right of the screen): ",
				getFieldEditorParent());
		editor.setEmptyStringAllowed(false);
		editor.setValidRange(1, 99);
		addField(editor);

		editor = new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_STOP_SCROLLING_PERCENT,
				"Stop auto-scrolling (Left of the screen): ",
				getFieldEditorParent());
		editor.setEmptyStringAllowed(false);
		editor.setValidRange(1, 99);
		addField(editor);

		addField(new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_MAX_NUMBER_OF_POINTS_TO_SHOW,
				"Max number of displayed points", getFieldEditorParent()));

		// addField(new IntegerFieldEditor(
		// ChartPlugin.PREFERENCES_MAX_BANDS_RANGE_LENGTH_TO_AVOID_COMPRESSION,
		// "Max range length to avoid bands (scale 7 and bigger) compression",
		// getFieldEditorParent()));

		addField(new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_MAX_NUMBER_OF_POSITIONS_TO_SHOW,
				"Max number of displayed trades markers",
				getFieldEditorParent()));

		addField(new IntegerFieldEditor(
				ChartPlugin.PREFERENCES_FRAMES_PER_SECOND,
				"Frames Per Second (Animation)", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		//Adding a comment to avoid empty block warning.
	}

}
