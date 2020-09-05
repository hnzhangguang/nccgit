package com.yonyou.common.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MTLLog {
	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = 2;
	
	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = 3;
	
	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = 4;
	
	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = 5;
	
	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6;
	
	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;
	
	
	public static Boolean LOGGER_SWITCH = true; // 日志文件总开关
	private static Boolean MYLOG_WRITE_TO_FILE = false;// 日志写入文件开关
	private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	private static String MYLOG_PATH_SDCARD_DIR = "/sdcard/";// 日志文件在sdcard中的路径
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数
	private static String MYLOGFILEName = "MTLLog.txt";// 本类输出的日志文件名称
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
	public Context context;
	
	public static void d(String tag, String text) {
		log(tag, text, 'd');
	}
	
	public static void d(String tag, String text, Throwable tr) {
		log(tag, text, tr, 'd');
	}
	
	public static void e(String tag, String text) {
		log(tag, text, 'e');
	}
	
	public static void e(String tag, String text, Throwable tr) {
		log(tag, text, tr, 'e');
	}
	
	public static void i(String tag, String text) {
		log(tag, text, 'i');
	}
	
	public static void i(String tag, String text, Throwable tr) {
		log(tag, text, tr, 'i');
	}
	
	public static void v(String tag, String text) {
		log(tag, text, 'v');
	}
	
	public static void v(String tag, String text, Throwable tr) {
		log(tag, text, tr, 'v');
	}
	
	public static void w(String tag, String text) {
		log(tag, text, 'w');
	}
	
	public static void w(String tag, Throwable tr) {
		log(tag, tr, 'w');
	}
	
	public static void w(String tag, String text, Throwable tr) {
		log(tag, text, tr, 'w');
	}
	
	public static boolean isLoggable(String tag, int level) {
		return Log.isLoggable(tag, level);
	}
	
	/**
	 * 根据tag, msg和等级，输出日志
	 *
	 * @param tag
	 * @param msg
	 * @param level
	 * @return void
	 */
	private static void log(String tag, String msg, char level) {
		if (LOGGER_SWITCH) {
			if ('e' == level) {
				Log.e(tag, msg);
			} else if ('w' == level) {
				Log.w(tag, msg);
			} else if ('d' == level) {
				Log.d(tag, msg);
			} else if ('i' == level) {
				Log.i(tag, msg);
			} else {
				Log.v(tag, msg);
			}
		}
	}
	
	private static void log(String tag, String msg, Throwable tr, char level) {
		if (LOGGER_SWITCH) {
			if ('e' == level) {
				Log.e(tag, msg, tr);
			} else if ('w' == level) {
				Log.w(tag, msg, tr);
			} else if ('d' == level) {
				Log.d(tag, msg, tr);
			} else if ('i' == level) {
				Log.i(tag, msg, tr);
			} else {
				Log.v(tag, msg, tr);
			}
		}
	}
	
	private static void log(String tag, Throwable tr, char level) {
		if (LOGGER_SWITCH) {
			if ('w' == level) {
				Log.w(tag, tr);
			}
		}
	}
	
	/**
	 * 打开日志文件并写入日志
	 *
	 * @return
	 **/
	private static void writeLogtoFile(String mylogtype, String tag, String text) {
		Date nowtime = new Date();
		String needWriteFiel = logfile.format(nowtime);
		String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype
			  + "    " + tag + "    " + text;
		File dirPath = Environment.getExternalStorageDirectory();
		File file = new File(dirPath.toString(), needWriteFiel + MYLOGFILEName);
		try {
			FileWriter filerWriter = new FileWriter(file, true);
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除制定的日志文件
	 */
	
	public static void delFile() {
		String needDelFiel = logfile.format(getDateBefore());
		File dirPath = Environment.getExternalStorageDirectory();
		File file = new File(dirPath, needDelFiel + MYLOGFILEName);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 */
	private static Date getDateBefore() {
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE)
			  - SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}
}