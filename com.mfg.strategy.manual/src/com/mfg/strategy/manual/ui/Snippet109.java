package com.mfg.strategy.manual.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Snippet109 {

	public static void main(String[] args) {
		Display display = new Display();
		Color red = display.getSystemColor(SWT.COLOR_RED);
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		// set the size of the scrolled content - method 1
		final ScrolledComposite sc1 = new ScrolledComposite(shell, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		final Composite c1 = new Composite(sc1, SWT.NONE);
		sc1.setContent(c1);
		c1.setBackground(red);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		c1.setLayout(layout);
		Button b1 = new Button(c1, SWT.PUSH);
		b1.setText("first button");
		c1.setSize(c1.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button add = new Button(shell, SWT.PUSH);
		add.setText("add children");
		final int[] index = new int[] { 0 };
		add.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				index[0]++;
				Button button = new Button(c1, SWT.PUSH);
				button.setText("button " + index[0]);
				// reset size of content so children can be seen - method 1
				c1.setSize(c1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				c1.layout();
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
