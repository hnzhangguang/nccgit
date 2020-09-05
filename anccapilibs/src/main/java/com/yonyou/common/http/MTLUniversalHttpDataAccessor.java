package com.yonyou.common.http;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.yonyou.common.utils.MTLLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;

/**
 * Created by xyy on 16/6/16.
 */
public class MTLUniversalHttpDataAccessor extends MTLUniversalDataAccessor {
	
	
	public static final String KEY_URL = "url";
	public String TAG = "YYUniversalHttpDataAccessor";
	private JSONObject mRequestConfigure = null;
	private String url;
	private Context mContext;
	private RequestParams result;
	private int mConnectTimeout = 0;
	private int mReadTimeout = 0;
	
	protected MTLUniversalHttpDataAccessor(Context ctx, String requestName,
	                                       JSONObject requestConfigure) {
		super(ctx, requestName, "");
		mRequestConfigure = requestConfigure;
		mContext = ctx;
	}
	
	public MTLUniversalHttpDataAccessor(Context ctx, String url) {
		super(ctx, "", "");
		setUrl(url);
		mContext = ctx;
	}
	
	private String getURL() {
		return url;
	}
	
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public void get(HashMap<String, ?> params, final MTLUDACallback callback) {
		x.http().get(getRequestParams(params), new SimpleResult(callback, url, mContext));
	}
	
	@Override
	public void headerParamsGet(HashMap<String, ?> params, boolean isJsonType,
	                            final MTLUDACallback callback) {
		x.http().get(getHeaderRequestParams(params, isJsonType), new SimpleResult(callback, url,
			  mContext));
	}
	
	@Override
	public void paramsPost(HashMap<String, ?> headerParams, HashMap<String, ?> bodyParams,
	                       boolean isJsonType, final MTLUDACallback callback) {
		x.http().post(postRequestParams(headerParams, bodyParams, isJsonType),
			  new SimpleResult(callback, url, mContext));
	}
	
	@Override
	public void paramsGet(HashMap<String, ?> headerParams, HashMap<String, ?> bodyParams,
	                      boolean isJsonType, MTLUDACallback callback) {
		x.http().get(getHeaderRequestParams(headerParams, bodyParams, isJsonType),
			  new SimpleResult(callback, url, mContext));
	}
	
	@Override
	public void paramsJsonPost(HashMap<String, ?> headerParams, String bodyJson,
	                           MTLUDACallback callback) {
		x.http().post(postRequestJsonParams(headerParams, bodyJson, true), new SimpleResult(callback,
			  url, mContext));
	}
	
	@Override
	public void paramsJsonStrPost(HashMap<String, ?> headerParams, String bodyJson, boolean isJsonType
		  , MTLUDACallback callback) {
		x.http().post(postRequestJsonParams(headerParams, bodyJson, isJsonType),
			  new SimpleResult(callback, url, mContext));
	}
	
	@Override
	public void post(HashMap<String, ?> params, MTLUDACallback callback) {
		x.http().post(getRequestParams(params), new SimpleResult(callback, url, mContext));
	}
	
	@Override
	public void upload(HashMap<String, ?> params, MTLUDACallback callback) {
		x.http().post(getUploadRequestParams(params, null), new SimpleResult(callback, url, mContext));
	}
	
	public void upload(HashMap<String, ?> headerParams, HashMap<String, ?> params,
	                   MTLUDACallback callback) {
		x.http().post(getUploadRequestParams(headerParams, params), new SimpleResult(callback, url,
			  mContext));
	}
	
	@Override
	public void formUpload(HashMap<String, ?> paramsHeader, HashMap<String, ?> params,
	                       MTLUDACallback callback) {
		x.http().post(getFormUploadRequestParams(paramsHeader, params), new SimpleResult(callback, url
			  , mContext));
	}
	
