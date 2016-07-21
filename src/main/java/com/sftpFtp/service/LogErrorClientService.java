package com.sftpFtp.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.sftpFtp.entity.LogErrorClient;

@Service
public interface LogErrorClientService extends CrudRepository<LogErrorClient, Long> {
}
