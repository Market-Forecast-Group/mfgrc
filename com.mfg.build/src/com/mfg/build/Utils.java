package com.mfg.build;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
	private static Logger _logger = Logger.getGlobal();

	public static void error(Exception e) {
		e.printStackTrace();
		_logger.log(Level.SEVERE, e.getMessage());
	}
}
