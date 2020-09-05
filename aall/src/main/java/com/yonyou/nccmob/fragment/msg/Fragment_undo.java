package com.yonyou.nccmob.fragment.msg;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseFragment;
import com.yonyou.nccmob.fragment.adapter_msg.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


/*
 * @功能: 代办消息fragment
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class Fragment_undo extends BaseFragment {
	
	RecyclerView recyclerView;
	List<String> datas;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment, container, false);
		recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
		initData();
		RecyclerViewAdapter adapter = new RecyclerViewAdapter(datas);
		recyclerView.setAdapter(adapter);
		
		SpacesItemDecoration decoration = new SpacesItemDecoration(16);
		recyclerView.addItemDecoration(decoration);
		
		adapter.setOnItemClickListener(new RecyclerViewAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View v) {
				Snackbar.make(v, "我是代办 " + v.getTag(), Snackbar.LENGTH_LONG).show();
			}
		});
		return view;
	}
	
	public void initData() {
		datas = new ArrayList<String>();
		for (int i = 0; i < 17; i++) {
			datas.add("代办消息 " + i);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
		private int space;
		
		public SpacesItemDecoration(int space) {
			this.space = space;
		}
		
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			outRect.left = space;
			outRect.right = space;
			outRect.bottom = space;
			if (parent.getChildAdapterPosition(view) == 0) {
				outRect.top = space;
			}
		}
	}
}
