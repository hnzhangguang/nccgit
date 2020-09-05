package com.yonyou.nccmob;

import android.Manifest;
import android.content.Intent;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.utils.AppCommonUtil;
import com.yonyou.common.utils.utils.ThemeUtil;
import com.yonyou.common.vo.MessageEventVo;
import com.yonyou.nccmob.appsetting.NCCManagerAppActivity;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.etc.NCCEtcMainActivity;
import com.yonyou.nccmob.fragment.AppH5Fragment;
import com.yonyou.nccmob.fragment.MineFragment;
import com.yonyou.nccmob.fragment.MsgFragment;
import com.yonyou.nccmob.message.history.HistoryMessageActivity;
import com.yonyou.nccmob.util.KeyboardUtil;
import com.yonyou.nccmob.widget.EditText_Clear;
import com.yonyou.nccmob.widget.TabView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * @功能: 本应用主界面
 * @Date 10:32 PM 2020/7/16
 * @Author zhangg
 **/

public class NCCMainPagerActivity extends BaseActivity implements KeyboardUtil.OnSoftKeyboardChangeListener {
	
	
	public boolean isKeyboardVisible;// true-->弹出,false-->末弹出,可直接继承这activity,然后通过这个标志位判断软键盘是否弹出.
	// 定时循环线程池
	ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
	private List<String> mTitles = new ArrayList<>(Arrays.asList("首页", "应用", "我的"));
	private ViewPager mViewPager;
	private TabView tab_weChat;
	private TabView tab_friend;
	private TabView tab_mine;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private TextView leftHome;
	private TextView mTitle;
	private SparseArray<Fragment> mFragments = new SparseArray();
	private List<TabView> mTabs = new ArrayList<>();
	private EditText_Clear et_search;
	private LinearLayout bottomLayout; // bottomLayout
	private TextView historyMsgOrManagerAppTv; // 历史消息/管理应用btn
	/**** 键盘 start  *****/
//	private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
	
