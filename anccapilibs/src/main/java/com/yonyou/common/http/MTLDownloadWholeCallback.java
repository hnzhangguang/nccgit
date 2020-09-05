package com.yonyou.common.http;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by zhengwb on 2017/6/1.
 */
public interface MTLDownloadWholeCallback extends MTLUDACallback {

    void onSuccess(File result);

    void onFinished();

    void onWaiting();

    void onStarted();

    void onCancelled(Callback.CancelledException cex);

    void onLoading(long total, long current, boolean isDownloading);
}
