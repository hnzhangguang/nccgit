package com.yonyou.plugins.security;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Project: UMP Function Description:UM  Company: UFIDA Inc. UAP
 * Copyright: 2013 UFIDA Inc. All rights reserved.
 *
 * @author wangyup
 * @Date 2013-4-25 上午9:17:36
 */


class UMDesEncrypt extends AbstractEncrypt {
	private static final String DES_KEY = "12345678";
	private static final byte[] DES_IV = {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90,
		  (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
	private static UMDesEncrypt inst = null;
	public String protocolName = "des";
	private AlgorithmParameterSpec iv = null;
	private SecretKey key = null;
	private Cipher cipher = null;
	
	UMDesEncrypt() throws Exception {
		DESKeySpec keySpec = new DESKeySpec(DES_KEY.getBytes("UTF-8"));
		iv = new IvParameterSpec(DES_IV);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		key = keyFactory.generateSecret(keySpec);
		cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	}
	
	public static UMDesEncrypt getInstance() throws Exception {
		if (inst == null) {
			inst = new UMDesEncrypt();
		}
		return inst;
	}
	
	@Override
	public synchronized byte[] encode(byte[] data) throws Exception {
		byte[] result = null;
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] pasByte = cipher.doFinal(data);
		result = Base64.encode(pasByte, Base64.DEFAULT);
		return result;
	}
	
	@Override
	public synchronized byte[] decode(byte[] data) throws Exception {
		byte[] encodeByte = Base64.decode(data, Base64.DEFAULT);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] pasByte = cipher.doFinal(encodeByte);
		return pasByte;
	}
}
