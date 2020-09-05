package com.yonyou.nccmob.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseFragment;

public class ImageFragment extends BaseFragment {
	
	public static final String KEY = "imageId";
	ImageView mDragImageView;
	private Context mContext;
	private int mImageId = -1;
	
	public static ImageFragment newInstance(int imageId) {
		ImageFragment imageFragment = new ImageFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(KEY, imageId);
		imageFragment.setArguments(bundle);
		return imageFragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		mContext = container.getContext();
		View view = View.inflate(mContext, R.layout.fragment_image, null);
		mDragImageView = (ImageView) view.findViewById(R.id.imageView);
		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle arguments = getArguments();
		if (arguments != null) {
			mImageId = arguments.getInt(KEY, -1);
			if (mImageId != -1) {
				mDragImageView.setImageResource(mImageId);
			}
		}
	}
}
