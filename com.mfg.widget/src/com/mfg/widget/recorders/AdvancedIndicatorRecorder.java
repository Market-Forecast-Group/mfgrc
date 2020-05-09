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

package com.mfg.widget.recorders;

import java.io.IOException;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.math.geom.Channel;
import com.mfg.widget.arc.strategy.IndicatorAdaptator;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * 
 * This indicator recorder will connect to the indicator signals to save the
 * objects. To disconnect the rocerder from the indicator, you must to call the
 * {@link #close()} method.
 * 
 * @author arian
 * 
 */
public class AdvancedIndicatorRecorder extends IndicatorRecorder {

	public AdvancedIndicatorRecorder(LayeredIndicator indicator,
			IndicatorMDBSession session, int layer) throws IOException {
		super(indicator, session, layer);
		getSession().saveProperties();

		IndicatorAdaptator listener = new IndicatorAdaptator() {

			@Override
			public void newStartedChannel(Channel ch) {
				try {
					addChannel(ch);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void newPivot(Pivot pv) {
				try {
					addPivot(pv);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void newPointRegressionLine(PointRegressionLine prl) {
				try {
					addBands(prl);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		MultiscaleIndicator indicatorLayer = indicator.getLayers().get(layer);
		indicatorLayer.addIndicatorListener(listener);
	}
}
