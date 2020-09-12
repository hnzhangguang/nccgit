package com.yonyou.nccmob.message.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.constant.ConstantUrl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.user.NccMsgUtil;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.JsonUtil;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.MessageVO;
import com.yonyou.common.vo.NCCUserVo;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.base.BaseFragment;
import com.yonyou.nccmob.listener.recycleview.EndLessOnScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * @功能: 代办fragment
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class NoTODOFragment extends BaseFragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private RecyclerView recyclerViewTodo;
    private ArrayList<MessageVO> todoList = new ArrayList<>();
    private BaseActivity mActivity;
    private SwipeRefreshLayout swipview;

    public static NoTODOFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NoTODOFragment pageFragment = new NoTODOFragment();
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

    //每次上拉加载的时候，给RecyclerView的后面添加了10条数据数据
    private void loadMoreData() {
        for (int i = 0; i < 10; i++) {
//            mData.add("嘿，我是“上拉加载”生出来的"+i);
//            mAdapter.notifyDataSetChanged();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notodo_tablayout, container, false);
//		TextView textView = (TextView) view;
//		textView.setText("Fragment #" + mPage);

        recyclerViewTodo = view.findViewById(R.id.recycler_notodo);


        LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerViewTodo.setLayoutManager(layoutManager);
        NoTodoAdapter adapter = new NoTodoAdapter(todoList);
        adapter.setItemClick(new NoTodoAdapter.onItemClick() {
            @Override
            public void onClickItemEvent(MessageVO app) {
                LogerNcc.e(JsonUtil.toJson(app));
                mActivity.showMessage(app.getSubject());
                String content = app.getContent();
//                LogerNcc.e(content);
            }
        });
        recyclerViewTodo.setAdapter(adapter);


        //  下拉刷新
        swipview = (SwipeRefreshLayout) view.findViewById(R.id.swiper);
        swipview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });

        // 上啦加载更多
        recyclerViewTodo.addOnScrollListener(new EndLessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMore();
            }
        });


        initFruits();

        return view;
    }


    /*
     * @功能: 获取数据列表
     * @参数: todoList
     * @Date  2020/8/31 4:36 PM
     * @Author zhangg
     **/
    private void getData() throws JSONException {
        NCCUserVo currentUserVo = UserUtil.getCurrentUserVo();
        String phone = currentUserVo.getPhone();
        // 获取代办消息数据
        JSONObject paramJson = new JSONObject();
        paramJson.put(Constant.msgType, Constant.todo_msg).put("mobile", phone); // 手机号必填
        NetUtil.callAction(getActivity(), ConstantUrl.requestTodoMsgUrl, paramJson, new HttpCallBack() {
            @Override
            public void onFailure(JSONObject error) {

            }

            @Override
            public void onResponse(JSONObject successJson) {

            }
        });
    }


    private void initFruits() {

        try {
            String data = NccMsgUtil.getData();
            NccMsgUtil.updateAllMessageDB(data);// 更新数据
            List<MessageVO> messageVosByType = NccMsgUtil.getMessageVosByTypeFromDB(Constant.todo_msg);
            todoList.clear();
            todoList.addAll(messageVosByType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
