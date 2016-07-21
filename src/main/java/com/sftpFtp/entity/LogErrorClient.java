package com.sftpFtp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sftpFtp.entity.base.BaseEntity;

@Entity
@Table(name = "log_error_client")
@SequenceGenerator(name = "SEQUENCE", sequenceName = "log_erro_client_id_seq")
public class LogErrorClient extends BaseEntity {
	private static final long serialVersionUID = 2324694109855953807L;
	@Column(name = "filename")
	private String filename;
	@Column(name = "date_log", nullable = false)
	private Date dateLog;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	@Column
	private String error;

	public LogErrorClient() {
	}

	public LogErrorClient(Client client, String filename, Exception e) {
		this();
		this.client = client;
		this.filename = filename;
		this.error = e.getMessage();
		this.dateLog = new Date();
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getDateLog() {
		return this.dateLog;
	}

	public void setDateLog(Date dateLog) {
		this.dateLog = dateLog;
	}

	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
