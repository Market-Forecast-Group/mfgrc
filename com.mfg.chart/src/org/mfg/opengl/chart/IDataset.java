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

public interface IDataset {

	public int getSeriesCount();


	public int getItemCount(int series);


	public double getX(int series, int item);


	public double getY(int series, int item);
}
