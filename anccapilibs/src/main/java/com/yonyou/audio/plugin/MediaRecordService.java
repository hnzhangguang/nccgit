package com.yonyou.audio.plugin;

import android.media.MediaRecorder;

import com.yonyou.audio.plugin.utils.ErrorCode;
import com.yonyou.common.utils.AudioFileUtil;

import java.io.File;
import java.io.IOException;

public class MediaRecordService {
	private static MediaRecordService mInstance;
	private boolean isRecord = false;
	private MediaRecorder mMediaRecorder;
	
	private MediaRecordService() {
	}
	
	public static synchronized MediaRecordService getInstance() {
		if (mInstance == null) mInstance = new MediaRecordService();
		return mInstance;
	}
	
	public int startRecordAndFile() {
		// 判断是否有外部存储设备sdcard
		if (AudioFileUtil.isSdcardExit()) {
			if (isRecord) {
				return ErrorCode.E_STATE_RECODING;
			} else {
				if (mMediaRecorder == null) createMediaRecord();
				
				try {
					mMediaRecorder.prepare();
					mMediaRecorder.start();
					// 让录制状态为true
					isRecord = true;
					return ErrorCode.SUCCESS;
				} catch (IOException ex) {
					ex.printStackTrace();
					return ErrorCode.E_UNKOWN;
				}
			}
			
		} else {
			return ErrorCode.E_NOSDCARD;
		}
	}
	
	public void stopRecordAndFile() {
		close();
	}
	
	public long getRecordFileSize() {
		return AudioFileUtil.getFileSize(AudioFileUtil.getAMRFilePath());
	}
	
	private void createMediaRecord() {
		/* ①Initial：实例化MediaRecorder对象 */
		mMediaRecorder = new MediaRecorder();
		
		/* setAudioSource/setVedioSource*/
		mMediaRecorder.setAudioSource(AudioFileUtil.AUDIO_INPUT); // 设置麦克风
		
		/* 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
		 * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
		 */
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		
		/* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		
		/* 设置输出文件的路径 */
		File file = new File(AudioFileUtil.getAMRFilePath());
		if (file.exists()) {
			file.delete();
		}
		mMediaRecorder.setOutputFile(AudioFileUtil.getAMRFilePath());
	}
	
	private void close() {
		if (mMediaRecorder != null) {
			System.out.println("stopRecord");
			isRecord = false;
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}
}
