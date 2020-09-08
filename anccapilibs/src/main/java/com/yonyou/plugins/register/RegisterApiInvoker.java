package com.yonyou.plugins.register;

import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

public class RegisterApiInvoker implements IApiInvoker {

    private static final String leftBtnCallback = "leftcallback";
    private static final String rightBtnCallback = "rightcallback";
    private static final String scanCallback = "scancallback";

    public static String leftCallbackName = "";


    @Override
    public String call(String apiname, MTLArgs args) throws com.yonyou.plugins.MTLException, MTLException {

        switch (apiname) {
            // 只是注册事件而已
            case leftBtnCallback:
                try {

                    String params = args.getParams();
                    LogerNcc.e(params);
                    JsonObjectEx jsonObj = JsonObjectEx.getJsonObj(params);
                    String callbackName = jsonObj.optString("callbackName");
                    if (TextUtils.isEmpty(callbackName)) {
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

                break;

            case scanCallback:


                break;


        }

        return null;
    }


}