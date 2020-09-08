package com.yonyou.nccmob;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.plugins.device.MTLBaseActivity;

import org.json.JSONObject;

public class NCCOpenH5MainActivity extends MTLBaseActivity {

    public static final String PREVIEW_URL = "preview_url";
    public static final String DEBUGGENABLED = "DEBUGGENABLED";
    public static final String PREVIEW_STATUS_BAR = "preview_status_bar";
    boolean defDebugg;
    EditText edit;
    private String url = "";

    public String getEdit() {
        return edit.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openh5_main);
        edit = findViewById(R.id.edit);
        findViewById(R.id.btn)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String edit = getEdit();
                                if (TextUtils.isEmpty(edit)) {
                                    Toast.makeText(NCCOpenH5MainActivity.this, "你有病是不输入内容就像访问...", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (edit.startsWith("http") || edit.startsWith("https")) {
                                        web.loadUrl(getEdit());
                                    } else {
                                        Toast.makeText(NCCOpenH5MainActivity.this, "输入的协议不对...", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        });

        findViewById(R.id.btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exeCallbackNameWebView("leftcallbackmethod");
            }
        });
        findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        url = getIntent().getStringExtra(PREVIEW_URL);
        if (!TextUtils.isEmpty(url) && url.startsWith("file://")) {
            if (!url.contains("android_asset")) {
                // url = url + "/index.html";
            }
        }
        //    url = "file://data/data/com.yonyou.nccmob/files/appid/1/index.html";
        //    url = "file:///data/user/0/com.yonyou.nccmob/files/appid/1/index.html";
        if (null == url || url.isEmpty()) {
            url = "https://www.baidu.com";
            url = "file:////android_asset/index.html";
        }
//        url = "http://10.6.232.223:8080/?timestamp" + new Date().getTime(); // 注入对象形式
//        url = "file:////android_asset/index.html";
//		url = "http://10.6.225.37:8080";
        defDebugg = getIntent().getBooleanExtra(DEBUGGENABLED, true);
        if (TextUtils.isEmpty(url)) {
            this.finish();
        }
        // 加载本地文件
        if (!url.startsWith("file://") && !url.startsWith("http")) {
            url = "file:///" + url;
        }
//        url = "http://10.11.115.72/ncc_mobile/mobile_pu/app/purchaser/main/index.html?appcode=400400830&code=bce0e43197b6f692ae50b93039023818286c8f20d959a9bac3b85d7cf5ef&fromType=upEsnApp&appId=1253&openAppId=0&qzId=187306&openTenantId=af8p63m4&ykj_req=1598954899977#/receiving/history";

        // 获取根布局
        FrameLayout webViewParent = findViewById(R.id.webView_parent);
        webViewParent.addView(
                web, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // 解决不是http:// , https:// 开头的网址不显示问题
//		web.setWebChromeClient(new WebChromeClient());  // yywebview里面已经有了
//        String userAgentString = web.getSettings().getUserAgentString();
//        LogerNcc.e(userAgentString);
        web.loadUrl(url);

    }


    @Override
    protected void onResume() {

        super.onResume();

        String resumecallbackName = UserUtil.getValueByKey(Constant.resumecallbackName);
        if (!TextUtils.isEmpty(resumecallbackName)) {
            exeCallbackIDWebView(resumecallbackName);
            UserUtil.setKeyValue_gone(Constant.resumecallbackName); // 用完即销毁
        }


    }

    @Override
    protected void onDestroy() {

        // 页面关闭的时候,清空注册的所有事件
        UserUtil.setKeyValue_gone(Constant.resumecallbackName);
        UserUtil.setKeyValue_gone(Constant.BackPressedCallbackId);

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        // 获取h5注册的物理返回键拦截事件
        String callbackId = UserUtil.getValueByKey(Constant.BackPressedCallbackId);
        if (!TextUtils.isEmpty(callbackId)) {
            exeCallbackIDWebView(callbackId);
            // 消费完情况物理返回键的callbackId
            UserUtil.setKeyValue(Constant.BackPressedCallbackId, "");
        } else {
            super.onBackPressed();
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


    /*
     * @功能: 根据callbackID回调h5 js 函数
     * @参数:
     * @Date  2020/9/8 7:16 PM
     * @Author zhangg
     **/
    public void exeCallbackIDWebView(String callbackId) {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("aaa", 111);
            String data = jsonObject1.toString();
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("callbackId", callbackId);  // 添加回调方法
            data = jsonObject.toString();
            // new  method
            String jsCode = String.format("%s(%s,%s)", "window.JSSDK.receiveNativeMessage", "'" + callbackId + "'", data);
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
}
