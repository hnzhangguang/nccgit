package com.yonyou.nccmob.fragment.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yonyou.common.net.MTLHttpCallBack;
import com.yonyou.common.net.MTLOKHttpUtils;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.utils.AppUtil;
import com.yonyou.common.vo.AppInfo;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;
import com.yonyou.nccmob.message.history.HistoryMessageActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/*
 * @功能:
 * @参数  ;
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class TestAdapter extends RecyclerView.Adapter {
	
	private List<Fruit> mFruitList;
	private BaseActivity activity;
	
	public TestAdapter(BaseActivity activity, List<Fruit> fruitList) {
		mFruitList = fruitList;
		this.activity = activity;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view =
			  LayoutInflater.from(parent.getContext()).inflate(R.layout.home_app_item, parent, false);
		ViewHolder holder = new ViewHolder(view);
		return holder;
	}
	
	
	private void getData() {
		
		String url = "xxxx";
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> headers = new HashMap<>();
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
		MTLOKHttpUtils.post(
			  url,
			  requestBody,
			  headers,
			  new MTLHttpCallBack() {
				  @Override
				  public void onFailure(String error) {
					  LogerNcc.e(error);
				  }
				  
				  @Override
				  public void onResponse(int code, String body) {
					  try {
						  LogerNcc.e(code + " -> " + body);
					  } catch (Exception e) {
						  e.printStackTrace();
					  }
				  }
			  });
	}
	
	
	public void openActivity() {
		Intent intent = new Intent(activity, HistoryMessageActivity.class);
		activity.startActivity(intent);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		Fruit fruit = mFruitList.get(position);
		if (holder instanceof ViewHolder) {
			ViewHolder viewHolder = (ViewHolder) holder;
			viewHolder.fruitImage.setImageResource(fruit.getImageId());
			viewHolder.fruitName.setText(fruit.getName());
			View view = viewHolder.getView();
			view.setOnClickListener(
				  new View.OnClickListener() {
					  @Override
					  public void onClick(View view) {
						  
						  try {
							  openActivity();
//							  getData();
//							  String jsonString =
//								    "[{“id”:0,”name”:”我是第0个”,”age”:10},{“id”:1,”name”:”我是第1个”,”age”:11},{“id”:2,”name”:”我是第2个”,”age”:12},{“id”:3,”name”:”我是第3个”,”age”:13},{“id”:4,”name”:”我是第4个”,”age”:14},{“id”:5,”name”:”我是第5个”,”age”:15},{“id”:6,”name”:”我是第6个”,”age”:16},{“id”:7,”name”:”我是第7个”,”age”:17},{“id”:8,”name”:”我是第8个”,”age”:18},{“id”:9,”name”:”我是第9个”,”age”:19}]";
//
//							  Gson gson = new Gson();
//							  List<TempVo> listObject =
//								    gson.fromJson(
//										jsonString,
//										new TypeToken<List<TempVo>>() {
//										}.getType()); // 把JSON格式的字符串转为List
//							  LogerNcc.e(listObject);
//
							  //                        PermissionX.init(activity)
							  //                                .permissions(Manifest.permission.CAMERA,
							  // Manifest.permission.CALL_PHONE)
							  //                                .onExplainRequestReason(new
							  // ExplainReasonCallback() {
							  //                                    @Override
							  //                                    public void onExplainReason(ExplainScope
							  // scope, List<String> deniedList) {
							  ////                                        showRequestReasonDialog(deniedList,
							  // "即将重新申请的权限是程序必须依赖的权限", "我已明白", "取消")
							  //                                    }
							  //                                })
							  //                                .request(new RequestCallback() {
							  //                                    @Override
							  //                                    public void onResult(boolean allGranted,
							  // List<String> grantedList, List<String> deniedList) {
							  //
							  //                                    }
							  //                                });
							  
							  AppInfo appInfo = new AppInfo();
							  appInfo.setAppname("name");
							  appInfo.setAppid("id1");
							  appInfo.save();
							  List<AppInfo> appInfoLocation = AppUtil.getAppInfoLocation();
							  LogerNcc.e(appInfoLocation);
							  //
//							  AppInfo appid2 = AppUtil.getAppInfoByAppId("id1");
							  //                        LogerNcc.e(appid2);
							  //
							  //                        appid2.setUrl("http://www.xxx33.com");
							  //                        appid2.setZipurl("6rrr43232");
							  //
							  //                        AppUtil.updateAppInfoLocation(appid2);
							  //                        appid2 = AppUtil.getAppInfoByAppId("id1");
							  //                        LogerNcc.e(appid2);
							  
						  } catch (Exception e) {
							  e.printStackTrace();
						  }
						  try {
							  if (activity instanceof BaseActivity) {
								  BaseActivity baseActivity = (BaseActivity) activity;
								  // 测试通知消息
								  //                        baseActivity.showNotificationMessage
								  //                                (baseActivity, "消息...");
								  //                            Intent intent = new Intent(baseActivity,
								  // UpdateActivity.class);
								  //                            JSONObject jsonObject = new JSONObject();
								  //                            JSONObject json = new JSONObject();
								  //                            jsonObject.put("host", json.toString());
								  //                            json.put("appId", "");
								  //                            json.put("httpPath", "");
								  //                            intent.putExtra("appInfo", jsonObject.toString());
								  //                            baseActivity.startActivity(intent);
								  
								  //                  AppInfo appInfo = new AppInfo();
								  //                  appInfo.setZipurl("http://www.baidu.com");
								  //                  appInfo.setAppname("appname1");
								  //                  appInfo.setAppid("appid1");
								  //                  appInfo.setUrl("http://wwwxx.xxxx.com");
								  //                  appInfo.setVersion(1);
								  //                  appInfo.setType("apptype");
								  //                  appInfo.setIconurl("iconurl");
								  //                  appInfo.save();
								  //                  List<AppInfo> list =
								  //                      LitePalNcc.select("id, appname ,
								  // version").find(AppInfo.class);
								  //                  LogerNcc.e(list);
								  //
								  //                  LitePalDB litePalDB = new LitePalDB("demo2", 1);
								  //                  litePalDB.addClassName(Singer.class.getName());
								  //                  litePalDB.addClassName(AppInfo.class.getName());
								  //                  LitePal.use(litePalDB);
								  //                  appInfo = new AppInfo();
								  //                  appInfo.setZipurl("http://www.baidu2.com");
								  //                  appInfo.setAppname("appname2");
								  //                  appInfo.setAppid("appid2");
								  //                  appInfo.setUrl("http://wwwxx.xxxx2.com");
								  //                  appInfo.setVersion(1);
								  //                  appInfo.setType("apptype2");
								  //                  appInfo.setIconurl("iconurl2");
								  //                  appInfo.save();
								  //                  list = LitePalNcc.select("appid, appname ,
								  // version").find(AppInfo.class);
								  //                  LogerNcc.e(list);
								  //
								  //                  litePalDB = LitePalDB.fromDefault("nccmobdatabase");
								  //                  LitePal.use(litePalDB);
								  //
								  //                  list = LitePalNcc.select("appid, appname ,
								  // version").find(AppInfo.class);
								  //                  LogerNcc.e(list);
								  
								  //                  LitePal.findBySQL()
								  //                  SQLiteDatabase mDatabase;
								  //                  mDatabase = new DBHelper(activity).getWritableDatabase();
								  //
								  //                  ContentValues contentValues = new ContentValues();
								  //                  contentValues.put("id", 0);
								  //                  contentValues.put("name", "peter");
								  //                  contentValues.put("gender", 0);
								  //                  contentValues.put("number", "201804081705");
								  //                  contentValues.put("score", "100");
								  
							  }
							  
						  } catch (Exception e) {
							  e.printStackTrace();
						  }
					  }
				  });
		}
	}
	
	@Override
	public int getItemCount() {
		return mFruitList.size();
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView fruitImage;
		TextView fruitName;
		View view;
		
		public ViewHolder(View view) {
			super(view);
			this.view = view;
			fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
			fruitName = (TextView) view.findViewById(R.id.fruitname);
		}
		
		public View getView() {
			return view;
		}
	}
}
