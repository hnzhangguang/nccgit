package com.yonyou.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

public class DeviceUtil {
	private static final String PREFS_FILE = "dsl_device_id";
	private static final String PREFS_DEVICE_ID = "dsl_device_id";
	
	
	public static String getImsi(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				((Activity) context).requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
			}
		}
		TelephonyManager telephonyManager =
			  (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonyManager.getSubscriberId();
		return imsi;
	}
	
	public static String getDeviceId(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				((Activity) context).requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
			}
		}
		TelephonyManager tm = (TelephonyManager) context
			  .getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = tm.getDeviceId();
		if (TextUtils.isEmpty(deviceid)) {
			deviceid = Settings.Secure.getString(context.getContentResolver(),
				  Settings.Secure.ANDROID_ID);//androidID
			if (TextUtils.isEmpty(deviceid) || deviceid.equals("9774d56d682e549c")) {
				SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
				deviceid = prefs.getString(PREFS_DEVICE_ID, null);
				if (TextUtils.isEmpty(deviceid)) {
					deviceid = UUID.randomUUID().toString();
					prefs.edit().putString(PREFS_DEVICE_ID, deviceid).commit();
				}
			}
		} else {
			if (Build.VERSION.SDK_INT > 8) {
				deviceid = Build.SERIAL + deviceid;
			}
		}
		return deviceid;
		
	}
	
	
	/**
	 * 获取mac地址
	 */
	public static String getMac(Context context) {
		String mac = getMacDefault(context);
		if (TextUtils.isEmpty(mac) || "02:00:00:00:00:00".equals(mac)) {
			mac = getMacAddress();
			if (TextUtils.isEmpty(mac) || "02:00:00:00:00:00".equals(mac)) {
				mac = get7Mac();
			}
		}
		return mac == null ? "" : mac;
	}
	
	/**
	 * Android 6.0 之前（不包括6.0）获取mac地址
	 * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
	 *
	 * @param context
	 */
	private static String getMacDefault(Context context) {
		String mac = "";
		if (context == null) {
			return mac;
		}
		WifiManager wifi =
			  (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = null;
		try {
			info = wifi.getConnectionInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (info == null) {
			return null;
		}
		mac = info.getMacAddress();
		if (!TextUtils.isEmpty(mac)) {
			mac = mac.toUpperCase(Locale.ENGLISH);
		}
		return mac;
	}
	
	/**
	 * Android 6.0-Android 7.0 获取mac地址
	 */
	private static String getMacAddress() {
		String macSerial = null;
		String str = "";
		
		try {
			Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			
			while (null != str) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return macSerial;
	}
	
	/**
	 * Android 7.0之后获取Mac地址
	 * 遍历循环所有的网络接口
	 * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
	 */
	private static String get7Mac() {
		String strMacAddr = null;
		try {
			// 获得IpD地址
			InetAddress ip = getLocalInetAddress();
			byte[] b = NetworkInterface.getByInetAddress(ip)
				  .getHardwareAddress();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				if (i != 0) {
					buffer.append(':');
				}
				String str = Integer.toHexString(b[i] & 0xFF);
				buffer.append(str.length() == 1 ? 0 + str : str);
			}
			strMacAddr = buffer.toString().toUpperCase();
		} catch (Exception e) {
		
		}
		
		return strMacAddr;
	}
	
	/**
	 * 获取移动设备本地IP
	 *
	 * @return
	 */
	private static InetAddress getLocalInetAddress() {
		InetAddress ip = null;
		try {
			// 列举
			Enumeration<NetworkInterface> en_netInterface = NetworkInterface
				  .getNetworkInterfaces();
			while (en_netInterface.hasMoreElements()) {// 是否还有元素
				NetworkInterface ni = (NetworkInterface) en_netInterface
					  .nextElement();// 得到下一个元素
				Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
				while (en_ip.hasMoreElements()) {
					ip = en_ip.nextElement();
					if (!ip.isLoopbackAddress()
						  && ip.getHostAddress().indexOf(":") == -1)
						break;
					else
						ip = null;
				}
				
				if (ip != null) {
					break;
				}
			}
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
		return ip;
	}
	
	
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