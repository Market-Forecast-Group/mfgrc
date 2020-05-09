package com.mfg.utils.ui.table;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class CopyFromMFGModelTableHandler extends AbstractHandler {

	public CopyFromMFGModelTableHandler() {
		super();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IMFGModelTableContainer view = (IMFGModelTableContainer) HandlerUtil
				.getActivePart(event);
		if (view != null) {
			System.out.println(Display.getCurrent().getFocusControl());
			new CopyFromMFGModelTableAction(view.getMFGModelTable()).run();
		}
		return null;
	}

}
