package com.mfg.build;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UpdateProductAction extends BuildAction {

	private static final long serialVersionUID = 1L;
	private boolean _showRunDialogs;
	protected Path _dfsReleaseDir;
	protected Path _mfgReleaseDir;
	protected Path _srcDir;

	public UpdateProductAction(boolean showRunDialogs) {
		super("Update MFG & DFS");
		_showRunDialogs = showRunDialogs;
	}

	public UpdateProductAction() {
		this(true);
	}

	@Override
	protected String[] getValidationFields() {
		return null;
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		buildProduct(cfg);
	}

	public void buildProduct(Config cfg) throws Exception, InterruptedException {
		boolean isWin = Config.isWindows();
		Path buildDir = cfg.getDir().resolve("build");
		_srcDir = buildDir.resolve("src");
		Path downloads = cfg.downloadsDir();
		Path products = cfg.getDir().resolve("products");

		Files.createDirectories(downloads);

		// Check for new source
		boolean newVersion = httpGet_newVersion_Bitbucket(downloads,
				"mfgrc_default.zip");
		if (!newVersion) {
			boolean doContinue = JOptionPane
					.showConfirmDialog(
							AppWindow.getInstance(),
							"There is not a new source version. Do you want to continue?",
							"Product Update", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
			if (!doContinue) {
				return;
			}

		}

		// Check for DFS
		boolean buildDFS = true;
		if (isDFSRunning()) {
			buildDFS = JOptionPane.showConfirmDialog(AppWindow.getInstance(),
					"DFS product is running, do you want to update it?",
					"Update DFS", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		}

		out.println("Build folder: " + buildDir);
		try {

			// CLEAN

			if (Files.exists(buildDir)) {
				out.println("\nCleaning build folder...");
				if (!deleteTree(buildDir)) {
					return;
				}
			}
			Files.createDirectories(_srcDir);

			DownloadSourceZipAction.downloadMFGSource(cfg);

			out.println("\nDownloading MFG dependencies...\n");
			DownloadMFGDepsAction.downloadMFGDeps(cfg);

			out.println();
			unzip(downloads.resolve("mfgrc_default.zip"), _srcDir, true);
			unzip(downloads.resolve(DownloadMFGDepsAction.getLibsFileName()),
					_srcDir, true);
			unzip(downloads.resolve("assets.zip"), _srcDir, true);

			// CUSTOM PRODUCT FILE
			String mfgProductFileRelPath = "com.mfg.application/MFGProduct.product";
			if (cfg.isDebugInfoAndAssertionsEnabled()) {
				Path path = _srcDir.resolve(mfgProductFileRelPath);
				String str = new String(Files.readAllBytes(path));
				str = str.replace("<vmArgs>", "<vmArgs>-ea ");
				Files.write(path, str.getBytes());
			}

			Path pluginsDir = buildDir.resolve("plugins");
			Path featuresDir = buildDir.resolve("features");
			Files.createDirectories(pluginsDir);
			Files.createDirectories(featuresDir);

			final List<Path> plugins = new ArrayList<>();
			final List<Path> features = new ArrayList<>();

			Files.walkFileTree(_srcDir, EnumSet.noneOf(FileVisitOption.class),
					1, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							if (Files.isDirectory(file)) {
								if (Files.exists(file.resolve("feature.xml"))) {
									features.add(file);
								} else if (Files.exists(file
										.resolve("build.properties"))) {
									plugins.add(file);
								}
							}
							return super.visitFile(file, attrs);
						}
					});

			// COPY

			out.println("\nCopying features...\n");
			for (Path p : features) {
				out.println(p.getFileName());
				copyTree(p, featuresDir.resolve(p.getFileName().toString()));
			}
			out.println("\nCopying plugins...\n");
			for (Path p : plugins) {
				out.println(p.getFileName());
				copyTree(p, pluginsDir.resolve(p.getFileName().toString()));
			}

			// INSTALL ECLIPSE
			out.println("\nStarting Eclipse setup...\n");
			InstallEclipseAction.installEclipse(cfg);
			Path eclipseExecPath = cfg.installationsDir().resolve(
					"eclipse/eclipse" + (Config.isWindows() ? ".exe" : ""));
			String eclipseInstall = normalizePath(eclipseExecPath.getParent());

			if (buildDFS) {
				// BUILD DFS
				out.println("\n\n**** Build DFS ****\n\n");
				_dfsReleaseDir = finalBuildStep(isWin, buildDir, products,
						eclipseExecPath, eclipseInstall,
						"com.marketforecastgroup.dfsa/DFSA.product", "DFS");
			}

			// BUILD MFG
			out.println("\n\n**** Build MFG ****\n\n");

			_mfgReleaseDir = finalBuildStep(isWin, buildDir, products,
					eclipseExecPath, eclipseInstall, mfgProductFileRelPath,
					"MFG");
		} catch (IOException e) {
			Utils.error(e);
		}
		AppWindow.getInstance().menuProducts();
	}

	private Path finalBuildStep(boolean isWin, Path buildDir, Path products,
			Path eclipseExecPath, String eclipseInstall, String productFile,
			final String productName) throws IOException, InterruptedException,
			Exception {
		Map<Object, Object> attrs = new HashMap<>();
		attrs.put("buildDirectory", normalizePath(buildDir));
		attrs.put("product", productFile);
		attrs.put("productName", productName);
		attrs.put("base-location-parent", normalizePath(eclipseExecPath
				.getParent().getParent()));
		attrs.put("base-location", eclipseInstall);
		attrs.put("baseos", isWin ? "win32" : "linux");
		attrs.put("basews", isWin ? "win32" : "gtk");
		String arch = Config.getArch();
		attrs.put("basearch", arch);
		attrs.put("configs", isWin ? "win32,win32," + arch
				: "linux, gtk, x86_64");
		boolean debugInfo = Config.getInstance()
				.isDebugInfoAndAssertionsEnabled();
		attrs.put("compilerArg", "-warn:none" + (debugInfo ? " -g" : "-g:none"));

		out.println("\nbuild.properties:\n");
		printProperties(attrs);

		String buildProps = readResource("/templates/headless-build.properties");

		buildProps = template(buildProps, attrs);
		Files.write(buildDir.resolve("build.properties"), buildProps.getBytes());

		// EXEC BUILD
		out.println("\n\nStarting product compilation, it takes a while...\n\n");
		String java = Paths.get(System.getProperty("java.home")).resolve("bin")
				.resolve("java").toAbsolutePath().toString();

		execProcessAndRedirect(
				buildDir.toFile(),
				java,
				"-jar",
				eclipseInstall
						+ "/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar",
				"-application",
				"org.eclipse.ant.core.antRunner",
				"-buildfile",
				eclipseInstall
						+ "/plugins/org.eclipse.pde.build_3.9.0.v20140415-2029/scripts/productBuild/productBuild.xml",
				"-Dbuilder=" + normalizePath(buildDir));

		Path tmpProductPath = buildDir.resolve("tmp").resolve(productName);
		Path dstProductPath = products.resolve(productName + "-"
				+ System.currentTimeMillis());
		if (Files.exists(tmpProductPath)) {
			if (Files.exists(dstProductPath)) {
				deleteTree(dstProductPath);
			}

			if (!Files.exists(products)) {
				Files.createDirectory(products);
			}

			out.println("\nCopying product to " + dstProductPath);
			Files.move(tmpProductPath, dstProductPath);
			final Path productExe = dstProductPath
					.resolve(productName + ".exe");
			Files.copy(eclipseExecPath, productExe);

			// out.println("Computing product checksums...");
			// checksumTree(dstProductPath);

			if (_showRunDialogs) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (JOptionPane.showConfirmDialog(
								AppWindow.getInstance(),
								"Do you want to launch " + productName + "?",
								"Launch " + productName,
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							out.println("\n\n\n\nLaunching " + productExe
									+ "...\n\n\n\n");

							try {
								launchProduct(productExe);
							} catch (Exception e) {
								Utils.error(e);
							}
						}
					}
				});
			}

		}

		out.println("Clean MFG-Current folder");
		String dirName = productName + "-Current";
		Path currentDir = products.resolve(dirName);
		if (Files.exists(currentDir)) {
			deleteTree(currentDir);
		}
		out.println("Copy product to " + productName + "-Current folder");
		copyTree(dstProductPath, products.resolve(dirName));

		return dstProductPath;
	}
}
