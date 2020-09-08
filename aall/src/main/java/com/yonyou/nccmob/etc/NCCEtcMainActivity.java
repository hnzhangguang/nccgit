package com.yonyou.nccmob.etc;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.utils.AppCommonUtil;
import com.yonyou.nccmob.NCCOpenH5MainActivity;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.scan.NCCSimpleScanActivity;
import com.yonyou.nccmob.scan.NCCWebViewScanActivity;


/*
 * @功能: ncc android 原生测试界面
 * @Date  2020/7/23;
 * @Author zhangg
 **/
public class NCCEtcMainActivity extends BaseActivity {

    AppCompatEditText edit;
    Button baiduMap;

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.activity_etcmain);
    }


    @Override
    public void initView() {
        super.initView();
        findViewById(R.id.baiduMap).setOnClickListener(this);
        findViewById(R.id.baiduMapLoc).setOnClickListener(this);
        findViewById(R.id.openh5).setOnClickListener(this);
        findViewById(R.id.downLoadH5).setOnClickListener(this);
        findViewById(R.id.changYongApp).setOnClickListener(this);
        findViewById(R.id.getEditValue).setOnClickListener(this);
        findViewById(R.id.btn_openSimpleScan).setOnClickListener(this);
        findViewById(R.id.btn_openWebViewScan).setOnClickListener(this);
        findViewById(R.id.btn_define).setOnClickListener(this);
        edit = findViewById(R.id.edit);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (null != data) {
                Bundle extras = data.getExtras();
                if (null != extras) {
                    LogerNcc.e(extras.getString("result"));
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);


        if (R.id.btn_define == view.getId()) {  // 自定义扫码

            ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.scan.NccWebviewScannerActivity");
            Intent intent = new Intent();
            intent.setComponent(cn);
            Bundle bundle = new Bundle();
            bundle.putString("aa", "aa");
            intent.putExtras(bundle);
            startActivity(intent);

//        简单扫描界面
        } else if (R.id.btn_openSimpleScan == view.getId()) {
//			Intent intent = new Intent(NCCEtcMainActivity.this, NCCSimpleScanActivity.class);
//			startActivityForResult(intent, 111);
            ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.scan.NCCSimpleScanActivity");
            Intent intent = new Intent();
            intent.setComponent(cn);
            Bundle bundle = new Bundle();
            bundle.putString("aa", "aa");
            intent.putExtras(bundle);
            startActivityForResult(intent, 111);


            // webview 扫码
        } else if (R.id.btn_openWebViewScan == view.getId()) {
//			Intent intent = new Intent(NCCEtcMainActivity.this, NCCWebViewScanActivity.class);
//			startActivityForResult(intent, 111);
            ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.scan.NCCWebViewScanActivity");
            Intent intent = new Intent();
            intent.setComponent(cn);
            Bundle bundle = new Bundle();
            bundle.putString("aa", "aa");
            intent.putExtras(bundle);
//			startActivityForResult(intent, 111);
            startActivity(intent);


        } else if (R.id.getEditValue == view.getId()) {
            Intent intent = new Intent(NCCEtcMainActivity.this, NCCOpenH5MainActivity.class);
            intent.putExtra(
                    NCCOpenH5MainActivity.PREVIEW_URL, "http://10.11.119.33:3005/mobile_am/test/index.html");
            startActivity(intent);
        } else if (R.id.changYongApp == view.getId()) {

            String activityString = "com.yonyou.nccmob.appsetting.NCCManagerAppActivity";
            ComponentName cn =
                    new ComponentName(getPackageName(), activityString);
            boolean existActivity =
                    AppCommonUtil.isExistActivity(activityString);

            if (existActivity) {
//				showMessage("存在");
            } else {
                showMessage("不存在");
                return;
            }

            Intent intent = new Intent();
            intent.setComponent(cn);
            startActivity(intent);
        } else if (R.id.downLoadH5 == view.getId()) {
            ComponentName cn =
                    new ComponentName(
                            getPackageName(), "com.yonyou.common.download.offline.ui.UpdateActivity");
            Intent intent = new Intent();
            intent.setComponent(cn);
            startActivity(intent);
        } else if (R.id.baiduMap == view.getId()) {
            //                  getLongitdue();
        } else if (R.id.baiduMapLoc == view.getId()) {

            try {
                ComponentName cn =
                        new ComponentName(getPackageName(), "com.yonyou.plugin.loc.LocationActivity");
                Intent intent = new Intent();
                intent.setComponent(cn);

                boolean existActivity =
                        AppCommonUtil.isExistActivity("com.yonyou.plugin.loc.LocationActivity");

                if (existActivity) {
                    showMessage("存在");
                } else {
                    showMessage("不存在");
                    return;
                }

                startActivity(intent);
            } catch (Exception e) {
                LogerNcc.e(e);
            }

        } else if (R.id.openh5 == view.getId()) {
            String url = "https://mdoctor.yonyoucloud.com";
            //            url = "https://www.baidu.com";
            url = "http://10.11.115.33:3006/mobile_so/app/salesmen/main";
            url =
                    "http://10.11.115.33/ncc_mobile/mobile_so/app/salesmen/main/index.html?appcode=4092&code=31a19793868916acb326b6ad2672d9cf5a25bc49b2a1cc81cfd21fc75e42&fromType=upEsnApp&appId=1295&openAppId=0&qzId=176487&openTenantId=zqich3pd&ykj_req=1594353708785";
            url =
                    "http://10.11.115.33/ncc_mobile/mobile_so/app/salesmen/main/index.html?appcode=4092&code=34894ee435b2a86fadbe7f99c4ca64d6e709b530c3c2b99954d32c147c24&fromType=upEsnApp&appId=1295&openAppId=0&qzId=176487&openTenantId=zqich3pd&ykj_req=1595223799191";
            //            url =
            // "http://10.11.115.38/ncc_mobile/mobile_so/app/salesmen/main/index.html?appcode=4092&code=1783e925d51c7000562b0129f29f5ec6b1689c3fbe348839f9b1fa2df47d&fromType=upEsnApp&appId=1295&openAppId=0&qzId=177854&openTenantId=hehjv9g7&ykj_req=1595293073624";
            url = "file:///android_asset/index.html";
            //      url = "http://10.6.221.37:5996/mobile_so/app/salesmen/main#/";
//			url = "https://mdoctor.yonyoucloud.com";
            Intent intent = new Intent(NCCEtcMainActivity.this, NCCOpenH5MainActivity.class);
            intent.putExtra(NCCOpenH5MainActivity.PREVIEW_URL, url);
            startActivity(intent);
        }
    }
}
