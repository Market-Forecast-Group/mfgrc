package com.mfg.strategy.automatic.exportIndicator;

import java.util.HashMap;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class IndicatorExportingSettings extends WizardPage {

	private int lower, upper;
	HashMap<String, Composite> scaleSettings;
	protected Composite selectedC;
	private IndicatorExportingConfiguration configuration;
	StackLayout layout;
	Composite cStack;
	
	/**
	 * @wbp.parser.constructor
	 */
	public IndicatorExportingSettings() {
		this(2, 6);
	}
	
	public IndicatorExportingSettings(int aLower, int aUpper) {
		super("wizardPage");
		this.lower = aLower;
		this.upper = aUpper;
		setTitle("Events Selection");
		setDescription("Selects the events to export to the data base.");
	}

	public IndicatorExportingConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(IndicatorExportingConfiguration aConfiguration) {
		this.configuration = aConfiguration;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));
		
//		Composite scaleSettings = new ScaleIndicatorExportingConfigurationComposite(container, SWT.NONE);
//		BorderLayout borderLayout = (BorderLayout) scaleSettings.getLayout();
//		scaleSettings.setLayoutData(BorderLayout.CENTER);
		
		Composite c = new Composite(container, SWT.NONE);
		c.setLayoutData(BorderLayout.WEST);
		c.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		cStack = new Composite(container, SWT.NONE);
		cStack.setLayoutData(BorderLayout.CENTER);
		cStack.setLayout(layout = new StackLayout());
		
		final List list = new List(c, SWT.BORDER);
		String[] items = new String[upper-lower+2];
		items[0]="General";
		configuration = new IndicatorExportingConfiguration();
		ScaleIndicatorExportingConfiguration[] scConfig = new ScaleIndicatorExportingConfiguration[upper-lower+1];
		configuration.setScaleIndicatorExportingConfigurations(scConfig);
		selectedC = new IndicatorExportingConfigurationComposite(cStack, SWT.BORDER,configuration);
		scaleSettings = new HashMap<>();
		scaleSettings.put(items[0], selectedC);
		for (int i = lower; i <= upper; i++) {
			String txt = "Scale "+i;
			items[i-lower+1] = txt;
			ScaleIndicatorExportingConfiguration cf = ScaleIndicatorExportingConfiguration.buildDefault(i);
			scConfig[i-lower] = cf;
			scaleSettings.put(txt, new ScaleIndicatorExportingConfigurationComposite(cStack, SWT.BORDER,cf));
//			BorderLayout borderLayout = (BorderLayout) scaleSettings.getLayout();
//			scaleSettings.setLayoutData(BorderLayout.CENTER);
		}
		list.setItems(items);
		list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedC = scaleSettings.get(list.getSelection()[0]);
				layout.topControl = selectedC;
				cStack.layout();
			}
		});
		layout.topControl = selectedC;
		cStack.layout();
	}
	
}
