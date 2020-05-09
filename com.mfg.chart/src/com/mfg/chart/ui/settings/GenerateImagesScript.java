package com.mfg.chart.ui.settings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenerateImagesScript {
	public static void main(String[] args) throws IOException {
		for (int i : SettingsUtils.LINE_WIDTHS) {
			BufferedImage img = new BufferedImage(64, 16,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = img.createGraphics();
			g2.setPaint(Color.black);
			int y = img.getHeight() / 2 - i / 2;
			g2.fill(new Rectangle2D.Double(0, y, img.getWidth(), i));
			g2.dispose();

			ImageIO.write(img, "gif", new File("width" + i + ".gif"));
		}
	}
}
