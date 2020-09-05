package com.yonyou.album.plugin.callback;

import org.json.JSONObject;

public interface AlbumCallback {
    void onResult(JSONObject jsonObject);

    void onError(String errMsg);
}