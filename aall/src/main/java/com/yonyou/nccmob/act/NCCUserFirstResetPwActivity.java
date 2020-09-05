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

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * @功能: ncc用户第一次登录系统需要重置密码
 * @Date  2020/8/17;
 * @Author zhangg
 **/
public class NCCUserFirstResetPwActivity extends BaseActivity {


    ImageView iv_back;
    TextView pagerTitle;
    EditText et_setnewpw;
    Button btn_resetpw;
    BaseActivity mActivity;

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.activity_nccuserfirstresetpw);
    }


    @Override
    public void initView() {
        super.initView();
        mActivity = this;
        btn_resetpw = findViewById(R.id.btn_resetpw);
        et_setnewpw = findViewById(R.id.et_setnewpw);
        pagerTitle = findViewById(R.id.pagerTitle);
        pagerTitle.setText("重置密码");
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private String getNewPW() {
        return et_setnewpw.getText().toString().trim();
    }

    public boolean checkParam() {
        String stringNewPW = getNewPW();
        if (TextUtils.isEmpty(stringNewPW)) {
            showMessage("请输入新密码");
            return false;
        }
        // 校验长度
        int length = stringNewPW.length();
        if (length < 6) {
            showMessage("新密码最小长度6位数字、字符组合");
            return false;
        }
        // 校验包含字母
        Pattern p = Pattern.compile("[A-Za-z]");
        Matcher m = p.matcher(stringNewPW);
        if (!m.find()) {
            showMessage("新密码应包含字母");
            return false;
        }
        p = Pattern.compile("[0-9]");
        m = p.matcher(stringNewPW);
        if (!m.find()) {
            showMessage("新密码应包含数字");
            return false;
        }
        String s = stringNewPW.replaceAll("[A-Za-z]", "").replaceAll("[0-9]", "");
        if (!TextUtils.isEmpty(s)) {
            showMessage("新密码只能包含字母、数字");
            return false;
        }
        return true;
    }

    @Override
    public void initListener() {
        super.initListener();
        btn_resetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 校验通过
                if (checkParam()) {
                    JsonObjectEx paramJson = JsonObjectEx.getJsonObj();
                    paramJson.putEx(Constant.accessToken, UserUtil.getValueByKey(Constant.accessToken));
                    paramJson.putEx("pwd", getNewPW()); // 新密码-这个需要加密后传输

                    // 发送请求
                    NetUtil.callAction(NCCUserFirstResetPwActivity.this, ConstantUrl.firstResetInitUrl, paramJson, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            LogerNcc.e(error);

                            String message = error.optString("message", "");
                            if (!TextUtils.isEmpty(message)) {
                                showMessage(message);
                            }

                            ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.act.LoginActivity");
                            Intent intent = new Intent();
                            intent.setComponent(cn);
                            Bundle bundle = new Bundle();
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();


                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            LogerNcc.e(successJson);

                            String data = successJson.optString("data", "");
                            // 处理登录信息
                            UserUtil.setLoginInfo(data);

                            // 页面跳转
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

    @Override
    public void initData() {
        super.initData();
    }
}
