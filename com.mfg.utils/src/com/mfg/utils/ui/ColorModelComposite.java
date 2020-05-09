package com.mfg.utils.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

public class ColorModelComposite extends Composite {
	public static final String PROP_COLOR = "color";
	final Canvas _xyCanvas;
	IColorModel _model;
	int _x, _y, _z;
	final Canvas _zCanvas;
	private final Button _btnComp1;
	private final Button _btnComp2;
	private final Button _btnComp3;
	private HashMap<Object, Object> _radioMap;
	HashMap<Object, Object> _sliderMap;
	protected int _selectedComp;
	final Slider _slider1;
	final Slider _slider3;
	final Slider _slider2;
	private final Spinner _spinner1;
	private final Spinner _spinner2;
	private final Spinner _spinner3;
	private RGB _color;
	protected boolean _spinnerChanging;

	public interface IColorModel {
		public RGB getColor(int x, int y, int z);

		public String[] getComponentNames();

		public int[] getComponentSizes();

		public RGB getSliderColor(int i, int selectedComp, int x, int y, int z);

		public int[] getComponents(RGB color);
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorModelComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		_xyCanvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE
				| SWT.DOUBLE_BUFFERED);
		GridData gd_xyCanvas = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gd_xyCanvas.heightHint = 255;
		gd_xyCanvas.widthHint = 255;
		_xyCanvas.setLayoutData(gd_xyCanvas);

