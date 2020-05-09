package com.mfg.utils.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ColorSamplesComposite extends Composite {
	public static final String PROP_COLOR = "color";
	final Canvas _allColorsCanvas;
	float _h;
	float _s;
	float _v;
	protected Color _color;
	private final RGB[] RGB_LIST = { new RGB(255, 0, 0), new RGB(255, 255, 0),
			new RGB(0, 255, 0), new RGB(0, 255, 255), new RGB(0, 0, 255), };
	private final Canvas _mainColorsCanvas;
	final Canvas _recentCanvas;

	public static final Set<RGB> recentColors = new LinkedHashSet<>();

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorSamplesComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		_allColorsCanvas = new Canvas(this, SWT.NO_REDRAW_RESIZE
				| SWT.DOUBLE_BUFFERED);
		GridData gd_canvas = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1);
		gd_canvas.widthHint = 255;
		gd_canvas.heightHint = 255;
		_allColorsCanvas.setLayoutData(gd_canvas);

		_mainColorsCanvas = new Canvas(this, SWT.NO_REDRAW_RESIZE
				| SWT.DOUBLE_BUFFERED);
		gd__mainColorsCanvas = new GridData(SWT.LEFT, SWT.FILL, false, false,
				1, 1);
		gd__mainColorsCanvas.widthHint = 50;
		_mainColorsCanvas.setLayoutData(gd__mainColorsCanvas);

		Group grpRecent = new Group(this, SWT.NONE);
		grpRecent.setLayout(new GridLayout(1, true));
		grpRecent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		grpRecent.setText("Recent");

		_recentCanvas = new Canvas(grpRecent, SWT.NO_REDRAW_RESIZE
				| SWT.DOUBLE_BUFFERED);
		_recentCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		Button btnClear = formToolkit
				.createButton(grpRecent, "Clear", SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				recentColors.clear();
				_recentCanvas.redraw();
			}
		});
		btnClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		_h = 1f;
		_s = 1f;
		_v = 1f;

		ColorMouseListener l = new ColorMouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				super.mouseUp(e);
				getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						_recentCanvas.redraw();
					}
				});

			}

			@Override
			protected void update(MouseEvent e) {
				Image image = new Image(e.display, 1, 1);
				GC gc = new GC((Canvas) e.widget);
				gc.copyArea(image, e.x, e.y);
				gc.dispose();
				ImageData d = image.getImageData();
				int p = d.getPixel(0, 0);
				RGB rgb = d.palette.getRGB(p);

				paintColor(rgb);
				Color oldColor = _color;
				_color = new Color(getDisplay(), rgb);
				_support.firePropertyChange(PROP_COLOR, oldColor, _color);

				if (!_pressed) {
					addRecent(rgb);
				}
			}
		};
		_allColorsCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				paintAllColorsCanvas(e);
			}
		});
		_allColorsCanvas.addMouseListener(l);
		_allColorsCanvas.addMouseMoveListener(l);

		_mainColorsCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				paintMainColorsCanvas(e);
			}
		});
		_mainColorsCanvas.addMouseListener(l);
		_mainColorsCanvas.addMouseMoveListener(l);

		_recentCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				paintRecentColorsCanvas(e);
			}
		});
		_recentCanvas.addMouseListener(l);
		_recentCanvas.addMouseMoveListener(l);
	}

	protected void paintRecentColorsCanvas(PaintEvent e) {
		int cols = 10;
		int rows = 10;
		int xspace = e.width / cols;
		int yspace = e.height / rows;
		RGB[] colors = new RGB[recentColors.size()];
		recentColors.toArray(colors);
		for (int i = 0; i < colors.length; i++) {
			RGB rgb = colors[i];
			Color c = new Color(getDisplay(), rgb);
			int x = i % cols;
			int y = i / cols;
			e.gc.setBackground(c);
			e.gc.fillRectangle(x * xspace, y * yspace, xspace - 5, yspace - 5);
			c.dispose();
		}
	}

	protected void paintMainColorsCanvas(PaintEvent e) {
		GC gc = e.gc;
		{
			int len = RGB_LIST.length;
			int space = e.height / 2 / len;
			for (int i = 0; i < len; i++) {
				RGB rgb = RGB_LIST[i];
				Color color = new Color(getDisplay(), rgb);
				gc.setBackground(color);
				gc.fillRectangle(0, i * space, e.width, space);
				color.dispose();
			}
		}

		{
			int len = 15;
			int space = e.height / 2 / len;
			int colorSpace = 255 / len;
			for (int i = 0; i <= len; i++) {
				int v = 255 - i * colorSpace;
				Color c = new Color(getDisplay(), v, v, v);
				gc.setBackground(c);
				gc.fillRectangle(0, e.height / 2 + i * space, e.width, space);
			}
		}

	}

	protected void paintAllColorsCanvas(PaintEvent e) {
		GC gc = e.gc;

		int cx = e.width / 2;
		int cy = e.height / 2;

		// top
		for (float i = 0; i < e.width; i++) {
			float scale = (i / e.width) * 90;
			float h = scale / 360;
			java.awt.Color c = java.awt.Color.getHSBColor(h, 1, 1);
			Color c2 = new Color(getDisplay(), c.getRed(), c.getGreen(),
					c.getBlue());
			gc.setForeground(c2);
			gc.drawLine((int) i, 0, cx, cy);
		}

		// right
		for (float i = 0; i < e.height; i++) {
			float scale = (i / e.height) * 90;
			float h = (90 + scale) / 360;
			java.awt.Color c = java.awt.Color.getHSBColor(h, 1, 1);
			Color c2 = new Color(getDisplay(), c.getRed(), c.getGreen(),
					c.getBlue());
			gc.setForeground(c2);
			gc.drawLine(e.width, (int) i, cx, cy);
		}

		// bottom
		for (float i = 0; i < e.width; i++) {
			float scale = (i / e.width) * 90;
			float h = (scale + 180) / 360;
			java.awt.Color c = java.awt.Color.getHSBColor(h, 1, 1);
			Color c2 = new Color(getDisplay(), c.getRed(), c.getGreen(),
					c.getBlue());
			gc.setForeground(c2);
			gc.drawLine(e.width - (int) i, e.height, cx, cy);
		}

		// left
		for (float i = 0; i < e.height; i++) {
			float scale = (i / e.height) * 90;
			float h = (270 + scale) / 360;
			java.awt.Color c = java.awt.Color.getHSBColor(h, 1, 1);
			Color c2 = new Color(getDisplay(), c.getRed(), c.getGreen(),
					c.getBlue());
			gc.setForeground(c2);
			gc.drawLine(0, e.height - (int) i, cx, cy);
			c2.dispose();
		}

		// saturation x value
		int w = e.width / 2;
		int h = e.height / 2;
		for (float x = 0; x < w; x++) {
			for (float y = 0; y < h; y++) {
				float S = x / w;
				float V = y / h;
				java.awt.Color c = java.awt.Color.getHSBColor(_h, S, V);
				Color c2 = new Color(getDisplay(), c.getRed(), c.getGreen(),
						c.getBlue());

				gc.setForeground(c2);
				// int x1 = e.width / 4 + (int) x;
				int w2 = (int) (x / w * (int) y);
				int x2 = e.width / 4 + (int) (w / 2 * (1 - y / h) + w2);
				int y1 = (int) (e.height * 3 / 4.0 - (int) y);
				gc.drawPoint(x2, y1);
				c2.dispose();
			}
		}
	}

	public Color getColor() {
		return _color;
	}

	public void setColor(Color color) {
		_color = color;
		paintColor(color.getRGB());
	}

	void paintColor(RGB rgb) {
		float[] comps = new float[3];
		java.awt.Color.RGBtoHSB(rgb.red, rgb.green, rgb.blue, comps);
		_h = comps[0];
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				_allColorsCanvas.redraw();
			}
		});
	}

	transient final PropertyChangeSupport _support = new PropertyChangeSupport(
			this);
	private final GridData gd__mainColorsCanvas;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());

	public void addPropertyChangeListener(PropertyChangeListener l) {
		_support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		_support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		_support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		_support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		_support.firePropertyChange(property, true, false);
	}

	void addRecent(RGB rgb) {
		recentColors.add(rgb);
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				_recentCanvas.redraw();
			}
		});
	}
}
