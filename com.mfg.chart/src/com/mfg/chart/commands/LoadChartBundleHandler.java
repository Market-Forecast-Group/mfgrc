package com.mfg.chart.commands;

import static java.lang.System.out;

import java.io.File;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.mfg.chart.ui.views.AbstractChartView;

public class LoadChartBundleHandler extends AbstractChartViewHanlder implements
		IElementUpdater {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Shell shell = HandlerUtil.getActiveShell(event);
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterExtensions(new String[] { "*.mfgchart" });

		String path = dlg.open();
		if (path != null) {
			File file = new File(path);
			if (!file.exists()) {
				MessageDialog.openError(shell, "Error", "File not found!");
				return null;
			}

			view.setContent(file);
			view.getChart().zoomOutAll(true);
		}

		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		out.println();
	}

}
