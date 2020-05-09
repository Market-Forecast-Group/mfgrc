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
package com.mfg.chart.model.mdb;

import org.mfg.mdb.runtime.IRecord;
import org.mfg.mdb.runtime.IValidator;
import org.mfg.mdb.runtime.IValidatorListener;
import org.mfg.mdb.runtime.MDB;
import org.mfg.mdb.runtime.ValidationArgs;
import org.mfg.mdb.runtime.ValidatorError;

import com.mfg.chart.model.mdb.PriceMDB.Record;

/**
 * @author arian
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ChartDataValidators {
	
	public static final IValidator PRICES_SUBSEQ_HAS_DIFF_VALUE = new IValidator() {

		@Override
		public boolean validate(ValidationArgs args, IValidatorListener listener) {
			PriceMDB.Record prev = (Record) args.getPrev();
			PriceMDB.Record cur = (Record) args.getCurrent();
			if (prev.price == cur.price) {
				listener.errorReported(new ValidatorError(args, "price("
						+ (args.getRow() - 1) + ")=" + prev.price
						+ " == price(" + args.getRow() + ")=" + cur.price));
				return false;
			}
			return true;
		}
	};

	public static final IValidator PRICES_SUBSEQ_HAS_1_TICK_DIFF = new IValidator() {

		@Override
		public boolean validate(ValidationArgs args, IValidatorListener listener) {
			MDB mdb = args.getMdb();
			ChartMDBSession session = (ChartMDBSession) mdb.getSession();
			int tickSize = session.getTickSize();
			if (tickSize == 0) {
				listener.errorReported(new ValidatorError(args, "Tick Size = 0"));
				return false;
			}

			PriceMDB.Record prev = (Record) args.getPrev();
			PriceMDB.Record cur = (Record) args.getCurrent();
			double diff = Math.abs(prev.price - cur.price) / tickSize;
			if (diff != 1) {
				listener.errorReported(new ValidatorError(args, "price("
						+ (args.getRow() - 1) + ")=" + prev.price + " has "
						+ diff + " ticks diff to price(" + args.getRow() + ")="
						+ cur.price));
				return false;
			}
			return true;
		}
	};

	/**
	 * This validity check says that no two real prices can distance less than 2
	 * ticks if there is a fake tick inside.
	 * 
	 * @return
	 */
	public static IValidator REAL_PRICES_SUBSEQ_HAS_MORE_THAN_1_TICK_DIFF() {
		return new IValidator() {

			private Double lastRealPrice = null;
			private long lastRealPriceRow;
			private boolean thereAreMiddleFakeTicks = false;

			@Override
			public boolean validate(ValidationArgs args,
					IValidatorListener listener) {
				boolean result = true;

				ChartMDBSession session = (ChartMDBSession) args.getMdb()
						.getSession();
				int tickSize = session.getTickSize();
				IRecord cur = args.getCurrent();
				Boolean real = (Boolean) cur.get(PriceMDB.COLUMN_REAL);
				Double price = (Double) cur.get(PriceMDB.COLUMN_PRICE);

				if (real.booleanValue()) {
					if (lastRealPrice != null && thereAreMiddleFakeTicks) {
						double priceDiff = Math.abs(lastRealPrice.doubleValue() - price.doubleValue());
						double tickDiff = priceDiff / tickSize;
						if (tickDiff < 2) {
							listener.errorReported(new ValidatorError(args,
									"real price(" + lastRealPriceRow + ")="
											+ lastRealPrice
											+ " has less than 2 ticks (size="
											+ tickSize
											+ ") diff with real price("
											+ args.getRow() + ")=" + price));
							result = false;
						}
					}
					lastRealPrice = price;
					lastRealPriceRow = args.getRow();
					thereAreMiddleFakeTicks = false;
				} else {
					thereAreMiddleFakeTicks = true;
				}

				return result;
			}
		};
	}

	public static final IValidator PIVOTS_SUBSEQ_HAS_DIFF_IS_UP_VALUE = new IValidator() {

		@Override
		public boolean validate(ValidationArgs args, IValidatorListener listener) {
			PivotMDB.Record prev = (PivotMDB.Record) args.getPrev();
			PivotMDB.Record cur = (PivotMDB.Record) args.getCurrent();
			if (prev.isUp == cur.isUp) {
				listener.errorReported(new ValidatorError(args, "pivot.isUp("
						+ (args.getRow() - 1) + ")=" + prev.isUp
						+ " == pivot.isUp(" + args.getRow() + ")=" + cur.isUp));
				return false;
			}
			return true;
		}
	};

	private ChartDataValidators() {
	}
}
