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

public class ShapeButton extends Composite {
	public static final String PROP_SHAPE_TYPE = "shapeType";
	private final Button _button;
	private int _shapeType = 0;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ShapeButton(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginBottom = 5;
		gridLayout.marginTop = 5;
		gridLayout.verticalSpacing = 0;
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

	public int getShapeType() {
		return _shapeType;
	}

	public void setShapeType(int lineType) {
		_shapeType = lineType;
		updateButton();
		firePropertyChange(PROP_SHAPE_TYPE);
	}

	private void updateButton() {
		_button.setImage(SettingsUtils.getShapeTypeImage(_shapeType));
	}

	protected void showDialog() {
		int type = SettingsUtils.openShapeTypeDialog(getShell(), _shapeType);
		if (type != -1) {
			setShapeType(type);
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
