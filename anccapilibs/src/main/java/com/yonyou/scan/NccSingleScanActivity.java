package com.yonyou.scan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
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
import com.iqos.qrscanner.camera.CameraManager;
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
 * @功能: ncc 非连续扫码界面
 * @Date  2020/9/8 9:16 PM
 * @Author zhangg
 **/
public class NccSingleScanActivity extends QRScannerActivity implements SurfaceHolder.Callback {

    private YYWebView web;


    /*
     * @功能: 根据callbackName回调h5 js 函数
     * @参数:
     * @Date  2020/9/8 7:16 PM
     * @Author zhangg
     **/
    public void exeCallbackNameWebView(String callbackName) {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("aaa", 111);
            String data = jsonObject1.toString();
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("callbackName", callbackName);  // 添加回调方法
            data = jsonObject.toString();
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
        // 闪光灯的控制
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Camera camera = CameraManager.get().getCamera();


                camera.startPreview();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 开启
                camera.setParameters(parameters);
            }
        });
        findViewById(R.id.btnclose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = CameraManager.get().getCamera();
                if (camera != null) {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 关闭
                    camera.setParameters(parameters);
//					camera.stopPreview();
//					camera.release();
//					camera = null;
                }
            }
        });

        // 返回按钮
        findViewById(R.id.left_btn).setOnClickListener(new View.OnClickListener() {
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


        // 右按钮
        findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
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

        super.onDestroy();
    }


    /**
     * 处理扫描结果
     *
     * @param result 扫描的结果
     */
    public void handleDecode(Result result) {
        playBeepSoundAndVibrate();
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
                        Toast.makeText(NccSingleScanActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
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
                        String qrContent = QRCodeDecoder.syncDecodeQRCode(NccSingleScanActivity.this, uri);
                        if (TextUtils.isEmpty(qrContent)) {
                            Toast.makeText(NccSingleScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
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
