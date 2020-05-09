package com.mfg.chart;

import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLException;

import org.eclipse.ui.IStartup;

public class ChartStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// just to load the gluegen-rt resources
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					GLDrawableFactory.getDesktopFactory()
							.createExternalGLContext();
				} catch (final GLException e) {
					//Adding a comment to avoid empty block warning.
				}
			}
		}).start();

	}

}
