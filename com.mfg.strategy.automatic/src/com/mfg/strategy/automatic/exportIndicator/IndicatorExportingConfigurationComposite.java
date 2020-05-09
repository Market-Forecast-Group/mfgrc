package com.mfg.strategy.automatic.exportIndicator;

import java.awt.BorderLayout;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
//import swing2swt.layout.BorderLayout;
//import org.eclipse.swt.SWT;

public class IndicatorExportingConfigurationComposite extends Composite {
	private Text text;
	private IndicatorExportingConfiguration configuration;
	private Button btnTime;
	private Button btnPrice;

	public IndicatorExportingConfigurationComposite(Composite parent, int style) {
		this(parent, style, new IndicatorExportingConfiguration());
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IndicatorExportingConfigurationComposite(Composite parent, int style, IndicatorExportingConfiguration aConfiguration) {
		super(parent, style);
		this.configuration = aConfiguration;
		setLayout(new swing2swt.layout.BorderLayout(0, 0));
//		setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(2, false));
		
		text = new Text(composite, SWT.BORDER);
		text.setText("<Select File Name>");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browse();
			}
		});
		btnNewButton.setText("Browse");
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new GridLayout(1, false));
		
		btnTime = new Button(composite_1, SWT.CHECK);
		btnTime.setText("Time");
		
		btnPrice = new Button(composite_1, SWT.CHECK);
		btnPrice.setText("Price");
		initDataBindings();

	}
	
	protected void browse() {
		Shell shell = Display.getDefault().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[]{"*.csv"});
		dialog.setFilterNames(new String[]{"Indicator Events"});
		dialog.setText("Export Indicator Events");
		String path = dialog.open();
		if (path == null)
			return;
		text.setText(path);
		getConfiguration().setFileName(path);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public IndicatorExportingConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(IndicatorExportingConfiguration aConfiguration) {
		this.configuration = aConfiguration;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeSelectionBtnTimeObserveWidget = WidgetProperties.selection().observe(btnTime);
		IObservableValue includingTimeGetConfigurationObserveValue = PojoProperties.value("includingTime").observe(getConfiguration());
		bindingContext.bindValue(observeSelectionBtnTimeObserveWidget, includingTimeGetConfigurationObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnPriceObserveWidget = WidgetProperties.selection().observe(btnPrice);
		IObservableValue includingPriceGetConfigurationObserveValue = PojoProperties.value("includingPrice").observe(getConfiguration());
		bindingContext.bindValue(observeSelectionBtnPriceObserveWidget, includingPriceGetConfigurationObserveValue, null, null);
		//
		return bindingContext;
	}
}
