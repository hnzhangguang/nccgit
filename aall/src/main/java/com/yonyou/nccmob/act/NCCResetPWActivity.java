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

import androidx.annotation.Nullable;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.AppCommonUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * @功能: ncc设置新密码界面
 * @Date  2020/8/3;
 * @Author zhangg
 **/
public class NCCResetPWActivity extends BaseActivity {

    EditText et_emailcheckcode; // 邮箱验证码
    EditText et_setnewpw; // 设置新密码
    TextView pagerTitle;
    ImageView iv_back;
    Button btn_resetpw_login;
    TextView tv_sendemail;
    String email;
    String userCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nccresetnewpw);
        tv_sendemail = findViewById(R.id.tv_sendemail);
        btn_resetpw_login = findViewById(R.id.btn_resetpw_login);
        iv_back = findViewById(R.id.iv_back);
        et_emailcheckcode = findViewById(R.id.et_emailcheckcode);
        et_setnewpw = findViewById(R.id.et_setnewpw);
        pagerTitle = findViewById(R.id.pagerTitle);
        pagerTitle.setText("设置新密码");


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 填写发送邮箱信息
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            email = extras.getString("email", "");  // 邮箱
            userCode = extras.getString("userCode", "");  // 用户名
            tv_sendemail.setText("邮箱验证码已发送至" + AppCommonUtil.emailHide(email)); // 显示邮箱
        }
        // 登录
        btn_resetpw_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkParam()) {
                    JsonObjectEx paramJson = JsonObjectEx.getJsonObj();
                    paramJson.putEx("key", UserUtil.getValueByKey("forgetpwcontent", ""));
                    paramJson.putEx("identifyCode", getCheckCode()); // 邮箱验证码
                    paramJson.putEx("type", "1"); // 邮箱验证码
                    paramJson.putEx("userCode", userCode); // 用户名
                    paramJson.putEx("contact", email); // 邮箱
                    paramJson.putEx("pwd", getNewPW()); // 新密码-这个需要加密后传输
                    paramJson.putEx("bc", UserUtil.getValueByKey(Constant.accountInfoKey, "")); // 账套信息

                    // 发送请求
                    NetUtil.callAction(NCCResetPWActivity.this, ConstantUrl.checkCode_newpw_resetpw_Url, paramJson, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            LogerNcc.e(error);
                            try {
                                String string = error.optString("code");
                                if ("202".equals(string)) {
                                    showMessage("需要绑定友户通");
                                } else {
                                    showMessage(Constant.MSG);
                                }


                                ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.act.LoginActivity");
                                Intent intent = new Intent();
                                intent.setComponent(cn);
                                Bundle bundle = new Bundle();
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();


                            } catch (Exception e) {
                                LogerNcc.e(e);
                            }

                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            LogerNcc.e(successJson);

                            String data = successJson.optString("data", "");
                            // 处理登录信息
                            UserUtil.setLoginInfo(data);

                            ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.NCCMainPagerActivity");
                            Intent intent = new Intent();
                            intent.setComponent(cn);
                            Bundle bundle = new Bundle();
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();


                        }
                    });
                }
            }
        });
    }


    public String getNewPW() {
        String stringResetNewPW = et_setnewpw.getText().toString().trim();
        return stringResetNewPW;
    }

    public String getCheckCode() {
        String stringEmailCheckCode = et_emailcheckcode.getText().toString().trim();
        return stringEmailCheckCode;
    }

    public boolean checkParam() {
        String stringEmailCheckCode = et_emailcheckcode.getText().toString().trim();
        if (TextUtils.isEmpty(stringEmailCheckCode)) {
            showMessage("请输入邮箱验证码");
            return false;
        }
        String stringResetNewPW = et_setnewpw.getText().toString().trim();
        if (TextUtils.isEmpty(stringResetNewPW)) {
            showMessage("请输入新密码");
            return false;
        } else {  // 校验新密码
            // 校验长度
            int length = stringResetNewPW.length();
            if (length < 6) {
                showMessage("新密码最小长度6位数字、字符组合");
                return false;
            }
            // 校验包含字母
            Pattern p = Pattern.compile("[A-Za-z]");
            Matcher m = p.matcher(stringResetNewPW);
            if (!m.find()) {
                showMessage("新密码应包含字母");
                return false;
            }
            p = Pattern.compile("[0-9]");
            m = p.matcher(stringResetNewPW);
            if (!m.find()) {
                showMessage("新密码应包含数字");
                return false;
            }
            String s = stringResetNewPW.replaceAll("[A-Za-z]", "").replaceAll("[0-9]", "");
            if (!TextUtils.isEmpty(s)) {
                showMessage("新密码只能包含字母、数字");
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


}
