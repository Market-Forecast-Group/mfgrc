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
package com.mfg.symbols.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.utils.ImageUtils;

/**
 * @author arian
 * 
 */
public class ConfigurationSetsManager {
	private final int[] sets = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

	//
	// *** Remove comments to generate the images. ***
	//
	// public static void main(String[] args) throws IOException {
	// java.awt.Color[] list = { java.awt.Color.blue, java.awt.Color.cyan,
	// java.awt.Color.blue.darker(), java.awt.Color.cyan.darker(),
	// java.awt.Color.gray.darker(), java.awt.Color.green.darker(),
	// java.awt.Color.magenta.darker(), java.awt.Color.red.darker(),
	// java.awt.Color.yellow.darker(), java.awt.Color.gray };
	//
	// int i = 0;
	// for (java.awt.Color c : list) {
	// BufferedImage img = new BufferedImage(16, 16,
	// BufferedImage.TYPE_INT_ARGB);
	// Graphics2D g2 = img.createGraphics();
	// g2.setPaint(c);
	// g2.fillRect(0, 0, 16, 16);
	// g2.dispose();
	// ImageIO.write(img, "png", new File("set" + i + ".png"));
	// i++;
	// }
	// }

	private final List<Integer> setList;

	public ConfigurationSetsManager() {
		setList = new ArrayList<>();
		for (int set : sets) {
			setList.add(Integer.valueOf(set));
		}
	}

	public int[] getSets() {
		return sets;
	}

	/**
	 * @return the setList
	 */
	public List<Integer> getSetList() {
		return setList;
	}

	public int getNextAvailableSet() {
		// TODO: for now, it just look for trading configurations.
		List<TradingConfiguration> tradings = SymbolsPlugin.getDefault()
				.getTradingStorage().getObjects();
		Set<Integer> setSet = new HashSet<>();
		for (TradingConfiguration trading : tradings) {
			setSet.add(Integer.valueOf(trading.getInfo().getConfigurationSet()));
		}
		for (int set : sets) {
			if (!setSet.contains(Integer.valueOf(set))) {
				return set;
			}
		}
		return 0;
	}

	public static Image getImage(int set) {
		Image img = ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
				"icons/set/set" + set + ".png");
		return img;
	}
}
