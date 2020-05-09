package com.mfg.chart.ui.animation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestClosedByInterruptException {
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try(RandomAccessFile f = new RandomAccessFile(new File(
						"pepe.txt"), "rw")) {
					FileChannel c = f.getChannel();
					while (true) {
						ByteBuffer b = ByteBuffer.allocate(1000);
						b.rewind();
						c.write(b);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		Thread.sleep(1000);
		t.interrupt();
	}
}
