package com.mfg.utils.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestColorChooserButton {

	protected Shell _shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestColorChooserButton window = new TestColorChooserButton();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		_shell.open();
		_shell.layout();
		while (!_shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		_shell = new Shell();
		_shell.setSize(450, 300);
		_shell.setText("SWT Application");

		ColorChooserButton colorChooserButton = new ColorChooserButton(_shell,
				SWT.NONE);
		colorChooserButton.setBounds(66, 58, 28, 26);

	}
}
