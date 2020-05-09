package com.mfg.dfs.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSStoppingSubscriptionEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.ISymbolListener;
import com.mfg.common.RealTick;
import com.mfg.common.RequestParams;
import com.mfg.dfs.data.HistoryTableSlicer;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.DataRequest;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.UnitsType;
import com.mfg.dm.filters.CacheExpander;
import com.mfg.dm.filters.CacheExpander.EGapFillingMethod;
import com.mfg.dm.filters.DelayableCacheExpanderListener;
import com.mfg.dm.filters.ICacheExpanderListener;
import com.mfg.utils.U;

/**
 * A symbol which is the union of a real symbol and some form of "past" request
 * which will make a virtual representation of they symbol.
 * 
 * <p>
 * This class is only used in DFS space. The corresponding class in MFG space is
 * the {@link CompositeDataSource} (which sometimes will be deprecated).
 * 
 * <p>
 * A virtual symbol can be local or remote, a local virtual symbol is a symbol
 * which is inside DFS in the client side (or the proxy side). The difference is
 * that in that case the caches are either local or proxy caches (so the client
 * must wait the arrival of the bars, much like it was in the former
 * architecture).
 * 
 * <p>
 * In the other case, if the {@link VirtualSymbol} is in the server side, than
 * it will always have local caches but it will pass the ticks using the proxy.
 * 
 * <p>
 * As the virtual symbol is in server's space it <i>does not</i> need to have a
 * progress, because by definition all the historical requests in the
 * Multiserver are istantaneous (they are done using the
 * {@link HistoryTableSlicer}).
 * 
 * <p>
 * The virtual symbol has its own thread of execution.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class VirtualSymbol extends VirtualSymbolBase implements ISymbolListener {

	/**
	 * This array corresponds to the array of the cache expanders, but inverted.
	 * Because the caches follows the same order as the request, so they start
	 * from daily and arrive to range.
	 */
	private ArrayList<IBarCache> _caches = new ArrayList<>();

	/**
	 * This is a queue of real quotes event, because I am only subscribed to
	 * real symbol's quotes, which of course do not have a symbol event.
	 */
	private LinkedBlockingQueue<DFSQuote> _quotesQueue = new LinkedBlockingQueue<>();

	private final int _tick;

	/**
	 * Used to filter 0p the prices in the
	 * {@link #onNewSymbolEvent(DFSSymbolEvent)} message.
	 */
	private int _lastPrice = -1;

	/**
	 * @param aRequest
	 *            the request used to create the VirtualSymbol.
	 * @param tick
	 *            the tick for this symbol (it may be present in the tick data
	 *            request, but this is going to change).
	 */
	public VirtualSymbol(MultiServer aServer, TickDataRequest aRequest, int tick) {
		super(aServer, aRequest);
		_tick = tick;

	}

	/**
	 * expands the expanders. This expansion is done in server space.
	 * 
	 * @throws MonitorCancelledException
	 * @throws DFSException
	 */
	private void _expand() throws DFSException, MonitorCancelledException {
		if (_request.isMerged()) {
			_expanders.get(_expanders.size() - 1).expand();
		} else {
			_expanders.get(0).expand();
		}
	}

	/**
	 * Gets the caches.
	 * 
	 * <p>
	 * This action is without wait, because here we are always in server's
	 * space.
	 * 
	 * @throws DFSException
	 */
	private void _getCaches() throws DFSException {
		_caches.clear();

		for (DataRequest req : _request.getRequests()) {
			RequestParams reqParams = _helperConverter(
					_request.getLocalSymbol(), req);
			_caches.add(_server.getCache(reqParams));
		}
	}

	private void _prepareExpanders() {
		_expanders.clear();

		/*
		 * simple code to ease the transition between the layered data source
		 * and the new listener architecture.
		 */
		ICacheExpanderListener listener = this;

		if (!_request.isRealTime()) {
			/*
			 * the request is delayable, so I create the delayable listener, to
			 * pause the virtual thread, if it is necessary.
			 */

			DelayableCacheExpanderListener dcel = new DelayableCacheExpanderListener(
					listener, _monitor, _request.getNumberWarmupPrices(),
					_request.getLayersSize());
			listener = dcel;
			_delayControl = new ServerDelayControl();
			dcel.setDelayControl(_delayControl);

		} else {
			/*
			 * The request is in real time, no control is possible, so the
			 * listener is myself, and the delay control is null.
			 */
		}

		int gap1 = 0;
		int gap2 = 0;

		double aXp = _request.getXp();
		double aDp = _request.getDp();

		// boolean isFilterLonelyTicks = _tickDataRequest.isFilterLonelyTicks();

		// int aTick = _request.getTick();

		CacheExpander upperExpander = null;
		boolean isUseWindow = this._request.isGapFillingUsingWindow();
		CacheExpander.EGapFillingMethod method;
		if (isUseWindow) {
			method = EGapFillingMethod.UPPER_CACHE;
		} else {
			method = EGapFillingMethod.PRICE_MULTIPLIER;
		}

		boolean filterLonelyTicks = _request.isFilterLonelyTicks();
		int minGapToFilter = _request.getMinimumGap();

		/*
		 * If I merge I have to create the expanders backwards, because the
		 * expansion goes from the daily to the top layer.
		 * 
		 * If I do not merge, instead, the expansion goes from the range to the
		 * daily. The ordinal of the layer is the same, but the connection
		 * between layers is different.
		 */

		if (_request.isMerged()) {
			// I have to create the expanders backwards, from daily to range.
			for (int currentLayer = _caches.size() - 1; currentLayer >= 0; --currentLayer) {

				gap1 = _request.getRequests().get(currentLayer).getGap1();
				gap2 = _request.getRequests().get(currentLayer).getGap2();

				int ordinalLayer = _caches.size() - 1 - currentLayer;

				/*
				 * If layer is negative this is the only data source so the flag
				 * is sufficient to know whether to filter or not the lonely
				 * ticks, otherwise the condition is that only the top most
				 * layer is filtered.
				 */
				boolean filterInLayer = currentLayer >= 0 ? (filterLonelyTicks && (currentLayer == 0))
						: filterLonelyTicks;

				// IBarCache rds = _caches.get(currentLayer);
				CacheExpander ce = new CacheExpander(_caches.get(currentLayer),
						upperExpander, method, gap1, gap2, _tick,
						_request.getSeed(), aXp, aDp, filterInLayer,
						minGapToFilter, listener, ordinalLayer, true,
						_request.isRealTime());

				_expanders.add(ce);

				upperExpander = ce;
			}
		} else {
			// not merged, from daily to range, the range is start of the chain

			for (int currentLayer = 0; currentLayer < _caches.size(); ++currentLayer) {

				gap1 = _request.getRequests().get(currentLayer).getGap1();
				gap2 = _request.getRequests().get(currentLayer).getGap2();

				int ordinalLayer = _caches.size() - 1 - currentLayer;

				/*
				 * I filter lonely ticks only for the range layer, ordinal zero
				 */
				boolean filterInLayer = (filterLonelyTicks && (ordinalLayer == 0));

				// IBarCache rds = _caches.get(currentLayer);
				CacheExpander ce = new CacheExpander(_caches.get(currentLayer),
						upperExpander, method, gap1, gap2, _tick,
						_request.getSeed(), aXp, aDp, filterInLayer,
						minGapToFilter, listener, ordinalLayer, false,
						_request.isRealTime());

				_expanders.add(ce);

				upperExpander = ce;
			}

			/*
			 * with this reverse I have the possibility to have always the range
			 * layer as layer number zero.
			 */
			Collections.reverse(_expanders);

		}
	}

	/*
	 * In the case of a database request the symbol here simulates the real time
	 * in this thread.
	 */
	private void _simulateRealTime() throws MonitorCancelledException,
			DFSException {
		/*
		 * To simulate the real time the symbol takes the ticks from the range
		 * layer (or the layer zero, in any case) and it passes them to the
		 * onNewQuote.
		 */
		while (true) {
			long newTick = _expanders.get(0).selfSendRealTimeTick();
			if (newTick < 0) {
				U.debug_var(
						281950,
						"End of simulated real time expansion for the virtual symbol ",
						_id);
				break;
			}
			for (int i = 1; i < _expanders.size(); ++i) {
				CacheExpander expander = _expanders.get(i);
				expander.selfSendRealTimeTicksUntil(newTick);
			}
		}

	}

	@Override
	protected void _virtualSymbolThread() {
		try {

			/*
			 * Start will be done in another thread, because the Virtual symbol
			 * acts as a virtual data feed.
			 */

			_getCaches();

			/*
			 * This thread is paused until I have refreshed the symbol, first I
			 * get the caches because they book the tables. In any case if the
			 * request is open the high index is set to the maximum (-1, see the
			 * HistoryTableSlicer).
			 */
			if (_request.isRealTime())
				_server.refreshSynchSymbol(this._request._symbol.getSymbol());

			_prepareExpanders();

			/*
			 * The virtual symbol must subscribe to the real symbol if this
			 * symbol is going to be real time.
			 */
			if (_request.mustSubscribeToQuote()) {
				_server.subscribeQuote(this, _request.getLocalSymbol());
			} else {
				/*
				 * I don't have to subscribe to quote, this is a "database"
				 * request OR a not-merged request where the first layer is not
				 * range. In any case I will start a real time thread generator
				 * based on the bars which are after the warm up point.
				 */
			}

			_expand();

			if (_monitor.isCanceled()) {
				return; // the warm up has been aborted.
			}

			if (!_request.isRealTime()) {
				/*
				 * no real time for this symbol, so the thread can end here,
				 * when the thread ends here the slicers which were used to
				 * create the virtual symbol are automatically closed by the
				 * finally clause.
				 * 
				 * I have to distinguish between a merged request and a
				 * not-merged request, because in the latter case there are all
				 * the "real time" ticks waiting.
				 * 
				 * here the request is not real time, so I simulate the real
				 * time using the portion of the bars which were not used during
				 * the warm up phase.
				 */
				if (!_request.isMerged()) {
					/*
					 * The real time simulation is only used in case of not
					 * merged expanders, because
					 */
					_simulateRealTime();
				}

				/*
				 * The virtual symbol will die here, I notify the end of the
				 * subscription to all!
				 */
				DFSStoppingSubscriptionEvent dsse = new DFSStoppingSubscriptionEvent(
						_id);

				_server.onNewSymbolEvent(dsse);

				return;
			}

			/*
			 * The request is real time. I have to know which of the slicers are
			 * not more needed, usually if the request is merged all the slicers
			 * are closed and only the real time subscription is taken alive.
			 * 
			 * the close of the History table slicer class is idempotent, so
			 * there is no harm in closing it twice.
			 * 
			 * Be careful, because the caches are stored in the normal way, from
			 * daily to range, but the ordinal is the contrary.
			 * 
			 * Be also careful that getLayersSize returns the size of the
			 * layers, which is different from the size of requests! If the
			 * request is merged I have only one layer...
			 */
			boolean newBarsNeeded = false;
			int ordinalLayer = _request.getRequests().size();
			for (int i = 0; i < _request.getRequests().size(); ++i) {
				--ordinalLayer;
				if (!_request.areNewBarsNeeded(ordinalLayer)) {
					_caches.get(i).close();
				} else {
					newBarsNeeded = true;
				}
			}

			/*
			 * then there is the never ending loop to get the quotes, because
			 * the quotes come from the data feed thread, and here I wait
			 * endlessly to quotes coming from the real symbol.
			 * 
			 * This integer is able to know whether the loop is ignoring the new
			 * bars, this may happen in case of fast market, because there will
			 * be always ticks from the queue (the poll returns a quote) and the
			 * check of new bars will starve, if there is a need to this (new
			 * bars).
			 */
			int skippedCycles = 0;
			while (true) {

				DFSQuote quote;
				if (newBarsNeeded) {
					/*
					 * this virtual symbol is composed of different layers which
					 * update, so I have to poll either the queue of quotes
					 * (from the cache expander) and also the layers which need
					 * bars.
					 */
					quote = _quotesQueue.poll(3, TimeUnit.SECONDS);

					if (quote == null || skippedCycles > 5) {
						skippedCycles = 0;
						/*
						 * No quote, or too much waiting, so I have to check for
						 * new bars
						 */
						ordinalLayer = _request.getRequests().size();
						for (int i = 0; i < _request.getRequests().size(); ++i) {
							--ordinalLayer;
							if (_request.areNewBarsNeeded(ordinalLayer)) {
								/*
								 * This call will do everything: check if there
								 * is a new bar, get the bar and "auto-feed" the
								 * bar towards itself.
								 */
								_expanders.get(ordinalLayer).checkNewBar();
							}
						}

						continue; // continue polling for the quote.
					}

				} else {
					// no bars needed, I can wait forever.
					quote = _quotesQueue.take();
					if (quote == null) {
						throw new IllegalStateException();
					}
				}

				skippedCycles++;

				/*
				 * The new quote is passed to the real time layer expander, to
				 * process it. If this assert fails it means that the
				 * subscription has started before creating the expanders. This
				 * should not be.
				 */
				assert (_expanders.size() != 0);

				/*
				 * go on... tell the expander that there is a new tick. The zero
				 * is hard coded because by convention the quotes layer is
				 * always the zero layer.
				 */
				_expanders.get(0).realTimeTick(new RealTick(quote));
			}

		} catch (DFSException e) {
			U.debug_var(283952, "BAD exception in virtual symbol thread ", _id);
			e.printStackTrace();

		} catch (MonitorCancelledException | InterruptedException e) {
			// nothing. the thread dies gracefully.
			U.debug_var(392592, "normal end of virtual symbol thread ", Thread
					.currentThread().getName());
		} finally {
			/*
			 * release all the caches.
			 */
			for (IBarCache cache : _caches) {
				try {
					cache.close();
				} catch (DFSException e) {
					U.debug_var(920921,
							"Strange exception during virtual symbol " + _id
									+ " closing");
					// check if it happens, should not happen.
					e.printStackTrace();
				}
			}

			/*
			 * I cannot unsubscribe here, because this thread may be interrupted
			 * by the user which unsubscribes to the virtual symbol... so the
			 * unsubscribe of the server is already occupied.
			 * 
			 * The solution is to unsubscribe in a little thread snippet here.
			 * 
			 * is it a solution? to be worked better...
			 */
			final ISymbolListener thisLis = this;
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (_request.mustSubscribeToQuote()) {
						try {
							_server.unsubscribeQuote(thisLis,
									_request.getLocalSymbol());
							U.debug_var(391938, "The VIRTUAL symbol ", _id,
									" unsubscribes to REAL symbol ",
									_request.getLocalSymbol());
						} catch (DFSException e) {
							// should not happen, give it a look.
							e.printStackTrace();
						}
					}
				}
			}).start();

		}

		U.debug_var(239358, "The virtual symbol thread ", _id, " dies here");

	}

	@Override
	public DfsSymbol getDfsSymbol() throws DFSException {
		return _server.getModel().getCache()
				.getSymbolDataSafe(getRequest().getLocalSymbol()).f2
				.getSymbol();
	}

	/**
	 * Called when the <b>real</b> symbol attached to this virtual symbol has a
	 * new quote. This quote is then converted to a virtual quote by the cache
	 * expanders.
	 * 
	 * @param aQuote
	 *            the raw quote from the data feed.
	 */
	@Override
	public void onNewSymbolEvent(DFSSymbolEvent aQuote) {
		/*
		 * This cast must succeed because the virtual symbol is always
		 * subscribed to a real symbol (for now I do not see the need to have a
		 * double layer of virtual symbols, that is a virtual symbol subscribed
		 * to another virtual symbol)
		 */
		DFSQuote dfsQuote = (DFSQuote) aQuote;
		if (dfsQuote.tick.getPrice() == _lastPrice) {
			// U.debug_var(382951, "#### volume quote ", aQuote);
			// _server.onNewQuote(aQuote);
			// return;
		} else {

			U.debug_var(293492, "virtual symbol ", _id, " new quote ",
					dfsQuote, " for REAL ", _request.getLocalSymbol());
		}

		_lastPrice = dfsQuote.tick.getPrice();

		_quotesQueue.add(dfsQuote);

	}

	/**
	 * A simple helper converter from a {@linkplain RawDataSource} (actually a
	 * {@linkplain DataRequest} to a {@linkplain RequestParams} object.
	 * 
	 * @param rds
	 * @return the converted object
	 * @throws DFSException
	 * 
	 */
	private static RequestParams _helperConverter(String symbol, DataRequest dr)
			throws DFSException {
		// DataRequest dr = rds.getRequest();

		boolean isDayRequest = (dr.fUnitsType == UnitsType.DAYS);

		RequestParams rp = null;

		// String symbol = rds.getLocalSymbol();
		switch (dr.barType) {
		case DAILY:
			if (dr.startDate > 0) {
				if (isDayRequest) {
					rp = RequestParams.createRequestHistDailyDays(symbol,
							dr.startDate, dr.numberOfBarsRequested);
				} else {
					rp = RequestParams.createRequestHistNumDailyBars(symbol,
							dr.startDate, dr.numberOfBarsRequested);
				}
			} else {
				if (isDayRequest) {
					rp = RequestParams.createRequestLastDailyDays(symbol,
							dr.numberOfBarsRequested);
				} else {
					rp = RequestParams.createRequestLastNumDailyBars(symbol,
							dr.numberOfBarsRequested);
				}
			}

			break;
		case HOUR:
			throw new UnsupportedOperationException();
		case MINUTE:

			if (dr.startDate > 0) {
				if (isDayRequest) {
					rp = RequestParams.createRequestNumDaysOfMinuteBarsSince(
							symbol, dr.numberOfBarsRequested, 1, dr.startDate);
				} else {
					rp = RequestParams.createRequestNumMinuteBarsSince(symbol,
							dr.numberOfBarsRequested, 1, dr.startDate);
				}
			} else {
				if (isDayRequest) {
					rp = RequestParams.createRequestLastMinuteDays(symbol,
							dr.numberOfBarsRequested, 1);
				} else {
					rp = RequestParams.createRequestLastNumMinuteBars(symbol,
							dr.numberOfBarsRequested, 1);
				}
			}

			break;
		case MONTHLY:
			throw new UnsupportedOperationException();
		case PRICE:
			throw new UnsupportedOperationException();
		case RANGE:

			if (dr.startDate > 0) {
				if (isDayRequest) {
					rp = RequestParams.createRequestNumDaysOfRangeSince(symbol,
							dr.startDate, dr.numberOfBarsRequested);
				} else {
					rp = RequestParams.createRequestNumBarsOfRangeSince(symbol,
							dr.startDate, dr.numberOfBarsRequested);
				}
			} else {
				if (isDayRequest) {
					rp = RequestParams.createRequestLastRangeDays(symbol,
							dr.numberOfBarsRequested);
				} else {
					rp = RequestParams.createRequestLastNumRangeBars(symbol,
							dr.numberOfBarsRequested);
				}
			}

			break;
		case SECOND:
			throw new UnsupportedOperationException();
		case WEEKLY:
			throw new UnsupportedOperationException();
		default:
			throw new UnsupportedOperationException();

		}

		return rp;
	}

}
