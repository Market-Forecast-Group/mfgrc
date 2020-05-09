package com.mfg.build;

public class DownloadMFGDepsAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public DownloadMFGDepsAction() {
		super("Download MFG dependencies");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		downloadMFGDeps(cfg);
	}

	/**
	 * @param cfg
	 */
	public static void downloadMFGDeps(Config cfg) throws Exception {
		String libsName = getLibsFileName();
		ftpGet_MFG(libsName);
		
		ftpGet_MFG("assets.zip");
	}

	public static String getLibsFileName() {
		return "mfgrc-libs-" + (Config.isArch64() ? "64" : "32") + ".zip";
	}

	@Override
	protected String[] getValidationFields() {
		return null;
	}
}
