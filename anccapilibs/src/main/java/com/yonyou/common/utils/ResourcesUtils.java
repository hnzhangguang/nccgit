package com.yonyou.common.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ResourcesUtils {
	public static String getStringResourse(Context context, int resourse) {
		return context.getResources().getString(resourse);
	}
	
	public static String getFromAssets(Context context, String fileName) {
		String result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
