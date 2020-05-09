package com.mfg.application.splashHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.DisplayAccess;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * @since 3.3
 * 
 */
public class ExtensibleSplashHandler extends AbstractSplashHandler {

	/**
	 * 
	 */
	public ExtensibleSplashHandler() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets
	 * .Shell)
	 */
	@Override
	public void init(final Shell splash) {
		super.init(splash);

		final Color[] color = new Color[] { splash.getDisplay().getSystemColor(
				SWT.COLOR_BLACK) };
		final Canvas canvasDot1 = new Canvas(splash, SWT.NONE);
		final Canvas canvasDot2 = new Canvas(splash, SWT.NONE);
		final Canvas canvasDot3 = new Canvas(splash, SWT.NONE);
		canvasDot1.setBackground(splash.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		canvasDot2.setBackground(splash.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		canvasDot3.setBackground(splash.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		canvasDot1.setBounds(75, 295, 5, 5);
		canvasDot2.setBounds(82, 295, 5, 5);
		canvasDot3.setBounds(89, 295, 5, 5);
		canvasDot1.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(color[0]);
				e.gc.setBackground(color[0]);
				e.gc.fillOval(0, 0, 4, 4);
			}

		});
		canvasDot2.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(color[0]);
				e.gc.setBackground(color[0]);
				e.gc.fillOval(0, 0, 4, 4);
			}

		});
		canvasDot3.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(color[0]);
				e.gc.setBackground(color[0]);
				e.gc.fillOval(0, 0, 4, 4);
			}

		});

		canvasDot1.setVisible(false);
		canvasDot2.setVisible(false);
		canvasDot3.setVisible(false);

		Thread worker = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 2; i++) {
					DisplayAccess.accessDisplayDuringStartup();
					try {
						Thread.sleep(300); // sleep a little so we can see the
											// dot change
					} catch (InterruptedException e) {
						// Adding a comment to avoid empty block warning.
					}
					if (!canvasDot1.isDisposed()) {
						canvasDot1.getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								canvasDot1.setVisible(!canvasDot1.getVisible());
								if (!canvasDot1.isDisposed())
									canvasDot1.redraw();
							}
						});
						try {
							Thread.sleep(300); // sleep a little so we can see
												// the
												// dot change
						} catch (InterruptedException e) {
							// Adding a comment to avoid empty block warning.
						}
					}
					if (!canvasDot2.isDisposed()) {
						canvasDot2.getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								canvasDot2.setVisible(!canvasDot2.getVisible());
								if (!canvasDot2.isDisposed())
									canvasDot2.redraw();
							}
						});
					}

					try {
						Thread.sleep(300); // sleep a little so we can see the
											// dot change
					} catch (InterruptedException e) {
						// Adding a comment to avoid empty block warning.
					}
					if (!canvasDot3.isDisposed()) {
						canvasDot3.getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								canvasDot3.setVisible(!canvasDot3.getVisible());
								if (!canvasDot3.isDisposed())
									canvasDot3.redraw();
							}
						});
						try {
							Thread.sleep(300); // sleep a little so we can see
												// the
												// dot change
						} catch (InterruptedException e) {
							// Adding a comment to avoid empty block warning.
						}
					}
				}
			}
		};
		worker.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.splash.AbstractSplashHandler#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}
}
