package com.yonyou.nccmob.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yonyou.nccmob.R;

/**
 * 本类功能:
 *
 * @author zhangg
 */
public class TabFragment extends Fragment {

  private static final String BUNDLE_KEY_TITLE = "bundle_key_title";
  // 控件实例
  private TextView mTvTitle;
  // 控件实例要显示的文本
  private String mTitle;

  /*
   * @功能:定义接口供activity使用
   * @Param
   * @return
   * @Date 8:06 PM 2020/7/16
   * @Author zhangg
   **/
  public static interface OnTitleClickListener {
    void onClick(String title);
  }

  public OnTitleClickListener mListener;

  public void setOnTitleClickListener(OnTitleClickListener listener) {
    mListener = listener;
  }

  public static TabFragment newInstance(String title) {
    Bundle bundle = new Bundle();
    bundle.putString(BUNDLE_KEY_TITLE, title);
    TabFragment fragment = new TabFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 从bundle中拿数据
    Bundle arguments = getArguments();
    if (arguments != null) {
      mTitle = arguments.getString(BUNDLE_KEY_TITLE, "");
    }
  }

  /*
   * @功能:第一步加载布局文件
   * @Param
   * @return
   * @Date 6:09 PM 2020/7/16
   * @Author zhangg
   **/
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_tab, container, false);
  }

  /*
   * @功能: 第二步,根据加载的布局文件,获取某个控件实例
   * @Param
   * @return
   * @Date 6:09 PM 2020/7/16
   * @Author zhangg
   **/
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mTvTitle = view.findViewById(R.id.mTvTitle);
    mTvTitle.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mListener != null) {
              mListener.onClick("微信changed");
            }
          }
        });
    mTvTitle.setText(mTitle);
  }

  /*
   * @功能: 向外暴露可以修改title的方法
   * @Param
   * @return
   * @Date 7:53 PM 2020/7/16
   * @Author zhangg
   **/
  public void changeTitle(String title) {
    if (isAdded()) {
      mTvTitle.setText(title);
    }
  }
}
