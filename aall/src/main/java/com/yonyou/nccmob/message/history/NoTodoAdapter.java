package com.yonyou.nccmob.message.history;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.logs.LogerNcc;
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


    /*
     * @功能: 获取发送消息日期(去掉时分秒)
     * @参数: sendtime
     * @Date  2020/9/8 1:38 PM
     * @Author zhangg
     **/
    public String getDate(String sendtime) {
        if (TextUtils.isEmpty(sendtime) || sendtime.equals("null")) {
            return "";
        }
        if (sendtime.length() > 10) {
            return sendtime.substring(0, 10);
        }
        return sendtime;
    }


    /*
     * @功能: 处理内容是HTML的列表item显示
     * @参数: content
     * @Date  2020/9/9 9:56 AM
     * @Author zhangg
     **/
    public String getShowContent(String content) {
        //\u003c,\u003d,\u003e,\u003cspan,class\u003d
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        String s1 = content.replaceAll("<div class=\"divtext\">", "")
                .replaceAll("<span class=\"labeltext\">", "")
                .replaceAll("<span  class=\"labeltext\">", "")
                .replaceAll("<span class=\"normaltext\">", "")
                .replaceAll("<span  class=\"normaltext\">", "")
                .replaceAll("<span class=\"keytext\">", "")
                .replaceAll("<span  class=\"keytext\">", "")
                .replaceAll(":</span >", ":")
                .replaceAll("</span >", " | ")
                .replaceAll("</div>", "")
                .replaceAll(":\\n", ":")
                .replaceAll("\\n\\n\\n\\n", "");
        if (s1.startsWith("\n")) {
            s1 = s1.replaceFirst("\\n", "");
        }
        LogerNcc.e(s1);
        return s1;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageVO item = mFruitList.get(position);
        holder.fruitName.setText(item.getSubject());
        holder.fruitName2.setText(getDate(item.getSendtime()));
        holder.fruitName3.setText(TextUtils.isEmpty(item.getSenderpersonname()) ? "发送人: 张三 " : item.getSenderpersonname());
//        holder.fruitName3.setText(getShowContent(item.getContent()));  // 测试使用
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClick != null) {
                    itemClick.onClickItemEvent(item);
                }
            }
        });

        // 消息类型
        String msgtype = item.getMsgtype();
        if (Constant.msgtype_approve.equals(msgtype)) {  // 审批消息
            holder.fruitImage.setImageResource(R.mipmap.b3);
        } else if (Constant.msgtype_working.equals(msgtype)) { // 工作流
            holder.fruitImage.setImageResource(R.mipmap.b2);
        } else if (Constant.msgtype_business.equals(msgtype)) { // 业务
            holder.fruitImage.setImageResource(R.mipmap.b1);
        } else {
            holder.fruitImage.setImageResource(R.mipmap.ic_launcher);
        }

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
        TextView fruitName2;
        TextView fruitName3;
        View rootView;

        public ViewHolder(View view) {
            super(view);
            rootView = view;
            fruitImage = (ImageView) view.findViewById(R.id.itemImage);
            fruitName = (TextView) view.findViewById(R.id.itemName);
            fruitName2 = (TextView) view.findViewById(R.id.itemName22);
            fruitName3 = (TextView) view.findViewById(R.id.itemName33);
//            msg_date = (TextView) view.findViewById(R.id.msg_date);
        }

    }


}