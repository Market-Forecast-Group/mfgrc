package com.marketforescastgroup.logger.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

@Deprecated
public class LogViewCommandState extends AbstractSourceProvider {
	public final static String MY_STATE = "com.marketforecastgroup.logger.logviewactive";
	public final static String ENABLED = "ENABLED";
	public final static String DISABLED = "DISABLED";
	private boolean enabled = true;

	@Override
	public void dispose() {
		//Adding a comment to avoid empty block warning.
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