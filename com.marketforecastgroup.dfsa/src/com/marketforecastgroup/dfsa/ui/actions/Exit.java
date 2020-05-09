package com.marketforecastgroup.dfsa.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class Exit extends AbstractHandler
{

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        HandlerUtil.getActiveWorkbenchWindow(event).close();
        return null;
    }

}
