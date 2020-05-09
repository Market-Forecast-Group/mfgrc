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

public class ShapeWidthButton extends Composite {
	public static final String PROP_SHAPE_WIDTH = "shapeWidth";
	private final Button _button;
	private int _shapeWidth = 0;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ShapeWidthButton(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginTop = 5;
		gridLayout.marginBottom = 5;
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
		_button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		afterCreateWidgets();

	}

	private void afterCreateWidgets() {
		updateButton();
	}

	public int getShapeWidth() {
		return _shapeWidth;
	}

	public void setShapeWidth(int shapeWidth) {
		_shapeWidth = shapeWidth;
		updateButton();
		firePropertyChange(PROP_SHAPE_WIDTH);
	}

	private void updateButton() {
		_button.setImage(SettingsUtils.getShapeWidthImage(_shapeWidth));
	}

	protected void showDialog() {
		int type = SettingsUtils.openShapeWidthDialog(getShell(), _shapeWidth);
		if (type != -1) {
			setShapeWidth(type);
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
