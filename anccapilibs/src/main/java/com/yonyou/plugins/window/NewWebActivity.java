package com.yonyou.plugins.window;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.yonyou.ancclibs.R;
import com.yonyou.plugins.device.MTLBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * 加载Url + 加载补丁h5
 */
public class NewWebActivity extends MTLBaseActivity {
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_web);
		FrameLayout parent_layout = findViewById(R.id.new_web_parent_layout);
		parent_layout.addView(web, FrameLayout.LayoutParams.MATCH_PARENT,
			  FrameLayout.LayoutParams.MATCH_PARENT);
		String url = getIntent().getStringExtra("url");
		if (url.startsWith("http://") || url.startsWith("https://")) {
			web.loadUrl(url);
		} else {
			String parameters = getIntent().getStringExtra("parameters");
			if (!TextUtils.isEmpty(parameters)) {
				try {
					JSONObject jsonObject = new JSONObject(parameters);
					Iterator<String> it = jsonObject.keys();
					while (it.hasNext()) {
						// 获得key
						String key = it.next();
						String value = jsonObject.optString(key);
						url = url + "?" + key + "=" + value;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			web.loadUrl("file:///android_asset/www/" + url);
		}
		
		web.setWebContentsDebuggingEnabled(true);
		//        web.addJavascriptInterface(new MTLExposedJsApi(this, web), "mtlBridge");
	}
	
	//    @Override
	//    public boolean onKeyDown(int keyCode, KeyEvent event) {
	//        if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
	//            web.goBack();// 返回前一个页面
	//            return true;
	//        }
	//        return super.onKeyDown(keyCode, event);
	//    }
}
