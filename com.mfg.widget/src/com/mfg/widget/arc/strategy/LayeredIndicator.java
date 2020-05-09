package com.mfg.widget.arc.strategy;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.mfg.common.QueueTick;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.TickAdapter;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.indicator.mdb.PivotMDB.RandomCursor;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.utils.U;

/**
 * The layered indicator is a special indicator in the sense that it is composed
 * of layers. Each <i>layer</i> is itself a {@linkplain MultiscaleIndicator}
 * which is fed by a different {@linkplain CompositeDataSource}.
 * 
 * <p>
 * The layered indicator does not have logic. It's only aim is to group
 * different indicators (which may have different parameters, but this is not
 * always the case).
 * 
 * @author Sergio
 * 
 */
public final class LayeredIndicator extends TickAdapter implements IIndicator,
		IIndicatorConsumer {

	/**
	 * This enumeration lists the mode in which the indicator will give the data
	 * to the outside. At first the only mode was {@link #GIVE_UNIQUE_INDICATOR}
	 * , with the indicator firmly based on the first layer (it was fixed). Now
	 * the layered indicator is able to give different modes.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	public enum ELayeredStatus {
		GIVE_SEPARATED_INDICATORS, MERGE_INDICATORS
	}

	/**
	 * A virtual scale is a scale which is mapped to a real scale in a real
	 * layer and a real scale.
	 * 
	 * <p>
	 * All the data is mapped, except for the tentative pivot of the upper
	 * layers which need to be updated in real time.
	 * 
	 * <p
	 * This structure holds the correspondence between a physical scale and a
	 * layer and a level in the array of indicators.
	 * 
	 * <p>
	 * The level is 1-based, the layer is 0-based.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	public final class VirtualScale {
		public final int _layer;

		public final int _level;

		Point _tentativePivot;

		/**
		 * The stats for this virtual scale.
		 */
		// public double[] _stats;

		public VirtualScale(int aLayer, int aLevel) {
			_layer = aLayer;
			_level = aLevel;

			// if (_layer != 0) {
			// _tentativePivot = new Point(-1, -1);
			// }
		}

		public Point getCurrentTentativePivot() {
			Point res;
			if (_layer == 0) {
				return fLayers.get(0 /* _layer */).getCurrentTentativePivot(
						_level);
			}
			res = _tentativePivot;

			return res;
		}

		@SuppressWarnings("boxing")
		public void onNewTickFromLayerZero(QueueTick qt) {

			// the tentative pivot for layer zero is OK.
			// it is computed by the right indicator.
			if (_layer == 0) {
				return;
			}

			Point tentPivot = fLayers.get(_layer).getCurrentTentativePivot(
					_level);

			/*
			 * Initialization of the tentative pivot from the last update of the
			 * real scale.
			 */
			if (_tentativePivot == null) {
				_tentativePivot = (Point) tentPivot.clone();

				if (_tentativePivot.x < 0) {
					/*
					 * The tentative is the current tick!
					 */
					_tentativePivot.x = qt.getFakeTime();
					_tentativePivot.y = qt.getPrice();

					U.debug_var(920185, "Virtual Scale @ ", _layer, " scale ",
							_level, " no tentative pivot yet, fixing it to ",
							_tentativePivot.x, " p ", _tentativePivot.y);

					return;
				}

				long physTimeTentative = fLayers.get(_layer).getPhysicalTimeAt(
						_tentativePivot.x);

				int oldTentativeX = _tentativePivot.x;
				_tentativePivot.x = fLayers.get(0).getFakeTimeFor(
						physTimeTentative, false);

				long physTimeZero = fLayers.get(0).getPhysicalTimeAt(
						_tentativePivot.x);

				U.debug_var(837428, "virtual scale @", _layer, " scale ",
						_level, " tentative.x ", oldTentativeX, " phys time ",
						new Date(physTimeTentative), " newTx ",
						_tentativePivot.x, " which is ", new Date(physTimeZero));

			}

			if (tentPivot.x < 0) {
				return;
			}

			if (fLayers.get(_layer).isSwingDown(_level)) {
				/*
				 * Ok, the scale is a ZAG, I have to know if I break a new DOWN
				 * tentative pivot
				 */
				if (qt.getPrice() <= tentPivot.y) {
					/*
					 * the new price is lower than the current tentative pivot,
					 * I update it
					 */
					_tentativePivot.x = qt.getFakeTime();
					_tentativePivot.y = qt.getPrice();
				}
			} else {
				/*
				 * Ok, the scale is a ZIG, I have to know if I break a new UP
				 * tentative pivot
				 */
				if (qt.getPrice() >= tentPivot.y) {
					/*
					 * the new price is lower than the current tentative pivot,
					 * I update it
					 */
					_tentativePivot.x = qt.getFakeTime();
					_tentativePivot.y = qt.getPrice();
				}
			}

		}
	}

	private static final long MIN_PIVOTS = 15;

	/**
	 * The array that stores the layers. See the
	 * {@link #addLayer(MultiscaleIndicator)} method to indications about the
	 * layers order.
	 */
	final ArrayList<MultiscaleIndicator> fLayers = new ArrayList<>();

	/**
	 * The layered indicator uses the session because it needs to access the
	 * full range of pivots to do the merging of the scales.
	 */
	private final IndicatorMDBSession _session;

	/**
	 * At first the indicator will give the separated indicators, after the
	 * statistics are done the user can choose to merge the indicators together
	 * to make a "unique" merged indicator.
	 */
	ELayeredStatus _status = ELayeredStatus.GIVE_SEPARATED_INDICATORS;

	private ArrayList<VirtualScale> _mergedInfo = new ArrayList<>();

	private int _curLevel;

	/**
	 * The chosen layer is the indicator given to the outside in case of
	 * {@link ELayeredStatus#GIVE_SEPARATED_INDICATORS} mode.
	 */
	private int _curLayer;

	public LayeredIndicator(IndicatorMDBSession indicatorSession) {
		_session = indicatorSession;
	}

	/**
	 * searches the given layer
	 * 
	 * @param aLayer
	 * @param numPivots
	 * @param timeFirstPivot
	 * @return the corresponding scale, -1 if the scale cannot be found because
	 *         also in the upper layer there are not enough pivots.
	 * @throws IOException
	 */
	@SuppressWarnings("boxing")
	private int _findCorrespondenceUpperLayer(int aLayer, long numPivots,
			long timeFirstPivot) throws IOException {

		U.debug_var(920815, "Finding correspondence within layer ", aLayer);

		int scales = fLayers.get(aLayer).getChscalelevels();

		int minDelta = Integer.MAX_VALUE;
		int correspondingScale = -1;

		for (int curScale = 2; curScale <= scales; ++curScale) {

			PivotMDB pivotMDB = _session.connectTo_PivotMDB(aLayer, curScale);

			if (pivotMDB.size() == 0) {
				return correspondingScale;
			}

			try (RandomCursor pivots = pivotMDB.randomCursor()) {
				// _session.defer(pivots);

				int indexOfStartPivot = (int) pivotMDB
						.indexOfPivotPhysicalTime(pivots, timeFirstPivot);

				// so the overlapping count is simply the difference
				int overlappingCount = (int) (pivotMDB.size() - indexOfStartPivot);

				int curDelta = (int) Math.abs(numPivots - overlappingCount);

				if (curDelta < minDelta) {
					minDelta = curDelta;
					correspondingScale = curScale;
				}

				U.debug_var(287399, "layer ", aLayer, " scale ", curScale,
						" totalpivots, ", pivotMDB.size(), " overlapping ",
						overlappingCount, " delta ", curDelta);

			}

			// pivots.close();

		}

		U.debug_var(380195, "The winner scale is ", correspondingScale,
				" with a delta of ", minDelta);

		return correspondingScale;
	}

	private void _onNewTickFromLayerZero(QueueTick qt) {
		for (VirtualScale vs : _mergedInfo) {
			vs.onNewTickFromLayerZero(qt);
		}
	}

	/**
	 * updates the layer level information. It will give to the outside a view
	 * of a normal indicator or a separate view of different indicators.
	 */
	private void _updateLayerLevel(int aLevel) {
		switch (_status) {
		case GIVE_SEPARATED_INDICATORS:
			_curLevel = aLevel;
			break;
		case MERGE_INDICATORS:
			VirtualScale info = _mergedInfo.get(aLevel - 1);
			_curLayer = info._layer;
			_curLevel = info._level;
			break;
		default:
			break;
		}

	}

	/**
	 * a Layer is only an indicator. The indicator is then stored there. The
	 * {@link LayeredIndicator} is not responsible to update the indicators
	 * individually.
	 * 
	 * <p>
	 * The layers are inserted in the same order of the request: from daily to
	 * range. This means that the real time layer is actually the last. This is
	 * the inverse convention used by the cache expanders, but it has been
	 * historically so.
	 * 
	 * 
	 * @param aLayer
	 */
	public void addLayer(MultiscaleIndicator aLayer) {
		/*
		 * If this is the first layer I want to subscribe myself to the ticks of
		 * this indicator as I want to be able to update the tentative pivots of
		 * the virtual scales which needs the ticks of the first layer.
		 */
		if (fLayers.size() == 0) {
			aLayer.addIndicatorConsumer(this);
		}
		fLayers.add(aLayer);
	}

	@Override
	public void begin(int tick) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void consume(IndicatorConsumeArgs args) {
		// I have not yet merged the scales.
		if (_mergedInfo.size() == 0) {
			return;
		}
		/*
		 * I have to consume the tick because it comes from layer zero (I am
		 * subscribed to it).
		 */
		_onNewTickFromLayerZero(args.getTick());
	}

	@Override
	public boolean getChisgoingup(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getChisgoingup(_curLevel);
	}

	@Override
	public int getChscalelevels() {
		switch (_status) {
		case GIVE_SEPARATED_INDICATORS:
			return fLayers.get(_curLayer).getChscalelevels();
		case MERGE_INDICATORS:
			/*
			 * The merged info will always contain the one level which is the
			 * "zero level" of the range layer, usually.
			 */
			return _mergedInfo.size();
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public double getChslope(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getChslope(_curLevel);
	}

	@Override
	public double getCurrentBottomRegressionPrice(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer)
				.getCurrentBottomRegressionPrice(_curLevel);
	}

	@Override
	public double getCurrentCenterRegressionPrice(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer)
				.getCurrentCenterRegressionPrice(_curLevel);
	}

	@Override
	public int getCurrentPivotsCount(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getCurrentPivotsCount(_curLevel);
	}

	@Override
	public int getCurrentPrice() {
		return fLayers.get(fLayers.size() - 1).getCurrentPrice();
	}

	@Override
	public int getCurrentRCTouches(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getCurrentRCTouches(_curLevel);
	}

	@Override
	public int getCurrentSCTouches(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getCurrentSCTouches(_curLevel);
	}

	@Override
	public Point getCurrentTentativePivot(int level) {
		// _updateLayerLevel(level);
		// return fLayers.get(_curLayer).getCurrentTentativePivot(_curLevel);

		if (_status == ELayeredStatus.GIVE_SEPARATED_INDICATORS) {
			return fLayers.get(_curLayer).getCurrentTentativePivot(level);
		}

		return _mergedInfo.get(level - 1).getCurrentTentativePivot();
	}

	@Override
	public long getCurrentTime() {
		/* I assume that the last indicator is the real time indicator */
		return fLayers.get(fLayers.size() - 1).getCurrentTime();
	}

	@Override
	public double getCurrentTopRegressionPrice(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getCurrentTopRegressionPrice(_curLevel);
	}

	@Override
	public int getFakeTimeFor(long physicalTime, boolean exactMatch) {
		return fLayers.get(0).getFakeTimeFor(physicalTime, exactMatch);
	}

	@Override
	public long getHHPrice(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getHHPrice(_curLevel);
	}

	@Override
	public int getHHTime(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getHHTime(_curLevel);
	}

	/**
	 * Get the real indicator scale from the synthetic scale.
	 * 
	 * @author arian
	 * @param aLevel
	 *            The synthetic scale.
	 * @return The real scale.
	 */
	public int getIndicatorScaleFromSynthScale(int aLevel) {
		switch (_status) {
		case GIVE_SEPARATED_INDICATORS:
			return _curLayer;
		case MERGE_INDICATORS:
			VirtualScale info = _mergedInfo.get(aLevel - 1);
			return info._level;
		default:
			throw new IllegalStateException();

		}
	}

	// public double[] getStatisticsForScale(int aLevel) {
	// switch (_status) {
	// case MERGE_INDICATORS:
	// return _mergedInfo.get(aLevel - 1)._stats;
	// case GIVE_SEPARATED_INDICATORS:
	// default:
	// throw new IllegalStateException();
	//
	// }
	// }

	@Override
	public Pivot getLastPivot(int steps, int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getLastPivot(steps, _curLevel);
	}

	/**
	 * gets the real layer for the virtual scale.
	 * 
	 * @param aLevel
	 *            the virtual level
	 * @return the layer which implements this level. It is a number that starts
	 *         from zero. Zero is the range layer.
	 */
	public int getLayerForScale(int aLevel) {
		switch (_status) {
		case GIVE_SEPARATED_INDICATORS:
			return _curLayer;
		case MERGE_INDICATORS:
			VirtualScale info = _mergedInfo.get(aLevel - 1);
			return info._layer;
		default:
			throw new IllegalStateException();

		}
	}

	/**
	 * @return the fLayers
	 */
	public ArrayList<MultiscaleIndicator> getLayers() {
		return fLayers;
	}

	@Override
	public long getLLPrice(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getLLPrice(_curLevel);
	}

	@Override
	public int getLLTime(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).getLLTime(_curLevel);
	}

	@Override
	public AbstractIndicatorParamBean getParamBean() {
		/* I can give one or the other, they should be the same */
		return fLayers.get(0).getParamBean();
	}

	@Override
	public int getStartScaleLevelWidget() {
		return fLayers.get(0).getStartScaleLevelWidget();
	}

	/**
	 * 
	 * @param aLevel
	 *            a level (1/based). It must be in the range [1,
	 *            {@link #getChscalelevels()}].
	 * @return the info associated with the level. In this way the user may get
	 *         the single indicator which is implementing the given virtual
	 *         layer.
	 */
	public VirtualScale getStatForLevel(int aLevel) {

		return _mergedInfo.get(aLevel - 1);

	}

	public ELayeredStatus getStatus() {
		return _status;
	}

	@Override
	public boolean isLevelInformationPresent(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).isLevelInformationPresent(_curLevel);
	}

	@Override
	public boolean isSwingDown(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).isSwingDown(_curLevel);
	}

	@Override
	public boolean isThereANewPivot(int aLevel) {
		_updateLayerLevel(aLevel);
		return fLayers.get(_curLayer).isThereANewPivot(_curLevel);
	}

	@Override
	public boolean isThereANewRC(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).isThereANewRC(_curLevel);
	}

	@Override
	public boolean isThereANewSC(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).isThereANewSC(_curLevel);
	}

	/**
	 * The layered indicator has been subscribed to the daily layer, this means
	 * that when it receives the {@link #onWarmUpFinished()} it will be the end
	 * of all layers.
	 */
	@SuppressWarnings("boxing")
	@Override
	public void onWarmUpFinished() {
		/*
		 * I have subscribed myself to the end of warm up of layer zero which in
		 * this realm means the daily layer.
		 */
		U.debug_var(129583,
				"Got the end of layer daily! Layered indicator will do its computations here.");

		_mergedInfo.clear();

		try {
			/*
			 * let's get the first layer.
			 */
			int curLayer = 0;

			int initialScale = fLayers.get(curLayer).getStartScaleLevelWidget();

			ALL: for (curLayer = 0; curLayer < fLayers.size(); ++curLayer) {
				int scales = fLayers.get(curLayer).getChscalelevels();

				/*
				 * The initial scales are put first. They have no data, this
				 * only for layer zero.
				 */
				if (curLayer == 0)
					for (int i = 1; i < initialScale; ++i) {
						VirtualScale lli = new VirtualScale(0, i);
						_mergedInfo.add(lli);
					}

				for (int curScale = initialScale; curScale <= scales; ++curScale) {

					PivotMDB pivotMDB = _session.connectTo_PivotMDB(curLayer,
							curScale);

					/*
					 * If also the top most scale has a greater number of pivots
					 * I will have to merge it.
					 */
					if (pivotMDB.size() > MIN_PIVOTS && curScale != scales) {
						U.debug_var(283475, "layer ", curLayer, " cur scale ",
								curScale, " has more than ", MIN_PIVOTS,
								" pivots, (", pivotMDB.size(), ") I go on");
						VirtualScale lli = new VirtualScale(curLayer, curScale);
						_mergedInfo.add(lli);
						continue;
					}

					/*
					 * I have to find the number of pivots inside the interval.
					 */
					long firstPivotTime;
					if (pivotMDB.size() == 0) {
						firstPivotTime = 0;
					} else {
						try (RandomCursor pivots = pivotMDB.randomCursor()) {
							// _session.defer(pivots);
							pivots.seek(0);
							firstPivotTime = pivots.pivotPhysicalTime;
						}
					}

					U.debug_var(248759, "(ALMOST EMPTY) layer ", curLayer,
							" scale ", curScale, " npivos ", pivotMDB.size(),
							" first ", new Date(firstPivotTime));

					/*
					 * maybe there is not a next layer, in that case I simply
					 * write it
					 */
					if (curLayer == fLayers.size() - 1) {
						VirtualScale lli = new VirtualScale(curLayer, curScale);
						_mergedInfo.add(lli);
						continue;
					}

					/*
					 * This will be the initial scale for the next layer.
					 */
					initialScale = _findCorrespondenceUpperLayer(curLayer + 1,
							pivotMDB.size(), firstPivotTime);

					if (initialScale < 0) {
						/*
						 * could not found the corresponding scale, so I simply
						 * break it.
						 */
						break ALL;
					}
					break; // this layer is finished

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Just a placeholder to put a breakpoint.
		U.debug_var(
				285715,
				"end of correspondence searching.... The merged indicator has ",
				_mergedInfo.size(), " scales.");

		// for (int i = 0; i < _mergedInfo.size(); ++i) {
		// VirtualScale info = _mergedInfo.get(i);
		//
		// U.debug_var(923049, "Virtual scale ", i + 1,
		// " corresponds to layer ", info._layer, " and scale ",
		// info._level, " it has ", fLayers.get(info._layer)
		// .getCurrentPivotsCount(info._level), " pivots");
		//
		// info._stats = fLayers.get(info._layer)
		// .doS0S0PrimeStats(info._level);
		//
		// }

		U.debug_var(378195,
				">>>>>>>>>>>>>>>>>> end of warm up for layered indicator");

	}

	@Override
	public void setParamBean(AbstractIndicatorParamBean parameters) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This is the status, a layered status can give informations in two ways,
	 * one is merged and one is not.
	 * 
	 * @param aStatus
	 */
	public void setStatus(ELayeredStatus aStatus, int aChosenLayer) {
		this._status = aStatus;
		_curLayer = aChosenLayer;
	}

	@Override
	public void stopped(IIndicator indicator) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isThereANewTentativePivot(int level) {
		_updateLayerLevel(level);
		return fLayers.get(_curLayer).isThereANewTentativePivot(_curLevel);
	}

	@Override
	public int getConfirmThreshold(int aLevel) {
		_updateLayerLevel(aLevel);
		return fLayers.get(_curLayer).getConfirmThreshold(_curLevel);
	}

	@Override
	public double[] getStatsForLevel(int aLevel) {
		_updateLayerLevel(aLevel);
		return fLayers.get(_curLayer).getStatsForLevel(aLevel);
	}

}
