package com.yonyou.plugins;

import android.app.Activity;

import com.yonyou.common.utils.ResourcesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MTLArgs {
	
	protected String params;
	protected String callbackId = "";
	protected String callbackName = "";
	protected ApiCallback callback;
	protected JSONObject jsonParam;
	private String TAG = "";
	private WeakReference<Activity> ctx = null;
	private boolean sync;
	
	public MTLArgs(Activity activity, String params, ApiCallback callback, boolean sync) {
		setParams(params);
		if (!sync) {
			this.callback = callback;
			if (jsonParam != null) {
				callbackName = jsonParam.optString("callbackName");
				this.callback.setCallbackName(callbackName);
				callbackId = jsonParam.optString("callbackId");
				this.callback.setCallbackId(callbackId);
			}
		}
		this.sync = sync;
		ctx = new WeakReference(activity);
	}
	
	public void success(String key, Object value, boolean keepCallback) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		success(jsonObject, keepCallback);
	}
	
	public void success() {
		success("", false);
	}
	
	public void success(Object data) {
		success(data, false);
	}
	
	public void success(Object data, boolean keepCallback) {
		if (sync) {
			return;
		}
		getCallback().success(data, keepCallback, callbackId, callbackName);
	}
	
	public void success(Object data, boolean keepCallback, String callbackId) {
		if (sync) {
			return;
		}
		getCallback().success(data, keepCallback, callbackId, callbackName);
	}
	
	public void success(Object data, boolean keepCallback, String callbackId, String callbackName) {
		if (sync) {
			return;
		}
		getCallback().success(data, keepCallback, callbackId, callbackName);
	}
	
	public void error(String code, String message) {
		error(code, message, false);
		
	}
	
	public void error(String message) {
		error(message, false);
	}
	
	public void error(int id) {
		error(ResourcesUtils.getStringResourse(ctx.get(), id), false);
	}
	
	
	public void error(String code, String message, boolean keepCallback) {
		if (sync) {
			return;
		}
		getCallback().error(code, message, keepCallback);
	}
	
	public void error(String message, boolean keepCallback) {
		if (sync) {
			return;
		}
		getCallback().error(message, keepCallback);
	}
	
	
	public String getParams() {
		return params;
	}
	
	public void setParams(String params) {
		try {
			jsonParam = new JSONObject(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.params = params;
	}
	
	public void setHeaderParams(String params) {
		try {
			jsonParam = new JSONObject(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ApiCallback getCallback() {
		return callback;
	}
	
	public void setCallback(ApiCallback callback) {
		this.callback = callback;
	}
	
	public Activity getContext() {
		return ctx.get();
	}
	
	public String getString(String key) {
		return getJSONObject().optString(key);
	}
	
	public String getString(String key, String fallback) {
		return getJSONObject().optString(key, fallback);
	}
	
	public Object getObject(String key) {
		return getJSONObject().opt(key);
	}
	
	public JSONObject getJSONObject() {
		return jsonParam;
	}
	
	public boolean getBoolean(String key) {
		return getJSONObject().optBoolean(key);
	}
	
	public boolean getBoolean(String key, boolean defValue) {
		return getJSONObject().optBoolean(key, defValue);
	}
	
	public double getDouble(String key) {
		return getJSONObject().optDouble(key);
	}
	
	public int getInteger(String key) {
		return getJSONObject().optInt(key);
	}
	
	public int getInteger(String key, int defaultValue) {
		return getJSONObject().optInt(key, defaultValue);
	}
	
	public JSONArray getJsonArray(String key) {
		JSONArray v = getJSONObject().optJSONArray(key);
		return v;
	}
	
	public JSONObject getJson(String key) {
		JSONObject v = getJSONObject().optJSONObject(key);
		return v;
	}
	
	
	public String getTAG() {
		return TAG;
	}
	
	public void setTAG(String TAG) {
		this.TAG = TAG;
	}
}
