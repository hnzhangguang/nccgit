package com.yonyou.nccmob.act;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.common.vo.NCCUserVo;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import org.json.JSONObject;


/*
 * @功能: 修改密码:根据就密码设置新密码
 * @Date  2020/9/1 2:42 PM
 * @Author zhangg
 **/
public class NCCModifyPwActivity extends BaseActivity {

    TextView titleBar_right; // 右边的tv
    TextView modify_userName;
    TextView modify_old_pw;
    TextView modify_new_pw;
    TextView modify_confirm_pw;
    TextView modify_forgetpw;

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.activity_ncc_modifypw);
    }

    @Override
    public void initView() {
        super.initView();
        titleBar_right = findViewById(R.id.titleBar_right);
        modify_userName = findViewById(R.id.modify_userName);
        modify_old_pw = findViewById(R.id.modify_old_pw);
        modify_new_pw = findViewById(R.id.modify_new_pw);
        modify_forgetpw = findViewById(R.id.modify_forgetpw);
        modify_confirm_pw = findViewById(R.id.modify_confirm_pw);
        View iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleBar_right.setText("确定");
        titleBar_right.setVisibility(View.VISIBLE);
        titleBar_right.setTextColor(getResources().getColor(R.color.white));
        titleBar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkParam()) {
                    JsonObjectEx jsonObj = JsonObjectEx.getJsonObj();
                    jsonObj.putEx("userCode", UserUtil.getValueByKey(Constant.userCode));
                    jsonObj.putEx("oldPassword", getModify_old_pw());
                    jsonObj.putEx("newPassword", getModify_new_pw());
                    jsonObj.putEx("newPassword", getModify_confirm_pw());
                    NetUtil.callAction(NCCModifyPwActivity.this, ConstantUrl.requestmodifypwUrl, jsonObj, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            LogerNcc.e(error);
                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            LogerNcc.e(successJson);
                            String code = successJson.optString("code");
                            if ("200".equals(code)) {
                                showMessage("修改成功");
                            }
                        }
                    });
                }
            }
        });

        // 忘记密码
        modify_forgetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.act.NCCForgetPWActivity");
                Intent intent = new Intent();
                intent.setComponent(cn);
                Bundle bundle = new Bundle();
                bundle.putString("aa", "aa");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });


        // 设置用户名
        NCCUserVo currentUserVo = UserUtil.getCurrentUserVo();
        if (null != currentUserVo) {
            String userName = currentUserVo.getUserName();
            modify_userName.setText(userName);
        }


    }

    public boolean checkParam() {
        if (isNull(getModify_old_pw())){
            showMessage("旧密码不可为空");
            return  false;
        }
        if (isNull(getModify_new_pw())){
            showMessage("新密码不可为空");
            return  false;
        }
        if (isNull(getModify_confirm_pw())){
            showMessage("确认密码不可为空");
            return  false;
        }
        boolean equals = getModify_new_pw().equals(getModify_confirm_pw());
        if (equals) {
            String modify_new_pw = getModify_new_pw();
            if (modify_new_pw.length() < 6) {
                showMessage("新密码最小长度6位");
                return false;
            }
        } else {
            showMessage("新密码和确认密码不一致");
            return false;
        }
        return true;
    }

    public String getModify_confirm_pw() {
        return modify_confirm_pw.getText().toString();
    }

    public String getModify_new_pw() {
        return modify_new_pw.getText().toString();
    }

    public String getModify_old_pw() {
        return modify_old_pw.getText().toString();
    }

    public String getModify_userName() {
        return modify_userName.getText().toString();
    }


    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initData() {
        super.initData();
    }


}
