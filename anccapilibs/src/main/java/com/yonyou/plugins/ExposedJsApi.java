/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.yonyou.plugins;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.yonyou.common.utils.MTLLog;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/*
 * @功能: 原生对象注入h5页面对象
 * @参数  ;
 * @Date  2020;
 * @Author zhangg
 **/
public class ExposedJsApi {
	private static final String TAG = "mmmmExposedJsApi";
	private static final String MTL_ANDROID = " mtlAndroid";
	
	private WeakReference<Activity> mCtx = null;
	private WeakReference<WebView> mWebView = null;
	private APIInvoker invoker;
	
	public ExposedJsApi(Activity _ctx, WebView _webView) {
		if (_ctx == null) {
			MTLLog.w(TAG, "ERROR - ctx is not activity");
		}
		mWebView = new WeakReference<WebView>(_webView);
		String ua = mWebView.get().getSettings().getUserAgentString();
		mWebView.get().getSettings().setUserAgentString(ua + MTL_ANDROID);
		mCtx = new WeakReference<Activity>(_ctx);
		invoker = new APIInvoker(_ctx);
	}
	
	
	@JavascriptInterface
	public String callSync(String name, String params) {
		try {
			return invoker.call(mCtx.get(), name, params, null, true);
		} catch (MTLException e) {
			e.printStackTrace();
		}
		return "error";
	}
	
	@JavascriptInterface
	public String call(String name, String params, String callback) {
		ApiCallback mtlCallback = new ApiCallback(this, callback);
		try {
//			String callbackId = mtlCallback.getCallbackId();
//			if (TextUtils.isEmpty(callbackId)) {
//				mtlCallback.setCallbackId(callback.replace("()", ""));
//			}
			invoker.call(mCtx.get(), name, params, mtlCallback, false);
		} catch (MTLException e) {
			mtlCallback.error(e.getMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	
	public void callback(final String method_callbackId, final JSONObject rs) {
		if (mCtx.get() == null) {
			return;
		}
		if (mWebView.get() == null) {
			return;
		}
		
		MTLLog.d("yyy", "execute callback  : " + method_callbackId);
		mCtx.get().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// old method
//				String callback = method.replace("()", "(" + frs + ")");
//				callback = callback.replaceAll("%5C", "/");
//				callback = callback.replaceAll("%0A", "");
//				String script = "try{" + callback + "}catch(e){console.error(e)}";
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//					mWebView.get().evaluateJavascript(script, null);
//				} else {
//					mWebView.get().loadUrl("javascript:" + script);
//				}
				
				try {
					String data = rs.toString();
					JSONObject jsonObject = new JSONObject(data);
					jsonObject.put("callbackId", method_callbackId);  // 添加回调方法
					data = jsonObject.toString();
					// new  method
					String jsCode = String.format("%s(%s,%s)", "window.JSSDK.receiveNativeMessage", "'" + method_callbackId + "'", data);
					jsCode = jsCode.replaceAll("%5C", "/");
					jsCode = jsCode.replaceAll("%0A", "");
					String script = "try{" + jsCode + "}catch(e){console.error(e)}";
//					Log.e(TAG, "script: " + script);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						mWebView.get().evaluateJavascript(script, null);
					} else {
						mWebView.get().loadUrl("javascript:" + script);
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		});
	}
}
