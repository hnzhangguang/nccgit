package com.yonyou.nccmob.message.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.vo.MessageVO;
import com.yonyou.nccmob.R;

import java.util.List;


/*
 * @功能: 预警消息item适配器
 * @参数  ;
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
	
	public onItemClick itemClick;
	private List<MessageVO> mFruitList;
	
	public NotificationAdapter(List<MessageVO> fruitList) {
		mFruitList = fruitList;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_notification_item, parent, false);
		ViewHolder holder = new ViewHolder(view);
		return holder;
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		
		MessageVO item = mFruitList.get(position);
		holder.fruitName.setText(item.getSubject());
		holder.itemName33.setText(item.getContent());
		
		holder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (itemClick != null) {
					itemClick.onClickItemEvent(item);
				}
			}
		});
		
		
	}
	
	@Override
	public int getItemCount() {
		return mFruitList.size();
	}
	
	public void setItemClick(onItemClick itemClick) {
		this.itemClick = itemClick;
	}
	
	public interface onItemClick {
		void onClickItemEvent(MessageVO app);
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		TextView fruitName;
		TextView itemName33;
		View rootView;
		
		public ViewHolder(View view) {
			super(view);
			rootView = view;
			fruitName = (TextView) view.findViewById(R.id.itemName);
			itemName33 = (TextView) view.findViewById(R.id.itemName33);
		}
		
	}
	
	
}