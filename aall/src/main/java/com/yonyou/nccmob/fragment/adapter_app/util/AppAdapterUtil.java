package com.yonyou.nccmob.fragment.adapter_app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.constant.Constant;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.fragment.adapter_app.App;
import com.yonyou.nccmob.fragment.adapter_app.AppGroup;
import com.yonyou.nccmob.fragment.adapter_app.AppsAdapter;

import java.util.ArrayList;
import java.util.List;


/*
 * @功能: app item 组装工具类
 * @Date  2020/8/12;
 * @Author zhangg
 **/
public class AppAdapterUtil {


    /*
     * @功能: 根据appinfo数据,组装一个view
     * @Date  2020/8/12;
     * @Author zhangg
     **/
    public static List<View> getOneViewByAppInfoData(Context context, List<App> listApps, AppsAdapter.onItemClick itemClick) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View inflate = layoutInflater.inflate(R.layout.fragment, null);
        RecyclerView recyclerView = inflate.findViewById(R.id.recyclerView);

        AppsAdapter appsAdapter = new AppsAdapter(context);
        appsAdapter.list.addAll(listApps);
        recyclerView.setAdapter(appsAdapter);
        appsAdapter.setItemClick(new AppsAdapter.onItemClick() {
            @Override
            public void onClickItemEvent(App app) {
                if (itemClick != null) {
                    itemClick.onClickItemEvent(app);
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, Constant.ROW_APP_NUM);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        List<View> list = new ArrayList<>();
        list.add(inflate);
        return list;

    }


    /*
     * @功能: 根据appinfo数据,组装views
     * @Date  2020/8/12;
     * @Author zhangg
     **/
    public static List<View> getViewsByAppInfoData(Context context, List<App> listApps, AppsAdapter.onItemClick itemClick) {
        List<View> pagerViews = new ArrayList<>();
        int rowAppSumapp_ = Constant.ROW_APP_NUM * 2;
        int pagerNum = listApps.size() / rowAppSumapp_;  // 默认分页数
        int pagerRemainder = listApps.size() % rowAppSumapp_; // 整数分页后剩余元素数
        if (pagerRemainder > 0) {
            if (pagerNum == 0) {
                pagerNum = 1;
            } else {
                pagerNum++;
            }
        }
        // 根据计算总页数后,创建页
        for (int i = 0; i < pagerNum; i++) {

            ArrayList tempApps = new ArrayList<>();
            if (i == pagerNum - 1) {  // 最后一页
                List list = listApps.subList(rowAppSumapp_ * i, listApps.size());
                tempApps.addAll(list);
            } else {  // 不是最后一页
                List list = listApps.subList(rowAppSumapp_ * i, rowAppSumapp_ * i + rowAppSumapp_);
                tempApps.addAll(list);
            }

            List<View> list = getOneViewByAppInfoData(context, tempApps, itemClick);

            pagerViews.addAll(list);
        }
        return pagerViews;

    }


    public static List<App> getCommonUseApp() {
        ArrayList listApps = new ArrayList<>();
        listApps.add(new App("英语等级1", false));
        listApps.add(new App("英语等级2", false));
        listApps.add(new App("英语等级3", false));
        listApps.add(new App("英语等级4", false));
        listApps.add(new App("英语等级5", false));
        listApps.add(new App("英语等级6", false));
        listApps.add(new App("英语等级7", false));
        listApps.add(new App("英语等级8", false));
        listApps.add(new App("英语等级9", false));
        listApps.add(new App("英语等级10", false));
        listApps.add(new App("英语等级11", false));
        return listApps;
    }

    public static List<AppGroup> addModels() {

        List<AppGroup> listAppGroup = new ArrayList<>();

        List<App> listApps = new ArrayList<>();
        listApps.add(new App(11, "移动采购", false, "yonyou/mobile_pu.zip", "mobile_pu.zip"));
        listApps.add(new App(22, "平台platform", false, "yonyou/mobile_platform.zip", "mobile_platform.zip"));
        listApps.add(new App(33, "平台uapbd", false, "yonyou/mobile_uapbd.zip", "mobile_uapbd.zip"));
        listApps.add(new App("供应商协同", false));
        listApps.add(new App("移动销售", false));
        listApps.add(new App("客户协同", false));
        listApps.add(new App("移动库管", false));

        listAppGroup.add(new AppGroup("NCC供应链", listApps));


        listApps = new ArrayList<>();
        listApps.add(new App("考试查询", false));
        listApps.add(new App("考试查询", false));
        listApps.add(new App("考试查询", false));
        listApps.add(new App("考试查询", false));

        listAppGroup.add(new AppGroup("NCC资产", listApps));

        listApps = new ArrayList<>();
        listApps.add(new App("通知公告", false));
        listApps.add(new App("通知公告", false));
        listApps.add(new App("通知公告", false));
        listApps.add(new App("通知公告", false));
        listApps.add(new App("通知公告", false));

        listAppGroup.add(new AppGroup("NCC财资", listApps));

        listApps = new ArrayList<>();
        listApps.add(new App("英语等级", false));
        listApps.add(new App("英语等级", false));
        listApps.add(new App("英语等级", false));
        listApps.add(new App("英语等级", false));
        listApps.add(new App("英语等级", false));
        listApps.add(new App("英语等级", false));

        listAppGroup.add(new AppGroup("NCC其他", listApps));

        return listAppGroup;

    }


}
