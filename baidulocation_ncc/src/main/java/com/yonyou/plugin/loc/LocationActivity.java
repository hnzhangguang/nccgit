package com.yonyou.plugin.loc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;
import com.yonyou.common.utils.utils.AppCommonUtil;
import com.yonyou.nccmob2.R;
import com.yonyou.plugin.utils.LocationConfig;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class LocationActivity extends Activity
        implements SensorEventListener, OnGetGeoCoderResultListener, OnClickListener {
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private static final String DEFAULT_NAME = "[位置]";
    public MyLocationListenner myListener = new MyLocationListenner();
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    // 定位相关
    LocationClient mLocClient;
    BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    // UI相关
    OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private double mDestLat;
    private double mDestLng;
    private String mName;
    private String mAddress;
    private Context mContext;
    private BottomSheetDialog bsd;
    private double mLat;
    private double mLng;
    private RelativeLayout rl_my_local;
    private ImageView iv_navigate;
    private TextView tv_local_name;
    private TextView tv_local_address;
    private MyLocationData locData;
    private float direction;
    private String[] permisions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mContext = this;
        checkPermission();
    }

    private void checkPermission() {
        if (PermissionsUtil.hasPermission(this, permisions)) {
            // 地图初始化
            init();
            initMap();
        } else {
            PermissionsUtil.requestPermission(
                    this,
                    new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permission) {
                            init();
                            initMap();
                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permission) {
                            Toast.makeText(LocationActivity.this, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    },
                    permisions);
        }
    }

    private void initMap() {
        // 地图初始化
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void init() {
        mDestLat = getIntent().getDoubleExtra("lat", -1);
        mDestLng = getIntent().getDoubleExtra("lng", -1);
        String name = getIntent().getStringExtra("name");
        mName = TextUtils.isEmpty(name) ? DEFAULT_NAME : name;
        mAddress = getIntent().getStringExtra("address");
        requestLocButton = findViewById(R.id.button1);
        rl_my_local = findViewById(R.id.rl_my_local);
        iv_navigate = findViewById(R.id.iv_navigate);
        rl_my_local.setOnClickListener(this);
        iv_navigate.setOnClickListener(this);
        tv_local_address = findViewById(R.id.tv_local_address);
        tv_local_name = findViewById(R.id.tv_local_name);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // 获取传感器管理服务
        mCurrentMode = LocationMode.NORMAL;
        requestLocButton.setText("普通");
        OnClickListener btnClickListener =
                new OnClickListener() {
                    public void onClick(View v) {
                        switch (mCurrentMode) {
                            case NORMAL:
                                requestLocButton.setText("跟随");
                                mCurrentMode = LocationMode.FOLLOWING;
                                mBaiduMap.setMyLocationConfiguration(
                                        new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                                MapStatus.Builder builder = new MapStatus.Builder();
                                builder.overlook(0);
                                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                                break;
                            case COMPASS:
                                requestLocButton.setText("普通");
                                mCurrentMode = LocationMode.NORMAL;
                                mBaiduMap.setMyLocationConfiguration(
                                        new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                                MapStatus.Builder builder1 = new MapStatus.Builder();
                                builder1.overlook(0);
                                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                                break;
                            case FOLLOWING:
                                requestLocButton.setText("罗盘");
                                mCurrentMode = LocationMode.COMPASS;
                                mBaiduMap.setMyLocationConfiguration(
                                        new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                                break;
                            default:
                                break;
                        }
                    }
                };
        requestLocButton.setOnClickListener(btnClickListener);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        radioButtonListener =
                new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.defaulticon) {
                            // 传入null则，恢复默认图标
                            mCurrentMarker = null;
                            mBaiduMap.setMyLocationConfigeration(
                                    new MyLocationConfiguration(mCurrentMode, true, null));
                        }
                        if (checkedId == R.id.customicon) {
                            // 修改为自定义marker
                            mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_geo);
                            mBaiduMap.setMyLocationConfiguration(
                                    new MyLocationConfiguration(
                                            mCurrentMode,
                                            true,
                                            mCurrentMarker,
                                            accuracyCircleFillColor,
                                            accuracyCircleStrokeColor));
                        }
                    }
                };
        group.setOnCheckedChangeListener(radioButtonListener);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    private void initBottomSheet() {
        if (AppCommonUtil.isInstalled(mContext, LocationConfig.BAIDU_PACKAGE)
                || AppCommonUtil.isInstalled(mContext, LocationConfig.GAODE_PACKAGE)
                || AppCommonUtil.isInstalled(mContext, LocationConfig.TENGXUN_PACKAGE)) {
            View view = View.inflate(mContext, R.layout.dailog_location_bottom_sheet, null);
            LinearLayout ll_content = view.findViewById(R.id.ll_content);
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(this);
            addItem(ll_content);
            bsd = new BottomSheetDialog(this);
            bsd.setContentView(view);
        }
    }

    private void addItem(LinearLayout ll_content) {
        addMapAppItem(ll_content, LocationConfig.BAIDU_PACKAGE);
        addMapAppItem(ll_content, LocationConfig.GAODE_PACKAGE);
        addMapAppItem(ll_content, LocationConfig.TENGXUN_PACKAGE);
    }

    private void addMapAppItem(LinearLayout ll_content, final String packageName) {
        if (AppCommonUtil.isInstalled(mContext, packageName)) {
            View dailog_sheet_item = View.inflate(mContext, R.layout.dailog_location_sheet_item, null);
            TextView tv_item = dailog_sheet_item.findViewById(R.id.tv_item);
            if (packageName.equals(LocationConfig.TENGXUN_PACKAGE)) {
                tv_item.setText("腾讯地图");
                tv_item.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToTencentMap();
                            }
                        });
            } else if (packageName.equals(LocationConfig.BAIDU_PACKAGE)) {
                tv_item.setText("百度地图");
                tv_item.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToBaiduMap();
                            }
                        });
            } else if (packageName.equals(LocationConfig.GAODE_PACKAGE)) {
                tv_item.setText("高德地图");
                tv_item.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToGaodeMap();
                            }
                        });
            }
            ll_content.addView(dailog_sheet_item);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData =
                    new MyLocationData.Builder()
                            .accuracy(mCurrentAccracy)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mCurrentDirection)
                            .latitude(mCurrentLat)
                            .longitude(mCurrentLon)
                            .build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            //            Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast
            //            .LENGTH_LONG).show();
            return;
        }

        mBaiduMap.clear();
        mBaiduMap.addOverlay(
                new MarkerOptions()
                        .position(result.getLocation())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marka)));

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo =
                String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);

        Toast.makeText(LocationActivity.this, strInfo, Toast.LENGTH_LONG).show();

        MTLLog.e("GeoCodeDemo", "onGetGeoCodeResult = " + result.toString());
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result.getLocation() != null) {
            mLat = result.getLocation().latitude;
            mLng = result.getLocation().longitude;
        } else {
            mLng = mCurrentLon;
            mLat = mCurrentLat;
        }
        initBottomSheet();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            //            Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast
            //            .LENGTH_LONG).show();
            return;
        }

        mBaiduMap.clear();
        mBaiduMap.addOverlay(
                new MarkerOptions()
                        .position(result.getLocation())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marka)));

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

        if (DEFAULT_NAME.equals(mName)) {
            //            tv_local_name.setText(result.getPoiRegionsInfoList().get(0)
            //            .getRegionName());
            tv_local_name.setText(DEFAULT_NAME);
        }
        if (TextUtils.isEmpty(mAddress)) {
            tv_local_address.setText(result.getAddress());
        }

        //        Toast.makeText(LocationActivity.this, result.getAddress() + " adcode:
        //        " + result.getAdcode(), Toast.LENGTH_LONG).show();

        MTLLog.e("GeoCodeDemo", "ReverseGeoCodeResult = " + result.toString());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_navigate) {
            if (bsd == null) {
                return;
            }
            bsd.show();
        } else if (i == R.id.tv_cancel) {
            bsd.dismiss();
        } else if (i == R.id.rl_my_local) {
            mCurrentMode = LocationMode.FOLLOWING;
            mBaiduMap.setMyLocationConfiguration(
                    new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
            MapStatus.Builder builder1 = new MapStatus.Builder();
            builder1.overlook(0);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
        }
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
        // 为系统的方向传感器注册监听器
        if (mSensorManager != null) {
            mSensorManager.registerListener(
                    this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onStop() {
        // 取消注册传感器监听
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        releaseLocation();
        mContext = null;
        super.onDestroy();
    }

    private void releaseLocation() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    /**
     * 跳转百度地图
     */
    private void goToBaiduMap() {
        Intent intent = new Intent();
        //        @"baidumap://map/direction?origin={{我的位置}}&destination=%f,
        //        %f&mode=driving&coord_type=gcj02"
        intent.setData(
                Uri.parse(
                        "baidumap://map/direction?destination="
                                + mLat
                                + ","
                                + mLng
                                + /*"|name:" + mAddressStr +*/ // 终点
                                "&mode=driving"
                                + // 导航路线方式
                                "&src="
                                + LocationConfig.BAIDU_PACKAGE));
        startActivity(intent); // 启动调用
    }

    /**
     * 跳转高德地图
     */
    private void goToGaodeMap() {
        LatLng endPoint = BD2GCJ(new LatLng(mLat, mLng)); // 坐标转换
        StringBuffer stringBuffer =
                new StringBuffer("androidamap://navi" + "?sourceApplication=").append("amap");
        stringBuffer
                .append("&lat=")
                .append(endPoint.latitude)
                .append("&lon=")
                .append(endPoint.longitude) /*.append("&keywords=" +
                mAddressStr)*/
                .append("&dev=")
                .append(0)
                .append("&style=")
                .append(2);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()));
        intent.setPackage(LocationConfig.GAODE_PACKAGE);
        startActivity(intent);
    }

    /**
     * 跳转腾讯地图
     */
    private void goToTencentMap() {
        LatLng endPoint = BD2GCJ(new LatLng(mLat, mLng)); // 坐标转换
        StringBuffer stringBuffer =
                new StringBuffer("qqmap://map/routeplan?type=drive")
                        .append("&tocoord=")
                        .append(endPoint.latitude)
                        .append(",")
                        .append(endPoint.longitude) /*.append("&to=" + mAddressStr)*/;
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()));
        startActivity(intent);
    }

    /**
     * BD-09 坐标转换成 GCJ-02 坐标
     */
    private LatLng BD2GCJ(LatLng bd) {
        double x = bd.longitude - 0.0065, y = bd.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);

        double lng = z * Math.cos(theta); // lng
        double lat = z * Math.sin(theta); // lat
        return new LatLng(lat, lng);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData =
                    new MyLocationData.Builder()
                            .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mCurrentDirection)
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                float scale = getIntent().getIntExtra("scale", 28);
                scale = (float) (21 - 4) / 28 * scale + 4;
                builder.target(ll).zoom(scale); // 百度地图取值范围 4 - 21
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            if (mDestLat != -1 && mDestLng != -1) {
                tv_local_address.setText(mAddress);
                tv_local_name.setText(mName);
                LatLng ptCenter = new LatLng(mDestLat, mDestLng);
                mSearch.reverseGeoCode(
                        new ReverseGeoCodeOption().location(ptCenter).newVersion(0).radius(500));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
