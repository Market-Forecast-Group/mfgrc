package com.marketforecastgroup.dfsa.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.marketforecastgroup.dfsa.ui.views.MaturitiesView;
import com.mfg.common.DfsSymbol;
import com.mfg.utils.PartUtils;

public class MaturitiesDetailsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		MaturitiesView view = PartUtils.openView(MaturitiesView.ID);
		if (view != null && sel.getFirstElement() instanceof DfsSymbol) {
			view.setSymbol((DfsSymbol) sel.getFirstElement());
		}
		return null;
	}

}
