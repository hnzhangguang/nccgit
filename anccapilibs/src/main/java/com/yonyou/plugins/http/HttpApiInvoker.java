package com.yonyou.plugins.http;

import android.text.TextUtils;

import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.DeviceUtil;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONObject;

public class HttpApiInvoker implements IApiInvoker {

    private static final String REQUEST = "request";
    private static final String DOWNLOAD = "downloadFile";
    private static final String requestNccService = "requestNccService";  // 借助原生调用ncc服务

    @Override
    public String call(String apiname, final MTLArgs args) throws MTLException {
        switch (apiname) {
            case REQUEST:
                HttpControl.request(args);
                return "";
            case DOWNLOAD:
                HttpControl.download(args);
                return "";
            case requestNccService:


                JsonObjectEx params = JsonObjectEx.getJsonObj(args.getParams());
                String url = params.optString("url", "");
                if (TextUtils.isEmpty(url)) {
//                    url = "/nccloud/mob/platform/standard/login";
                }
                JsonObjectEx parmJson = JsonObjectEx.getJsonObj(params.optString("param"));
                if (!DeviceUtil.isNetworkConnected(args.getContext())) {
                    args.error("请检查网络");
                    return "";
                }

                // 调用ncc服务
                NetUtil.callAction(args.getContext(), url, parmJson, new HttpCallBack() {
                    @Override
                    public void onFailure(JSONObject error) {
                        LogerNcc.e(error);
                        args.error(error.toString());
                    }

                    @Override
                    public void onResponse(JSONObject successJson) {
                        LogerNcc.e(successJson);
                        args.success(successJson);
                    }
                });
                return "";
        }
        throw new MTLException(apiname + ": function not found");
    }


}
