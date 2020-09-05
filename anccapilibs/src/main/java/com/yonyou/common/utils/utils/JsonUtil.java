package com.yonyou.common.utils.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yonyou.common.vo.JsonObjectEx;

/*
 * @功能: ncc json 工具类
 * @Date  2020/7/22;
 * @Author zhangg
 **/
public class JsonUtil {


	public static JsonObjectEx getJsonObject(){
		return JsonObjectEx.getJsonObj();
	}
	
	/*
	 * @功能: json 字符串转换为对象
	 * @参数  dataString json字符串
	 * @参数  cls 对象类型
	 * @Date  2020/7/22;
	 * @Author zhangg
	 **/
	public static <T> T toObject(String dataString, Class<T> cls) {
		
		if (TextUtils.isEmpty(dataString)) {
			return null;
		}
		if (null == cls) {
			return null;
		}
		
		Gson gson = new Gson();
		T object = gson.fromJson(dataString, cls);
		if (null != object) {
			return object;
		}
		return null;
	}
	
	
	/*
	 * @功能: 将object对象转成json格式字符串
	 * @参数  object 要转化为json串的实体对象
	 * @Date  2020/7/22;
	 * @Author zhangg
	 **/
	public static String toJson(Object object) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		Gson gson = gsonBuilder.create();
		return gson.toJson(object);
	}
}
