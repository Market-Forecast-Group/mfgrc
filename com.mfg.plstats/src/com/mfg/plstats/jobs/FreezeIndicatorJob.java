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

package com.mfg.plstats.jobs;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.mfg.common.DFSException;
import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.IndicatorManager;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.SessionInfo;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.ChannelIndicator;
import com.mfg.widget.arc.strategy.FrozenWidget;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class FreezeIndicatorJob extends AbstractIndicatorJob {

	private FrozenWidget frozenIndicagtor;
	private SessionInfo session;

	/**
	 * @param name
	 */
	public FreezeIndicatorJob(IIndicatorConfiguration configuration,
			IndicatorManager aManager) {
		super("Load Indicator", configuration, aManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			IIndicatorConfiguration configuration = getConfiguration();
			session = getManager().getOrCreateMDBSession(configuration);
			// int scalesCount = configuration.getIndicatorSettings()
			// .getIndicatorNumberOfScales();

			// int total = 0;

			// long step;
			// for (int level = 1; level <= scalesCount; level++) {
			// step = session.getIndicatorSession()
			// .connectTo_ChannelMDB(0, level).size();
			// total += step;
			// step = session.getIndicatorSession()
			// .connectTo_PivotMDB(0, level).size();
			// total += step;
			// step = session.getIndicatorSession()
			// .connectTo_BandsMDB(0, level).size();
			// total += step;
			// // step = session.connectTo_ChannelInfoMDB(0, level).size();
			// // total += step;
			// }

			/*
			 * The number of steps is given by the number of prices in the mdb
			 * file.
			 */
			long priceSize = session.getPriceSession().connectTo_PriceMDB(0)
					.size();

			// long totalSteps = session.getIndicatorSession()
			// .connectTo_BandsMDB(0, 2).size();
			// totalSteps *= 1.2;

			// total += 1; // freeze step
			monitor.beginTask("Loading Indicator", (int) priceSize);

			// ChannelStatData data = loadData(monitor);

			if (!monitor.isCanceled()) {
				frozenIndicagtor = createFrozen(monitor, priceSize);
				if (!monitor.isCanceled()) {
					getManager().setFrozenIndicator(configuration,
							frozenIndicagtor);
					monitor.worked(1);
				}
			}
			// test();

		} catch (IOException | DFSException e) {
			e.printStackTrace();
			status = new Status(IStatus.ERROR, PLStatsPlugin.PLUGIN_ID,
					e.getMessage(), e);
		} finally {
			monitor.done();
		}

		if (monitor.isCanceled()) {
			status = Status.CANCEL_STATUS;
		}

		return status;
	}

	/**
	 * Use it to compare the frozen indicator with the real time indicator.
	 */

	/**
	 * @return the frozenWidget
	 */
	public FrozenWidget getFrozenIndicator() {
		return frozenIndicagtor;
	}

	/**
	 * @param data
	 * @param monitor
	 * @param totalSteps
	 *            the number or total steps
	 * @return
	 * @throws DFSException
	 */
	private FrozenWidget createFrozen(IProgressMonitor monitor, long totalSteps)
			throws DFSException {
		IndicatorParamBean param = (IndicatorParamBean) getConfiguration()
				.getIndicatorSettings();

		ChannelIndicator gci;
		gci = new MultiscaleIndicator(param, null, 0);

		CSVSymbolData symbol = getConfiguration().getSymbol();
		FrozenWidget frozen = gci.freeze(symbol, monitor, totalSteps);
		// frozen.concealTheStates();

		return frozen;
	}
}
