package com.mfg.dm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.DFSVolumeUpdateEvent;
import com.mfg.common.DFSWarmUpFinishedEvent;
import com.mfg.common.ISymbolListener;
import com.mfg.common.IDataSource;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.utils.U;

/**
 * The {@link MfgDataSource} is a class which will be the substitute for
 * {@link CompositeDataSource} which is the Client side version of the data
 * source.
 * 
 * <p>
 * This data source is inside DFS and DFS is responsible to make the merge of
 * all the different caches.
 * 
 * <p>
 * The data itself may be in the client version of it, in the sense that if DFS
 * is embedded it is in the same process space of MFG, so it seems there is not
 * so much difference. But the difference is mostly logical, because the data
 * comes to MFG not in the form of bars but in the form of ticks already
 * expanded.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class MfgDataSource extends CompositeDataSource implements
		ISymbolListener, PropertyChangeListener {

	/*
	 * This is only a proxy version of the data source. The bars are not here
	 * and here only the prices stream is coming.
	 * 
	 * this is a class used to maintain the interface of the
	 * CompositeDataSource, but purging all the things in it which are not
	 * useful.
	 * 
	 * The cache expander listener method inherited are good, but the problem is
	 * that the expansion could be layered.
	 * 
	 * If the request is database I should have the possibility to control it.
	 * 
	 * So the expansion will be created in servers space and this class is only
	 * a remote controlled stream of ticks.
	 */

	@Override
	public String getDataSourceId() {
		return _dataSource.getId();
	}

	private IDataSource _dataSource;

	public MfgDataSource(TickDataRequest cdr1, IDataProvider dp, UUID aId) {
		super(cdr1, dp, aId);

	}

	/*
	 * if the server side data source is interrupted there is a monitor cancel
	 * exception.
	 * 
	 * Where does it go?
	 * 
	 * mm. this object is created in the client side of dfs, it is server side
	 * but it is in reality client side.
	 * 
	 * This client side is in some way the part which can use the history.
	 */

	/**
	 * here it is a no/op, forever here... because the start will subscribe to
	 * the virtual symbol and it will create the virtual symbol's thread.
	 * 
	 * <p>
	 * The kick the can should steal the caller's thread and this is OK, it will
	 * steal it, but it will do nothing, because the real data will come from
	 * the virtual symbol.
	 * 
	 */
	@Override
	public void kickTheCan(IProgressMonitor aMonitor) throws DFSException {
		_currentMonitor = aMonitor;

		try {

			for (;;) {
				synchronized (_currentMonitor) {
					_currentMonitor.wait(3000);
				}
				if (aMonitor.isCanceled()) {
					break;
				}
			}
		} catch (InterruptedException e) {
			/*
			 * This is a not so normal end of the data source, please
			 * investigate.
			 */
			e.printStackTrace();
		} finally {
			notifyStopping();
			/*
			 * this is tricky, because if the monitor is cancelled there should
			 * be a MonitorCancelledException in the subscription. But in that
			 * case the method will notify the monitor and we end here.
			 */
			stop();
		}

		U.debug_var(233952, "normal end for kick the can of ",
				_dataSource.getId());
	}

	@Override
	public EStartOutput start(IProgressMonitor monitor) throws DFSException {
		_inWarmUp = true;

		/*
		 * This actually starts a virtual symbol in server's space, the
		 * interface returned may be a real data source or a proxy data source
		 * to the server.
		 * 
		 * The data source will never subscribes to the symbol. This is the
		 * local listener to the virtual symbol itself.
		 * 
		 * so the subscription to the virtual symbol must not be passed through
		 * the dfs data provider.
		 * 
		 * This also because a virtual symbol is unique to a certain job in a
		 * certain time. Also the same job at different times will have a
		 * different virtual symbol id.
		 */
		_currentMonitor = monitor;
		_dataSource = fDp.createDataSource(this, _request);
		/*
		 * The start is already called by the onNewQuote event.
		 */
		// _notifyStarting();
		_dataSource.start();

		return EStartOutput.START_OK;
	}

	@Override
	public void stop() throws DFSException {
		_dataSource.stop();
	}

	@Override
	public void setDelayControl(IDelayControl control) {
		_delayControl = control;
		_delayControl.getModel().setState(DataSpeedControlState.PLAYING);
		_delayControl.getModel().addPropertyChangeListener(this);
	}

	@Override
	public void onNewSymbolEvent(DFSSymbolEvent aQuote) {
		/*
		 * Here there is actually the dispatching of the quote to the inherited
		 * methods of the ICacheExpanderListener interface.
		 * 
		 * There is no thread, because the quote must be processed in the same
		 * thread as the listener. If this quote comes from a proxy the proxy
		 * must be informed when this client has finished processing it.
		 */
		try {
			if (aQuote instanceof DFSWarmUpFinishedEvent) {
				DFSWarmUpFinishedEvent wufe = (DFSWarmUpFinishedEvent) aQuote;
				notifyEndWarmUp(wufe.layer, _currentMonitor);
			} else if (aQuote instanceof DFSQuote) {
				DFSQuote pQ = (DFSQuote) aQuote;

				if (pQ.finalTick) {
					finalNotifiyTick(pQ.layer, pQ.tick);
				} else {
					passNotFinalTick(pQ.layer, pQ.tick);
				}
			} else if (aQuote instanceof DFSSubscriptionStartEvent) {
				DFSSubscriptionStartEvent dsse = (DFSSubscriptionStartEvent) aQuote;
				_notifyStartingPars(dsse._tick, dsse._scale);
			} else if (aQuote instanceof DFSVolumeUpdateEvent) {
				DFSVolumeUpdateEvent dvue = (DFSVolumeUpdateEvent) aQuote;

				onVolumeUpdate(dvue._fakeTime, dvue._volume);
			}
		} catch (MonitorCancelledException e) {
			/*
			 * If I am here then the user wants to halt the streaming. I have to
			 * pass this desire to the underlying data source.
			 * 
			 * If I am here the cancelled monitor should have also broken the
			 * kick the can method.
			 */
			synchronized (this) {
				/*
				 * This notify may help the kick the can method to end rapidly.
				 */
				this.notify();
			}

			/*
			 * We cannot stop here the data source, because a dead lock may
			 * happen if DFS is remote.
			 * 
			 * This because the onNewQuote is called by a push sink which
			 * injects the code using the runMainLoop in SimpleSocketTextClient
			 * which is blocked here, but in the stop method the Unsubscribe
			 * command is sent and we cannot join the answer as the processLine
			 * is blocked to wait this code to return.
			 * 
			 * So the sensible way is to nofify the kick the can and stop there
			 * the data source.
			 */

			/*
			 * Tech note. probably I have to rethrow the exception, as the
			 * MfgDataSource can be attached to a proxy data source which may
			 * need the exception to delete the push source in the server.
			 */
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		DataSpeedControlState state = _delayControl.getModel().getState();

		/*
		 * I pass the information to the data source which will be either a
		 * proxy or an embedded data source. In that case the data source will
		 * marshall the property change towards a socket.
		 */

		//U.debug_var(763782, "PROP change in mfg data source ",
		//		evt.getPropertyName(), " state ", state);

		/*
		 * I may be called before the start method which creates the data
		 * source.
		 */
		if (_dataSource == null) {
			return;
		}

		try {
			if (evt.getPropertyName().compareTo("state") == 0) {
				switch (state) {
				case DISABLED:
					break;
				case FAST_FORWARDING:
					_dataSource.fastForward();
					break;
				case INITIAL:
					break;
				case PAUSED:
					_dataSource.pause();
					break;
				case PLAYING:
					_dataSource.play();
					break;
				case STEP:
					_dataSource.step();
					break;
				case STOPPED:
					break;
				default:
					break;
				}

			} else if (evt.getPropertyName().compareTo("delay") == 0) {
				_dataSource.setDelay(_delayControl.getModel()
						.getDelayInMillis());
			} else if (evt.getPropertyName().compareTo("timeToRun") == 0) {
				_dataSource.fullSpeedUntil((int) _delayControl.getModel()
						.getTimeToRun());
			}
		} catch (DFSException e) {
			//
		}

	}
}
