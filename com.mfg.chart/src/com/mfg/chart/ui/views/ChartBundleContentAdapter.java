package com.mfg.chart.ui.views;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.IChartUtils;

public class ChartBundleContentAdapter extends ChartContentAdapter {

	private final File _zipFile;

	public ChartBundleContentAdapter(File zipFile) {
		super(zipFile.getName(), ChartType.FINANCIAL, false);
		_zipFile = zipFile;
	}

	@Override
	protected Chart createChart(String chartName,
			IChartModel model, ChartType type, Object content) {
		File tmpDir;
		try {
			tmpDir = File.createTempFile("tmp-mfg-chart",
					UUID.randomUUID().toString()).getParentFile();
			Chart chart = IChartUtils.createChart_fromZipFile(
					tmpDir, _zipFile, isPhysicalTimeChart());
			setType(chart.getType());
			return chart;
		} catch (IOException e) {
			e.printStackTrace();
			return super.createChart(chartName, model, type, content);
		}
	}

	@Override
	public void dispose(IChartView chartView) {
		super.dispose(chartView);
		ChartModel_MDB model = (ChartModel_MDB) _chart.getModel();
		try {
			model.getPriceSession().closeAndDelete();
			if (model.getIndicatorSession() != null) {
				model.getIndicatorSession().closeAndDelete();
			}
			if (model.getTradingSession() != null) {
				model.getTradingSession().closeAndDelete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

}
