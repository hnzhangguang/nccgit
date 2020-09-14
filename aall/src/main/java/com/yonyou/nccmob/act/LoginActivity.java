package com.yonyou.nccmob.act;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.CheckUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.netsetting.NetSettingActivity;
import com.yonyou.nccmob.util.Base64Utils;
import com.yonyou.nccmob.widget.XCDropDownListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * @功能:ncc门户登录界面
 * @Date 9:30 AM 2020/7/17
 * @Author zhangg
 **/
public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.e("mmmm", "handleMessage: " + msg.arg1);
            }
        }
    };
    // 布局内的控件
    private EditText et_name;
    private EditText et_password;
    private Button mLoginBtn;
    private CheckBox checkBox_password;  //
    private CheckBox checkBox_isAutoLogin;   // 自动登录
    private ImageView iv_see_password;
    private TextView tv_setting;
    private TextView forgetPW;
    private TextView up_current_tv; // 切换用户名登录
    private TextView up_backup_tv; // 切换手机号登录
    private XCDropDownListView drop_down_list_view;
    private ProgressDialog mLoadingDialog; // 显示正在加载的对话框

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initView() {
        super.initView();
        up_current_tv = findViewById(R.id.up_current_tv);
        up_backup_tv = findViewById(R.id.up_backup_tv);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        et_name = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        checkBox_password = (CheckBox) findViewById(R.id.checkBox_password);
        checkBox_isAutoLogin = (CheckBox) findViewById(R.id.checkBox_login);
        iv_see_password = (ImageView) findViewById(R.id.iv_see_password);
        tv_setting = findViewById(R.id.tv_setting);
        tv_setting.setText("设置服务地址");
        forgetPW = findViewById(R.id.forgetPW);

        drop_down_list_view = findViewById(R.id.drop_down_list_view);
    }

    @Override
    public void initListener() {
        super.initListener();
        up_backup_tv.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        forgetPW.setOnClickListener(this);
        checkBox_password.setOnCheckedChangeListener(this);
        checkBox_isAutoLogin.setOnCheckedChangeListener(this);
        iv_see_password.setOnClickListener(this);
        tv_setting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(LoginActivity.this, NetSettingActivity.class));
                        //            finish();
                    }
                });
    }

    public void setCurrentLoginUserType() {
        // 登录类型
//		String loginType = UserUtil.getValueByKey(Constant.loginType, "1");
        String loginType = UserUtil.getValueByKey(Constant.loginType, "1");
        if ("1".equals(loginType)) {
            up_backup_tv.setText("手机号登录");
            up_current_tv.setText("用户名登录");
            forgetPW.setVisibility(View.VISIBLE);

        } else {
            up_backup_tv.setText("用户名登录");
            up_current_tv.setText("手机号登录");
            forgetPW.setVisibility(View.INVISIBLE);
        }
    }


    /*
     * @功能: 处理账套列表信息
     * @参数:
     * @Date  2020/8/30 7:04 PM
     * @Author zhangg
     **/
    private void setAccountList() {
        // 账套处理
        ArrayList<String> list = new ArrayList<String>();
        String accountList = UserUtil.getValueByKey(Constant.accountListKey);
        if (isNull(accountList)) {
            list.add("design");
        } else {
            try {
                JSONArray array = new JSONArray(accountList);
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject accountJson = new JSONObject(array.get(i) + "");
                    list.add(accountJson.optString("code"));
                }
            } catch (JSONException e) {
                LogerNcc.e(e);
//                e.printStackTrace();
            }
        }

