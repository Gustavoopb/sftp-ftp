package com.sftpFtp.entity.base;

import java.io.Serializable;

public abstract class BaseEntityComparator implements Serializable, IEntity {
	private static final long serialVersionUID = 6181140334280931596L;

	@Override
	public boolean isNew() {
		if (this.getId() == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(BaseEntityComparator... entity) {
		boolean empty = false;
		for (BaseEntityComparator baseEntity : entity) {
			if (baseEntity == null || baseEntity.getId() == null) {
				empty = true;
				break;
			}
		}
		return empty;
	}

	public static boolean isNotEmpty(BaseEntityComparator... entity) {
		return !BaseEntityComparator.isEmpty(entity);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		return hash += this.getId() != null ? this.getId().hashCode() : 0;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BaseEntityComparator)) {
			return false;
		}
		BaseEntityComparator other = (BaseEntityComparator) object;
		if (this.getId() == null && other.getId() != null
				|| this.getId() != null && !this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "[ID=" + this.getId() + "]";
	}
}
