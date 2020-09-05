package com.yonyou.album.plugin.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.yonyou.album.plugin.model.ImageItem;
import com.yonyou.album.plugin.utils.BitmapCache;
import com.yonyou.ancclibs.R;
import com.yonyou.common.utils.MTLLog;

import java.util.ArrayList;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 */
public class AlbumGridViewAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	BitmapCache cache;
	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
		                      Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					MTLLog.e(TAG, "callback, bmp not match");
				}
			} else {
				MTLLog.e(TAG, "callback, bmp null");
			}
		}
	};
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private ArrayList<ImageItem> selectedDataList;
	private DisplayMetrics dm;
	private OnItemClickListener mOnItemClickListener;
	
	public AlbumGridViewAdapter(Context c, ArrayList<ImageItem> dataList,
	                            ArrayList<ImageItem> selectedDataList) {
		mContext = c;
		cache = new BitmapCache();
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
			  .getMetrics(dm);
		
	}
	
	public int getCount() {
		return dataList.size();
	}
	
	public Object getItem(int position) {
		return dataList.get(position);
	}
	
	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		int itemWidth;
		if (convertView == null) {
			
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.album_grid_item, parent, false);
			// 计算width
			itemWidth = (mContext.getResources().getDisplayMetrics().widthPixels - 4 * mContext.getResources().getDimensionPixelSize(
				  R.dimen.album_photo_spacing)) / 3;
			// width = height
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(itemWidth, itemWidth);
			convertView.setLayoutParams(param);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
			viewHolder.choosetoggle = (Button) convertView.findViewById(R.id.choosedbt);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dipToPx(65)); 
//			lp.setMargins(50, 0, 50,0); 
//			viewHolder.imageView.setLayoutParams(lp);
			convertView.setTag(R.string.album_viewholder, viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.string.album_viewholder);
			itemWidth = convertView.getMeasuredHeight();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position).imagePath;
		else
			path = "camera_default";
		if (path.contains("camera_default")) {
			viewHolder.imageView.setImageResource(R.drawable.album_no_pictures);
		} else {
//			ImageManager2.from(mContext).displayImage(viewHolder.imageView,
//					path, Res.getDrawableID("plugin_camera_camera_default"), 100, 100);
			ImageItem item = dataList.get(position);
//			viewHolder.imageView.setTag(item.imagePath);
			viewHolder.imageView.setTag(R.id.image_loader_uri, item.imagePath);
			Glide.with(mContext)
				  .load(item.imagePath)
				  .centerCrop()
//				  .crossFade()
				  .placeholder(R.drawable.album_no_pictures)
				  .into(viewHolder.imageView);
//			cache.displayBmp(viewHolder.imageView, item.thumbnailPath, item.imagePath, callback);
		}
		viewHolder.toggleButton.setTag(position);
		viewHolder.choosetoggle.setTag(position);
		viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
		if (selectedDataList.contains(dataList.get(position))) {
			viewHolder.toggleButton.setChecked(true);
			viewHolder.choosetoggle.setVisibility(View.VISIBLE);
		} else {
			viewHolder.toggleButton.setChecked(false);
			viewHolder.choosetoggle.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}
	
	
	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position,
		                        boolean isChecked, Button chooseBt);
	}
	
	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imageView;
		public ToggleButton toggleButton;
		public Button choosetoggle;
		public TextView textView;
	}
	
	private class ToggleClickListener implements OnClickListener {
		Button chooseBt;
		
		public ToggleClickListener(Button choosebt) {
			this.chooseBt = choosebt;
		}
		
		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (dataList != null && mOnItemClickListener != null
					  && position < dataList.size()) {
					mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(), chooseBt);
				}
			}
		}
	}
	
}
