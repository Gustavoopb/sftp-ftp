package com.sftpFtp.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sftpFtp.entity.Client;
import com.sftpFtp.entity.LogErrorClient;
import com.sftpFtp.util.base.AbstractClientUtil;

@Component
public class SftpClientUtil extends AbstractClientUtil {

	public SftpClientUtil() {
		super(Logger.getLogger("sftp"));
	}

	@Async
	public void download(Client client) {
		StandardFileSystemManager manager = new StandardFileSystemManager();
		try {
			manager.init();
			List<FileObject> sftpFiles = loginGetFiles(client, manager);
			this.getLogger().info("Connecting to Host: " + client.getHost() + " port: " + client.getPort());
			this.getLogger().info(String.valueOf(sftpFiles.size()) + " files in: " + client.getRemoteDirectory());
			for (FileObject sftpFile : sftpFiles) {
				try {
					FileObject localFile = manager.resolveFile(String.valueOf(this.getLocalTempPath()) + File.separator
							+ sftpFile.getName().getBaseName());
					localFile.copyFrom(sftpFile, Selectors.SELECT_SELF);
					if (sftpFile.exists()) {
						sftpFile.delete();
					}
					FileUtil.moveFile(
							new File(String.valueOf(this.getLocalTempPath()) + File.separator
									+ localFile.getName().getBaseName()),
							client.getLocalDirectory(), this.getLocalBackupPath());
					this.getLogger().info("File: " + sftpFile.getName() + " moved to: " + client.getLocalDirectory());
				} catch (IOException e) {
					this.getLogErrorClientService()
							.save(new LogErrorClient(client, sftpFile.getName().getBaseName(), e));
					this.getLogger().error("Unexpected error occurred: " + sftpFile.getName().getBaseName()
							+ ". Host: " + client.getHost() + ". User: " + client.getUsername(), e);
				}
			}
		} catch (UnsupportedEncodingException | FileSystemException e) {
			this.getLogErrorClientService().save(new LogErrorClient(client, null, e));
			this.getLogger().error("Unexpected error occurred: " + client.getHost() + ". User: " + client.getUsername(),
					e);
		} finally {
			manager.close();
		}
	}

	private List<FileObject> loginGetFiles(Client client, StandardFileSystemManager manager)
			throws FileSystemException, UnsupportedEncodingException {
		FileObject remoteFiles = manager.resolveFile(this.createConnectionString(client.getHost(), client.getUsername(),
				client.getUserPassword(), client.getRemoteDirectory(), client.getPort()), this.createDefaultOptions());
		FileFilterSelector selector = new FileFilterSelector((FileFilter) new FileFilterImp());
		List<FileObject> remoteFilesList = Arrays.asList(remoteFiles.findFiles((FileSelector) selector));
		return remoteFilesList;
	}

	protected String getLocalBackupPath() {
		return this.getEnvironment().getProperty("local.BACKUP.sftp.path");
	}

	protected String getLocalTempPath() {
		return this.getEnvironment().getProperty("local.TEMP.sftp.path");
	}

	public FileSystemOptions createDefaultOptions() throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, Integer.valueOf(60000));
		return opts;
	}

	public String createConnectionString(String hostName, String username, String password, String remoteFilePath,
			String port) throws UnsupportedEncodingException {
		return "sftp://" + this.parseURL(username) + ":" + this.parseURL(password) + "@" + this.parseURL(hostName) + ":"
				+ port + remoteFilePath;
	}

	private String parseURL(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
}
