package com.yonyou.album.plugin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yonyou.album.plugin.model.YYPhotoAlbum;
import com.yonyou.ancclibs.R;

import java.util.List;


public class PhotoAibumAdapter extends BaseAdapter {
	
	/**
	 * 相册列表
	 */
	private List<YYPhotoAlbum> aibumList;
	/**
	 * context
	 */
	private Context context;
	/**
	 * viewHolder
	 */
	private ViewHolder holder;
	
	public PhotoAibumAdapter(List<YYPhotoAlbum> list, Context context) {
		this.aibumList = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return aibumList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return aibumList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.album_list_item, null);
			holder = new ViewHolder();
			holder.albumImage = convertView.findViewById(R.id.album_listitem_image);
			holder.albumText = convertView.findViewById(R.id.album_listitem_name);
			holder.selectedImage = convertView.findViewById(R.id.album_listitem_cur);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		/** 通过ID 获取缩略图*/
		if (aibumList.get(position).isTotal()) {
			holder.albumImage.setImageResource(aibumList.get(position).getBitmap());
		} else {
			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
				  aibumList.get(position).getBitmap(), Thumbnails.MICRO_KIND, null);
			
			if (bitmap == null) {
				holder.albumImage.setImageResource(R.drawable.album_no_pictures);
			} else {
				holder.albumImage.setImageBitmap(bitmap);
			}
		}
		
		holder.albumText.setText(aibumList.get(position).getName() + " ( " + aibumList.get(position).count() + " )");
		holder.selectedImage.setVisibility(aibumList.get(position).isCurrentChoice() ? View.VISIBLE : View.GONE);
		
		return convertView;
	}
	
	static class ViewHolder {
		
		ImageView albumImage;
		TextView albumText;
		ImageView selectedImage;
	}
	
}
