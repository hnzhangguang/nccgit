package com.yonyou.nccmob.act;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

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

import org.json.JSONObject;


/*
 * @功能: ncc忘记密码界面
 * @Date  2020/8/3;
 * @Author zhangg
 **/
public class NCCForgetPWActivity extends BaseActivity {

    TextView et_forgetpw;
    TextView et_forgetemail;
    TextView pagerTitle;
    ImageView iv_back;
    Button btn_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nccforgetpw);
        btn_next = findViewById(R.id.btn_next);
        iv_back = findViewById(R.id.iv_back);
        et_forgetpw = findViewById(R.id.et_forgetpw);
        et_forgetemail = findViewById(R.id.et_forgetemail);
        pagerTitle = findViewById(R.id.pagerTitle);
        pagerTitle.setText("找回密码");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 根据用户code给对应的邮箱发送验证码
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkParam()) {
                    JsonObjectEx paramJson = JsonObjectEx.getJsonObj();
                    paramJson.putEx("bc", UserUtil.getValueByKey(Constant.accountInfoKey, "")); // 账套信息
                    paramJson.putEx("userCode", getUserCode()); // 用户名
                    paramJson.putEx("contact", getEmail()); // 邮箱
                    paramJson.putEx("type", "1"); // 1, 邮箱, 2, 手机号
                    paramJson.putEx("langCode", "simpchn"); //
                    // 发送请求
                    NetUtil.callAction(NCCForgetPWActivity.this, ConstantUrl.forgetUrl_sendCode, paramJson, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            LogerNcc.e(error);
                            showMessage(error.optString("error", "error"));
//								Intent intent = new Intent(NCCForgetPWActivity.this, NCCResetPWActivity.class);
//								Bundle bundle = new Bundle();
//								bundle.putString("email", error.optString("email", ""));
//								intent.putExtras(bundle);
//								startActivity(intent);
//								finish();
                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            LogerNcc.e(successJson);

                            try {
                                String data = successJson.optString("data", "");
                                JSONObject json = new JSONObject(data);

                                String status = json.optString("status", "");
                                String content = json.optString("content", "");
                                // 正确时处理逻辑
                                if ("0".equals(status)) {
                                    UserUtil.setKeyValue("forgetpwcontent", content);

                                    ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.act.NCCResetPWActivity");
                                    Intent intent = new Intent();
                                    intent.setComponent(cn);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", getEmail());
                                    bundle.putString("userCode", getUserCode()); // 用户名
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();


                                } else {
                                    showMessage(content);

                                }
                            } catch (Exception e) {

                            }

                        }
                    });

                }
            }
        });

        // 带用户名
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (intent != null && extras != null) {
            String userCode = extras.getString(Constant.userCode);
            if (!TextUtils.isEmpty(userCode)) {
                et_forgetpw.setText(userCode);
            }
        }


    }


    public boolean checkParam() {
        String userCode = getUserCode();
        if (isNull(userCode)) {
            showMessage("请输入用户名");
            return false;
        }
        String email = getEmail();
        if (isNull(email)) {
            showMessage("请输入验证邮箱");
            return false;
        }
        if (!CheckUtil.isEmail(email)) {
            showMessage("输入邮箱不对");
            return false;
        }

        return true;
    }


    public String getUserCode() {
        String stringForgetPW = et_forgetpw.getText().toString().trim();
        return stringForgetPW;
    }

    public String getEmail() {
        String email = et_forgetemail.getText().toString().trim();
        return email;
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
