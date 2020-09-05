package com.yonyou.plugins.ucg;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.net.MTLHttpCallBack;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.plugins.ApiCallback;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONException;
import org.json.JSONObject;

public class UCGApiInvoker implements IApiInvoker {


    public static final String SUCCESS = "200";
    /**
     * js相关调用方法，配合call
     */
    private static final String CALL_ACTION = "callAction";
    private static final String CALL_SERVICE = "callService";
    private static final String requestService = "requestService";  // 壳 代替发送网络请求
    private static final String WRITE_UCGCONFIG = "writeUCGConfig";
    private static final String READ_UCGCONFIG = "readUCGConfig";
    private static final String NCC_LOGIN = "login";
    private static final int UN_LOGIN = 0;
    private static final int LOGINING = 1;
    private static final int LOGINED = 2;


    /**
     * 通过有空间code登录NCC相关参数
     */
    private String langCode;
    private String appcode;
    private String upesncode;
    private String origin;
    private String ncctoken;
    private String token;
    private MTLLoginCallBack loginCall;


    /**
     * 登录状态  loginStatus
     * 0  未登录 登录失效
     * 1  正在登录
     * 2 已经登录
     */
    private int loginStatus = UN_LOGIN;
    private String mApiName;


    @Override
    public String call(String apiname, MTLArgs arg) throws com.yonyou.plugins.MTLException, MTLException {
        mApiName = apiname;
        final MTLUCGArgs args = MTLUCGArgs.build(arg, langCode, appcode, upesncode, origin);
        if (loginCall == null) {
            loginCall = new MTLLoginCallBack();
        }
        switch (apiname) {
            case requestService:   // h5 借助壳 发送网络请求

                String param1 = arg.getString("param", "");
                if (TextUtils.isEmpty(param1)) {
                    args.error("请先登录");
                    return "";
                }
                // zhangg
                String url = arg.getString("url", "");// /nccloud/mob/platform/mob/openapp
                try {
                    JSONObject param = new JSONObject(param1);
                    NetUtil.callAction(arg.getContext(), url, param, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            args.error(error.toString());
                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            args.success(successJson);
                        }
                    });
                } catch (Exception ee) {

                }

                return "";

            case CALL_ACTION:
            case CALL_SERVICE:
                if (loginStatus != LOGINED) {
                    args.error("请先登录");
                    return "";
                }
                args.setNccToken(ncctoken);
                args.setToken(token);
                //                args.setSysParamJson();
                args.setParams(arg.getJson("params").toString());
                args.setDefurl(arg.getString("url"));
                args.setAppcode(appcode);
                args.setUpesncode(upesncode);
                loginCall.setCallback(args.getCallback());
                return MTLService.callAction(args);

            case WRITE_UCGCONFIG:
                return MTLService.writeUCGConfig(args);

            case READ_UCGCONFIG:   // 读取ucg配置信息
//				return MTLService.readUCGConfig(args);  // 原来的方式

                // zhangg
                JSONObject jsonObject = new JSONObject();
                try {
                    String net_ip = UserUtil.getValueByKey(Constant.net_ip);
                    String net_port = UserUtil.getValueByKey(Constant.net_port);
                    jsonObject.put("ip", net_ip);
                    jsonObject.put("port", net_port);
                    jsonObject.put(Constant.accountInfoKey, UserUtil.getValueByKey(Constant.accountInfoKey));
                    args.success(jsonObject);

                } catch (Exception e) {
                    Log.e("mmmm", jsonObject.toString() + " - " + e);
                    args.error(e.getMessage());
                }
                return jsonObject.toString();

            case NCC_LOGIN:
                if (TextUtils.isEmpty(arg.getString("upesncode"))) {
                    return "";
                }
                loginCall.setCallback(args.getCallback());
                if (loginStatus == LOGINING) {
                    return "success";
                }
                return loginNcc(args);

        }
        throw new MTLException(apiname + ": function not found");
    }

    @NonNull
    private String loginNcc(MTLUCGArgs args) {
        setCommon(args);
        if (mApiName.equals(NCC_LOGIN)) {
            loginStatus = LOGINING;
        }
        return MTLService.login(args, loginCall);
    }

    private boolean isLogin(MTLUCGArgs arg) {

        switch (loginStatus) {
            case UN_LOGIN:
                loginNcc(arg);
            case LOGINING:
                loginCall.startCallAction(true);
                break;
            case LOGINED:
                return true;

        }
        return false;
    }

    /**
     * 设置公共参数
     * {"origin":"http://172.23.79.2:8080","upesncode":"42f0bff32b4c400a86b5eaf149099956",
     * "appcode":"TEST001","langCode":"simpchn"}
     *
     * @param args
     */
    private void setCommon(MTLUCGArgs args) {
        JSONObject p = args.getJSONObject();
        if (p == null) {
            return;
        }
        langCode = p.optString("langcode");
        upesncode = p.optString("code");
        //        origin = p.optString("origin");
        appcode = p.optString("appcode");
        args.setAppcode(appcode);
        args.setUpesncode(upesncode);
        args.setOrigin(origin);
        args.setLangCode(langCode);

    }

    ;

    class MTLLoginCallBack implements MTLHttpCallBack {

        MTLUCGArgs args;
        private boolean startCallAction;
        private ApiCallback callback;

        public void setArgs(MTLUCGArgs args) {
            this.args = args;
            this.callback = args.getCallback();
        }

        public void setCallback(ApiCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(String error) {
            if (NCC_LOGIN.equals(mApiName)) {
                loginStatus = UN_LOGIN;
            }
            if (callback != null) {
                callback.error(error);
            }
        }

        @Override
        public void onResponse(int status, String body) {
            try {
                JSONObject data = new JSONObject(body);

                String code = data.optString("code");
                if (SUCCESS.equals(code)) {
                    if (NCC_LOGIN.equals(mApiName)) {
                        loginStatus = LOGINED;
                    }
                    JSONObject resdata = data.optJSONObject("data");
                    if (resdata != null) {
                        ncctoken = resdata.optString("ncctoken");
                        token = resdata.optString("token");
                        appcode = resdata.optString("appcode");
                        if (startCallAction) {
                            startCallAction = false;
                            MTLService.callAction(args);
                        } else {
                            if (callback != null)
                                callback.success(resdata);
                        }
                    } else {
                        if (callback != null)
                            callback.success(data);
                    }


                } else {
                    if (NCC_LOGIN.equals(mApiName)) {
                        loginStatus = UN_LOGIN;
                    }
                    if (callback != null)
                        if (TextUtils.isEmpty(code)) {
                            code = data.optString("status");
                        }
                    callback.error(code, data.optString("message"));
                }
            } catch (JSONException e) {
                if (NCC_LOGIN.equals(mApiName)) {
                    loginStatus = UN_LOGIN;
                }
                if (callback != null)
                    callback.error("网络请求错误");
            } catch (Exception e) {
                if (NCC_LOGIN.equals(mApiName)) {
                    loginStatus = UN_LOGIN;
                }
                if (callback != null)
                    callback.error("网络请求错误");
                e.printStackTrace();
            } catch (com.yonyou.plugins.MTLException e) {
                if (NCC_LOGIN.equals(mApiName)) {
                    loginStatus = UN_LOGIN;
                }
                callback.error("网络请求错误");
                e.printStackTrace();
            }
        }

        public void startCallAction(boolean b) {
            this.startCallAction = b;
        }
    }

}
