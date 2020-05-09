/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.ui.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public class FractionField extends Composite {
	private final DataBindingContext m_bindingContext;
	private static final String PROP_DENOMINATOR = "denominator";
	private static final String PROP_NUMERATOR = "numerator";
	private final Text numeratorText;
	private final Text denominatorText;
	private int numerator;
	private int denominator;
	private final FractionField self = this;
	private final Label label;

	public static class DenominatorValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			return value.equals(Integer.valueOf(0)) ? ValidationStatus
					.error("Invalid denominator value.") : Status.OK_STATUS;
		}

	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FractionField(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		numeratorText = new Text(this, SWT.BORDER);
		GridData gd_numeratorText = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_numeratorText.minimumWidth = 30;
		numeratorText.setLayoutData(gd_numeratorText);

		label = new Label(this, SWT.NONE);
		label.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_BACKGROUND));
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1));
		label.setText("/");

		denominatorText = new Text(this, SWT.BORDER);
		GridData gd_denominatorText = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_denominatorText.minimumWidth = 30;
		denominatorText.setLayoutData(gd_denominatorText);

		afterCreateWidgets();
		m_bindingContext = initDataBindings();
		afterInitBindings();

	}

	/**
	 * 
	 */
	private void afterCreateWidgets() {
		numeratorText.setBackground(getParent().getBackground());
		denominatorText.setBackground(getParent().getBackground());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return numeratorText.setFocus();
	}

	private void afterInitBindings() {
		DataBindingUtils.decorateBindings(m_bindingContext);
	}

	/**
	 * @return the numeratorText
	 */
	public Text getNumeratorText() {
		return numeratorText;
	}

	/**
	 * @return the denominatorText
	 */
	public Text getDenominatorText() {
		return denominatorText;
	}

	/**
	 * @return the denominator
	 */
	public int getDenominator() {
		return denominator;
	}

	/**
	 * @param aDenominator
	 *            the denominator to set
	 */
	public void setDenominator(int aDenominator) {
		this.denominator = aDenominator;
		firePropertyChange(PROP_DENOMINATOR);
	}

	/**
	 * @return the numerator
	 */
	public int getNumerator() {
		return numerator;
	}

	/**
	 * @param aNumerator
	 *            the numerator to set
	 */
	public void setNumerator(int aNumerator) {
		this.numerator = aNumerator;
		firePropertyChange(PROP_NUMERATOR);
	}

	@Override
	protected void checkSubclass() {
		// Adding a comment to avoid empty block warning.
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

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
		IObservableValue observeTextNumeratorTextObserveWidget = WidgetProperties
				.text(new int[] { SWT.Modify, SWT.FocusOut,
						SWT.DefaultSelection }).observe(numeratorText);
		IObservableValue numeratorSelfObserveValue = BeanProperties.value(
				"numerator").observe(self);
		bindingContext.bindValue(observeTextNumeratorTextObserveWidget,
				numeratorSelfObserveValue, null, null);
		//
		IObservableValue observeTextDenominatorTextObserveWidget = WidgetProperties
				.text(new int[] { SWT.Modify, SWT.FocusOut,
						SWT.DefaultSelection }).observe(denominatorText);
		IObservableValue denominatorSelfObserveValue = BeanProperties.value(
				"denominator").observe(self);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new DenominatorValidator());
		bindingContext.bindValue(observeTextDenominatorTextObserveWidget,
				denominatorSelfObserveValue, strategy, null);
		//
		return bindingContext;
	}
}
