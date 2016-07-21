package com.sftpFtp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

public class FileUtil implements FilenameFilter {
	private ArrayList<String> patterns = new ArrayList<>();
	private boolean regexp;
	private boolean acceptdir;

	public static String moveFile(File file, String directory, String backupPath) throws IOException {
		return FileUtil.moveFile(file, directory, ".txt", backupPath);
	}

	public static String moveFile(File file, String directory, String extension, String backupPath) throws IOException {
		String newFilename = FileUtil.getNewFilename(file, directory, extension);
		if (StringUtils.isNotBlank(backupPath)) {
			FileUtil.createBackupFile(file, newFilename, backupPath);
		}
		File newFile = new File(newFilename.replace("/", File.separator));
		file.renameTo(newFile);
		return newFile.getName();
	}

	private static String getNewFilename(File file, String directory, String extension) {
		String newFilename = file.getName().replace(extension.toLowerCase(), "").replace(extension.toUpperCase(), "");
		String newFullFilename = directory.endsWith(File.separator) ? String.valueOf(directory) + newFilename
				: String.valueOf(directory) + File.separator + newFilename;
		int sequence = 0;
		while (FileUtil.existingOutputFile(file, newFullFilename, extension)) {
			if (sequence == 0) {
				newFullFilename = String.valueOf(newFullFilename) + "_";
				newFilename = String.valueOf(newFilename) + "_";
			} else {
				newFullFilename = newFullFilename.substring(0, newFullFilename.lastIndexOf("_") + 1);
				newFilename = newFilename.substring(0, newFilename.lastIndexOf("_") + 1);
			}
			newFullFilename = String.valueOf(newFullFilename) + ++sequence;
			newFilename = String.valueOf(newFilename) + sequence;
		}
		return String.valueOf(newFullFilename) + extension;
	}

	private static boolean existingOutputFile(File file, String fileName, String extension) {
		File outputFile = new File((String.valueOf(fileName) + extension).replace("/", File.separator));
		boolean existFile = new File(String.valueOf(fileName) + extension).exists();
		if (!existFile) {
			return false;
		}
		try {
			return !FileUtil.generateFileHash(file).equals(FileUtil.generateFileHash(outputFile));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String generateFileHash(File file) throws IOException, NoSuchAlgorithmException {
		String hash;
		byte[] buffer = new byte[8192];
		hash = null;
		int read = 0;
		try (FileInputStream input = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			while ((read = input.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			hash = bigInt.toString(16);
		}
		return hash;
	}

	public static List<File> getFileByPath(String path, String... extensions) {
		ArrayList<File> files = new ArrayList<File>();
		for (String extension : extensions) {
			files.addAll(FileUtil.listFiles(path, extension, false, false));
		}
		return files;
	}

	private FileUtil(String filter, boolean regexp, boolean acceptdir) {
		this.acceptdir = acceptdir;
		this.setPatterns(filter);
		this.regexp = regexp;
	}

	@Override
	public boolean accept(File dir, String name) {
		File f;
		if (this.acceptdir && (f = new File(String.valueOf(dir.getAbsolutePath()) + File.separatorChar + name)).exists()
				&& f.isDirectory()) {
			return true;
		}
		return this.validate(name);
	}

	private boolean validate(String name) {
		boolean result = false;
		boolean bMatch = false;
		for (String element : this.patterns) {
			bMatch = Pattern.matches(element, name);
			if (!bMatch)
				continue;
			result = true;
			break;
		}
		return result;
	}

	private void setPatterns(String filter) {
		String aux = filter;
		if (!this.regexp) {
			aux = aux.replace("\\", "\\\\");
			aux = aux.replace("[", "\\[");
			aux = aux.replace("]", "\\]");
			aux = aux.replace(".", "[.]{1}");
			aux = aux.replace("*", ".*");
			aux = aux.replace("?", ".{1}");
		}
		this.patterns.clear();
		String[] auxArray = aux.split(";");
		int i = 0;
		while (i < auxArray.length) {
			String str = auxArray[i];
			this.patterns.add(str);
			++i;
		}
	}

	private static ArrayList<File> processDir(File dir, FilenameFilter filter, boolean recursive) {
		File[] listArray = dir.listFiles(filter);
		ArrayList<File> files = new ArrayList<File>();
		int i = 0;
		while (i < listArray.length) {
			File elem = listArray[i];
			if (elem.isDirectory()) {
				if (recursive) {
					ArrayList<File> tmp = FileUtil.processDir(elem, filter, recursive);
					files.addAll(tmp);
				}
			} else {
				files.add(elem);
			}
			++i;
		}
		return files;
	}

	private static ArrayList<File> listFiles(String directory, String filter, boolean recursive, boolean regexp) {
		ArrayList<File> result = new ArrayList<File>();
		File dir = new File(directory);
		if (!dir.exists() || !dir.isDirectory()) {
			throw new RuntimeException("Dir not found: " + dir.getAbsolutePath());
		}
		FileUtil fileUtil = new FileUtil(filter, regexp, recursive);
		ArrayList<File> fileTmp = FileUtil.processDir(dir, fileUtil, recursive);
		result.addAll(fileTmp);
		return result;
	}

	private static void createBackupFile(File source, String dest, String backupPath) throws IOException {
		String[] destArray = dest.substring(dest.indexOf(":") + 1).replace(File.separator, "/").split("/");
		String filename = destArray[destArray.length - 1];
		String complementPath = dest.substring(dest.indexOf(":") + 1).replace(filename, "");
		FileUtil.mkBackupDir(FileUtil.formatBackupPath(backupPath, complementPath));
		FileUtil.copyFileUsingStream(source,
				new File(String.valueOf(FileUtil.formatBackupPath(backupPath, complementPath)) + filename));
	}

	private static boolean mkBackupDir(String path) {
		return new File(path).mkdirs();
	}

	private static String formatBackupPath(String backupPath, String localPath) {
		return String.valueOf(backupPath) + localPath + new LocalDate().toString("yyyyMMdd") + File.separator;
	}

	public static void copyFileUsingStream(File source, File dest) throws IOException {
		try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}
}
