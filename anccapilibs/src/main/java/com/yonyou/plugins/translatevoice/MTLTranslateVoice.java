package com.yonyou.plugins.translatevoice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.baidu.aip.speech.AipSpeech;
import com.yonyou.ancclibs.R;
import com.yonyou.common.utils.AudioFileUtil;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.ResourcesUtils;
import com.yonyou.plugins.MTLArgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MTLTranslateVoice {
	
	public static void translateVoice(final MTLArgs args) {
		
		String localId = args.getString("localId");
		if (TextUtils.isEmpty(localId)) {
			args.error("localId为空！");
			return;
		}
		final Context context = args.getContext();
		final String path = AudioFileUtil.getWavFilePathByLocalId(context, localId);
		if (TextUtils.isEmpty(path)) {
			args.error(context.getResources().getString(R.string.audio_file_not_exist));
			return;
		}
		
		String APP_ID = getMetaDataValue(context, "com.baidu.speech.APP_ID");
		String API_KEY = getMetaDataValue(context, "com.baidu.speech.API_KEY");
		String SECRET_KEY = getMetaDataValue(context, "com.baidu.speech.SECRET_KEY");
		// 初始化一个AipSpeech
		final AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
		
		int showProgressTips = args.getInteger("isShowProgressTips", 1);
		final ProgressDialog tip = showTip(context, showProgressTips);
		// 调用接口
		new Thread(
			  new Runnable() {
				  @Override
				  public void run() {
					  JSONObject res = client.asr(path, "wav", 16000, null);
					  dismissTip(tip);
					  String err_msg = res.optString("err_msg");
					  int err_no = res.optInt("err_no");
					  if (err_msg.contains("success") && err_no == 0) {
						  JSONArray jsonArray = res.optJSONArray("result");
						  if (jsonArray == null || jsonArray.length() < 1) {
							  args.error(
								    ResourcesUtils.getStringResourse(
										context, R.string.mtl_translate_voice_error));
							  return;
						  }
						  JSONObject jsonObject = new JSONObject();
						  try {
							  jsonObject.put("translateResult", jsonArray.opt(0));
							  args.success(jsonObject);
						  } catch (JSONException e) {
							  e.printStackTrace();
						  }
					  } else {
						  if (TextUtils.isEmpty(err_msg)) {
							  args.error(
								    ResourcesUtils.getStringResourse(
										context, R.string.mtl_translate_voice_error));
						  } else {
							  args.error(err_msg);
						  }
					  }
				  }
			  })
			  .start();
	}
	
	private static String getMetaDataValue(Context context, String name) {
		Object value = null;
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo =
				  packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (applicationInfo != null && applicationInfo.metaData != null) {
				value = applicationInfo.metaData.get(name);
			}
		} catch (PackageManager.NameNotFoundException e) {
			MTLLog.e("SpeechServer", "Could not read the name in the manifest file.");
		}
		if (value == null) {
			MTLLog.d(
				  "SpeechServer",
				  "The name '" + name + "' is not defined in the manifest file's meta data.");
			return "";
		}
		return value.toString();
	}
	
	private static ProgressDialog showTip(Context context, int isShowProgressTips) {
		ProgressDialog mDialog = null;
		if (isShowProgressTips == 1) {
			mDialog = ProgressDialog.show(context, "", "正在转换，请稍候...");
		}
		return mDialog;
	}
	
	private static void dismissTip(ProgressDialog tip) {
		if (tip != null && tip.isShowing()) {
			tip.dismiss();
		}
	}
}
