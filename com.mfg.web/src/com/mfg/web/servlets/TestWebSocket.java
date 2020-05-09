package com.mfg.web.servlets;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.mfg.inputdb.prices.mdb.PriceMDB;

public class TestWebSocket extends WebSocketAdapter {
	private RemoteEndpoint _remote;

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		_remote = sess.getRemote();
	}

	@Override
	public void onWebSocketText(String message) {
		try {
			PriceMDB.Record r = new PriceMDB.Record();

			int count = 100;

			ByteBuffer buf = ByteBuffer.allocate(count * PriceMDB.RECORD_SIZE);

			for (int i = 0; i < 100; i++) {
				r.priceRaw = 200 + i;
				r.physicalTime = 200 - i;
				r.volume = i % 2;
				PriceMDB.writeBuffer(r, buf);
			}

			buf.rewind();

			_remote.sendBytes(buf);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
