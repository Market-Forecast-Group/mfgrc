package com.mfg.logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.FieldLayoutPreferencePage;

import com.mfg.logger.application.preferences.DefaultLoggerPreferencesFactory;
import com.mfg.logger.application.preferences.ILoggerComponentPreferencesFactory;

public class LoggerPreferencePage extends FieldLayoutPreferencePage implements
		IWorkbenchPreferencePage {
	public LoggerPreferencePage() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setTitle("Logger");
		setPreferenceStore(LoggerPlugin.getDefault().getPreferenceStore());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wb.swt.FieldLayoutPreferencePage#createPageContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createPageContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		container.setLayoutData(gd);
		container.setLayout(new GridLayout(2, false));

		LoggerPlugin plugin = LoggerPlugin.getDefault();

		for (IConfigurationElement config : plugin.getComponentConfigurations()) {
			try {
				if (config != null) {
					ILoggerComponentPreferencesFactory factory;
					String factoryName = config
							.getAttribute("preferencesFactory");
					if (factoryName == null) {
						factory = new DefaultLoggerPreferencesFactory();
					} else {
						factory = (ILoggerComponentPreferencesFactory) config
								.createExecutableExtension(factoryName);
					}

					String componentID = config.getAttribute("id");
					String componentName = config.getAttribute("name");

					LogLevel[] levels = factory.getPossibleLevels();
					String[][] data = new String[levels.length][];

					int i = 0;
					for (LogLevel level : levels) {
						data[i] = new String[] { level.getName(),
								Float.toString(level.getPriority()) };
						i++;
					}

					String key = LoggerPlugin
							.getComponentLogLevelPreferenceKey(componentID);

					ComboFieldEditor editor = new ComboFieldEditor(key, "",
							data, container);

					Label label = editor.getLabelControl(container);
					label.setText(componentName);
					gd = new GridData();
					label.setLayoutData(gd);

					addField(editor);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return container;
	}
}
