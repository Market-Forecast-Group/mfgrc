package com.mfg.build;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class DirPatch {

	public static class Base {
		final Path _dir;
		final Map<Path, String> _hashMap;

		public Base(Path dir) throws IOException {
			super();
			_dir = dir;
			_hashMap = new HashMap<>();

			Files.walkFileTree(_dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					String md5 = DigestUtils.md5Hex(Files.newInputStream(file));
					Path relFile = _dir.relativize(file);
					_hashMap.put(relFile, md5);
					return FileVisitResult.CONTINUE;
				}
			});

		}

		public Path getDir() {
			return _dir;
		}

		public String md5(Path path) {
			return _hashMap.get(path);
		}
	}

	public static Base buildBase(Path dir) throws IOException {
		return new Base(dir);
	}

	public static List<Path> buildPatch(final Base base, final Path dir,
			final boolean debug) throws IOException {
		final List<Path> list = new ArrayList<>();

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Path relFile = dir.relativize(file);
				String hash1 = base.md5(relFile);
				if (hash1 == null) {
					// new file
					list.add(relFile);
					if (debug)
						out.println("+ " + relFile);
				} else {
					String hash2 = DigestUtils.md5Hex(Files
							.newInputStream(file));
					if (hash1.equals(hash2)) {
						if (debug)
							out.println("= " + relFile);
					} else {
						// modified file
						if (debug)
							out.println("M " + relFile);
						list.add(relFile);
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});

		return list;
	}

	public static void main(String[] args) throws IOException {
		String dir1 = "/home/arian/.mfg-build/products/MFG-1405164882764";
		String dir2 = "/home/arian/.mfg-build/products/MFG-1411466300487";
		// dir1 = dir2;

		Base base = buildBase(Paths.get(dir1));
		List<Path> patch = buildPatch(base, Paths.get(dir2), true);

		for (Path p : patch) {
			out.println(p);
		}
	}

}
