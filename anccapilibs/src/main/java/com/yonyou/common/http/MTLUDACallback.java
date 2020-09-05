package com.yonyou.common.http;

import org.json.JSONObject;

/**
 * Created by xyy on 16/6/16.
 */
public interface MTLUDACallback {

    void onResult(JSONObject data);

    void onError(String message);
}
