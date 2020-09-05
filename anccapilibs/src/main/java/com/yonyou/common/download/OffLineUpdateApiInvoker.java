package com.yonyou.common.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.yonyou.ancclibs.R;
import com.yonyou.common.download.ui.UpdateActivity;
import com.yonyou.common.onActivityForResult.OnActivityForResultUtils;
import com.yonyou.common.onActivityForResult.SimpleOnActivityForResultCallback;
import com.yonyou.common.utils.DeviceUtil;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;


/*
 * @功能: 下载h5应用包
 * @Date  2020/7/24;
 * @Author zhangg
 **/
public class OffLineUpdateApiInvoker implements IApiInvoker {
//    private final String APP_ID = "appId";
//    private final String APP_NAME = "appName";
//    private final String ICON = "icon";
//    private final String START_PAGE = "startPage";
//    private final String VERSION = "version";
	
	private static final String OFFLINE_UPDATE = "offlineUpdate";
	
	private Activity act;
	private MTLArgs args;
	
	@Override
	public String call(String apiname, MTLArgs args) throws MTLException {
		this.args = args;
		this.act = args.getContext();
		switch (apiname) {
			// offlineUpdate
			case OFFLINE_UPDATE:
				String appId = args.getString("appId");
				if (!isNetWork(act)) {
					updateErrorCallback(appId, R.string.mtl_offline_update_network_unavailable);
					return "";
				}
				startUpdateActivity(args);
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
	private void startUpdateActivity(final MTLArgs args) {
		String appInfo = args.getParams();
		Intent intent = new Intent(args.getContext(), UpdateActivity.class);
		intent.putExtra("appInfo", appInfo);
		OnActivityForResultUtils.startActivityForResult(args.getContext(), 10001, intent, new SimpleOnActivityForResultCallback() {
			@Override
			public void success(Integer resultCode, Intent data) {
				String startPagePath = data.getStringExtra("startPagePath");
				args.success(startPagePath);
			}
		});
	}
	
	
	private void updateErrorCallback(String appId, int errorId) {
		String startPage = com.yonyou.common.download.OfflineUpdateControl.getLocalAppParam(act, appId, "startPage");
		if (!TextUtils.isEmpty(startPage)) {
			if (!startPage.startsWith("/")) {
				startPage = "/" + startPage;
			}
			int tenantVersion = com.yonyou.common.download.OfflineUpdateControl.getAppParam(act, appId, "tenantVersion");
			args.success("startPagePath", OfflineUpdateControl.getOfflinePath(act, appId) + "/" + tenantVersion + "/" + startPage, false);
		} else {
			args.error(errorId);
		}
	}
	
	
	private boolean isNetWork(Context context) {
		return DeviceUtil.isNetworkConnected(context);
	}
	
	
}
