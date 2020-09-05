package com.yonyou.common.onActivityForResult;

import android.app.Activity;
import android.content.Intent;

/**
 * OnActivityForResultUtils
 * 支持Android的OnActivityForResult方法
 */
public class OnActivityForResultUtils {
	//    public static Map<Integer, List<OnActivityForResultCallback>> resultMap = new
	//    HashMap<Integer, List<OnActivityForResultCallback>>();
	
	private static OnActivityForResultCallback onActivityForResultCallback;
	
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_CANCELED == resultCode) {
			onActivityForResultCallback.cancel(data);
		} else {
			onActivityForResultCallback.success(resultCode, data);
		}
	}
	
	public static void startActivityForResult(Activity activity, Integer requestCode, Intent intent,
	                                          OnActivityForResultCallback callback) {
		onActivityForResultCallback = callback;
		activity.startActivityForResult(intent, requestCode);
	}
	
	
}
