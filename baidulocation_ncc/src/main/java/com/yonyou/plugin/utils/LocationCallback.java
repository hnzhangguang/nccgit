package com.yonyou.plugin.utils;

import org.json.JSONObject;

public interface LocationCallback {
    void onResult(JSONObject jsonObject);

    void onError(String errMsg);
}