//        list.add("scm0819");  // 开发环境暂时先使用
        if (list.size() == 1) {  // 如果只有一个时候,直接默认
            UserUtil.setKeyValue(Constant.accountInfoKey, list.get(0));
        }

        drop_down_list_view.setItemsData(list);
    }

    public void initData() {

        setCurrentLoginUserType(); // 处理登录账户类型


        // 自动登录标识处理
        String aFalse = UserUtil.getValueByKey(Constant.isAutoLogin, "false");
        checkBox_isAutoLogin.setChecked(aFalse.equals("true") ? true : false);

        // 还原账号
        String userCode = UserUtil.getValueByKey(Constant.userCode);
        String password = UserUtil.getValueByKey(Constant.password);
        et_name.setText(userCode);
        et_password.setText(password);
        et_name.setText("zhangg");
        et_password.setText("123456a");


        // 设置账套信息
        String account = UserUtil.getValueByKey(Constant.accountInfoKey);
        if (!TextUtils.isEmpty(account)) {
            drop_down_list_view.setEditText(account);
        }


        // 如果ip为空,到设置ip界面
        String valueByKey = UserUtil.getValueByKey(Constant.net_ip);
        if (isNull(valueByKey)) {
            startActivity(new Intent(LoginActivity.this, NetSettingActivity.class));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up_backup_tv:

                // 判定登录方式(用户名/手机号)
                String string = up_backup_tv.getText().toString();
                UserUtil.setKeyValue(Constant.loginType, "用户名登录".equals(string) ? "1" : "2");
                setCurrentLoginUserType();

                break;
            case R.id.btn_login:
                try {
                    login(); // 登陆
                } catch (JSONException e) {
                    LogerNcc.e(e);
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    e.printStackTrace();
                }
                break;
            case R.id.iv_see_password:
                setPasswordVisibility(); // 改变图片并设置输入框的文本可见或不可见
                break;
            case R.id.forgetPW:
//                showMessage("你点击了忘记密码按钮~");
                Intent intent = new Intent(this, NCCForgetPWActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.userCode, getAccount());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        // 获取SharedPreferences对象，使用自定义类的方法来获取对象
        String password = UserUtil.getValueByKey("password", "");
        return Base64Utils.decryptBASE64(password); // 解码一下
        //       return password;   //解码一下

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLoginButtonEnabled(true);

        try {
            setAccountList(); // 处理账套信息
        } catch (Exception e) {

        }

    }

    /*
     * @功能: 设置登录按钮的可用性
     * @参数  isEnabled
     * @Date  2020/7/29;
     * @Author zhangg
     **/
    public void setLoginButtonEnabled(boolean isEnabled) {
        mLoginBtn.setEnabled(isEnabled);
    }

    /*
     * @功能: 登录前校验
     * @Date  2020/8/13;
     * @Author zhangg
     **/
    private boolean checkLoginParam() {

        String ip = UserUtil.getValueByKey(Constant.net_ip);
        String port = UserUtil.getValueByKey(Constant.net_port);
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            showMessage("IP 端口不允许为空");
            return false;
        }

        if (TextUtils.isEmpty(getAccount())) {
            showMessage("登录账号不允许为空");
            return false;
        }
        // 登录类型 1 是用户名+密码 ; 2 手机号+密码
        String loginType = UserUtil.getValueByKey(Constant.loginType, "1");
        if (loginType.equals("2")) {
            boolean mobileNO = CheckUtil.isMobileNO(getAccount());
            if (!mobileNO) {
                showMessage("手机号输入有误,请检查");
                return false;
            }
        }
        if (TextUtils.isEmpty(getPassword())) {
            showMessage("密码不允许为空");
            return false;
        }

        return true;
    }

    /*
     * @功能: 测试消息接口
     * @Date  2020/8/13;
     * @Author zhangg
     **/
    private void callMessage() {
        try {
            String url = "/nccloud/mob/platform/standard/login";  // 相对url路径
            JSONObject parmJson = new JSONObject();
            parmJson.put("userid", "");
            NetUtil.callAction(LoginActivity.this, url, parmJson, new HttpCallBack() {
                @Override
                public void onFailure(JSONObject error) {

                }

                @Override
                public void onResponse(JSONObject successJson) {

                }
            });
        } catch (Exception e) {
            LogerNcc.e(e);
        }
    }


    /**
     * 模拟登录情况 用户名csdn，密码123456，就能登录成功，否则登录失败
     */
    private void login() throws JSONException {

        if (true) {
//            String string = "{\"data\":{\"messages\":{\"todo\":[{\"pk_message\":\"1001Z01000000002D9LN\",\"pk_detail\":\"1001Z01000000002D9LM\",\"subject\":\"zhuhlm提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e555\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202005280077\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e32123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e23123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-28 10:14:54\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002DA7D\",\"pk_detail\":\"1001Z01000000002DA5T\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202005280080\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e362123123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e332123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-28 10:18:28\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002DZRV\",\"pk_detail\":\"1001Z01000000002DZQB\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202006030081\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e56456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e745\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-06-03 13:50:33\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002O04X\",\"pk_detail\":\"1001Z01000000002O04N\",\"subject\":\"kejl提交单据ZDYDJ202007300019，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-07-30 15:18:43\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-05 14:55:56\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002NVCP\",\"pk_detail\":\"1001A01000000002NVCM\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202008050120\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e5645456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e56454565645456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-05 10:22:53\",\"attachment\":[]},{\"pk_message\":\"1001Z010000000030DFC\",\"pk_detail\":\"1001Z010000000030DFA\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202008250128\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e5654656\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e54456564\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-25 17:02:52\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002OMSH\",\"pk_detail\":\"1001Z01000000002OMSE\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202008100122\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e56456456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e45456456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-10 13:41:24\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002NCI4\",\"pk_detail\":\"1001A01000000002NCI2\",\"subject\":\"kejl提交单据ZDYDJ202008030025，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-08-03 16:15:57\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-03 16:16:39\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002O05K\",\"pk_detail\":\"1001Z01000000002O05J\",\"subject\":\"kejl 加签,单据类型：自定义工单,单据号: ZDYDJ202007220011,请审批单据\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-05 15:25:48\",\"attachment\":[]}],\"prealert\":[],\"notice\":[{\"pk_message\":\"1001A01000000002L9YR\",\"pk_detail\":\"1001A01000000002L9Y8\",\"subject\":\"kejl提交单据ZDYDJ202007220011，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-07-22 19:35:19\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-22 19:35:28\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ6N\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-20 17:26:12\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVIJ\",\"pk_detail\":\"1001Z01000000002ZVIC\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:48:26\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7S9\",\"pk_detail\":\"1001A01000000002L7RJ\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:32:01\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000KK\",\"pk_detail\":\"1001A01000000002L8CI\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:55:00\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002MGTK\",\"pk_detail\":\"1001A01000000002MGT0\",\"subject\":\"kejl提交单据ZDYDJ202007290012，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-07-29 19:44:06\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-29 19:44:32\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7TU\",\"pk_detail\":\"1001A01000000002L7TL\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:33:54\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7YW\",\"pk_detail\":\"1001A01000000002L7YN\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 13:36:27\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000H3\",\"pk_detail\":\"1002A0100000000000GS\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:10:36\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000001T5\",\"pk_detail\":\"1001A01000000002L9AE\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-22 16:54:23\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ6E\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-13 15:31:21\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7VG\",\"pk_detail\":\"1001A01000000002L7V7\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:35:40\",\"attachment\":[]},{\"pk_message\":\"1001ZZ10000000029T9A\",\"pk_detail\":\"1001ZZ10000000029T86\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"sms\",\"sendtime\":\"2020-04-22 19:43:00\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ66\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-13 15:30:41\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L5TT\",\"pk_detail\":\"1001A01000000002L5TK\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003ekeyy\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202007210086\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e5444564654\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e0897878\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 09:38:17\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000HX\",\"pk_detail\":\"1001A01000000002L88W\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:12:45\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVM7\",\"pk_detail\":\"1001Z01000000002ZVLP\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e555\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202005210071\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfdsf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfsdf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-24 10:22:25\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ6J\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-13 15:31:40\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZOIM\",\"pk_detail\":\"1001Z01000000002ZOIG\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:42:38\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVHZ\",\"pk_detail\":\"1001Z01000000002ZVHW\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:47:53\",\"attachment\":[]},{\"pk_message\":\"1001Z010000000029MDV\",\"pk_detail\":\"1001Z010000000029MC4\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"sms\",\"sendtime\":\"2020-04-21 20:07:09\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002L6ZI\",\"pk_detail\":\"1001A01000000002L642\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003ekeyy\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202007210091\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e5645456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e56456456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:29:28\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVLC\",\"pk_detail\":\"1001Z01000000002ZVL9\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e555\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202005210071\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfdsf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfsdf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-24 10:01:10\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000JD\",\"pk_detail\":\"1001Z01000000002L8JL\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:50:13\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZOI3\",\"pk_detail\":\"1001Z01000000002ZOI0\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:42:16\",\"attachment\":[]}]}},\"success\":true}";
//            List<MessageVO> todo_msg = MsgUtil.getMessageVosByType(string, Constant.todo_msg);
//            List<MessageVO> alter_msg = MsgUtil.getMessageVosByType(string, Constant.alter_msg);
//            List<MessageVO> notification_msg = MsgUtil.getMessageVosByType(string, Constant.notification_msg);
//            LogerNcc.e(todo_msg.size());
//            LogerNcc.e(alter_msg.size());
//            LogerNcc.e(notification_msg.size());
//            ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.NCCMainPagerActivity");
//            Intent intent = new Intent();
//            intent.setComponent(cn);
//            Bundle bundle = new Bundle();
//            bundle.putString("aa", "aa");
//            intent.putExtras(bundle);
//            startActivity(intent);
//            String userAgent = System.getProperty("http.agent");
//            LogerNcc.e(userAgent);
//            return;
        }

        // 校验是否通过
        if (checkLoginParam()) {

            // 请求参数json
            JSONObject parmJson = new JSONObject();
            parmJson.put(Constant.userCode, getAccount());
            parmJson.put(Constant.password, getPassword());
            String loginType = UserUtil.getValueByKey(Constant.loginType, "1");
            parmJson.put(Constant.loginType, loginType); // 1为ncc用户登录,2位友户通用户登录

            setLoginButtonEnabled(false);
            showLoading(); // 显示加载框
            // 执行登录action
            NetUtil.callAction(LoginActivity.this, ConstantUrl.loginUrl, parmJson, new HttpCallBack() {
                @Override
                public void onFailure(JSONObject error) {
                    LogerNcc.e(error);
                    // 登录按钮可用性
                    setLoginButtonEnabled(true);
                    // dialog 消失掉
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    try {
                        String string = error.optString(Constant.CODE);
                        showMessage(error.optString(Constant.MSG, "error"));
                        // 用户不存在或是密码不正确 or 该用户没有移动应用的权限
                        if (string.equals("401")) {
                            return;
                        }
                        // 首次登陆初始化密码
                        if (string.equals("201") || string.equals("202")) {
                            // 把用户code保存一下
                            UserUtil.setKeyValue(Constant.userCode, getAccount());
                            String data = error.optString("data", "");
                            if (!TextUtils.isEmpty(data)) {
                                JSONObject dataJson = new JSONObject(data);
                                String accessToken = dataJson.optString("accessToken", ""); // 修改密码时候的token
                                // 把token保存下来
                                UserUtil.setKeyValue(Constant.accessToken, accessToken);

                                if (string.equals("201")) {
                                    et_password.setText("");  // 情况密码,让重新登录
                                    //  第一次重置密码
                                    startActivity(new Intent(LoginActivity.this, NCCUserFirstResetPwActivity.class));

                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Constant.userCode, getAccount());
                                    bundle.putString("phone", "18001105602");
                                    Intent intent = new Intent(LoginActivity.this, NCCUserBindingYhtActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);//  去绑定yht
                                }
                            }
                        }

                    } catch (Exception e) {
                        LogerNcc.e(e);
                    }
                }

                @Override
                public void onResponse(JSONObject successJson) {
//                    LogerNcc.e(successJson);
                    // 无论如何保存一下用户名
                    saveUserName();
                    setLoginButtonEnabled(true);
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    String data = successJson.optString("data", "");
                    String code = successJson.optString("code", "");
                    String message = successJson.optString("message", "");

                    // 处理登录信息
                    UserUtil.setLoginInfo(data);

                    // 跳转界面
//                      startActivity(new Intent(LoginActivity.this, NCCMainPagerActivity.class));
                    ComponentName cn = new ComponentName(getPackageName(), "com.yonyou.nccmob.NCCMainPagerActivity");
                    Intent intent = new Intent();
                    intent.setComponent(cn);
                    Bundle bundle = new Bundle();
                    bundle.putString("aa", "aa");
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
        }
    }

    /**
     * 保存用户账号
     */
    public void saveUserName() {
        // 把登录账号记录下来
        UserUtil.setKeyValue(Constant.userCode, getAccount());
        UserUtil.setKeyValue(Constant.password, getPassword());

    }

    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (iv_see_password.isSelected()) {
            iv_see_password.setSelected(false);
            // 密码不可见
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password.setSelected(true);
            // 密码可见
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    /**
     * 获取账号
     */
    public String getAccount() {
        return et_name.getText().toString().trim(); // 去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return et_password.getText().toString().trim(); // 去掉空格
    }


    /**
     * 是否可以点击登录按钮
     *
     * @param clickable
     */
    public void setLoginBtnClickable(boolean clickable) {
        mLoginBtn.setClickable(clickable);
    }

    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            //            mLoadingDialog = new ProgressDialog(this, getString(R.string.loading), false);
            mLoadingDialog = new ProgressDialog(this);
        }
        mLoadingDialog.show();
    }

    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.hide();
                        }
                    });
        }
    }

    /**
     * CheckBox点击时的回调方法 ,不管是勾选还是取消勾选都会得到回调
     *
     * @param buttonView 按钮对象
     * @param isChecked  按钮的状态
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_password) { // 记住密码选框发生改变时

        } else if (buttonView == checkBox_isAutoLogin) { // 自动登陆选框发生改变时
            checkBox_isAutoLogin.setChecked(isChecked);
            UserUtil.setKeyValue(Constant.isAutoLogin, isChecked ? "true" : "false");
        }
    }

    /**
     * 监听回退键
     */
    @Override
    public void onBackPressed() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.cancel();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 页面销毁前回调的方法
     */
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }
}
