package com.mfg.strategy.manual.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

public class TestingWidgets extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public TestingWidgets(Composite parent, int style) {
		super(parent, style);

		ExpandBar expandBar = new ExpandBar(this, SWT.NONE);
		expandBar.setBounds(46, 30, 249, 220);

		ExpandItem xpndtmNewExpanditem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setExpanded(true);
		xpndtmNewExpanditem.setText("New ExpandItem");

		Button btnNewButton = new Button(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setControl(btnNewButton);
		btnNewButton.setText("New Button");
		xpndtmNewExpanditem.setHeight(xpndtmNewExpanditem.getControl()
				.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		ExpandItem xpndtmNewExpanditem_1 = new ExpandItem(expandBar, SWT.NONE);
		xpndtmNewExpanditem_1.setExpanded(true);
		xpndtmNewExpanditem_1.setText("New ExpandItem");

		Canvas canvas = new Canvas(expandBar, SWT.NONE);
		xpndtmNewExpanditem_1.setControl(canvas);
		xpndtmNewExpanditem_1.setHeight(xpndtmNewExpanditem_1.getControl()
				.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
