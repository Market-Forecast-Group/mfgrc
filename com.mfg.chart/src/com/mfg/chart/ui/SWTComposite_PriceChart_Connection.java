/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
/**
 *
 */

package com.mfg.chart.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.mfg.chart.backend.opengl.Chart;

public class SWTComposite_PriceChart_Connection implements MouseListener,
		MouseMoveListener, MouseWheelListener, KeyListener, ControlListener,
		FocusListener {
	final Chart _chart;
	final Composite _comp;
	private boolean _isDragging = false;
	private final Cursor _crossCursor;
	private final Cursor _defaultCursor;
	private final Cursor _draggingCursor;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	private Cursor _hiddenCursor;

	public SWTComposite_PriceChart_Connection(final Composite comp1,
			final Chart chart1) {
		this._chart = chart1;
		this._comp = comp1;

		comp1.addMouseListener(this);
		comp1.addMouseWheelListener(this);
		comp1.addMouseMoveListener(this);
		comp1.addKeyListener(this);
		comp1.addControlListener(this);
		_comp.addFocusListener(this);

		final Display display = comp1.getShell().getDisplay();

		_crossCursor = display.getSystemCursor(SWT.CURSOR_CROSS);
		_defaultCursor = display.getSystemCursor(SWT.CURSOR_ARROW);
		_draggingCursor = display.getSystemCursor(SWT.CURSOR_SIZEALL);
	}

	public void close() {
		if (!_comp.isDisposed()) {

			final SWTComposite_PriceChart_Connection listener = this;
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					_comp.removeMouseListener(listener);
					_comp.removeMouseWheelListener(listener);
					_comp.removeMouseMoveListener(listener);
					_comp.removeKeyListener(listener);
					_comp.removeControlListener(listener);
					_comp.removeFocusListener(listener);
				}

			});

		}
	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		_chart.mouseScrolled(e.x, _comp.getBounds().height - e.y, e.count);
		updateCursor(e);
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		final Rectangle bounds = _comp.getBounds();
		int y = bounds.height - e.y;
		if (_isDragging) {
			_chart.mouseDragged(e.x, y, bounds.width, bounds.height,
					_ctrlPressed, e.button);
		} else {
			_chart.mouseMoved(e.x, y);
		}

		updateCursor(e);
	}

	private void updateCursor(final MouseEvent e) {
		int y = _comp.getBounds().height - e.y;
		final MouseCursor cursor = _chart.getDesiredMouseCursorAt(e.x, y);
		final Cursor currentCursor = _comp.getCursor();
		Cursor newCursor = null;
		switch (cursor) {
		case CROSSHAIR:
			newCursor = _crossCursor;
			break;
		case DRAGGING:
			newCursor = _draggingCursor;
			break;
		case DEFAULT:
			newCursor = _defaultCursor;
			break;
		case HIDDEN:
			if (_hiddenCursor == null) {
				Display display = Display.getDefault();
				Color white = display.getSystemColor(SWT.COLOR_WHITE);
				Color black = display.getSystemColor(SWT.COLOR_BLACK);
				PaletteData palette = new PaletteData(new RGB[] {
						white.getRGB(), black.getRGB() });
				ImageData sourceData = new ImageData(16, 16, 1, palette);
				sourceData.transparentPixel = 0;
				_hiddenCursor = new Cursor(display, sourceData, 0, 0);
			}
			newCursor = _hiddenCursor;
			break;

		default:
			break;
		}
		if (_isDragging && cursor == MouseCursor.CROSSHAIR) {
			newCursor = _draggingCursor;
		}
		if (newCursor != currentCursor) {
			_comp.setCursor(newCursor);
		}
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		// Adding a comment to avoid empty block warning.
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		_isDragging = true;
		final Rectangle bounds = _comp.getBounds();
		int y = bounds.height - e.y;
		_chart.mouseDown(e.x, y, bounds.width, bounds.height, e.button);
		updateCursor(e);
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		_isDragging = false;
		final Rectangle bounds = _comp.getBounds();
		int y = bounds.height - e.y;
		_chart.mouseUp(e.x, y, bounds.width, bounds.height, e.button);
		updateCursor(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.
	 * KeyEvent)
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		_ctrlPressed = e.keyCode == SWT.CTRL;
		_shiftPressed = e.keyCode == SWT.SHIFT;
		_chart.keyPressed(e.character);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events
	 * .KeyEvent)
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		discardKeyStates();
	}

	private void discardKeyStates() {
		_ctrlPressed = false;
		_shiftPressed = false;
	}

	public boolean isCtrlPressed() {
		return _ctrlPressed;
	}

	public boolean isShiftPressed() {
		return _shiftPressed;
	}

	@Override
	public void controlMoved(ControlEvent arg) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				_chart.repaint();
			}
		});
	}

	@Override
	public void controlResized(ControlEvent arg) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				_chart.repaint();
			}
		});
	}

	@Override
	public void focusGained(FocusEvent e) {
		//
	}

	@Override
	public void focusLost(FocusEvent e) {
		discardKeyStates();
	}

}
