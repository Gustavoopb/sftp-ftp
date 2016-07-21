package com.sftpFtp.enumeration;

public enum ProtocolType {
	SFTP, FTP;

	public static ProtocolType fromInt(Integer ordinal) {
		for (ProtocolType protocolType : ProtocolType.values()) {
			if (ordinal != null && ordinal.equals(protocolType.ordinal())) {
				return protocolType;
			}
		}
		return null;
	}
}
