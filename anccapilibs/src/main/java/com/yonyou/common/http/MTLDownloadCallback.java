package com.yonyou.common.http;

/**
 * Created by zhengwb on 2017/3/25.
 */
public interface MTLDownloadCallback extends MTLUDACallback {

    void onBeforeDownload();

    void updateDownloading(boolean action, long size, long max);

    void onDownloaded(String file);
}
