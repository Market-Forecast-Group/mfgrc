package com.mfg.connector.csv.reader;

import com.mfg.common.Tick;
import com.mfg.dm.FilterOneTick;

public class BarDeserializator {

	public int fake_time = 0;
	public long bar_dur;
	public FilterOneTick fotd = null;
	
	public com.mfg.dm.FillGapsMachine fga;
	public Tick last_sent_qt = null;
	public long last_physical_time = -1;
	public int tick;
	public void_f_QueueTick cb;
	public int scale;

}
