package com.mfg.widget.arc.strategy;

import static com.mfg.utils.Utils.debug_var;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.CsvSymbol;
import com.mfg.common.DFSException;
import com.mfg.common.MfgSymbol;
import com.mfg.common.QueueTick;
import com.mfg.connector.csv.CsvCompositeDataSource;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.TickDataSource;
import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * This class it is simply a class which takes a Channel Widget and can "freeze"
 * it; in this way all the states are recorded and the simulation can be faster.
 */
public class FrozenWidget extends TickAdapter implements IIndicator {

	/**
	 * All the states for each scale are inside this matrix. The rows are the
	 * scales and the columns are the ticks. The cells are
	 * FrozenChannelWidgetState objects.
	 */
	protected FrozenChannelWidgetState _myStates[][] = null;

	/**
	 * This array holds (for each scale) the current pivot index. This because
	 * many states will share the same pivot.
	 */
	protected int _currentPvIndeces[] = null;

	/**
	 * Each Array is a variable length of the pivots for a particular scale.
	 */
	protected ArrayList<Pivot> _frozenPivots[] = null;

	@SuppressWarnings("unchecked")
	protected void _helpMeFreezeThisWidget(final IIndicator aWidget,
			CSVSymbolData csvSymbol, final IProgressMonitor monitor,
			long totalSteps) throws DFSException {

		MfgSymbol aSymbol = new CsvSymbol(csvSymbol.getFile().getAbsolutePath());
		final CsvCompositeDataSource dataSource = new CsvCompositeDataSource(
				aSymbol);

		dataSource.addTickListener(TickDataSource.CSV_LAYER, new TickAdapter() {

			@Override
			public void onStopping() {
				_printSomeStats();
			}

			@Override
			public void onStarting(int tick, int scale) {
				aWidget.onStarting(tick, scale);
				_commonBegin();
			}

			@Override
			public void onNewTick(QueueTick qt) {
				aWidget.onNewTick(qt);
				/**
				 * Ok, I must now freeze this state.!!!!
				 */
				fCurrentTick = qt.clone();
				_freezeThisState(aWidget);
			}
		});
		dataSource.start(null);

		_myStates = new FrozenChannelWidgetState[this._scalelevels
				- this._startScaleLevelWidget + 1][(int) totalSteps];

		_frozenPivots = new ArrayList[this._scalelevels
				- this._startScaleLevelWidget + 1];

		for (int i = 0; i < _frozenPivots.length; ++i) {
			_frozenPivots[i] = new ArrayList<>();
		}

		_currentPvIndeces = new int[this._scalelevels
				- this._startScaleLevelWidget + 1];

		Arrays.fill(_currentPvIndeces, -1);

		dataSource.playTicks(monitor);
	}

	// private static final long serialVersionUID = -6162599004543040272L;

	transient protected String seriesName;
	transient protected IndicatorParamBean parameters;

	/**
	 * This is the TOTAL number of levels, but this number could not be the
	 * total number of the rows in the matrix, because we could have the
	 * possibility of a start scale != 0.
	 */
	private final int _scalelevels;
	private final int _startScaleLevelWidget;

	QueueTick fCurrentTick = null;

	public FrozenWidget(int scalelevels, int startScaleLevelWidget) {
		super();
		_scalelevels = scalelevels;
		_startScaleLevelWidget = startScaleLevelWidget;
	}

	@Override
	public int getCurrentPivotsCount(int level) {
		return _myStates[level - this._startScaleLevelWidget][fCurrentTick
				.getFakeTime()].pivotIndex + 1;
	}

	@SuppressWarnings("boxing")
	protected void _printSomeStats() {
		// It prints out the number of states for each level.
		for (int i = 0; i < _myStates.length; i++) {
			debug_var(828311, "I have " + _myStates[i].length
					+ " states at level " + i, " pivots # ",
					_frozenPivots[i].size());
		}
	}

	/**
	 * Function called by the two onStarting methods of the class, one when the
	 * widget is being frozen and then when the widget is used.
	 */
	protected void _commonBegin() {
		// Nothing
	}

