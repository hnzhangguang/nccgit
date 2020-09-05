package com.yonyou.plugins.http;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;

import com.yonyou.common.http.MTLUDACallback;
import com.yonyou.common.http.MTLUniversalHttpDataAccessor;
import com.yonyou.common.utils.DeviceUtil;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.net.MTLHttpCallBack;
import com.yonyou.common.utils.net.MTLHttpDownCallBack;
import com.yonyou.common.utils.net.MTLOKHttpUtils;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.system.FileControl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HttpControl {
	
	public static void request(final MTLArgs args) {
		if (!DeviceUtil.isNetworkConnected(args.getContext())) {
			args.error("请检查网络");
			return;
		}
		
		boolean okhttp = args.getBoolean("okhttp", false);
		if (okhttp) {
			requestOkHttp(args);
		} else {
			StringBuilder url = new StringBuilder(args.getString("url"));
			String method = args.getString("method");
			boolean uniCode = args.getBoolean("uniCode", false);
			int timeout = args.getInteger("timeout") == 0 ? 10000 : args.getInteger("timeout");
			String responseType = args.getString("responseType");
			JSONObject params = args.getJson("params");
			boolean isJsonRequestType = true;
			if (params != null) {
				url.append("?");
				Map<String, Object> paramsMap = toMap(params);
				for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
					Object value = entry.getValue();
					if (uniCode && value instanceof String) {//JSON字符串需要转义
						try {
							value = URLEncoder.encode((String) value, "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					url.append(entry.getKey()).append("=").append(value).append("&");
				}
				url = new StringBuilder(url.substring(0, url.length() - 1));
			}
			final String requestUrl = url.toString();
			
			// 请求参数json
			JSONObject ruquestParamDataJson = args.getJson("data");
			HashMap<String, Object> dataMap = new HashMap<>();
			if (ruquestParamDataJson != null) {
				dataMap = toMap(ruquestParamDataJson);
			} else {
				ruquestParamDataJson = new JSONObject();
			}
			
			// header 部分处理
			JSONObject headers = args.getJson("headers");
			headers = headers != null ? headers : args.getJson("header");
			HashMap<String, Object> headerMap = new HashMap<>();
			if (headers != null) {
				String type = TextUtils.isEmpty(headers.optString("content-type")) ? headers.optString("Content-Type") : headers.optString("content-type");
				if (!TextUtils.isEmpty(type) && type.contains("form")) {
					isJsonRequestType = false;
				}
				headerMap = toMap(headers);
			}
			x.Ext.init(args.getContext().getApplication());
			
			// 设置请求requestUrl eg: http://172.20.4.61:8080/console/login?__=4581559542825607
			MTLUniversalHttpDataAccessor accessor = (MTLUniversalHttpDataAccessor) MTLUniversalHttpDataAccessor.getInstance(args.getContext(), requestUrl);
			accessor.setTimeout(timeout);
			accessor.setReadTimeout(timeout);
			
			CookieManager cookieManager = CookieManager.getInstance();
			String cookie = cookieManager.getCookie(getDomain(requestUrl));
			if (!TextUtils.isEmpty(cookie)) {
				headerMap.put("Cookie", cookie);
			}
			if ("POST".equalsIgnoreCase(method)) {
				accessor.paramsJsonStrPost(headerMap, ruquestParamDataJson.toString(), isJsonRequestType, new MTLUDACallback() {
					@Override
					public void onResult(JSONObject data) {
						if (data != null) {
							setCookie(requestUrl);
							args.success(data);
						}
					}
					
					@Override
					public void onError(String message) {
						args.error(message);
					}
				});
			} else {
				accessor.paramsGet(headerMap, dataMap, isJsonRequestType, new MTLUDACallback() {
					@Override
					public void onResult(JSONObject data) {
						if (data != null) {
							setCookie(requestUrl);
							args.success(data);
						}
					}
					
					@Override
					public void onError(String message) {
						args.error(message);
					}
				});
			}
		}
	}
	
	private static HashMap<String, Object> toMap(JSONObject object) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Object value;
		String key;
		for (Iterator<?> it = object.keys(); it.hasNext(); ) {
			key = (String) it.next();
			if (object.isNull(key)) {
				map.put(key, null);
			} else {
				try {
					value = object.get(key);
					if (value instanceof JSONArray) {
						value = toList((JSONArray) value);
					} else if (value instanceof JSONObject) {
						value = toMap((JSONObject) value);
					}
					map.put(key, value);
				} catch (JSONException e) {
					MTLLog.e(e.getMessage(), e.toString());
				}
			}
		}
		return map;
	}
	
	private static List toList(JSONArray array) {
		List list = new ArrayList();
		Object value;
		for (int i = 0; i < array.length(); i++) {
			try {
				value = array.get(i);
				if (value instanceof JSONArray) {
					value = toList((JSONArray) value);
				} else if (value instanceof JSONObject) {
					value = toMap((JSONObject) value);
				}
				list.add(value);
			} catch (JSONException e) {
				MTLLog.e(e.getMessage(), e.toString());
			}
		}
		return list;
	}
	
	
	private static void requestOkHttp(MTLArgs args) {
		StringBuilder url = new StringBuilder(args.getString("url"));
		String method = args.getString("method");
		boolean uniCode = args.getBoolean("uniCode", false);
		int timeout = args.getInteger("timeout") == 0 ? 10000 : args.getInteger("timeout");
		String responseType = args.getString("responseType");
		JSONObject params = args.getJson("params");
		boolean isJsonRequestType = true;
		if (params != null) {
			url.append("?");
			Map<String, String> paramsMap = okhttpMap(params);
			for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
				Object value = entry.getValue();
				if (uniCode && value instanceof String) {//JSON字符串需要转义
					try {
						value = URLEncoder.encode((String) value, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				url.append(entry.getKey()).append("=").append(value).append("&");
			}
			url = new StringBuilder(url.substring(0, url.length() - 1));
		}
		String requestUrl = url.toString();
		JSONObject data = args.getJson("data");
		if (data == null) {
			data = new JSONObject();
		}
		JSONObject headers = args.getJson("headers");
		headers = headers != null ? headers : args.getJson("header");
		HashMap<String, String> headerMap = new HashMap<>();
		if (headers != null) {
//            String type = TextUtils.isEmpty(headers.optString("content-type")) ? headers.optString("Content-Type") : headers.optString("content-type");
//            if (!TextUtils.isEmpty(type) && type.contains("form")) {
//                isJsonRequestType = false;
//            }
			headerMap = okhttpMap(headers);
		}
		CookieManager cookieManager = CookieManager.getInstance();
		String cookie = cookieManager.getCookie(getDomain(requestUrl));
		if (!TextUtils.isEmpty(cookie)) {
			headerMap.put("Cookie", cookie);
		}
		if ("POST".equalsIgnoreCase(method)) {
			okHttpRequestPost(args, requestUrl, data, headerMap, timeout);
		} else {
			okHttpRequestGet(args, requestUrl, headerMap, timeout);
		}
	}
	
	private static void okHttpRequestPost(final MTLArgs args, final String requestUrl, final JSONObject data, HashMap<String, String> headerMap, int timeout) {
		
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		RequestBody requestBody = RequestBody.create(mediaType, data.toString());
//        FormBody requestBody = new FormBody.Builder()//TODO 表单格式需传参数判断
//                .add("opinion_subject", "android基础")
//                .add("bgzw", "好 吧 啦  啦")
//                .add("yjflbm", "0301")
//                .add("equipType", "app")
//                .add("tokenid", "VCdQ6tSucRMBIL366ZbYUBnTMQjw_Tsht7toTTiOTmPmxE_Gwzs8!-1963499658!1590495466670")
//                .build();
		MTLOKHttpUtils.post(requestUrl, requestBody, headerMap, timeout, new MTLHttpCallBack() {
			@Override
			public void onFailure(String error) {
				args.error(error);
			}
			
			@Override
			public void onResponse(int code, String res) {
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(res);
					if (code == 200) {
						args.success(jsonObject);
					} else {
						args.error(res);
					}
				} catch (JSONException e) {
					if (!TextUtils.isEmpty(res)) {
						JSONObject result = new JSONObject();
						try {
							result.put("data", res);
							if (code == 200) {
								args.success(result);
							} else {
								args.error(result.toString());
							}
						} catch (JSONException ex) {
							ex.printStackTrace();
						}
					}
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void okHttpRequestGet(final MTLArgs args, final String requestUrl, HashMap<String, String> headerMap, int timeout) {
		
		MTLOKHttpUtils.get(requestUrl, headerMap, timeout, new MTLHttpCallBack() {
			@Override
			public void onFailure(String error) {
				args.error(error);
			}
			
			@Override
			public void onResponse(int code, String res) {
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(res);
					if (code == 200) {
						args.success(jsonObject);
					} else {
						args.error(res);
					}
				} catch (JSONException e) {
					if (!TextUtils.isEmpty(res)) {
						JSONObject result = new JSONObject();
						try {
							result.put("data", res);
							if (code == 200) {
								args.success(result);
							} else {
								args.error(result.toString());
							}
						} catch (JSONException ex) {
							ex.printStackTrace();
						}
					}
					e.printStackTrace();
				}
			}
		});
	}
	
	private static HashMap<String, String> okhttpMap(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		String value;
		String key;
		for (Iterator<?> it = object.keys(); it.hasNext(); ) {
			key = (String) it.next();
			if (object.isNull(key)) {
				map.put(key, null);
			} else {
				try {
					value = String.valueOf(object.get(key));
					map.put(key, value);
				} catch (JSONException e) {
					MTLLog.e(e.getMessage(), e.toString());
				}
			}
		}
		return map;
	}
	
	public static void download(final MTLArgs args) {
		final Context context = args.getContext();
		String url = args.getString("url");
		String savePath = args.getString("savePath");
		String fileType = args.getString("fileType");
		String fileName = args.getString("fileName");
		final int autoPreview = args.getInteger("autoPreview", 0);
		boolean cover = args.getBoolean("cover", true);
		JSONObject headers = args.getJson("header");
		JSONObject form = args.getJson("formBody");
		JSONObject jsonBody = args.getJson("jsonBody");
		
		if (TextUtils.isEmpty(savePath)) {
			savePath = args.getContext().getExternalFilesDir("DIRECTORY_ALARMS").getPath() + "/downloadfile";
		}
		MTLOKHttpUtils.downLoadFile(url, savePath, fileName, fileType, cover, headers, form, jsonBody, new MTLHttpDownCallBack() {
			@Override
			public void onDownloadSuccess(File file) {
				if (file != null) {
					String path = file.getPath();
					if (autoPreview == 1) {
						FileControl.openFile(context, path);
					}
					args.success("filePath", path, false);
				} else {
					args.error("下载失败");
				}
			}
			
			@Override
			public void onDownloading(int progress) {
			
			}
			
			@Override
			public void onDownloadFailed(int code, String message) {
				args.error("下载失败:" + message);
			}
		});
	}
	
	
	private static void setCookie(String requestUrl) {
		CookieManager cookieManager = CookieManager.getInstance();
		DbCookieStore instance = DbCookieStore.INSTANCE;
		List<HttpCookie> cookies = instance.getCookies();
		for (HttpCookie cookie : cookies) {
			String value = cookie.getName() + "=" + cookie.getValue();
			cookieManager.setCookie(getDomain(requestUrl), value);
		}
	}
	
	private static String getDomain(String url) {
		url = url.replace("http://", "").replace("https://", "");
		if (url.contains("/")) {
			url = url.substring(0, url.indexOf('/'));
		}
		return url;
	}
	
}
