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
package org.mfg.opengl.tools.bmpCreator;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author arian
 * 
 */
public class BitmapCreator extends JFrame {

	private Dimension bmpDim;
	private boolean[][] boolBitmap;

	/**
	 *
	 */
	public BitmapCreator() {
		super("Bitmap Creator v0.2");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setExtendedState(MAXIMIZED_BOTH);

		init();
	}

	/**
	 *
	 */
	private void init() {
		bmpDim = showBitmapDimensionDialog();

		boolBitmap = new boolean[bmpDim.width][bmpDim.height];

		final GLCanvas canvas = new GLCanvas();

		final GWBitmapCreator creator = new GWBitmapCreator(boolBitmap,
				new Runnable() {

					@Override
					public void run() {
						canvas.repaint();
					}
				});

		canvas.addGLEventListener(new GLEventListener() {

			@SuppressWarnings("static-access")
			@Override
			public void reshape(final GLAutoDrawable drawable, final int x,
					final int y, final int width, final int height) {

				GL2 gl = drawable.getGL().getGL2();
				gl.glMatrixMode(GL2.GL_PROJECTION);
				gl.glLoadIdentity();

				// coordinate system origin at lower left with width and height
				// same as the window
				GLU glu = new GLU();
				glu.gluOrtho2D(0.0f, width, 0.0f, height);

				gl.glMatrixMode(GL2.GL_MODELVIEW);
				gl.glLoadIdentity();

				gl.glViewport(0, 0, width, height);

				creator.reshape(drawable.getGL().getGL2(), width, height);
			}

			@Override
			public void init(final GLAutoDrawable drawable) {
				final GL2 gl = drawable.getGL().getGL2();

				gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

				gl.glDisable(GL.GL_DEPTH_TEST);
				gl.glDisable(GLLightingFunc.GL_LIGHTING);
				// gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
				gl.glClearColor(0, 0, 0, 0);
				gl.glShadeModel(GLLightingFunc.GL_FLAT);

				gl.glEnable(GL.GL_LINE_SMOOTH);
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);

				creator.init(gl);
			}

			@Override
			public void dispose(final GLAutoDrawable drawable) {
				//
			}

			@Override
			public void display(final GLAutoDrawable drawable) {
				GL2 gl2 = drawable.getGL().getGL2();
				creator.display(gl2, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
				gl2.glFlush();
			}
		});
		final MouseAdapter listener = new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent
			 * )
			 */
			@Override
			public void mousePressed(final MouseEvent e) {
				creator.mouseClicked(e.getX(), canvas.getHeight() - e.getY());
			}
		};
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);

		getContentPane().add(canvas);
	}

	/**
	 *
	 */
	private static Dimension showBitmapDimensionDialog() {
		String dimStr = JOptionPane.showInputDialog(
				"Enter the bitmap size (Width x Height)", "16x16");

		if (dimStr == null) {
			System.exit(0);
		}

		@SuppressWarnings("null")
		final String[] arr = dimStr.trim().split("x");
		final int w = Integer.parseInt(arr[0].trim());
		final int h = Integer.parseInt(arr[1].trim());

		return new Dimension(w, h);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final InstantiationException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new BitmapCreator().setVisible(true);
	}
}
