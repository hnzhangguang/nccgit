package com.yonyou.common.utils;

import android.content.Context;

import org.json.JSONObject;

public class CommonRes {
	
	
	public static JSONObject appConfig = new JSONObject();
	
	private static Context context;
	
	private CommonRes() {
	}
	
	public static synchronized void initResourceValue(Context ctx) {
		context = ctx;
		
	}
}
