package com.yonyou.common.utils.net;

import java.io.File;

/**
 * Created by lgy  on 2019/11/19
 */
public interface MTLHttpDownCallBack {
	
	/**
	 * 下载成功之后的文件
	 */
	void onDownloadSuccess(File file);
	
	/**
	 * 下载进度
	 */
	void onDownloading(int progress);
	
	/**
	 * 下载异常信息
	 */
	
	void onDownloadFailed(int code, String message);
	
}
