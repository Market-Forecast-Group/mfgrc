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
package com.mfg.symbols.trading.ui.chart;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.broker.IOrderMfg;
import com.mfg.chart.model.mdb.recorders.AbstractChartRecorder;
import com.mfg.interfaces.ProbabilityRecord;
import com.mfg.interfaces.trading.IPositionListener;
import com.mfg.interfaces.trading.IStrategyShell;
import com.mfg.interfaces.trading.PositionClosedEvent;
import com.mfg.interfaces.trading.PositionOpenedEvent;
import com.mfg.strategy.ProbabilitiesDealer;
import com.mfg.tradingdb.mdb.EquityMDB;
import com.mfg.tradingdb.mdb.ProbabilityMDB;
import com.mfg.tradingdb.mdb.ProbabilityMDB.Appender;
import com.mfg.tradingdb.mdb.ProbabilityPercentMDB;
import com.mfg.tradingdb.mdb.TradeMDB;
import com.mfg.tradingdb.mdb.TradeMDB.RandomCursor;
import com.mfg.tradingdb.mdb.TradeMDB.Record;
import com.mfg.tradingdb.mdb.TradingMDBSession;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.collections.TimeMap;

/**
 * @author arian
 * 
 */
public class TradingRecorder extends AbstractChartRecorder implements
		IPositionListener, ProbabilitiesDealer.IListener {

	private final TradeMDB.Appender tradeAppender;
	private final EquityMDB.Appender _equityAppender;
	/**
	 * Key order id, value position (rowid) in the database
	 */
	private final Map<Integer, Long> mapOrderIdOpenPosition;
	private ProbabilityMDB.Appender[] probsAppenderMap;
	private ProbabilityPercentMDB.Appender[] probsPercentAppenderMap;
	private final boolean enabledProbabilityPercent;
	// private final PortfolioStrategy portfolio;
	private final double tickValue;

	private StepDefinition _tick;

	/**
	 * @param session
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public TradingRecorder(TimeMap timeMap, TradingMDBSession session,
			int levels, StepDefinition tick, double aTickValue,
			boolean aEnabledProbabilityPercent) throws IOException {
		super(session);
		// this.portfolio = portfolio;
		_tick = tick;
		// tickValue = symbol.getTickValue();
		this.tickValue = aTickValue;
		this.enabledProbabilityPercent = aEnabledProbabilityPercent;

		tradeAppender = session.connectTo_TradeMDB().appender();
		_equityAppender = session.connectTo_EquityMDB().appender();
		mapOrderIdOpenPosition = new HashMap<>();

		// int count = portfolio.getIndicator().getChscalelevels();
		int count = levels;
		session.setScalesCount(count);
		session.setPercentProbabilityMode(aEnabledProbabilityPercent);
		// TODO: sure?
		session.setConditionalProbabilitiesOnly(false);
		session.saveProperties();

		if (aEnabledProbabilityPercent) {
			probsPercentAppenderMap = new ProbabilityPercentMDB.Appender[count + 1];
			for (int level = 1; level <= count; level++) {
				probsPercentAppenderMap[level] = session
						.connectTo_ProbabilityPercentMDB(level).appender();
			}
		} else {
			probsAppenderMap = new ProbabilityMDB.Appender[count + 1];
			for (int level = 1; level <= count; level++) {
				if (session.getMode() != SessionMode.MEMORY) {
					new File(session.getRoot(), Integer.toString(level))
							.mkdirs();
				}
				probsAppenderMap[level] = session.connectTo_ProbabilityMDB(
						level).appender();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IPositionListener#positionOpened(com.mfg.strategy.
	 * PositionOpenedEvent)
	 */
	@Override
	public void positionOpened(PositionOpenedEvent event) {
		try {
			mapOrderIdOpenPosition.put(
					Integer.valueOf(event.getOrder().getId()),
					Long.valueOf(tradeAppender.getMDB().size()));
			tradeAppender.isLong = event.isLongPosition();
			tradeAppender.openTime = event.getExecutionTime();
			tradeAppender.openPhysicalTime = event.getPhysicalExecutionTime();
			tradeAppender.openPrice = event.getExecutionPrice();
			long[] openings = event.getChildrenOpenings();
			tradeAppender.openingCount = (byte) openings.length;
			tradeAppender.opening0 = -1;
			tradeAppender.opening1 = -1;
			if (openings.length > 0) {
				tradeAppender.opening0 = openings[0];
			}
			if (openings.length > 1) {
				tradeAppender.opening1 = openings[1];
			}
			tradeAppender.isClosed = false;
			tradeAppender.orderId = event.getOrder().getId();
			tradeAppender.closeTime = tradeAppender.openTime;
			tradeAppender.closePhysicalTime = tradeAppender.openPhysicalTime;
			// to mark it was not closed yet
			tradeAppender.closePrice = -1;

			ArrayList<IOrderMfg> children = event.getOrder().getChildren();
			if (tradeAppender.openingCount > 0) {
				IOrderMfg order = children.get(0);
				tradeAppender.opening0_childType = order.getChildType()
						.ordinal();
				tradeAppender.opening0_orderId = order.getId();

				if (tradeAppender.openingCount > 1) {
					order = children.get(1);
					tradeAppender.opening1_childType = order.getChildType()
							.ordinal();
					tradeAppender.opening1_orderId = order.getId();
				}
			}
			tradeAppender.append();
		} catch (IOException e) {
			handleAppenderException(e);
		} catch (BufferOverflowException e1) {
			handleAppenderException(e1);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IPositionListener#positionClosed(com.mfg.strategy.
	 * PositionClosedEvent)
	 */
	@Override
	public void positionClosed(PositionClosedEvent event) {
		try {
			// trades

			Long i = mapOrderIdOpenPosition.remove(Integer.valueOf(event
					.getOrder().getId()));
			Assert.isNotNull(i);
			TradeMDB mdb = tradeAppender.getMDB();
			RandomCursor c = mdb.thread_randomCursor();
			Record r = mdb.record(c, i.longValue());
			Assert.isNotNull(r);
			r.closeTime = event.getExecutionTime();
			r.closePhysicalTime = event.getPhysicalExecutionTime();
			r.closePrice = event.getExecutionPrice();
			r.isGain = event.isGain();
			r.isClosed = true;
			// StepDefinition tick = portfolio.getTick();
			mdb.replace(i.longValue(), r);

			// equity

			// add 0 as first record.
			if (_equityAppender.getMDB().size() == 0) {
				_equityAppender.total = 0;
				_equityAppender.totalPrice = 0;
				_equityAppender.fakeTime = event.getExecutionTime();
				_equityAppender.physicalTime = event.getPhysicalExecutionTime();
				_equityAppender.append();
			}

			double total = _tick.roundLong(event.getTotal());
			_equityAppender.total = _tick.getTicksOn(total) * tickValue;
			_equityAppender.totalPrice = total;
			_equityAppender.fakeTime = event.getExecutionTime();

			_equityAppender.physicalTime = event.getPhysicalExecutionTime();
			_equityAppender.append();
		} catch (IOException e) {
			handleAppenderException(e);
		} catch (BufferOverflowException e1) {
			handleAppenderException(e1);
		}
	}

	@Override
	public void probabilityRecordComputed(IStrategyShell sender,
			ProbabilityRecord record) {
		try {
			if (enabledProbabilityPercent) {
				ProbabilityPercentMDB.Appender a = probsPercentAppenderMap[record
						.getLevel()];
				a.time = record.getTime();
				a.posCurrentPrice = record
						.getPositiveCurrentProbabilitiesPrice();
				a.negCurrentPrice = record
						.getNegativeCurrentProbabilitiesPrice();
				a.posTradeDirection = record.isPositiveTradeDirection();

				a.posTHPrice = record.getPositiveTHProbabilitiesPrice();
				a.negTHPrice = record.getNegativeTHProbabilitiesPrice();

				a.append();
			} else {
				Appender a = probsAppenderMap[record.getLevel()];
				a.time = record.getTime();
				a.posPrice = record.getPositiveTargetPrice();
				a.negPrice = record.getNegativeTargetPrice();
				a.posTradeDirection = record.isPositiveTradeDirection();
				a.append();
			}

		} catch (IOException e) {
			handleAppenderException(e);
		} catch (BufferOverflowException e1) {
			handleAppenderException(e1);
		}
	}

	public void setTick(StepDefinition stepDef) {
		_tick = stepDef;
	}
}
