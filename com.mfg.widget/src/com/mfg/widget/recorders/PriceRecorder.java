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

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;

import org.mfg.mdb.runtime.SessionMode;

import com.mfg.chart.model.mdb.recorders.AbstractChartRecorder;
import com.mfg.common.QueueTick;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.Appender;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.utils.collections.TimeMap;

/**
 * @author arian
 * 
 */
public class PriceRecorder extends AbstractChartRecorder {

	private final PriceMDB pricesMDB;
	private final Appender appender;
	private final TimeMap _timeMap;

	/**
	 * @param rootDir
	 * @throws IOException
	 */
	public PriceRecorder(PriceMDBSession session) throws IOException {
		super(session);
		if (session.getMode() != SessionMode.MEMORY) {
			new File(session.getRoot(), "layer-0").mkdirs();
		}
		pricesMDB = session.connectTo_PriceMDB(0);
		appender = pricesMDB.appender();
		_timeMap = session.getTimeMap(0);
	}

	public void addPrice(QueueTick tick) throws IOException {
		try {
			Record r = new PriceMDB.Record();
			r.update(tick.getFakeTime(), tick.getPhysicalTime(),
					tick.getPrice(), tick.getReal(), tick.getVolume());
			appender.append(r);
			_timeMap.put(tick.getFakeTime(), tick.getPhysicalTime());
		} catch (BufferOverflowException e1) {
			handleAppenderException(e1);
		}
	}

	/**
	 * @param tick
	 */
	public void newTick(QueueTick tick) {
		try {
			addPrice(tick);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
