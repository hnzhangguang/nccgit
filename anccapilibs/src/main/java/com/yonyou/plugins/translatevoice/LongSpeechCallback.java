package com.yonyou.plugins.translatevoice;

import com.yonyou.common.utils.MTLLog;
import com.yonyou.plugins.MTLArgs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LongSpeechCallback implements Callback {
	
	private final static int SCUESS_CODE = 200;
	private MTLArgs args;
	
	public LongSpeechCallback(MTLArgs args) {
		this.args = args;
		
	}
	
	@Override
	public void onFailure(Call call, IOException e) {
		if (e != null) {
			MTLLog.i("error", e.getMessage());
		}
	}
	
	@Override
	public void onResponse(Call call, Response response) throws IOException {
		if (response.code() == 200) {
			try {
				String res = response.body().string();
				JSONObject jsonObject = new JSONObject(res);
				JSONObject result = new JSONObject();
				result.put("data", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
