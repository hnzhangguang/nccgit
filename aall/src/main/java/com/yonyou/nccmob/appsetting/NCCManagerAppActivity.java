package com.yonyou.nccmob.appsetting;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.fragment.adapter_app.App;
import com.yonyou.nccmob.fragment.adapter_app.AppGroup;
import com.yonyou.nccmob.fragment.adapter_app.AppGroupAdapter;
import com.yonyou.nccmob.fragment.adapter_app.AppsAdapter;
import com.yonyou.nccmob.fragment.adapter_app.util.AppAdapterUtil;

import java.util.ArrayList;
import java.util.List;


/*
 * @功能: 管理我的引用界面
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class NCCManagerAppActivity extends BaseActivity {
	
	private final String[] mTitles = new String[]{
		  "代办", "预警", "通知"
	};
	AppGroupAdapter appGroupAdapter;
	PagerAdapter changYongAdapter;
	private ImageView back;
	private TextView titleBar_right;
	private RecyclerView mRecyclerView;
	private TabLayout mTabLayout;
	private boolean isInit = true;
	private AppBarLayout mAppBar;
	private List<AppGroup> listAppGroup = new ArrayList<>();
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		TextView pagerTitle = findViewById(R.id.pagerTitle);
		pagerTitle.setText("管理应用");
		titleBar_right = findViewById(R.id.titleBar_right);
		titleBar_right.setVisibility(View.VISIBLE);
		titleBar_right.setText("编辑");
		titleBar_right.setTextColor(getResources().getColor(R.color.white));
		titleBar_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String showTitle = titleBar_right.getText().toString().contains("编辑") ? "完成" : "编辑";
				if ("完成".equals(showTitle)) {
					AppsAdapter.isEdit = true;
				} else {
					AppsAdapter.isEdit = false;
				}
				initChangYong();
				appGroupAdapter.notifyDataSetChanged();
				titleBar_right.setText(showTitle);
			}
		});
		
		back = findViewById(R.id.iv_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		initViews();
		initRecyclerView();
		initChangYong();
		listAppGroup = AppAdapterUtil.addModels();
		
		// 为tabLayout添加names
		for (AppGroup g : listAppGroup) {
			mTabLayout.addTab(mTabLayout.newTab().setText(g.getName()));
		}
		appGroupAdapter = new AppGroupAdapter(mRecyclerView, listAppGroup, mRecyclerView.getContext());
		mRecyclerView.setAdapter(appGroupAdapter);
		
		
	}
	
	private void initChangYong() {
		
		// 常用应用s
		List<App> listApps = AppAdapterUtil.getCommonUseApp();
		
		List<View> list = AppAdapterUtil.getOneViewByAppInfoData(this, listApps, new AppsAdapter.onItemClick() {
			@Override
			public void onClickItemEvent(App app) {
				showMessage(app);
			}
		});
		
		// 常用adapter
		ViewPager viewpagerCommonUse = findViewById(R.id.viewpager_changyong);
		changYongAdapter = new PagerAdapter() {
			@Override
			public int getCount() {
				return 1;
			}
			
			/**
			 * 初始化position位置的界面
			 */
			@Override
			public Object instantiateItem(View view, int position) {
				
				((ViewPager) view).addView(list.get(position), 0);
				
				return list.get(position);
			}
			
			@Override
			public boolean isViewFromObject(View view, Object o) {
				return view == o;
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(list.get(position));
			}
		};
		viewpagerCommonUse.setAdapter(changYongAdapter);
		
		
	}
	
	private void initViews() {
		
		
		// viewpager
		mTabLayout = findViewById(R.id.tab_app_group);
		mAppBar = findViewById(R.id.appbar);
		mRecyclerView = findViewById(R.id.recycler_all);
		
		// tab点击事件
		mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				LogerNcc.e(tab.getText());
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
	
	
	private void initRecyclerView() {
		mSmoothScroller = new LinearSmoothScroller(this) {
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
		
		mLinearLayoutManager = new LinearLayoutManager(this);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// 常用应用的编辑性,改为非编辑态
		AppsAdapter.isEdit = false;
	}
}
