package com.yonyou.nccmob.fragment;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.base.BaseFragment;
import com.yonyou.nccmob.fragment.adapter_app.AppGroup;
import com.yonyou.nccmob.fragment.adapter_app.AppGroupAdapter;
import com.yonyou.nccmob.fragment.adapter_app.util.AppAdapterUtil;

import java.util.ArrayList;
import java.util.List;


/*
 * @功能: ncc 应用fragment
 * @Date  2020/8/10;
 * @Author zhangg
 **/
public class AppH5Fragment extends BaseFragment {

    private static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    private View mRootView;

    private List<AppGroup> listAppGroup = new ArrayList<>();

    /**
     * TabLayout
     */
    private TabLayout mTabLayout;
    /**
     * 内容列表
     */
    private RecyclerView mRecyclerView;
    /**
     * AppBarLayout
     */
    private AppBarLayout mAppBar;

    /**
     * LinearLayoutManager
     */
    private LinearLayoutManager mLinearLayoutManager;


    /**
     * 是否处于滚动状态，避免连锁反应
     */
    private boolean isScroll;
    /**
     * RecyclerView高度
     */
    private int mRecyclerViewHeight;
    /**
     * 平滑滚动 Scroller
     */
    private RecyclerView.SmoothScroller mSmoothScroller;


    private boolean isInit = true;
    private BaseActivity mActivity;
    private SwipeRefreshLayout swipview;


    public static AppH5Fragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TITLE, title);
        AppH5Fragment fragment = new AppH5Fragment();
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

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.i("mmmm", "AppH5Fragment");
            mRootView = inflater.inflate(R.layout.apph5_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        findViews(mRootView);
        mActivity = (BaseActivity) getActivity();
        return mRootView;
    }


    private void reloadData() {
        mActivity.showMessage("reloadData");
        //加载完成后改变状态
        swipview.setRefreshing(false);
    }


    private void findViews(View mRootView) {

        // 轮播图 ,新隐藏掉
        //Banner banner = mRootView.findViewById(R.id.banner);
        // 本地图片数据（资源文件）
//		List<Integer> list = new ArrayList<>();
//		list.add(R.mipmap.b1);
//		list.add(R.mipmap.b2);
//		list.add(R.mipmap.b3);
//		banner.setImages(list).setImageLoader(new GlideImageLoader()).start();


        //  下拉刷新
        swipview = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiper);
        swipview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });


        mTabLayout = mRootView.findViewById(R.id.tab_app_group);
        mRecyclerView = mRootView.findViewById(R.id.recycler_all);
        mAppBar = mRootView.findViewById(R.id.appbar);
        initRecyclerView();
        initTabLayout();
        listAppGroup = AppAdapterUtil.addModels();

        // 为tabLayout添加names
        for (AppGroup g : listAppGroup) {
            mTabLayout.addTab(mTabLayout.newTab().setText(g.getName()));
        }
        AppGroupAdapter appGroupAdapter = new AppGroupAdapter(mRecyclerView, listAppGroup, getContext());
        mRecyclerView.setAdapter(appGroupAdapter);

    }


    private void initTabLayout() {
//        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
//        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
//        mTabLayout.setTabTextColors(Color.BLACK, ContextCompat.getColor(this, R.color.colorAccent));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isInit) {
                    mAppBar.setExpanded(false, true);
                }
                if (isInit) isInit = false;

                //点击tab的时候，RecyclerView自动滑到该tab对应的item位置
                int position = tab.getPosition();
                if (!isScroll) {
                    // 有动画且滚动到顶部
                    mSmoothScroller.setTargetPosition(position);
                    mLinearLayoutManager.startSmoothScroll(mSmoothScroller);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void initRecyclerView() {
        mSmoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Nullable
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return mLinearLayoutManager.computeScrollVectorForPosition(targetPosition);
            }
        };

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScroll = false;
                } else {
                    isScroll = true;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滑动RecyclerView list的时候，根据最上面一个Item的position来切换tab
//                mTabLayout.setScrollPosition(mLinearLayoutManager.findFirstVisibleItemPosition(), 0, true);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                TabLayout.Tab tabAt = mTabLayout.getTabAt(layoutManager.findFirstVisibleItemPosition());
                if (tabAt != null && !tabAt.isSelected()) {
                    tabAt.select();
                }
            }
        });
    }


}