	private void newTickPrice(QueueTick qt) {
		fCurrentTick = qt.clone();
	}

	protected void _freezeThisState(IIndicator aWidget) {

		IIndicator piw = aWidget;

		int startLev = getStartScaleLevelWidget();

		for (int level = startLev; level <= _scalelevels; ++level) {

			// Every level has its state, so I must create a new object for each
			// level.
			FrozenChannelWidgetState fcws = new FrozenChannelWidgetState();

			fcws.currentSCTouches = piw.getCurrentSCTouches(level);
			fcws.currentRCTouches = piw.getCurrentRCTouches(level);

			fcws.top___RegressionPrice = piw
					.getCurrentTopRegressionPrice(level);
			fcws.centerRegressionPrice = piw
					.getCurrentCenterRegressionPrice(level);
			fcws.bottomRegressionPrice = piw
					.getCurrentBottomRegressionPrice(level);
			// fcws.isPriceDecreasing = piw.isPriceDecreasing(level);
			fcws.HHTime = piw.getHHTime(level);
			fcws.LLTime = piw.getLLTime(level);
			fcws.HHPrice = piw.getHHPrice(level);
			fcws.LLPrice = piw.getLLPrice(level);
			fcws.chslope = piw.getChslope(level);

			if (piw.isThereANewPivot(level)) {

				Pivot lastPivot = piw.getLastPivot(0, level);
				_currentPvIndeces[level - _startScaleLevelWidget] = this._frozenPivots[level
						- _startScaleLevelWidget].size();
				this._frozenPivots[level - _startScaleLevelWidget]
						.add(lastPivot);
				fcws.isThereANewPivot = true;
			} else {
				fcws.isThereANewPivot = false;
			}
			fcws.pivotIndex = _currentPvIndeces[level - _startScaleLevelWidget];
			_myStates[level - _startScaleLevelWidget][fCurrentTick
					.getFakeTime()] = fcws;
		}

	}

	/**
	 * This method is called by a "normal" widget to freeze itself. The method
	 * call is a bit convoluted, but this for legacy reasons, to be changed.
	 * 
	 * @param monitor
	 * @param totalSteps
	 * @throws DFSException
	 */
	public void freezeMe(IIndicator aWidget, CSVSymbolData csvSymbol,
			IProgressMonitor monitor, long totalSteps) throws DFSException {
		setParamBean(aWidget.getParamBean());
		_helpMeFreezeThisWidget(aWidget, csvSymbol, monitor, totalSteps);
	}

	/**
	 * Get the value of seriesName
	 * 
	 * @return the value of seriesName
	 */
	public String getSeriesName() {
		return seriesName;
	}

	/**
	 * Set the value of seriesName
	 * 
	 * @param seriesName1
	 *            new value of seriesName
	 */
	public void setSeriesName(String seriesName1) {
		this.seriesName = seriesName1;
	}

	/**
	 * Get the value of parameters
	 * 
	 * @return the value of parameters
	 */
	@Override
	public IndicatorParamBean getParamBean() {
		return parameters;
	}

	/**
	 * Set the value of parameters
	 * 
	 * @param parameters1
	 *            new value of parameters
	 */
	@Override
	public void setParamBean(AbstractIndicatorParamBean parameters1) {
		this.parameters = (IndicatorParamBean) parameters1;
	}

	// //////////////////////////////////////////////////////
	// The PivotsIndicatorWidget interface
	@Override
	public long getCurrentTime() {
		return fCurrentTick.getFakeTime();
	}

	@Override
	public int getCurrentPrice() {
		return fCurrentTick.getPrice();
	}

