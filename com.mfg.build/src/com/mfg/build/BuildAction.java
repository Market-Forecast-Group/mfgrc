package com.mfg.build;

import static java.lang.System.out;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;

public abstract class BuildAction extends AbstractAction {
	private static boolean _runningProcess = false;

	private static final long serialVersionUID = 1L;

	{
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public BuildAction() {
		super();
	}

	public BuildAction(String name, Icon icon) {
		super(name, icon);
	}

	public BuildAction(String name) {
		super(name);
	}

	protected abstract void execute(Config cfg) throws Exception;

	protected abstract String[] getValidationFields();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (_runningProcess) {
			JOptionPane.showMessageDialog(AppWindow.getInstance(),
					"There is another process running. Try later.");
			return;
		}
		_runningProcess = true;
		try {
			Config cfg = Config.getInstance();
			String[] validationFields = getValidationFields();
			if (cfg.isValid(validationFields)) {
				actionExecute(cfg);
			} else {
				if (ConfigDialog.open(getValidationFields())) {
					actionExecute(cfg);
				} else {
					out.println("Command " + getValue(NAME) + " aborted!");
					finished();
				}
			}
		} catch (Exception e1) {
			Utils.error(e1);
			finished();
		}
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm");

	public static String getProductLauncherName(String productName, Path path) {
		try {
			FileTime time = Files.getLastModifiedTime(path.getParent());
			return productName + " - "
					+ dateFormat.format(new Date(time.toMillis()));
		} catch (IOException e) {
			e.printStackTrace();
			return path.getFileName().toString();
		}
	}

	private void actionExecute(final Config cfg) throws Exception {
		out.println("\n"
				+ getValue(NAME)
				+ "\n--------------------------------------------------------------------");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					execute(cfg);
				} catch (Exception e) {
					Utils.error(e);
				}
				finished();
			}
		}).start();
	}

	public static void redirectProcess(Process proc, PrintStream printer)
			throws IOException, InterruptedException {
		InputStream is = proc.getInputStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				printer.println(line);
			}
		}

		is = proc.getErrorStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				printer.println(line);
			}
		}

		proc.waitFor();
	}

	protected static void finished() {
		out.println("\n-- end --");
		_runningProcess = false;
	}

	public static Process execProcess(File curDir, String... args)
			throws IOException {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(curDir);
		Process proc = pb.start();
		return proc;
	}

	public static int execProcessAndRedirect(File curDir, String... args)
			throws IOException, InterruptedException {
		Process proc = execProcess(curDir, args);
		redirectProcess(proc, out);
		return proc.exitValue();
	}

	public static String execProcessAndGetOutput(File curDir, String... args)
			throws IOException, InterruptedException {
		Process proc = execProcess(curDir, args);
		StringBuilder sb = new StringBuilder();
		InputStream is = proc.getInputStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		proc.waitFor();
		return sb.toString();
	}

	public static class CopyFileVisitor extends SimpleFileVisitor<Path> {
		private final Path _targetPath;
		private Path _sourcePath = null;

		public CopyFileVisitor(Path targetPath) {
			this._targetPath = targetPath;
		}

		@Override
		public FileVisitResult preVisitDirectory(final Path dir,
				final BasicFileAttributes attrs) throws IOException {
			if (_sourcePath == null) {
				_sourcePath = dir;
			} else {
				Files.createDirectories(_targetPath.resolve(_sourcePath
						.relativize(dir)));
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(final Path file,
				final BasicFileAttributes attrs) throws IOException {
			if (_sourcePath == null) {
				_sourcePath = file.getParent();
			}
			Files.copy(file, _targetPath.resolve(_sourcePath.relativize(file)));
			return FileVisitResult.CONTINUE;
		}

	}

	public static void copyTree(Path srcDir, Path dstDir,
			boolean includesRootDir, Consumer<Path> visitor) throws IOException {
		Path dstDir2 = dstDir;
		if (includesRootDir && Files.isDirectory(srcDir)) {
			dstDir2 = dstDir.resolve(srcDir.getFileName());
		}
		Files.createDirectories(dstDir2);
		Files.walkFileTree(srcDir, new CopyFileVisitor(dstDir2) {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				if (visitor != null) {
					visitor.accept(file);
				}
				return super.visitFile(file, attrs);
			}
		});
	}

	public static void copyTree(Path srcDir, Path dstDir, Consumer<Path> visitor)
			throws IOException {
		copyTree(srcDir, dstDir, false, visitor);
	}

	public static void copyTree(Path srcDir, Path dstDir) throws IOException {
		copyTree(srcDir, dstDir, false, null);
	}

	public static String readResource(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				Utils.class.getResourceAsStream(path)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		return sb.toString();
	}

	public static String template(String templ, Map<Object, Object> attrs) {
		String str = templ;
		for (Object key : attrs.keySet()) {
			Object val = attrs.get(key);
			str = str.replace("%" + key + "%", val.toString());
		}
		return str;
	}

	public static String normalizePath(Path path) {
		return normalizePath(path.toAbsolutePath().toString());
	}

	public static String normalizePath(String path) {
		String replace = path.replace("\\", "/");
		return replace;
	}

	public static void printProperties(
			Map<? extends Object, ? extends Object> attrs) {
		for (Object k : attrs.keySet()) {
			out.println(k + "=" + attrs.get(k));
		}
	}

	public static void printTree(Path tree) throws IOException {
		Files.walkFileTree(tree, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				out.println(file);
				return super.visitFile(file, attrs);
			}
		});
	}

	public static boolean deleteTree(Path tree) throws IOException {
		if (Files.exists(tree)) {
			if (Files.isDirectory(tree)) {
				Files.walkFileTree(tree, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return super.visitFile(file, attrs);
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir,
							IOException exc) throws IOException {
						Files.delete(dir);
						return super.postVisitDirectory(dir, exc);
					}
				});
			} else {
				Files.delete(tree);
			}
			if (Files.exists(tree)) {
				out.println("Error: cannot delete folder " + tree);
				JOptionPane.showMessageDialog(AppWindow.getInstance(),
						"Error: cannot delete folder " + tree, "Delete",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	public static void ftpGet(String hostname, String ftpFile, Path saveTo)
			throws Exception {
		ftpGet(hostname, ftpFile, null, null, saveTo);
	}

	public static void ftpDelete(String hostname, String user, String pwd,
			String ftpFile) throws Exception {
		long t = System.currentTimeMillis();

		out.println("Deleting " + hostname + "/" + ftpFile + "...");

		FTPClient ftp = new FTPClient();
		try {
			out.print("Connecting to " + hostname + "... ");
			ftp.connect(hostname);
			out.println("done!");

			if (user != null) {
				out.print("Login... ");
				if (ftp.login(user, pwd)) {
					out.println("done!");
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					ftp.enterLocalPassiveMode();
					ftp.deleteFile(ftpFile);
				} else {
					out.println("failed!");
					throw new IOException("Login failed");
				}
			}
		} finally {
			if (ftp.isConnected()) {
				ftp.disconnect();
			}
		}
		t = (System.currentTimeMillis() - t) / 1000;
		out.println(t / 60 + " min, " + (t % 60) + " sec");
	}

	public static void ftpUpload(String hostname, String user, String pwd,
			String ftpFolder, Path localFile) throws Exception {
		long t = System.currentTimeMillis();

		out.println("Uploading " + localFile + " to " + hostname + "/"
				+ ftpFolder + "...");

		FTPClient ftp = new FTPClient();
		ftp.setUseEPSVwithIPv4(true);

		try {
			out.print("Connecting to " + hostname + "... ");
			ftp.connect(hostname);
			out.println("done!");

			if (user != null) {
				out.print("Login... ");
				if (ftp.login(user, pwd)) {
					out.println("done!");
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					ftp.enterLocalPassiveMode();
					ftp.setCopyStreamListener(createCopyStreamListener(
							localFile.getFileName().toString(),
							Files.size(localFile)));
					ftp.cwd(ftpFolder);
					try (InputStream input = Files.newInputStream(localFile)) {
						ftp.storeFile(localFile.getFileName().toString(), input);
					}
				} else {
					out.println("failed!");
					throw new IOException("Login failed");
				}
			}
		} finally {
			if (ftp.isConnected()) {
				ftp.disconnect();
			}
		}
		t = (System.currentTimeMillis() - t) / 1000;
		out.println(t / 60 + " min, " + (t % 60) + " sec");
	}

	public static boolean ftpGet(String hostname, String user, String pwd,
			String ftpFile, final Path saveTo) throws Exception {
		boolean newfile = true;

		long t = System.currentTimeMillis();

		out.println("Fetching " + hostname + "/" + ftpFile + "...");

		if (!Files.exists(saveTo.getParent())) {
			Files.createDirectories(saveTo.getParent());
		}

		Long fileSize = null;
		if (Files.exists(saveTo)) {
			fileSize = Long.valueOf(saveTo.toFile().length());
		} else {
			Files.createFile(saveTo);
		}

		FTPClient ftp = new FTPClient();
		try {
			out.print("Connecting to " + hostname + "... ");
			ftp.connect(hostname);
			out.println("done!");

			if (user != null) {
				out.print("Login... ");
				if (ftp.login(user, pwd)) {
					out.println("done!");
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					ftp.enterLocalPassiveMode();
					FTPFile[] files = ftp.listFiles(ftpFile);
					if (files.length == 0) {
						throw new IOException("Cannot list files at " + ftpFile);
					}
					final long ftpFileSize = files[0].getSize();
					if (fileSize != null) {
						if (fileSize.longValue() == ftpFileSize) {
							newfile = false;
						} else {
							out.println("There is a new file, deleting the old one...");
							Files.delete(saveTo);
						}
					}
					if (newfile) {
						try (FileOutputStream fos = new FileOutputStream(
								saveTo.toFile())) {
							out.print("Saving file to " + saveTo + "... ");
							ftp.setCopyStreamListener(createCopyStreamListener(
									ftpFile, ftpFileSize));
							if (ftp.retrieveFile(ftpFile, fos)) {
								out.println();
								out.println("done!");
							} else {
								out.println("failed!");
								throw new IOException("Failed retriving file "
										+ ftpFile + ".");
							}
						}
						ftp.disconnect();
					} else {
						out.println("There is not a new file to download.");
					}
				} else {
					out.println("failed!");
					throw new IOException("Login failed");
				}
			}
		} finally {
			if (ftp.isConnected()) {
				ftp.disconnect();
			}
		}
		t = (System.currentTimeMillis() - t) / 1000;
		out.println(t / 60 + " min, " + (t % 60) + " sec");
		return newfile;
	}

	private static CopyStreamListener createCopyStreamListener(String name,
			final long fileSize) {

		out.println(name + "\t\t\t\t0% ");

		return new CopyStreamListener() {
			private long _lastPercent = -1;

			@Override
			public void bytesTransferred(long totalBytesTransferred,
					int bytesTransferred, long streamSize) {
				int p = (int) ((double) totalBytesTransferred
						/ (double) fileSize * 100);
				if (p == 100 || _lastPercent + 10 <= p) {
					out.println(name + "\t\t\t\t" + p + "% ");
					_lastPercent = p;
				}
			}

			@Override
			public void bytesTransferred(CopyStreamEvent event) {
				// nothing
			}
		};
	}

	public static boolean httpGet_newVersion(Path curDir, String url,
			String user, String pwd) throws AuthenticationException {
		try {
			out.println("Fetching " + url + "...");

			String[] split = url.split("/");
			String name = split[split.length - 1];
			Path saveTo = curDir.resolve(name);
			Date fileDate = null;
			if (Files.exists(saveTo)) {
				fileDate = new Date(Files.getLastModifiedTime(saveTo)
						.toMillis());

				HttpClientBuilder builder = HttpClientBuilder.create();
				HttpGet request = new HttpGet(url);

				if (user != null) {
					BasicCredentialsProvider prov = new BasicCredentialsProvider();
					UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
							user, pwd);
					prov.setCredentials(AuthScope.ANY, creds);
					builder.setDefaultCredentialsProvider(prov);
					request.addHeader(new BasicScheme().authenticate(creds,
							request, new BasicHttpContext()));
				}

				try (CloseableHttpClient client = builder.build();
						CloseableHttpResponse response = client
								.execute(request);) {
					Header header = response.getLastHeader("Last-Modified");
					if (header != null) {
						Date headerDate = DateUtils
								.parseDate(header.getValue());

						if (headerDate.getTime() == fileDate.getTime()) {
							out.println("There is not a new file to download.");
							return false;
						}
					}
				}
			}
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		out.println("There is a new file to download");
		return true;

	}

	public static boolean httpGet(Path curDir, String url, String user,
			String pwd) throws Exception {
		long t = System.currentTimeMillis();
		out.println("Fetching " + url + "...");

		if (!Files.exists(curDir)) {
			Files.createDirectories(curDir);
		}

		String[] split = url.split("/");
		String name = split[split.length - 1];
		Path saveTo = curDir.resolve(name);
		Date fileDate = null;
		if (Files.exists(saveTo)) {
			fileDate = new Date(Files.getLastModifiedTime(saveTo).toMillis());
		}

		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpGet request = new HttpGet(url);

		if (user != null) {
			BasicCredentialsProvider prov = new BasicCredentialsProvider();
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
					user, pwd);
			prov.setCredentials(AuthScope.ANY, creds);
			builder.setDefaultCredentialsProvider(prov);
			request.addHeader(new BasicScheme().authenticate(creds, request,
					new BasicHttpContext()));
		}

		try (CloseableHttpClient client = builder.build();
				CloseableHttpResponse response = client.execute(request);) {
			Header header = response.getLastHeader("Last-Modified");
			if (header == null) {
				out.println("Error: Last-Modified header is not available. Try later.");
				return false;
			}

			Date headerDate = DateUtils.parseDate(header.getValue());

			if (fileDate != null) {
				if (headerDate.getTime() == fileDate.getTime()) {
					out.println("There is not a new file to download.");
					return true;
				}
				out.println("Different files dates (" + fileDate + ", "
						+ headerDate + ")");
				out.println("Delete old file " + saveTo);
				Files.delete(saveTo);
			}

			out.print("Writing file to " + saveTo + "... ");
			try (InputStream is = response.getEntity().getContent()) {
				Files.copy(is, saveTo);
				Files.setLastModifiedTime(saveTo,
						FileTime.fromMillis(headerDate.getTime()));
			}
			out.println("done!");
		}

		t = (System.currentTimeMillis() - t) / 1000;
		out.println(t / 60 + " min, " + (t % 60) + " sec\n");

		return true;
	}

	/**
	 * Download file from MFG ftp MFGBuild folder.
	 * 
	 * @param filename
	 * @return True if there is a new file.
	 * @throws Exception
	 */
	public static boolean ftpGet_MFG(String filename) throws Exception {
		return ftpGet("ftp.marketforecastgroup.com", "mfg", "positiveMfg99_",
				"MFGBuild/" + filename, Config.getInstance().downloadsDir()
						.resolve(filename));
	}

	public static void ftpDelete_MFG(String filename) throws Exception {
		ftpDelete("ftp.marketforecastgroup.com", "mfg", "positiveMfg99_",
				"MFGBuild/" + filename);
	}

	public static void ftpUpload_MFG(String ftpFolder, Path localFile)
			throws Exception {
		ftpUpload("ftp.marketforecastgroup.com", "mfg", "positiveMfg99_",
				"MFGBuild/" + ftpFolder, localFile);
	}

	public static void httpGet_Bitbucket(Path curDir, String filename)
			throws Exception {
		if (!httpGet(curDir,
				"https://bitbucket.org/marketforecastgroup/mfgrc/get/"
						+ filename, "arianfornaris", "arianfornafdz")) {
			throw new IOException("Problem fetching " + filename);
		}
	}

	public static boolean httpGet_newVersion_Bitbucket(Path curDir,
			String filename) throws Exception {
		return httpGet_newVersion(curDir,
				"https://bitbucket.org/marketforecastgroup/mfgrc/get/"
						+ filename, "arianfornaris", "arianfornafdz");
	}

	public static void unzip(Path zipPath, Path dstDir) throws IOException {
		unzip(zipPath, dstDir, false);
	}

	public static void installZipIfChanged(String zipFileName, Path dstDir,
			boolean ignoreRootFolderInZip, boolean clean) throws IOException {
		Path zipPath = Config.getInstance().getDir().resolve("downloads")
				.resolve(zipFileName);
		out.println("Installing " + zipFileName + "...");
		if (newZipFile(zipPath, dstDir)) {
			if (clean) {
				deleteTree(dstDir);
			}
			unzip(zipPath, dstDir, ignoreRootFolderInZip);
		} else {
			out.println(zipFileName + " is already installed.");
		}
	}

	public static void ftpInstall(String zipFileName, String dstDirName,
			boolean clean) throws Exception {
		Path dstDir = Config.getInstance().installationsDir()
				.resolve(dstDirName);
		ftpGet_MFG(zipFileName);
		installZipIfChanged(zipFileName, dstDir, true, clean);
		out.println();
	}

	public static void unzip(Path zipPath, Path dstDir, boolean ignoreRootFolder)
			throws IOException {
		out.println("Unzipping " + zipPath + "... ");
		long t = System.currentTimeMillis();
		byte[] buffer = new byte[1024];

		Files.createDirectories(dstDir);
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(
				zipPath.toFile()))) {
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				if (!ze.isDirectory()) {
					Path fileName = Paths.get(ze.getName());
					if (ignoreRootFolder) {
						fileName = fileName.getName(0).relativize(fileName);
					}
					Path newFile = dstDir.resolve(fileName);

					Files.createDirectories(newFile.getParent());

					try (FileOutputStream fos = new FileOutputStream(
							newFile.toFile())) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
		}
		t = (System.currentTimeMillis() - t) / 1000;
		out.println(t / 60 + " min, " + (t % 60) + " sec\n");
	}

	public static boolean newZipFile(Path newfile, Path oldfile) {
		try {
			return !Files.exists(oldfile)
					|| Files.getLastModifiedTime(oldfile).toMillis() < Files
							.getLastModifiedTime(newfile).toMillis();
		} catch (IOException e) {
			Utils.error(e);
			return true;
		}
	}

	public static void setExecutable(Path exe) {
		if (Config.isWindows()) {
			exe.getParent().resolve(exe.getFileName() + ".exe");
		}
		exe.toFile().setExecutable(true);
	}

	public static boolean isMfgPluginName(Path path) {
		String name = path.getFileName().toString();
		return name.startsWith("com.mfg.") || name.startsWith("org.mfg.")
				|| name.startsWith("com.marketforecastgroup.")
				|| name.startsWith("com.marketforescastgroup.");

	}

	/**
	 * <p>
	 * Create a tree-checksums.properties file. This file can be used to
	 * synchronized two folders.
	 * </p>
	 * <p>
	 * This file is a "Java properties" file and the contents is formed by the
	 * key/value pairs:
	 * </p>
	 * <p>
	 * Key: the relative path to a file.<br>
	 * Value: the checksum of the file.
	 * </p>
	 * 
	 * @param tree
	 *            The path to the folder.
	 * @throws IOException
	 */
	public static void checksumTree(final Path tree) throws IOException {
		final Path hashesFile = tree.resolve("tree-checksums.properties");

		final Properties props = new Properties();
		Files.walkFileTree(tree, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				if (!file.equals(hashesFile)) {
					String md5 = DigestUtils.md5Hex(Files.newInputStream(file));
					String relpath = normalizePath(tree.relativize(file)
							.toString());
					props.put(relpath, md5);
					// out.println("MD5 " + relpath + " " + md5);
				}
				return FileVisitResult.CONTINUE;
			}
		});

		props.store(Files.newOutputStream(hashesFile), null);
	}

	public static void launchProduct(Path productExePath) throws IOException {
		if (Config.isWindows()) {
			Runtime.getRuntime().exec(
					new String[] { "cmd", "/c", productExePath.toString(),
							"-consoleLog" });
		} else {
			Runtime.getRuntime().exec(
					new String[] { productExePath.toString(), "-consoleLog" });
		}
	}

	public static List<String> getRunningProcess() throws Exception {
		List<String> list = new ArrayList<>();

		if (Config.isWindows()) {
			Process proc = Runtime.getRuntime().exec(
					new String[] { "cmd", "/c", "tasklist", "/FO", "CSV" });

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(proc.getInputStream()))) {
				String l;
				while ((l = reader.readLine()) != null) {
					// out.println(l.split(",")[0]);
					list.add(l);
				}
			}
		}

		return list;
	}

	public static boolean isDFSRunning() {
		try {
			return getRunningProcess().stream().anyMatch(
					(s) -> s.contains("DFS.exe"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}