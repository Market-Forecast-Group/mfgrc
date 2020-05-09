/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.utils.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class ShowHideLayout extends Layout {

	private boolean visible;

	public ShowHideLayout() {
		visible = true;
	}

	public void setVisible(boolean aVisible) {
		this.visible = aVisible;
	}

	@Override
	protected boolean flushCache(Control control) {
		return true;
	}

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		int w = 0;
		int h = 0;
		Control child = composite.getChildren()[0];
		if (visible) {
			Point s = child.computeSize(wHint, hHint);
			w += s.x;
			h = Math.max(h, s.y);
		}
		Point point = new Point(w + 10 * (visible ? 2 : 1), h + 20);
		return point;
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		int x = 10;
		Assert.isTrue(composite.getChildren().length == 1);
		Control child = composite.getChildren()[0];
		Point computeSize = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (visible) {
			child.setBounds(x, 10, computeSize.x, computeSize.y);
			x += computeSize.x + 10;
			child.setVisible(true);
		} else {
			child.setBounds(0, 0, 0, 0);
			child.setVisible(false);
		}
	}
}