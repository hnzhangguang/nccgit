package com.yonyou.nccmob.fragment.adapter_msg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.nccmob.R;

import java.util.List;


/**
 * Created by finch on 2016/7/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
	
	private List<String> datas;
	private OnRecyclerViewItemClickListener mOnItemClickListener = null;
	
	public RecyclerViewAdapter(List<String> datas) {
		this.datas = datas;
	}
	
	public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
		mOnItemClickListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnItemClickListener.onItemClick(v);
			}
		});
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.item.setTag(position);
		holder.tv.setText(datas.get(position));
	}
	
	@Override
	public int getItemCount() {
		return datas.size();
	}
	
	public void addItem(String s, int position) {
		datas.add(position, s);
		notifyItemInserted(position);
	}
	
	public void removeItem(final int position) {
		datas.remove(position);
		notifyItemRemoved(position);
	}
	
	public static interface OnRecyclerViewItemClickListener {
		void onItemClick(View view);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		public View item;
		public TextView tv;
		
		public ViewHolder(View view) {
			super(view);
			item = view;
			tv = (TextView) view.findViewById(R.id.text);
		}
	}
}
