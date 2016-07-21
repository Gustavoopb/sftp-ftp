package com.sftpFtp.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.sftpFtp.entity.Client;

@Service
public interface ClientService extends CrudRepository<Client, Long> {
}
