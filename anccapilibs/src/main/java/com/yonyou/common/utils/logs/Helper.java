package com.yonyou.common.utils.logs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

final class Helper {
	
	private Helper() {
		// Hidden constructor.
	}
	
	
	static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}
	
	
	static boolean equals(CharSequence a, CharSequence b) {
		if (a == b) return true;
		if (a != null && b != null) {
			int length = a.length();
			if (length == b.length()) {
				if (a instanceof String && b instanceof String) {
					return a.equals(b);
				} else {
					for (int i = 0; i < length; i++) {
						if (a.charAt(i) != b.charAt(i)) return false;
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
	 * in unit tests.
	 *
	 * @return Stack trace in form of String
	 */
	static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}
		
		// This is to reduce the amount of log spew that apps do in the non-error
		// condition of the network being unavailable.
		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
	
}
