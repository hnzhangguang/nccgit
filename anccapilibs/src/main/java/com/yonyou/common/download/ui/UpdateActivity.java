package com.yonyou.common.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yonyou.ancclibs.R;
import com.yonyou.common.download.OfflineUpdateControl;
import com.yonyou.common.download.callback.UpdateDialogCallback;
import com.yonyou.common.utils.FileUtils;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.net.MTLHttpCallBack;
import com.yonyou.common.utils.net.MTLHttpDownCallBack;
import com.yonyou.common.utils.net.MTLOKHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UpdateActivity extends AppCompatActivity {
	private final int ERROR = 0;
	private final int SUCESS = 1;
	private final int PROGRESS = 2;
	private String host;
	private ProgressBarView progressView;
	
	private Handler handler =
		  new Handler() {
			  @Override
			  public void handleMessage(Message msg) {
				  super.handleMessage(msg);
				  
				  switch (msg.what) {
					  case SUCESS:
						  break;
					  case PROGRESS:
						  progressView.setProgress(msg.arg1);
						  break;
				  }
			  }
		  };
	
	/*
	 * @功能: 根据应用信息打开应用
	 * @参数  appid
	 * @参数  jsonObject
	 * @Date  2020/7/24
	 * @Author zhangg
	 **/
	public static void openH5App(String appid, JSONObject jsonObject) {
		
		
		// TODO
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		progressView = findViewById(R.id.progress_bar_view);
		Intent intent = getIntent();
		// 获取此h5应用信息json串
		String appInfo = intent.getStringExtra("appInfo");
		try {
			// 不为空,继续走逻辑
			if (!TextUtils.isEmpty(appInfo)) {
				JSONObject jsonObject = new JSONObject(appInfo);
				getAppInfo(jsonObject);
			} else {
				Toast.makeText(UpdateActivity.this, "appInfo is null!", Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			Log.e("mmmm", e.toString());
			e.printStackTrace();
		}
	}
	
	private void getAppInfo(JSONObject json) {
		host = json.optString("host");
		String httpPath = json.optString("httpPath");
		String url = host + httpPath;
		final String appId = json.optString("appId");
		// 获取appId对应的应用信息
		String appInfo = OfflineUpdateControl.getAppInfo(this, appId);
		JSONObject jsonObject = null;
		try {
			// 本地记录没有此应用信息的时候
			if (TextUtils.isEmpty(appInfo)) {
				jsonObject = new JSONObject();
				jsonObject.put("appId", appId);
				jsonObject.put("tenantVersion", 0);
			} else {
				// 本地记录已有此应用信息
				jsonObject = new JSONObject(appInfo);
				jsonObject.put("appId", appId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
		MTLOKHttpUtils.post(
			  url,
			  requestBody,
			  null,
			  new MTLHttpCallBack() {
				  @Override
				  public void onFailure(String error) {
					  //                updateErrorCallback(appId,
					  // R.string.mtl_offline_update_update_http_request_error);
				  }
				  
				  @Override
				  public void onResponse(int code, String body) {
					  try {
						  JSONObject result = new JSONObject(body);
						  boolean success = result.optBoolean("success", false);
						  if (code == 200 && success) {
							  JSONObject data = result.optJSONObject("data");
							  startUpdate(data);
						  } else {
							 
						  }
					  } catch (JSONException e) {
						  e.printStackTrace();
					  }
				  }
			  });
	}
	
	private void startUpdate(final JSONObject data) {
		this.runOnUiThread(
			  new Runnable() {
				  @Override
				  public void run() {
					  int forceUpdate = data.optInt("forceUpdate");
					  int existVsersion = data.optInt("existVersion");
					  if (existVsersion == 1) {
						  if (forceUpdate == 1) {
							  update(data);
						  } else {
							  String appId = data.optString("appId");
							  int tenantVersion = data.optInt("tenantVersion");
							  int ignoreTenantVersion =
								    OfflineUpdateControl.getAppIgnoreTenantVersion(UpdateActivity.this, appId);
							  if (tenantVersion != ignoreTenantVersion) {
								  updateDialog(data, appId);
							  }
						  }
					  }
				  }
			  });
	}
	
	private void updateDialog(final JSONObject data, final String appId) {
		com.yonyou.common.download.ui.UpdateDialog dialog =
			  new UpdateDialog(
				    this,
				    new UpdateDialogCallback() {
					    @Override
					    public void action(boolean confirm, boolean ignore) {
						    if (confirm) {
							    update(data);
						    } else {
							    if (ignore) {
								    int tenantVersion = data.optInt("tenantVersion");
								    OfflineUpdateControl.saveAppIgnoreTenantVersion(
										UpdateActivity.this, appId, tenantVersion);
							    }
						    }
					    }
				    });
		dialog.show();
	}
	
	private void update(final JSONObject data) {
		updateDownLoad(data);
	}
	
	/*
	 * @功能: 下载h5应用包
	 * @参数  jsonObject
	 * @Date  2020/7/24;
	 * @Author zhangg
	 **/
	private void updateDownLoad(final JSONObject jsonObject) {
		String downloadUrl = host + jsonObject.optString("downloadUrl");
		final String appid = jsonObject.optString("appId");
		final int tenantVersion = jsonObject.optInt("tenantVersion");
		// 下载的h5应用包存放的目录
		final String filePath = OfflineUpdateControl.getOfflinePath(this, appid) + "/" + tenantVersion;
		// 下载后的文件名+扩展名
		String fileName = "app.zip";
		MTLOKHttpUtils.downLoadFile(
			  downloadUrl,
			  filePath,
			  fileName,
			  new MTLHttpDownCallBack() {
				  @Override
				  public void onDownloadSuccess(File file) {
					  MTLLog.i("success", file.getPath());
					  try {
						  FileUtils.unZipFile(file.getPath(), filePath, true);
						  removeHistory(appid, jsonObject);
						  openH5App(appid, jsonObject);
					  } catch (IOException e) {
						  e.printStackTrace();
					  }
				  }
				  
				  @Override
				  public void onDownloading(int progress) {
					  
					  Message message = new Message();
					  message.what = PROGRESS;
					  message.arg1 = progress;
					  handler.sendMessage(message);
					  MTLLog.i("success", progress + "");
				  }
				  
				  @Override
				  public void onDownloadFailed(int code, String message) {
					  MTLLog.i("error", message);
				  }
			  });
	}
	
	/*
	 * @功能: 删除历史
	 * @参数  appId
	 * @参数  jsonObject
	 * @Date  2020/7/24
	 * @Author zhangg
	 **/
	private void removeHistory(String appId, JSONObject jsonObject) {
		// 查询本地已经记录的应用信息
		int oldTenantVersion = OfflineUpdateControl.getAppParam(this, appId, "tenantVersion");
		// 如果本地没有此应用信息,则版本号返回-1
		if (oldTenantVersion != -1) {
			String oldAppPath = OfflineUpdateControl.getOfflinePath(this, appId) + "/" + oldTenantVersion;
			// 删除旧版本的应用信息
			FileUtils.deleteDirectory(oldAppPath);
		}
		
		// 新版本的应用信息记录下来
		OfflineUpdateControl.saveAppInfo(this, appId, jsonObject.toString());
		String startPage = OfflineUpdateControl.getLocalAppParam(this, appId, "startPage");
		if (!TextUtils.isEmpty(startPage)) {
			String newTenantVersion = jsonObject.optString("tenantVersion");
			if (!startPage.startsWith("/")) {
				startPage = "/" + startPage;
			}
			String startPagePath = OfflineUpdateControl.getOfflinePath(this, appId) + "/" +
				  newTenantVersion + startPage;

//			Intent intent = new Intent();
//			intent.putExtra("startPagePath", startPagePath);
//			intent.putExtra("code", 1);
//			setResult(1000, intent);
//			finish();
		
		
		}
		
	}
	
	
}
