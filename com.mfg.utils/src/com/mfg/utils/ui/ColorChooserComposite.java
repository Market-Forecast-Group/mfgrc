package com.mfg.utils.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class ColorChooserComposite extends Composite {
	final ColorModelComposite _HSBComposite;
	final ColorModelComposite _RGBComposite;
	final Composite _previewColorComp;
	private final Text _rgbText;
	private final Text _codeText;
	final BasicColorsComposite _basicColorsComposite;
	final CustomColorsComposite _customColorsComposite;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorChooserComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		composite_2.setLayout(new GridLayout(1, false));

		Label lblBasicColors = new Label(composite_2, SWT.NONE);
		GridData gd_lblBasicColors = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblBasicColors.horizontalIndent = 5;
		lblBasicColors.setLayoutData(gd_lblBasicColors);
		lblBasicColors.setText("Basic Colors");

		_basicColorsComposite = new BasicColorsComposite(composite_2, SWT.NONE);
		_basicColorsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false, 1, 1));

		Label lblCustomColors = new Label(composite_2, SWT.NONE);
		GridData gd_lblCustomColors = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblCustomColors.horizontalIndent = 5;
		lblCustomColors.setLayoutData(gd_lblCustomColors);
		lblCustomColors.setText("Custom Colors");

		_customColorsComposite = new CustomColorsComposite(composite_2,
				SWT.NONE);
		_customColorsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		Label lblPreview = new Label(composite_2, SWT.NONE);
		GridData gd_lblPreview = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblPreview.horizontalIndent = 5;
		lblPreview.setLayoutData(gd_lblPreview);
		lblPreview.setText("Preview");

		Composite grpPreview = new Composite(composite_2, SWT.NONE);
		grpPreview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		grpPreview.setSize(341, 15);
		grpPreview.setLayout(new GridLayout(2, false));

		_previewColorComp = new Composite(grpPreview, SWT.BORDER);
		GridData gd_previewColorComp = new GridData(SWT.FILL, SWT.FILL, false,
				true, 1, 1);
		gd_previewColorComp.widthHint = 100;
		_previewColorComp.setLayoutData(gd_previewColorComp);

		Composite composite_1 = new Composite(grpPreview, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));

		Label lblRgb = new Label(composite_1, SWT.NONE);
		lblRgb.setText("RGB");

		_rgbText = new Text(composite_1, SWT.BORDER);
		GridData gd_rgbText = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_rgbText.widthHint = 100;
		_rgbText.setLayoutData(gd_rgbText);

		Label lblCode = new Label(composite_1, SWT.NONE);
		lblCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblCode.setText("Code");

		_codeText = new Text(composite_1, SWT.BORDER);
		GridData gd_codeText = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_codeText.widthHint = 100;
		_codeText.setLayoutData(gd_codeText);

		Button btnAddCustomColor = new Button(composite_2, SWT.NONE);
		btnAddCustomColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addCustomColor();
			}
		});
		btnAddCustomColor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnAddCustomColor.setText("Add Custom Color");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false,
				1, 1));
		composite.setLayout(new GridLayout(1, false));

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		tabFolder.setSize(193, 394);

		TabItem tbtmHsb = new TabItem(tabFolder, SWT.NONE);
		tbtmHsb.setText("HSV");

		_HSBComposite = new ColorModelComposite(tabFolder, SWT.NONE);
		tbtmHsb.setControl(_HSBComposite);

		TabItem tbtmRgb = new TabItem(tabFolder, SWT.NONE);
		tbtmRgb.setText("RGB");

		_RGBComposite = new ColorModelComposite(tabFolder, SWT.NONE);
		tbtmRgb.setControl(_RGBComposite);
		afterCreateWidgets();
	}

	protected void addCustomColor() {
		_customColorsComposite.addColor(getColor());
	}

	private void afterCreateWidgets() {
		_HSBComposite.setColorModel(new HSBModel());

		_RGBComposite.addPropertyChangeListener(ColorModelComposite.PROP_COLOR,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						RGB color = (RGB) evt.getNewValue();
						_HSBComposite.setColor(color);
						setPreviewColor(color);
					}
				});
		_HSBComposite.addPropertyChangeListener(ColorModelComposite.PROP_COLOR,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						RGB color = (RGB) evt.getNewValue();
						_RGBComposite.setColor(color);
						setPreviewColor(color);
					}
				});
		_basicColorsComposite.addPropertyChangeListener(
				BasicColorsComposite.PROP_COLOR, new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setColor(_basicColorsComposite.getColor());
					}
				});
		_customColorsComposite.addPropertyChangeListener(
				CustomColorsComposite.PROP_COLOR, new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setColor(_customColorsComposite.getColor());
					}
				});
		setColor(_basicColorsComposite.getColor());

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public RGB getColor() {
		return _HSBComposite.getColor();
	}

	public void setColor(RGB color) {
		setPreviewColor(color);
		_HSBComposite.setColor(color);
		_RGBComposite.setColor(color);
		_basicColorsComposite.selectColor(color);
	}

	void setPreviewColor(RGB rgb) {
		Color color = SWTResourceManager.getColor(rgb);
		_previewColorComp.setBackground(color);

		int rgb2 = new java.awt.Color(color.getRed(), color.getGreen(),
				color.getBlue()).getRGB();
		_codeText.setText("#" + Integer.toHexString(rgb2).toUpperCase());
		_rgbText.setText(color.getRed() + ", " + color.getGreen() + ", "
				+ color.getBlue());
	}
}
