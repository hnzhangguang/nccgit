package com.yonyou.album.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yonyou.album.plugin.utils.Bimp;
import com.yonyou.album.plugin.utils.PublicWay;
import com.yonyou.album.plugin.zoom.PhotoView;
import com.yonyou.album.plugin.zoom.PhotoViewAttacher;
import com.yonyou.album.plugin.zoom.ViewPagerFixed;
import com.yonyou.ancclibs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用于进行图片浏览时的界面
 */
public class GalleryActivity extends Activity {
	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	RelativeLayout photo_relativeLayout;
	private Intent intent;
	//顶部显示预览图片位置的textview
	private TextView positionTextView;
	//获取前一个activity传过来的position
	private int position;
	//当前的位置
	private int location = 0;
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;
	private Context mContext;
	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
		
		public void onPageSelected(int arg0) {
			location = arg0;
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		
		}
		
		public void onPageScrollStateChanged(int arg0) {
		
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_gallery);// 切屏到主界面
		PublicWay.activityList.add(this);
		mContext = this;
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		position = Integer.parseInt(intent.getStringExtra("position"));
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews(Bimp.tempSelectBitmap.get(i).getBitmap());
		}
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
		
	}
	
	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				finish();
			}
		});
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			  LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	
	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (position == 1) {
				this.finish();
				//				intent.setClass(GalleryActivity.this, AlbumActivity.class);
				//				startActivity(intent);
			} else if (position == 2) {
				this.finish();
				//				intent.setClass(GalleryActivity.this, ShowAllPhoto.class);
				//				startActivity(intent);
			}
		}
		return true;
	}
	
	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {
		
		public void onClick(View v) {
			//			intent.setClass(GalleryActivity.this, ImageFile.class);
			//			startActivity(intent);
			finish();
		}
	}
	
	class MyPageAdapter extends PagerAdapter {
		
		private ArrayList<View> listViews;
		
		private int size;
		
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}
		
		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}
		
		public int getCount() {
			return size;
		}
		
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}
		
		public void finishUpdate(View arg0) {
		}
		
		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);
				
			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}
		
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
	}
}
