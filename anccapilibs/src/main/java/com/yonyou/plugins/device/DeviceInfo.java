package com.yonyou.plugins.device;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.yonyou.ancclibs.R;
import com.yonyou.common.utils.DeviceUtil;
import com.yonyou.plugins.MTLArgs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import static com.yonyou.common.utils.CommonRes.appConfig;


public class DeviceInfo {
	
	private static final String orientation = "orientation";
	private static final String portrait = "portrait";
	private static final String landscape = "landscape";
	private static final String landscape_left = "landscape-left";
	private static final String landscape_right = "landscape-right";
	
	private static Hashtable<String, String> deviceCache = new Hashtable<>();
	
	
	/**
	 * 获取IMSI
	 *
	 * @param args
	 */
	public static void getImsi(MTLArgs args) {
		String imsi = deviceCache.get("imsi");
		if (TextUtils.isEmpty(imsi)) {
			imsi = DeviceUtil.getImsi(args.getContext());
		}
		if (!TextUtils.isEmpty(imsi)) {
			deviceCache.put("imsi", imsi);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("imsi", imsi);
				args.success(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			args.error("获取设备imsi失败");
		}
	}
	
	/**
	 * 获取设备ID
	 *
	 * @param args
	 * @param key
	 */
	public static void getDeviceId(MTLArgs args, String key) {
		String deviceId = deviceCache.get("key");
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = DeviceUtil.getDeviceId(args.getContext());
		}
		if (!TextUtils.isEmpty(deviceId)) {
			deviceCache.put(key, deviceId);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(key, deviceId);
				args.success(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			args.error(R.string.mtl_Device_getDeviceId_Error);//TODO国际化
		}
	}
	
	/**
	 * 获取mac地址
	 *
	 * @param args
	 */
	public static void getMac(MTLArgs args) {
		String mac = DeviceUtil.getMac(args.getContext());
		if (!TextUtils.isEmpty(mac)) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("macAddress", mac);
				args.success(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			args.error("获取mac地址失败");
		}
	}
	
	/**
	 * 获取网络状态
	 *
	 * @param args
	 */
	public static void getNetworkType(MTLArgs args) {
		MtlDeviceManager.sharedManager().setContext(args.getContext());
		if (MtlDeviceManager.sharedManager().getNetworkType().equals(MtlDeviceManager.NO_DISPLAY)) {
			args.getCallback().error(MtlDeviceManager.NO_DISPLAY);
		} else {
			args.getCallback().success(MtlDeviceManager.sharedManager().jsNetworkType());
		}
	}
	
	/**
	 * 获取设备的型号
	 *
	 * @param args
	 */
	public static void getDeviceModel(MTLArgs args) {
		String deviceModel = Build.MODEL;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("deviceModel", deviceModel == null ? "" : deviceModel);
			args.success(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取手机厂商
	 *
	 * @param args
	 */
	public static void getVendor(MTLArgs args) {
		String vendor = Build.BRAND;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("vendor", vendor == null ? "" : vendor);
			args.success(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取手机系统版本
	 *
	 * @param args
	 */
	public static void getOSVersion(MTLArgs args) {
		String osVersion = Build.VERSION.RELEASE;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("OSVersion", osVersion == null ? "" : osVersion);
			args.success(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 手机拨号
	 *
	 * @param args
	 */
	public static void dial(MTLArgs args) {
		String number = args.getString("number");
		boolean confirm = args.getBoolean("confirm", true);
		Intent intent;
		if (confirm) {
			intent = new Intent(Intent.ACTION_DIAL);
		} else {
			intent = new Intent(Intent.ACTION_CALL);
		}
		Uri data = Uri.parse("tel:" + number);
		intent.setData(data);
		args.getContext().startActivity(intent);
		args.success("");
	}
	
	/**
	 * 锁定屏幕方向
	 *
	 * @param args
	 */
	public static void lockOrientation(MTLArgs args) {
		String screenOrientation = args.getString(orientation);
		if (screenOrientation.equals(portrait)) {
			args.getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (screenOrientation.equals(landscape_left)) {
			args.getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		} else if (screenOrientation.equals(landscape_right)) {
			args.getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (screenOrientation.equals(landscape)) {
			args.getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
		args.success();
	}
	
	/**
	 * 取消锁定屏幕方向
	 *
	 * @param args
	 */
	public static void unlockOrientation(MTLArgs args) {
		args.getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		args.success();
	}
	
	/**
	 * 获取设备类型
	 *
	 * @param args
	 */
	public static void getTerminalType(MTLArgs args) {
		if (appConfig != null) {
			JSONObject configJson = appConfig.optJSONObject("config");
			if (configJson != null) {
				String terminalType = configJson.optString("terminalType");
				args.success("terminalType", terminalType, false);
				return;
			}
		}
		args.success("terminalType", "", false);
	}
	
}
