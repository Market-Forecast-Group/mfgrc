package com.mfg.chart.demo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.mfg.mdb.runtime.DBSynchronizer;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.chart.ui.IChartUtils;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.Appender;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;

public class StandaloneChartDemo_realtime {
	public static void main(String[] args) throws IOException, TimeoutException {
		PriceMDBSession session = new PriceMDBSession("demo", new File(
				"price_db"), SessionMode.READ_WRITE, true, new DBSynchronizer());
		session.setDataLayersCount(1);
		PriceMDB mdb = session.connectTo_PriceMDB(0);
		final Appender app = mdb.appender();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					app.physicalTime = System.currentTimeMillis();
					app.priceRaw = (200 + (int) (Math.random() * 100))
							* (Math.random() < 0.2 ? -1 : 1);
					app.time = i;
					try {
						app.append();
						Thread.sleep(200);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();

		IChartUtils.openStandAlongChartWindow(session, null, null);

		session.closeAndDelete();
	}
}
