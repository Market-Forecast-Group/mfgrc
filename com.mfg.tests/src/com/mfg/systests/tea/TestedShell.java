package com.mfg.systests.tea;

import java.util.concurrent.atomic.AtomicBoolean;

import com.mfg.broker.IOrderMfg;
import com.mfg.common.DFSException;
import com.mfg.common.IDataSource;
import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dm.RandomTickDataRequest;
import com.mfg.tea.conn.ITEA;
import com.mfg.tea.conn.IVirtualBroker;
import com.mfg.tea.conn.VirtualBrokerParams;
import com.mfg.utils.U;

class TestedShell {

	private AtomicBoolean _ended = new AtomicBoolean();
	private IDFS _dfs;
	private ITEA _tea;
	private Thread _shellThread;
	private IDataSource _dataSource;
	private TestedShellParams _params;
	private TestedStrategy _testedStrategy;
	private IVirtualBroker _vb;

	/**
	 * Builds a tested shell with a particular DFS and TEA.
	 * 
	 * 
	 * <p>
	 * There are several other parameters which the tested shell recognizes.
	 * These are set in the TestedShellParamBean object which is the struct used
	 * to pass all the parameters to the tested tea shell (also the
	 * "tested strategy" implementations).
	 * 
	 * 
	 */
	TestedShell(IDFS aDfs, ITEA aTea, TestedShellParams params) {
		_dfs = aDfs;
		_tea = aTea;

		_params = params;
	}

	/**
	 * runs the test. The nature of the test is given by the parameters given to
	 * the tested shell itself.
	 * 
	 * @throws TEAException
	 * @throws DFSException
	 * 
	 */
	void _run() throws TEAException, DFSException {

		/*
		 * The parameters are not really important, but at least the request
		 * must be coherent
		 */
		RandomTickDataRequest tdr = new RandomTickDataRequest(_params.symbol,
				9393, _params.isRealTime, 12, 34234);

		_testedStrategy = new TestedStrategy(this);

		_dataSource = _dfs.createDataSource(tdr, _testedStrategy);

		VirtualBrokerParams vbp = new VirtualBrokerParams(tdr);
		vbp.virtualSymbol = _dataSource.getId();
		vbp.tradingSymbol = _params.symbol.getSymbol();

		vbp.listener = _testedStrategy;
		vbp.isRealTimeRequest = _params.isRealTime;
		vbp.isPaperTradingRequested = _params.isPaperTrading;
		vbp.shellId = "KKJKJ";

		vbp.tickSize = _params.symbol.getTick();
		vbp.tickValue = _params.symbol.getTickValue();

		_vb = _tea.createVirtualBroker(vbp);
		_vb.start();

		/*
		 * Ok, now we start the data source
		 */
		U.debug_var(920942, "starting data source... ready!");
		_dataSource.start();

		try {
			/*
			 * I have here to simulate the kick the can.
			 */
			U.debug_var(238499,
					"Kicking the can... spinning the thread around.");
			for (;;) {
				if (_ended.get()) {
					break;
				}
				synchronized (_ended) {
					_ended.wait(10_000);
				}
			}

			if (Thread.interrupted()) {
				return;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			U.debug_var(920942, "Start of shutting down of shell ");
			_vb.stop();
			_dataSource.stop();
			U.debug_var(827385,
					"The shell thread dies here..................................... dead!");
		}

	}

	/**
	 * joins the shell, it steals the thread until the shell thread ends.
	 * 
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException {
		_shellThread.join();
	}

	/**
	 * Starts the given shell in its own thread.
	 * 
	 * <p>
	 * If the request is a database request the created thread will close
	 * automatically when the request comes to an end.
	 * 
	 * <p>
	 * If the request is real time, however, the shell must be stopped from the
	 * outside
	 * 
	 * <p>
	 * Of course it can be stopped from the outside also in the case of a
	 * database request, to simulate a user stop, but usually the stop is called
	 * only to stop a real time tested shell.
	 * 
	 */
	public void start() {
		_shellThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					_run();
				} catch (TEAException | DFSException e) {
					e.printStackTrace();
				}
			}
		});

		_shellThread.start();
	}

	/**
	 * Stops the given shell, but does not wait automatically to it to actually
	 * stop (if you want you may call {@link #join()})
	 */
	public void stop() {
		_shellThread.interrupt();
	}

	public void end() {
		_ended.set(true);
		synchronized (_ended) {
			_ended.notify();
		}
	}

	/**
	 * Sends an order from a pattern in the strategy.
	 * 
	 * @param mfgOrder
	 */
	public void sendOrder(IOrderMfg mfgOrder) {
		// TEAOrder aOrder = new TEAOrder(mfgOrder, "DD");
		try {
			_vb.placeOrder(mfgOrder, true);
		} catch (TEAException e) {
			e.printStackTrace();
		}
	}

}
