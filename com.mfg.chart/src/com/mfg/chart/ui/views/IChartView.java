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
package com.mfg.chart.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewSite;

import com.mfg.chart.backend.opengl.Chart;

/**
 * @author arian
 * 
 */
public interface IChartView {

	public Chart getChart();

	public void setContent(Object newContent);

	public String getPartProperty(String key);

	public Object getContent();

	public void setPartProperty(String key, String value);

	public void setPartName(String partName);

	public String getPartName();

	public void setTitleImage(Image img);

	public void setChart(Chart chart);

	public IViewSite getViewSite();

	public IChartContentAdapter getContentAdapter();

}
