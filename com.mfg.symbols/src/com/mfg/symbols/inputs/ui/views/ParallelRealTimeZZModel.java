package com.mfg.symbols.inputs.ui.views;

import java.awt.Point;
import java.io.IOException;

import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IParallelRealTimeZZModel;
import com.mfg.chart.model.PriceModel_MDB;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;
import com.mfg.widget.arc.strategy.StraightLineFreeIndicator;

public class ParallelRealTimeZZModel implements IParallelRealTimeZZModel {

	private final PriceModel_MDB _priceModel;
	private final int _level;
	private final LayeredIndicator _layeredIndicator;

	public ParallelRealTimeZZModel(LayeredIndicator layeredIndicator,
			int level, IChartModel chartModel) {
		_level = level;
		_layeredIndicator = layeredIndicator;
		_priceModel = ((PriceModel_MDB) chartModel.getPriceModel());
	}

	public PriceModel_MDB getPriceModel() {
		return _priceModel;
	}

	public LayeredIndicator getIndicator() {
		return _layeredIndicator;
	}

	@Override
	public Data getRealtimeZZ(int dataLayer) {
		try {
			MultiscaleIndicator indicator = _layeredIndicator.getLayers().get(
					dataLayer);
			Pivot lastPivot = indicator.getLastPivot(0, _level);
			Point tentativePivot = indicator.getCurrentTentativePivot(_level);

			if (lastPivot != null && tentativePivot != null) {
				Data d = new Data();
				long x1 = lastPivot.getPivotTime();
				d.x1 = getTime(dataLayer, x1);
				d.y1 = lastPivot.getPivotPrice();
				long x2 = (long) tentativePivot.getX();
				d.x2 = getTime(dataLayer, x2);
				d.y2 = (long) tentativePivot.getY();

				StraightLineFreeIndicator ind = new StraightLineFreeIndicator(
						_priceModel.getMDB(dataLayer), (int) x1);
				ind.setRightAnchor((int) x2);
				d.topDistance = ind.getTopDistance();
				d.bottomDistance = ind.getBottomDistance();

				return d;
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			//
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param dataLayer
	 * @param displayTime
	 * @return
	 */
	@SuppressWarnings("static-method")
	protected long getTime(int dataLayer, long displayTime) {
		return displayTime;
	}
}
