package com.mfg.symbols.ui.chart.models;

import com.mfg.chart.model.IChannel2Collection;
import com.mfg.chart.model.ItemCollection;
import com.mfg.inputdb.indicator.mdb.Channel2MDB;
import com.mfg.inputdb.indicator.mdb.Channel2MDB.Record;
import com.mfg.widget.arc.math.geom.PolyEvaluator;

public class Channel2Collection extends ItemCollection<Channel2MDB.Record>
		implements IChannel2Collection {

	private final int _degree;

	public Channel2Collection(Record[] data, int degree) {
		super(data);
		_degree = degree;
	}

	@Override
	public double getStart(int i) {
		return getItem(i).startTime;
	}

	@Override
	public double getEnd(int i) {
		return getItem(i).endTime;
	}

	@Override
	public double evaluateCentarLine(int i, double time) {
		Record r = getItem(i);
		double[] c = new double[_degree + 1];
		c[0] = r.c0;
		c[1] = r.c1;
		if (_degree > 1) {
			c[2] = r.c2;
			if (_degree > 2) {
				c[3] = r.c3;
				if (_degree > 3) {
					c[4] = r.c4;
				}
			}
		}
		double y = PolyEvaluator.evaluate(c, time);
		return y;
	}

	@Override
	public double getTopDistance(int i) {
		return getItem(i).topDistance;
	}

	@Override
	public double getBottomDistance(int i) {
		return getItem(i).bottomDistance;
	}

}
