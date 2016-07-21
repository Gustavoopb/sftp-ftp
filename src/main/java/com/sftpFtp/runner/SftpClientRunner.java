package com.sftpFtp.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.sftpFtp.entity.Client;
import com.sftpFtp.entity.LogErrorClient;
import com.sftpFtp.enumeration.ProtocolType;
import com.sftpFtp.service.ClientService;
import com.sftpFtp.service.LogErrorClientService;
import com.sftpFtp.util.SftpClientUtil;

@Async
@Component
@EnableAsync
public class SftpClientRunner implements CommandLineRunner {
	@Autowired
	private ClientService clientService;
	@Autowired
	private LogErrorClientService logErrorClientService;
	@Autowired
	private SftpClientUtil sftpClientUtil;

	public void run(String... args) throws Exception {
		for (Client client : this.clientService.findAll()) {
			try {
				if (!this.isSftpTransfer(client))
					continue;
				this.runSftpClient(client);
				continue;
			} catch (Exception e) {
				this.logErrorClientService.save(new LogErrorClient(client, null, e));
			}
		}
	}

	private void runSftpClient(Client client) {
		this.sftpClientUtil.download(client);
	}

	private boolean isSftpTransfer(Client client) {
		if (client.getActive().booleanValue() && client.getProtocolType().equals(ProtocolType.SFTP)) {
			return true;
		}
		return false;
	}
}
