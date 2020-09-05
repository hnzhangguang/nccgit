package com.yonyou.common.app;

import android.app.Application;

import com.yonyou.common.utils.litepal.LitePalNcc;
import com.yonyou.common.utils.utils.SPUtil;
import com.yonyou.common.utils.utils.ThemeUtil;

public class BaseApplication extends Application {
	
	public static BaseApplication baseApp;
	
	public static BaseApplication getBaseApp() {
		return baseApp;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		baseApp = this;
		SPUtil.init();
		LitePalNcc.initialize(this);
		LitePalNcc.getDatabase();
		ThemeUtil.loadTheme();
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
