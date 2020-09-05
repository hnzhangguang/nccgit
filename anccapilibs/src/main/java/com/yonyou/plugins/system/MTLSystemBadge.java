package com.yonyou.plugins.system;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.yonyou.ancclibs.R;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MTLSystemBadge {
	private static int xiaomiNotificationId = 1;
	
	public static void setXiaomiNotificationBadge(int count, Context context) {
		//注：小米手机需要在设置通知中开启允许显示角标
		NotificationManager notificationManager =
			  (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		if (notificationManager == null) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// 8.0之后添加角标需要NotificationChannel
			NotificationChannel channel = new NotificationChannel("badge", "badge",
				  NotificationManager.IMPORTANCE_DEFAULT);
			channel.setShowBadge(true);
			notificationManager.createNotificationChannel(channel);
		}
		Intent intent = new Intent(context, context.getClass());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification notification = new NotificationCompat.Builder(context, "badge")
			  .setContentTitle("应用角标")
			  .setContentText("您有" + count + "条未读消息")
			  //                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R
			  //                .mipmap
			  //                        .ic_launcher))
			  .setSmallIcon(R.drawable.mtl_icon)
			  .setAutoCancel(true)
			  .setContentIntent(pendingIntent)
			  .setChannelId("badge")
			  .setNumber(count)
			  .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL).build();
		
		
		ShortcutBadger.applyNotification(context.getApplicationContext(), notification, count);
		
		xiaomiNotificationId = xiaomiNotificationId++;
		notificationManager.notify(xiaomiNotificationId, notification);
	}
	
	public static void removeBadge(Context context) {
		ShortcutBadger.removeCount(context);
	}
	
	
	public static void setBadge(Context context, int badgeCount) {
		String manufacturer = Build.MANUFACTURER;
		if (manufacturer != null && manufacturer.length() > 0) {
			String phone_type = manufacturer.toLowerCase();
			if ("xiaomi".equals(phone_type)) {
				setXiaomiNotificationBadge(badgeCount, context);
			} else {
				ShortcutBadger.applyCount(context, badgeCount);
			}
		}
	}
	
}
