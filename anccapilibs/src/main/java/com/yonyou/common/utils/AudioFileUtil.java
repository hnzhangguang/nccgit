package com.yonyou.common.utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;

public class AudioFileUtil {
	// 音频输入-麦克风
	public static final int AUDIO_INPUT = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
	
	// 采用频率
	// 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	public static final int AUDIO_SAMPLE_RATE = 16000; // 44.1KHz,普遍使用的频率
	public static final String AUDIO_AMR_FILENAME = "FinalAudio.amr";
	// 录音输出文件
	private static final String AUDIO_RAW_FILENAME = "RawAudio.raw";
	//    private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
	private static final String EXTENSION = ".wav";
	private static final String MP3_EXTENSION = ".mp3";
	private static String mCurrentLocalId;
	private static String mAudioWavPath;
	
	/**
	 * 判断是否有外部存储设备sdcard
	 *
	 * @return true | false
	 */
	public static boolean isSdcardExit() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else return false;
	}
	
	/**
	 * 获取麦克风输入的原始音频流文件路径
	 *
	 * @return
	 */
	public static String getRawFilePath() {
		String mAudioRawPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioRawPath = fileBasePath + "/" + AUDIO_RAW_FILENAME;
		}
		
		return mAudioRawPath;
	}
	
	/**
	 * 获取编码后的WAV格式音频文件路径
	 *
	 * @return
	 */
	public static String getWavFilePath(Context context) {
		
		if (isSdcardExit()) {
			// 规定目录
			String fileBasePath = context.getExternalFilesDir((String) null).getPath();
			if (fileBasePath.endsWith("/")) {
				fileBasePath = fileBasePath + "audio/";
			} else {
				fileBasePath = fileBasePath + "/audio/";
			}
			mCurrentLocalId = "audio" + System.currentTimeMillis();
			File rs = new File(fileBasePath, mCurrentLocalId + EXTENSION);
			rs.getParentFile().mkdirs();
			mAudioWavPath = rs.getAbsolutePath();
		}
		return mAudioWavPath;
	}
	
	public static File getWavFile() {
		return new File(mAudioWavPath);
	}
	
	public static String getWavFilePathByLocalId(Context context, String localId) {
		String mAudioWavPath = "";
		if (isSdcardExit()) {
			String fileBasePath = context.getExternalFilesDir((String) null).getPath();
			if (fileBasePath.endsWith("/")) {
				fileBasePath = fileBasePath + "audio/";
			} else {
				fileBasePath = fileBasePath + "/audio/";
			}
			mAudioWavPath = fileBasePath + localId + EXTENSION;
		}
		return mAudioWavPath;
	}
	
	public static String getMp3FilePathByLocalId(Context context, String localId) {
		String mAudioWavPath = "";
		if (isSdcardExit()) {
			String fileBasePath = context.getExternalFilesDir((String) null).getPath();
			if (fileBasePath.endsWith("/")) {
				fileBasePath = fileBasePath + "audio/";
			} else {
				fileBasePath = fileBasePath + "/audio/";
			}
			mAudioWavPath = fileBasePath + localId + MP3_EXTENSION;
		}
		return mAudioWavPath;
	}
	
	public static String getLocalId() {
		return String.valueOf(mCurrentLocalId);
	}
	
	/**
	 * 获取编码后的AMR格式音频文件路径
	 *
	 * @return
	 */
	public static String getAMRFilePath() {
		String mAudioAMRPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioAMRPath = fileBasePath + "/" + AUDIO_AMR_FILENAME;
		}
		return mAudioAMRPath;
	}
	
	/**
	 * 获取文件大小
	 *
	 * @param path,文件的绝对路径
	 * @return
	 */
	public static long getFileSize(String path) {
		File mFile = new File(path);
		if (!mFile.exists()) return -1;
		return mFile.length();
	}
	
	public static String getLocalId(String path) {
		String localId;
		if (path.lastIndexOf(".") > path.lastIndexOf("/") + 1) {
			localId = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		} else {
			localId = path.substring(path.lastIndexOf("/") + 1);
		}
		return localId;
	}
}
