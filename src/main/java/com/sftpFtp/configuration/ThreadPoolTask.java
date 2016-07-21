package com.sftpFtp.configuration;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadPoolTask extends ThreadPoolTaskExecutor {
	private static final long serialVersionUID = 1038932558477714991L;
	private static ThreadPoolTask instance = null;

	public static ThreadPoolTaskExecutor getInstance() {
		if (instance == null) {
			instance = new ThreadPoolTask();
		}
		return instance;
	}

	public void shutdownThread() {
		do {
			try {
				Thread.sleep(3000);
				continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (ThreadPoolTask.getInstance().getActiveCount() != 0);
		ThreadPoolTask.getInstance().shutdown();
	}
}
