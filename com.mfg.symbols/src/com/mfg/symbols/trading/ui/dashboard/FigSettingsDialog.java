package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class FigSettingsDialog extends Dialog {
	private FigSettingsComp _figSettingsComp;
	private WidgetFigureAdapter<?> _figure;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public FigSettingsDialog(Shell parentShell) {
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

		_figSettingsComp = new FigSettingsComp(container, SWT.NONE);

		afterCreateWidgets();

		return container;
	}

	private void afterCreateWidgets() {
		_figSettingsComp.updateFrom(_figure.getFigure());
	}

	@Override
	protected void okPressed() {
		_figSettingsComp.saveTo(_figure.getFigure());

		super.okPressed();
	}

	public FigureAdapter<?> getFigure() {
		return _figure;
	}

	public void setFigure(WidgetFigureAdapter<?> figure) {
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
		return new Point(538, 418);
	}

}
