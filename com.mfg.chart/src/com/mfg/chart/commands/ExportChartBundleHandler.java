package com.mfg.chart.commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.ui.IChartUtils;
import com.mfg.chart.ui.views.AbstractChartView;

public class ExportChartBundleHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		IChartModel model = chart.getModel();
		Shell shell = HandlerUtil.getActiveShell(event);
		if (model instanceof ChartModel_MDB) {
			FileDialog dlg = new FileDialog(shell, SWT.SAVE);
			DateFormat fmt = new SimpleDateFormat("dd-mm-yyy_HH_MM");
			String fname = "ChartData-" + fmt.format(new Date()) + ".mfgchart";
			dlg.setFileName(fname);
			dlg.setFilterExtensions(new String[] { "*.mfgchart" });

			String dest = dlg.open();
			if (dest != null) {
				ChartModel_MDB mdbModel = (ChartModel_MDB) model;
				File zipFile = new File(dest.toLowerCase()
						.endsWith(".mfgchart") ? dest : dest + ".mfgchart");

				try {
					mdbModel.getPriceSession().flush();
					if (mdbModel.getIndicatorSession() != null) {
						mdbModel.getIndicatorSession().flush();
					}
					if (mdbModel.getTradingSession() != null) {
						mdbModel.getTradingSession().flush();
					}

					if (zipFile.exists()) {
						if (!MessageDialog.openConfirm(shell, "Export Data",
								"Do you want to replace the file:\n" + dest
										+ " ...?")) {
							return null;
						}
					}

					IChartUtils.zipChartData(mdbModel, chart.getType(), zipFile);
					MessageDialog.openInformation(shell, "Export Data",
							"Data exported with success!");
				} catch (IOException e) {
					e.printStackTrace();
					MessageDialog.openError(shell, "Error", e.getMessage());
				}
			}
		} else {
			MessageDialog.openError(shell, "Error",
					"There is not data in chart");
		}

		return null;
	}

}
