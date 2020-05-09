package com.mfg.strategy.automatic.exportIndicator;

import java.util.ArrayList;
import java.util.List;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;

public class ScaleIndicatorExportingConfiguration {
	private int scale;
	private boolean included;
	private boolean pivotIncluded;
	private List<IndicatorParameter> parameters;

	public ScaleIndicatorExportingConfiguration() {
		super();
		parameters = new ArrayList<>();
	}
	
	public static ScaleIndicatorExportingConfiguration buildDefault(){
		return buildDefault(0);
	}

	public static ScaleIndicatorExportingConfiguration buildDefault(int scale){
		ScaleIndicatorExportingConfiguration res = new ScaleIndicatorExportingConfiguration();
		res.scale = scale;
		res.parameters.add(new IndicatorParameter("SC Touch","SC Touch") {
			@Override
			public String export(IIndicator indicator, int aScale) {
				return booleanToStr(indicator.isThereANewSC(aScale));
			}
		});
		res.parameters.add(new IndicatorParameter("RC Touch","RC Touch") {
			@Override
			public String export(IIndicator indicator, int aScale) {
				return booleanToStr(indicator.isThereANewRC(aScale));
			}
		});
		res.parameters.add(new IndicatorParameter("TH","TH") {
			@Override
			public String export(IIndicator indicator, int aScale) {
				return booleanToStr(indicator.isThereANewPivot(aScale));
			}
		});
		res.parameters.add(new IndicatorParameter("SwDir","Swing Direction") {
			@Override
			public String export(IIndicator indicator, int aScale) {
				return booleanToStr(!indicator.isSwingDown(aScale));
			}
		});
		res.parameters.add(new IndicatorParameter("SwLen","Swing Length") {
			@Override
			public String export(IIndicator indicator, int aScale) {
				Pivot lastPivot = indicator.getLastPivot(0, aScale);
				if (lastPivot!=null){
					double p = lastPivot.getPivotPrice();
					double hhll = (indicator.isSwingDown(aScale))
							?indicator.getLLPrice(aScale)
									:indicator.getHHPrice(aScale);
					return ""+(Math.abs(p-hhll));
				}
				return "-1";
			}
		});
		return res;
	}

	static String booleanToStr(boolean value) {
		return (value?"1":"0");
	}

	public int getScale() {
		return scale;
	}


	public void setScale(int aScale) {
		this.scale = aScale;
	}


	public boolean isIncluded() {
		return included;
	}


	public void setIncluded(boolean aIncluded) {
		this.included = aIncluded;
	}


	public List<IndicatorParameter> getParameters() {
		return parameters;
	}


	public void setParameters(List<IndicatorParameter> aParameters) {
		this.parameters = aParameters;
	}
	
	public boolean isPivotIncluded() {
		return pivotIncluded;
	}

	public void setPivotIncluded(boolean aPivotIncluded) {
		this.pivotIncluded = aPivotIncluded;
	}

	public boolean isToExport() {
		if (!included)
			return false;
		if (pivotIncluded)
			return true;
		for (IndicatorParameter element : parameters) {
			if (element.isIncluded())
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "ScaleConfiguration [scale=" + scale
				+ ", included=" + included + ", PivotIncluded=" + pivotIncluded 
				+ ", parameters=" + parameters + "]";
	}

	public void fillRecord(IndicatorRecord indicatorRecord, IIndicator indicator) {
		if (included){
			for (IndicatorParameter element : parameters) {
				if (element.isIncluded())
					indicatorRecord.addEventData(element.export(indicator, scale));
			}
		}
	}

	public String getColumnNames() {
		if (included){
			String res = "";
			for (IndicatorParameter element : parameters) {
				if (element.isIncluded())
					res+=(element.getShortName()+"("+getScale()+"), ");
			}
			return res;
		} 
		return "";
	}
	

}
