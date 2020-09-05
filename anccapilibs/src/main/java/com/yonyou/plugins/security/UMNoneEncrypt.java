package com.yonyou.plugins.security;

class UMNoneEncrypt extends AbstractEncrypt {
	
	@Override
	public byte[] encode(byte[] data) throws Exception {
		return data;
	}
	
	@Override
	public byte[] decode(byte[] data) throws Exception {
		return data;
	}
	
}
