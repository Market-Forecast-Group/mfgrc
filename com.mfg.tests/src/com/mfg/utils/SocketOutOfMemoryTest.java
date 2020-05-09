package com.mfg.utils;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class SocketOutOfMemoryTest {
	private static final int GB = 1_073_741_824;

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		int port = 1982;
		long t = currentTimeMillis();
		if (args.length > 0 && args[0].equals("consumer")) {
			out.println("consume");
			try (Socket socket = new Socket(InetAddress.getLocalHost(), port);
					InputStream is = socket.getInputStream();) {
				int b = -1;
				int n = 0;
				do {
					b = is.read();
					n++;
				} while (b != -1);
				out.println("read " + n + " bytes");
			}
		} else {
			// run the producer
			out.println("listen " + port);
			try (ServerSocket server = new ServerSocket(port);
					Socket socket = server.accept();
					OutputStream output = socket.getOutputStream();) {
				out.println("connected, produce");
				socket.setSendBufferSize(GB);
				byte[] buf = new byte[GB];
				for (int i = 0; i < GB; i++) {
					buf[i] = (byte) (i % 10);
				}
				output.write(buf);
			}
			out.println("done");
		}
		
		out.println("delay " + TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis() - t) + "s");
	}
}
