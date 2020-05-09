package com.mfg.strategy.automatic.exportIndicator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridData;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;

public class ScaleIndicatorExportingConfigurationComposite extends Composite {
	private ScaleIndicatorExportingConfiguration configuration;
	private Label lblX;

	private List<Button> btnCheckButtons;

	public ScaleIndicatorExportingConfigurationComposite(Composite parent,
			int style){
		this(parent, style, ScaleIndicatorExportingConfiguration.buildDefault());
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ScaleIndicatorExportingConfigurationComposite(Composite parent,
			int style, ScaleIndicatorExportingConfiguration aConfiguration) {
		super(parent, style);
		this.configuration = aConfiguration;
		setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(5, false));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 95;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		
		btnScale = new Button(composite, SWT.CHECK);
		GridData gd_btnScale = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnScale.widthHint = 68;
		btnScale.setLayoutData(gd_btnScale);
		btnScale.setText("Scale");
		
		lblX = new Label(composite, SWT.NONE);
		lblX.setText("x");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("           ");
		
		btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnCheckButton.setText("Include Pivot");
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new GridLayout(1, false));
		
		createButtons(composite_1);
		initDataBindings();

	}

	private void createButtons(Composite composite_1) {
		btnCheckButtons = new ArrayList<>();
		List<IndicatorParameter> parameters = getConfiguration().getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			Button button;
			btnCheckButtons.add(button=new Button(composite_1, SWT.CHECK));
			button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
					true, false, 1, 1));
			button.setText(parameters.get(i).getLongName());
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public int getScale() {
		return configuration.getScale();
	}

	public ScaleIndicatorExportingConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ScaleIndicatorExportingConfiguration aConfiguration) {
		this.configuration = aConfiguration;
	}
	
	public IndicatorParameter parameter;
	private Button btnScale;
	private Button btnCheckButton;

	private static void bindParameter(DataBindingContext bindingContext, IndicatorParameter parameter2, Widget btnCheckButton2) {
		IObservableValue observeSelectionBtnCheckButtonObserveWidget = WidgetProperties.selection().observe(btnCheckButton2);
		IObservableValue includedParameterObserveValue = PojoProperties.value("included").observe(parameter2);
		bindingContext.bindValue(observeSelectionBtnCheckButtonObserveWidget, includedParameterObserveValue, null, null);
		//
		IObservableValue observeTextBtnCheckButtonObserveWidget = WidgetProperties.text().observe(btnCheckButton2);
		IObservableValue longNameParameterObserveValue = PojoProperties.value("longName").observe(parameter2);
		bindingContext.bindValue(observeTextBtnCheckButtonObserveWidget, longNameParameterObserveValue, null, null);
		//
//		IObservableValue observeEnabledBtnCheckButtonObserveWidget = WidgetProperties.enabled().observe(btnCheckButton2);
//		IObservableValue includedGetConfigurationObserveValue = PojoProperties.value("included").observe(getConfiguration());
//		bindingContext.bindValue(observeEnabledBtnCheckButtonObserveWidget, includedGetConfigurationObserveValue, null, null);
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblXObserveWidget = WidgetProperties.text().observe(lblX);
		IObservableValue scaleGetConfigurationObserveValue = PojoProperties.value("scale").observe(getConfiguration());
		bindingContext.bindValue(observeTextLblXObserveWidget, scaleGetConfigurationObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnScaleObserveWidget = WidgetProperties.selection().observe(btnScale);
		IObservableValue includedGetConfigurationObserveValue = PojoProperties.value("included").observe(getConfiguration());
		bindingContext.bindValue(observeSelectionBtnScaleObserveWidget, includedGetConfigurationObserveValue, null, null);
		//
		//
		IObservableValue observeSelectionbtnCheckButtonObserveWidget = WidgetProperties.selection().observe(btnCheckButton);
		IObservableValue pivotIncludedGetConfigurationObserveValue = PojoProperties.value("pivotIncluded").observe(getConfiguration());
		bindingContext.bindValue(observeSelectionbtnCheckButtonObserveWidget, pivotIncludedGetConfigurationObserveValue, null, null);
		//
		//
		List<IndicatorParameter> parameters = getConfiguration().getParameters();
		for (int i=0; i<parameters.size(); i++) {
			bindParameter(bindingContext, parameters.get(i), btnCheckButtons.get(i));
		}
		//
		return bindingContext;
	}


}
