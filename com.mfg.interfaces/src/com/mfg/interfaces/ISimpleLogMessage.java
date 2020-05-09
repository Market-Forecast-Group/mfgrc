package com.mfg.interfaces;

import java.io.Serializable;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter;

public interface ISimpleLogMessage extends Serializable {
	public final static String CATEGORY_COMMENT = "COMMENT";
	public final static String CATEGORY_TH = "TH";
	public final static String CATEGORY_TARGET = "TARGET";

	public int getLogPriority();

	long getTHTime();
	void setTHTime(long time);

	long getTime();
	void setTime(long time);

	long getPrice();
	void setPrice(long price);

	long getTHPrice();
	void setTHPrice(long price);

	String getCategory();
	void setCategory(String category);

	String getMessage();
	void setMessage(String msg);

	boolean passFilter(IProbabilitiesFilter f);

	long getTargetPrice();
	void setTargetPrice(long price);

	public long getTimeCPU();
	void setTimeCPU(long time);

}
