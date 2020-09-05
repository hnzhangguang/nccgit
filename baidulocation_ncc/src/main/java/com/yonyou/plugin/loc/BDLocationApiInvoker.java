package com.yonyou.plugin.loc;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.yonyou.plugin.utils.LocationCallback;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONObject;

public class BDLocationApiInvoker implements IApiInvoker {
	
	private static final String INIT_LOCATION = "initLocation";
	private static final String OPEN_LOCATION = "openLocation";
	private static final String GET_LOCATION = "getLocation";
	private static final String STOP_LOCATION = "stopLocation";
	
	@Override
	public String call(String apiname, final MTLArgs args) throws MTLException {
		Activity context = args.getContext();
		switch (apiname) {
			case STOP_LOCATION:
				
				LocationService.getLocationService().setContext(context);
				LocationService.getLocationService().stopLocation();
				return "";
			case INIT_LOCATION:
				// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
				SDKInitializer.initialize(args.getContext().getApplicationContext());
				//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
				//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
				SDKInitializer.setCoordType(CoordType.BD09LL);
				return "";
			case OPEN_LOCATION:
				// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
				SDKInitializer.initialize(args.getContext().getApplicationContext());
				//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
				//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
				SDKInitializer.setCoordType(CoordType.BD09LL);
				Intent intent = new Intent();
				intent.setClass(context, LocationActivity.class);
				intent.putExtra("scale", args.getInteger("scale", 28));
				intent.putExtra("lat", args.getDouble("latitude"));
				intent.putExtra("lng", args.getDouble("longitude"));
				intent.putExtra("name", args.getString("name"));
				intent.putExtra("address", args.getString("address"));
				context.startActivity(intent);
				return "";
			case GET_LOCATION:
				String type = args.getString("type");
				// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
				SDKInitializer.initialize(args.getContext().getApplicationContext());
				//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
				//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
				if (TextUtils.isEmpty(type) && type.equalsIgnoreCase("gcj02")) {
					SDKInitializer.setCoordType(CoordType.GCJ02);
				} else {
					SDKInitializer.setCoordType(CoordType.BD09LL);
				}
				
				LocationService.getLocationService().setContext(context);
				LocationService.getLocationService().getLocation(new LocationCallback() {
					@Override
					public void onResult(JSONObject jsonObject) {
						args.getCallback().success(jsonObject);
					}
					
					@Override
					public void onError(String errMsg) {
						args.getCallback().error(errMsg);
					}
				});
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
}
