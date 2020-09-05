package com.yonyou.nccmob.message.history;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/*
 * @功能: 历史列表适配器
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class HistoryFragmentPagerAdapter extends FragmentPagerAdapter {
	
	
	final int PAGE_COUNT = 3;
	private String tabTitles[] = new String[]{"代办", "预警", "通知"};
	private Context context;
	
	public HistoryFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}
	
	@Override
	public Fragment getItem(int position) {
		if (0 == position) {
			return NoTODOFragment.newInstance(position + 1);
		} else if (1 == position) {
			return AlterFragment.newInstance(position + 1);
		} else if (2 == position) {
			return NotificationFragment.newInstance(position + 1);
		}
		return null;
	}
	
	@Override
	public int getCount() {
		return PAGE_COUNT;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return tabTitles[position];
	}
	
	
}
