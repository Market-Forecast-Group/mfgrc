/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.broker.IOrderMfg.ORDER_TYPE;

/**
 * 
 * @author gardero
 */
public enum LSFilterType {

	Long {

		@Override
		public boolean matchEntry(ORDER_TYPE oRDER_TYPE) {
			return oRDER_TYPE == ORDER_TYPE.BUY;
		}


		@Override
		public LSFilterType joinFilter(LSFilterType filter) {
			switch (filter) {
			case None:
			case Long:
				return Long;
				//$CASES-OMITTED$
			default:
				return Auto;
			}
		}
	},
	Short {

		@Override
		public boolean matchEntry(ORDER_TYPE oRDER_TYPE) {
			return oRDER_TYPE == ORDER_TYPE.SELL;
		}


		@Override
		public LSFilterType joinFilter(LSFilterType filter) {
			switch (filter) {
			case None:
			case Short:
				return Short;
				//$CASES-OMITTED$
			default:
				return Auto;
			}
		}

	},
	None {

		@Override
		public boolean matchEntry(ORDER_TYPE oRDER_TYPE) {
			return false;
		}


		@Override
		public LSFilterType joinFilter(LSFilterType filter) {
			return filter;
		}
	},
	Auto;

	/**
	 * @param oRDER_TYPE  
	 */
	@SuppressWarnings("static-method")// Used on inner classes.
	public  boolean matchEntry(ORDER_TYPE oRDER_TYPE) {
		return true;
	}


	/**
	 * @param filter  
	 */
	@SuppressWarnings("static-method")// Used on inner classes.
	public LSFilterType joinFilter(LSFilterType filter) {
		return Auto;
	}
}
