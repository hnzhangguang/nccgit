package com.yonyou.audio.plugin.callback;

public interface ILoadCallback {
    
    void onSuccess();
    
    void onFailure(Exception error);
    
}