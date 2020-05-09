package com.mfg.utils.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class CustomColorsComposite extends Composite {

	protected Control _selected;
	public static final String PROP_COLOR = "color";
	public static final List<RGB> _savedColors = new ArrayList<>();

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CustomColorsComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(16, true);
		setLayout(gridLayout);

		Label lblNewLabel = new Label(this, SWT.BORDER);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		gd_lblNewLabel.widthHint = 15;
		gd_lblNewLabel.heightHint = 15;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label = new Label(this, SWT.BORDER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_1 = new Label(this, SWT.BORDER);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_2 = new Label(this, SWT.BORDER);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_3 = new Label(this, SWT.BORDER);
		label_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_4 = new Label(this, SWT.BORDER);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_5 = new Label(this, SWT.BORDER);
		label_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_6 = new Label(this, SWT.BORDER);
		label_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_7 = new Label(this, SWT.BORDER);
		label_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_16 = new Label(this, SWT.BORDER);
		label_16.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_16.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_17 = new Label(this, SWT.BORDER);
		label_17.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_17.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_18 = new Label(this, SWT.BORDER);
		label_18.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_18.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_19 = new Label(this, SWT.BORDER);
		label_19.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_19.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_20 = new Label(this, SWT.BORDER);
		label_20.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_20.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_21 = new Label(this, SWT.BORDER);
		label_21.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_22 = new Label(this, SWT.BORDER);
		label_22.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_22.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_8 = new Label(this, SWT.BORDER);
		label_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_9 = new Label(this, SWT.BORDER);
		label_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label_9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_10 = new Label(this, SWT.BORDER);
		label_10.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_11 = new Label(this, SWT.BORDER);
		label_11.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		label_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_12 = new Label(this, SWT.BORDER);
		label_12.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_13 = new Label(this, SWT.BORDER);
		label_13.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		label_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_14 = new Label(this, SWT.BORDER);
		label_14.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		label_14.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_15 = new Label(this, SWT.BORDER);
		label_15.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		label_15.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_23 = new Label(this, SWT.BORDER);
		label_23.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_23.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_24 = new Label(this, SWT.BORDER);
		label_24.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_24.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_25 = new Label(this, SWT.BORDER);
		label_25.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_25.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_26 = new Label(this, SWT.BORDER);
		label_26.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_26.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_27 = new Label(this, SWT.BORDER);
		label_27.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_27.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_28 = new Label(this, SWT.BORDER);
		label_28.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_28.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_29 = new Label(this, SWT.BORDER);
		label_29.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_29.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label label_30 = new Label(this, SWT.BORDER);
		label_30.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_30.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		synchronized (_savedColors) {
			if (_savedColors.isEmpty()) {
				for (Control c : getChildren()) {
					_savedColors.add(c.getBackground().getRGB());
				}
			} else {
				int i = 0;
				for (Control c : getChildren()) {
					c.setBackground(new Color(getDisplay(), _savedColors.get(i)));
					i++;
				}
			}
		}
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				synchronized (_savedColors) {
					int i = 0;
					for (Control c : getChildren()) {
						_savedColors.set(i, c.getBackground().getRGB());
						i++;
					}
				}
			}
		});
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(getDisplay().getSystemColor(
						SWT.COLOR_DARK_GRAY));
				for (final Control c : getChildren()) {
					if (_selected == c) {
						Rectangle b = c.getBounds();
						e.gc.setLineDash(new int[] { 2, 2 });
						e.gc.drawRectangle(b.x - 2, b.y - 2, b.width + 3,
								b.height + 3);
						break;
					}
				}
			}
		});
		for (final Control c : getChildren()) {
			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					setSelected(c);
					if (!c.getBackground().getRGB()
							.equals(new RGB(255, 255, 255))) {
						firePropertyChange(PROP_COLOR);
					}
				}
			});
		}
		setSelected(getChildren()[0]);

	}

	void setSelected(Control selected) {
		_selected = selected;
		repaint();
	}

	public RGB getColor() {
		return _selected.getBackground().getRGB();
	}

	void repaint() {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				redraw();
			}
		});
	}

	public void addColor(RGB color) {
		_selected.setBackground(SWTResourceManager.getColor(color));
		repaint();
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
