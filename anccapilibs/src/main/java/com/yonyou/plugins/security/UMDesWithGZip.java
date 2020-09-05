package com.yonyou.plugins.security;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Project: UMP Function Description:UM Company: UFIDA Inc. UAP Copyright: 2013
 * UFIDA Inc. All rights reserved.
 *
 * @author wangyup
 * @Date 2013-4-25 上午9:17:36
 */

class UMDesWithGZip extends AbstractEncrypt {
	private static final String DES_KEY = "12345678";
	private static final byte[] DES_IV = {(byte) 0x12, (byte) 0x34,
		  (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD,
		  (byte) 0xEF};
	static Charset cs = Charset.forName("UTF-8");
	private static UMDesWithGZip inst = null;
	public String protocolName = "deswithgzip";
	private AlgorithmParameterSpec iv = null;
	private SecretKey key = null;
	private Cipher cipher = null;
	
	UMDesWithGZip() throws Exception {
		DESKeySpec keySpec = new DESKeySpec(DES_KEY.getBytes("UTF-8"));
		iv = new IvParameterSpec(DES_IV);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		key = keyFactory.generateSecret(keySpec);
		cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	}
	
	public static UMDesWithGZip getInstance() throws Exception {
		if (inst == null) {
			inst = new UMDesWithGZip();
		}
		return inst;
	}
	
	@Override
	public synchronized byte[] encode(byte[] data) throws Exception {
		byte[] result = null;
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] pasByte = cipher.doFinal(data);
		result = Base64.encode(pasByte, Base64.DEFAULT);
		return UMGZipUtil.compressedDataWithData(result);
	}
	
	@Override
	public synchronized byte[] decode(byte[] data) throws Exception {
		byte[] zipdata = UMGZipUtil.dataWithCompressedData(data);
		byte[] encodeByte = Base64.decode(zipdata, Base64.DEFAULT);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] pasByte = cipher.doFinal(encodeByte);
		return pasByte;
	}
	
	@SuppressLint("NewApi")
	@Override
	public String encode(String data) throws Exception {
		return encode(data, false);
	}
	
	@SuppressLint("NewApi")
	public String encode(String data, boolean isDes) throws Exception {
		byte[] sbb = data.getBytes(cs);
		byte[] sbb2 = UMGZipUtil.compressedDataWithData(sbb);
		String rs = Base64.encodeToString(sbb2, Base64.DEFAULT);
		if (isDes) {
			UMEncrypt encrypt = UMProtocolManager.getEncryption("des");
			return encrypt.encode(rs);
		} else {
			return rs;
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public String decode(String data) throws Exception {
		return decode(data, false);
	}
	
	@SuppressLint("NewApi")
	public String decode(String data, boolean isDes) throws Exception {
		
		String src = "";
		if (isDes) {
			UMEncrypt encrypt = UMProtocolManager.getEncryption("des");
			src = encrypt.decode(data);
		} else {
			src = data;
		}
		byte[] sbb3 = Base64.decode(src, Base64.DEFAULT);
		byte[] sbb4 = UMGZipUtil.dataWithCompressedData(sbb3);
		return new String(sbb4, cs);
		
	}
}
