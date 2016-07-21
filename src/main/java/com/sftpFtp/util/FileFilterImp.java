package com.sftpFtp.util;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileSelectInfo;

public class FileFilterImp implements FileFilter {
	public boolean accept(FileSelectInfo fileInfo) {
		boolean isTXT = fileInfo.getFile().getName().getBaseName().toLowerCase().matches(".*\\.txt$");
		return isTXT;
	}
}
