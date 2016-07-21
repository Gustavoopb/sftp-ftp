package com.sftpFtp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sftpFtp.entity.base.BaseEntity;
import com.sftpFtp.enumeration.FtpEncryptionType;
import com.sftpFtp.enumeration.ProtocolType;

@Entity
@Table(name = "client")
@SequenceGenerator(name = "SEQUENCE", sequenceName = "client_id_seq")
public class Client extends BaseEntity {
	private static final long serialVersionUID = 7688525021238332515L;
	@Column(nullable = false)
	private String username;
	@Column(nullable = false, name = "user_password")
	private String userPassword;
	@Column(nullable = false)
	private String host;
	@Column(nullable = false)
	private String port;
	@Column(name = "remote_directory", nullable = false)
	private String remoteDirectory;
	@Column(nullable = false)
	private Boolean active;
	@Column(nullable = false, name = "local_directory")
	private String localDirectory;
	@Column(nullable = false, name = "protocol_type")
	@Enumerated(value = EnumType.ORDINAL)
	private ProtocolType protocolType;
	@Column(name = "ftp_encryption_type")
	@Enumerated(value = EnumType.ORDINAL)
	private FtpEncryptionType ftpEncryptionType;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getRemoteDirectory() {
		return this.remoteDirectory;
	}

	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getLocalDirectory() {
		return this.localDirectory;
	}

	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	public ProtocolType getProtocolType() {
		return this.protocolType;
	}

	public void setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
	}

	public FtpEncryptionType getFtpEncryptionType() {
		return this.ftpEncryptionType;
	}

	public void setFtpEncryptionType(FtpEncryptionType ftpEncryptionType) {
		this.ftpEncryptionType = ftpEncryptionType;
	}
}
