package com.yonyou.common.utils;

import android.widget.Toast;

import com.yonyou.common.app.BaseApplication;

public class MsgUtil {

    public static void showMsg(String data) {
        Toast.makeText(BaseApplication.getBaseApp(), data, Toast.LENGTH_LONG).show();
    }


}
