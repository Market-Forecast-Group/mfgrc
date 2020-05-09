package com.mfg.build;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class InstallEclipseAction extends BuildAction {

	private static final long serialVersionUID = 1L;

	public InstallEclipseAction() {
		super("Update Eclipse & Dependencies");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		installEclipse(cfg);
	}

	public static void installEclipse(Config cfg) throws Exception {
		String os = Config.isWindows() ? "win" : "linux";
		String arch = Config.isArch64() ? "64" : "32";
		String eclipse_zip = "eclipse-" + os + "-" + arch + ".zip";

		ftpInstall(eclipse_zip, "eclipse", true);
		// to migrate to the new layout
		// delete all the eclipse/dropins/*.jar
		SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				if (file.toString().endsWith(".jar")) {
					out.println("Deleting " + file);
					Files.delete(file);
				}
				return super.visitFile(file, attrs);
			}
		};
		Path dropinsPath = cfg.installationsDir().resolve("eclipse/dropins");
		if (!Files.exists(dropinsPath)) {
			Files.createDirectories(dropinsPath);
		}
		Files.walkFileTree(dropinsPath, EnumSet.noneOf(FileVisitOption.class),
				1, visitor);
		ftpInstall("dropins.zip", "eclipse/dropins/mdb/eclipse/plugins", true);
		ftpInstall("dropins-nebula.zip",
				"eclipse/dropins/nebula/eclipse/plugins", false);
		setExecutable(cfg.installationsDir().resolve("eclipse/eclipse"));
	}

	@Override
	protected String[] getValidationFields() {
		return null;
	}

}
