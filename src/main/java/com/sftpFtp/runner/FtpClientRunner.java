package com.sftpFtp.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.sftpFtp.entity.Client;
import com.sftpFtp.entity.LogErrorClient;
import com.sftpFtp.enumeration.FtpEncryptionType;
import com.sftpFtp.enumeration.ProtocolType;
import com.sftpFtp.service.ClientService;
import com.sftpFtp.service.LogErrorClientService;
import com.sftpFtp.util.FtpClientUtil;

@Async
@Component
@EnableAsync
public class FtpClientRunner implements CommandLineRunner {
	@Autowired
	private FtpClientUtil ftpClientUtil;
	@Autowired
	private ClientService clientService;
	@Autowired
	private LogErrorClientService logErrorClientService;

	public void run(String... args) throws Exception {
		for (Client client : this.clientService.findAll()) {
			try {
				if (!FtpClientRunner.isFtpTransfer(client) && !FtpClientRunner.isFtpsTransfer(client))
					continue;
				this.runFtpClient(client);
				continue;
			} catch (Exception e) {
				this.logErrorClientService.save(new LogErrorClient(client, null, e));
			}
		}
	}

	private void runFtpClient(Client client) {
		this.ftpClientUtil.download(client);
	}

	private static boolean isFtpsTransfer(Client client) {
		if (client.getActive().booleanValue() && client.getProtocolType().equals(ProtocolType.FTP)
				&& (client.getFtpEncryptionType().equals(FtpEncryptionType.EXPLICIT_ENCRYPTION)
						|| client.getFtpEncryptionType().equals(FtpEncryptionType.IMPLICIT_ENCRYPTION))) {
			return true;
		}
		return false;
	}

	private static boolean isFtpTransfer(Client client) {
		if (client.getActive().booleanValue() && client.getProtocolType().equals(ProtocolType.FTP)
				&& client.getFtpEncryptionType().equals(FtpEncryptionType.NO_ENCRYPTION)) {
			return true;
		}
		return false;
	}
}
