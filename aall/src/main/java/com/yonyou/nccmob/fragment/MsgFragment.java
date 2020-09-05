package com.yonyou.nccmob.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.appsetting.NCCManagerAppActivity;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.base.BaseFragment;
import com.yonyou.nccmob.fragment.adapter_app.App;
import com.yonyou.nccmob.fragment.adapter_app.AppsAdapter;
import com.yonyou.nccmob.fragment.adapter_app.util.AppAdapterUtil;
import com.yonyou.nccmob.fragment.msg.Fragment_undo;
import com.yonyou.nccmob.message.history.AlterFragment;
import com.yonyou.nccmob.message.history.NoTODOFragment;
import com.yonyou.nccmob.message.history.NotificationFragment;
import com.yonyou.nccmob.widget.ChildViewPager;

import java.util.List;


/*
 * @功能: 消息 fragment
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class MsgFragment extends BaseFragment {

    private static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    private final String[] mTitles = new String[]{
            "代办", "预警", "通知"
    };
    private View mRootView;
    private ChildViewPager mViewPager;
    private TabLayout mTabLayout;
    private BaseActivity mActivity;

    public static MsgFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TITLE, title);
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();

        // 从bundle中拿数据
        Bundle arguments = getArguments();
        if (arguments != null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.i("mmmm", "MsgFragment");
            mRootView = inflater.inflate(R.layout.msg_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        initListener(mRootView);
        initData(mRootView);

        return mRootView;
    }

    private void initData(View mRootView) {

//		ArrayList mFragments = new ArrayList<>();
//		for (int i = 0; i < mTitles.length; i++) {
//			ImageFragment imageFragment = ImageFragment.newInstance(R.mipmap.ic_launcher);
//			mFragments.add(imageFragment);
//		}

        mViewPager.setAdapter(new FragmentPagerAdapter(mActivity.getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return NoTODOFragment.newInstance(0);
                    case 1:
                        return AlterFragment.newInstance(1);
                    case 2:
                        return NotificationFragment.newInstance(2);
                    default:
                        return new Fragment_undo();
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return mTitles[0];
                    case 1:
                        return mTitles[1];
                    case 2:
                        return mTitles[2];
                    default:
                        return "default";
                }
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);


        // 常用应用s
        List<App> listApps = AppAdapterUtil.getCommonUseApp();
        listApps.add(new App("Add", false));

        // 获取views
        List<View> pagerViews = (AppAdapterUtil.getViewsByAppInfoData(mActivity, listApps, new AppsAdapter.onItemClick() {
            @Override
            public void onClickItemEvent(App app) {
                mActivity.showMessage(app);
                if (app.getShortName().equals("Add")) {
                    Intent intent = new Intent(mActivity, NCCManagerAppActivity.class);
                    mActivity.startActivity(intent);
                }
            }
        }));


        // 常用adapter
        ViewPager viewpagerCommonUse = mRootView.findViewById(R.id.viewpager_changyong);
        viewpagerCommonUse.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pagerViews.size();
            }

            /**
             * 初始化position位置的界面
             */
            @Override
            public Object instantiateItem(View view, int position) {

                ((ViewPager) view).addView(pagerViews.get(position), 0);

                return pagerViews.get(position);
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(pagerViews.get(position));
            }
        });

    }

    private void initListener(View mRootView) {

        // viewpager
        mTabLayout = mRootView.findViewById(R.id.tab_app_group1);
        mViewPager = mRootView.findViewById(R.id.viewpager_all);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                LogerNcc.e(String.format("%d/" + mTitles.length, position + 1));
            }
        });

        // tab点击事件
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//				LogerNcc.e(tab.getText());
//				mActivity.showMessage(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