	/**** 键盘 end  *****/
	
	
	/*
	 * @功能: 轮询-解决定时处理业务问题
	 * @参数  ;
	 * @Date  2020/7/22;
	 * @Author zhangg
	 **/
	@Override
	public void initExtend() {
		super.initExtend();
		
		//LogerNcc.e("主要线程: " + Thread.currentThread().getName() + " - " + Thread.currentThread().getId());
		
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			// 是在子线程执行的
			public void run() {
				
				// 回归到主线程执行
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 主线程执行
						//LogerNcc.e("现在时间是:" + new Date().getHours() + " - " + new Date().getMinutes() + " - " + new Date().getSeconds());
					}
				});
				
			}
		}, 0, 20, TimeUnit.SECONDS);
	}
	
	@Override
	public void initLayout() {
		super.initLayout();
		setContentView(R.layout.activity_main_pager);
		EventBus.getDefault().register(this);
//		mOnGlobalLayoutListener = KeyboardUtil.observeSoftKeyboard(this, this);
	}
	
	
	private void initFragment() {
		mFragments.put(0, MsgFragment.newInstance("消息"));
		mFragments.put(1, AppH5Fragment.newInstance("应用"));
		mFragments.put(2, MineFragment.newInstance("我"));
	}
	
	@Override
	public void initView() {
		super.initView();
		
		// 左上角返回键, 隐藏掉
		ImageView back = findViewById(R.id.iv_back);
		back.setVisibility(View.GONE);
		historyMsgOrManagerAppTv = findViewById(R.id.titleBar_right);
		historyMsgOrManagerAppTv.setVisibility(View.VISIBLE);
		historyMsgOrManagerAppTv.setText("历史消息");
		historyMsgOrManagerAppTv.setTextColor(getResources().getColor(R.color.white));
		
		bottomLayout = findViewById(R.id.mainpager_bottom_layout);
		// 搜索框
		et_search = findViewById(R.id.et_search);
		et_search.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					
					// 1. 点击搜索按键后，根据输入的搜索字段进行查询
					// 注：由于此处需求会根据自身情况不同而不同，所以具体逻辑由开发者自己实现，此处仅留出接口
//					if (!(mCallBack == null)) {
//						mCallBack.SearchAciton(et_search.getText().toString());
//					}
					Toast.makeText(getApplication(), "需要搜索的是" + et_search.getText(), Toast.LENGTH_SHORT).show();
					
					
				}
				
				
				return false;
			}
		});
		
		
		initFragment();
		mTitle = findViewById(R.id.pagerTitle);
		mViewPager = findViewById(R.id.mViewPager);
		tab_weChat = findViewById(R.id.tab_weChat);
		tab_friend = findViewById(R.id.tab_friend);
		tab_mine = findViewById(R.id.tab_mine);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		leftHome = findViewById(R.id.back);
		ViewGroup.LayoutParams layoutParams = leftHome.getLayoutParams();
		
		tab_weChat.setIconAndText(R.mipmap.tab_contest_gray, R.mipmap.tab_contest_light, "消息");
		tab_friend.setIconAndText(R.mipmap.tab_counter_gray, R.mipmap.tab_counter_light, "应用");
		tab_mine.setIconAndText(R.mipmap.tab_center_gray, R.mipmap.tab_center_light, "我");
		
		mTabs.add(tab_weChat);
		mTabs.add(tab_friend);
		mTabs.add(tab_mine);
	}
	
	@Override
	public void initData() {
		super.initData();
		mTitle.setText(getResources().getString(R.string.app_name));
		// 默认页签
		changedCurrentTab(1);
		mViewPager.setCurrentItem(1, false);
	}
	
	@Override
	public void initListener() {
		super.initListener();
		
		// title 3 连击
		mTitle.setOnClickListener(
			  new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  if (AppCommonUtil.lianJi3()) {
						  Intent intent = new Intent(NCCMainPagerActivity.this, NCCEtcMainActivity.class);
						  startActivity(intent);
					  }
				  }
			  });
		leftHome.setOnClickListener(
			  new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  //左上角抽屉点击事件先注释掉
//					  mDrawerLayout.openDrawer(GravityCompat.START);
				  }
			  });
		
		for (int i = 0; i < mTabs.size(); i++) {
			TabView tabView = mTabs.get(i);
			int finalI = i;
			tabView.setOnClickListener(
				  new View.OnClickListener() {
					  @Override
					  public void onClick(View v) {
						  mViewPager.setCurrentItem(finalI, false);
						  changedCurrentTab(finalI);
					  }
				  });
		}
		
		// 历史消息/管理应用
		historyMsgOrManagerAppTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String string = historyMsgOrManagerAppTv.getText().toString();
				if (string.equals("历史消息")) {
					Intent intent = new Intent(NCCMainPagerActivity.this, HistoryMessageActivity.class);
					startActivity(intent);
				} else if (string.equals("管理应用")) {
					Intent intent = new Intent(NCCMainPagerActivity.this, NCCManagerAppActivity.class);
					startActivity(intent);
				}
			}
		});
		
		mNavigationView.setNavigationItemSelectedListener(
			  new NavigationView.OnNavigationItemSelectedListener() {
				  @Override
				  public boolean onNavigationItemSelected(MenuItem item) {
					  
					  LogerNcc.e("aaaa : " + item.toString());
					  // 白天
					  if (R.id.lightTheme == item.getItemId()) {
						  ThemeUtil.applyTheme(ThemeUtil.LIGHT_MODE);
					  } else if (R.id.blackTheme == item.getItemId()) { // 黑夜
						  ThemeUtil.applyTheme(ThemeUtil.DARK_MODE);
					  }
					  
					  // 关闭抽屉
					  mDrawerLayout.closeDrawer(mNavigationView);
					  // 在这里处理item的点击事件
					  return true;
				  }
			  });
		
		mViewPager.setOffscreenPageLimit(mTitles.size());
		mViewPager.addOnPageChangeListener(
			  new ViewPager.OnPageChangeListener() {
				  @Override
				  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					  // 左 - 右 0-1
					  
					  if (positionOffset > 0) {
						  TabView left = mTabs.get(position);
						  TabView right = mTabs.get(position + 1);
						  left.setProgress(1 - positionOffset);
						  right.setProgress(positionOffset);
					  }
				  }
				  
				  @Override
				  public void onPageSelected(int position) {
					  if (0 == position) {
						  historyMsgOrManagerAppTv.setVisibility(View.VISIBLE);
						  historyMsgOrManagerAppTv.setText("历史消息");
					  } else if (1 == position) {
						  historyMsgOrManagerAppTv.setVisibility(View.VISIBLE);
						  historyMsgOrManagerAppTv.setText("管理应用");
					  } else {
						  historyMsgOrManagerAppTv.setVisibility(View.INVISIBLE);
					  }
				  }
				  
				  @Override
				  public void onPageScrollStateChanged(int state) {
				  }
			  });
		mViewPager.setAdapter(
			  new FragmentStatePagerAdapter(getSupportFragmentManager()) {
				  @NonNull
				  @Override
				  public Fragment getItem(int position) {
					  Fragment fragment = mFragments.get(position);
					  return fragment;
				  }
				  
				  @Override
				  public int getCount() {
					  return mTitles.size();
				  }
			  });
		
		// 申请权限
		requestPermission();
	}
	
	/*
	 * @功能:权限申请
	 * @Param
	 * @return
	 * @Date 2:38 PM 2020/7/14
	 * @Author zhangg
	 **/
	public void requestPermission() {
		// 申请权限
		List<String> permission = new ArrayList<>();
		permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		permission.add(Manifest.permission.CAMERA);
		permission.add(Manifest.permission.VIBRATE);
		permission.add(Manifest.permission.CALL_PHONE);
		permission.add(Manifest.permission.SEND_SMS);
		permission.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
		permission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
		permission.add(Manifest.permission.ACCESS_WIFI_STATE);
		permission.add(Manifest.permission.ACCESS_NETWORK_STATE);
		permission.add(Manifest.permission.READ_PHONE_STATE);
		permission.add(Manifest.permission.CHANGE_WIFI_STATE);
		
		PermissionX.init(this)
			  .permissions(permission)
			  .request(
				    new RequestCallback() {
					    @Override
					    public void onResult(
							boolean allGranted, List<String> grantedList, List<String> deniedList) {
						    if (allGranted) {
//							    showMessage("good~");
						    } else {
							    String s = deniedList.toString();
							    showMessage(s + " 没有授予权限.");
						    }
					    }
				    });
	}
	
	/**
	 * 右上角的菜单
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sample_actions_menus, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	/*
	 * @功能:修改tab 文案
	 * @Param
	 * @return
	 * @Date 8:04 PM 2020/7/16
	 * @Author zhangg
	 **/
	public void changedCurrentTab(int pos) {
		for (int i = 0; i < mTabs.size(); i++) {
			TabView tabView = mTabs.get(i);
			if (i == pos) {
				tabView.setProgress(1);
			} else {
				tabView.setProgress(0);
			}
		}
	}
	
	/*
	 * @功能:接收一个消息实体
	 * @Param 消息实体vo
	 * @Date 10:39 PM 2020/7/16
	 * @Author zhangg
	 **/
	@Subscribe
	public void onEventMainThread(MessageEventVo messageEventvo) {
		LogerNcc.e("2onEvent() called with: messageEvent = [" + messageEventvo + "]");
		showMessage(messageEventvo.getMessage());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	@Override
	public void onSoftKeyBoardChange(int softKeyboardHeight, boolean visible) {
		isKeyboardVisible = visible;
		LogerNcc.e("判断软键盘是否弹出:" + visible);
		// 键盘弹起的时候隐藏掉,否则显示出来
		if (bottomLayout != null) {
//			bottomLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		boolean shutdown = scheduledThreadPool.isShutdown();
		// 如果没有shutdown就shutdown
		if (!shutdown) {
			scheduledThreadPool.shutdown();
		}
//		KeyboardUtil.removeSoftKeyboardObserver(this, mOnGlobalLayoutListener);
	}
	
	@Override
	public void onBackPressed() {
		
		// 2连击时候退出
		boolean b = AppCommonUtil.lianJi2();
		if (b) {
			super.onBackPressed();
		} else {
			showMessage("双击退出");
		}
	}
}
