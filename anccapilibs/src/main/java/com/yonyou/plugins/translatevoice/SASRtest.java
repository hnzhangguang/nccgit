package com.yonyou.plugins.translatevoice;

import android.content.Context;

public class SASRtest {
	public static void testWav(Context context, SASCallback callback) {
		
		String SecretId = "AKIDoSs45DEsQk1HRRDnU3SehYDfx2uZ3Xig";
		String SecretKey = "ueCXfaHl25vb4MSM7i7G9qKZbQ0LSJvI";
		
		// 识别引擎 8k or 16k
		String EngSerViceType = "16k";
		
		// 语音数据来源 0:语音url or 1:语音数据bodydata(data数据大小要小于800k)
		String SourceType = "1";
		
		// 音频格式 wav，mp3
		String VoiceFormat = "wav";
		
		//        String fileURI = AudioFileUtil.getWavFilePathByLocalId(context,"audio1559271909123");
		// 语音数据地址
		//        String fileURI = "/storage/emulated/0/16k.wav";
		String fileURI = "/storage/emulated/0/audio1559364557350.wav";
		//        String fileURI="http://liqiansunvoice-1255628450.cosgz.myqcloud.com/30s.wav";
		//        String fileURI="https://android-1257983260.cos.ap-chengdu.myqcloud.com/2.mp3";
		//        String fileURI="https://android-1257983260.cos.ap-chengdu.myqcloud.com/1.wav";
		
		// 调用setConfig函数设置相关参数
		int res =
			  SASRsdk.setConfig(SecretId, SecretKey, EngSerViceType, SourceType, VoiceFormat, fileURI);
		if (res < 0) {
			return;
		}
		
		// 调用sendVoice函数获得音频识别结果
		SASRsdk.sendVoice(callback);
	}
}
