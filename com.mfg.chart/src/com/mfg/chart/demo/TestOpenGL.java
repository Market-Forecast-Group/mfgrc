package com.mfg.chart.demo;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class TestOpenGL {

	private static double angle;

	static {
		// setting this true causes window events not to get sent on Linux if
		// you run from inside Eclipse
		GLProfile.initSingleton();
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("OneTriangle SWT");
		shell.setLayout(new FillLayout());
		shell.setSize(640, 480);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());

		final GLData gldata = new GLData();
		gldata.doubleBuffer = true;
		// need SWT.NO_BACKGROUND to prevent SWT from clearing the window
		// at the wrong times (we use glClear for this instead)
		final GLCanvas glcanvas = new GLCanvas(composite, SWT.NO_BACKGROUND,
				gldata);
		glcanvas.setCurrent();
		final GLProfile glprofile = GLProfile.getDefault();
		final GLContext glcontext = GLDrawableFactory.getFactory(glprofile)
				.createExternalGLContext();

		// fix the viewport when the user resizes the window
		glcanvas.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final Rectangle rectangle = glcanvas.getClientArea();
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				setup(glcontext.getGL().getGL2(), rectangle.width,
						rectangle.height);
				glcontext.release();
			}
		});

		// draw the triangle when the OS tells us that any part of the window
		// needs drawing
		glcanvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(final PaintEvent paintevent) {
				final Rectangle rectangle = glcanvas.getClientArea();
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				render(glcontext.getGL().getGL2(), rectangle.width,
						rectangle.height);
				glcanvas.swapBuffers();
				glcontext.release();
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!glcanvas.isDisposed() && !shell.isDisposed()) {
					shell.getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {
							try {
								glcanvas.redraw();
								glcanvas.update();
								try {
									Thread.sleep(10);
								} catch (final InterruptedException e) {
									//Adding a comment to avoid empty block warning.
								}
							} catch (final Exception e) {
								//Adding a comment to avoid empty block warning.
							}
						}
					});

				}
			}
		}).start();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		try {
			glcanvas.dispose();
			display.dispose();
		} catch (final Exception e) {
			//Adding a comment to avoid empty block warning.
		}
	}

	/**
	 * @param gl2
	 * @param width
	 * @param height
	 */
	protected static void setup(final GL2 gl2, final int width, final int height) {
		gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		final GLU glu = new GLU();
		glu.gluOrtho2D(-2, 2, -2, 2);

		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();

		gl2.glViewport(0, 0, width, height);
	}

	/**
	 * @param gl2
	 * @param width
	 * @param height
	 */
	protected static void render(final GL2 gl2, final int width, final int height) {
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		// draw a triangle filling the window
		angle += Math.PI;
		gl2.glLoadIdentity();
		final long m = System.currentTimeMillis();
		final boolean b = m % 1000 < 500;
		final boolean c = m % 10000 < 5000;
		gl2.glRotated(angle, b ? 2 : 0, b ? 0 : 2, c ? 2 : 0);

		gl2.glBegin(GL.GL_TRIANGLES);
		gl2.glColor3f(1, 0, 0);
		gl2.glVertex2f(-1, -1);

		gl2.glColor3f(0, 1, 0);
		gl2.glVertex2f(1, -1);

		gl2.glColor3f(0, 0, 2);
		gl2.glVertex2f(0.5f, 1);
		gl2.glEnd();
	}
}
