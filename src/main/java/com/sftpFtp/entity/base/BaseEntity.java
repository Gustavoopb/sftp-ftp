package com.sftpFtp.entity.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity extends BaseEntityComparator {
	private static final long serialVersionUID = -3336043433768035460L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQUENCE")
	private Long id;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setId(Object id) {
		this.id = id != null ? Long.valueOf(Long.parseLong(id.toString())) : null;
	}

	@Override
	public boolean isNew() {
		if (this.getId() == null) {
			return true;
		}
		return false;
	}
}
