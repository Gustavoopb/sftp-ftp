package com.sftpFtp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sftpFtp.configuration.ThreadPoolTask;

@EnableAsync
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = { "com.sftpFtp" })
public class Application {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		ThreadPoolTask taskPool = (ThreadPoolTask) context.getBean("TaskThread");
		taskPool.shutdownThread();
	}
}
