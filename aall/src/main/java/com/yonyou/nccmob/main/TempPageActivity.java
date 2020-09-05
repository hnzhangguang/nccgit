package com.yonyou.nccmob.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTabHost;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.AppCommonUtil;
import com.yonyou.common.utils.utils.ThemeUtil;
import com.yonyou.common.vo.MessageEventVo;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.etc.NCCEtcMainActivity;
import com.yonyou.nccmob.fragment.AppH5Fragment;
import com.yonyou.nccmob.fragment.MineFragment;
import com.yonyou.nccmob.fragment.MsgFragment;
import com.yonyou.nccmob.fragment.ReportFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class TempPageActivity extends BaseActivity {
	
	private FragmentTabHost mTabHost;
	private ViewPager mViewPager;
	private List<Fragment> mFragmentList;
	private DrawerLayout mDrawerLayout;
	private NavigationView navigationView;
	private TextView back;
	private TextView pagerTitle; // 页面标题
	private Class mClass[] = {
		  AppH5Fragment.class, MsgFragment.class, ReportFragment.class, MineFragment.class
	};
	private Fragment mFragment[] = {
		  new AppH5Fragment(), new MsgFragment(), new ReportFragment(), new MineFragment()
	};
	private String mTitles[] = {"消息", "应用", "报表", "我的"};
	private int mImages[] = {
		  R.drawable.tab_message, R.drawable.tab_home, R.drawable.tab_report, R.drawable.tab_mine
	};
	
	@Override
	public void initLayout() {
		super.initLayout();
		setContentView(R.layout.activity_temp_page);
		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	// 声明一个订阅方法，用于接收事件
	//    @Subscribe
	//    public void onEvent(MessageEventVo messageEventvo) {
	//        LogerNcc.e("onEvent() called with: messageEvent = [" + messageEventvo +
	//        "]");
	//        showMessage(messageEventvo.getMessage());
	//    }
	
	/**
	 * 接收一个消息实体
	 *
	 * @param messageEventvo
	 */
	@Subscribe
	public void onEventMainThread(MessageEventVo messageEventvo) {
		LogerNcc.e("2onEvent() called with: messageEvent = [" + messageEventvo + "]");
		showMessage(messageEventvo.getMessage());
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch (AppCompatDelegate.getDefaultNightMode()) {
			case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
				menu.findItem(R.id.menu_night_mode_system).setChecked(true);
				break;
			case AppCompatDelegate.MODE_NIGHT_AUTO:
				menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
				break;
			case AppCompatDelegate.MODE_NIGHT_YES:
				menu.findItem(R.id.menu_night_mode_night).setChecked(true);
				break;
			case AppCompatDelegate.MODE_NIGHT_NO:
				menu.findItem(R.id.menu_night_mode_day).setChecked(true);
				break;
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		} else if (item.getItemId() == R.id.menu_night_mode_system) {
			setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		} else if (item.getItemId() == R.id.menu_night_mode_day) {
			setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		} else if (item.getItemId() == R.id.menu_night_mode_night) {
			setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else if (item.getItemId() == R.id.menu_night_mode_auto) {
			//            setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
		AppCompatDelegate.setDefaultNightMode(nightMode);
		
		if (Build.VERSION.SDK_INT >= 11) {
			recreate();
		}
	}
	
	@SuppressLint("NewApi")
	public void initView() {
		
		back = findViewById(R.id.back);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		
		pagerTitle = findViewById(R.id.pagerTitle);
		pagerTitle.setText("NCC移动");
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		
		mFragmentList = new ArrayList<Fragment>();
		
		mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
		mTabHost.getTabWidget().setDividerDrawable(null);
		
		for (int i = 0; i < mFragment.length; i++) {
			TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
			mTabHost.addTab(tabSpec, mClass[i], null);
			mFragmentList.add(mFragment[i]);
			//            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
			
		}
		
		mViewPager.setAdapter(
			  new FragmentPagerAdapter(getSupportFragmentManager()) {
				  @Override
				  public Fragment getItem(int position) {
					  return mFragmentList.get(position);
				  }
				  
				  @Override
				  public int getCount() {
					  return mFragmentList.size();
				  }
			  });
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// 默认页签(1:应用)
		mTabHost.setCurrentTab(1);
		
		// 权限申请
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
		//    permission.add(Manifest.permission.CHANGE_CONFIGURATION);
		
		PermissionX.init(this)
			  .permissions(permission)
			  .request(
				    new RequestCallback() {
					    @Override
					    public void onResult(
							boolean allGranted, List<String> grantedList, List<String> deniedList) {
						    if (allGranted) {
							    showMessage("good~");
						    } else {
							    String s = deniedList.toString();
							    showMessage(s + " 没有授予权限.");
						    }
					    }
				    });
	}
	
	private View getTabView(int index) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
		
		ImageView image = (ImageView) view.findViewById(R.id.image);
		TextView title = (TextView) view.findViewById(R.id.title);
		
		image.setImageResource(mImages[index]);
		title.setText(mTitles[index]);
		
		return view;
	}
	
	@Override
	public void initListener() {
		
		navigationView.setNavigationItemSelectedListener(
			  new NavigationView.OnNavigationItemSelectedListener() {
				  @Override
				  public boolean onNavigationItemSelected(MenuItem item) {
					  
					  if (item.getItemId() == R.id.lightTheme) {
						  String string =
							    UserUtil.getValueByKey(ThemeUtil.CURRENT_MODE, ThemeUtil.DEFAULT_MODE);
						  if (!string.equals(ThemeUtil.LIGHT_MODE)) {
							  ThemeUtil.applyTheme(ThemeUtil.LIGHT_MODE);
						  }
						  
					  } else if (item.getItemId() == R.id.blackTheme) {
						  
						  String string =
							    UserUtil.getValueByKey(ThemeUtil.CURRENT_MODE, ThemeUtil.DEFAULT_MODE);
						  if (!string.equals(ThemeUtil.DARK_MODE)) {
							  ThemeUtil.applyTheme(ThemeUtil.DARK_MODE);
						  }
					  }
					  // 关闭抽屉
					  mDrawerLayout.closeDrawer(navigationView);
					  // 在这里处理item的点击事件
					  return true;
				  }
			  });
		
		pagerTitle.setOnClickListener(
			  new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  boolean b = AppCommonUtil.lianJi3();
					  if (b) {
						  Intent intent = new Intent(TempPageActivity.this, NCCEtcMainActivity.class);
						  startActivity(intent);
					  }
				  }
			  });
		back.setOnClickListener(
			  new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  mDrawerLayout.openDrawer(GravityCompat.START);
				  }
			  });
		mTabHost.setOnTabChangedListener(
			  new TabHost.OnTabChangeListener() {
				  @Override
				  public void onTabChanged(String tabId) {
					  mViewPager.setCurrentItem(mTabHost.getCurrentTab());
				  }
			  });
		
		mViewPager.addOnPageChangeListener(
			  new ViewPager.OnPageChangeListener() {
				  @Override
				  public void onPageScrolled(
					    int position, float positionOffset, int positionOffsetPixels) {
				  }
				  
				  @Override
				  public void onPageSelected(int position) {
					  mTabHost.setCurrentTab(position);
				  }
				  
				  @Override
				  public void onPageScrollStateChanged(int state) {
				  }
			  });
	}
	
	/**
	 * 申请权限回调
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(
		  int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
