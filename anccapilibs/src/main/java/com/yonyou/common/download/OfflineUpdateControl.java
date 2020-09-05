package com.yonyou.common.download;

import android.content.Context;
import android.text.TextUtils;

import com.yonyou.common.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class OfflineUpdateControl {
	private static final String IGNORE_TENANTVERSION = "ignore_tenantVersion";
	
	
	/*
	 * @功能: 下载文件存放路径
	 * @参数  context 上下文环境;
	 * @Date  2020/8/15;
	 * @Author zhangg
	 **/
	public static String getOfflinePathWhitoutAppId(Context context) {
//        String path = context.getExternalFilesDir("DIRECTORY_ALARMS").getPath()+"/"+appId;// /storage/emulated/0/Android/data/包名/files
		String path = context.getFilesDir().getPath();//  /data/data/包名/files
		return path;
	}
	
	/*
	 * @功能: 根据appid 存放到指定的路径下面(每个应用一个存放路径)
	 * @参数  context ;
	 * @参数  appId 应用id;
	 * @Date  2020/8/15;
	 * @Author zhangg
	 **/
	public static String getOfflinePath(Context context, String appId) {
//        String path = context.getExternalFilesDir("DIRECTORY_ALARMS").getPath()+"/"+appId;// /storage/emulated/0/Android/data/包名/files
		String path = context.getFilesDir().getPath() + "/" + appId;//  /data/data/包名/files
		return path;
	}
	
	public static boolean fileExists(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	//    public static JSONObject getLocalAppInfo(Context context,String appId){
//        String appJsonPath = getOfflinePath(context,appId)+"/"+getLocalAppParam(context,appId,"tenantVersion")+"/app.json";
//        if(!fileExists(appJsonPath)){
//            return null;
//        }
//        String content = readLocalFile(appJsonPath);
//        try {
//            JSONObject json = new JSONObject(content);
//            return json;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
	
	
	/*
	 * @功能:
	 * @参数  context
	 * @参数  appId
	 * @参数  key
	 * @Date  2020/7/24;
	 * @Author zhangg
	 **/
	public static String getLocalAppParam(Context context, String appId, String key) {
		String appJsonPath = getOfflinePath(context, appId) + "/" + getAppParam(context, appId, "tenantVersion") + "/app.json";
		if (!fileExists(appJsonPath)) {
			return "";
		}
		String content = readLocalFile(appJsonPath);
		try {
			JSONObject json = new JSONObject(content);
			String value = json.optString(key);
			return value;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static String readLocalFile(String filePath) {
		StringBuilder sb = new StringBuilder();
		try {
			File file = new File(filePath);
			InputStream in = null;
			in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				sb.append((char) tempbyte);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void saveAppInfo(Context context, String appid, String appInfo) {
		SharedPreferencesUtils.putString(context, appid, appInfo);
	}
	
	public static String getAppInfo(Context context, String appid) {
		return SharedPreferencesUtils.getString(context, appid, "");
	}
	
	/**
	 * 获取tenantVersion
	 *
	 * @param context
	 * @param appid
	 * @param key
	 * @return 获取应用版本号
	 */
	public static int getAppParam(Context context, String appid, String key) {
		String info = SharedPreferencesUtils.getString(context, appid, "");
		if (!TextUtils.isEmpty(info)) {
			try {
				JSONObject json = new JSONObject(info);
				int value = json.optInt(key);
				return value;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public static void saveAppIgnoreTenantVersion(Context context, String appid, int ignoreTenantVersion) {
		SharedPreferencesUtils.putInt(context, appid + IGNORE_TENANTVERSION, ignoreTenantVersion);
	}
	
	public static int getAppIgnoreTenantVersion(Context context, String appid) {
		return SharedPreferencesUtils.getInt(context, appid + IGNORE_TENANTVERSION, -1);
	}
	
	
}
