package com.mfg.utils.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public class ColorChooserButton extends Composite {

	private static final String PROP_TEXT = "text";
	public static final String PROP_COLOR = "color";
	private RGB _color;
	private final Button _button;
	private final Map<Color, Image> _imageMap = new HashMap<>();

	public static RGB colorToSWTColor(float[] color) {
		return colorToRGB(color);
	}

	public static RGB colorToRGB(float[] color) {
		int r = (int) (255 * color[0]);
		int g = (int) (255 * color[1]);
		int b = (int) (255 * color[2]);
		RGB rgb = new RGB(r, g, b);
		return rgb;
	}

	public static float[] rgbToColor(RGB rgb) {
		float[] color = new float[] { (float) rgb.red / 255,
				(float) rgb.green / 255, (float) rgb.blue / 255, 1 };
		return color;
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorChooserButton(Composite parent, int style) {
		super(parent, style);
		_color = new RGB(0, 0, 255);

		setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		_button = new Button(this, SWT.NONE);
		_button.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				paintButton(e);
			}
		});
		_button.setAlignment(SWT.LEFT);
		_button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeColor();
			}
		});
		afterCreateWidgets();
	}

	protected void paintButton(PaintEvent e) {
		try {
			GC gc = e.gc;
			gc.setBackground(SWTResourceManager.getColor(_color));
			gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
			int m = 6;
			gc.fillRectangle(m, m, e.width - m * 2 - 1, e.height - m * 2 - 1);
			gc.drawRectangle(m, m, e.width - m * 2 - 1, e.height - m * 2 - 1);
		} catch (Exception e1) {
			//
		}
	}

	private void afterCreateWidgets() {
		//
	}

	public RGB getColor() {
		return _color;
	}

	public float[] getGLColor() {
		return rgbToColor(_color);
	}

	public void setColor(RGB color) {
		setColor(color, true);
	}

	public void setColor(RGB color, boolean notify) {
		_color = color;
		_button.redraw();
		if (notify) {
			firePropertyChange(PROP_COLOR);
		}
	}

	public void setColor(float[] color) {
		setColor(color, false);
	}

	public void setColor(float[] color, boolean notify) {
		setColor(colorToSWTColor(color), notify);
	}

	public String getText() {
		return _button.getText();
	}

	public void setText(String text) {
		_button.setText(text);
		firePropertyChange(PROP_TEXT);
	}

	void changeColor() {
		ColorDialog2 dlg = new ColorDialog2(getShell());
		dlg.setRGB(_color);
		if (dlg.open() == Window.OK) {
			RGB rgb = dlg.getRGB();
			setColor(rgb);
		}
	}

	@Override
	public void dispose() {
		for (Image img : _imageMap.values()) {
			img.dispose();
		}
		super.dispose();
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
