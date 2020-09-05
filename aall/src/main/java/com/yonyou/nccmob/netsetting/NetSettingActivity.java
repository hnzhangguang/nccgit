package com.yonyou.nccmob.netsetting;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.CheckUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 网络设置
 */
public class NetSettingActivity extends BaseActivity {

    BaseActivity mActivity;
    private TextView pagerTitle;
    private ImageView leftBack;
    private AppCompatEditText editText_ip;  // ip
    private AppCompatEditText editText_port;  // port
    private Button net_confirm;

    @Override
    public void initLayout() {
        setContentView(R.layout.activity_netsetting);
    }

    @Override
    public void initView() {

        pagerTitle = findViewById(R.id.pagerTitle);
        pagerTitle.setText("设置服务地址");
        leftBack = findViewById(R.id.iv_back);
        editText_ip = findViewById(R.id.net_ip);
        editText_port = findViewById(R.id.net_port);
        net_confirm = findViewById(R.id.net_confirm);

        mActivity = this;
    }

    @Override
    public void initListener() {
        leftBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        net_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkParam()) {
                    JsonObjectEx jsonObject = JsonObjectEx.getJsonObj();

                    // 发送请求拿账号列表信息
                    NetUtil.callAction(mActivity, ConstantUrl.requestAccountListUrl, jsonObject, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            String string = error.optString(Constant.CODE);
                            showMessage(error.optString(Constant.MSG, "error"));
                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            LogerNcc.e(successJson);
                            String code = successJson.optString("code", "");
                            String message = successJson.optString("message", "");
                            String data = successJson.optString("data", "");
                            if (!isNull(data)) {
                                try {
                                    JSONArray array = new JSONArray(data);
                                    JSONArray tempArray = new JSONArray();
                                    int length = array.length();
                                    for (int i = 0; i < length; i++) {
                                        Object o = array.get(i);
                                        JsonObjectEx jsonObj = JsonObjectEx.getJsonObj(o.toString());
                                        if (!jsonObj.optString("code", "").equals("0000")) {
                                            tempArray.put(jsonObj);
                                        }
                                    }
                                    UserUtil.setKeyValue(Constant.accountListKey, tempArray.toString());
//                                    String valueByKey = UserUtil.getValueByKey(Constant.accountListKey);
//                                    LogerNcc.e(valueByKey);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            // 关闭
                            finish();
                        }
                    });


                }
            }
        });
    }

    public String getIp() {
        return editText_ip.getText().toString().trim();
    }

    public String getPort() {
        return editText_port.getText().toString().trim();
    }

    public boolean checkParam() {

        String ip = getIp();
        String port = getPort();
        if (TextUtils.isEmpty(ip) && TextUtils.isEmpty(port)) {
            showMessage("IP 端口不可为空!");
            return false;
        } else if (TextUtils.isEmpty(ip)) {
            showMessage("IP 不可为空!");
            return false;
        } else if ((TextUtils.isEmpty(port))) {
            showMessage("端口 不可为空!");
            return false;
        }
        if (!CheckUtil.isIp(ip)) {
            showMessage("IP 不合法,请检查");
            return false;
        }

        // 保存新的ip,port
        UserUtil.setKeyValue(Constant.net_ip, ip);
        UserUtil.setKeyValue(Constant.net_port, port);

        return true;

    }

    @Override
    public void initData() {

        // 读取本地的配置
        String ip = UserUtil.getValueByKey(Constant.net_ip);
        String port = UserUtil.getValueByKey(Constant.net_port);
        editText_ip.setText(ip);
        editText_port.setText(port);


    }
}
