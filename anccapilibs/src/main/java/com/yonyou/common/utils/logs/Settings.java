package com.yonyou.common.utils.logs;

public final class Settings {
	
	/**
	 * Determines to how logs will be printed
	 */
	public LogLevel logLevel = LogLevel.FULL;
	private int methodCount = 2;
	private boolean showThreadInfo = true;
	private int methodOffset = 0;
	private LogAdapter logAdapter;
	
	public Settings() {
	}
	
	public Settings(LogLevel logLevel) {
		this.logLevel = logLevel;
	}
	
	public Settings hideThreadInfo() {
		showThreadInfo = false;
		return this;
	}
	
	public Settings methodCount(int methodCount) {
		if (methodCount < 0) {
			methodCount = 0;
		}
		this.methodCount = methodCount;
		return this;
	}
	
	public Settings logLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}
	
	public Settings methodOffset(int offset) {
		this.methodOffset = offset;
		return this;
	}
	
	public Settings logAdapter(LogAdapter logAdapter) {
		this.logAdapter = logAdapter;
		return this;
	}
	
	public int getMethodCount() {
		return methodCount;
	}
	
	public Settings setMethodCount(int methodCount) {
		if (methodCount < 0) {
			methodCount = 0;
		}
		this.methodCount = methodCount;
		return this;
	}
	
	public boolean isShowThreadInfo() {
		return showThreadInfo;
	}
	
	public LogLevel getLogLevel() {
		return logLevel;
	}
	
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}
	
	public int getMethodOffset() {
		return methodOffset;
	}
	
	public LogAdapter getLogAdapter() {
		if (logAdapter == null) {
			logAdapter = new AndroidLogAdapter();
		}
		return logAdapter;
	}
	
	public void reset() {
		methodCount = 2;
		methodOffset = 0;
		showThreadInfo = true;
		logLevel = LogLevel.FULL;
	}
}
