package com.yonyou.plugins.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yonyou.common.utils.FileUtils;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.vo.AppInfo;
import com.yonyou.common.vo.NCCUserVo;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StorageApiInvoker implements IApiInvoker {

    private static final String SET_FILE_STORAGE = "setFileStorage";
    private static final String GET_FILE_STORAGE = "getFileStorage";
    private static final String REMOVE_FILE_STORAGE = "removeFileStorage";
    private static final String CLEAR_FILE_STORAGE = "clearFileStorage";

    private static final String SET_STORAGE = "setStorage";
    private static final String GET_STORAGE = "getStorage";
    private static final String REMOVE_STORAGE = "removeStorage";
    private static final String CLEAR_STORAGE = "clearStorage";
    private static final String COPY_STORAGE = "copyStorage";
    private static final String GETUSERINFO = "getUserInfo";// 获取用户信息
    private static final String GETAPPINFO = "getAppInfo";// 获取app信息

    private static final String TYPE = "type";
    private static final String TYPE_JSON = "json";
    private static final String TYPE_JSONARRAY = "jsonarray";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_INT = "int";

    private String domain = "mtlConfig";

    private ReentrantReadWriteLock rtLock = new ReentrantReadWriteLock();

    @SuppressLint({"WrongConstant", "CommitPrefEdits"})
    @Override
    public String call(String apiname, final MTLArgs args) throws MTLException {
        Context context = args.getContext();
        domain = args.getString("domain", "storage");
        SharedPreferences sp = context.getSharedPreferences(domain, Context.MODE_APPEND);
        String key = args.getString("key");

        if (GETUSERINFO.equals(apiname) || GETAPPINFO.equals(apiname)) {
        } else {
            if (TextUtils.isEmpty(key) && !apiname.equals(CLEAR_STORAGE) && !apiname.equals(CLEAR_FILE_STORAGE)) {
                args.error("key不能为空");
                return "";
            }
        }

        switch (apiname) {
            case GETAPPINFO:  // 获取单个应用信息
                String appid = args.getString("appId");
                if (appid.isEmpty()) {
                    appid = "appid0";
                }
                JSONObject appJson = new JSONObject();
                try {
                    AppInfo appInfo = UserUtil.getAppInfo(appid);
                    // 获取到的应用信息是否为空
                    if (null != appInfo) {
                        String json = JSON.toJSONString(appInfo);//关键
                        appJson.put("appInfo", json);
                    } else {
                        appJson.put("appInfo", "");
                    }
                    args.success(appJson);
                } catch (Exception ee) {
                    LogerNcc.e(ee);
                    args.error(ee.toString());
                }
                return "";
            case GETUSERINFO:  // 获取用户信息
                JSONObject jsonObject1 = new JSONObject();
                NCCUserVo currentUserVo = UserUtil.getCurrentUserVo(); // 当前用户信息
                if (null != currentUserVo) {
                    String tenantId = currentUserVo.getTenantId();
                    String accessToken = currentUserVo.getAccessToken();
                    String userCode = currentUserVo.getUserCode();
                    String userid = currentUserVo.getUserid();
                    String userType = currentUserVo.getUserType();
                    String yhtUserId = currentUserVo.getYhtUserId();
                    try {
                        jsonObject1.put("tenantId", tenantId);
                        jsonObject1.put("accessToken", accessToken);
                        jsonObject1.put("userCode", userCode);
                        jsonObject1.put("userid", userid);
                        jsonObject1.put("userType", userType);
                        jsonObject1.put("yhtUserId", yhtUserId);
                        args.success(jsonObject1);
                    } catch (Exception ee) {
                        LogerNcc.e(ee);
                    }
                } else {
                    try {
                        jsonObject1.put("message", "没有获取到用户信息");
                        args.success(jsonObject1);
                    } catch (Exception ee) {
                        LogerNcc.e(ee);
                    }
                }

                return "";
            case SET_STORAGE:
                Object data = args.getObject("data");
                if (data == null) {
                    args.error("data不能为空");
                    return "";
                }
                if (data instanceof Boolean) {
                    sp.edit().putBoolean(key, (Boolean) data).apply();
                    sp.edit().putString(key + TYPE, TYPE_BOOLEAN).apply();
                } else if (data instanceof Integer) {
                    sp.edit().putInt(key, (Integer) data).apply();
                    sp.edit().putString(key + TYPE, TYPE_INT).apply();
                } else if (data instanceof String) {
                    sp.edit().putString(key, (String) data).apply();
                } else if (data instanceof JSONObject) {
                    sp.edit().putString(key, data.toString()).apply();
                    sp.edit().putString(key + TYPE, TYPE_JSON).apply();
                } else if (data instanceof JSONArray) {
                    sp.edit().putString(key, data.toString()).apply();
                    sp.edit().putString(key + TYPE, TYPE_JSONARRAY).apply();
                }
                args.success(new JSONObject());
                return "";

            case GET_STORAGE:
                Map<String, ?> all = sp.getAll();
                for (Map.Entry<String, ?> entry : all.entrySet()) {
                    if (key.equals(entry.getKey())) {
                        try {
                            Object getData = entry.getValue();
                            JSONObject jsonObject = new JSONObject();
                            String keyType = sp.getString(key + TYPE, "");
                            if (TYPE_JSON.equals(keyType)) {
                                JSONObject dataJson = new JSONObject((String) getData);
                                jsonObject.put("data", dataJson);
                            } else if (TYPE_JSONARRAY.equals(keyType)) {
                                JSONArray dataJson = new JSONArray((String) getData);
                                jsonObject.put("data", dataJson);
                            } else {
                                jsonObject.put("data", getData);
                            }
                            args.success(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            args.error(e.getMessage());
                        }
                        return "";
                    }
                }
                JSONObject getJson = new JSONObject();
                try {
                    new JSONObject().put("data", "null");
                } catch (JSONException e) {
                }
                args.success(getJson);
                return "";

            case REMOVE_STORAGE:
                sp.edit().remove(key).apply();
                JSONObject removeJson = new JSONObject();
                args.success(removeJson);
                return "";

            case CLEAR_STORAGE:
                sp.edit().clear().apply();
                JSONObject clearJson = new JSONObject();
                args.success(clearJson);
                return "";

            case SET_FILE_STORAGE:
                setFileStorage(args, domain);
                return "";
            case GET_FILE_STORAGE:
                getFileStorage(args, domain);
                return "";
            case REMOVE_FILE_STORAGE:
                removeFileStorage(args, domain);
                return "";
            case CLEAR_FILE_STORAGE:
                clearFileStorage(args, domain);
                return "";
            case COPY_STORAGE:
                copyStorage(args, domain);
                return "";
        }
        throw new MTLException(apiname + ": function not found");
    }

    private void copyStorage(final MTLArgs args, String domain) {
        File fileDir = args.getContext().getFilesDir();
        if (fileDir != null && !TextUtils.isEmpty(domain)) {
            String fromPath = args.getContext().getFilesDir().getParent() + "/shared_prefs/" + domain + ".xml";
            final File fromFile = new File(fromPath);
            String toPath = args.getContext().getExternalFilesDir("").getAbsolutePath() + "/" + domain + ".xml";
            final File toFile = new File(toPath);
            if (fromFile.exists()) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileUtils.copyFile(fromFile, toFile);
                            args.success();
                        } catch (IOException e) {
                            e.printStackTrace();
                            args.error("fail");
                        }
                    }
                });
                thread.start();
            } else {
                args.error("文件不存在");
            }

        }
    }


    private void setFileStorage(final MTLArgs args, String domain) {
        Activity activity = args.getContext();
        String key = args.getString("key");
        String pathType = args.getString("pathType");
        final String data = args.getString("data");
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(data)) {
            args.error("key或data为空！");
            return;
        }

        final String path;
        if ("sd".equals(pathType)) {
            path = activity.getExternalFilesDir("").getAbsolutePath() + "/mtl/File/" + domain + "/" + key + ".txt";
        } else {
            path = activity.getFilesDir().getPath() + "/mtl/File/" + domain + "/" + key + ".txt";
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Lock lock = rtLock.writeLock();
                lock.lock();
                try {
                    FileUtils.writeTextFile(path, data);
                    args.success();
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    private void getFileStorage(final MTLArgs args, String domain) {
        Activity activity = args.getContext();
        String key = args.getString("key");
        String pathType = args.getString("pathType");
        if (TextUtils.isEmpty(key)) {
            args.error("key为空！");
            return;
        }
        final String path;
        if ("sd".equals(pathType)) {
            path = activity.getExternalFilesDir("").getAbsolutePath() + "/mtl/File/" + domain + "/" + key + ".txt";
        } else {
            path = activity.getFilesDir().getPath() + "/mtl/File/" + domain + "/" + key + ".txt";
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Lock lock = rtLock.readLock();
                lock.lock();
                try {
                    String data = FileUtils.readTextFile(path);
                    if (TextUtils.isEmpty(data)) {
                        args.error("文件不存在或文件数据为空！");
                        return;
                    }
                    args.success("data", data, false);
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    private void removeFileStorage(MTLArgs args, String domain) {
        Activity activity = args.getContext();
        String key = args.getString("key");
        String pathType = args.getString("pathType");
        if (TextUtils.isEmpty(key)) {
            args.error("key为空！");
            return;
        }
        final String path;
        if ("sd".equals(pathType)) {
            path = activity.getExternalFilesDir("").getAbsolutePath() + "/mtl/File/" + domain + "/" + key + ".txt";
        } else {
            path = activity.getFilesDir().getPath() + "/mtl/File/" + domain + "/" + key + ".txt";
        }
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                args.success();
            } else {
                args.error("删除失败");
            }
        } else {
            args.error("文件不存在");
        }

    }


    private void clearFileStorage(MTLArgs args, String domain) {
        Activity activity = args.getContext();
        String pathType = args.getString("pathType");
        final String path;
        if ("sd".equals(pathType)) {
            path = activity.getExternalFilesDir("").getAbsolutePath() + "/mtl/File/" + domain;
        } else {
            path = activity.getFilesDir().getPath() + "/mtl/File/" + domain;
        }
        if (FileUtils.deleteDirFile(path)) {
            args.success();
        } else {
            args.error("文件夹不存在");
        }

    }

}
