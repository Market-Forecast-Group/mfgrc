package com.mfg.plstats;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.mfg.plstats.charts.IndicatorChartView;
import com.mfg.plstats.ui.editors.ComparisonView;
import com.mfg.plstats.ui.editors.PatternsModelView;
import com.mfg.plstats.ui.editors.ProbabilitiesArrayView;
import com.mfg.plstats.ui.editors.ProbabilitiesLogFilterView;
import com.mfg.plstats.ui.editors.ProbabilitiesSetView;
import com.mfg.plstats.ui.editors.SCTProbabilitiesView;
import com.mfg.plstats.ui.editors.T1ComputerView;
import com.mfg.plstats.ui.editors.TargetsDistributionView;
import com.mfg.widget.ui.IndicatorSettingsView;
import com.mfg.widget.ui.ProbabilitiesSettingsView;

@XmlTransient
public class ProbabilitiesPerpective implements IPerspectiveFactory {

	public static String ID = "com.mfg.plstats.ProbabilitiesPerpective";

	private static final String ID_TABS_FOLDER = "0";
	private static final String ID_TABS_FOLDER1 = "1";
	private static final String ID_TABS_FOLDER2 = "2";

	private static final String ID_TABS_FOLDER3 = "3";

	/**
	 * Creates the initial layout for a page.
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
	}

	/**
	 * Add fast views to the perspective.
	 */
	private static void addFastViews(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		IFolderLayout tabs = layout.createFolder(ID_TABS_FOLDER,
				IPageLayout.RIGHT, 0.6f, editorArea);
		tabs.addView(ProbabilitiesSettingsView.ID);
		tabs.addView(ProbabilitiesSetView.ID);
		tabs.addView(T1ComputerView.ID);
		tabs.addView(ProbabilitiesLogFilterView.ID);
		tabs.addView(ComparisonView.ID);
		tabs = layout.createFolder(ID_TABS_FOLDER1, IPageLayout.BOTTOM, 0.6f,
				ID_TABS_FOLDER);
		tabs.addView(PatternsModelView.ID);
		tabs.addView(ProbabilitiesArrayView.ID);
		tabs = layout.createFolder(ID_TABS_FOLDER2, IPageLayout.TOP, 0.55f,
				editorArea);
		tabs.addView(TargetsDistributionView.ID);
		tabs.addPlaceholder(IndicatorChartView.VIEW_ID);
		tabs.addView(SCTProbabilitiesView.ID);
		tabs.addView(IndicatorSettingsView.ID);
		tabs = layout.createFolder(ID_TABS_FOLDER3, IPageLayout.BOTTOM, 0.55f,
				editorArea);
		tabs.addView(ProbabilitiesCalcLogView.ID);
	}

	/**
	 * Add view shortcuts to the perspective.
	 * @param layout Used in inner classes.
	 */
	private void addViewShortcuts(IPageLayout layout) {
		//Adding some comment to avoid empty block warning.
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 * @param layout Used in inner classes.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
		//Adding some comment to avoid empty block warning.
	}

}
