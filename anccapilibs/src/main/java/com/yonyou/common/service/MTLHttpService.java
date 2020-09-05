package com.yonyou.common.service;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.yonyou.common.callback.MTLCallback;
import com.yonyou.common.http.MTLDownloadCallback;
import com.yonyou.common.http.MTLUDACallback;
import com.yonyou.common.http.MTLUniversalHttpDataAccessor;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;

public class MTLHttpService {
    private Context mContext;

    private MTLHttpService() {
    }

    public MTLHttpService(Application application, Context context) {
        this.mContext = context;
        x.Ext.init(application);
    }

    public void uploadFile(String url, File file, final MTLCallback callback) {
        MTLUniversalHttpDataAccessor accessor = (MTLUniversalHttpDataAccessor) MTLUniversalHttpDataAccessor.getInstance(mContext, url);
        HashMap<String, Object> headerParams = new HashMap<>();
        HashMap<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("file", file);
        accessor.upload(headerParams, bodyParams, new MTLUDACallback() {
            @Override
            public void onResult(JSONObject jsonObject) {
                callback.onResult(jsonObject);
            }

            @Override
            public void onError(String s) {
                callback.onError(s);
            }
        });
    }

    public void downloadFile(String url, final MTLCallback callback) {
        MTLUniversalHttpDataAccessor accessor = (MTLUniversalHttpDataAccessor) MTLUniversalHttpDataAccessor.getInstance(mContext, url);
        HashMap<String, Object> bodyParams = new HashMap<>();
        accessor.download(bodyParams, new MTLDownloadCallback() {
            @Override
            public void onBeforeDownload() {

            }

            @Override
            public void updateDownloading(boolean action, long size, long max) {

            }

            @Override
            public void onDownloaded(String file) {
                if (!TextUtils.isEmpty(file)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("path", file);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    callback.onResult(jsonObject);
                } else {
                    callback.onError("下载失败");
                }
            }

            @Override
            public void onResult(JSONObject jsonObject) {
            }

            @Override
            public void onError(String s) {
                callback.onError(s);
            }
        });
    }

}
