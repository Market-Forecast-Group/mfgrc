package com.mfg.jogl;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class TestOpenGL {

	static {
		// setting this true causes window events not to get sent on Linux if
		// you run from inside Eclipse
		// TODO: I commented this to see if it is really needed
		// GLProfile.initSingleton();
	}

	public static class OneTriangle {
		protected static void setup(GL2 gl2, int width, int height) {
			gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl2.glClearColor(.3f, .5f, .8f, 1.0f);
			gl2.glLoadIdentity();

			gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl2.glLoadIdentity();

			// coordinate system origin at lower left with width and height same
			// as the window
			GLU glu = new GLU();
			glu.gluOrtho2D(0.0f, width, 0.0f, height);

			gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl2.glLoadIdentity();

			gl2.glViewport(0, 0, width, height);
		}

		protected static void render(GL2 gl2, int width, int height) {
			gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl2.glClearColor(.3f, .5f, .8f, 1.0f);

			// draw a triangle filling the window
			gl2.glLoadIdentity();
			gl2.glBegin(GL.GL_TRIANGLES);
			gl2.glColor3f(1, 0, 0);
			gl2.glVertex2f(0, 0);
			gl2.glColor3f(0, 1, 0);
			gl2.glVertex2f(width, 0);
			gl2.glColor3f(0, 0, 1);
			gl2.glVertex2f(width / 2, height);
			gl2.glEnd();
		}
	}

	public static class OneTriangleAWT {

		public static void main(String[] args) {
			GLProfile glprofile = GLProfile.getDefault();
			GLCapabilities glcapabilities = new GLCapabilities(glprofile);
			final javax.media.opengl.awt.GLCanvas glcanvas = new javax.media.opengl.awt.GLCanvas(
					glcapabilities);

			glcanvas.addGLEventListener(new GLEventListener() {

				@Override
				public void reshape(GLAutoDrawable glautodrawable, int x,
						int y, int width, int height) {
					OneTriangle.setup(glautodrawable.getGL().getGL2(), width,
							height);
				}

				@Override
				public void init(GLAutoDrawable glautodrawable) {
					//
				}

				@Override
				public void dispose(GLAutoDrawable glautodrawable) {
					//
				}

				@Override
				public void display(GLAutoDrawable glautodrawable) {
					OneTriangle.render(glautodrawable.getGL().getGL2(),
							glautodrawable.getSurfaceWidth(),
							glautodrawable.getSurfaceHeight());
				}
			});

			final Frame frame = new Frame("One Triangle AWT");
			frame.add(glcanvas);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent windowevent) {
					frame.remove(glcanvas);
					frame.dispose();
					System.exit(0);
				}
			});

			frame.setSize(640, 480);
			frame.setVisible(true);
		}
	}

	public static class OneTriangleSWT {

		public static void main(String[] args) {
			final Display display = new Display();
			final Shell shell = new Shell(display);
			shell.setText("OneTriangle SWT");
			shell.setLayout(new FillLayout());
			shell.setSize(640, 480);

			final Composite composite = new Composite(shell, SWT.NONE);
			composite.setLayout(new FillLayout());

			GLData gldata = new GLData();
			gldata.doubleBuffer = true;
			// need SWT.NO_BACKGROUND to prevent SWT from clearing the window
			// at the wrong times (we use glClear for this instead)
			final org.eclipse.swt.opengl.GLCanvas glcanvas = new org.eclipse.swt.opengl.GLCanvas(
					composite, SWT.NONE, gldata);
			glcanvas.setCurrent();
			GLProfile glprofile = GLProfile.getDefault();
			final GLContext glcontext = GLDrawableFactory.getFactory(glprofile)
					.createExternalGLContext();

			// fix the viewport when the user resizes the window
			glcanvas.addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Rectangle rectangle = glcanvas.getClientArea();
					glcanvas.setCurrent();
					glcontext.makeCurrent();
					OneTriangle.setup(glcontext.getGL().getGL2(),
							rectangle.width, rectangle.height);
					glcontext.release();
				}
			});

			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					Rectangle rectangle = glcanvas.getClientArea();
					glcanvas.setCurrent();
					glcontext.makeCurrent();
					OneTriangle.render(glcontext.getGL().getGL2(),
							rectangle.width, rectangle.height);
					glcanvas.swapBuffers();
					glcontext.release();
					display.asyncExec(this);
				}
			});

			glcontext.makeCurrent();
			GL2 gl = glcontext.getGL().getGL2();
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
			gl.glClearDepth(1.0);
			gl.glLineWidth(2);
			gl.glEnable(GL.GL_DEPTH_TEST);
			glcontext.release();

			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}

			glcanvas.dispose();
			display.dispose();
		}
	}

	public static void main(final String[] args) {
		OneTriangleSWT.main(args);
	}
}