		_zCanvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE
				| SWT.DOUBLE_BUFFERED);
		GridData gd__yCanvas = new GridData(SWT.LEFT, SWT.TOP, false, false, 1,
				1);
		gd__yCanvas.heightHint = 255;
		gd__yCanvas.widthHint = 50;
		_zCanvas.setLayoutData(gd__yCanvas);

		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				2, 1));

		_btnComp1 = new Button(composite, SWT.RADIO);

		_btnComp1.setText("Comp 1");

		_slider1 = new Slider(composite, SWT.NONE);
		_slider1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_spinner1 = new Spinner(composite, SWT.BORDER);

		_btnComp2 = new Button(composite, SWT.RADIO);
		_btnComp2.setText("Comp 2");

		_slider2 = new Slider(composite, SWT.NONE);
		_slider2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_spinner2 = new Spinner(composite, SWT.BORDER);

		_btnComp3 = new Button(composite, SWT.RADIO);
		_btnComp3.setText("Comp 3");

		_slider3 = new Slider(composite, SWT.NONE);
		_slider3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_spinner3 = new Spinner(composite, SWT.BORDER);

		afterCreateWidgets();
	}

	class ZMouseListener extends ColorMouseListener {
		@Override
		protected void update(MouseEvent e) {
			int v = Math.max(0, Math.min(255, e.y));
			switch (_selectedComp) {
			case 0:
				_x = scaleToCompSize(255 - v, 0);
				break;
			case 1:
				_y = scaleToCompSize(255 - v, 1);
				break;
			default:
				_z = scaleToCompSize(255 - v, 2);
				break;
			}
			updateAll(true);

			if (_pressed) {
				ColorSamplesComposite.recentColors.add(getColor());
			}
		}
	}

	class XYMouseListener extends ColorMouseListener {
		@Override
		protected void update(MouseEvent e) {
			Rectangle b = _xyCanvas.getBounds();
			if (e.x < 0 || e.x > b.width || e.y < 0 || e.y > b.height) {
				return;
			}

			switch (_selectedComp) {
			case 0:
				_y = scaleToCompSize(e.x, 1);
				_z = scaleToCompSize(e.y, 2);
				break;
			case 1:
				_x = scaleToCompSize(e.x, 0);
				_z = scaleToCompSize(e.y, 2);
				break;
			default:
				_x = scaleToCompSize(e.x, 0);
				_y = scaleToCompSize(e.y, 1);
				break;
			}
			updateAll(true);

			if (!_pressed) {
				ColorSamplesComposite.recentColors.add(getColor());
			}
		}
	}

	private void afterCreateWidgets() {
		_x = 0;
		_y = 0;
		_z = 0;

		_selectedComp = 0;
		_btnComp1.setSelection(true);
		_xyCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				paintXYCanvas(e);
			}
		});
		XYMouseListener xyListener = new XYMouseListener();
		_xyCanvas.addMouseListener(xyListener);
		_xyCanvas.addMouseMoveListener(xyListener);

		_zCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				paintZCanvas(e);
			}
		});

		ZMouseListener listener = new ZMouseListener();
		_zCanvas.addMouseListener(listener);
		_zCanvas.addMouseMoveListener(listener);

		setColorModel(new RGBModel());
		// setColorModel(new HSBModel(getDisplay()));
	}

	protected void paintZCanvas(PaintEvent e) {
		IColorModel schema = _model;
		if (schema != null) {
			GC gc = e.gc;
			for (int i = 0; i < 256; i++) {
				RGB color = _model.getSliderColor(
						scaleToCompSize(255 - i, _selectedComp), _selectedComp,
						_x, _y, _z);
				// out.println(color.toString());
				gc.setForeground(SWTResourceManager.getColor(color));
				gc.drawLine(0, i, e.width, i);
			}
			int y;
			switch (_selectedComp) {
			case 0:
				y = scaleTo255(_x, 0);
				break;
			case 1:
				y = scaleTo255(_y, 1);
				break;
			default:
				y = scaleTo255(_z, 2);
				break;
			}
			y = Math.min(253, 255 - y);

			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(0, y - 2, e.width, 5);
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.fillRectangle(1, y - 1, e.width - 2, 3);
		}
	}

	protected void paintXYCanvas(PaintEvent e) {
		IColorModel schema = _model;
		if (schema != null) {
			GC gc = e.gc;
			for (int i = 0; i < 256; i++) {
				for (int j = 0; j < 256; j++) {
					RGB color;
					switch (_selectedComp) {
					case 0:
						color = schema.getColor(_x, scaleToCompSize(i, 1),
								scaleToCompSize(j, 2));
						break;
					case 1:
						color = schema.getColor(scaleToCompSize(i, 0), _y,
								scaleToCompSize(j, 2));
						break;
					default:
						color = schema.getColor(scaleToCompSize(i, 0),
								scaleToCompSize(j, 1), _z);
						break;
					}
					// paint color
					Color c2 = SWTResourceManager.getColor(color);
					gc.setForeground(c2);
					gc.drawPoint(i, j);
				}
			}
			int selx;
			int sely;
			switch (_selectedComp) {
			case 0:
				selx = scaleTo255(_y, 1);
				sely = scaleTo255(_z, 2);
				break;
			case 1:
				selx = scaleTo255(_x, 0);
				sely = scaleTo255(_z, 2);
				break;
			default:
				selx = scaleTo255(_x, 0);
				sely = scaleTo255(_y, 1);
				break;
			}

			// paint cross
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.drawLine(selx - 15, sely, selx + 15, sely);
			gc.drawLine(selx, sely - 15, selx, sely + 15);

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.drawLine(selx - 15 - 1, sely - 1, selx + 15 - 1, sely - 1);
			gc.drawLine(selx - 1, sely - 15 - 1, selx - 1, sely + 15 - 1);
		}
	}

	public int scaleTo255(int value, int comp) {
		int[] sizes = _model.getComponentSizes();
		return (int) (value / (double) sizes[comp] * 255);

	}

	public int scaleToCompSize(int value, int comp) {
		int[] sizes = _model.getComponentSizes();
		return (int) (value / 255.0 * sizes[comp]);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setColorModel(IColorModel schema) {
		_model = schema;
		_selectedComp = 0;

		_radioMap = new HashMap<>();
		_sliderMap = new HashMap<>();
		Button[] btns = { _btnComp1, _btnComp2, _btnComp3 };
		Slider[] scales = { _slider1, _slider2, _slider3 };
		Spinner[] spins = { _spinner1, _spinner2, _spinner3 };
		int[] sizes = _model.getComponentSizes();
		int i = 0;
		for (String name : _model.getComponentNames()) {
			Button btn = btns[i];
			btn.setText(name);
			Integer ii = new Integer(i);
			_radioMap.put(btn, ii);
			_radioMap.put(ii, btn);

			final int fi = i;
			btn.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					_selectedComp = fi;
					updateAll(true);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					//
				}
			});

			final Spinner spin = spins[i];
			spin.setMinimum(0);
			spin.setMaximum(sizes[i]);
			spin.setIncrement(1);

			final Slider slider = scales[i];
			_sliderMap.put(slider, ii);
			_sliderMap.put(ii, slider);
			slider.setMinimum(spin.getMinimum());
			slider.setMaximum(spin.getMaximum());

			slider.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int sel = slider.getSelection();
					updateSpinAndSliderWithSelection(fi, sel);
					spin.setSelection(sel);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					//
				}
			});
			spin.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					_spinnerChanging = true;
					int sel = spin.getSelection();
					updateSpinAndSliderWithSelection(fi, sel);
					slider.setSelection(sel);
					_spinnerChanging = false;
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					//
				}
			});
			i++;
		}

		updateAll(false);
	}

	public IColorModel getColorModel() {
		return _model;
	}

	public RGB getColor() {
		return _color;
	}

	public void setColor(RGB color) {
		_color = color;
		int[] comps = _model.getComponents(color);
		_x = comps[0];
		_y = comps[1];
		_z = comps[2];
		updateAll(false);
	}

	void updateAll(boolean fireColor) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				_xyCanvas.redraw();
				_zCanvas.redraw();
			}
		});
		_slider1.setSelection(_x);
		_slider2.setSelection(_y);
		_slider3.setSelection(_z);
		if (!_spinnerChanging) {
			_spinner1.setSelection(_x);
			_spinner2.setSelection(_y);
			_spinner3.setSelection(_z);
		}

		RGB newColor = _model.getColor(_x, _y, _z);
		if (fireColor) {
			if (_color == null || !newColor.equals(_color)) {
				support.firePropertyChange(PROP_COLOR, _color,
						_color = newColor);
			}
		} else {
			_color = newColor;
		}
	}

	void updateSpinAndSliderWithSelection(final int fi, int selection) {
		switch (fi) {
		case 0:
			_x = selection;
			break;
		case 1:
			_y = selection;
			break;
		default:
			_z = selection;
			break;
		}
		updateAll(true);
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
