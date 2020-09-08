package com.yonyou.nccmob.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;


/*
 * @功能: 自定义拦截时间的viewpager
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class ChildViewPager extends ViewPager {

    private static final String TAG = "mmmm";

    public ChildViewPager(Context context) {
        super(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int curPosition;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                curPosition = this.getCurrentItem();
                int count = this.getAdapter().getCount();
//				Log.i(TAG, "curPosition:=" + curPosition);
//                全部由孩子拦截触摸事件
                /*                getParent().requestDisallowInterceptTouchEvent(true);*/
                // 当当前页面在最后一页和第0页的时候，由父亲拦截触摸事件
                if (curPosition == count - 1 || curPosition == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {//其他情况，由孩子拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

        }
        return super.dispatchTouchEvent(ev);
    }


}
