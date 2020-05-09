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

package com.mfg.utils.io;

import java.io.File;
import java.io.IOException;

public class IO {

	/**
	 * Should be called deleteDirectory instead...
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void deleteFile(File file) throws IOException {
		File[] list = file.listFiles();
		if (list != null) {
			for (File f : list) {
				deleteFile(f);
			}
		}
		if (!file.delete()) {
			if (file.exists()) {
				throw new IOException("Cannot remove the file " + file);
			}
		}
	}
}
