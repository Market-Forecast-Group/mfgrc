package com.mfg.utils.ui;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class MainShell {
	public MainShell(String title) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText(title);
		shell.setLayout(new FillLayout());
		shell.setSize(640, 480);
		
		createContents(shell);
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	public abstract void createContents(Shell shell);
}
