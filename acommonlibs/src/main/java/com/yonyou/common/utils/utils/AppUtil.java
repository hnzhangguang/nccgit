package com.yonyou.common.utils.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.yonyou.common.utils.litepal.LitePalNcc;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.vo.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 应用工具类
 *
 * @author zhangg
 */
public class AppUtil {
	
	
	public static boolean isDarkTheme(Context context) {
		
		
		return false;
	}
	
	/**
	 * 跟新本地应用信息
	 *
	 * @param appInfo
	 */
	public static void updateAppInfoLocation(AppInfo appInfo) {
		try {
			if (null != appInfo) {
				
				List<AppInfo> list = LitePalNcc.select(" id,appid ,appname,iconurl,type,version,zipurl,url ").where(" appid = ? ", appInfo.getAppid()).find(AppInfo.class);
				if (null != list && list.size() == 1) {
					AppInfo info = list.get(0);
					info.setUrl(appInfo.getUrl());
					info.setZipurl(appInfo.getZipurl());
					info.setAppname(appInfo.getAppname());
					info.save();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取本地应用信息
	 *
	 * @return
	 */
	public static List<AppInfo> getAppInfoLocation() {
		try {
			List<AppInfo> list = new ArrayList<>();
			JSONArray array = LitePalNcc.findBySQL(" select * from appinfo ", AppInfo.class);
			for (int i = 0; i < array.length(); i++) {
				AppInfo info = buildAppInfoByJsonObject(array.getJSONObject(i));
				list.add(info);
			}
			return list;
		} catch (JSONException e) {
			LogerNcc.e(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据appId获取本地存在的应用信息
	 *
	 * @param appid
	 * @return
	 */
	public static AppInfo getAppInfoByAppId(String appid) {
		try {
			if (!TextUtils.isEmpty(appid.trim())) {
				JSONArray result = LitePalNcc.findBySQL(" select * from appinfo where appid = '" + appid + "' ", AppInfo.class);
				if (result != null && result.length() == 1) {
					AppInfo info = buildAppInfoByJsonObject(result.getJSONObject(0));
					return info;
				}
			}
		} catch (JSONException e) {
			LogerNcc.e(e);
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 构造appInfo对象
	 *
	 * @param jsonObject
	 * @return
	 */
	public static AppInfo buildAppInfoByJsonObject(JSONObject jsonObject) {
		AppInfo info = new AppInfo();
		if (null == jsonObject) {
			return info;
		}
		info.setAppid(jsonObject.optString("appid", ""));
		info.setAppname(jsonObject.optString("appname", ""));
		info.setIconurl(jsonObject.optString("iconurl", ""));
		info.setType(jsonObject.optString("type", ""));
		info.setVersion(jsonObject.optInt("version", 1));
		info.setZipurl(jsonObject.optString("zipurl", ""));
		info.setUrl(jsonObject.optString("url", ""));
		return info;
	}
	
	
	/**
	 * 获取App具体设置
	 *
	 * @param context 上下文
	 */
	public static void getAppDetailsSettings(Context context, int requestCode) {
		getAppDetailsSettings(context, context.getPackageName(), requestCode);
	}
	
	/**
	 * 获取App具体设置
	 *
	 * @param context     上下文
	 * @param packageName 包名
	 */
	public static void getAppDetailsSettings(Context context, String packageName, int requestCode) {
		if (TextUtils.isEmpty(packageName)) return;
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + packageName));
		((AppCompatActivity) context).startActivityForResult(intent, requestCode);
	}
	
	/**
	 * 获取App具体设置的意图
	 *
	 * @param packageName 包名
	 * @return intent
	 */
	public static Intent getAppDetailsSettingsIntent(String packageName) {
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:" + packageName));
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	/**
	 * 通过任务管理器杀死进程
	 * 需添加权限 {@code <uses-permission android:name="android.permission.RESTART_PACKAGES"/>}</p>
	 *
	 * @param context
	 */
	public static void restart(Context context) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startMain);
			System.exit(0);
		} else {// android2.1
			ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(context.getPackageName());
		}
	}
	
	
}
