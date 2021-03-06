package com.yonyou.nccmob.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

/**
 * 判断软键盘是否弹出
 * 1.activity-->android:windowSoftInputMode="adjustResize|stateHidden"
 * 2.如果高版本出现输入框焦点问题,可由listView改为recycleView
 * 3.监听注册监听,同时需要取消监听本listener
 */
public class KeyboardUtil {
	/**
	 * 监听软键盘高度和状态
	 */
	public static ViewTreeObserver.OnGlobalLayoutListener observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
		final View decorView = activity.getWindow().getDecorView();
		ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			int previousKeyboardHeight = -1;
			Rect rect = new Rect();
			boolean lastVisibleState = false;
			
			@Override
			public void onGlobalLayout() {
				rect.setEmpty();
				decorView.getWindowVisibleDisplayFrame(rect);
				int displayHeight = rect.bottom - rect.top;
				//考虑上状态栏的高度
				int height = decorView.getHeight() - rect.top;
				int keyboardHeight = height - displayHeight;
				if (previousKeyboardHeight != keyboardHeight) {
					boolean hide = (double) displayHeight / height > 0.8;
					if (hide != lastVisibleState) {
						listener.onSoftKeyBoardChange(keyboardHeight, !hide);
						lastVisibleState = hide;
					}
				}
				previousKeyboardHeight = height;
			}
		};
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
		return onGlobalLayoutListener;
	}
	
	public static void removeSoftKeyboardObserver(Activity activity, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (listener == null) return;
		final View decorView = activity.getWindow().getDecorView();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			decorView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		} else {
			decorView.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		}
	}
	
	/*
	 * @功能: 判断软键盘是否弹出
	 * @Date  2020/8/10;
	 * @Author zhangg
	 **/
	public static boolean isSHowKeyboard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
		if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
			imm.showSoftInput(v, 0);
			return true;
			//软键盘已弹出
		} else {
			return false;
			//软键盘未弹出
		}
	}
	
	
	public interface OnSoftKeyboardChangeListener {
		void onSoftKeyBoardChange(int softKeyboardHeight, boolean visible);
	}
	
	
}