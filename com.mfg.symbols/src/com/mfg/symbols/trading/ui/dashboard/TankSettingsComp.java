package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.TankFigure;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

import com.mfg.utils.ui.ColorChooserButton;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class TankSettingsComp extends Composite {
	private Button _effect3d;
	private ColorChooserButton _fillColor;
	private ColorChooserButton _fillBg;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public TankSettingsComp(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblFillColor = new Label(this, SWT.NONE);
		lblFillColor.setText("Fill Color");

		_fillColor = new ColorChooserButton(this, SWT.NONE);
		GridData gd__fillColor = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__fillColor.widthHint = 50;
		_fillColor.setLayoutData(gd__fillColor);

		Label lblFillBackgroundColor = new Label(this, SWT.NONE);
		lblFillBackgroundColor.setText("Fill Background Color");

		_fillBg = new ColorChooserButton(this, SWT.NONE);
		_fillBg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_effect3d = new Button(this, SWT.CHECK);
		_effect3d.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		_effect3d.setText("Effect 3D");

	}

	public void updateFrom(TankAdapter adapter) {
		TankFigure fig = adapter.getFigure();
		_fillColor.setColor(fig.getFillColor().getRGB());
		_fillBg.setColor(fig.getFillBackgroundColor().getRGB());
		_effect3d.setSelection(fig.isEffect3D());
	}

	public void saveTo(TankAdapter adapter) {
		TankFigure fig = adapter.getFigure();
		fig.setFillColor(SWTResourceManager.getColor(_fillColor.getColor()));
		fig.setFillBackgroundColor(SWTResourceManager.getColor(_fillBg
				.getColor()));
		fig.setEffect3D(_effect3d.getSelection());
	}

}
