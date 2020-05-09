package com.mfg.build;

import static java.lang.System.out;

public class ListRunningJVMAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public ListRunningJVMAction() {
		super("List Running JVM");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		if (isDFSRunning()) {
			out.println("\nDFS product is running");
		} else {
			out.println("\nDFS product is not running");
		}
	}

	@Override
	protected String[] getValidationFields() {
		return null;
	}

}
