package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import com.mfg.utils.ui.ColorChooserButton;

import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

public class GaugeSettingsComp extends Composite {
	private ColorChooserButton _needle;
	private Button _effect3d;
	private Button _gradient;
	private Composite _composite;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GaugeSettingsComp(Composite parent, int style) {
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
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		_composite.setLayout(gl_composite);
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
				
						_effect3d = new Button(_composite, SWT.CHECK);
						_effect3d.setText("Effect 3D");
								
										_gradient = new Button(_composite, SWT.CHECK);
										_gradient.setText("Gradient");

	}

	public void updateFrom(GaugeAdapter gauge) {
		GaugeFigure fig = gauge.getFigure();
		_needle.setColor(fig.getNeedleColor().getRGB());
		_effect3d.setSelection(fig.isEffect3D());
		_gradient.setSelection(fig.isGradient());
	}

	public void saveTo(GaugeAdapter gauge) {
		GaugeFigure fig = gauge.getFigure();
		fig.setNeedleColor(SWTResourceManager.getColor(_needle.getColor()));
		fig.setEffect3D(_effect3d.getSelection());
		fig.setGradient(_gradient.getSelection());
	}

}
