package com.yonyou.audio.plugin.utils;

import org.json.JSONObject;

public interface AudioCallback {
    void onResult(JSONObject jsonObject);

    void onError(String errMsg);
}