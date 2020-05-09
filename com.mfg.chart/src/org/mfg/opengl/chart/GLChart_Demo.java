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

package org.mfg.opengl.chart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mfg.opengl.IGLConstants;


public class GLChart_Demo {

	/**
	 * @param args
	 */
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

		final GLChart chart = new GLChart();

		final IDataset ds = new IDataset() {

			@Override
			public double getY(final int series, final int item) {
				return Math.sin(getX(series, item));
			}


			@Override
			public double getX(final int series, final int item) {
				return item;
			}


			@Override
			public int getSeriesCount() {
				return 1;
			}


			@Override
			public int getItemCount(final int series) {
				return 100;
			}
		};

		chart.plot.addDataset(ds, new SimplePainter(IGLConstants.COLOR_GREEN));
		chart.plot.yrange = new PlotRange(-1, 1);
		chart.plot.xrange = new PlotRange(0, 100);

		final GLChartCanvas glcanvas = new GLChartCanvas(composite, gldata, chart);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		glcanvas.dispose();
		display.dispose();
	}

}
