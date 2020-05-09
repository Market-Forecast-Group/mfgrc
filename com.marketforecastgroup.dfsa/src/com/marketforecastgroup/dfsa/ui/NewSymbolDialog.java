package com.marketforecastgroup.dfsa.ui;

import static java.lang.System.out;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.TimeZone;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mfg.common.DfsSymbol;
import com.mfg.utils.DataBindingUtils;

public class NewSymbolDialog extends Dialog {
	private static class TypeLabelProvider extends LabelProvider {
		public TypeLabelProvider() {
		}

		@Override
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			return DfsSymbol.TYPES__DEF_NAME_MAP.get(element);
		}
	}

	private static final String PROP_TIME_ZONE = "timeZone";
	public static final String TZ_AMERICA_NEW_YORK = "America/New_York";
	private static final String PROP_NAME = "name";
	private static final String ROP_TICK_SIZE = "tickSize";
	public static final String USD = "USD";
	public static final String EUR = "EUR";

	private DataBindingContext m_bindingContext;

	private Text _text;
	private Text _text_1;
	private final NewSymbolDialog _self = this;
	private String _name;
	private double _tickSize;
	IInputValidator _nameValidator;
	private String _timeZone;
	private double _tickValue;
	private String _currency = USD;
	private String _type = DfsSymbol.TYPE_FUTURES;
	private boolean _runSchedulerNow = true;

	public class Validator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			if (_nameValidator != null) {
				String msg = _nameValidator.isValid((String) value);
				if (msg != null) {
					return ValidationStatus.error(msg);
				}

			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public NewSymbolDialog(Shell parentShell) {
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
		gridLayout.numColumns = 2;

		Label lblName = new Label(container, SWT.NONE);
		lblName.setText("Name");

		_text = new Text(container, SWT.BORDER);
		_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblTickSize = new Label(container, SWT.NONE);
		lblTickSize.setText("Tick Size");

		_text_1 = new Text(container, SWT.BORDER);
		_text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblTickValue = new Label(container, SWT.NONE);
		lblTickValue.setText("Tick Value");

		_tickValueText = new Text(container, SWT.BORDER);
		_tickValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblCurrency = new Label(container, SWT.NONE);
		lblCurrency.setText("Currency");

		_currencyCombo = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo_1 = _currencyCombo.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		_currencyCombo.setContentProvider(new ArrayContentProvider());
		_currencyCombo.setLabelProvider(new LabelProvider());

		Label lblType = new Label(container, SWT.NONE);
		lblType.setText("Type");

		_typeCombo = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo_2 = _typeCombo.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		_typeCombo.setLabelProvider(new TypeLabelProvider());
		_typeCombo.setContentProvider(new ArrayContentProvider());

		Label lblTimeZone = new Label(container, SWT.NONE);
		lblTimeZone.setText("Time Zone");

		_tzViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo = _tzViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		_btnRunTheScheduler = new Button(container, SWT.CHECK);
		_btnRunTheScheduler.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		_btnRunTheScheduler.setText("Run the scheduler now");
		new Label(container, SWT.NONE);
		_tzViewer.setContentProvider(new ArrayContentProvider());
		_tzViewer.setInput(TimeZone.getAvailableIDs());

		afterCreateWidgets();

		return container;
	}

	private void afterCreateWidgets() {
		// TODO: should be an enum somewhere there
		_currencyCombo.setInput(new String[] { USD, EUR });
		_typeCombo.setInput(DfsSymbol.TYPES);
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
		firePropertyChange(PROP_NAME);
	}

	public double getTickSize() {
		return _tickSize;
	}

	public void setTickSize(double tickSize) {
		_tickSize = tickSize;
		firePropertyChange(ROP_TICK_SIZE);
	}

	public String getTimeZone() {
		return _timeZone;
	}

	public void setTimeZone(String timeZone) {
		_timeZone = timeZone;
		firePropertyChange(PROP_TIME_ZONE);
	}

	public double getTickValue() {
		return _tickValue;
	}

	public void setTickValue(double tickValue) {
		_tickValue = tickValue;
		firePropertyChange("tickValue");
	}

	public String getCurrency() {
		return _currency;
	}

	public void setCurrency(String currency) {
		_currency = currency;
		firePropertyChange("currency");
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
		firePropertyChange("type");
	}

	public void setRunSchedulerNow(boolean runSchedulerNow) {
		_runSchedulerNow = runSchedulerNow;
		firePropertyChange("runSchedulerNow");
	}

	public boolean isRunSchedulerNow() {
		return _runSchedulerNow;
	}

	public void setNameValidator(IInputValidator nameValidator) {
		_nameValidator = nameValidator;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	Binding _nameBinding;
	private ComboViewer _tzViewer;
	private Text _tickValueText;
	private ComboViewer _currencyCombo;
	private Button _btnRunTheScheduler;
	private ComboViewer _typeCombo;

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
		m_bindingContext = initDataBindings();
		afterCreateBindings();
	}

	private void afterCreateBindings() {
		DataBindingUtils.decorateBindings(m_bindingContext);
		_nameBinding.getValidationStatus().addChangeListener(
				new IChangeListener() {

					@Override
					public void handleChange(ChangeEvent event) {
						@SuppressWarnings("synthetic-access")
						Button btn = getButton(IDialogConstants.OK_ID);
						IStatus status = (IStatus) _nameBinding
								.getValidationStatus().getValue();
						out.println(status);
						boolean ok = status.isOK();
						btn.setEnabled(ok);
					}
				});
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(519, 393);
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(SWT.Modify).observe(_text);
		IObservableValue name_selfObserveValue = BeanProperties.value("name").observe(_self);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new Validator());
		_nameBinding = bindingContext.bindValue(observeText_textObserveWidget, name_selfObserveValue, strategy, null);
		//
		IObservableValue observeText_text_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(_text_1);
		IObservableValue tickSize_selfObserveValue = BeanProperties.value("tickSize").observe(_self);
		bindingContext.bindValue(observeText_text_1ObserveWidget, tickSize_selfObserveValue, null, null);
		//
		IObservableValue observeSingleSelection_tzViewer = ViewerProperties.singleSelection().observe(_tzViewer);
		IObservableValue timeZone_selfObserveValue = BeanProperties.value("timeZone").observe(_self);
		bindingContext.bindValue(observeSingleSelection_tzViewer, timeZone_selfObserveValue, null, null);
		//
		IObservableValue observeText_tickValueTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(_tickValueText);
		IObservableValue tickValue_selfObserveValue = BeanProperties.value("tickValue").observe(_self);
		bindingContext.bindValue(observeText_tickValueTextObserveWidget, tickValue_selfObserveValue, null, null);
		//
		IObservableValue observeSingleSelection_currencyCombo = ViewerProperties.singleSelection().observe(_currencyCombo);
		IObservableValue currency_selfObserveValue = BeanProperties.value("currency").observe(_self);
		bindingContext.bindValue(observeSingleSelection_currencyCombo, currency_selfObserveValue, null, null);
		//
		IObservableValue observeSelection_btnRunTheSchedulerObserveWidget = WidgetProperties.selection().observe(_btnRunTheScheduler);
		IObservableValue runSchedulerNow_selfObserveValue = BeanProperties.value("runSchedulerNow").observe(_self);
		bindingContext.bindValue(observeSelection_btnRunTheSchedulerObserveWidget, runSchedulerNow_selfObserveValue, null, null);
		//
		IObservableValue observeSingleSelection_typeCombo = ViewerProperties.singleSelection().observe(_typeCombo);
		IObservableValue type_selfObserveValue = BeanProperties.value("type").observe(_self);
		bindingContext.bindValue(observeSingleSelection_typeCombo, type_selfObserveValue, null, null);
		//
		return bindingContext;
	}
}
