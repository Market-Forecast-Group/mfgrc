package com.marketforecastgroup.dfsa.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		// layout.addView(DFSAview.ID, IPageLayout.LEFT,
		// IPageLayout.DEFAULT_VIEW_RATIO, IPageLayout.ID_EDITOR_AREA);
		// layout.getViewLayout(DFSAview.ID).setCloseable(
		// false);
		// layout.getViewLayout(DFSAview.ID)
		// .setMoveable(false);
		// layout.setFixed(true);
	}

}
