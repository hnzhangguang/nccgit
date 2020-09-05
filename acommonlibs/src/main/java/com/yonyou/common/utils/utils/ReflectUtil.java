package com.yonyou.common.utils.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 本类功能: 反射工具类
 *
 * @author zhangg
 */
public class ReflectUtil {
	
	/*
	 * @功能: 获取某个字段
	 * @Param
	 * @return
	 * @Date 7:46 PM 2020/7/11
	 * @Author zhangg
	 **/
	public static Field findField(Object instance, String name) throws NoSuchFieldException {
		
		Class<?> aClass = instance.getClass();
		
		while (null != aClass) {
			try {
				Field field = aClass.getDeclaredField(name);
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				return field;
			} catch (NoSuchFieldException e) {
				aClass = aClass.getSuperclass();
			}
		}
		throw new NoSuchFieldException("没有此字段:" + name);
	}
	
	/*
	 * @功能:获取某个方法
	 * @Param  对象实例,方法名称, 方法参数类型列表
	 * @return
	 * @Date 7:48 PM 2020/7/11
	 * @Author zhangg
	 **/
	public static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
		  throws NoSuchMethodException {
		
		Class<?> aClass = instance.getClass();
		
		while (null != aClass) {
			try {
				Method method = aClass.getDeclaredMethod(name, parameterTypes);
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				return method;
			} catch (NoSuchMethodException e) {
				aClass = aClass.getSuperclass();
			}
		}
		throw new NoSuchMethodException("没有此方法:" + name);
	}
}
