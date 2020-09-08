package com.yonyou.nccmob.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;


/*
 * @功能: 自己算高度的viewpager
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class ChildHeightViewPager extends ViewPager {

    private static final String TAG = "mmmm";

    public ChildHeightViewPager(Context context) {
        super(context);
    }

    public ChildHeightViewPager(Context context, AttributeSet attrs) {
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


    /*
     * @功能: 根据child计算viewpager高度
     * @参数  ;
     * @Date  2020/8/11;
     * @Author zhangg
     **/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) //采用最大的view的高度。
                height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
