package com.yonyou.plugins.ucg;

import android.app.Activity;
import android.text.TextUtils;

import com.yonyou.plugins.ApiCallback;
import com.yonyou.plugins.MTLArgs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MTLUCGArgs extends MTLArgs {
	
	
	private static final String UPESNCODE = "upesncode";
	private static final String APPCODE = "appcode";
	private static final String AUTO_LOGIN = "autoLogin";
	private static final String HEADER_PARAMS = "headerParams";
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String _TOKEN = "token";
	private static final String ORIGIN = "origin";
	private static final String LANG_CODE = "langCode";
	Map<String, String> headers;
	private String defurl = "";
	private boolean autoLogin;
	private String ncctoken;
	private String token;
	private String langcode;
	private String appcode;
	private String upesncode;
	private String origin;
	
	
	public MTLUCGArgs(Activity ctx, String params, ApiCallback callback) {
		super(ctx, params, callback, false);
	}
	
	public static MTLUCGArgs build(MTLArgs args, String langcode, String appcode, String upesncode, String origin) {
		MTLUCGArgs item = new MTLUCGArgs(args.getContext(), args.getParams(), args.getCallback());
		
		String _appcode = item.getJSONObject().optString(APPCODE);
		if (TextUtils.isEmpty(_appcode)) {
			item.appcode = appcode;
			item.upesncode = upesncode;
			item.langcode = langcode;
			item.origin = origin;
		} else {
			item.appcode = item.getJSONObject().optString(APPCODE);
			item.upesncode = item.getJSONObject().optString(UPESNCODE);
			item.langcode = item.getJSONObject().optString(LANG_CODE);
		}
		
		
		return item;
	}
	
	
	public String getUrl() {
		return defurl;
	}
	
	public void setDefurl(String defurl) {
		this.defurl = defurl;
	}
	
	public boolean checkUrl() {
		if (TextUtils.isEmpty(defurl)) {
			super.error("url is null");
			return true;
		}
		return false;
	}
	
	public Map<String, String> getHeaders() {
		
		if (headers == null) {
			headers = new HashMap<>();
			if (!TextUtils.isEmpty(ncctoken))
				headers.put(ACCESS_TOKEN, ncctoken);
			if (!TextUtils.isEmpty(token))
				headers.put(_TOKEN, token);
		}
		if (jsonParam.has(HEADER_PARAMS)) {
			JSONObject param = jsonParam.optJSONObject(HEADER_PARAMS);
			if (param == null) {
				return headers;
			}
			Iterator iterator = param.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = param.optString(key);
				headers.put(key, value);
			}
		}
		
		
		return headers;
	}
	
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setNccToken(String ncctoken) {
		this.ncctoken = ncctoken;
	}
	
	public String getLangCode() {
		return langcode;
	}
	
	public void setLangCode(String langCode) {
		this.langcode = langCode;
	}
	
	public String getAppcode() {
		return appcode;
	}
	
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	
	public String getUpesncode() {
		return upesncode;
	}
	
	public void setUpesncode(String upesncode) {
		this.upesncode = upesncode;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getID_CODE() {
		return appcode + upesncode;
	}
	
	/**
	 * sysParamJson: {
	 * "busiaction": "查询模板",
	 * "appCode": "400400800",
	 * "langCode": "simpchn",
	 * "ts": 1557126277441
	 * <p>
	 * }
	 */
	public void setSysParamJson() {
		try {
			JSONObject object = new JSONObject();
			object.put("busiaction", "mobile-api-" + appcode);
			object.put("appCode", appcode);
			object.put("langCode", langcode);
			object.put("ts", System.currentTimeMillis());
			jsonParam.put("sysParamJson", object);
			setDefurl(jsonParam.getString("url") + "?appcode=" + appcode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
