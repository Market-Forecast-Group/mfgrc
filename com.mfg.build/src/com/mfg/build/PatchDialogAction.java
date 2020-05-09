package com.mfg.build;

import static java.lang.System.out;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.mfg.build.DirPatch.Base;

public class PatchDialogAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public PatchDialogAction() {
		super("Generate \"MFGUpdate.exe\" Patch");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		try {

			Path productsDir = Config.getInstance().getDir()
					.resolve("products");
			Path lastProduct = null;
			for (File f : productsDir.toFile().listFiles()) {
				if (f.isDirectory() && f.getName().startsWith("MFG-")) {
					Path p = f.toPath();
					if (lastProduct == null
							|| Files.getLastModifiedTime(p).compareTo(
									Files.getLastModifiedTime(lastProduct)) > 0) {
						lastProduct = p;
					}
				}
			}
			if (lastProduct == null) {
				out.println("No products to patch");
				return;
			}

			Path dir1 = lastProduct;
			Path dir2 = Paths.get(
					Config.getInstance().get(Config.K_MFG_REPO_PATH)).resolve(
					"Setup/base/MFG");

			out.println("Copy to product some basic files...");
			Files.delete(dir1.resolve("MFG.exe"));
			Files.deleteIfExists(dir1.resolve("logo.ico"));
			Files.copy(dir2.resolve("MFG.exe"), dir1.resolve("MFG.exe"));
			Files.copy(dir2.resolve("logo.ico"), dir1.resolve("logo.ico"));

			out.println("Generate patch:");
			out.println("Old: " + dir1);
			out.println("New: " + dir2);

			Base base = DirPatch.buildBase(dir1);
			List<Path> patch = DirPatch.buildPatch(base, dir2, false);

			out.println("");
			out.println("NSIS Script:");
			out.println("");

			for (Path f : patch) {
				out.println("File " + f);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected String[] getValidationFields() {
		return new String[] { Config.K_MFG_REPO_PATH };
	}

}
