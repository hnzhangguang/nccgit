package com.yonyou.plugin.loc;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;
import com.yonyou.plugin.utils.LocationCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationService {

    private static LocationService mLocationService;
    private Context mContext;
    private LocationCallback mCallback;
    private LocationClient mLocClient;
    private String[] permisions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private LocationService() {
    }

    public static LocationService getLocationService() {
        if (mLocationService == null) {
            mLocationService = new LocationService();
        }
        return mLocationService;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void getLocation(LocationCallback callback) {
        mCallback = callback;
        checkPermission();
    }


    public void stopLocation() {
        if (mLocClient != null && mLocClient.isStarted()) {
            try {
                mLocClient.stop();
                mLocClient.registerLocationListener((BDLocationListener) null);
                mLocClient = null;
            } catch (Exception e) {

            }

        }
    }

    private void getLocationInfo() {
        if (mContext == null) {
            // TODO: 2019/5/9
            mCallback.onError("上下文出错");
            return;
        }
        MyLocationListener myListener = new MyLocationListener();
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void checkPermission() {
        if (PermissionsUtil.hasPermission(mContext, permisions)) {
            getLocationInfo();
        } else {
            PermissionsUtil.requestPermission(mContext, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    getLocationInfo();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    // TODO: 2019/5/11 回调给js 错误信息
                    Toast.makeText(mContext, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
                }
            }, permisions);
        }
    }

    public void getLongitdue() {
        LocationClient locationClient = new LocationClient(mContext);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");        //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);    //设置定位优先级
        option.setProdName("prodName");
        //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(100);//设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            public void onReceivePoi(BDLocation arg0) {
            }

            public void onReceiveLocation(BDLocation location) {
                //                double latitude = location.getLatitude();
                //                double longitude = location.getLongitude();
                Log.e("mmmm", location.toString());

            }
        });
        if (locationClient.isStarted()) {
            locationClient.stop();
        }
        locationClient.start();
        locationClient.requestLocation();
        //        locationClient.requestNotifyLocation();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            JSONObject json = new JSONObject();
            if (location == null) {
                mCallback.onError("获取点位失败");
                return;
            }
            try {
                json.put("latitude", location.getLatitude());
                json.put("longitude", location.getLongitude());
                json.put("loctype", location.getLocType());//gps or network
                json.put("radius", location.getRadius());
                String addrStr = location.getAddrStr();
                String city = location.getCity();
                String countryCode = location.getCountryCode();
                String district = location.getDistrict();
                String locationDescribe = location.getLocationDescribe();
                String locationID = location.getLocationID();
                json.put("addrStr", addrStr);
                json.put("city", city);
                json.put("countryCode", countryCode);
                json.put("district", district);
                json.put("locationDescribe", locationDescribe);
                json.put("locationID", locationID);

                LogerNcc.e(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mCallback.onResult(json);
            mLocClient.stop();
        }
    }


}
