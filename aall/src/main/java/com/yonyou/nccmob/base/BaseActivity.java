package com.yonyou.nccmob.base;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.yonyou.common.utils.utils.CheckUtil;
import com.yonyou.common.utils.utils.DataUtil;
import com.yonyou.nccmob.NCCOpenH5MainActivity;
import com.yonyou.nccmob.R;

/**
 * 基本activity
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
	
	/*
	 * @功能: 判空工具method
	 * @参数  ;
	 * @Date  2020/8/25;
	 * @Author zhangg
	 **/
	public static boolean isNull(String string) {
		return CheckUtil.isNull(string);
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w("mmmm", this.getLocalClassName());
		
		initLayout();
		initView();
		initListener();
		initData();
		initExtend(); // 扩展使用,一般不用
		
	}
	
	public void initLayout() {
	}
	
	public void initView() {
	}
	
	public void initListener() {
	}
	
	public void initData() {
	}
	
	public void initExtend() {
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View view) {
		
	}
	
	public void showMessage(Object message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message == null ? "~~" :
						    message.toString(),
					  Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void showToast(Object message) {
		showMessage(message);
	}
	
	/**
	 * 通知栏
	 *
	 * @param value
	 */
	private void showNotification(String value) {
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			
			showNotificationMessage(NccAppliction.appliction, value);
			
		} else {
			NotificationManager mNotificationManager =
				  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			//构造Builder对象
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				  .setSmallIcon(R.mipmap.ic_launcher)
				  .setContentTitle("我是标题")
				  .setContentText("我是内容")
				  .setDefaults(Notification.DEFAULT_ALL)//全部
				  //                    .setDefaults(Notification.DEFAULT_LIGHTS)//闪光灯
				  //                    .setDefaults(Notification.DEFAULT_VIBRATE)//震动
				  //                    .setDefaults(Notification.DEFAULT_SOUND)//声音
				  ; // requires VIBRATE permission  消息提醒设置
			Notification notification = builder.build();
			
			//点击通知本身会显示ResultActivity
			Intent resultIntent = new Intent(this, NCCOpenH5MainActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			
			
			PendingIntent resultPendingIntent =
				  PendingIntent.getActivity(
					    this,
					    0,
					    resultIntent,
					    PendingIntent.FLAG_UPDATE_CURRENT
				  );
			
			builder.setContentIntent(resultPendingIntent);
			mNotificationManager.notify(1, notification);
		}
		
		
	}
	
	/**
	 * 显示通知消息
	 *
	 * @param context
	 */
	public void showNotificationMessage(Context context, String msg) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			
			String channel1 = DataUtil.getMetaData(NccAppliction.appliction, "com" +
				  ".yonyou" +
				  ".nccmob.channelid");
			if (TextUtils.isEmpty(channel1)) {
				channel1 = "channelID";
			}
			// 获取manager
			NotificationManager manager =
				  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// 创建channel
			NotificationChannel channel = new NotificationChannel(channel1,
				  "channelNAME", NotificationManager.IMPORTANCE_HIGH);
			manager.createNotificationChannel(channel);
			// 点击通知跳转界面
			//点击通知本身会显示ResultActivity
			Intent resultIntent = new Intent(this, NCCOpenH5MainActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
				  .FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent resultPendingIntent =
				  PendingIntent.getActivity(
					    context,
					    0,
					    resultIntent,
					    PendingIntent.FLAG_UPDATE_CURRENT
				  );
			// 创建notification
			NotificationCompat.Builder notification =
				  new NotificationCompat.Builder(context, channel1)
					    .setContentTitle("通知")
					    .setContentText(msg)
					    .setContentIntent(resultPendingIntent)
					    .setSmallIcon(R.mipmap.ic_launcher)
					    .setPriority(NotificationCompat.PRIORITY_HIGH)
					    .setAutoCancel(true);
			
			// 显示通知
			int NOTIFICATION_ACTION = 1;
			manager.notify(NOTIFICATION_ACTION, notification.build());
		}
	}
	
}
