package com.yonyou.common.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.vo.JsonObjectEx;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 描述:网络工具类
 *
 * @author zhangg
 * @下午10:30:02
 */
public class NetUtil {


    /*
     * @功能: 是否忽略此url 请求header 不传token
     * @参数:
     * @Date  2020/8/30 7:08 PM
     * @Author zhangg
     **/
    public static boolean isIgnoreUrl(String url) {
        Set<String> set = new HashSet<String>();
        // 需要过滤掉的入下url
        set.add(ConstantUrl.loginUrl);
        set.add(ConstantUrl.forgetUrl_sendCode);
        set.add(ConstantUrl.checkCode_newpw_resetpw_Url);
        set.add(ConstantUrl.requestAccountListUrl);

        if (set.contains(url)) {
            return true;
        }
        return false;
    }


    /*
     * @功能:  POST方式提交String
     * 在构造 Request对象时，需要多构造一个RequestBody对象，携带要提交的数据。
     * 在构造 RequestBody 需要指定MediaType，用于描述请求/响应 body 的内容类型
     * @参数  url 相对url
     * @参数  busiParamJson 请求参数json
     * @参数  callBack 回调对象
     * @Date  2020/8/13;
     * @Author zhangg
     **/
    public static void callAction(final Activity context, String url, JSONObject busiParamJson, final HttpCallBack callBack) {

        // 判断当前网络是否可用
        if (!NetUtil.isAvailable()) {
            final JsonObjectEx json = JsonObjectEx.getJsonObj();
            json.putEx(Constant.ERROR, "当前网络不可用");
            json.putEx(Constant.MSG, "当前网络不可用");
            if (callBack != null && null != context) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(json);
                    }
                });
            }
            return;
        }

        try {
            String ip = UserUtil.getValueByKey(Constant.net_ip, "");
            String port = UserUtil.getValueByKey(Constant.net_port, "");
//			ip = "10.11.115.38";
//			port = "8899";
            String requestUrl = "http://" + ip + ":" + port + url;
            // 定义返送参数类型
            MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
            final JSONObject json = new JSONObject();
            JSONObject sysParamJson = new JSONObject();
            sysParamJson.put("busiaction", "ncc移动登录");
            sysParamJson.put("appCode", "400400");
            sysParamJson.put("langCode", "simpchn");
            sysParamJson.put("ts", new Date().getTime());
            sysParamJson.put("requestUrl", requestUrl);

            // 设置账套信息
            String account = UserUtil.getValueByKey(Constant.accountInfoKey);
            if (TextUtils.isEmpty(account)) {
                busiParamJson.put("code", "design");  // 账套
            } else {
                busiParamJson.put("code", account);  // 账套
            }

            // 参数体也把token传给后台,以防备用
            //busiParamJson.put(Constant.accessToken, UserUtil.getValueByKey(Constant.accessToken));

            // 语言
            String langCode = busiParamJson.optString("langCode", "");
            if (TextUtils.isEmpty(langCode)) {
                busiParamJson.put("langCode", "simpchn");
            }
            json.put("busiParamJson", busiParamJson.toString());
            json.put("sysParamJson", sysParamJson);
            LogerNcc.e(busiParamJson);

            String requestBody = json.toString();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(requestUrl)
                    .post(RequestBody.create(mediaType, requestBody));

            // 过滤不需要加accessToken
            if (!isIgnoreUrl(url)) {
                requestBuilder.addHeader(Constant.accessToken, UserUtil.getValueByKey(Constant.accessToken));
            }

            Request request = requestBuilder.build();

//			OkHttpClient okHttpClient = new OkHttpClient();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .pingInterval(20, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build();


            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogerNcc.e("postString onFailure: " + e.toString());
                    handerException(context, callBack, e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String data = response.body().string();
//                    LogerNcc.e(data);

                    try {
                        // 为空的时候,走成功回调
                        if (TextUtils.isEmpty(data)) {
                            final JSONObject parentJson = new JSONObject();
                            parentJson.put("data", data);
                            parentJson.put("message", "后台返回的数据为空");
                            if (callBack != null && null != context) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.onFailure(parentJson);
                                    }
                                });
                            }
                        } else {
                            final JSONObject parentJson = new JSONObject(data);
                            final String requestReturnCode = parentJson.optString(Constant.CODE); // 200 时候正常,否则有错误
                            parentJson.put("code", requestReturnCode);
                            parentJson.put("responseCode", response.code());
                            parentJson.put("message", response.message());
                            parentJson.put("responseProtocol", response.protocol());
                            if (callBack != null && null != context) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ("200".equals(requestReturnCode)) {
                                            callBack.onResponse(parentJson);
                                        } else {
                                            callBack.onFailure(parentJson);
                                        }
                                    }
                                });
                            }
                        }


                    } catch (Exception e1) {
                        Log.e("mmmm", "callAction: " + e1);
                        handerException(context, callBack, e1);
                    }
                }
            });

        } catch (Exception e) {
            Log.e("mmmm", "callAction: " + e);
            handerException(context, callBack, e);
        }


    }


    /*
     * @功能: 统一处理请求错误情况
     * @参数:
     * @Date  2020/8/30 7:14 PM
     * @Author zhangg
     **/
    public static void handerException(Activity context, final HttpCallBack callBack, Exception e) {
        try {
            final JSONObject json = new JSONObject();
            json.put(Constant.ERROR, e.toString());
            json.put(Constant.MSG, e.getMessage());
            if (callBack != null && null != context) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(json);
                    }
                });
            }
        } catch (Exception e1) {
            LogerNcc.e("postString onFailure: " + e1.getMessage());
            e1.printStackTrace();
        }
    }


    /**
     * 判断网络是否连接
     *
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] infos = cm.getAllNetworkInfo();
        if (infos != null) {
            for (NetworkInfo ni : infos) {
                if (ni.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断网络是否连接有线网络（ Ethernet）
     *
     * @return
     */
    private static boolean isEthernetEnabled() {
        ConnectivityManager cm = (ConnectivityManager) UiUtils.getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当个网络是否可用
     *
     * @param
     * @return
     */
    public static boolean isAvailable() {
        // 需要权限
        // <uses-permission
        // android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
        ConnectivityManager cwjManager = (ConnectivityManager) UiUtils.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cwjManager.getActiveNetworkInfo()) {
            return false;
        }
        return cwjManager.getActiveNetworkInfo().isAvailable();
    }


    /*
     * @功能: 网络是否连接
     * @参数: context 上下文
     * @Date  2020/8/31 7:51 PM
     * @Author zhangg
     **/
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}