package com.mfg.utils.ui;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;

abstract class ColorMouseListener extends MouseAdapter implements
		MouseMoveListener {
	protected boolean _pressed = false;

	@Override
	public void mouseUp(MouseEvent e) {
		_pressed = false;
		update(e);
	}

	@Override
	public void mouseDown(MouseEvent e) {
		_pressed = true;
		update(e);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (_pressed) {
			update(e);
		}
	}

	protected abstract void update(MouseEvent e);
}