package com.yonyou.plugins.security;

/**
 * Project: UMP Function Description:UM  Company: UFIDA Inc. UAP
 * Copyright: 2013 UFIDA Inc. All rights reserved.
 *
 * @author wangyup
 * @Date 2013-4-25 上午9:17:36
 * @version
 */

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class UMAesEncrypt extends AbstractEncrypt {
	private static final String AES_KEY = "12345678";
	private static final byte[] DES_IV = {(byte) 0x12, (byte) 0x34,
		  (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD,
		  (byte) 0xEF};
	private volatile static UMAesEncrypt inst = null;
	public String protocolName = "AES";
	private AlgorithmParameterSpec iv = null;
	private SecretKeySpec key = null;
	private Cipher cipher = null;
	
	UMAesEncrypt() throws Exception {
		
		KeyGenerator kgen = KeyGenerator.getInstance(protocolName);
		kgen.init(128, new SecureRandom(AES_KEY.getBytes()));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		key = new SecretKeySpec(enCodeFormat, "AES");
		cipher = Cipher.getInstance("AES");// 创建密码器
		
		// cipher = Cipher.getInstance("AES/ECB/NoPadding");
	}
	
	public static UMAesEncrypt getInstance() throws Exception {
		if (inst == null) {
			synchronized (UMAesEncrypt.class) {
				if (inst == null) {
					inst = new UMAesEncrypt();
				}
			}
		}
		return inst;
	}
	
	/**
	 * 将二进制转换成16进制
	 *
	 * @param buf
	 * @return
	 */
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	/**
	 * 将16进制转换为二进制
	 *
	 * @param hexStr
	 * @return
	 */
	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
				  16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
	
	@Override
	public synchronized byte[] encode(byte[] data) throws Exception {
		
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(data);
		return result; // 加密
	}
	
	@Override
	public synchronized byte[] decode(byte[] data) throws Exception {
		
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(data);
		return result; // 加密
	}
	
	@Override
	public String encode(String data) throws Exception {
		byte[] encryptResult = encode(data.getBytes("utf-8"));
		String encryptResultStr = parseByte2HexStr(encryptResult);
		return encryptResultStr;
	}
	
	@Override
	public String decode(String data) throws Exception {
		byte[] decryptFrom = parseHexStr2Byte(data);
		byte[] decryptResult = decode(decryptFrom);
		return new String(decryptResult);
	}
	
}
