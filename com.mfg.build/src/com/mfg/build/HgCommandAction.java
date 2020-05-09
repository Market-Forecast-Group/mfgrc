package com.mfg.build;

import static java.lang.System.out;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class HgCommandAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public HgCommandAction() {
		super("Execute Hg Command...");
	}

	@Override
	protected String[] getValidationFields() {
		return new String[] { Config.K_MERCURIAL_EXEC_PATH,
				Config.K_MFG_REPO_PATH };
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		String result = JOptionPane.showInputDialog(AppWindow.getInstance(),
				"Hg command arguments (like 'branches')", "Hg Command",
				JOptionPane.QUESTION_MESSAGE);
		if (result != null) {
			String hg = cfg.get(Config.K_MERCURIAL_EXEC_PATH);
			List<String> args = new ArrayList<>();
			args.add(hg);
			args.addAll(Arrays.asList(result.split(" ")));
			int ev = execProcessAndRedirect(
					new File(cfg.get(Config.K_MFG_REPO_PATH)),
					args.toArray(new String[args.size()]));
			if (ev != 0) {
				out.println("Error: exit value " + ev);
			}
		}
	}
}
