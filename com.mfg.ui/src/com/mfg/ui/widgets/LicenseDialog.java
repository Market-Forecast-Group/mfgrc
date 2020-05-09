package com.mfg.ui.widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mfg.utils.lic.LicenseUtil;

public class LicenseDialog extends Dialog {
	private Text _keyText;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public LicenseDialog(Shell parentShell) {
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

		Label lblEnterProductKey = new Label(container, SWT.NONE);
		lblEnterProductKey.setText("Enter Product Key");

		_keyText = new Text(container, SWT.BORDER);
		_keyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		return container;
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
		return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
		String k = _keyText.getText();
		String result = LicenseUtil.registerLicKey(k);
		if (result == null) {
			super.okPressed();
		}
	}

}
