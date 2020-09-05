package com.yonyou.plugins.ucg;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yonyou.common.utils.MTLLog;
import com.yonyou.plugins.MTLArgs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by yanglin  on 2019/4/29
 */
public class MTLServiceConfig {
	
	public static final String MTL_MASERVER_CONFIG = "mtl-maserver-config";
	private static final String APPCODE = "appcode";
	private static final String CONFIG = "config";
	private static final String SUCCESS = "success";
	private static final String HOST = "host";
	private static final String PORT = "port";
	private static final String ISHTTPS = "isHttps";
	private static final String DEFAULT_TP = "default_tp";
	
	private static HashMap<String, JSONObject> mCache = new HashMap<String, JSONObject>();
	;
	
	public static JSONObject setConfig(MTLUCGArgs args) {
		String appId = args.getAppcode();
		JSONObject config = args.getJSONObject().optJSONObject(CONFIG);
		
		if (TextUtils.isEmpty(appId) || config == null) {
			return null;
		}
		mCache.put(appId, config);
		Context context = args.getContext();
		setValue(context, appId, config.toString());
		return config;
	}
	
	private static void setValue(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(MTL_MASERVER_CONFIG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * @param args
	 * @return
	 */
	public static JSONObject getConfig(MTLUCGArgs args) throws JSONException {
		String appId = args.getAppcode();
		if (TextUtils.isEmpty(appId)) {
			args.error("没有配置");
			return new JSONObject();
		}
		String str = getValue(args.getContext(), appId);
		JSONObject config = new JSONObject(str);
		mCache.put(appId, config);
		return config;
	}
	
	/**
	 * @return
	 */
	public static JSONObject getConfig(Context context, String appCode) throws JSONException {
		if (TextUtils.isEmpty(appCode)) {
			return new JSONObject();
		}
		String str = getValue(context, appCode);
		JSONObject config = new JSONObject(str);
		mCache.put(appCode, config);
		return config;
	}
	
	private static String getValue(Context context, String appId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(MTL_MASERVER_CONFIG, Context.MODE_PRIVATE);
		return sharedPreferences.getString(appId, "");
	}
	
	public static String getDefAppId(MTLArgs args) {
		String appid = getValue(args.getContext(), APPCODE);
		return appid;
	}
	
	public static String getHost(MTLUCGArgs args) {
		String appid = args.getAppcode();
		JSONObject config = mCache.get(appid);
		if (config != null) {
		
		} else {
			try {
				config = getConfig(args);
			} catch (JSONException e) {
				e.printStackTrace();
				return "";
			}
		}
		MTLLog.v("mtlservicecconfig", "config: " + config.toString());
		return config.optString(HOST);
	}
	
	public static String getPort(MTLUCGArgs args) {
		String appid = args.getAppcode();
		JSONObject config = mCache.get(appid);
		if (config != null) {
			return config.optString(PORT);
		}
		return null;
	}
	
	public static boolean isHttp(MTLUCGArgs args) {
		String appid = args.getAppcode();
		JSONObject config = mCache.get(appid);
		if (config != null) {
			return config.optBoolean(ISHTTPS);
		}
		return false;
	}
	
	public static JSONObject getTokenData(MTLUCGArgs args) {
		String code = args.getID_CODE();
		if (mCache.get(code) != null) {
			return mCache.get(code);
		}
		try {
			return new JSONObject(getValue(args.getContext(), code));
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
	
	public static String getToken(MTLUCGArgs mtlucgArgs) {
		JSONObject item = getTokenData(mtlucgArgs);
		return item.optString("token");
	}
	
	public static String getNccToken(MTLUCGArgs mtlucgArgs) {
		JSONObject item = getTokenData(mtlucgArgs);
		return item.optString("ncctoken");
	}
}
