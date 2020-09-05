package com.yonyou.plugins.system;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.yonyou.plugins.MTLArgs;

import java.io.File;
import java.util.Locale;

/**
 * author lgy
 */
public class FileControl {
	private static final String DATA_TYPE_ALL = "*/*";//未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
	private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
	private static final String DATA_TYPE_VIDEO = "video/*";
	private static final String DATA_TYPE_AUDIO = "audio/*";
	private static final String DATA_TYPE_HTML = "text/html";
	private static final String DATA_TYPE_IMAGE = "image/*";
	private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
	private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
	private static final String DATA_TYPE_WORD = "application/msword";
	private static final String DATA_TYPE_CHM = "application/x-chm";
	private static final String DATA_TYPE_TXT = "text/plain";
	private static final String DATA_TYPE_PDF = "application/pdf";
	
	
	public static void openFile(MTLArgs args) {
		String filePath = args.getString("filePath");
		String fileType = args.getString("fileType");
		File file = new File(filePath);
		if (!file.exists()) {
			args.error("文件不存在");
			return;
		}
		if (TextUtils.isEmpty(fileType)) {
			fileType =
				  file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase(Locale.getDefault());
		}
		openFile(args.getContext(), file, fileType);
		args.success();
	}
	
	public static boolean openFile(Context context, String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}
		openFile(context, file, "");
		return true;
	}
	
	public static void openFile(Context context, File file, String type) {
		if (TextUtils.isEmpty(type)) {
			type = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase(Locale.getDefault());
		} else {
			type = type.toLowerCase(Locale.getDefault());
		}
		Intent intent;
		if (type.equals("m4a") || type.equals("mp3") || type.equals("mid") || type.equals("xmf") || type.equals("ogg") || type.equals("wav")) {
			intent = generateVideoAudioIntent(context, file, DATA_TYPE_AUDIO);
		} else if (type.equals("3gp") || type.equals("mp4")) {
			intent = generateVideoAudioIntent(context, file, DATA_TYPE_VIDEO);
		} else if (type.equals("jpg") || type.equals("gif") || type.equals("png") || type.equals("jpeg"
		) || type.equals("bmp")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_IMAGE);
		} else if (type.equals("apk")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_APK);
		} else if (type.equals("html") || type.equals("htm")) {
			intent = generateHtmlFileIntent(file.getPath());
		} else if (type.equals("ppt") || type.equals("pptx")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_PPT);
		} else if (type.equals("xls") || type.equals("xlsx")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_EXCEL);
		} else if (type.equals("doc") || type.equals("docx")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_WORD);
		} else if (type.equals("pdf")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_PDF);
		} else if (type.equals("chm")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_CHM);
		} else if (type.equals("txt")) {
			intent = generateCommonIntent(context, file, DATA_TYPE_TXT);
		} else {
			intent = generateCommonIntent(context, file, DATA_TYPE_ALL);
		}
		context.startActivity(intent);
	}
	
	/**
	 * 产生打开视频或音频的Intent
	 *
	 * @param file     文件
	 * @param dataType 文件类型
	 * @return
	 */
	private static Intent generateVideoAudioIntent(Context context, File file, String dataType) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		intent.setDataAndType(getUri(context, intent, file), dataType);
		return intent;
	}
	
	/**
	 * 产生打开网页文件的Intent
	 *
	 * @param filePath 文件路径
	 * @return
	 */
	private static Intent generateHtmlFileIntent(String filePath) {
		Uri uri = Uri.parse(filePath)
			  .buildUpon()
			  .encodedAuthority("com.android.htmlfileprovider")
			  .scheme("content")
			  .encodedPath(filePath)
			  .build();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, DATA_TYPE_HTML);
		return intent;
	}
	
	/**
	 * 产生除了视频、音频、网页文件外，打开其他类型文件的Intent
	 *
	 * @param file     文件
	 * @param dataType 文件类型
	 * @return
	 */
	private static Intent generateCommonIntent(Context context, File file, String dataType) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = getUri(context, intent, file);
		intent.setDataAndType(uri, dataType);
		return intent;
	}
	
	/**
	 * 获取对应文件的Uri
	 *
	 * @param context 上下文
	 * @param intent  相应的Intent
	 * @param file    文件对象
	 * @return
	 */
	private static Uri getUri(Context context, Intent intent, File file) {
		Uri uri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}
}
