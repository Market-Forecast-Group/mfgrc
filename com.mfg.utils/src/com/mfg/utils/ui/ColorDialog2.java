package com.mfg.utils.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ColorDialog2 extends Dialog {

	private RGB _rgb;
	private ColorChooserComposite _colorComposite;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ColorDialog2(Shell parentShell) {
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

		_colorComposite = new ColorChooserComposite(container, SWT.NONE);

		afterCreateWidgets();

		return container;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		getShell().pack();
		return c;
	}

	private void afterCreateWidgets() {
		if (_rgb != null) {
			_colorComposite.setColor(_rgb);
		}
	}

	@Override
	protected void okPressed() {
		_rgb = _colorComposite.getColor();
		super.okPressed();
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
		return new Point(672, 496);
	}

	public void setRGB(RGB rgb) {
		_rgb = rgb;
	}

	public RGB getRGB() {
		return _rgb;
	}

}
