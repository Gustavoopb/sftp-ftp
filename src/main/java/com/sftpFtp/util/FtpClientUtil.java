package com.sftpFtp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sftpFtp.entity.Client;
import com.sftpFtp.entity.LogErrorClient;
import com.sftpFtp.util.base.AbstractClientUtil;

@Component
public class FtpClientUtil extends AbstractClientUtil {

	public FtpClientUtil() {
		super(Logger.getLogger("ftp"));
	}

	public FTPClient createClientByclient(Client client) {
		switch (client.getFtpEncryptionType().ordinal()) {
		case 1:
			FTPClient ftp = new FTPClient();
			ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
			return ftp;
		case 3:
			return this.createFtpsClient(false);

		case 2:
			return this.createFtpsClient(true);
		}
		return null;
	}

	private FTPClient createFtpsClient(boolean isImplicit) {
		FTPSClient ftps = new FTPSClient(isImplicit);
		ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		return ftps;
	}

	public void disconnect(FTPClient ftps) {
		if (ftps.isConnected()) {
			try {
				ftps.logout();
				ftps.disconnect();
				this.getLogger().info("Client disconnected.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Async
	public void download(Client client) {
		FTPClient ftp = this.createClientByclient(client);
		try {
			this.ftpConnection(ftp, client);
			ftp.setFileType(2);
			if (ftp instanceof FTPSClient) {
				((FTPSClient) ftp).execPROT("P");
			}
			this.login(client, ftp);
			ftp.enterLocalPassiveMode();
			this.downloadFilePath(client, ftp, client.getRemoteDirectory(), client.getLocalDirectory());
		} catch (Exception e) {
			this.getLogErrorClientService().save(new LogErrorClient(client, null, e));
			e.printStackTrace();
		}
		this.disconnect(ftp);
	}

	public void downloadFilePath(Client client, FTPClient ftp, String remoteFilePath, String localFilePath)
			throws IOException {
		if (!ftp.changeWorkingDirectory(remoteFilePath)) {
			return;
		}
		List<FTPFile> ftpFiles = this.fileFilter(ftp.listFiles());
		this.getLogger().info(String.valueOf(ftpFiles.size()) + " files in: " + remoteFilePath);
		for (FTPFile ftpFile : ftpFiles) {
			try (FileOutputStream fos = new FileOutputStream(this.getLocalTempFilename(ftpFile))) {
				if (ftp.retrieveFile(ftpFile.getName(), fos)) {
					ftp.deleteFile(ftpFile.getName());
					FileUtil.moveFile(new File(this.getLocalTempFilename(ftpFile)), localFilePath,
							this.getLocalBackupPath());
					this.getLogger().info("File: " + ftpFile.getName() + " moved to: " + localFilePath);
				}
			} catch (IOException e) {
				this.getLogErrorClientService().save(new LogErrorClient(client, ftpFile.getName(), e));
				this.getLogger().error("Unexpected error occurred: " + ftpFile.getName(), e);
				continue;
			}
		}
		this.getLogger().info("Download successfully.");
	}

	private List<FTPFile> fileFilter(FTPFile... ftpFiles) throws IOException {
		List<FTPFile> result = new ArrayList<>();
		for (FTPFile ftpFile : ftpFiles) {
			if (ftpFile.getName().toLowerCase().matches(".*\\.txt$")) {
				result.add(ftpFile);
			}
		}
		return result;
	}

	protected String getLocalBackupPath() {
		return this.getEnvironment().getProperty("local.BACKUP.ftp.path");
	}

	protected String getLocalTempPath() {
		return this.getEnvironment().getProperty("local.TEMP.ftp.path");
	}

	private String getLocalTempFilename(FTPFile ftpFile) {
		return this.getLocalFilename(this.getLocalTempPath(), ftpFile);
	}

	private String getLocalFilename(String localFilePath, FTPFile remoteFile) {
		return localFilePath.endsWith(File.separator) ? String.valueOf(localFilePath) + remoteFile.getName()
				: String.valueOf(localFilePath) + File.separator + remoteFile.getName();
	}

	public void ftpConnection(FTPClient ftps, Client client) throws SocketException, IOException, Exception {
		ftps.connect(client.getHost(), Integer.parseInt(client.getPort()));
		int reply = ftps.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftps.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		this.getLogger().info("Connecting to Host: " + client.getHost() + " port: " + client.getPort());
	}

	public void login(Client client, FTPClient ftp) throws IOException {
		ftp.login(client.getUsername(), client.getUserPassword());
		this.getLogger().info("Log in: " + client.getUsername());
	}
}
