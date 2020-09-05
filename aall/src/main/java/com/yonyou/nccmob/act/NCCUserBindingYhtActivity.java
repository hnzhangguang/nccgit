package com.yonyou.nccmob.act;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.AppCommonUtil;
import com.yonyou.common.utils.utils.CheckUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * @功能: ncc用户第一次登录系统需要绑定yht
 * @Date  2020/8/17;
 * @Author zhangg
 **/
public class NCCUserBindingYhtActivity extends BaseActivity {


    ImageView iv_back;
    TextView pagerTitle;  // title
    TextView tv_sendPhoneTo;  // 文本显示,验证码已发送至....
    Button btn_bding_login;  // 绑定按钮
    Button phoneCheckCode;  // 绑定按钮
    EditText et_phone;  // 验证码
    EditText et_checkPhone;  // 输入手机号验证码
    EditText et_imgCode;  // 图形验证码
    String userCOode;  // 带来的用户名
    String phone;  // 手机号
    String imgUrl;
    String registUrl;
    ImageView imgCheckCode;
    String time;

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.activity_nccuserbindingyht);
    }

    @Override
    public void initView() {
        super.initView();
        // 手机验证码已发送至111111***1111
        imgCheckCode = findViewById(R.id.imgCheckCode);
        tv_sendPhoneTo = findViewById(R.id.tv_sendPhoneTo);
        et_phone = findViewById(R.id.et_phone);
        et_checkPhone = findViewById(R.id.et_checkPhone);
        et_imgCode = findViewById(R.id.et_imgCode);
        phoneCheckCode = findViewById(R.id.phoneCheckCode);
        btn_bding_login = findViewById(R.id.btn_bding_login);
        pagerTitle = findViewById(R.id.pagerTitle);
        pagerTitle.setText("绑定友户通");
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 获取手机号
    public String getPhoneCheckCode() {
        return et_checkPhone.getText().toString().trim();
    }

    // 获取手机验证码
    private String getPhone() {
        return et_phone.getText().toString().trim();
    }

    // 获取图形验证码
    private String getImgCheck() {
        return et_imgCode.getText().toString().trim();
    }

    public boolean checkParam() {
        // 手机号校验
        String phone = getPhone();
        if (TextUtils.isEmpty(phone)) {
            showMessage("手机号不可为空");
            return false;
        }
        // 校验长度
        int length = phone.length();
        if (length != 11 || !CheckUtil.isMobileNO(phone)) {
            showMessage("手机号不对");
            return false;
        }

        // 手机验证码校验
        String phoneCheckCode = getPhoneCheckCode();
        if (TextUtils.isEmpty(phoneCheckCode)) {
            showMessage("手机验证码不可为空");
            return false;
        } else {
            if (phoneCheckCode.length() != 6) {
                showMessage("手机验证码不对");
                return false;
            }
        }
        // 图形验证码校验
        String imgCheck = getImgCheck();
        if (isNull(imgCheck)) {
            showMessage("图形验证码不可为空");
            return false;
        }
        if (imgCheck.length() != 4) {
            showMessage("图形验证码不对");
            return false;
        }

        Pattern p = Pattern.compile("\\d{11}");
        Matcher m = p.matcher(phone);
        boolean isMatch = m.matches();

        return isMatch;
    }


    /*
     * @功能: 获取图形验证码
     * @参数  ;
     * @Date  2020/8/28;
     * @Author zhangg
     **/
    public void requestImgCheckCode() {
        JsonObjectEx paramJson = JsonObjectEx.getJsonObj();
        paramJson.putEx(Constant.userCode, UserUtil.getValueByKey(Constant.userCode));
        NetUtil.callAction(NCCUserBindingYhtActivity.this, ConstantUrl.requestImgCheckCodeUrl, paramJson, new HttpCallBack() {
            @Override
            public void onFailure(JSONObject error) {
                LogerNcc.e(error);
                showMessage(error);
            }

            @Override
            public void onResponse(JSONObject successJson) {
                LogerNcc.e(successJson);
                try {
                    String dataString = successJson.optString("data", "");
                    JSONObject dataJson = new JSONObject(dataString);
                    imgUrl = dataJson.optString("imgUrl", "");
                    registUrl = dataJson.optString("registUrl", "");
                    time = String.valueOf(new Date().getTime());
                    Glide.with(NCCUserBindingYhtActivity.this).load(imgUrl + time).placeholder(R.mipmap.ic_launcher).into(imgCheckCode);


                } catch (Exception e) {

                }

            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();

        // 获取手机验证码
        phoneCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String imgCheck = getImgCheck();
                if (isNull(imgCheck)) {
                    showMessage("图形验证码不可为空");
                    return;
                }
                String phone = getPhone();
                if (isNull(phone)) {
                    showMessage("手机号不可为空");
                    return;
                }

                JsonObjectEx paramJson = JsonObjectEx.getJsonObj();
                paramJson.putEx(Constant.userCode, UserUtil.getValueByKey(Constant.userCode));
                paramJson.putEx("account", getPhone()); // 手机号
                paramJson.putEx("imgCode", getImgCheck());  // 图形验证码
                paramJson.putEx("key", time); //ts
                paramJson.putEx("type", "mobile");
                paramJson.putEx("countryCode", "86");
                NetUtil.callAction(NCCUserBindingYhtActivity.this, ConstantUrl.requestPhoneCheckCodeUrl, paramJson, new HttpCallBack() {
                    @Override
                    public void onFailure(JSONObject error) {
                        LogerNcc.e(error);
                        showMessage("失败: " + error);
                    }

                    @Override
                    public void onResponse(JSONObject successJson) {
                        LogerNcc.e(successJson);
//							showMessage("成功: " + successJson);
                        try {
                            String dataString = successJson.optString("data", "");
                            JSONObject dataJson = new JSONObject(dataString);
                            String voiceSid = dataJson.optString("voiceSid", "");
                            String send = dataJson.optString("send", "");
                            String curYhtEnvironment = dataJson.optString("curYhtEnvironment", "");
                            String status = dataJson.optString("status", "");
                            // 正确情况处理
                            if (status.equals("1")) {
                                showMessage("请查收手机验证码");
                                // 正确处理....
                            } else {
                                showMessage("失败: " + dataString);
                            }

                        } catch (Exception e) {
                            LogerNcc.e(e);
                        }

                    }
                });
            }
        });

        // 获取图形验证码
        imgCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestImgCheckCode();
            }
        });
        // 绑定并登录
        btn_bding_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 校验通过
                if (checkParam()) {
                    JsonObjectEx paramJson = JsonObjectEx.getJsonObj();
                    paramJson.putEx(Constant.userCode, UserUtil.getValueByKey(Constant.userCode));
                    paramJson.putEx("account", getPhone()); // 手机号
                    paramJson.putEx("type", "mobile");
                    paramJson.putEx("key", time);// ts
                    paramJson.putEx("activeCode", getPhoneCheckCode());
                    paramJson.putEx("countryCode", "86");
                    paramJson.putEx(Constant.accessToken, UserUtil.getValueByKey(Constant.accessToken));
                    paramJson.putEx(Constant.userCode, UserUtil.getValueByKey(Constant.userCode));
                    NetUtil.callAction(NCCUserBindingYhtActivity.this, ConstantUrl.bandingYhtUrl, paramJson, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            showMessage(error);
                            LogerNcc.e(error);
                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            LogerNcc.e(successJson);
                            try {
                                String code = successJson.optString("code", "");
                                JSONObject dataJson = new JSONObject(successJson.optString("data"));
                                if (code.equals("200")) {
                                    try {
                                        // 处理登录信息
                                        UserUtil.setLoginInfo(dataJson);

                                        // 跳转界面
                                        ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.NCCMainPagerActivity");
                                        Intent intent = new Intent();
                                        intent.setComponent(cn);
                                        Bundle bundle = new Bundle();
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();


                                    } catch (Exception e) {
                                        LogerNcc.e(e);
                                        showMessage(e.getMessage());
                                    }
                                } else {
                                    String msg = dataJson.optString("msg");
                                    showMessage(msg);
                                }
                            } catch (Exception ee) {
                                LogerNcc.e(ee);
                            }

                        }
                    });

                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();

        // 获取图形验证码
        requestImgCheckCode();


        Intent intent = getIntent();
        if (null != intent) {
            Bundle extras = intent.getExtras();
            if (null != extras) {
                userCOode = extras.getString(Constant.userCode, "");
                phone = extras.getString("phone", "");
                et_phone.setText(phone);
            }
        }

        // 手机号是11位
        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
            tv_sendPhoneTo.setText("手机验证码已发送至" + AppCommonUtil.phoneNoHide(phone));
        }


    }
}
