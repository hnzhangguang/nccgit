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
 * @功能: 预警fragment
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class AlterFragment extends BaseFragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    RecyclerView recyclerViewAlter;
    BaseActivity mActivity;
    private int mPage;
    private SwipeRefreshLayout swipview;
    private ArrayList<MessageVO> alterList = new ArrayList<>();

    public static AlterFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AlterFragment pageFragment = new AlterFragment();
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
        View view = inflater.inflate(R.layout.fragment_alter_tablayout, container, false);

        RecyclerView recyclerViewAlter = view.findViewById(R.id.recycler_alter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerViewAlter.setLayoutManager(layoutManager);
        AlterAdapter adapter = new AlterAdapter(mActivity, alterList);
        adapter.setItemClick(new AlterAdapter.onItemClick() {
            @Override
            public void onClickItemEvent(MessageVO app) {
                mActivity.showMessage(app);
            }
        });
        recyclerViewAlter.setAdapter(adapter);
        //  下拉刷新
        swipview = (SwipeRefreshLayout) view.findViewById(R.id.swiper);
        swipview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });
        // 上啦加载更多
        recyclerViewAlter.addOnScrollListener(new EndLessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMore();
            }
        });

        initFruits();


        return view;
    }


    private void initFruits() {
        for (int i = 0; i < 2; i++) {
            MessageVO apple = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Apple22", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(apple);
            MessageVO banana = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Banana22", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(banana);
            MessageVO orange = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Orange", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(orange);
            MessageVO watermelon = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Watermelon", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(watermelon);
            MessageVO pear = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Pear", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(pear);
            MessageVO grape = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Grape", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(grape);
            MessageVO pineapple = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "2020-12-12", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(pineapple);
            MessageVO strawberry = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "2020-12-12", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(strawberry);
            MessageVO cherry = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Cherry", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(cherry);
            MessageVO mango = new MessageVO("预警预警预警", "预警内容预警内容预警内容", "alter Mango", "2020-12-12", R.drawable.ic_launcher_foreground);
            alterList.add(mango);

        }
    }


}
