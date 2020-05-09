package com.mfg.logger.ui.views;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class NextLogAction extends NavigatorAction {
	private final String ID = "com.mfg.logger.ui.views.NextLogAction";

	public NextLogAction(AbstractLoggerViewControl control) {
		super("Next");
		setId(ID);
		setControl(control);
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
	}

	@Override
	public void run() {
		getControl().gotoNext(getControl().getEvent());
	}

}