	@Override
	public void download(HashMap<String, ?> params, MTLUDACallback callback) {
		x.http().post(getDownloadRequestParams(params), new SimpleDownloadResult(callback, url,
			  mContext));
	}
	
	
	@NonNull
	private RequestParams getRequestParams(HashMap<String, ?> params) {
		result = new RequestParams(getURL());
		setSsl(result);
		for (String key : params.keySet()) {
			if (params.get(key) instanceof String)
				result.addBodyParameter(key, (String) params.get(key));
			if (params.get(key) instanceof File)
				result.addBodyParameter(key, (File) params.get(key));
		}
		if (mConnectTimeout > 0)
			result.setConnectTimeout(mConnectTimeout);
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		return result;
	}
	
	@NonNull
	private RequestParams getHeaderRequestParams(HashMap<String, ?> params, boolean isJsonType) {
		result = new RequestParams(getURL());
		setSsl(result);
		for (String key : params.keySet()) {
			if (params.get(key) instanceof String)
				result.addHeader(key, (String) params.get(key));
		}
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		if (isJsonType) {
			result.setAsJsonContent(true);
		}
		return result;
	}
	
	@NonNull
	private RequestParams getHeaderRequestParams(HashMap<String, ?> params,
	                                             HashMap<String, ?> bodyParams, boolean isJsonType) {
		result = new RequestParams(getURL());
		setSsl(result);
		for (String key : params.keySet()) {
			if (params.get(key) instanceof String) {
				if ("Cookie".equals(key)) {
					result.addHeader(key, (String) params.get(key));
				} else {
					result.addBodyParameter(key, (String) params.get(key));
				}
			}
			if (params.get(key) instanceof File) {
				result.addBodyParameter(key, (File) params.get(key));
			}
		}
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		if (isJsonType) {
			result.setAsJsonContent(false);
		}
		return result;
	}
	
