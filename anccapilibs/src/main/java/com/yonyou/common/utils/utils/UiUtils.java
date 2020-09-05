package com.yonyou.common.utils.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.yonyou.common.app.BaseApplication;

/**
 * 描述:UI 工具类
 *
 * @author zhangg
 * @上午9:11:06
 */
public class UiUtils {
	/**
	 * @param tabNames
	 * @return
	 */
	public static String[] getStringArray(int tabNames) {
		return getResource().getStringArray(tabNames);
	}
	
	public static Resources getResource() {
		return getContext().getResources();
	}
	
	public static Context getContext() {
		return BaseApplication.getBaseApp();
	}
	
	/**
	 * @param dip
	 * @return
	 */
	public static int dip2px(int dip) {
		final float scale = getResource().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}
	
	/**
	 * @param px
	 * @return
	 */
	public static int px2dip(int px) {
		final float scale = getResource().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
	
	
	public static View inflate(int id) {
		return View.inflate(getContext(), id, null);
	}
	
	@SuppressWarnings("deprecation")
	public static Drawable getDrawalbe(int id) {
		return getResource().getDrawable(id);
	}
	
	public static int getDimens(int homePictureHeight) {
		return (int) getResource().getDimension(homePictureHeight);
	}
	
	
}
