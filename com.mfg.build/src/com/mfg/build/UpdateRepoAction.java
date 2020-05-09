package com.mfg.build;

import static java.lang.System.out;

import java.io.File;

public class UpdateRepoAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public UpdateRepoAction() {
		super("Update Repository To Default");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		updateRepo(cfg);
	}

	@Override
	protected String[] getValidationFields() {
		return new String[] { Config.K_MERCURIAL_EXEC_PATH,
				Config.K_MFG_REPO_PATH };
	}

	public static boolean updateRepo(Config cfg) {
		try {
			String hg = cfg.get(Config.K_MERCURIAL_EXEC_PATH);
			File hgdir = new File(cfg.get(Config.K_MFG_REPO_PATH));

			String output = execProcessAndGetOutput(hgdir, hg, "status");
			if (output.trim().length() == 0) {
				out.println("This is connecting to internet, wait a minute...\n");
				int ev = execProcessAndRedirect(hgdir, hg, "pull", "-u",
						"http://arianfornaris:arianfornafdz@bitbucket.org/marketforecastgroup/mfgrc");
				if (ev == 0) {
					out.println("\nUpdate to default (mfgrc_default)\n");
					execProcessAndRedirect(hgdir, hg, "update", "-C",
							"mfgrc_default");
				} else {
					out.println("Error, exit value = " + ev);
					return false;
				}
			} else {
				out.println("Cannot execute the operation, there are un-commited changes:\n");
				out.println(output);
				return false;
			}
			return true;
		} catch (Exception e) {
			Utils.error(e);
			return false;
		}
	}
}
