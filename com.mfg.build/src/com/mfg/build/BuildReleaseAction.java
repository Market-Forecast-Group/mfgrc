package com.mfg.build;

import static java.lang.System.out;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class BuildReleaseAction extends UpdateProductAction {

	private static final long serialVersionUID = 1L;
	private boolean _buildSetup;
	private boolean _buildUpdate;
	private JFileChooser _chooser;

	public BuildReleaseAction(boolean publishAll) {
		super(false);
		_buildSetup = publishAll;
		_buildUpdate = true;
		putValue(NAME, publishAll ? "Publish Setup (MFGSetup.exe & MFGUpdate.exe)"
				: "Publish Update (MFGUpdate.exe)");
	}

	@Override
	protected void execute(Config cfg) throws Exception {
		super.execute(cfg);

		out.println();
		out.println("************* ");
		out.println();
		out.println("Start building installers");
		out.println();

		{
			// MFGSetup.exe

			out.println("MFGSetup.exe");
			out.println();

			// nsis
			ftpGet_MFG("nsis.zip");
			unzip(cfg.downloadsDir().resolve("nsis.zip"),
					cfg.installationsDir(), false);

			// copy MFG
			out.println("Copying " + _mfgReleaseDir + " files...");

			Path setup_exe_dir = _srcDir.resolve("Setup/setup_exe");
			Path setup_exe_mfgDir = setup_exe_dir.resolve("MFG");
			Files.createDirectories(setup_exe_mfgDir);
			copyTree(_mfgReleaseDir, setup_exe_mfgDir);

			// jre

			ftpGet_MFG("jre.zip");
			unzip(cfg.downloadsDir().resolve("jre.zip"), setup_exe_dir, false);

			// setup resources:
			// MFG.exe
			// logo.ci
			Files.delete(setup_exe_mfgDir.resolve("MFG.exe"));
			if (ftpGet_MFG("setup-resources.zip")) {
				Path dst = cfg.installationsDir().resolve("setup-resources");
				deleteTree(dst);
				unzip(cfg.downloadsDir().resolve("setup-resources.zip"), dst,
						false);
			}

			Files.copy(cfg.installationsDir()
					.resolve("setup-resources/MFG.exe"), setup_exe_mfgDir
					.resolve("MFG.exe"));
			Files.copy(
					cfg.installationsDir().resolve("setup-resources/logo.ico"),
					setup_exe_mfgDir.resolve("logo.ico"));

			String nsis_exe = cfg.installationsDir()
					.resolve("nsis/makensis.exe").toAbsolutePath().toString();

			// MFGSetup.exe

			boolean upload = JOptionPane
					.showConfirmDialog(
							AppWindow.getInstance(),
							"Do you want to upload the setups files? Press NO if you only want to copy them.",
							"Upload setup files", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION;
			Path copyDirDst = null;
			if (!upload) {
				if (_chooser == null) {
					_chooser = new JFileChooser();
					_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					_chooser.setDialogTitle("Open Save-To Directory");
				}
				_chooser.showOpenDialog(AppWindow.getInstance());
				File file = _chooser.getSelectedFile();
				if (file != null) {
					copyDirDst = file.toPath();
					Files.createDirectories(copyDirDst);
				}
			}

			if (_buildSetup) {
				int exitCode = execProcessAndRedirect(setup_exe_dir.toFile(),
						nsis_exe, "/V4", "setup.nsi");
				if (exitCode == 0) {
					Path setup_exe = setup_exe_dir.resolve("MFGSetup.exe");
					if (copyDirDst == null) {
						out.println("\n\nUploading MFGSetup.exe...\n\n");
						ftpDelete_MFG("Setup/MFGSetup.exe");
						ftpUpload_MFG("Setup", setup_exe);
					} else {
						out.println("\n\nCopying MFGSetup.exe...\n\n");
						Path dst = copyDirDst.resolve("MFGSetup.exe");
						Files.copy(setup_exe, dst,
								StandardCopyOption.REPLACE_EXISTING);
					}
				} else {
					out.println("Error: exit code " + exitCode);
				}
			}

			// MFGUpdate.exe
			if (_buildUpdate) {
				StringBuilder sb = new StringBuilder();

				sb.append("  SetOutPath \"$INSTDIR\\MFG\\plugins\" \n");

				Files.walk(_mfgReleaseDir.resolve("plugins"))
						.filter(BuildAction::isMfgPluginName)
						.forEach(
								p -> {
									sb.append("  File /r MFG\\plugins\\"
											+ p.getFileName() + "\n");
								});

				sb.append("  SetOutPath \"$INSTDIR\\MFG\\features\" \n");

				Files.walk(_mfgReleaseDir.resolve("features"))
						.filter(BuildAction::isMfgPluginName)
						.forEach(
								p -> {
									sb.append("  File /r MFG\\features\\"
											+ p.getFileName() + "\n");
								});
				Path update_nsi = setup_exe_dir.resolve("update.nsi");
				String content = new String(Files.readAllBytes(update_nsi));
				content = content.replace("; Install new files here", sb);
				Files.write(update_nsi, content.getBytes());

				out.println("MFGUpdate.exe script");

				out.println(content);

				int exitCode = execProcessAndRedirect(setup_exe_dir.toFile(),
						nsis_exe, "/V4", "update.nsi");
				if (exitCode == 0) {
					Path update_exe = setup_exe_dir.resolve("MFGUpdate.exe");
					if (copyDirDst == null) {
						out.println("\n\nUploading MFGUpdate.exe...\n\n");
						ftpDelete_MFG("Setup/MFGUpdate.exe");
						ftpUpload_MFG("Setup", update_exe);
					} else {
						out.println("\n\nCopying MFGUpdate.exe...\n\n");
						Path dst = copyDirDst.resolve("MFGUpdate.exe");
						Files.copy(update_exe, dst,
								StandardCopyOption.REPLACE_EXISTING);
					}
				} else {
					out.println("Error: exit code " + exitCode);
				}

				if (copyDirDst != null) {
					Desktop.getDesktop().open(copyDirDst.toFile());
				}
			}
		}
	}
}
