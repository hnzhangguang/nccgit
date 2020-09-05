package com.yonyou.common.callback;

import org.json.JSONObject;

/**
 */
public interface MTLCallback {

    void onResult(JSONObject data);

    void onError(String message);
}
