package com.mfg.logger.ui.views;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class PrevLogAction extends NavigatorAction {
	private final String ID = "com.mfg.logger.ui.views.PrevLogAction";

	public PrevLogAction(AbstractLoggerViewControl control) {
		super("Previous");
		setId(ID);
		setControl(control);
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));

	}

	@Override
	public void run() {
		getControl().gotoPrevious(getControl().getEvent());
	}

}
