package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.model.IParallelRealTimeZZModel;
import com.mfg.chart.model.IParallelRealTimeZZModel.Data;

public class ParallelRealTimeZZDataset implements IDataset {

	private final IParallelRealTimeZZModel.Data _data;

	public ParallelRealTimeZZDataset(Data data) {
		_data = data;
	}

	@Override
	public int getSeriesCount() {
		return _data == null ? 0 : 3;
	}

	@Override
	public int getItemCount(int series) {
		return 2;
	}

	@Override
	public double getX(int series, int item) {
		return item == 0 ? _data.x1 : _data.x2;
	}

	@Override
	public double getY(int series, int item) {
		switch (series) {
		case 0:
			return (item == 0 ? _data.y1 : _data.y2) + _data.topDistance;
		case 1:
			return (item == 0 ? _data.y1 : _data.y2);
		case 2:
			return (item == 0 ? _data.y1 : _data.y2) - _data.bottomDistance;
		}
		return 0;
	}

}
