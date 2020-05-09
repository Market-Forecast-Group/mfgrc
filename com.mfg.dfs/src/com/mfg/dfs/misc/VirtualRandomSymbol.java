package com.mfg.dfs.misc;

import java.util.Date;

import com.mfg.common.DFSException;
import com.mfg.common.DFSStoppingSubscriptionEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.DfsSymbol;
import com.mfg.common.QueueTick;
import com.mfg.common.RealTick;
import com.mfg.common.Tick;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.RandomTickDataRequest;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.RandomTickSource;
import com.mfg.utils.U;

public class VirtualRandomSymbol extends VirtualSymbolBase {

	private DfsSymbol _randomDfsSpecimen;

	public VirtualRandomSymbol(MultiServer aServer, TickDataRequest aRequest) {
		super(aServer, aRequest);
		_randomDfsSpecimen = new DfsSymbol(aRequest.getLocalSymbol(), "rand",
				aRequest.getTick(), aRequest.getSymbol().getScale(), aRequest
						.getSymbol().getTickValue());
	}

	@Override
	public DfsSymbol getDfsSymbol() throws DFSException {
		return _randomDfsSpecimen;
	}

	@SuppressWarnings("boxing")
	@Override
	protected void _virtualSymbolThread() {

		long seed = _request.getSeed();

		RandomTickSource rts = new RandomTickSource(seed,
				_randomDfsSpecimen.tick, System.currentTimeMillis());

		rts.setNoGaps();

		RandomTickDataRequest rtdr = (RandomTickDataRequest) _request;

		boolean isRealTime = _request.isRealTime();
		if (!isRealTime) {
			/*
			 * the request is delayable, so I create the delayable listener, to
			 * pause the virtual thread, if it is necessary.
			 */
			_delayControl = new ServerDelayControl();

			// Try to set programmaticaly the speed for the delay control.
			// _delayControl.getModel().setState(
			// DataSpeedControlState.FAST_FORWARDING);

			_delayControl.getModel().setDelay(123);
		}

		int fakeTime = 0;
		// at first the symbol is in warm up.
		boolean inWarmUp = true;
		long previousTickTime = 0;

		while (true) {

			try {
				Tick nextTick = rts.getNextTick();

				if (_delayControl != null) {
					_delayControl.delay(_monitor, fakeTime);
				} else if (!inWarmUp) {
					/*
					 * I have to wait to simulate the real time request.
					 */
					assert (isRealTime);
					long delta = nextTick.getPhysicalTime() - previousTickTime;

					U.debug_var(293851, "Going to wait for ", delta, " real ",
							delta / 5, " simulated  next tick will be @ ",
							new Date(nextTick.getPhysicalTime()));
					Thread.sleep(delta / 5);
				}
				previousTickTime = nextTick.getPhysicalTime();

				finalNotifiyTick(0, new QueueTick(new RealTick(nextTick, true),
						fakeTime));

				/*
				 * This will work also for a real time request, because in that
				 * case I will simulate the end of warm up.
				 */
				if (fakeTime == _request.getNumberWarmupPrices()) {
					notifyEndWarmUp(0, _monitor);
					inWarmUp = false;
				}

				fakeTime++;

				if (fakeTime == rtdr.getMaximumFakeTime()) {
					DFSSymbolEvent quote = new DFSStoppingSubscriptionEvent(_id);
					_server.onNewSymbolEvent(quote);
					break;
				}

			} catch (MonitorCancelledException e) {
				e.printStackTrace();
				U.debug_var(
						239850,
						"normal end (Cancelled monitor) for random virtual symbol ",
						_id);
				break;
			} catch (InterruptedException e) {
				U.debug_var(892851, "Thread interrupted for symbol ", _id,
						" I die here.");
				break;
			}

		}

	}
}
