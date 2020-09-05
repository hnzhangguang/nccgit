package com.yonyou.nccmob.message.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yonyou.common.vo.MessageVO;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.base.BaseFragment;
import com.yonyou.nccmob.listener.recycleview.EndLessOnScrollListener;

import java.util.ArrayList;


/*
 * @功能: 通知fragment
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class NotificationFragment extends BaseFragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private ArrayList<MessageVO> notificationList = new ArrayList<>();
    private BaseActivity mActivity;
    private SwipeRefreshLayout swipview;

    public static NotificationFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NotificationFragment pageFragment = new NotificationFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mActivity = (BaseActivity) getActivity();
    }

    private void reloadData() {
        //此处掠过数据加载逻辑
        mActivity.showMessage("reloadData");
        //加载完成后改变状态
        swipview.setRefreshing(false);
    }

    private void loadMore() {
        mActivity.showMessage("loadMore");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_tablayout, container, false);

        RecyclerView recyclerViewNotification = view.findViewById(R.id.recycler_notification);


        LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerViewNotification.setLayoutManager(layoutManager);
        NotificationAdapter adapter = new NotificationAdapter(notificationList);
        adapter.setItemClick(new NotificationAdapter.onItemClick() {
            @Override
            public void onClickItemEvent(MessageVO app) {
                mActivity.showMessage(app);
            }
        });
        recyclerViewNotification.setAdapter(adapter);


        //  下拉刷新
        swipview = (SwipeRefreshLayout) view.findViewById(R.id.swiper);
        swipview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });
        // 上啦加载更多
        recyclerViewNotification.addOnScrollListener(new EndLessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMore();
            }
        });


        initnotification();


        return view;
    }

    private void initnotification() {


        for (int i = 0; i < 2; i++) {
            MessageVO apple = new MessageVO("通知通知通知", "notification Apple", "notification Apple", "notification Apple", R.drawable.ic_launcher_foreground);
            notificationList.add(apple);
            MessageVO banana = new MessageVO("通知通知通知", "notification Banana", "notification Banana", "notification Banana", R.drawable.ic_launcher_foreground);
            notificationList.add(banana);
            MessageVO orange = new MessageVO("通知通知通知 Orange", "notification Orange", "notification Orange", "notification Orange", R.drawable.ic_launcher_foreground);
            notificationList.add(orange);
            MessageVO watermelon = new MessageVO("通知通知通知 Watermelon", "notification Watermelon", "notification Watermelon", "notification Watermelon", R.drawable.ic_launcher_foreground);
            notificationList.add(watermelon);
            MessageVO pear = new MessageVO("通知通知通知 Pear", "notification Pear", "notification Pear", "notification Pear", R.drawable.ic_launcher_foreground);
            notificationList.add(pear);
            MessageVO grape = new MessageVO("通知通知通知 Grape", "notification Grape", "notification Grape", "notification Grape", R.drawable.ic_launcher_foreground);
            notificationList.add(grape);
            MessageVO pineapple = new MessageVO("通知通知通知 Pineapple", "notification Pineapple", "notification Pineapple", "notification Pineapple", R.drawable.ic_launcher_foreground);
            notificationList.add(pineapple);
            MessageVO strawberry = new MessageVO("通知通知通知 Strawberry", "2018年9月6日 - 分类专栏: Android开发 Loading 是很普遍的需求,比如请求的时候需要显示 Loaing ,而一般的实现方式是在布局xml里添加一个 ProgressBar,但是这样写就有", "notification Strawberry", "notification Strawberry", R.drawable.ic_launcher_foreground);
            notificationList.add(strawberry);
            MessageVO cherry = new MessageVO("通知通知通知 Cherry", "notification Cherry", "notification Cherry", "notification Cherry", R.drawable.ic_launcher_foreground);
            notificationList.add(cherry);
            MessageVO mango = new MessageVO("通知通知通知 Mango", "notification Mango", "notification Mango", "notification Mango", R.drawable.ic_launcher_foreground);
            notificationList.add(mango);

        }


    }

}
