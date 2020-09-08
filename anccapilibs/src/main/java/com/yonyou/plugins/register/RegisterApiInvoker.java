package com.yonyou.plugins.register;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;


/*
 * @功能: h5注册回调到原生界面
 * @Date  2020/9/8 9:01 PM
 * @Author zhangg
 **/
public class RegisterApiInvoker implements IApiInvoker {

    private static final String leftBtnCallback = "leftcallback";
    private static final String rightBtnCallback = "rightcallback";
    private static final String titlename = "titlename";
    private static final String scanCallback = "scancallback";
    private static final String openSingleScanActivity = "opensinglescanpage";  // 调起单次扫码界面
    private static final String openmixScanActivity = "openmixscanpage"; // 调起连续扫码界面

    public static String titleName = ""; // 连续扫码界面title


    @Override
    public String call(String apiname, MTLArgs args) throws com.yonyou.plugins.MTLException, MTLException {

        switch (apiname) {

            case openSingleScanActivity:

                ComponentName cn = new ComponentName(args.getContext().getPackageName(), "com.yonyou.scan.NccSingleScanActivity");
                Intent intent = new Intent();
                intent.setComponent(cn);
                Bundle bundle = new Bundle();
                bundle.putString("aa", "aa");
                intent.putExtras(bundle);
                args.getContext().startActivity(intent);


                break;
            case openmixScanActivity:

                ComponentName cn2 = new ComponentName(args.getContext().getPackageName(), "com.yonyou.scan.NccWebviewScannerActivity");
                Intent intent2 = new Intent();
                intent2.setComponent(cn2);
                Bundle bundle2 = new Bundle();
                bundle2.putString("aa", "aa");
                intent2.putExtras(bundle2);
                args.getContext().startActivity(intent2);


                break;

            case titlename:
                try {

                    String params2 = args.getParams();
                    LogerNcc.e(params2);
                    JsonObjectEx jsonObj2 = JsonObjectEx.getJsonObj(params2);
                    String titlename = jsonObj2.optString("titlename");
                    if (!TextUtils.isEmpty(titlename)) {
                        titleName = titlename;
                        UserUtil.setKeyValue(Constant.titleNameKey, titlename);
//                        Toast.makeText(args.getContext(), titlename, Toast.LENGTH_SHORT).show();
                        // 获取左按钮的回调函数名称
//                        leftCallbackName = callbackName2;
                        // 更改title名称
                    }

//                    JSONObject json = new JSONObject();
//                    json.put("code", 200);
//                    args.success(json);

                } catch (Exception e) {

                }

                break;

            // 只是注册事件而已
            case leftBtnCallback:
                try {

                    String params = args.getParams();
                    LogerNcc.e(params);
                    JsonObjectEx jsonObj = JsonObjectEx.getJsonObj(params);
                    String callbackName = jsonObj.optString("callbackName");
                    if (!TextUtils.isEmpty(callbackName)) {
                        UserUtil.setKeyValue(Constant.leftbtncallbackNameKey, callbackName);
                    }

//                    JSONObject json = new JSONObject();
//                    json.put("code", 200);
//                    args.success(json);

                } catch (Exception e) {

                }

                break;
            case rightBtnCallback:

                String params = args.getParams();
                LogerNcc.e(params);
                JsonObjectEx jsonObj = JsonObjectEx.getJsonObj(params);
                String callbackName = jsonObj.optString("callbackName");
                if (!TextUtils.isEmpty(callbackName)) {
                    UserUtil.setKeyValue(Constant.rightbtncallbackNameKey, callbackName);
                }


                break;

            case scanCallback:


                // 连续扫码,扫码结果回调事件注册
                String params2 = args.getParams();
                LogerNcc.e(params2);
                JsonObjectEx jsonObj2 = JsonObjectEx.getJsonObj(params2);
                String callbackName2 = jsonObj2.optString("callbackName");
                if (!TextUtils.isEmpty(callbackName2)) {
                    UserUtil.setKeyValue(Constant.scanCallbackKey, callbackName2);
                }


                break;


        }

        return null;
    }


}
