package com.yonyou.plugins.security;

import java.nio.charset.Charset;


public abstract class AbstractEncrypt implements UMEncrypt {
	
	@Override
	public abstract byte[] encode(byte[] data) throws Exception;
	
	@Override
	public abstract byte[] decode(byte[] data) throws Exception;
	
	@Override
	public String encode(String data) throws Exception {
		return byteToString(encode(stringToByte(data)));
	}
	
	@Override
	public String decode(String data) throws Exception {
		return byteToString(decode(stringToByte(data)));
	}
	
	public String byteToString(byte[] source) {
		return byteToString(source, Charset.forName("UTF-8"));
	}
	
	public String byteToString(byte[] source, Charset cs) {
		return new String(source, cs);
	}
	
	public byte[] stringToByte(String source) {
		return stringToByte(source, Charset.forName("UTF-8"));
	}
	
	public byte[] stringToByte(String source, Charset cs) {
		return source.getBytes(cs);
	}
	
}
