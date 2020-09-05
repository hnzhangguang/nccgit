package com.yonyou.plugins.window;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.AppCache;
import com.yonyou.common.utils.ResourcesUtils;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WindowApiInvoker implements IApiInvoker {

    private static final String NAVIGATE_TO = "navigateTo";
    private static final String REDIRECT_TO = "redirectTo";
    private static final String NAVIGATE_BACK = "navigateBack";
    private static final String backlntercept = "backlntercept";
    private static final String registerResume = "registerResume";
    private static final String isScreenOrientatio = "ScreenOrientation";


    @Override
    public String call(String apiname, final MTLArgs args) throws MTLException {
        Activity context = args.getContext();
        switch (apiname) {
            case NAVIGATE_TO:
                toActivity(args, context, false);
                return "";
            case REDIRECT_TO:
                toActivity(args, context, true);
                return "";
            case NAVIGATE_BACK:
                context.finish();
                return "";
            case backlntercept:
                // 只是注册物理返回键的callbackId
                String callbackId = args.getString("callbackId");
                UserUtil.setKeyValue(Constant.BackPressedCallbackId, callbackId);
                return "";
            case registerResume:
                // 只是注册物理返回键的callbackId
                String registerResume = args.getString("callbackId");
                UserUtil.setKeyValue(Constant.resumecallbackid, registerResume);
                return "";
            case isScreenOrientatio:  // 横竖屏api

                return "";

        }
        throw new MTLException(apiname + ": function not found");
    }

    private void toActivity(MTLArgs args, Activity context, String pageConfig,
                            boolean needFinish) {
        String page = args.getString("page");
        String parameters = args.getString("parameters");
        try {
            JSONObject pageConfigJson = new JSONObject(pageConfig);
            JSONArray platforms = pageConfigJson.optJSONArray("platforms");
            if (platforms != null && platforms.length() > 0) {
                for (int i = 0; i < platforms.length(); i++) {
                    JSONObject platformJson = platforms.optJSONObject(i);
                    String platform = platformJson.optString("platform");
                    if ("android".equals(platform)) {
                        JSONObject pages = platformJson.optJSONObject("pages");
                        JSONObject pageJson = pages.optJSONObject(page);
                        if (pageJson == null) {
                            args.error("page不能为空");
                            return;
                        }
                        Intent intent = new Intent();
                        String url = pageJson.optString("url");
                        if (!TextUtils.isEmpty(url)) {
                            intent.setClass(context, NewWebActivity.class);
                            intent.putExtra("url", url);
                        } else {
                            try {
                                Class<Activity> aClass =
                                        (Class<Activity>) Class.forName(pageJson.optString("class"));
                                intent.setClass(context, aClass);
                            } catch (ClassNotFoundException e) {
                                args.error("页面不存在");
                                e.printStackTrace();
                                return;
                            }
                        }
                        intent.putExtra("parameters", parameters);
                        context.startActivity(intent);
                        if (needFinish) {
                            context.finish();
                        }
                        args.success();
                    }
                }
            } else {
                args.error("请检查页面配置");
            }


        } catch (JSONException e) {
            args.error("参数配置文件不存在或者参数配置错误");
            e.printStackTrace();
        }
    }

    private void toActivity(MTLArgs args, Activity context, boolean needFinish) {
        String pageConfig = ResourcesUtils.getFromAssets(context, "www/pages.json");
        if (TextUtils.isEmpty(pageConfig)) {
            Object pagesUrl = AppCache.getAppCache().getValue("pagesUrl");
            if (pagesUrl instanceof String) {
                if (!TextUtils.isEmpty((String) pagesUrl)) {
                    getPagesJson(args, (String) pagesUrl, context, needFinish);
                }
            }
        } else {
            toActivity(args, context, pageConfig, needFinish);
        }
    }

    private void getPagesJson(final MTLArgs args, String pagesUrl, final Activity context,
                              final boolean needFinish) {
        Request request = new Request.Builder().url(pagesUrl).get().build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                args.error("获取页面配置失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String res = response.body().string();
                    toActivity(args, context, res, needFinish);
                } else {
                    args.error("获取页面配置失败");
                }
            }
        });
    }

}
