package com.mfg.build;

import static java.lang.System.out;

import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadSourceZipAction extends BuildAction {

	private static final int TRY_N_TIMES = 12;
	private static final long serialVersionUID = 1L;

	public DownloadSourceZipAction() {
		super("Download Source ZIP");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		downloadMFGSource(cfg);
	}

	public static void downloadMFGSource(Config cfg) throws Exception {
		out.println("\nDownloading MFG source...\n");
		Path downloads = cfg.getDir().resolve("downloads");
		Files.createDirectories(downloads);

		for (int i = 0; i < TRY_N_TIMES; i++) {
			try {
				httpGet_Bitbucket(downloads, "mfgrc_default.zip");
				break;
			} catch (Exception e) {
				out.println("Error: " + e.getMessage());
				out.println("Trying again (" + (i + 1) + "/" + TRY_N_TIMES
						+ ")...");
			}
		}
	}

	@Override
	protected String[] getValidationFields() {
		return null;
	}

}
