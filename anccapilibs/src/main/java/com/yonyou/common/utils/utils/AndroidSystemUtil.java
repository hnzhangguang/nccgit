package com.yonyou.common.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;


/*
 * @功能: 系统信息工具类
 * @Date  2020/7/24;
 * @Author zhangg
 **/
public class AndroidSystemUtil {
	
	public static final String TAG = "AndroidUtil";
	public static final String NET_WIFI = "WIFI";
	public static final String NET_4G = "4G";
	public static final String NET_3G = "3G";
	public static final String NET_2G = "2G";
	public static final String NET_UNKNOWN = "UNKNOWN";
	
	
	/**
	 * 获取当前手机系统语言。
	 *
	 * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
	 */
	public static String getSystemLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	/**
	 * 获取当前系统上的语言列表(Locale列表)
	 *
	 * @return 语言列表
	 */
	public static Locale[] getSystemLanguageList() {
		return Locale.getAvailableLocales();
	}
	
	
	/**
	 * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
	 *
	 * @return 手机IMEI
	 */
	public static String getIMEI(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
		if (tm != null) {
			return tm.getDeviceId();
		}
		return null;
	}
	
	
	/**
	 * 获取手机序列号
	 */
	public static String getDeviceId(Context context) {
		String deviceID = null;
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (!TextUtils.isEmpty(tm.getDeviceId())) {
			deviceID = tm.getDeviceId();
		}
		
		return deviceID;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	 * 说明：根据手机的分辨率将sp转成为px
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 获取应用的ApplicationId
	 *
	 * @param context
	 * @return
	 */
	public static String getApplicationId(Context context) {
		try {
			String pkName = context.getPackageName();
			return pkName;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 取得操作系统版本号
	 */
	public static String getOSVersion(Context context) {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取应用版本号
	 */
	public static String getAppVersion(Context context) {
		String strVersion = null;
		
		try {
			PackageInfo pi = null;
			pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (pi != null) {
				strVersion = pi.versionName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			LogerNcc.e(e);
		}
		
		return strVersion;
	}
	
	/**
	 * 获取签名摘要
	 */
	public static String getSign(Context context) {
		String strSign = null;
		
		try {
			int flag = PackageManager.GET_SIGNATURES;
			PackageManager pm = context.getPackageManager();
			List<PackageInfo> apps = pm.getInstalledPackages(flag);
			Object[] objs = apps.toArray();
			for (int i = 0, j = objs.length; i < j; i++) {
				PackageInfo packageinfo = (PackageInfo) objs[i];
				String packageName = packageinfo.packageName;
				if (packageName.equals(context.getPackageName())) {
					Signature[] temps = packageinfo.signatures;
					Signature tmpSign = temps[0];
					strSign = tmpSign.toCharsString();
				}
			}
		} catch (Exception e) {
		}
		return strSign;
	}
	
	/**
	 * 判断手机是否ROOT
	 */
	public static boolean isSystemRoot() {
		boolean isRoot = false;
		try {
			isRoot = (new File("/system/bin/su").exists())
				  || (new File("/system/xbin/su").exists());
			LogerNcc.e("isRoot  = " + isRoot);
		} catch (Exception e) {
		
		}
		return isRoot;
	}
	
	public static String getHostIP(Context activity) {
		
		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return hostIp;
	}
	
	/**
	 * 返回用户手机运营商标识
	 */
	public static String getProvidersName(Context context) {
		String ProvidersName = null;
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMSI = telephonyManager.getSubscriberId(); // 返回唯一的用户ID;就是这张卡的编号神马的
		if (IMSI == null)
			return "";//unkwon
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。其中
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "03";//中国移动
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "02";//中国联通
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "01";//中国电信
		}
		return ProvidersName;
	}
	
	/**
	 * 网络类型判断
	 */
	public static String getNetworkType(Context context) {
		String strNetworkType = NET_UNKNOWN;
		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
			  .getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = NET_WIFI;
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();
				// TD-SCDMA networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
						strNetworkType = NET_2G;
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by 14
					case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by 12
					case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by 15
						strNetworkType = NET_3G;
						break;
					case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by 13
						strNetworkType = NET_4G;
						break;
					default:
						// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
						if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
							  || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
							strNetworkType = NET_3G;
						} else {
							strNetworkType = _strSubTypeName;
						}
						break;
				}
			}
		}
		return strNetworkType;
	}
	
	/**
	 * 获取当前手机系统版本号
	 *
	 * @return 系统版本号
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取手机型号
	 *
	 * @return 手机型号
	 */
	public static String getSystemModel() {
		return android.os.Build.MODEL;
	}
	
	/**
	 * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
	 *
	 * @param context
	 * @return 平板返回 True，手机返回 False
	 */
	public static boolean isPad(Context context) {
		return (context.getResources().getConfiguration().screenLayout
			  & Configuration.SCREENLAYOUT_SIZE_MASK)
			  >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	/**
	 * 获取手机厂商
	 *
	 * @return 手机厂商
	 */
	public static String getDeviceBrand() {
		return android.os.Build.BRAND;
	}
}