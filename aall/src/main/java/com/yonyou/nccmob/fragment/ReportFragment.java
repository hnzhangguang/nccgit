package com.yonyou.nccmob.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseFragment;

public class ReportFragment extends BaseFragment {
	
	private static final String BUNDLE_KEY_TITLE = "bundle_key_title";
	private View mRootView;
	
	public static ReportFragment newInstance(String title) {
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_KEY_TITLE, title);
		ReportFragment fragment = new ReportFragment();
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
			Log.e("666", "ReportFragment");
			mRootView = inflater.inflate(R.layout.report_fragment, container, false);
		}
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		return mRootView;
	}
}
