package com.yonyou.plugins.register;

import android.app.Activity;
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
import com.yonyou.scan.NccWebviewScannerActivity;


/*
 * @功能: h5注册回调到原生界面
 * @Date  2020/9/8 9:01 PM
 * @Author zhangg
 **/
public class RegisterApiInvoker implements IApiInvoker {

    private static final String leftBtnCallback = "leftcallback";
    private static final String rightBtnCallback = "rightcallback";
    private static final String continueScanType = "continueScanType";   // 是否支持连续扫码
    private static final String continueScanAction = "continueScanAction";   // 开始执行扫码

    private static final String openlight = "openlight";   // 灯开
    private static final String closelight = "closelight";   // 灯关

    private static final String openContinueScanActivity = "openContinueScan";   // 灯关

    private static final String titlename = "titlename";
    private static final String scanCallback = "scancallback";
    private static final String openSingleScanActivity = "opensinglescanpage";  // 调起单次扫码界面
    private static final String openmixScanActivity = "openmixscanpage"; // 调起连续扫码界面

    public static String titleName = ""; // 连续扫码界面title


    @Override
    public String call(String apiname, MTLArgs args) throws com.yonyou.plugins.MTLException, MTLException {

        switch (apiname) {


            case openContinueScanActivity:  // 打开连续扫码界面

                ComponentName componentName = new ComponentName(args.getContext(), "com.yonyou.scan.NccWebviewScannerActivity");
                Intent intent2 = new Intent();
                intent2.setComponent(componentName);
                Bundle bundle2 = new Bundle();
                bundle2.putString("aa", "aa");
                intent2.putExtras(bundle2);
                args.getContext().startActivity(intent2);

                break;

            case openlight:
                Activity context1 = args.getContext();
                if (context1 instanceof NccWebviewScannerActivity) {
                    NccWebviewScannerActivity mActivity1 = (NccWebviewScannerActivity) context1;
                    mActivity1.openLight();
                }
                break;
            case closelight:
                Activity context2 = args.getContext();
                if (context2 instanceof NccWebviewScannerActivity) {
                    NccWebviewScannerActivity mActivity2 = (NccWebviewScannerActivity) context2;
                    mActivity2.closeLight();
                }
                break;

            case continueScanAction:   // 连续扫码时候开始非第一次的扫码执行

                Activity context = args.getContext();
                if (context instanceof NccWebviewScannerActivity) {
                    NccWebviewScannerActivity mActivity = (NccWebviewScannerActivity) context;
                    mActivity.restartQRScanner();
                }

                break;

            case continueScanType:

                String params21 = args.getParams();
                LogerNcc.e(params21);
                JsonObjectEx jsonObj2 = JsonObjectEx.getJsonObj(params21);
                String value = jsonObj2.optString("type");
                if (!TextUtils.isEmpty(value)) {
                    UserUtil.setKeyValue(Constant.isContinueScanKey, value);
                }


                break;


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
                Intent intent21 = new Intent();
                intent21.setComponent(cn2);
                Bundle bundle21 = new Bundle();
                bundle21.putString("aa", "aa");
                intent21.putExtras(bundle21);
                args.getContext().startActivity(intent21);


                break;

            case titlename:
                try {

                    String params22 = args.getParams();
                    LogerNcc.e(params22);
                    JsonObjectEx jsonObj22 = JsonObjectEx.getJsonObj(params22);
                    String titlename = jsonObj22.optString("titlename");
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
                String params23 = args.getParams();
                LogerNcc.e(params23);
                JsonObjectEx jsonObj23 = JsonObjectEx.getJsonObj(params23);
                String callbackName23 = jsonObj23.optString("callbackName");
                if (!TextUtils.isEmpty(callbackName23)) {
                    UserUtil.setKeyValue(Constant.scanCallbackKey, callbackName23);
                }


                break;


        }

        return null;
    }


}
