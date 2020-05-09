package com.mfg.install;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

public class Main {
	{
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static void main(String[] args) {
		Main main = new Main();
		try {
			main.initUI();
			main.update();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		main._win.setVisible(false);
		System.exit(0);
	}

	private FTPClient _ftp;
	Path _installPath;
	Path _localHashesPath;
	private String _ftpBuildsPath;
	private Path _versionPath;
	private JProgressBar _progressBar;
	JLabel _label;
	JFrame _win;
	Path _mfgIconPath;

	private void initUI() throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// nothing
		}

		_win = new JFrame("MFG Installer");
		_win.setIconImage(ImageIO.read(getClass().getResourceAsStream(
				"64x64_32bits.gif")));
		_win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		_win.setSize(400, 200);
		Dimension screenSize = _win.getToolkit().getScreenSize();
		_win.setLocation(screenSize.width / 2 - _win.getWidth() / 2,
				screenSize.height / 2 - _win.getHeight() / 2);

		final BufferedImage img = ImageIO.read(getClass().getResourceAsStream(
				"mfg.png"));

		JPanel mainPanel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(img, getWidth() / 2 - img.getWidth() / 2,
						getHeight() / 2 - img.getHeight() / 2 - 20, null);
			}
		};
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		mainPanel.setOpaque(true);
		mainPanel.setBackground(Color.white);
		_win.getContentPane().add(mainPanel);

		_progressBar = new JProgressBar();
		_progressBar.setVisible(false);
		mainPanel.add(_progressBar, BorderLayout.SOUTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		_label = new JLabel();
		centerPanel.add(_label, BorderLayout.SOUTH);

		_win.setVisible(true);
		JOptionPane.showMessageDialog(null,
				"Please, close any instance of MFG before to continue.",
				"MFG Installer", JOptionPane.WARNING_MESSAGE);
	}

	public void update() throws Exception {
		String home = System.getProperty("user.home");

		_installPath = Paths.get(home, "MFG System");
		_localHashesPath = _installPath.resolve("tree-checksums.properties");
		_versionPath = _installPath.resolve("version");
		_mfgIconPath = _installPath.resolve("MFG.ico");

		String ftpName = "ftp.marketforecastgroup.com";
		String ftpUser = "mfg";
		String ftpPwd = "positiveMfg99_";
		_ftpBuildsPath = "/MFGBuild/Builds";

		if (!Files.exists(_installPath)) {
			Files.createDirectories(_installPath);
		}

		Properties localHashes = new Properties();
		if (Files.exists(_localHashesPath)) {
			localHashes.load(Files.newInputStream(_localHashesPath));
		}

		_ftp = new FTPClient();
		_ftp.setDefaultTimeout((int) TimeUnit.SECONDS.toMillis(10));
		try {
			log("Connecting to " + ftpName + "... ");
			_ftp.connect(ftpName);
			log("done!");

			log("Login... ");
			if (_ftp.login(ftpUser, ftpPwd)) {
				log("done!");
				_ftp.setFileType(FTP.BINARY_FILE_TYPE);
				_ftp.enterLocalPassiveMode();

				download();
				install();
			} else {
				log("Login error.");
			}
			_ftp.disconnect();
		} finally {
			if (_ftp.isConnected()) {
				_ftp.disconnect();
			}
		}
	}

	private void install() throws Exception {
		if (!System.getProperty("os.name").contains("indows")) {
			return;
		}

		Preferences node = Preferences.userRoot().node("com.mfg.install");
		String installDir = node.get("installDir", null);
		if (installDir == null) {
			// is the first time, install it!
			try {
				executeCreateLinkScript();
				node.put("installDir", _installPath.toAbsolutePath().toString());
				node.sync();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(
						null,
						"<html>Cannot create Desktop shortuct:<br><b>"
								+ e.getMessage() + "</b></html>",
						"MFG Installer", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void executeCreateLinkScript() throws IOException {
		log("Creating Desktop shortcut...");
		if (!Files.exists(_mfgIconPath)) {
			try (OutputStream os = Files.newOutputStream(_mfgIconPath);
					InputStream is = getClass().getResourceAsStream(
							"64x64_32bits.ico");) {
				int b;
				while ((b = is.read()) != -1) {
					os.write(b);
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				Main.class.getResourceAsStream("link.vbs")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		String target = System.getProperty("user.home")
				+ "\\Desktop\\MFG System.lnk";
		CharSequence source = _installPath.resolve("MFG.exe").toAbsolutePath()
				.toFile().toString();
		String script = sb.toString().replace("$target$", target)
				.replace("$source$", source)
				.replace("$icon$", _mfgIconPath.toAbsolutePath().toString());
		Path file = Files.createTempFile("MfgCreateLnk", ".vbs");

		out.println("Execute:\n" + script);

		Files.write(file, script.getBytes());
		Runtime.getRuntime().exec(
				"wscript.exe " + file.toAbsolutePath().toFile().toString());
	}

	private void download() throws IOException {
		boolean newVer = true;
		log("List builds:");
		FTPFile[] list = _ftp.listFiles(_ftpBuildsPath);

		int lastVer = -1;
		FTPFile lastVerFile = null;

		for (FTPFile dir : list) {
			int ver = Integer.parseInt(dir.getName().substring(1));
			log("v " + ver);
			if (ver > lastVer) {
				lastVer = ver;
				lastVerFile = dir;
			}
		}

		if (lastVerFile == null) {
			log("There is not a new version");
			newVer = false;
		} else {
			log("Select version " + lastVer);
			if (Files.exists(_versionPath)) {
				String str = new String(Files.readAllBytes(_versionPath));
				int ver = Integer.parseInt(str.trim());
				if (ver == lastVer) {
					log("There is not a new version");
					newVer = false;
				}
			}

			if (newVer) {
				String ftpVerDir = _ftpBuildsPath + "/" + lastVerFile.getName();
				byte[] buf;
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					String fname = ftpVerDir + "/tree-checksums.properties";
					downloadFile(fname, os);
					buf = os.toByteArray();
				}
				final Properties ftpHashes = new Properties();
				try (ByteArrayInputStream is = new ByteArrayInputStream(buf)) {
					ftpHashes.load(is);
				}

				_progressBar.setVisible(true);
				_progressBar.setMinimum(0);
				_progressBar.setMaximum(ftpHashes.size());

				Properties localHashes = new Properties();
				if (Files.exists(_localHashesPath)) {
					localHashes.load(Files.newInputStream(_localHashesPath));
				}

				log("Download files...");
				boolean changed = false;
				for (Entry<Object, Object> entry : ftpHashes.entrySet()) {
					String fname = (String) entry.getKey();
					String hash1 = (String) entry.getValue();
					boolean get = true;
					if (localHashes.containsKey(fname)) {
						String hash2 = localHashes.getProperty(fname);
						if (hash1.equals(hash2)) {
							log("Skip " + fname);
							get = false;
						} else {
							log("There is a new version of " + fname);
						}
					}
					if (get) {
						changed = true;
						log(fname);
						Path localPath = _installPath.resolve(fname);
						if (Files.exists(localPath)) {
							Files.delete(localPath);
							log("Deleted " + fname);
						}

						Files.createDirectories(localPath.getParent());
						downloadFile(ftpVerDir + "/" + fname,
								Files.newOutputStream(localPath));
					}
					_progressBar.setValue(_progressBar.getValue() + 1);
				}

				if (changed) {
					log("Deleting remaining files...");
					Files.walkFileTree(_installPath,
							new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult visitFile(Path file,
										BasicFileAttributes attrs)
										throws IOException {
									String fname = _installPath
											.relativize(file).toString()
											.replace("\\", "/");
									if (!file.equals(_localHashesPath)
											&& !file.equals(_mfgIconPath)
											&& !ftpHashes.containsKey(fname)) {
										log("Delete file " + fname);
										Files.delete(file);
									}
									return FileVisitResult.CONTINUE;
								}

								@Override
								public FileVisitResult postVisitDirectory(
										Path dir, IOException exc)
										throws IOException {
									String[] files = dir.toFile().list();
									if (files == null || files.length == 0) {
										log("Delete empty dir " + dir);
										Files.delete(dir);
									}
									return FileVisitResult.CONTINUE;
								}
							});

					try (OutputStream os = Files
							.newOutputStream(_localHashesPath)) {
						ftpHashes.store(os, "");
					}
					log("New hashes saved.");

					Files.write(_versionPath, Integer.toString(lastVer)
							.getBytes());
					log("New version saved.");
				} else {
					log("No changes detected");
				}
			}
		}
		_win.setVisible(false);
		if (newVer) {
			log("Update finished!");
			JOptionPane
					.showMessageDialog(null, "Update finished with success!");
		} else {
			JOptionPane.showMessageDialog(null, "There is not a new version.");
		}
	}

	@SuppressWarnings("null")
	private void downloadFile(final String ftpFile, OutputStream os)
			throws IOException {
		FTPFile[] files = null;
		boolean tryAgain = false;
		do {
			try {
				files = _ftp.listFiles(ftpFile);
			} catch (ConnectException e) {
				e.printStackTrace();
				tryAgain = JOptionPane.showConfirmDialog(null,
						"Connection problem: " + e.getMessage()
								+ ". Do you want to try again?", "FTP",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				if (!tryAgain) {
					throw new RuntimeException("Conneciton problem.");
				}
			}
		} while (tryAgain);

		if (files.length == 0) {
			throw new IOException("Cannot list files at " + ftpFile);
		}
		final long ftpFileSize = files[0].getSize();
		log("Fetching " + ftpFile + "...");
		_ftp.setCopyStreamListener(new CopyStreamListener() {
			private long _lastPercent = -1;

			@Override
			public void bytesTransferred(long totalBytesTransferred,
					int bytesTransferred, long streamSize) {
				int p = (int) ((double) totalBytesTransferred
						/ (double) ftpFileSize * 100);
				if (p == 100 || _lastPercent + 10 <= p) {
					log(p + "% " + ftpFile);
					_lastPercent = p;
				}
			}

			@Override
			public void bytesTransferred(CopyStreamEvent event) {
				// nothing
			}
		});
		if (_ftp.retrieveFile(ftpFile, os)) {
			log("done!");
		} else {
			log("failed!");
			throw new IOException("Failed retriving file " + ftpFile + ".");
		}
	}

	void log(String msg) {
		out.println(msg);
		_label.setText(msg);
	}
}
