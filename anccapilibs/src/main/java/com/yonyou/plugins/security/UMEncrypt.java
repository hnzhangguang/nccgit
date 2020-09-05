package com.yonyou.plugins.security;

/**
 * Project: UMP Function Description:UM  Company: UFIDA Inc. UAP
 * Copyright: 2013 UFIDA Inc. All rights reserved.
 *
 * @author wangyup
 * @Date 2013-4-25 上午9:17:36
 */

public interface UMEncrypt {
	public byte[] encode(byte[] data) throws Exception;
	
	public byte[] decode(byte[] data) throws Exception;
	
	public String encode(String data) throws Exception;
	
	public String decode(String data) throws Exception;
}
