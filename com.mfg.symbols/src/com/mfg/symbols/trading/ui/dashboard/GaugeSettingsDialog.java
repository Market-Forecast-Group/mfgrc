package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class GaugeSettingsDialog extends Dialog {
	private FigSettingsComp _figSettingsComp;
	private GaugeAdapter _figure;
	private GaugeSettingsComp _gaugeSettingsComp;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public GaugeSettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		_gaugeSettingsComp = new GaugeSettingsComp(container, SWT.BORDER);
		_gaugeSettingsComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		_figSettingsComp = new FigSettingsComp(container, SWT.BORDER);
		_figSettingsComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		afterCreateWidgets();

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Gauge Settings");
	}

	private void afterCreateWidgets() {
		_gaugeSettingsComp.updateFrom(_figure);
		_figSettingsComp.updateFrom(_figure.getFigure());
	}

	@Override
	protected void okPressed() {
		_gaugeSettingsComp.saveTo(_figure);
		_figSettingsComp.saveTo(_figure.getFigure());

		super.okPressed();
	}

	public GaugeAdapter getFigure() {
		return _figure;
	}

	public void setFigure(GaugeAdapter figure) {
		_figure = figure;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(574, 448);
	}

}
