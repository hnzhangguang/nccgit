package com.yonyou.plugins.system;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.utils.AndroidSystemUtil;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SystemApiInvoker implements IApiInvoker {
	
	private static final String IS_LOGS = "isLogs";
	private static final String SET_LOGS = "setLogs";
	private static final String QUIT = "quit";
	
	private static final String REMOVE_BADGE = "removeAppBadge";
	private static final String SET_BADGE = "setAppBadge";
	private static final String OPEN_FILE = "openFile";
	private static final String GET_STATUSBAR_HEIGHT = "getStatusBarHeight";
	private static final String GET_SystemInfo = "getStemInfo";  // 获取系统信息
	
	@Override
	public String call(String apiname, MTLArgs args) throws MTLException {
		switch (apiname) {
			case GET_SystemInfo:
				getStemInfo(args);
				return "";
			case IS_LOGS:
				getIsLog(args);
				return "";
			case SET_LOGS:
				setLog(args);
				return "";
			case QUIT:
				quit(args);
				return "";
			case SET_BADGE:
				int badgeNum = args.getInteger("number");
				MTLSystemBadge.setBadge(args.getContext(), badgeNum);
				args.success();
				return "";
			case REMOVE_BADGE:
				MTLSystemBadge.removeBadge(args.getContext());
				args.success();
				return "";
			case OPEN_FILE:
				FileControl.openFile(args);
				return "";
			case GET_STATUSBAR_HEIGHT:
				getStatusBarHeight(args);
				return "";
			
		}
		throw new MTLException(apiname + ": function not found");
	}
	
	
	/*
	 * @功能: 获取系统信息
	 * @参数  ;
	 * @Date  2020/8/26;
	 * @Author zhangg
	 **/
	private void getStemInfo(MTLArgs args) {
		JSONObject json = new JSONObject();
		try {
			String model = AndroidSystemUtil.getSystemModel();//获取手机型号
			String brand = AndroidSystemUtil.getDeviceBrand();//获取手机厂商
			String version = AndroidSystemUtil.getSystemVersion();//获取手机系统版本
			
			json.put("osversion", version);
			json.put("model", model);
			json.put("brand", brand);
			args.success(json);
		} catch (JSONException e) {
			e.printStackTrace();
			args.error("获取失败");
		}
	}
	
	
	private void getIsLog(MTLArgs args) {
		JSONObject json = new JSONObject();
		try {
			json.put("isLog", MTLLog.LOGGER_SWITCH);
			args.success(json);
		} catch (JSONException e) {
			e.printStackTrace();
			args.error("获取失败");
		}
	}
	
	private void setLog(MTLArgs args) {
		boolean log = args.getBoolean("log");
		MTLLog.LOGGER_SWITCH = log;
		args.success();
	}
	
	private void quit(MTLArgs args) {
		ActivityManager activityManager =
			  (ActivityManager) args.getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.AppTask> appTaskList = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			appTaskList = activityManager.getAppTasks();
			for (ActivityManager.AppTask appTask : appTaskList) {
				appTask.finishAndRemoveTask();
			}
			Process.killProcess(Process.myPid());
			System.exit(0);
		} else {
			System.exit(0);
		}
	}
	
	/**
	 * 获取状态栏高度
	 * 该方法获取需要在onWindowFocusChanged方法回调之后
	 *
	 * @param args
	 * @return
	 */
	public int getStatusBarHeight(MTLArgs args) {
		Context context = args.getContext();
		int statusBarHeight = getStatusBarByResId(context);
		if (statusBarHeight <= 0) {
			statusBarHeight = getStatusBarByReflex(context);
		}
		if (statusBarHeight > 0) {
			args.success("statusBarHeight", statusBarHeight, false);
		} else {
			args.error("状态栏高度获取失败");
		}
		return statusBarHeight;
	}
	
	/**
	 * 通过状态栏资源id来获取状态栏高度
	 *
	 * @param context
	 * @return
	 */
	private int getStatusBarByResId(Context context) {
		int height = 0;
		//获取状态栏资源id
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			try {
				height = context.getResources().getDimensionPixelSize(resourceId);
			} catch (Exception e) {
			}
		}
		return height;
	}
	
	/**
	 * 通过Activity的内容距离顶部高度来获取状态栏高度，该方法获取需要在onWindowFocusChanged回调之后
	 *
	 * @param activity
	 * @return
	 */
	//    public int getStatusBarByTop(Activity activity) {
	//        Rect rect = new Rect();
	//        Window window = activity.getWindow();
	//        window.getDecorView().getWindowVisibleDisplayFrame(rect);
	//        return rect.top;
	//    }
	
	/**
	 * 通过反射获取状态栏高度
	 *
	 * @param context
	 * @return
	 */
	private int getStatusBarByReflex(Context context) {
		int statusBarHeight = 0;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
				  .get(object).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
}
