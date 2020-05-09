package com.mfg.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

public class Config {
	public static final String K_MERCURIAL_EXEC_PATH = "mercurial-exec-path";
	public static final String K_MFG_REPO_PATH = "mfg-repo-path";
	public static final String K_DEBUG_INFO_AND_ASSERTIONS = "enable-debug-info-and-assertions";

	private static Config _config;
	private static Boolean _isWindows;
	private Properties _props;
	private File _dir;

	public static Config getInstance() {
		if (_config == null) {
			_config = new Config();
		}
		return _config;
	}

	public static boolean isWindows() {
		if (_isWindows == null) {
			_isWindows = new Boolean(System.getProperty("os.name").startsWith(
					"Windows"));
		}
		return _isWindows.booleanValue();
	}

	public static boolean isArch64() {
		return System.getProperty("os.arch").contains("64");
	}

	public static String getArch() {
		return isArch64() ? "x86_64" : "x86";
	}

	public Config() {
		_dir = new File(System.getProperty("user.home") + "/.mfg-build");
		_dir.mkdirs();

		// default properties
		_props = new Properties();
		_props.put(K_DEBUG_INFO_AND_ASSERTIONS, Boolean.TRUE.toString());

		File f = new File(_dir, "config.properties");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			try (FileInputStream input = new FileInputStream(f)) {
				_props.load(input);
			}
		} catch (IOException e) {
			Utils.error(e);
		}
	}

	public void put(String key, String value) {
		_props.put(key, value);
	}

	public String get(String key) {
		String v = _props.getProperty(key);
		return v == null ? "" : v;
	}

	public void save() {
		try (FileOutputStream fos = new FileOutputStream(new File(_dir,
				"config.properties"))) {
			_props.store(fos, "");
		} catch (IOException e) {
			Utils.error(e);
		}
	}

	public static boolean isValidMFGRepo(Path path) {
		return Files.exists(path.resolve(".hg"));
	}

	public static boolean isValidEclipseExec(Path path) {
		if (isWindows()) {
			return Files.exists(path)
					&& path.getFileName().toString().equals("eclipse.exe");
		}
		return Files.exists(path) && !Files.isDirectory(path)
				&& path.getFileName().toString().equals("eclipse");
	}

	public static boolean isValidMercurialExec(Path path) {
		if (isWindows()) {
			return Files.exists(path)
					&& path.getFileName().toString().equals("hg.exe");
		}
		return Files.exists(path) && !Files.isDirectory(path)
				&& path.getFileName().toString().equals("hg");
	}

	public Path getDir() {
		return _dir.toPath();
	}

	public Path installationsDir() {
		return _dir.toPath().resolve("installations");
	}

	public Path downloadsDir() {
		return _dir.toPath().resolve("downloads");
	}

	public static String getElipseExecName() {
		return isWindows() ? "eclipse.exe" : "eclipse";
	}

	public static String getMercurialExecName() {
		return isWindows() ? "hg.exe" : "hg";
	}

	public boolean isDebugInfoAndAssertionsEnabled() {
		return Boolean.parseBoolean(_props
				.getProperty(K_DEBUG_INFO_AND_ASSERTIONS));
	}

	public void setDebugInfoAndAssertionsEnabled(boolean enabled) {
		_props.put(K_DEBUG_INFO_AND_ASSERTIONS, Boolean.valueOf(enabled));
	}

	public static boolean isValidJREPath(Path path) {
		return isWindows() ? Files.exists(path.resolve("bin").resolve(
				"java.exe")) : Files
				.exists(path.resolve("bin").resolve("java"));
	}

	public boolean isValid() {
		return isValidMFGRepo(Paths.get(get(K_MFG_REPO_PATH)))
				&& isValidMercurialExec(Paths.get(get(K_MERCURIAL_EXEC_PATH)));
	}

	public boolean isValid(String... keys) {
		if (keys == null) {
			return true;
		}

		Set<String> fields = new HashSet<>(Arrays.asList(keys));
		List<String> errors = new ArrayList<>();
		if (fields.contains(K_MFG_REPO_PATH)) {
			if (!isValidMFGRepo(Paths.get(get(K_MFG_REPO_PATH)))) {
				errors.add("Invalid MFG repository directory path.");
			}
		}
		if (fields.contains(K_MERCURIAL_EXEC_PATH)) {
			if (!isValidMercurialExec(Paths.get(get(K_MERCURIAL_EXEC_PATH)))) {
				errors.add("Invalid Mercurial executable (hg.exe) path.");
			}
		}
		if (!errors.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html>There are wrong settings, please fix them.<br><ul>");
			for (String e : errors) {
				sb.append("<li>" + e + "</li>");
			}
			sb.append("</ul></html>");
			JOptionPane.showMessageDialog(AppWindow.getInstance(),
					sb.toString(), "Settings", JOptionPane.ERROR_MESSAGE);
		}
		return errors.isEmpty();
	}
}
