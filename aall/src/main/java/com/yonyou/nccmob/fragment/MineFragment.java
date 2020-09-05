package com.yonyou.nccmob.fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.download.OfflineUpdateControl;
import com.yonyou.common.utils.FileUtils;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.vo.NCCUserVo;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.act.LoginActivity;
import com.yonyou.nccmob.appsetting.usersetting.AboutWeActivity;
import com.yonyou.nccmob.appsetting.usersetting.ContactsWeActivity;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.base.BaseFragment;

/*
 * @功能: 我的设置
 * @Date  2020/8/31 7:00 PM
 * @Author zhangg
 **/
public class MineFragment extends BaseFragment {

    private static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    private View mRootView;
    private TextView userName; // 用户名
    private TextView userPhone; // 手机号
    private View account_safe; // 账户安全
    private TextView tv_account_safe;
    private View clearCahce;

    public static MineFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TITLE, title);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 从bundle中拿数据
        Bundle arguments = getArguments();
        if (arguments != null) {
        }
    }

    public void setUserName(String name) {
        userName.setText(name);
    }

    public void setUserPhone(String phone) {
        userPhone.setText(phone);
    }

    public void initView(View mRootView) {
        userName = mRootView.findViewById(R.id.userName);
        userPhone = mRootView.findViewById(R.id.userPhone);
        account_safe = mRootView.findViewById(R.id.account_safe);
        tv_account_safe = mRootView.findViewById(R.id.tv_account_safe);
        clearCahce = mRootView.findViewById(R.id.clearCahce);

    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "MineFragment");
            mRootView = inflater.inflate(R.layout.mine_fragment, container, false);
            initView(mRootView);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        clearCahce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = OfflineUpdateControl.getOfflinePathWhitoutAppId(getContext());
                LogerNcc.e(filePath);

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 删除/data/user/0/com.yonyou.nccmob/files目录下面的所有文件
                        FileUtils.deleteDirectory(filePath);
                        FragmentActivity activity = getActivity();
                        BaseActivity mActivity = (BaseActivity) activity;
                        mActivity.showMessage("清理完毕");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setMessage("确定要清除本地缓存?").create();
                alertDialog.show();


            }
        });

        // 账户安全
        account_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String valueByKey = UserUtil.getValueByKey(Constant.loginType, "1");
                // 只有ncc账号+密码登录的时候才允许在此修改密码
                if (valueByKey.equals("1")) {
                    ComponentName cn = new ComponentName(getActivity().getPackageName(), "com.yonyou.nccmob.act.NCCModifyPwActivity");
                    Intent intent = new Intent();
                    intent.setComponent(cn);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.userCode, userName.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setMessage("请前往友户通官网更改您的账户密码").create();
                    alertDialog.show();

                }


            }
        });

        //联系我们
        View contactsWe = mRootView.findViewById(R.id.contactsWe);
        contactsWe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(container.getContext(), ContactsWeActivity.class);
                container.getContext().startActivity(intent);
            }
        });


        //关于我们
        View aboutWe = mRootView.findViewById(R.id.aboutWe);
        aboutWe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(container.getContext(), AboutWeActivity.class);
                container.getContext().startActivity(intent);
            }
        });

        // 退出登录
        TextView outLogin = mRootView.findViewById(R.id.outLogin);
        outLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//				outLoginCallAction();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

                UserUtil.setKeyValue(Constant.isAutoLogin, "false");

                // 退出登录处理逻辑
                outLoginHandler();
            }
        });


        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        NCCUserVo currentUserVo = UserUtil.getCurrentUserVo();
        if (null != currentUserVo) {
            setUserName(currentUserVo.getUserName());
            setUserPhone(currentUserVo.getPhone());
        }

        
        String valueByKey = UserUtil.getValueByKey(Constant.loginType, "1");
        if (valueByKey.equals("2")) {
//            account_safe.setEnabled(false);
//            tv_account_safe.setEnabled(false);
        }
    }

    /*
     * @功能: 退出登录处理函数
     * @Date  2020/8/30;
     * @Author zhangg
     **/
    private void outLoginHandler() {

        UserUtil.setKeyValue(Constant.accessToken, ""); // 清空token
        UserUtil.setKeyValue(Constant.isAutoLogin, "false"); // 自动登录标识清理掉

    }


}
