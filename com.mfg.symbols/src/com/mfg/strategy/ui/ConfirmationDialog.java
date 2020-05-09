package com.mfg.strategy.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class ConfirmationDialog extends Dialog {

	public static final String GREEN = "green";
	private StyledText _msgText;
	private String _message;
	private String _title;
	private String _question;
	private Runnable _closeAction;
	private Label _questionLabel;
	private boolean _createCancel = true;
	private boolean _open;
	private String _msgColor;
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ConfirmationDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(_title);
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
	}

	@Override
	public void setBlockOnOpen(boolean shouldBlock) {
		super.setBlockOnOpen(false);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		Composite container = (Composite) super.createDialogArea(parent);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.marginTop = 10;
		gl_container.verticalSpacing = 20;
		container.setLayout(gl_container);

		_questionLabel = new Label(container, SWT.NONE);
		_questionLabel.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_BLACK));
		_questionLabel.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_questionLabel.setAlignment(SWT.CENTER);
		_questionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		_msgText = new StyledText(container, SWT.NONE);
		_msgText.setEditable(false);
		_msgText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		return container;
	}

	public void open(Runnable closeAction) {
		_open = true;
		setReturnCode(CANCEL);
		_closeAction = closeAction;
		super.open();
	}

	@Override
	public boolean close() {
		_open = false;
		return super.close();
	}

	public boolean isOpen() {
		return _open;
	}

	@Override
	protected void okPressed() {
		setReturnCode(OK);
		if (_closeAction != null) {
			_closeAction.run();
		}
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		if (_closeAction != null) {
			_closeAction.run();
		}
		super.cancelPressed();
	}

	public void updateUI() {
		_msgText.setText(_message == null ? "" : _message);
		RGB c = _msgColor == GREEN ? new RGB(0, 255, 0) : new RGB(255, 0, 0);
		_msgText.setBackground(SWTResourceManager.getColor(c));
		_questionLabel.setText(_question.toUpperCase());
		getShell().setText(_title);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		if (_createCancel) {
			createButton(parent, IDialogConstants.CANCEL_ID,
					IDialogConstants.CANCEL_LABEL, false);
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public void setMessage(String msg) {
		_message = msg;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setQuestion(String question) {
		_question = question;
	}

	public void setMessageColor(String color) {
		_msgColor = color;
	}

	public void setCreateCancel(boolean createCancel) {
		_createCancel = createCancel;
	}
}
