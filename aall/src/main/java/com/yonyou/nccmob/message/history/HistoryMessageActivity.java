package com.yonyou.nccmob.message.history;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;


/*
 * @功能: 历史消息列表界面
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class HistoryMessageActivity extends BaseActivity {
	
	
	private TextView pagerTitle;
	private HistoryFragmentPagerAdapter pagerAdapter;
	private ViewPager viewPager;
	private TabLayout tabLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_message);
		
		
		pagerTitle = findViewById(R.id.pagerTitle);
		pagerTitle.setText("历史消息");
		
		View back = findViewById(R.id.iv_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		pagerAdapter = new HistoryFragmentPagerAdapter(getSupportFragmentManager(), this);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);
		
		
		tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		
		// ViewPager 和 TabLayout 相互关联
		tabLayout.setupWithViewPager(viewPager);
		
		// tabMode有两种属性：fixed和 scrollable；fixed：指的是固定tab；scrollable指的是tab可滑动。
		//tabGravity有两种属性：center和fill；center指的是居中显示，fill指的是沾满全屏。
		tabLayout.setTabMode(TabLayout.MODE_FIXED);
//		tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
	}
}