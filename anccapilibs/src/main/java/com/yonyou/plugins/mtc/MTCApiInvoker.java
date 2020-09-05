package com.yonyou.plugins.mtc;

/**
 * Created by yanglin  on 2019/5/5
 */


import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

/**
 * Multi terminal collaboration
 * 多端协同
 */
public class MTCApiInvoker implements com.yonyou.plugins.IApiInvoker {
	
	/**
	 * 投屏图片
	 */
	private static final String SCREEN_IMAGE = "screenImage";
	/**
	 * 投屏视频文件
	 */
	private static final String SCREEN_URL = "screenUrl";
	/**
	 * 投屏网址
	 */
	private static final String OPEN_SCREEN_PANEL = "openScreenPanel";
	/**
	 * 打开触控板
	 */
	private static final String SCREEN_VIDEO = "screenVideo";
	
	@Override
	public String call(String apiname, MTLArgs args) throws MTLException {
		switch (apiname) {
			case SCREEN_IMAGE:
				break;
			case SCREEN_VIDEO:
				break;
			case SCREEN_URL:
				break;
			case OPEN_SCREEN_PANEL:
				break;
		}
		throw new MTLException(apiname + ": function not found");
	}
}
