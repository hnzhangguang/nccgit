package com.yonyou.nccmob.base;

import android.content.Context;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;

import com.yonyou.common.app.BaseApplication;
import com.yonyou.common.utils.logs.LogerNcc;

import org.xutils.x;

import java.lang.reflect.Method;

public class NccAppliction extends BaseApplication {
	
	public static NccAppliction appliction = null;
	// 是否是debug环境
	public static boolean isDebug = BuildConfig.DEBUG;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// init aliCloud
		//    initAliPush();
		// init baidu map
//		initBaiduMap();
		
		// 多 dex
		MultiDex.install(this);
		
		appliction = this;
		// webView history report ...
		x.Ext.init(this);
		LogerNcc.init("mmmm", 2);
		
	}
	
	/**
	 * 初始化百度地图
	 */
	private void initBaiduMap() {
		
		Class<?> threadClazz = null;
		try {
			threadClazz = Class.forName("com.yonyou.plugin.loc.BaiduMapUtil");
			Method method = threadClazz.getMethod("init", Context.class);
			method.invoke(null, this);
		} catch (Exception e) {
			LogerNcc.e(e);
			e.printStackTrace();
		}
	}
	
	private void initAliPush() {
		Class<?> threadClazz = null;
		try {
			threadClazz = Class.forName("com.yonyou.mtlaliyunpush.MTLAliyunPush");
			Method method = threadClazz.getMethod("initCloudChannel", Context.class);
			method.invoke(null, this);
		} catch (Exception e) {
			LogerNcc.e(e);
			e.printStackTrace();
		}
	}
}
