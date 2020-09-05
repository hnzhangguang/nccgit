package com.yonyou.plugins.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MtlDeviceManager {
	
	public static final String NO_DISPLAY = "no display";
	private static MtlDeviceManager mDeviceManager;
	private Context context;
	
	//    构造函数私有化
	private MtlDeviceManager() {
		super();
	}
	
	//    提供一个全局的静态方法
	public static MtlDeviceManager sharedManager() {
		if (mDeviceManager == null) {
			synchronized (MtlDeviceManager.class) {
				if (mDeviceManager == null) {
					mDeviceManager = new MtlDeviceManager();
				}
			}
		}
		return mDeviceManager;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public JSONObject jsNetworkType() {
		String networkType = getNetworkType();
		JSONObject json = new JSONObject();
		try {
			json.put("networkType", networkType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 获取当前网络类型
	 **/
	public String getNetworkType() {
		if (context == null) {
			return NO_DISPLAY;
		}
		//        结果返回值
		String netType = NO_DISPLAY;
		//        获取手机所有连接管理对象
		ConnectivityManager manager =
			  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//        获取NetworkInfo对象
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		//        NetworkInfo对象为空 则代表没有网络
		if (networkInfo == null) {
			return netType;
		}
		//        否则 NetworkInfo对象不为空 则获取该networkInfo的类型
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_WIFI) {
			//            WIFI
			netType = "wifi";
		} else if (nType == ConnectivityManager.TYPE_MOBILE) {
			int nSubType = networkInfo.getSubtype();
			TelephonyManager telephonyManager =
				  (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
				  && !telephonyManager.isNetworkRoaming()) {
				//                4G 网络
				netType = "4g";
			} else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
				  || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
				  || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
				  && !telephonyManager.isNetworkRoaming()) {
				//                3G网络   联通的3G为UMTS或HSDPA 电信的3G为EVDO
				netType = "3g";
			} else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
				  || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
				  || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
				  && !telephonyManager.isNetworkRoaming()) {
				//                2G网络 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
				netType = "2g";
			} else
				netType = NO_DISPLAY;
		}
		return netType;
	}
	
	/**
	 * 获取WIFI信息
	 **/
	public WifiInfo fetchSSIDInfo() {
		
		WifiManager manager =
			  (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return manager.getConnectionInfo();
	}
	
	/**
	 * 获取WIFI名字
	 **/
	public String getWifiSSID() {
		
		return fetchSSIDInfo().getSSID().replace("\"", "");
	}
	
	/**
	 * 获取WIFi的BSSID
	 **/
	public String getWifiBSSID() {
		
		return fetchSSIDInfo().getBSSID();
	}
	
	/**
	 * 获取WIFi的MAC地址
	 **/
	public String getWifiMacAddress() {
		
		return fetchSSIDInfo().getMacAddress();
	}
	
	/**
	 * 获取Wifi信号强度
	 * 0到-100的区间值
	 * 0到-50表示信号最好
	 * -50到-70表示信号偏差
	 * 小于-70表示最差
	 **/
	public int getWifiSignalStrength() {
		
		return fetchSSIDInfo().getRssi();
	}
	
	/**
	 * 获取设备IP地址
	 **/
	public String getIPAddress() {
		
		return getCorrectIPAddress(fetchSSIDInfo().getIpAddress());
	}
	
	/**
	 * 获取开启热点时设备IP地址
	 **/
	public String getServerAddress() {
		WifiManager wifiManager =
			  (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		DhcpInfo dhcpinfo = wifiManager.getDhcpInfo();
		String serverAddress = getCorrectIPAddress(dhcpinfo.serverAddress);
		return serverAddress;
	}
	
	/**
	 * 获取WIFI热点的状态
	 **/
	public HOTSPOT getWifiApState() {
		WifiManager wifiManager =
			  (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			return HOTSPOT.getType((int) method.invoke(wifiManager));
		} catch (Exception e) {
			return HOTSPOT.WIFI_AP_STATE_FAILED;
		}
	}
	
	/**
	 * 判断Wifi热点是否可用
	 **/
	public boolean isApEnabled() {
		return getWifiApState() == HOTSPOT.WIFI_AP_STATE_ENABLED ? true : false;
	}
	
	/**
	 * 获取连接到当前热点的设备IP
	 **/
	public ArrayList<String> getConnectedHotIP() {
		ArrayList<String> connectedIP = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
				  "/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");
				if (splitted != null && splitted.length >= 4) {
					String ip = splitted[0];
					connectedIP.add(ip);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		connectedIP.remove(0);
		return connectedIP;
	}
	
	/**
	 * 将获取的int转为真正的ip地址
	 **/
	private String getCorrectIPAddress(int iPAddress) {
		StringBuilder sb = new StringBuilder();
		sb.append(iPAddress & 0xFF).append(".");
		sb.append((iPAddress >> 8) & 0xFF).append(".");
		sb.append((iPAddress >> 16) & 0xFF).append(".");
		sb.append((iPAddress >> 24) & 0xFF);
		return sb.toString();
	}
}
