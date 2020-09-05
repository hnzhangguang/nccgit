package com.yonyou.plugins;

public interface IApiInvoker {
	
	String call(String apiname, MTLArgs args) throws MTLException;
	
}
