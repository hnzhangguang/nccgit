package com.yonyou.common.net;

/**
 * Created by yanglin  on 2019/4/28
 */
public interface MTLHttpCallBack {
    void onFailure(String error);
    void onResponse(int code,String body);
}
