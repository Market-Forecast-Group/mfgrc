package com.marketforescastgroup.logger.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.marketforescastgroup.logger.LogViewPlugin;

public class LogPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * constructor
	 */
	public LogPreferencePage() {
		super(GRID);
		setTitle("Logger");
	}

	/*
	 * @Override(non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	public void createFieldEditors() {
		DirectoryFieldEditor directory = new DirectoryFieldEditor(
				LogViewPlugin.LOG_DIRECTORY, "&Logger directory:",
				getFieldEditorParent());
		directory.setEmptyStringAllowed(false);
		final Text text = directory.getTextControl(getFieldEditorParent());
		text.setEnabled(false);
		addField(directory);
	}

	/*
	 * @Override(non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		final IPreferenceStore preferenceStore = LogViewPlugin.getDefault()
				.getPreferenceStore();
		String log = preferenceStore.getString(LogViewPlugin.LOG_DIRECTORY);
		if (log == null || log.length() == 0) {
			preferenceStore.setValue(LogViewPlugin.LOG_DIRECTORY,
					LogViewPlugin.LOG_DEFAULT_PATH);
			preferenceStore.setDefault(LogViewPlugin.LOG_DIRECTORY,
					LogViewPlugin.LOG_DEFAULT_PATH);
		}

		setPreferenceStore(preferenceStore);
	}

	/*
	 * @Override(non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		final IPreferenceStore preferenceStore = LogViewPlugin.getDefault()
				.getPreferenceStore();

		final String oldLogDir = preferenceStore
				.getString(LogViewPlugin.LOG_DIRECTORY);

		final boolean retVal = super.performOk();

		final String logDir = preferenceStore
				.getString(LogViewPlugin.LOG_DIRECTORY);

		boolean isEquals = false;

		if (!oldLogDir.equals(logDir)) {
			isEquals = true;
		}

		if (isEquals) {
			Display.getDefault().asyncExec(new Runnable() {
				/*
				 * @Override(non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					final Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					MessageDialog
							.openInformation(
									shell,
									"INFO",
									"Log Directory has been changed: "
											+ logDir
											+ ", requires RESTART for the changes to take effect.");
				}
			});
		}
		return retVal;
	}
}