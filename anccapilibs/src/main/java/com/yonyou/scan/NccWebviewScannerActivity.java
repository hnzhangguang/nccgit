package com.yonyou.scan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.zxing.Result;
import com.iqos.qrscanner.app.QRScannerActivity;
import com.iqos.qrscanner.utils.QRCodeDecoder;
import com.yonyou.ancclibs.BuildConfig;
import com.yonyou.ancclibs.R;
import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.plugins.ExposedJsApi;
import com.yonyou.plugins.window.YYWebView;

import org.json.JSONObject;

import java.lang.reflect.Field;

/*
 * @功能: ncc 连续扫码界面
 * @Date  2020/9/8 9:14 PM
 * @Author zhangg
 **/
public class NccWebviewScannerActivity extends QRScannerActivity implements SurfaceHolder.Callback {
    private YYWebView web;

    /*
     * @功能: 根据callbackName回调h5 js 函数
     * @参数:
     * @Date  2020/9/8 7:16 PM
     * @Author zhangg
     **/
    public void exeCallbackNameWebViewForData(String callbackName, String dataString) {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("data", dataString);
            String data = jsonObject1.toString();
            // new  method
            String jsCode = String.format("%s(%s,%s)", "" + callbackName, "'" + callbackName + "'", data);
            jsCode = jsCode.replaceAll("%5C", "/");
            jsCode = jsCode.replaceAll("%0A", "");
            String script = "try{" + jsCode + "}catch(e){console.error(e)}";
            Log.e("mmmm", "script: " + script);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                web.evaluateJavascript(script, null);
            } else {
                web.loadUrl("javascript:" + script);
            }

        } catch (Exception e) {
            Log.e("mmmm", e.toString());
        }
    }


    /*
     * @功能: 根据callbackName回调h5 js 函数
     * @参数:
     * @Date  2020/9/8 7:16 PM
     * @Author zhangg
     **/
    public void exeCallbackNameWebView(String callbackName) {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("code", 0);
            String data = jsonObject1.toString();
            // new  method
            String jsCode = String.format("%s(%s,%s)", "" + callbackName, "'" + callbackName + "'", data);
            jsCode = jsCode.replaceAll("%5C", "/");
            jsCode = jsCode.replaceAll("%0A", "");
            String script = "try{" + jsCode + "}catch(e){console.error(e)}";
            Log.e("mmmm", "script: " + script);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                web.evaluateJavascript(script, null);
            } else {
                web.loadUrl("javascript:" + script);
            }

        } catch (Exception e) {
            Log.e("mmmm", e.toString());
        }
    }


    /**
     * 获取XML里面的控件
     */
    protected void findViews() {
        super.findViews();


        // 左按钮
        getBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String leftCallbackName = UserUtil.getValueByKey(Constant.leftbtncallbackNameKey);
                if (!TextUtils.isEmpty(leftCallbackName)) {
                    exeCallbackNameWebView(leftCallbackName);
                    // 清理事件
                    UserUtil.setKeyValue_gone(Constant.leftbtncallbackNameKey);
                } else {
                    finish();
                }
            }
        });
        //右按钮
        getTitleBar_right().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rightCallbackName = UserUtil.getValueByKey(Constant.rightbtncallbackNameKey);
                if (!TextUtils.isEmpty(rightCallbackName)) {
                    exeCallbackNameWebView(rightCallbackName);
                    // 清理事件
                    UserUtil.setKeyValue_gone(Constant.rightbtncallbackNameKey);
                } else {
                    finish();
                }
            }
        });


        FrameLayout framelayout = findViewById(R.id.framelayout);
        web = new YYWebView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web.setWebContentsDebuggingEnabled(true);
        }

        ExposedJsApi exposedJsApi = new ExposedJsApi(this, web);
        // 注入对象
//		web.addJavascriptInterface(exposedJsApi, "mtlBridge");
        web.addJavascriptInterface(exposedJsApi, "NativeBridge"); // 可以添加多个
        framelayout.addView(
                web, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        String url = "file:////android_asset/index.html";
        web.loadUrl(url);
    }


    @Override
    protected void onDestroy() {
        releaseWebView();


        // title
        UserUtil.setKeyValue_gone(Constant.titleNameKey);
        // 左按钮事件清除
        UserUtil.setKeyValue_gone(Constant.leftbtncallbackNameKey);
        // 右按钮事件清除
        UserUtil.setKeyValue_gone(Constant.rightbtncallbackNameKey);
        // 扫码回调事件清除
        UserUtil.setKeyValue_gone(Constant.scanCallbackKey);
        // 是否是连续
        UserUtil.setKeyValue_gone(Constant.isContinueScanKey);

        super.onDestroy();
    }


    /**
     * 处理扫描结果
     *
     * @param result 扫描的结果
     */
    public void handleDecode(Result result) {
        playBeepSoundAndVibrate();
        String scanString = result.getText();

        // 获取连续扫码,扫码到结果后的回调函数
        String valueByKey = UserUtil.getValueByKey(Constant.scanCallbackKey);
        if (!TextUtils.isEmpty(valueByKey)) {
            // 执行js回调函数
            exeCallbackNameWebViewForData(valueByKey, scanString);

        }

        // 判断是否是连续扫码
        String scanType = UserUtil.getValueByKey(Constant.isContinueScanKey);
        if (TextUtils.isEmpty(scanType) || "0".equals(scanType)) {  // 单次
            finish();
        } else if ("1".equals(scanType)) { // 连续扫码
//            restartQRScanner();
        }

        showResult(result.getText());
        /*inactivityTimer.onActivity();
        String resultStr = result.getText();
        Intent intent = new Intent();
        intent.putExtra(SCAN_RESULT, resultStr);
        this.setResult(SCAN_RESULT_CODE, intent);
        finish();*/
    }

    private void showResult(final String result) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("识别结果")
                .setMessage(result)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartQRScanner();
                    }
                })
                .setNegativeButton("复制", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 得到剪贴板管理器
                        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setPrimaryClip(ClipData.newPlainText("iqosjay@gmail.com", result));
                        Toast.makeText(NccWebviewScannerActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (2 == requestCode) {
            if (null == data) return;
            final Uri uri = data.getData();
            if (null != uri) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String qrContent = QRCodeDecoder.syncDecodeQRCode(NccWebviewScannerActivity.this, uri);
                        if (TextUtils.isEmpty(qrContent)) {
                            Toast.makeText(NccWebviewScannerActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                        } else {
                            showResult(qrContent);
                        }
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
