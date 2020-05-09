package com.mfg.web.servlets;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static java.lang.System.out;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.json.JSONObject;

import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.symbols.dfs.jobs.DFSJob;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.jobs.InputPipe;

public class HistoricalDataWebsocket extends AbstractDataWebsocket {

	@Override
	protected void processDataRequest(DFSJob job, Object config)
			throws Exception {
		// the total of all the chunks of data we are about to send.
		int workTotal = 0;
		int layers = 3;

		JSONObject respJson = new JSONObject();

		respJson.put("type", "data-desc");
		respJson.put("layers", layers);

		InputConfiguration input = null;

		if (config instanceof InputConfiguration) {
			input = (InputConfiguration) config;
			int scales = input.getInfo().getIndicatorParams()
					.getIndicatorNumberOfScales();
			respJson.put("scales", scales);
			// count all the pivots, bands, channels of the scale
			workTotal = layers * ((scales - 1) * 2 /* 3 */);
		}

		// count all the prices layers
		workTotal += layers;

		// send a description of the data is about to be sent
		sendJSON(respJson);

		// wait for warm-up to finish
		long t = currentTimeMillis();
		while (!job.isWarmupCompleted()) {
			long d = currentTimeMillis() - t;
			if (d > 10_000) {
				err.println("Waiting too much for the warmup, maybe there was an error");
				break;
			}
			Thread.sleep(50);
		}
		out.println("After warmup send all the data");

		int worked = 0;

		worked = sendPricesData(job, worked, workTotal);

		if (input != null) {
			worked = sendInputData(job, input, worked, workTotal);
		}
	}

	private void sendProgress(int worked, int total) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("type", "progress");
		obj.put("prog", (int) ((double) worked / (double) total * 100));
		sendJSON(obj);
	}

	private int sendInputData(DFSJob job, InputConfiguration config,
			int worked, int workTotal) throws Exception {
		int work = worked;
		InputPipe pipe = job.getInputPipe(config);
		IndicatorMDBSession session = pipe.getMdbSession();
		for (int dataLayer = 0; dataLayer < 3; dataLayer++) {
			// send pivots
			for (int scale = 2; scale <= session.getScalesCount(); scale++) {
				PivotMDB mdb = session.connectTo_PivotMDB(dataLayer, scale);
				PivotMDB.Record[] records;
				try (PivotMDB.Cursor cur = mdb.cursor()) {
					records = mdb.selectAll(cur);
				}

				sendRecords(records, dataLayer, scale, ObjectType.PIVOT,
						PivotMDB.RECORD_SIZE, PivotMDB::writeBuffer);

				work++;
				sendProgress(work, workTotal);
			}

			// send bands
			for (int scale = 2; scale <= session.getScalesCount(); scale++) {
				BandsMDB mdb = session.connectTo_BandsMDB(dataLayer, scale);
				BandsMDB.Record[] records;
				try (BandsMDB.Cursor cur = mdb.cursor()) {
					records = mdb.selectAll(cur);
				}

				sendRecords(records, dataLayer, scale, ObjectType.BANDS,
						BandsMDB.RECORD_SIZE, BandsMDB::writeBuffer);

				work++;
				sendProgress(work, workTotal);
			}
		}
		return work;
	}

	private int sendPricesData(DFSJob job, int progress, int progTotal)
			throws Exception {
		int work = progress;
		// for now just send all the data
		PriceMDBSession priceSession = job.getMdbSession();

		// only send layer one
		for (int dataLayer = 0; dataLayer < 3; dataLayer++) {
			PriceMDB mdb = priceSession.connectTo_PriceMDB(dataLayer);

			PriceMDB.Record[] records;

			try (PriceMDB.Cursor cur = mdb.cursor()) {
				records = mdb.selectAll(cur);
			}

			sendRecords(records, dataLayer, 0, ObjectType.PRICE,
					PriceMDB.RECORD_SIZE, PriceMDB::writeBuffer);

			out.println("Done.");
			work++;
			sendProgress(work, progTotal);
		}
		return work;
	}

	private <T> void sendRecords(T[] records, int dataLayer, int scale,
			ObjectType type, int recordSize,
			BiConsumer<T, ByteBuffer> writeBuffer) throws IOException {
		int len = records.length;

		int RECORDS_BLOCK_SIZE = 100;
		ByteBuffer buffer1 = createBuffer(type, dataLayer, scale, recordSize,
				RECORDS_BLOCK_SIZE);
		ByteBuffer buffer2 = null;
		int lastBlockSize = len % RECORDS_BLOCK_SIZE;
		if (lastBlockSize > 0) {
			buffer2 = createBuffer(type, dataLayer, scale, recordSize,
					lastBlockSize);
		}
		ByteBuffer buffer;

		for (int j = 0; j < len; j += RECORDS_BLOCK_SIZE) {
			int n = RECORDS_BLOCK_SIZE;
			buffer = buffer1;
			if (j + RECORDS_BLOCK_SIZE > len) {
				n = len - j;
				buffer = buffer2;
			}

			// clear header
			buffer.rewind();
			// write header
			writeBufferHeader(type, dataLayer, scale, buffer);
			out.println(buffer.position());
			for (int i = 0; i < n; i++) {
				T rec = records[j + i];
				writeBuffer.accept(rec, buffer);
			}

			out.println("Sending " + RECORDS_BLOCK_SIZE + " prices, dataLayer "
					+ dataLayer + "...");

			// set pointer at first position
			buffer.rewind();
			// send it
			_remote.sendBytes(buffer);
			// I don't know why, but the buffer capacity is set to 0 when
			// the buffer is sent by the network, so we should reset it.
			buffer.limit(buffer.capacity());
		}
	}
}
