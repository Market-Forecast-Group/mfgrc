package com.mfg.logger.ui.views;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class CopyHandler extends AbstractHandler {

	public CopyHandler() {
		super();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final AbstractLogView view = (AbstractLogView) HandlerUtil
				.getActivePart(event);
		if (view != null) {
			System.out.println(Display.getCurrent().getFocusControl());
			new CopyFromLogAction(view.getControl()).run();
		}
		return null;
	}

}
