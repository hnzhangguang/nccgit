package com.yonyou.album.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.yonyou.album.plugin.zoom.PhotoView;
import com.yonyou.ancclibs.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class PicturesActivity extends Activity {
	
	private ViewPager mViewPager;
	private TextView tv_page;
	private int position = 0;
	private ArrayList<String> mUrls;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pictures);
		mUrls = new ArrayList<>();
		mViewPager = (PictureViewPager) findViewById(R.id.iv_picture);
		tv_page = findViewById(R.id.tv_page);
		String current = getIntent().getStringExtra("current");
		String urls = getIntent().getStringExtra("urls");
		if (!TextUtils.isEmpty(urls)) {
			try {
				JSONArray jsonArray = new JSONArray(urls);
				for (int i = 0; i < jsonArray.length(); i++) {
					String url = jsonArray.optString(i);
					mUrls.add(url);
					if (url.equals(current)) {
						position = i;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mViewPager.setAdapter(new PicturePagerAdapter());
		mViewPager.setCurrentItem(position);
		tv_page.setText((position + 1) + "/" + mUrls.size());
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			
			}
			
			@Override
			public void onPageSelected(int i) {
				tv_page.setText((i + 1) + "/" + mUrls.size());
			}
			
			@Override
			public void onPageScrollStateChanged(int i) {
			
			}
		});
	}
	
	class PicturePagerAdapter extends PagerAdapter {
		//这里暂时写死了，实际情况中要从服务端获取图片地址结合，传过来
		//        private static final String[] url = {"https://wx3.sinaimg
		//        .cn/mw690/70396e5agy1g0leax4owvj211418gk2j.jpg",
		//                "https://wx4.sinaimg.cn/mw690/70396e5agy1g0leay2ymtj211418gk11.jpg",
		//                "https://wx2.sinaimg.cn/mw690/70396e5agy1g0leayhrhoj211418gq9q.jpg",
		//                "https://wx3.sinaimg.cn/mw690/70396e5agy1g0leazeikxj211418gqdc.jpg"};
		
		@Override
		public int getCount() {
			return mUrls.size();
		}
		
		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			Glide.with(container.getContext()).load(mUrls.get(position)).into(photoView);
			
			container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
				  ViewGroup.LayoutParams.MATCH_PARENT);
			
			return photoView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
	}
}