	@Override
	public double getCurrentTopRegressionPrice(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].top___RegressionPrice;
	}

	@Override
	public double getCurrentCenterRegressionPrice(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].centerRegressionPrice;
	}

	@Override
	public double getCurrentBottomRegressionPrice(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].bottomRegressionPrice;
	}

	@Override
	public double getChslope(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].chslope;
	}

	@Override
	public int getChscalelevels() {
		return _scalelevels;
	}

	@Override
	public boolean getChisgoingup(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].chslope > 0;
	}

	@Override
	public boolean isSwingDown(int level) {
		Pivot pv = getLastPivot(0, level);
		if (pv == null) {
			// warn("null pivot @ " + fCurrentTick.getFakeTime() + " lev " +
			// level);
			return false;
		}
		return pv.isStartingDownSwing();
	}

	@Override
	public int getHHTime(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].HHTime;
	}

	@Override
	public int getLLTime(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].LLTime;
	}

	@Override
	public long getHHPrice(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].HHPrice;
	}

	@Override
	public long getLLPrice(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].LLPrice;
	}

	@Override
	public Pivot getLastPivot(int steps, int level) {

		ArrayList<Pivot> frozenPivots = _frozenPivots[level
				- _startScaleLevelWidget];

		int pivotIndex = _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].pivotIndex;

		if (pivotIndex < 0) {
			// no pivots arrived yet
			return null;
		}

		// Steps is NEGATIVE!
		int pvArrIndex = pivotIndex + steps;

		if (pvArrIndex < 0) {
			throw new IllegalArgumentException("underflow " + pvArrIndex
					+ " pv requested " + level + " step " + steps);
		}

		if (pvArrIndex >= frozenPivots.size()) {
			throw new IllegalArgumentException("overflow " + pvArrIndex
					+ " pv requested lev: " + level + " step " + steps + " @ "
					+ fCurrentTick.getFakeTime());
		}

		return frozenPivots.get(pvArrIndex);
	}

	@Override
	public boolean isThereANewPivot(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].isThereANewPivot;
	}

	@Override
	public int getStartScaleLevelWidget() {
		return _startScaleLevelWidget;
	}

	@Override
	public boolean isLevelInformationPresent(int aLevel) {
		if (aLevel >= _startScaleLevelWidget && aLevel <= this._scalelevels) {
			return true;
		}
		return false;
	}

	@Override
	public void onNewTick(QueueTick qt) {
		newTickPrice(qt);
	}

	@Override
	public void onStarting(int tick, int scale) {
		_commonBegin();
	}

	// @Override
	// public void onNoNewTick() {
	// // nothing
	// }

	@Override
	public void onStopping() {
		// nothing
	}

	@Override
	public void begin(int tick) {
		// nothing here.

	}

	@Override
	public Point getCurrentTentativePivot(int level) {
		return null;
	}

	@Override
	public int getCurrentSCTouches(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].getCurrentSCTouches();
	}

	@Override
	public int getCurrentRCTouches(int level) {
		return _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].getCurrentRCTouches();
	}

	@Override
	public int getFakeTimeFor(long physicalTime, boolean exactMatch) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isThereANewSC(int level) {
		int prevSCTouches = 0;
		if (fCurrentTick.getFakeTime() != 0) {
			prevSCTouches = _myStates[level - _startScaleLevelWidget][fCurrentTick
					.getFakeTime() - 1].getCurrentSCTouches();
		}
		int curSCTouches = _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].getCurrentSCTouches();
		return curSCTouches != prevSCTouches;
	}

	@Override
	public boolean isThereANewRC(int level) {
		int prevRCTouches = 0;
		if (fCurrentTick.getFakeTime() != 0) {
			prevRCTouches = _myStates[level - _startScaleLevelWidget][fCurrentTick
					.getFakeTime() - 1].getCurrentRCTouches();
		}
		int curRCTouches = _myStates[level - _startScaleLevelWidget][fCurrentTick
				.getFakeTime()].getCurrentRCTouches();
		return curRCTouches != prevRCTouches;
	}

	@Override
	public boolean isThereANewTentativePivot(int level) {
		assert (false);
		return false;
	}

	@Override
	public int getConfirmThreshold(int aLevel) {
		return Integer.MIN_VALUE;
	}

	@Override
	public double[] getStatsForLevel(int aLevel) {
		assert (false);
		return null;
	}

}
