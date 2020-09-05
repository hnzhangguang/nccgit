package com.yonyou.nccmob.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yonyou.common.utils.utils.CheckUtil;


/*
 * @功能: 基类
 * @Date  2020/8/14;
 * @Author zhangg
 **/
public class BaseFragment extends Fragment {
	
	
	/*
	 * @功能: 判空工具method
	 * @参数  ;
	 * @Date  2020/8/25;
	 * @Author zhangg
	 **/
	public static boolean isNull(String string) {
		return CheckUtil.isNull(string);
	}
	
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w("mmmm", getContext().getClass().toString());
	}
}
