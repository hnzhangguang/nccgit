package com.yonyou.nccmob.fragment.adapter_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.nccmob.R;

import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter {
	
	private static final int type_item = 1;
	private static final int type_group = 2;
	public static boolean isEdit = false;  // 是否是编辑态,默认false
	public List<App> list = new ArrayList<>();
	public onItemClick itemClick;
	private Context context;
	private OnItemDeleteListener itemDeleteListener;
	
	public AppsAdapter(Context context) {
		this.context = context;
	}
	
	public void setItemClickListener(OnItemDeleteListener itemClickListener) {
		this.itemDeleteListener = itemClickListener;
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == type_group) {
			return initGroup(parent);
		}
		return initItem(parent);
		
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		displayItem(holder, position);
	}
	
	@Override
	public int getItemViewType(int position) {
		App app = list.get(position);
		return app.isCat() ? type_group : type_item;
	}
	
	@Override
	public int getItemCount() {
		return list.size();
	}
	
	private void displayItem(RecyclerView.ViewHolder holder, final int position) {
		final App model = list.get(position);
		if (getItemViewType(position) == type_group) {
			ItemGroup itemGroup = (ItemGroup) holder;
			itemGroup.tvGroupName.setText(model.getShortName());
		} else if (getItemViewType(position) == type_item) {
			ItemHolder itemHolder = (ItemHolder) holder;
			if (model.getShortName() != null) itemHolder.tvName.setText(model.getShortName());
			
			if (isEdit) {
				itemHolder.imgOperate.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (itemDeleteListener != null) {
							itemDeleteListener.clickItem(model, position);
						}
					}
				});
				itemHolder.imgOperate.setVisibility(View.VISIBLE);
			} else {
				itemHolder.imgOperate.setVisibility(View.GONE);
			}
			
			
			// 监听点击事件
			itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (itemClick != null) {
						itemClick.onClickItemEvent(model);
					}
				}
			});
			
		}
		
	}
	
	public void setItemClick(onItemClick itemClick) {
		this.itemClick = itemClick;
	}
	
	private ItemHolder initItem(ViewGroup parent) {
		View inflate = LayoutInflater.from(context).inflate(R.layout.apph5_grid_item, parent, false);
		return new ItemHolder(inflate);
	}
	
	private ItemGroup initGroup(ViewGroup parent) {
		View inflate = LayoutInflater.from(context).inflate(R.layout.apph5_grid_group, parent, false);
		return new ItemGroup(inflate);
	}
	
	public interface onItemClick {
		void onClickItemEvent(App app);
	}
	
	
	public interface OnItemDeleteListener {
		void clickItem(App model, int position);
	}
	
	class ItemHolder extends RecyclerView.ViewHolder {
		
		private ImageView imgIcon, imgOperate;
		private TextView tvName;
		private View rootView;
		
		public ItemHolder(@NonNull View itemView) {
			super(itemView);
			rootView = itemView;
			imgIcon = itemView.findViewById(R.id.img_icon);
			imgOperate = itemView.findViewById(R.id.img_operate);
			tvName = itemView.findViewById(R.id.tv_name);
		}
	}
	
	class ItemGroup extends RecyclerView.ViewHolder {
		
		private TextView tvGroupName;
		
		public ItemGroup(@NonNull View itemView) {
			super(itemView);
			
			tvGroupName = itemView.findViewById(R.id.tv_name);
		}
	}
	
}
