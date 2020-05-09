package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.MeterFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.utils.ui.ColorChooserButton;

public class MeterSettingsComp extends Composite {
	private ColorChooserButton _needle;
	private Button _gradient;
	private Composite _composite;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MeterSettingsComp(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblNeedleColor = new Label(this, SWT.NONE);
		GridData gd_lblNeedleColor = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblNeedleColor.widthHint = 100;
		lblNeedleColor.setLayoutData(gd_lblNeedleColor);
		lblNeedleColor.setText("Needle Color");

		_needle = new ColorChooserButton(this, SWT.NONE);
		GridData gd_color = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_color.heightHint = 30;
		gd_color.widthHint = 50;
		_needle.setLayoutData(gd_color);
		
		_composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		_composite.setLayout(gl_composite);
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
								
										_gradient = new Button(_composite, SWT.CHECK);
										_gradient.setText("Gradient");

	}

	public void updateFrom(MeterAdapter gauge) {
		MeterFigure fig = gauge.getFigure();
		_needle.setColor(fig.getNeedleColor().getRGB());
		_gradient.setSelection(fig.isGradient());
	}

	public void saveTo(MeterAdapter gauge) {
		MeterFigure fig = gauge.getFigure();
		fig.setNeedleColor(SWTResourceManager.getColor(_needle.getColor()));
		fig.setGradient(_gradient.getSelection());
	}

}
