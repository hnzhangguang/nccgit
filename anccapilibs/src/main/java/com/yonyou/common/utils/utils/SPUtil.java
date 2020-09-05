package com.yonyou.common.utils.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.yonyou.common.app.BaseApplication;
import com.yonyou.common.utils.logs.LogerNcc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SPUtil {
	
	// 文件名称为config
	public static final String ncc_login = "config_login"; // 登录相关配置
	public static final String ncc_normal = "config_normal"; // 常规配置
	public static final String ncc_setting = "config_setting"; // 设置相关配置
	public static final String nccKey = "configNcc"; // 设置相关配置
	public static final String XY_SETTING_SPEAK = "xy_setting_speak";
	
	public static SharedPreferences sharedPreferences;
	public static SharedPreferences configLogin;
	public static SharedPreferences configNormal;
	public static SharedPreferences configSetting;
	
	public SPUtil() {
		configLogin = BaseApplication.baseApp.getSharedPreferences(ncc_login, Context.MODE_PRIVATE);
		configNormal = BaseApplication.baseApp.getSharedPreferences(ncc_normal, Context.MODE_PRIVATE);
		configSetting = BaseApplication.baseApp.getSharedPreferences(ncc_setting, Context.MODE_PRIVATE);
	}
	
	// 创建SharePreference对象时要上下文和存储的模式
	// 通过构造方法传入一个上下文
	public SPUtil(Context context, String setting) {
		// 实例化SharePreference对象，使用的是get方法，而不是new创建
		// 第一个参数是文件的名字
		// 第二个参数是存储的模式，一般都是使用私有方式：Context.MODE_PRIVATE
		sharedPreferences = context.getSharedPreferences(setting, Context.MODE_PRIVATE);
	}
	
	public static void init() {
		new SPUtil();
	}
	
	
	/**
	 * 写入String变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值String
	 */
	public static void putStringForNormal(String key, String value) {
		configNormal.edit().putString(key, value).commit();
	}
	
	/**
	 * 读取String标识从sharedPreferences中
	 *
	 * @param key      存储节点名称
	 * @param defValue 没有此节点的默认值
	 * @return 返回默认值或者此节点读取到的结果
	 */
	public static String getStringForNormal(String key, String defValue) {
		return configNormal.getString(key, defValue);
	}
	
	
	/**
	 * 写入String变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值String
	 */
	public static void putStringForSetting(String key, String value) {
		configSetting.edit().putString(key, value).commit();
	}
	
	/**
	 * 读取String标识从sharedPreferences中
	 *
	 * @param key      存储节点名称
	 * @param defValue 没有此节点的默认值
	 * @return 返回默认值或者此节点读取到的结果
	 */
	public static String getStringForSetting(String key, String defValue) {
		return configSetting.getString(key, defValue);
	}
	
	
	/**
	 * 写入String变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值String
	 */
	public static void putStringForLogin(String key, String value) {
		configLogin.edit().putString(key, value).commit();
	}
	
	/**
	 * 读取String标识从sharedPreferences中
	 *
	 * @param key      存储节点名称
	 * @param defValue 没有此节点的默认值
	 * @return 返回默认值或者此节点读取到的结果
	 */
	public static String getStringForLogin(String key, String defValue) {
		return configLogin.getString(key, defValue);
	}
	
	
	/**
	 * 写入Boolean变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值
	 */
	public static void putBooleanNormal(String key, boolean value) {
		configNormal.edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 读取boolean标识从sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 没有此节点的默认值
	 * @return 默认值或者此节点读取到的结果
	 */
	public static boolean getBooleanNormal(String key, boolean value) {
		return configNormal.getBoolean(key, value);
	}
	
	
	/**
	 * 写入Boolean变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值
	 */
	public static void putBooleanLogin(String key, boolean value) {
		configLogin.edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 读取boolean标识从sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 没有此节点的默认值
	 * @return 默认值或者此节点读取到的结果
	 */
	public static boolean getBooleanLogin(String key, boolean value) {
		return configLogin.getBoolean(key, value);
	}
	
	/***
	 * 写对象
	 *
	 * @param sp
	 * @param key
	 * @param object
	 * @author zhangg
	 */
	public static void writeObject(SharedPreferences sp, String key, Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(baos);
			out.writeObject(object);
			String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, objectVal);
			editor.commit();
		} catch (IOException e) {
			LogerNcc.e(e);
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * 读对象
	 *
	 * @param sp
	 * @param key
	 * @param clazz
	 * @return
	 * @author zhangg
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readObject(SharedPreferences sp, String key, Class<T> clazz) {
		T t = null;
		if (sp.contains(key)) {
			String objectVal = sp.getString(key, null);
			byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				t = (T) ois.readObject();
				return t;
			} catch (Exception e) {
				LogerNcc.e(e);
				e.printStackTrace();
			} finally {
				try {
					if (bais != null) {
						bais.close();
					}
					if (ois != null) {
						ois.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return t;
	}
	
	/**
	 * 写入String变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值String
	 */
	public void putString(String key, String value) {
		// 存储节点文件的名称，读写方式
		check();
		sharedPreferences.edit().putString(key, value).commit();
	}
	
	/**
	 * 读取String标识从sharedPreferences中
	 *
	 * @param key      存储节点名称
	 * @param defValue 没有此节点的默认值
	 * @return 返回默认值或者此节点读取到的结果
	 */
	public String getString(String key, String defValue) {
		// 存储节点文件的名称，读写方式
		check();
		return sharedPreferences.getString(key, defValue);
	}
	
	/**
	 * 写入int变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值String
	 */
	public void putInt(String key, int value) {
		// 存储节点文件的名称，读写方式
		check();
		sharedPreferences.edit().putInt(key, value).commit();
	}
	
	/**
	 * 读取int标识从sharedPreferences中
	 *
	 * @param key      存储节点名称
	 * @param defValue 没有此节点的默认值
	 * @return 返回默认值或者此节点读取到的结果
	 */
	public int getInt(String key, int defValue) {
		// 存储节点文件的名称，读写方式
		check();
		return sharedPreferences.getInt(key, defValue);
	}
	
	/**
	 * 写入int变量至sharedPreferences中
	 *
	 * @param key   存储节点名称
	 * @param value 存储节点的值String
	 */
	public void putLong(String key, long value) {
		// 存储节点文件的名称，读写方式
		check();
		sharedPreferences.edit().putLong(key, value).commit();
	}
	
	/**
	 * 读取int标识从sharedPreferences中
	 *
	 * @param key      存储节点名称
	 * @param defValue 没有此节点的默认值
	 * @return 返回默认值或者此节点读取到的结果
	 */
	public long getLong(String key, long defValue) {
		// 存储节点文件的名称，读写方式
		check();
		return sharedPreferences.getLong(key, defValue);
	}
	
	/**
	 * 从sharedPreferences中移除指定节点
	 *
	 * @param key 需要移除节点的名称
	 */
	public void remove(String key) {
		// 存储节点文件的名称，读写方式
		check();
		sharedPreferences.edit().remove(key).commit();
	}
	
	public void check() {
		if (sharedPreferences == null) {
			sharedPreferences =
				  BaseApplication.baseApp.getSharedPreferences(nccKey, Context.MODE_PRIVATE);
		}
	}
	
	public void put(String key, Object value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		// 如果是字符型类型
		if (value instanceof String) {
			editor.putString(key, value.toString()).commit();
		}
		// 如果是int类型
		if (value instanceof Integer) {
			editor.putInt(key, Integer.parseInt(value.toString())).commit();
		}
		// 如果是Long类型
		if (value instanceof Long) {
			editor.putLong(key, Long.parseLong(value.toString())).commit();
		}
		// 如果是布尔类型
		if (value instanceof Boolean) {
			editor.putBoolean(key, Boolean.parseBoolean(value.toString())).commit();
		}
	}
	
	// 一次可以传入多个ContentValue对象的值
	public void putValues(ContentValue... contentValues) {
		// 获取SharePreference对象的编辑对象，才能进行数据的存储
		SharedPreferences.Editor editor = sharedPreferences.edit();
		// 数据分类和存储
		for (ContentValue contentValue : contentValues) {
			// 如果是字符型类型
			if (contentValue.value instanceof String) {
				editor.putString(contentValue.key, contentValue.value.toString()).commit();
			}
			// 如果是int类型
			if (contentValue.value instanceof Integer) {
				editor.putInt(contentValue.key, Integer.parseInt(contentValue.value.toString())).commit();
			}
			// 如果是Long类型
			if (contentValue.value instanceof Long) {
				editor.putLong(contentValue.key, Long.parseLong(contentValue.value.toString())).commit();
			}
			// 如果是布尔类型
			if (contentValue.value instanceof Boolean) {
				editor
					  .putBoolean(contentValue.key, Boolean.parseBoolean(contentValue.value.toString()))
					  .commit();
			}
		}
	}
	
	// 获取数据的方法
	public String getString(String key) {
		return sharedPreferences.getString(key, null);
	}
	
	public boolean getBoolean(String key, Boolean b) {
		return sharedPreferences.getBoolean(key, b);
	}
	
	public int getInt(String key) {
		return sharedPreferences.getInt(key, -1);
	}
	
	public long getLong(String key) {
		return sharedPreferences.getLong(key, -1);
	}
	
	// 清除当前文件的所有的数据
	public void clear() {
		sharedPreferences.edit().clear().commit();
	}
	
	/**
	 * 存储数据 这里要对存储的数据进行判断在存储 只能存储简单的几种数据 这里使用的是自定义的ContentValue类，来进行对多个数据的处理
	 */
	// 创建一个内部类使用，里面有key和value这两个值
	public static class ContentValue {
		String key;
		Object value;
		
		// 通过构造方法来传入key和value
		public ContentValue(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}
	
	
}