	@NonNull
	private RequestParams postRequestParams(HashMap<String, ?> headerParams,
	                                        HashMap<String, ?> bodyParams, boolean isJsonType) {
		result = new RequestParams(getURL());
		setSsl(result);
		for (String key : headerParams.keySet()) {
			result.addHeader(key, (String) headerParams.get(key));
		}
		for (String key : bodyParams.keySet()) {
			if (key.contains("[]")) {
				List list = (List) bodyParams.get(key);
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						String mKey = key.substring(0, key.length() - 2);
						result.addBodyParameter(mKey, (String) list.get(i));
						if (list.size() == 1) {
							result.addBodyParameter(mKey, "");
						}
					}
				}
				
			} else {
				result.addBodyParameter(key, (String) bodyParams.get(key));
			}
		}
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (isJsonType) {
			result.setAsJsonContent(true);
		}
		return result;
	}
	
	@NonNull
	private RequestParams postRequestJsonParams(HashMap<String, ?> headerParams, String bodyJson,
	                                            boolean isJson) {
		result = new RequestParams(getURL());
		setSsl(result);
		for (String key : headerParams.keySet()) {
			result.addHeader(key, (String) headerParams.get(key));
		}
		if (!isJson) {
			try {
				HashMap<String, ?> bodyParams = toMap(new JSONObject(bodyJson));
				for (String key : bodyParams.keySet()) {
					if (key.contains("[]")) {
						List list = (List) bodyParams.get(key);
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								String mKey = key.substring(0, key.length() - 2);
								result.addBodyParameter(mKey, (String) list.get(i));
								if (list.size() == 1) {
									result.addBodyParameter(mKey, "");
								}
							}
						}
						
					} else {
						result.addBodyParameter(key, (String) bodyParams.get(key));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			result.setAsJsonContent(isJson);
			result.setBodyContent(bodyJson);
		}
		
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		return result;
	}
	
	@NonNull
	private RequestParams getUploadRequestParams(HashMap<String, ?> headerParams,
	                                             HashMap<String, ?> params) {
		RequestParams result = new RequestParams(getURL());
		List<KeyValue> list = new ArrayList<>();
		if (headerParams != null) {
			for (String key : headerParams.keySet()) {
				result.addHeader(key, (String) headerParams.get(key));
			}
		}
		for (String key : params.keySet()) {
			if (params.get(key) instanceof File) {
				list.add(new KeyValue(key, (File) params.get(key)));
			} else if (params.get(key) instanceof List) {
				list.add(new KeyValue(key, params.get(key)));
			} else {
				list.add(new KeyValue(key, params.get(key)));
			}
		}
		result.setAsJsonContent(true);
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		MultipartBody body = new MultipartBody(list, "UTF-8");
		result.setRequestBody(body);
		return result;
	}
	
	@NonNull
	private RequestParams getFormUploadRequestParams(HashMap<String, ?> headerParams, HashMap<String,
		  ?> params) {
		RequestParams result = new RequestParams(getURL());
		List<KeyValue> list = new ArrayList<>();
		if (headerParams != null) {
			for (String key : headerParams.keySet()) {
				result.addHeader(key, (String) headerParams.get(key));
			}
		}
		for (String key : params.keySet()) {
			if (params.get(key) instanceof String) {
				list.add(new KeyValue(key, (String) params.get(key)));
			}
			if (params.get(key) instanceof File) {
				list.add(new KeyValue(key, (File) params.get(key)));
			}
			if (params.get(key) instanceof List) {
				List<File> files = (List<File>) params.get(key);
				for (int i = 0; i < files.size(); i++) {
					File file = files.get(i);
					list.add(new KeyValue(key, file));
				}
			}
		}
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		MultipartBody body = new MultipartBody(list, "UTF-8");
		result.setRequestBody(body);
		result.setMultipart(true);
		return result;
	}
	
	private RequestParams getDownloadRequestParams(HashMap<String, ?> params) {
		RequestParams result = new RequestParams(getURL());
		for (String key : params.keySet()) {
			if (params.get(key) instanceof String) {
				result.setSaveFilePath((String) params.get(key));
			}
		}
		if (mConnectTimeout > 0) {
			result.setConnectTimeout(mConnectTimeout);
		}
		if (mReadTimeout > 0) {
			result.setReadTimeout(mReadTimeout);
		}
		return result;
	}
	
	public void setTimeout(int time) {
		mConnectTimeout = time;
	}
	
	public void setReadTimeout(int time) {
		mReadTimeout = time;
	}
	
	private void setSsl(RequestParams result) {
		
		if (!"https".equals(isHttp)) {
			return;
		}
		SSLContext sslContext = MTLSSLSocketFactoryY.getSSLContext();
		if (null == sslContext) {
			return;
		}
		result.setSslSocketFactory(sslContext.getSocketFactory()); // 设置ssl
	}
	
	private HashMap<String, Object> toMap(JSONObject object) {
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
	
	private List toList(JSONArray array) {
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
	
	private static class SimpleResult implements Callback.ProgressCallback<String> {
		
		private MTLUDACallback callback = null;
		private String error_code;
		private long response_size;
		private String error_msg;
		private String url;
		private Context context;
		
		public SimpleResult(MTLUDACallback cback, String url, Context context) {
			callback = cback;
			this.url = url;
			this.context = context;
		}
		
		
		@Override
		public void onStarted() {
		}
		
		@Override
		public void onLoading(long total, long current, boolean isDownloading) {
			response_size = total;
		}
		
		@Override
		public void onSuccess(String result) {
			
			try {
				error_code = "200";
				error_msg = "请求成功";
				if (result.equals("")) {
					callback.onResult(new JSONObject());
				} else {
					JSONObject jsonObject = new JSONObject(result);
					callback.onResult(jsonObject);
				}
			} catch (JSONException e) {
				try {
					JSONArray jsonArray = new JSONArray(result);
					callback.onResult(new JSONObject().put("data", jsonArray));
					return;
				} catch (JSONException e1) {
					try {
						callback.onResult(new JSONObject().put("data", result));
					} catch (JSONException ex) {
						MTLLog.d("yyhttpResult", ex.getMessage());
						ex.printStackTrace();
						MTLLog.d("yyhttpResult", e.getMessage());
						error_msg = "服务器返回格式异常";
						error_code = "404";
						callback.onError("错误码：" + error_code + "; 错误信息：" + error_msg);
					}
				}
			}
		}
		
		@Override
		public void onError(Throwable ex, boolean isOnCallback) {
			String result = "";
			if (ex != null) {
				if (ex instanceof HttpException) {
					error_code = ((HttpException) ex).getCode() + "";
					result = ((HttpException) ex).getResult();
				} else {
					error_code = "404";
				}
			} else {
				error_code = "404";
				error_msg = "服务器连接失败";
			}
			
			if (TextUtils.isEmpty(result)) {
				result = "错误码：" + error_code + "; 错误信息：" + ex.getMessage();
			}
			callback.onError(result);
		}
		
		@Override
		public void onCancelled(CancelledException cex) {
		}
		
		@Override
		public void onFinished() {
		}
		
		@Override
		public void onWaiting() {
		
		}
	}
	
	private static class SimpleDownloadResult implements Callback.ProgressCallback<File> {
		
		private MTLUDACallback callback = null;
		private String error_code;
		private long response_size;
		private String error_msg;
		private String url;
		private Context context;
		
		public SimpleDownloadResult(MTLUDACallback cback, String url, Context context) {
			callback = cback;
			this.url = url;
			this.context = context;
		}
		
		
		@Override
		public void onStarted() {
			((MTLDownloadCallback) callback).onBeforeDownload();
		}
		
		@Override
		public void onLoading(long total, long current, boolean isDownloading) {
			response_size = total;
			((MTLDownloadCallback) callback).updateDownloading(isDownloading, current, total);
		}
		
		@Override
		public void onSuccess(File result) {
			if (result != null) {
				error_code = "200";
				error_msg = "请求成功";
				((MTLDownloadCallback) callback).onDownloaded(result.toString());
			} else {
				((MTLDownloadCallback) callback).onDownloaded("");
			}
		}
		
		@Override
		public void onError(Throwable ex, boolean isOnCallback) {
			if (ex != null) {
				if (ex instanceof HttpException) {
					error_code = ((HttpException) ex).getCode() + "";
				} else {
					error_code = "404";
				}
				error_msg = ex.getMessage();
			} else {
				error_msg = "服务器连接失败";
				error_code = "404";
			}
			callback.onError(error_msg);
		}
		
		@Override
		public void onCancelled(CancelledException cex) {
		}
		
		@Override
		public void onFinished() {
		}
		
		@Override
		public void onWaiting() {
		
		}
	}
	
	private static class DownloadResult implements Callback.ProgressCallback<File> {
		
		private MTLDownloadWholeCallback callback = null;
		private String error_code;
		private long response_size;
		private String error_msg;
		private String url;
		private Context context;
		
		public DownloadResult(MTLDownloadWholeCallback cback, String url, Context context) {
			callback = cback;
			this.url = url;
			this.context = context;
		}
		
		
		@Override
		public void onStarted() {
		}
		
		@Override
		public void onLoading(long total, long current, boolean isDownloading) {
			response_size = total;
			callback.onLoading(total, current, isDownloading);
		}
		
		@Override
		public void onSuccess(File result) {
			if (result != null) {
				error_code = "200";
				error_msg = "请求成功";
				callback.onSuccess(result);
			} else {
				callback.onError("下载出错");
				error_code = "404";
				error_msg = "下载出错";
			}
		}
		
		@Override
		public void onError(Throwable ex, boolean isOnCallback) {
			if (ex != null) {
				if (ex instanceof HttpException) {
					error_code = ((HttpException) ex).getCode() + "";
				} else {
					error_code = "404";
				}
				error_msg = ex.getMessage();
			} else {
				error_msg = "服务器连接失败";
				error_code = "404";
			}
			callback.onError(error_msg);
		}
		
		@Override
		public void onCancelled(CancelledException cex) {
		}
		
		@Override
		public void onFinished() {
			callback.onFinished();
		}
		
		@Override
		public void onWaiting() {
		
		}
	}
}
