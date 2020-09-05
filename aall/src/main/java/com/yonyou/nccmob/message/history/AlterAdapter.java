package com.yonyou.nccmob.message.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.vo.MessageVO;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import java.util.List;


/*
 * @功能: 预警消息item适配器
 * @参数  ;
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class AlterAdapter extends RecyclerView.Adapter<AlterAdapter.ViewHolder> {

    public onItemClick itemClick;
    private List<MessageVO> mFruitList;
    private BaseActivity mActivity;

    public AlterAdapter(BaseActivity mActivity, List<MessageVO> fruitList) {
        mFruitList = fruitList;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_alter_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MessageVO item = mFruitList.get(position);
        holder.fruitName.setText(item.getSubject());
        holder.fruitName22.setText(item.getContent());
        holder.fruitName33.setText(item.getSendtime());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClick != null) {
                    itemClick.onClickItemEvent(item);
                }
            }
        });
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mActivity.showMessage("你长按了我,你真坏~");

                return false;
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
        TextView fruitName22;
        TextView fruitName33;
        View rootView;

        public ViewHolder(View view) {
            super(view);
            rootView = view;
            fruitName = (TextView) view.findViewById(R.id.itemName);
            fruitName22 = (TextView) view.findViewById(R.id.itemName22);
            fruitName33 = (TextView) view.findViewById(R.id.itemName33);
        }

    }


}