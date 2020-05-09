package com.mfg.strategy.automatic.exportIndicator;

import java.util.Arrays;

import com.mfg.interfaces.indicator.IIndicator;

public class IndicatorExportingConfiguration {
	private String fileName;
	private ScaleIndicatorExportingConfiguration[] scaleIndicatorExportingConfigurations;
	private boolean includingTime = true;
	private boolean includingPrice;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String aFileName) {
		this.fileName = aFileName;
	}
	public ScaleIndicatorExportingConfiguration[] getScaleIndicatorExportingConfigurations() {
		return scaleIndicatorExportingConfigurations;
	}
	public void setScaleIndicatorExportingConfigurations(
			ScaleIndicatorExportingConfiguration[] aScaleIndicatorExportingConfigurations) {
		this.scaleIndicatorExportingConfigurations = aScaleIndicatorExportingConfigurations;
	}
	public boolean isIncludingTime() {
		return includingTime;
	}
	public void setIncludingTime(boolean aIncludingTime) {
		this.includingTime = aIncludingTime;
	}
	public boolean isIncludingPrice() {
		return includingPrice;
	}
	public void setIncludingPrice(boolean aIncludingPrice) {
		this.includingPrice = aIncludingPrice;
	}
	public boolean isReady() {
		boolean toExport = false;
		for (ScaleIndicatorExportingConfiguration element : scaleIndicatorExportingConfigurations) {
			toExport |= element.isToExport();
		}
		return fileName!=null && toExport;
	}
	@Override
	public String toString() {
		return "IndicatorExportingConfiguration [\nfileName=" + fileName
				+ ", \nincludingTime=" + includingTime + ", includingPrice="
				+ includingPrice + ", \nscaleIndicatorExportingConfigurations="
				+ Arrays.toString(scaleIndicatorExportingConfigurations) + "\n]";
	}
	public void fillRecord(IndicatorRecord indicatorRecord, IIndicator indicator) {
		if (includingTime)
			indicatorRecord.addEventData(""+indicator.getCurrentTime());
		if (includingPrice)
			indicatorRecord.addEventData(""+indicator.getCurrentPrice());
		for (ScaleIndicatorExportingConfiguration e : scaleIndicatorExportingConfigurations) {
			e.fillRecord(indicatorRecord, indicator);
		}
	}
	
	public boolean isPivotIncluded(int scale){
		for (ScaleIndicatorExportingConfiguration e : scaleIndicatorExportingConfigurations) {
			if (e.getScale()==scale)
				return (e.isIncluded() && e.isPivotIncluded());
		}
		return false;
	}
	public String getColumnNames() {
		String res = "";
		boolean isPivotIncluded = false;
		if (includingTime)
			res+=("Time, ");
		if (includingPrice)
			res+=("Price, ");
		for (ScaleIndicatorExportingConfiguration e : scaleIndicatorExportingConfigurations) {
			isPivotIncluded |= (e.isIncluded() && e.isPivotIncluded());
			res+=(e.getColumnNames());
		}
		if (isPivotIncluded){
			res += ("Pivot");
		}
		return res;
	}
	
}
