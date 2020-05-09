package com.mfg.build;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class OpenSettingsAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public OpenSettingsAction() {
		super("Settings");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ConfigDialog.open(new String[] { Config.K_MFG_REPO_PATH,
				Config.K_MERCURIAL_EXEC_PATH,
				Config.K_DEBUG_INFO_AND_ASSERTIONS });
	}

}
