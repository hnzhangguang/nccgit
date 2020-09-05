package com.yonyou.common.http;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 加载Demo数据，从Asset/demoData/目录下
 * <p/>
 * Created by xyy on 16/6/16.
 */
public class MTLUniversalDemoDataAccessor extends MTLUniversalDataAccessor {


    public static final String DEMO_DATA_PATH = "demoData/%s.json";
    public static final String ERR_INFO_CTX_ISNULL = "Activity is null";
    public static final String TAG_LOAD_DATA_FROM_CLASS = "loadDataFromClass";
    public static final String TAG_CLASS = "class";

    public MTLUniversalDemoDataAccessor(Activity ctx, String requestName, String modular) {
        super(ctx, requestName, modular);
    }

    @Override
    public void setUrl(String url) {

    }

    @Override
    public void get(HashMap<String, ?> params, final MTLUDACallback callback) {
        String file = String.format(DEMO_DATA_PATH, reqComplete);
        try {
            final Context ctx = getContext();
            if (ctx == null) {
                callback.onError(ERR_INFO_CTX_ISNULL);
                return;
            }
            InputStream reader = getContext().getAssets().open(file);
            MTLUDARequest request = MTLUDARequest.obtain(reqComplete, params);
            DemoDataLoader loader = new DemoDataLoader(reader, request, new MTLUDACallback() {
                @Override
                public void onResult(final JSONObject data) {
                    if (ctx instanceof Activity) {
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(data);
                            }
                        });
                    } else {
                        callback.onResult(data);
                    }
                }

                @Override
                public void onError(final String message) {
                    if (ctx instanceof Activity) {
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(message);
                            }
                        });
                    } else {
                        callback.onError(message);
                    }
                }
            });
            new Thread(loader).start();
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }



    @Override
    public void post(HashMap<String, ?> params, MTLUDACallback callback) {
        get(params, callback);
    }

    @Override
    public void upload(HashMap<String, ?> params, MTLUDACallback callback) {

    }

    @Override
    public void upload(HashMap<String, ?> params, HashMap<String, ?> paramsHeader, MTLUDACallback callback) {

    }

    @Override
    public void formUpload(HashMap<String, ?> paramsHeader, HashMap<String, ?> params, MTLUDACallback callback) {

    }

    @Override
    public void download(HashMap<String, ?> params, MTLUDACallback callback) {

    }

    @Override
    public void headerParamsGet(HashMap<String, ?> params, boolean isJsonType, MTLUDACallback callback) {

    }

    @Override
    public void paramsPost(HashMap<String, ?> headerParams, HashMap<String, ?> bodyParams, boolean isJsonType, MTLUDACallback callback) {

    }

    @Override
    public void paramsGet(HashMap<String, ?> headerParams, HashMap<String, ?> bodyParams, boolean isJsonType, MTLUDACallback callback) {

    }

    @Override
    public void paramsJsonPost(HashMap<String, ?> headerParams, String bodyJson, MTLUDACallback callback) {

    }

    @Override
    public void paramsJsonStrPost(HashMap<String, ?> headerParams, String bodyJson, boolean isJsonType, MTLUDACallback callback) {

    }

    private class DemoDataLoader implements Runnable {

        private InputStream _reader = null;
        private MTLUDACallback _callback = null;
        private MTLUDARequest _request = null;

        public DemoDataLoader(InputStream reader, MTLUDARequest request, MTLUDACallback callback) {
            _reader = reader;
            _callback = callback;
            _request = request;
        }

        @Override
        public void run() {
            StringBuilder result = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(_reader, CS_UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                _reader.close();

                JSONObject data = new JSONObject(result.toString());
                boolean isClassLoader = data.optBoolean(TAG_LOAD_DATA_FROM_CLASS, false);
                if (isClassLoader) {
                    String className = data.optString(TAG_CLASS);
                    if (!TextUtils.isEmpty(className)) {
                        Object classLoader = Class.forName(className).newInstance();
                        if (classLoader instanceof MTLUDADemoLoader) {
                            data = ((MTLUDADemoLoader) classLoader).getData(_request.getRequestName(), _request.getParameter());
                        }
                    }
                }
                _callback.onResult(data);
            } catch (Exception e) {
                _callback.onError(e.getMessage());
            }

        }
    }
}
