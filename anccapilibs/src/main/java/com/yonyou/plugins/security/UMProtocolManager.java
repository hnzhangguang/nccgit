package com.yonyou.plugins.security;

import java.util.HashMap;


public class UMProtocolManager {
	
	public static final String NONE = "none";
	public static final String GZIP = "des_gzip";
	public static final String DES = "des";
	public static final String AES = "aes";
	static HashMap<String, UMEncrypt> encryptions = new HashMap<String, UMEncrypt>();
	
	static {
		try {
			encryptions.put(DES, new UMDesEncrypt());
			encryptions.put(AES, new UMAesEncrypt());
			encryptions.put(GZIP, new UMDesWithGZip());
			encryptions.put(NONE, new UMNoneEncrypt());
		} catch (Exception e) {
			throw new Error(e);
		}
	}
	
	public static HashMap<String, UMEncrypt> getEncryptions() {
		return encryptions;
	}
	
	public static UMEncrypt getEncryption(String encryptiontype) {
		return encryptions.get(encryptiontype);
	}
	
}
