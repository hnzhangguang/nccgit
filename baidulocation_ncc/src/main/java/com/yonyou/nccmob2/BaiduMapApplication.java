package com.yonyou.nccmob2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class BaiduMapApplication extends Application {

    public static void init(Context context) {

        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(context.getApplicationContext());
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL);
        } catch (Exception e) {
            Log.e("mmmm", e.toString());
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }


}
