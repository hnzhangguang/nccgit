package com.yonyou.plugins.register;

import android.text.TextUtils;
import android.widget.Toast;

import com.yonyou.common.utils.logs.LogerNcc;
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

    public static String leftCallbackName = "";  // 左按钮回调函数名称
    public static String rightCallbackName = ""; // 右按钮回调函数名称


    @Override
    public String call(String apiname, MTLArgs args) throws com.yonyou.plugins.MTLException, MTLException {

        switch (apiname) {

            case titlename:
                try {

                    String params2 = args.getParams();
                    LogerNcc.e(params2);
                    JsonObjectEx jsonObj2 = JsonObjectEx.getJsonObj(params2);
                    String titlename = jsonObj2.optString("titlename");
                    if (!TextUtils.isEmpty(titlename)) {
                        Toast.makeText(args.getContext(), titlename, Toast.LENGTH_SHORT).show();
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
                        // 获取左按钮的回调函数名称
                        leftCallbackName = callbackName;
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
                    // 获取左按钮的回调函数名称
                    rightCallbackName = callbackName;
                }


                break;

            case scanCallback:


                break;


        }

        return null;
    }


}
