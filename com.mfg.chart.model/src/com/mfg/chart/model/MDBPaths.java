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

package com.mfg.chart.model;

import java.io.File;

/**
 * 
 * We this class we pretend unify all the MDB file paths.
 * 
 * @author arian
 * 
 */
public class MDBPaths {
	public static String getPriceMDB() {
		return "prices.mdb";
	}

	public static String getPriceMDB(int layer) {
		return "/layer-" + layer + "/prices.mdb";
	}

	/**
	 * @param rootDir
	 * @return
	 */
	public static String getOpenPositionMDB() {
		return "open-position.mdb";
	}

	/**
	 * @param rootDir
	 * @return
	 */
	public static String getClosePositionMDB() {
		return "close-position.mdb";
	}

	/**
	 * @param rootDir
	 * @return
	 */
	public static File getChildrenOpeningsPositionMDB(File rootDir) {
		return new File(rootDir, "children-openings.mdb");
	}

	/**
	 * @param root
	 * @return
	 */
	public static String getOpenClosePositionMDB() {
		return "open-close.mdb";
	}

	/**
	 * @param level
	 * @return
	 */
	public static String getPivotMDB(int layer, int level) {
		return "layer-" + layer + "/" + level + "/pivot.mdb";
	}

	/**
	 * @param root
	 * @param level
	 * @return
	 */
	public static String getBandsMDB(int layer, int level) {
		return "layer-" + layer + "/" + level + "/regression.mdb";
	}

	/**
	 * @param file
	 * @param level
	 * @return
	 */
	public static String getCompressedBandsMDB(int layer, int level) {
		return "layer-" + layer + "/" + level + "/compressed-regression.mdb";
	}

	/**
	 * @param root
	 * @param level
	 * @return
	 */
	public static String getChannelMDB(int layer, int level) {
		return "layer-" + layer + "/" + level + "/channel.mdb";
	}

	/**
	 * @param root
	 * @param level
	 * @return
	 */
	public static String getChannelInfoMDB(int layer, int level) {
		return "layer-" + layer + "/" + level + "/channel-info.mdb";
	}

	public static String getProbabilityMDB(int level) {
		return level + "/probs.mdb";
	}

	public static String getProbabilityPercentMDB(int level) {
		return level + "/probs-percent.mdb";
	}

	/**
	 * @param root
	 * @return
	 */
	public static String getTradeMDB() {
		return "trades.mdb";
	}

	/**
	 * @param root
	 * @return
	 */
	public static String getEquityMDB() {
		return "equity.mdb";
	}
}
