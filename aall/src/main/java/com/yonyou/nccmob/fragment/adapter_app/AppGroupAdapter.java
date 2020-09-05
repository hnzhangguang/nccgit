package com.yonyou.nccmob.fragment.adapter_app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.download.OfflineUpdateControl;
import com.yonyou.common.utils.FileUtils;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.net.MTLHttpDownCallBack;
import com.yonyou.common.utils.net.MTLOKHttpUtils;
import com.yonyou.nccmob.NCCOpenH5MainActivity;
import com.yonyou.nccmob.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppGroupAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_FOOTER = 2;
    protected List<AppGroup> mGroupData;
    private RecyclerView mRecyclerView;
    /**
     * RecyclerView高度
     */
    private int mRecyclerViewHeight;
    /**
     * 复用同一个View对象池
     */
    private RecyclerView.RecycledViewPool mRecycledViewPool;
    /**
     * item高度
     */
    private int itemHeight;

    private Context context;

    public AppGroupAdapter(RecyclerView recyclerView, List<AppGroup> data, Context context) {
        mRecyclerView = recyclerView;
        mGroupData = data;
        this.context = context;
        mRecycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.apph5_item, parent, false);
            return new ItemVH(view);
        } else {
            //Footer是最后留白的位置，以便最后一个item能够出发tab的切换
            View view = new View(context);
            Log.e("footer", "parentHeight: " + mRecyclerViewHeight + "--" + "itemHeight: " + itemHeight);
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            mRecyclerViewHeight - itemHeight));
            return new FootVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ItemVH vh = (ItemVH) holder;
            vh.tvGroup.setText(mGroupData.get(position).getName());
            vh.recyclerView.setRecycledViewPool(mRecycledViewPool);
            vh.recyclerView.setHasFixedSize(false);
            vh.recyclerView.setNestedScrollingEnabled(false);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4) {

                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public void onLayoutCompleted(RecyclerView.State state) {
                    super.onLayoutCompleted(state);
                    mRecyclerViewHeight = mRecyclerView.getHeight();
                    itemHeight = vh.itemView.getHeight();
                }
            };
            gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

            vh.recyclerView.setLayoutManager(gridLayoutManager);

            AppsAdapter appsAdapter = new AppsAdapter(context);
            appsAdapter.list.addAll(mGroupData.get(position).getApps());
            vh.recyclerView.setAdapter(appsAdapter);
            appsAdapter.setItemClick(new AppsAdapter.onItemClick() {
                @Override
                public void onClickItemEvent(App app) {
                    int id = app.getId();
                    downloadAndOpenH5(app);
                    Toast.makeText(context, app.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void downloadAndOpenH5(App app) {
        String downloadUrl = "http://10.6.243.157/" + app.getDownloadUrl();
        String fileName = app.getRename();
        String filePath = OfflineUpdateControl.getOfflinePathWhitoutAppId(context);

        String openPaht = filePath + "/mobile_pu/app/purchaser/main/index.html";
        File file = new File(openPaht);
        if (file.exists()) {
            ComponentName cn = new ComponentName(context.getPackageName(), "com.yonyou.nccmob.NCCOpenH5MainActivity");
            Intent intent = new Intent();
            intent.putExtra(NCCOpenH5MainActivity.PREVIEW_URL, openPaht);
            intent.setComponent(cn);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {
            MTLOKHttpUtils.downLoadFile(
                    downloadUrl,
                    filePath,
                    fileName,
                    new MTLHttpDownCallBack() {
                        @Override
                        public void onDownloadSuccess(File file) {
                            MTLLog.i("success", file.getPath());
                            try {
                                FileUtils.unZipFile(file.getPath(), filePath, false);
                                downloadAndOpenH5(app);
                            } catch (IOException e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                LogerNcc.e(e);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDownloading(int progress) {

                            MTLLog.i("success", progress + "");
                        }

                        @Override
                        public void onDownloadFailed(int code, String message) {
                            MTLLog.i("error", message);
                            LogerNcc.e(message);
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                        }
                    });
        }


    }


    @Override
    public int getItemCount() {
        return mGroupData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mGroupData.size()) return VIEW_TYPE_FOOTER;
        return VIEW_TYPE_ITEM;
    }

    class FootVH extends RecyclerView.ViewHolder {

        public FootVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ItemVH extends RecyclerView.ViewHolder {

        private TextView tvGroup;
        private RecyclerView recyclerView;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            tvGroup = itemView.findViewById(R.id.tv_name);
            recyclerView = itemView.findViewById(R.id.recycler);
        }
    }
}
	