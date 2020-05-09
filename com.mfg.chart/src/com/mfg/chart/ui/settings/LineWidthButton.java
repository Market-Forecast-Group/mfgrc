package com.mfg.chart.ui.settings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class LineWidthButton extends Composite {
	public static final String PROP_LINE_WIDTH = "lineWidth";
	private final Button _button;
	private int _lineWidth = 1;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LineWidthButton(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		_button = new Button(this, SWT.NONE);
		_button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showDialog();
			}
		});
		_button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		afterCreateWidgets();

	}

	private void afterCreateWidgets() {
		updateButton();
	}

	public int getLineWidth() {
		return _lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		_lineWidth = lineWidth;
		updateButton();
		firePropertyChange(PROP_LINE_WIDTH);
	}

	private void updateButton() {
		_button.setImage(SettingsUtils.getLineWidthImage(_lineWidth));
		_button.setText(_lineWidth + " px ");
	}

	protected void showDialog() {
		int width = SettingsUtils.openLineWidthDialog(getShell(), _lineWidth);
		if (width != -1) {
			setLineWidth(width);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
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

}
