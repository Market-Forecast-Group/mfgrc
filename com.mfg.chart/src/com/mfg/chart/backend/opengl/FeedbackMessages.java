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
package com.mfg.chart.backend.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.eclipse.swt.widgets.Display;
import org.mfg.opengl.IGLDrawable;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author arian
 * 
 */
public class FeedbackMessages implements IGLDrawable {
	final List<String> messages = new ArrayList<>();
	private GLUT glut;
	final Chart chart;
	private final float[] bg;
	private static int FONT = GLUT.BITMAP_HELVETICA_18;

	/**
	 * 
	 */
	public FeedbackMessages(Chart chart1) {
		this.chart = chart1;
		bg = new float[] { 0, 1, 0.67f, 1 };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#init(javax.media.opengl.GL2)
	 */
	@Override
	public void init(GL2 gl) {
		// Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#reshape(javax.media.opengl.GL2, int, int)
	 */
	@Override
	public void reshape(GL2 gl, int width, int height) {
		// Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#display(javax.media.opengl.GL2, int, int)
	 */
	@Override
	public void display(GL2 gl, int width, int height) {
		if (glut == null) {
			glut = new GLUT();
		}
		synchronized (messages) {
			int y = height - 30;
			for (String msg : messages) {
				int w = glut.glutBitmapLength(FONT, msg);
				int h = 20;

				gl.glColor4fv(bg, 0);
				gl.glBegin(GL2GL3.GL_QUADS);
				gl.glVertex2i(10, y - h / 2);
				gl.glVertex2i(10 + w + 10, y - h / 2);
				gl.glVertex2i(10 + w + 10, y + h / 2);
				gl.glVertex2i(10, y + h / 2);
				gl.glEnd();

				gl.glColor3fv(COLOR_BLACK, 0);
				gl.glRasterPos2d(15, y - h / 4);
				glut.glutBitmapString(FONT, msg);
				y -= h + 3;
			}
		}
	}

	public void showMessage(final String msg) {
		synchronized (messages) {
			messages.add(msg);
		}
		new Thread("To hide feedbackmessage " + msg) {
			@Override
			public void run() {
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					//
				}
				synchronized (messages) {
					messages.remove(msg);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							chart.repaint();
						}
					});

				}
			}
		}.start();
	}
}
