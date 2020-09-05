package com.yonyou.common.net;

import org.json.JSONObject;

public interface HttpCallBack {
	
	void onFailure(JSONObject error);
	
	void onResponse(JSONObject successJson);
	
	
}
