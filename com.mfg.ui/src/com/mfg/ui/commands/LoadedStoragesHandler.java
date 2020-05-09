package com.mfg.ui.commands;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;

public class LoadedStoragesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Set<SimpleStorage<?>> storages = PersistInterfacesPlugin.getDefault()
				.getLoadedStorages();
		ListDialog dlg = new ListDialog(HandlerUtil.getActiveShell(event));
		dlg.setContentProvider(new ArrayContentProvider());
		dlg.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.getClass().getSimpleName();
			}
		});
		dlg.setInput(storages);
		dlg.setMessage("Loaded Storages");
		dlg.setTitle("Storages");
		dlg.open();
		return null;
	}

}
