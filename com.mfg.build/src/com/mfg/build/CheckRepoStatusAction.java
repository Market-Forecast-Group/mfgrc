package com.mfg.build;

import static java.lang.System.out;

import java.io.File;

public class CheckRepoStatusAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public CheckRepoStatusAction() {
		super("Check Repository Status");
	}

	@Override
	protected String[] getValidationFields() {
		return new String[] { Config.K_MERCURIAL_EXEC_PATH,
				Config.K_MFG_REPO_PATH };
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		String hg = cfg.get(Config.K_MERCURIAL_EXEC_PATH);
		File curDir = new File(cfg.get(Config.K_MFG_REPO_PATH));

		out.println("Local changset:\n");

		execProcessAndRedirect(curDir, hg, "log", "-l", "1");

		out.println("\nIncoming changes:\n");
		out.println("It is connecting to Internet, wait a minute...\n");

		execProcessAndRedirect(curDir, hg, "incoming", "-l", "10", "-n",
				"http://arianfornaris:arianfornafdz@bitbucket.org/marketforecastgroup/mfgrc/");

		out.println("\nLocal modifications:\n");
		String output = execProcessAndGetOutput(curDir, hg, "status");
		if (output.trim().length() == 0) {
			out.println("No files modified");
		} else {
			out.println(output);
		}
		String branch = execProcessAndGetOutput(curDir, hg, "branch");
		out.println("\nLocal branch: " + branch);
	}
}
