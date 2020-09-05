package com.yonyou.common.utils.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yonyou.common.app.BaseApplication;

import java.util.List;

/*
 * @功能: 移动app公共工具类
 * @Date 12:10 PM 2020/7/13
 * @Author zhangg
 **/
public class AppCommonUtil {
	
	static long[] Hit2s = new long[2];
	static long[] Hit3s = new long[3];
	
	/**
	 * 检测程序是否安装
	 *
	 * @param packageName
	 * @return
	 */
	public static boolean isInstalled(Context mContext, String packageName) {
		PackageManager manager = mContext.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
		if (installedPackages != null) {
			for (PackageInfo info : installedPackages) {
				if (info.packageName.equals(packageName)) return true;
			}
		}
		return false;
	}
	
	/*
	 * @功能:判断此应用中是否存在该activity
	 * @Param activity全路径
	 * @return 是否存在
	 * @Date 12:09 PM 2020/7/13
	 * @Author zhangg
	 **/
	public static boolean isExistActivity(String activityName) {
		
		BaseApplication baseApp = BaseApplication.getBaseApp();
		if (null != baseApp) {
			Intent intent = new Intent();
			intent.setClassName(baseApp.getPackageName(), activityName);
			if (intent.resolveActivity(baseApp.getPackageManager()) == null) {
				// 说明系统中不存在这个activity
				return false;
			}
		}
		
		return true;

//		Class<?> threadClazz = null;
//		boolean isExist = true;
//		try {
//			threadClazz = Class.forName(activityName);
//			Method method = threadClazz.getMethod("initCloudChannel", Context.class);
//			method.invoke(null, baseApp);
//		} catch (Exception e) {
//			isExist = false;
//			LogerNcc.e(e);
//			e.printStackTrace();
//		} finally {
//			return isExist;
//		}
	}
	
	/*
	 * @功能:判断是否是2连击事件
	 * @Param
	 * @return
	 * @Date 12:05 PM 2020/7/13
	 * @Author zhangg
	 **/
	public static boolean lianJi2() {
		System.arraycopy(Hit2s, 1, Hit2s, 0, Hit2s.length - 1);
		Hit2s[Hit2s.length - 1] = System.currentTimeMillis();
		if (Hit2s[Hit2s.length - 1] - Hit2s[0] < 800) {
			Hit2s = null;
			Hit2s = new long[2];
			return true;
		}
		return false;
	}
	
	/*
	 * @功能:判断是否是3连击事件
	 * @Param
	 * @return
	 * @Date 12:05 PM 2020/7/13
	 * @Author zhangg
	 **/
	public static boolean lianJi3() {
		System.arraycopy(Hit3s, 1, Hit3s, 0, Hit3s.length - 1);
		Hit3s[Hit3s.length - 1] = System.currentTimeMillis();
		if (Hit3s[Hit3s.length - 1] - Hit3s[0] < 800) {
			Hit3s = null;
			Hit3s = new long[2];
			return true;
		}
		return false;
	}
	
	
	/**
	 * 手机号码，中间4位星号替换
	 *
	 * @param phone 手机号
	 * @return 星号替换的手机号
	 */
	public static String phoneNoHide(String phone) {
		// 括号表示组，被替换的部分$n表示第n组的内容
		// 正则表达式中，替换字符串，括号的意思是分组，在replace()方法中，
		// 参数二中可以使用$n(n为数字)来依次引用模式串中用括号定义的字串。
		// "(\d{3})\d{4}(\d{4})", "$1****$2"的这个意思就是用括号，
		// 分为(前3个数字)中间4个数字(最后4个数字)替换为(第一组数值，保持不变$1)(中间为*)(第二组数值，保持不变$2)
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}
	
	
	/**
	 * 邮箱，中间4位星号替换
	 *
	 * @param email 邮箱
	 * @return 星号替换的邮箱
	 */
	public static String emailHide(String email) {
		String laststring = email.substring(email.indexOf("@"));
		String firstString = email.replaceAll(laststring, "");
		int length = firstString.length();
		int len2 = length / 2;
		String substring = firstString.substring(0, len2);  // 1
		String substring1 = firstString.substring(len2, firstString.length());
		StringBuffer buffer = new StringBuffer();
		buffer.append(substring);
		for (int i = 0; i < substring1.length(); i++) {
			buffer.append("*");
		}
		buffer.append(laststring);
		return buffer.toString();
	}
	
	
}
