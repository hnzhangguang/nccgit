package com.yonyou.plugins;

import android.text.TextUtils;
import android.util.Log;

import com.yonyou.common.utils.logs.LogerNcc;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xyy  on 2019/4/29
 */
public final class ApiCallback {

    private static final String SUCCESS = "0";
    private static final String ERROR = "1";
    private String callback = "";
    private String callbackId = "";
    private String callbackName = "";
    private ExposedJsApi jsApi;

    public ApiCallback(ExposedJsApi jsApi, String callback) {
        this.jsApi = jsApi;
        this.callback = callback;
    }

    public void success(Object data) {
        success(data, false, callbackId, callbackName);
    }

    public void success(Object data, boolean keepCallback, String callbackId, String callbackName) {
        JSONObject rs = new JSONObject();
        try {
            if (!TextUtils.isEmpty(callbackName)) {
                rs.put("callbackName", callbackName);
            }
            rs.put("code", SUCCESS);
            rs.put("data", data);
            rs.put("keepCallback", keepCallback);
            rs.put("callbackId", callbackId);
            jsApi.callback(callback, rs);
        } catch (JSONException e) {
            LogerNcc.e(e);
            e.printStackTrace();
        }
    }

    public void error(String code, String message) {
        error(code, message, false);
    }

    public void error(String code, String message, boolean keepCallback) {
        JSONObject rs = new JSONObject();
        try {
            rs.put("code", code);
            rs.put("msg", message);
            rs.put("keepCallback", keepCallback);
            rs.put("callbackId", callbackId);
            rs.put("callbackName", callbackName);
            jsApi.callback(callback, rs);
        } catch (JSONException e) {
            Log.e("mmmm", "error: " + e);
            e.printStackTrace();
        }
    }

    public void error(String message) {
        error(message, false);
    }

    public void error(String message, boolean keepCallback) {
        error(ERROR, message, keepCallback);
    }


    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }


}
