package org.mfg.opengl.chart;

import javax.media.opengl.GL2;

import org.mfg.opengl.IGLConstants;

public interface ISeriesPainter extends IGLConstants {
	public void paint(GL2 gl, IDataset ds, PlotRange xrange, PlotRange yrange);
}
