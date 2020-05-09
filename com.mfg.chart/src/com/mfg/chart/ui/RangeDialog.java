package com.mfg.chart.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mfg.opengl.chart.PlotRange;

public class RangeDialog extends Dialog {
	private static final String PROP_DISABLE_AUTO_RANGE = "autoRange";
	private static final String PROP_YUPPER = "yupper";
	private static final String PROP_YLOWER = "ylower";
	private static final String PROP_XUPPER = "xupper";
	private static final String PROP_XLOWER = "xlower";
	private final RangeDialog _self = this;
	private double _ylower;
	private double _yupper;
	private double _xlower;
	private double _xupper;
	private boolean _autoRange;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public RangeDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@SuppressWarnings("unused")
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 3;
		new Label(container, SWT.NONE);

		Label _label = new Label(container, SWT.NONE);
		_label.setText("Lower");

		Label _label_1 = new Label(container, SWT.NONE);
		_label_1.setText("Upper");

		Label btnPriceRange = new Label(container, SWT.CHECK);
		btnPriceRange.setText("Price Range");

		_text = new Text(container, SWT.BORDER);
		_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		_text_1 = new Text(container, SWT.BORDER);
		_text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label btnTimeRange = new Label(container, SWT.CHECK);
		btnTimeRange.setText("Time Range");

		_text_2 = new Text(container, SWT.BORDER);
		_text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_text_3 = new Text(container, SWT.BORDER);
		_text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label _label_2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		_label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				3, 1));

		_btnDisableAutoRange = new Button(container, SWT.CHECK);
		_btnDisableAutoRange.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		_btnDisableAutoRange.setText("Auto Range");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		initDataBindings();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public boolean isAutoRange() {
		return _autoRange;
	}

	public void setAutoRange(boolean autoRange) {
		_autoRange = autoRange;
		firePropertyChange(PROP_DISABLE_AUTO_RANGE);
	}

	public double getXlower() {
		return _xlower;
	}

	public void setXlower(double xlower) {
		_xlower = xlower;
		firePropertyChange(PROP_XLOWER);
	}

	public double getXupper() {
		return _xupper;
	}

	public void setXupper(double xupper) {
		_xupper = xupper;
		firePropertyChange(PROP_XUPPER);
	}

	public double getYlower() {
		return _ylower;
	}

	public void setYlower(double ylower) {
		_ylower = ylower;
		firePropertyChange(PROP_YLOWER);
	}

	public double getYupper() {
		return _yupper;
	}

	public void setYupper(double yupper) {
		_yupper = yupper;
		firePropertyChange(PROP_YUPPER);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private Text _text;
	private Text _text_1;
	private Text _text_2;
	private Text _text_3;
	private Button _btnDisableAutoRange;

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(_text);
		IObservableValue ylower_selfObserveValue = BeanProperties.value(
				"ylower").observe(_self);
		bindingContext.bindValue(observeText_textObserveWidget,
				ylower_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_1);
		IObservableValue yupper_selfObserveValue = BeanProperties.value(
				"yupper").observe(_self);
		bindingContext.bindValue(observeText_text_1ObserveWidget,
				yupper_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_2);
		IObservableValue xlower_selfObserveValue = BeanProperties.value(
				"xlower").observe(_self);
		bindingContext.bindValue(observeText_text_2ObserveWidget,
				xlower_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_3ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_3);
		IObservableValue xupper_selfObserveValue = BeanProperties.value(
				"xupper").observe(_self);
		bindingContext.bindValue(observeText_text_3ObserveWidget,
				xupper_selfObserveValue, null, null);
		//
		IObservableValue observeSelection_btnDisableAutoRangeObserveWidget = WidgetProperties
				.selection().observe(_btnDisableAutoRange);
		IObservableValue disableAutoRange_selfObserveValue = BeanProperties
				.value("autoRange").observe(_self);
		bindingContext.bindValue(
				observeSelection_btnDisableAutoRangeObserveWidget,
				disableAutoRange_selfObserveValue, null, null);
		//
		return bindingContext;
	}

	public void setTimeRange(PlotRange range) {
		setXlower(range.lower);
		setXupper(range.upper);
	}

	public PlotRange getTimeRange() {
		return new PlotRange(_xlower, _xupper);
	}

	public void setPriceRange(PlotRange range) {
		setYlower(range.lower);
		setYupper(range.upper);
	}

	public PlotRange getPriceRange() {
		return new PlotRange(_ylower, _yupper);
	}
}
