package com.mfg.plstats.ui.views;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.navigator.CommonNavigator;

import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.ProbabilitiesPerpective;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.Utils;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.ProbabilityElement;

public class ProbabilitiesNavigator2 extends CommonNavigator {
	@Override
	protected Object getInitialInput() {
		return PLStatsPlugin.getDefault().getCSVStorage();
	}

	@Override
	protected void initListeners(TreeViewer viewer) {
		super.initListeners(viewer);
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection sel = (StructuredSelection) event
						.getSelection();
				Object elem = sel.getFirstElement();
				if (elem != null) {
					try {

						if (elem instanceof ProbabilityElement) {
							openProbabilityElement(elem);
						} else {
							UIPlugin.openEditor(elem);
						}
					} catch (PartInitException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		});
	}

	protected static void openProbabilityElement(Object elem) {
		ProbabilityElement probabilityElement = (ProbabilityElement) elem;
		Utils.debug_var(999999, "1 load");
		probabilityElement.loadDistributionContainer(false);
		Utils.debug_var(999999, "2 set");
		WidgetPlugin
				.getDefault()
				.getProbabilitiesManager()
				.setDistributionsContainer(
						probabilityElement.getDistributionsContainer());
		Utils.debug_var(999999, "3 show");
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		window.getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				// open the perspective
				try {
					workbench.showPerspective(ProbabilitiesPerpective.ID,
							window);
				} catch (WorkbenchException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
