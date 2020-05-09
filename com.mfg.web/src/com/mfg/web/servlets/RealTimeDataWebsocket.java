package com.mfg.web.servlets;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.mfg.common.QueueTick;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.TickAdapter;
import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.symbols.dfs.jobs.DFSJob;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.jobs.InputPipe;
import com.mfg.symbols.jobs.InputPipe.Recorder;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.strategy.IndicatorAdaptator;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

public class RealTimeDataWebsocket extends AbstractDataWebsocket {

	private DFSJob _job;

	@Override
	protected void processDataRequest(DFSJob job, Object config) {
		connectToRealTimeStream(job, config);
	}

	class TickListener extends TickAdapter {
		private int _listenerDataLayer;

		public TickListener(int dataLayer) {
			super();
			_listenerDataLayer = dataLayer;
		}

		@Override
		public void onNewTick(QueueTick qt) {
			sendTick(qt, _listenerDataLayer);
		}
	}

	private void connectToRealTimeStream(DFSJob job, Object config) {
		_job = job;
		// connect prices stream

		CompositeDataSource ds = (CompositeDataSource) job.getDataSource();

		// if there is a new dataset, connect to it
		for (int layer = 0; layer < 3; layer++) {
			TickListener listener = new TickListener(layer);
			ds.addTickListener(layer, listener);
			final int layer_ = layer;
			addRemoveListenerAction(new Runnable() {

				@Override
				public void run() {
					ds.removeTickListener(layer_, listener);
				}
			});
		}

		// connect input stream

		// if it is really an input, then connect to it
		if (config instanceof InputConfiguration) {
			InputPipe input = job.getInputPipe((InputConfiguration) config);
			LayeredIndicator indicator = input.getIndicator();
			ArrayList<MultiscaleIndicator> indLayers = indicator.getLayers();
			int layer = 0;
			for (MultiscaleIndicator indLayer : indLayers) {
				int layer_ = layer;
				Recorder recorder = input.getRecorders()[layer];
				IndicatorAdaptator listener = new IndicatorAdaptator() {
					@Override
					public void newPivot(Pivot pv) {
						sendPivot(pv, layer_);
					}

					@Override
					public void newPointRegressionLine(PointRegressionLine prl) {
						sendBands(prl, layer_, recorder);
					}
				};
				indLayer.addIndicatorListener(listener);
				addRemoveListenerAction(new Runnable() {

					@Override
					public void run() {
						indLayer.removeListener(listener);
					}
				});
				layer++;
			}
		}
	}

	protected void sendBands(PointRegressionLine bands, int dataLayer,
			Recorder recorder) {
		if (_job.isWarmupCompleted() && getSession().isOpen()) {
			BandsMDB.Record r = new BandsMDB.Record();
			recorder.updateBandsRecord(r, bands);

			ByteBuffer buffer = createBuffer(ObjectType.BANDS, dataLayer,
					bands.getLevel(), BandsMDB.RECORD_SIZE, 1);

			BandsMDB.writeBuffer(r, buffer);
			buffer.rewind();

			out.println("send bands " + r + " dataLayer " + dataLayer
					+ " scale " + bands.getLevel());

			try {
				_remote.sendBytes(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void sendPivot(Pivot pivot, int dataLayer) {
		if (_job.isWarmupCompleted() && getSession().isOpen()) {
			ByteBuffer buffer = createBuffer(ObjectType.PIVOT, dataLayer,
					pivot.getLevel(), PivotMDB.RECORD_SIZE, 1);

			PivotMDB.Record r = new PivotMDB.Record();
			r.confirmPrice = pivot.fConfirmPrice;
			r.confirmTime = pivot.fConfirmTime;
			r.pivotTime = pivot.fPivotTime;
			r.pivotPrice = pivot.fPivotPrice;
			r.timeInterval = pivot.getTimeInterval();
			r.confirmTime = pivot.fConfirmTime;
			r.confirmPrice = pivot.fConfirmPrice;
			r.isUp = !pivot.isStartingDownSwing();
			// TODO: missing physical information
			r.pivotPhysicalTime = 0;
			r.confirmPhysicalTime = 0;

			PivotMDB.writeBuffer(r, buffer);
			buffer.rewind();

			out.println("send pivot " + r + " dataLayer " + dataLayer
					+ " scale " + pivot.getLevel());

			try {
				_remote.sendBytes(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void sendTick(QueueTick qt, int dataLayer) {
		if (_job.isWarmupCompleted() && getSession().isOpen()) {
			try {
				ByteBuffer buffer = createBuffer(ObjectType.PRICE, dataLayer,
						0, PriceMDB.RECORD_SIZE, 1);
				PriceMDB.Record record = new PriceMDB.Record();
				record.update(qt);
				PriceMDB.writeBuffer(record, buffer);
				buffer.rewind();

				out.println("send rt price record " + record);

				_remote.sendBytes(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
