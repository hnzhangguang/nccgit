package com.yonyou.plugins;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncArgs {
	
	protected String params;
	protected JSONObject jsonParam;
	private String TAG = "";
	
	public SyncArgs(String params) {
		setParams(params);
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
	
	public String getString(String key) {
		return getJSONObject().optString(key);
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
}
