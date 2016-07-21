package com.sftpFtp.util.base;

import javax.persistence.MappedSuperclass;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

import com.sftpFtp.entity.Client;
import com.sftpFtp.service.LogErrorClientService;

@MappedSuperclass
public abstract class AbstractClientUtil {

	@Autowired
	private Environment environment;
	private Logger logger;
	@Autowired
	private LogErrorClientService logErrorClientService;

	public AbstractClientUtil(Logger logger) {
		this.logger = logger;
	}

	@Async
	public abstract void download(Client client);

	protected abstract String getLocalTempPath();

	protected abstract String getLocalBackupPath();

	public Environment getEnvironment() {
		return environment;
	}

	public Logger getLogger() {
		return logger;
	}

	public LogErrorClientService getLogErrorClientService() {
		return logErrorClientService;
	}
}
