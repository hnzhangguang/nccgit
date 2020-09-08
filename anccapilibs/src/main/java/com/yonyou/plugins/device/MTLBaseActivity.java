package com.yonyou.plugins.device;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.yonyou.ancclibs.BuildConfig;
import com.yonyou.common.onActivityForResult.OnActivityForResultUtils;
import com.yonyou.plugins.ExposedJsApi;
import com.yonyou.plugins.window.YYWebView;

import java.lang.reflect.Field;

public class MTLBaseActivity extends AppCompatActivity {

    protected YYWebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("mmmm", this.getLocalClassName());
        super.onCreate(savedInstanceState);
        web = new YYWebView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web.setWebContentsDebuggingEnabled(true);
        }

        ExposedJsApi exposedJsApi = new ExposedJsApi(this, web);
        // 注入对象
//		web.addJavascriptInterface(exposedJsApi, "mtlBridge");
        web.addJavascriptInterface(exposedJsApi, "NativeBridge"); // 可以添加多个
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (EventApiInvoker.executeEvent(this.hashCode(),
                    EventApiInvoker.BACK_BUTTON)) {
                return true;
            }
            if (web != null && web.canGoBack()) {
                web.goBack();// 返回前一个页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        OnActivityForResultUtils.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /*
     * @功能: 如果h5注册了onresume回调方法则回调h5,否则不回调
     * @Date  2020/8/7;
     * @Author zhangg
     **/
    public void onResumeH5() {
        // method 为回调h5resume的方法名
        String method = "NCCresume()";
        if (!TextUtils.isEmpty(method)) {
            String callback = method.replace("()", "(" + "" + ")");
            callback = callback.replaceAll("%5C", "/");
            callback = callback.replaceAll("%0A", "");
            String script = "try{" + callback + "}catch(e){console.error(e)}";
            if (web != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    web.evaluateJavascript(script, null);
                } else {
                    web.loadUrl("javascript:" + script);
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//		onResumeH5();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public boolean webCanGoBack() {
        if (web != null) {
            return web.canGoBack();
        }
        return false;
    }

    public void webGoBack() {
        if (web != null) {
            web.goBack();
        }
    }


    @Override
    protected void onDestroy() {
        releaseWebView();
        super.onDestroy();
    }

    private void releaseWebView() {
        if (null != web) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;
            // 这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = web.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(web);
            }

            web.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            web.getSettings().setJavaScriptEnabled(false);
            web.clearHistory();
            web.clearView();
            web.removeAllViews();
            web.destroy();
            web = null;
        }
        releaseAllWebViewCallback();
    }

    public void releaseAllWebViewCallback() {
        if (android.os.Build.VERSION.SDK_INT < 16) {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Field sConfigCallback =
                        Class.forName("android.webkit.BrowserFrame").getDeclaredField(
                                "sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }
}
