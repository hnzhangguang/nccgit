package com.yonyou.plugins.audio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.SoundPool;
import android.text.TextUtils;

import com.yonyou.ancclibs.R;
import com.yonyou.audio.plugin.AudioPlayService;
import com.yonyou.audio.plugin.AudioRecordService;
import com.yonyou.audio.plugin.utils.AudioCallback;
import com.yonyou.common.callback.MTLCallback;
import com.yonyou.common.service.MTLHttpService;
import com.yonyou.common.utils.AudioFileUtil;
import com.yonyou.common.utils.CommonRes;
import com.yonyou.common.utils.GlobalConstants;
import com.yonyou.plugins.ApiCallback;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class AudioApiInvoker implements IApiInvoker {
	
	private static final String START_RECORD = "startRecord";
	private static final String STOP_RECORD = "stopRecord";
	private static final String VOICE_RECORD_END = "onVoiceRecordEnd";
	private static final String PLAY_VOICE = "playVoice";
	private static final String PAUSE_VOICE = "pauseVoice";
	private static final String STOP_VOICE = "stopVoice";
	private static final String VOICE_PLAY_END = "onVoicePlayEnd";
	private static final String UPLOAD_VOICE = "uploadVoice";
	private static final String DOWNLOAD_VOICE = "downloadVoice";
	private static final String TRANSLATE_VOICE = "translateVoice";
	
	private ApiCallback mtlCallback;
	private SoundPool soundPool;
	/**
	 * 录音结束回调
	 */
	private AudioCallback audioRecordCallback =
		  new AudioCallback() {
			  @Override
			  public void onResult(JSONObject jsonObject) {
				  if (mtlCallback != null) {
					  mtlCallback.success(jsonObject);
					  mtlCallback = null;
				  }
			  }
			  
			  @Override
			  public void onError(String errMsg) {
				  if (mtlCallback != null) {
					  mtlCallback.error(errMsg);
					  mtlCallback = null;
				  }
			  }
		  };
	/**
	 * 播放录音结束回调
	 */
	private AudioCallback audioPlayCallback =
		  new AudioCallback() {
			  @Override
			  public void onResult(JSONObject jsonObject) {
				  if (mtlCallback != null) {
					  mtlCallback.success(jsonObject);
					  mtlCallback = null;
				  }
			  }
			  
			  @Override
			  public void onError(String errMsg) {
				  if (mtlCallback != null) {
					  mtlCallback.error(errMsg);
					  mtlCallback = null;
				  }
			  }
		  };
	
	@Override
	public String call(String apiname, final MTLArgs args) throws MTLException {
		final Activity context = args.getContext();
		String localId = args.getString("localId");
		switch (apiname) {
			case START_RECORD:
				AudioRecordService.getInstance().setContext(context);
				AudioRecordService.getInstance().startRecord(args);
				return "";
			case STOP_RECORD: // 停止和自动停止的callback都需要返回
				mtlCallback = args.getCallback();
				if (!AudioRecordService.getInstance().isRecord) { // 取统一标志
					mtlCallback.error(context.getResources().getString(R.string.audio_audio_start));
				} else {
					AudioRecordService.getInstance().setCallback(audioRecordCallback);
					AudioRecordService.getInstance().stopRecord();
				}
				return "";
			case VOICE_RECORD_END:
				mtlCallback = args.getCallback();
				AudioRecordService.getInstance().setCallback(audioRecordCallback);
				return "";
			case PLAY_VOICE:
				String assetAudio = args.getString("assetAudio");
				if (!TextUtils.isEmpty(assetAudio)) {
					playAssetAudio(args, assetAudio);
				} else {
					AudioPlayService.getInstance().setContext(context);
					if (TextUtils.isEmpty(localId)) {
						args.error(context.getResources().getString(R.string.audio_localid_null));
					} else {
						AudioPlayService.getInstance().playVoice(localId);
					}
				}
				
				return "";
			case PAUSE_VOICE:
				if (TextUtils.isEmpty(localId)) {
					args.error("LocalId不可以为空！");
					return "";
				}
				AudioPlayService.getInstance().pauseVoice(localId);
				return "";
			case STOP_VOICE:
				if (TextUtils.isEmpty(localId)) {
					args.error("LocalId不可以为空！");
					return "";
				}
				AudioPlayService.getInstance().stopVoice(localId);
				return "";
			case VOICE_PLAY_END:
				mtlCallback = args.getCallback();
				AudioPlayService.getInstance().setCallback(audioPlayCallback);
				return "";
			case TRANSLATE_VOICE:
				return "";
			case UPLOAD_VOICE:
				if (TextUtils.isEmpty(localId)) {
					args.error("localId为空！");
					return "";
				}
				String path = AudioFileUtil.getMp3FilePathByLocalId(context, localId);
				if (TextUtils.isEmpty(path)) {
					args.error(context.getResources().getString(R.string.audio_file_not_exist));
					return "";
				}
				String uploadUrl;
				//                String upConfig = ResourcesUtils.getFromAssets(context,
				// "www/config.json");
				try {
					//                    JSONObject configJson = new JSONObject(upConfig);
					JSONObject config = CommonRes.appConfig.optJSONObject("config");
					if (config == null) {
						args.error(context.getResources().getString(R.string.audio_config_error));
						return "";
					}
					JSONObject serviceUrl = config.getJSONObject("serviceUrl");
					if (serviceUrl == null) {
						args.error(context.getResources().getString(R.string.audio_service_url_null));
						return "";
					}
					uploadUrl = serviceUrl.optString("uploadUrl");
					if (TextUtils.isEmpty(uploadUrl)) {
						args.error(context.getResources().getString(R.string.audio_upload_address_null));
						return "";
					}
				} catch (JSONException e) {
					e.printStackTrace();
					args.error(context.getResources().getString(R.string.audio_config_error));
					return "";
				}
				MTLHttpService uploadService =
					  new MTLHttpService(args.getContext().getApplication(), args.getContext());
				//                String uploadUrl =
				// "https://mdoctor.yonyoucloud.com/mtldebugger/mtl/file/uploadToOSS";
				File file = new File(path);
				
				int isShowProgressTips = args.getInteger("isShowProgressTips", 1);
				final ProgressDialog tip =
					  showTip(
						    context,
						    isShowProgressTips,
						    context.getResources().getString(R.string.mtl_audio_up_loading));
				uploadService.uploadFile(
					  uploadUrl,
					  file,
					  new MTLCallback() {
						  @Override
						  public void onResult(JSONObject data) {
							  dismissTip(tip);
							  if (data != null) {
								  JSONObject jsonObject = new JSONObject();
								  int code = data.optInt("code");
								  String msg = data.optString("msg");
								  if (code == 0) {
									  String serviceId = data.optString("data");
									  try {
										  jsonObject.put("code", code);
										  jsonObject.put("msg", msg);
										  jsonObject.put("serverId", serviceId);
									  } catch (JSONException e) {
										  e.printStackTrace();
									  }
									  args.success(jsonObject);
								  } else {
									  args.error(msg);
								  }
							  } else {
								  args.error(context.getResources().getString(R.string.audio_http_error));
							  }
						  }
						  
						  @Override
						  public void onError(String message) {
							  dismissTip(tip);
							  args.error(message);
						  }
					  });
				return "";
			case DOWNLOAD_VOICE:
				final String serverId = args.getString("serverId");
				if (TextUtils.isEmpty(serverId)) {
					args.error("serverId不可以为空！");
					return "";
				}
				String cachePath = GlobalConstants.idPathMap.get(serverId);
				if (!TextUtils.isEmpty(cachePath)) {
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("localId", AudioFileUtil.getLocalId(cachePath));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					args.success(jsonObject);
					return "";
				}
				String downloadUrl;
				//                String downConfig = ResourcesUtils.getFromAssets(context,
				// "www/config.json");
				try {
					//                    JSONObject configJson = new JSONObject(downConfig);
					JSONObject config = CommonRes.appConfig.getJSONObject("config");
					if (config == null) {
						args.error(context.getResources().getString(R.string.audio_config_error));
						return "";
					}
					JSONObject serviceUrl = config.getJSONObject("serviceUrl");
					if (serviceUrl == null) {
						args.error(context.getResources().getString(R.string.audio_service_url_null));
						return "";
					}
					downloadUrl = serviceUrl.optString("downloadUrl");
					if (TextUtils.isEmpty(downloadUrl)) {
						args.error(context.getResources().getString(R.string.audio_download_address_null));
						return "";
					}
				} catch (JSONException e) {
					e.printStackTrace();
					args.error(context.getResources().getString(R.string.audio_config_error));
					return "";
				}
				MTLHttpService downloadService =
					  new MTLHttpService(args.getContext().getApplication(), args.getContext());
				downloadUrl = downloadUrl + "?serviceId=" + serverId;
				//                String downloadUrl =
				// "https://mdoctor.yonyoucloud.com/mtldebugger/mtl/stream/download" + "?serviceId=" +
				// serverId;
				
				int showProgressTips = args.getInteger("isShowProgressTips", 1);
				final ProgressDialog downTip =
					  showTip(
						    context,
						    showProgressTips,
						    context.getResources().getString(R.string.mtl_audio_down_loading));
				downloadService.downloadFile(
					  downloadUrl,
					  new MTLCallback() {
						  @Override
						  public void onResult(JSONObject data) {
							  dismissTip(downTip);
							  String downPath = data.optString("path");
							  String localId = AudioFileUtil.getLocalId(downPath);
							  GlobalConstants.idPathMap.put(localId, downPath);
							  GlobalConstants.idPathMap.put(serverId, downPath);
							  JSONObject jsonObject = new JSONObject();
							  try {
								  jsonObject.put("localId", localId);
							  } catch (JSONException e) {
								  e.printStackTrace();
							  }
							  args.success(jsonObject);
						  }
						  
						  @Override
						  public void onError(String message) {
							  dismissTip(downTip);
							  args.error(message);
						  }
					  });
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
	private void playAssetAudio(final MTLArgs args, String assetAudio) {
		if (soundPool == null) {
			soundPool = new SoundPool(10, 1, 5);
		}
		try {
			AssetFileDescriptor afd = args.getContext().getAssets().openFd(assetAudio);
			soundPool.load(afd, 1);
			soundPool.setOnLoadCompleteListener(
				  new SoundPool.OnLoadCompleteListener() {
					  @Override
					  public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						  soundPool.play(1, 1.0F, 1.0F, 0, 0, 1.0F);
						  //                    soundPool.release();
						  args.success();
					  }
				  });
		} catch (IOException e) {
			e.printStackTrace();
			args.error("获取文件异常");
		}
	}
	
	private ProgressDialog showTip(Context context, int isShowProgressTips, String tipText) {
		ProgressDialog mDialog = null;
		if (isShowProgressTips == 1) {
			mDialog = ProgressDialog.show(context, "", tipText);
		}
		return mDialog;
	}
	
	private void dismissTip(ProgressDialog tip) {
		if (tip != null && tip.isShowing()) {
			tip.dismiss();
		}
	}
}
