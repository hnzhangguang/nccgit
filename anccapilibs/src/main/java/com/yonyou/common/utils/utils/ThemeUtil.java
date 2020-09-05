package com.yonyou.common.utils.utils;

import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.yonyou.common.utils.user.UserUtil;

/**
 * app 主题工具类
 */
public class ThemeUtil {
	
	// 亮色主题
	public static final String LIGHT_MODE = "light";
	// 黑色主题
	public static final String DARK_MODE = "dark";
	// 默认主题
	public static final String DEFAULT_MODE = "default";
	// 记录当前应用主题key
	public static final String CURRENT_MODE = "currentMode";
	
	public static void loadTheme() {
		String string = UserUtil.getValueByKey(ThemeUtil.CURRENT_MODE, DEFAULT_MODE);
		if (LIGHT_MODE.equals(string)) {
			ThemeUtil.applyTheme(ThemeUtil.LIGHT_MODE);
		}
		if (DARK_MODE.equals(string)) {
			ThemeUtil.applyTheme(ThemeUtil.DARK_MODE);
		}
	}
	
	/**
	 * 切换主题
	 */
	public static void changeTheme() {
		String string = UserUtil.getValueByKey(ThemeUtil.CURRENT_MODE, DEFAULT_MODE);
		if (LIGHT_MODE.equals(string)) {
			applyTheme(DARK_MODE);
		} else if (DARK_MODE.equals(string)) {
			applyTheme(LIGHT_MODE);
		} else {
			applyTheme(DEFAULT_MODE);
		}
	}
	
	/**
	 * 引用主题色
	 *
	 * @param mode
	 */
	public static void applyTheme(String mode) {
		if (mode.equals(LIGHT_MODE)) {
			UserUtil.setKeyValue(ThemeUtil.CURRENT_MODE, mode);
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}
		if (mode.equals(DARK_MODE)) {
			UserUtil.setKeyValue(ThemeUtil.CURRENT_MODE, mode);
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}
		if (mode.equals(DEFAULT_MODE)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				// 跟随系统Theme 切换主题
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
				UserUtil.setKeyValue(ThemeUtil.CURRENT_MODE, ThemeUtil.DEFAULT_MODE);
			} else {
				// 根据电量 自动切换
//				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
//				edit.putString(ThemeUtil.CURRENT_MODE, ThemeUtil.DEFAULT_MODE).commit();
			}
		}
	}
	
	/**
	 * 判断是否是night主题
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNightMode(Context context) {
		String string = UserUtil.getValueByKey(ThemeUtil.CURRENT_MODE, DEFAULT_MODE);
		if (LIGHT_MODE.equals(string)) {
			return true;
		}
		return false;
	}
}
