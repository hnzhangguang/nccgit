package com.yonyou.nccmob.message.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.vo.MessageVO;
import com.yonyou.nccmob.R;

import java.util.List;


/*
 * @功能: 代办item适配器
 * @参数  ;
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class NoTodoAdapter extends RecyclerView.Adapter<NoTodoAdapter.ViewHolder> {

    public onItemClick itemClick;
    private List<MessageVO> mFruitList;

    public NoTodoAdapter(List<MessageVO> fruitList) {
        mFruitList = fruitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_notodo_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MessageVO item = mFruitList.get(position);
//		holder.fruitImage.setImageResource(item.getId());
        holder.fruitName.setText(item.getSubject());
//        holder.fruitName2.loadData(Html.fromHtml(item.getContent()).toString(), "text/html", "GBK");
//        holder.fruitName2.loadData(item.getContent(), "text/html", "GBK");
        holder.fruitName2.loadDataWithBaseURL(null, item.getContent(), "text/html", "utf-8", null);
        holder.fruitName3.setText(item.getSendtime());
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
        ImageView fruitImage;
        TextView fruitName;
        WebView fruitName2;
        TextView fruitName3;
        View rootView;

        public ViewHolder(View view) {
            super(view);
            rootView = view;
            fruitImage = (ImageView) view.findViewById(R.id.itemImage);
            fruitName = (TextView) view.findViewById(R.id.itemName);
            fruitName2 = (WebView) view.findViewById(R.id.itemName2);
            fruitName3 = (TextView) view.findViewById(R.id.itemName3);
        }

    }


}