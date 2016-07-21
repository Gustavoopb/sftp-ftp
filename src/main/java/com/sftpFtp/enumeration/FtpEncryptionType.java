package com.sftpFtp.enumeration;

public enum FtpEncryptionType {
	NO_ENCRYPTION, IMPLICIT_ENCRYPTION, EXPLICIT_ENCRYPTION;

	public static FtpEncryptionType fromInt(Integer ordinal) {
		for (FtpEncryptionType ftpEncryptionType : FtpEncryptionType.values()) {
			if (ordinal != null && ordinal.equals(ftpEncryptionType.ordinal())) {
				return ftpEncryptionType;
			}
		}
		return null;
	}
}
