package com.mfg.chart.ui.settings;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class LineTypeButton extends Composite {
	public static final String PROP_LINE_TYPE = "lineType";
	private final Button _button;
	private int _lineType = 0;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LineTypeButton(Composite parent, int style) {
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

	public int getLineType() {
		return _lineType;
	}

	public void setLineType(int lineType) {
		_lineType = lineType;
		updateButton();
		firePropertyChange(PROP_LINE_TYPE);
	}

	private void updateButton() {
		_button.setImage(SettingsUtils.getLineTypeImage(_lineType));
		// _button.setText(_lineType + " px ");
	}

	protected void showDialog() {
		int type = SettingsUtils.openLineTypeDialog(getShell(), _lineType);
		if (type != -1) {
			setLineType(type);
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

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main2(String[] args) throws IOException {
		for (float type : new float[] { 0, 1, 3, 5, 7, 9, 11, 13, 15 }) {
			BufferedImage img = new BufferedImage(120, 16,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = img.createGraphics();

			g2.setPaint(java.awt.Color.black);
			if (type == 0) {
				g2.fillRect(0, 8 - 2, img.getWidth(), 4);
			} else {
				int x = (img.getWidth() % (int) type) / 2;
				while (x < img.getWidth()) {
					g2.fillRect(x, 8 - 2, (int) type, 4);
					x += type * 2;
				}
			}
			g2.dispose();

			ImageIO.write(img, "png", new File("type" + (int) type + ".png"));
		}

	}

}
