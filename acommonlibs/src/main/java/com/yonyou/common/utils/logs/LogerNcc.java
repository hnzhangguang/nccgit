package com.yonyou.common.utils.logs;

import org.json.JSONObject;

/**
 * ncc移动日志工具类 author zhangg
 */
public final class LogerNcc {
	public static final int DEBUG = 3;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;
	public static final int INFO = 4;
	public static final int VERBOSE = 2;
	public static final int WARN = 5;
	
	public static String DEFAULT_TAG = "mmmm";
	
	private static Printer printer = new LoggerPrinter();
	
	// no instance
	private LogerNcc() {
	}
	
	/**
	 * 更改tag标识
	 *
	 * @param defaultTag
	 */
	public static void setDefaultTag(String defaultTag) {
		DEFAULT_TAG = defaultTag;
		init(DEFAULT_TAG);
	}
	
	public static Settings init() {
		return init(DEFAULT_TAG);
	}
	
	public static Settings init(String tag) {
		printer = new LoggerPrinter(tag);
		printer.getSettings().setLogLevel(LogLevel.FULL);
		return printer.init(tag);
	}
	
	public static Settings init(String tag, int methodNum) {
		printer = new LoggerPrinter(tag, methodNum);
		return printer.init(tag);
	}
	
	public static Settings init(int methodNum) {
		printer = new LoggerPrinter(DEFAULT_TAG, methodNum);
		return printer.init(DEFAULT_TAG);
	}
	
	public static void resetSettings() {
		printer.resetSettings();
	}
	
	public static Printer t(String tag) {
		return printer.t(tag, printer.getSettings().getMethodCount());
	}
	
	public static Printer t(int methodCount) {
		return printer.t(null, methodCount);
	}
	
	public static Printer t(String tag, int methodCount) {
		return printer.t(tag, methodCount);
	}
	
	public static void log(int priority, String tag, String message, Throwable throwable) {
		printer.log(priority, tag, message, throwable);
	}
	
	public static void d(String message, Object... args) {
		printer.d(message, args);
	}
	
	public static void d(Object object) {
		printer.d(object);
	}
	
	public static void e(Object message, Object... args) {
		if (null != message) {
			if (message instanceof JSONObject) {
				printer.json(message.toString());
			} else {
				printer.e(null, message.toString(), args);
			}
		} else {
			printer.e(null, "输出为空~", args);
		}
	}
	
	public static void e(Throwable throwable, String message, Object... args) {
		printer.e(throwable, message, args);
	}
	
	public static void i(String message, Object... args) {
		printer.i(message, args);
	}
	
	public static void v(String message, Object... args) {
		printer.v(message, args);
	}
	
	public static void w(String message, Object... args) {
		printer.w(message, args);
	}
	
	public static void wtf(String message, Object... args) {
		printer.wtf(message, args);
	}
	
	/**
	 * Formats the json content and print it
	 *
	 * @param json the json content
	 */
	public static void json(String json) {
		printer.json(json);
	}
	
	/**
	 * Formats the json content and print it
	 *
	 * @param xml the xml content
	 */
	public static void xml(String xml) {
		printer.xml(xml);
	}
}
