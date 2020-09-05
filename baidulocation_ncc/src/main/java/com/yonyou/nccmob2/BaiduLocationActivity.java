package com.yonyou.nccmob2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class BaiduLocationActivity extends AppCompatActivity {

    Button btn_location;
    Button btn_get;
    Context mContext;
    private LocationClient mLocClient;
    private String[] permisions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_baidu_location);
        btn_get = findViewById(R.id.btn_get);
        btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.plugin.loc.LocationActivity");
                Intent intent = new Intent();
                intent.setComponent(cn);
                startActivity(intent);
            }
        });
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                getLocationInfo();
            }
        });
    }

    private void checkPermission() {
        if (PermissionsUtil.hasPermission(BaiduLocationActivity.this, permisions)) {
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

    private void getLocationInfo() {
        if (mContext == null) {
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

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            JSONObject json = new JSONObject();
            if (location == null) {
                return;
            }
            try {
                json.put("latitude", location.getLatitude());
                json.put("longitude", location.getLongitude());
                Log.e("mmmm", location.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mLocClient.stop();
        }
    }


}
