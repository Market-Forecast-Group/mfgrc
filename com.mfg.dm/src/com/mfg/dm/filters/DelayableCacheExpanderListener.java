package com.mfg.dm.filters;

import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.QueueTick;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.dm.speedControl.IDelayedDataSource;
import com.mfg.utils.U;

/**
 * A cache expander listener which is able to be delayed, paused and stopped,
 * much like a CsvCompositeDataSource.
 * 
 * <p>
 * This object has no processing logic, it acts only as a bridge between a
 * {@link CacheExpander} and a {@link CompositeDataSource}.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DelayableCacheExpanderListener implements ICacheExpanderListener,
		IDelayedDataSource {

	private final ICacheExpanderListener _cds;
	private IDelayControl _control = null;
	private final IProgressMonitor _monitor;

	private final int _numPricesToWarmUp;
	private int _lastFakeTimeGiven;
	private long _endOfWarmUpForOtherLayers;

	private boolean _warmUpGivenForLayers[];
	private long _lastPhysicalTime;

	/**
	 * The delayable listener wants a destination which is the object which is
	 * the ultimate destination of the tick.
	 * 
	 * @param aDestination
	 * @param numWarmUpPrices
	 *            how many prices are used to warmup
	 */
	public DelayableCacheExpanderListener(ICacheExpanderListener aDestination,
			IProgressMonitor aMonitor, int numWarmUpPrices, int numLayers) {
		_cds = aDestination;
		_monitor = aMonitor;
		if (numWarmUpPrices < 0) {
			throw new IllegalArgumentException();
		}
		_numPricesToWarmUp = numWarmUpPrices;
		_lastFakeTimeGiven = -1; // nothing
		_warmUpGivenForLayers = new boolean[numLayers];
	}

	private void _delay() {
		if (_control == null) {
			// U.sleep(1);
		} else {
			_control.delay(_monitor, _lastFakeTimeGiven);
		}
	}

	@Override
	public void endOfExpansion(int aLayer) throws MonitorCancelledException {
		if (!_warmUpGivenForLayers[aLayer]) {
			_cds.notifyEndWarmUp(aLayer, _monitor);
			_warmUpGivenForLayers[aLayer] = true;

			if (aLayer == 0) {
				U.debug_var(201953, "layer zero force end for warm up @ ",
						new Date(_lastPhysicalTime));
				_endOfWarmUpForOtherLayers = _lastPhysicalTime;
			}
		}
	}

	@Override
	public boolean finalNotifiyTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException {
		_delay();
		_cds.finalNotifiyTick(aLayer, aTick);
		_lastFakeTimeGiven = aTick.getFakeTime();
		_lastPhysicalTime = aTick.getPhysicalTime();
		if (aLayer == 0 && _lastFakeTimeGiven == _numPricesToWarmUp) {
			_cds.notifyEndWarmUp(aLayer, _monitor);

			_endOfWarmUpForOtherLayers = aTick.getPhysicalTime();
			U.debug_var(294052, "End of warm up for other layers is "
					+ new Date(aTick.getPhysicalTime()));
			_warmUpGivenForLayers[aLayer] = true;
			return true;
		} else if (aLayer != 0
				&& aTick.getPhysicalTime() > _endOfWarmUpForOtherLayers
				&& !_warmUpGivenForLayers[aLayer]) {
			_cds.notifyEndWarmUp(aLayer, _monitor);
			_warmUpGivenForLayers[aLayer] = true;
			return true;
		}
		return false;
	}

	@Override
	public IDelayControl getDelayControl() {
		return _control;
	}

	public boolean isInWarmUp() {
		if (_lastFakeTimeGiven < _numPricesToWarmUp)
			return true;
		return false;
	}

	@Override
	public void notifyEndWarmUp(int aLayer, IProgressMonitor _monitor1) {
		// here is not meaningful, I expect the endOfExpansion to be called.
		throw new IllegalStateException();
	}

	@Override
	public void onVolumeUpdate(int aFakeTime, int aVolume) {
		// TODO Auto-generated method stub

	}

	@Override
	public void passNotFinalTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException {
		_delay();
		_cds.passNotFinalTick(aLayer, aTick);
	}

	@Override
	public void setDelayControl(IDelayControl control) {
		_control = control;
	}

}
