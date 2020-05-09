package com.mfg.build;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CheckNewSourceVersionAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public CheckNewSourceVersionAction() {
		super("Check New Source Version");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		check(cfg);
	}

	public static boolean check(Config cfg) throws IOException, Exception {
		out.println("\nChecking MFG source...\n");
		Path downloads = cfg.getDir().resolve("downloads");
		Files.createDirectories(downloads);

		return httpGet_newVersion_Bitbucket(downloads, "mfgrc_default.zip");
	}

	@Override
	protected String[] getValidationFields() {
		return null;
	}

}
