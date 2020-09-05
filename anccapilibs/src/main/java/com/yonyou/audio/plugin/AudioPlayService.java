package com.yonyou.audio.plugin;

import android.Manifest;
import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yonyou.audio.plugin.utils.AudioCallback;
import com.yonyou.common.utils.AudioFileUtil;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AudioPlayService {
	
	private static AudioPlayService mAudioService;
	private MediaPlayer mMediaPlayer;
	private String mCurrentLocalId;
	private boolean isPause;
	private Context mContext;
	private AudioCallback mCallback;
	private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
	
	private AudioPlayService() {
	}
	
	public static synchronized AudioPlayService getInstance() {
		if (mAudioService == null) {
			mAudioService = new AudioPlayService();
		}
		return mAudioService;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public void setCallback(AudioCallback callback) {
		mCallback = callback;
	}
	
	/**
	 * 播放语音接口
	 *
	 * @param localId 唯一文件名标识
	 */
	public void playVoice(final String localId) {
		if (PermissionsUtil.hasPermission(mContext, permissions)) {
			start(localId);
		} else {
			PermissionsUtil.requestPermission(
				  mContext,
				  new PermissionListener() {
					  @Override
					  public void permissionGranted(@NonNull String[] permission) {
						  start(localId);
					  }
					  
					  @Override
					  public void permissionDenied(@NonNull String[] permission) {
						  Toast.makeText(mContext, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
					  }
				  },
				  permissions);
		}
	}
	
	/**
	 * 暂停播放接口
	 *
	 * @param localId 唯一文件名标识
	 */
	public void pauseVoice(String localId) {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
			isPause = true;
		}
	}
	
	/**
	 * 停止播放接口
	 *
	 * @param localId 唯一文件名标识
	 */
	public synchronized void stopVoice(String localId) {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			isPause = false;
		}
	}
	
	/**
	 * 上传语音接口
	 */
	public void uploadVoice() {
	}
	
	/**
	 * 下载语音接口
	 */
	public void downloadVoice() {
	}
	
	private void start(String localId) {
		// 重复点击重复播放
		if (mMediaPlayer != null && !isPause) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if (!isPause) {
			mCurrentLocalId = localId;
			mMediaPlayer = new MediaPlayer();
			try {
				mMediaPlayer.setDataSource(AudioFileUtil.getMp3FilePathByLocalId(mContext, localId));
				mMediaPlayer.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isPause = false;
		mMediaPlayer.start();
		onVoicePlayEnd();
	}
	
	/**
	 * 监听语音播放完毕接口
	 */
	private void onVoicePlayEnd() {
		if (mCallback == null) {
			return;
		}
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnCompletionListener(
				  new MediaPlayer.OnCompletionListener() {
					  @Override
					  public void onCompletion(MediaPlayer mp) {
						  JSONObject jsonObject = new JSONObject();
						  try {
							  jsonObject.put("localId", mCurrentLocalId);
						  } catch (JSONException e) {
							  e.printStackTrace();
						  }
						  mCallback.onResult(jsonObject);
					  }
				  });
		} else {
			mCallback.onError("stop error");
		}
	}
}
