package com.marketforecastgroup.dfsa.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class DisconnectCommandState extends AbstractSourceProvider {
	public final static String MY_STATE = "com.marketforecastgroup.dfsa.ui.commands.disconnectactive";
	public final static String ENABLED = "ENABLED";
	public final static String DISABLED = "DISABLED";
	private boolean enabled = false;

	@Override
	public void dispose() {
		//Adding a comment to avoid empty block warning
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { MY_STATE };
	}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> map = new HashMap<>(1);
		String value = enabled ? ENABLED : DISABLED;
		map.put(MY_STATE, value);
		return map;
	}

	// This method can be used from other commands to change the state
	public void toogleEnabled() {
		enabled = !enabled;
		String value = enabled ? ENABLED : DISABLED;
		fireSourceChanged(ISources.WORKBENCH, MY_STATE, value);
	}
}
