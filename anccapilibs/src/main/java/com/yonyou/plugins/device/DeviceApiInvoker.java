package com.yonyou.plugins.device;

import com.yonyou.common.utils.MTLLog;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

public class DeviceApiInvoker implements IApiInvoker {
	
	private static final String GET_TERMINAL_TYPE = "getTerminalType";
	
	private static final String GET_OS_VERSION = "getOSVersion";
	private static final String NETWORK_TYPE = "getNetworkType";
	private static final String GET_MAC = "getMac";
	
	private static final String GET_IMEI = "getImei";
	private static final String GET_IMSI = "getImsi";
	private static final String GET_MODEL = "getDeviceModel";
	private static final String GET_VENDOR = "getVendor";
	private static final String GET_UUID = "getUUID";
	private static final String DAIL = "dail";
	
	private static final String LOCK_ORIENTATION = "lockOrientation";
	private static final String UNLOCK_ORIENTATION = "unlockOrientation";
	
	@Override
	public String call(String apiname, MTLArgs args) throws MTLException {
		switch (apiname) {
			case NETWORK_TYPE:
				DeviceInfo.getNetworkType(args);
				return "";
			case GET_MAC:
				DeviceInfo.getMac(args);
				return "";
			case GET_IMEI:
				MTLLog.i("MTL___DEVICE", GET_IMEI);
				DeviceInfo.getDeviceId(args, "imei");
				return "";
			case GET_IMSI:
				MTLLog.i("MTL___DEVICE", GET_IMSI);
				DeviceInfo.getImsi(args);
				return "";
			case GET_MODEL:
				DeviceInfo.getDeviceModel(args);
				return "";
			case GET_VENDOR:
				DeviceInfo.getVendor(args);
				return "";
			case GET_UUID:
				DeviceInfo.getDeviceId(args, "UUID");
				return "";
			case DAIL:
				DeviceInfo.dial(args);
				return "";
			case GET_OS_VERSION:
				DeviceInfo.getOSVersion(args);
				return "";
			case LOCK_ORIENTATION:
				DeviceInfo.lockOrientation(args);
				return "";
			case UNLOCK_ORIENTATION:
				DeviceInfo.unlockOrientation(args);
				return "";
			case GET_TERMINAL_TYPE:
				DeviceInfo.getTerminalType(args);
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
}
