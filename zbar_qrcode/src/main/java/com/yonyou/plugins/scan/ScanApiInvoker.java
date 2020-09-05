package com.yonyou.plugins.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yonyou.common.onActivityForResult.OnActivityForResultUtils;
import com.yonyou.common.onActivityForResult.SimpleOnActivityForResultCallback;
import com.yonyou.common.utils.ResourcesUtils;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;
import com.yonyou.common.utils.utils.ImageBase64Utils;
import com.yonyou.plugins.ApiCallback;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;
import com.yonyou.zbarqrcode.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zbar.CommonScanActivity;
import cn.bingoogolapple.qrcode.zbar.QRCodeEncoder;
import cn.bingoogolapple.qrcode.zbar.listener.ResultListener;

public class ScanApiInvoker implements IApiInvoker {
	
	private static final String SCAN_QRCODE = "scanQRCode";
	private static final String GENERATE_QRCODE = "generateQRCode";
	private static final int REQUEST_SCAN = 999;
	private String[] permisions = {Manifest.permission.CAMERA,
		  Manifest.permission.READ_EXTERNAL_STORAGE};
	
	
	@Override
	public String call(String apiname, final MTLArgs args) throws MTLException {
		
		switch (apiname) {
			case SCAN_QRCODE:
				scanCode(args);
				return "";
			
			case GENERATE_QRCODE:
				generateQrcode(args);
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
	
	private void scanCode(final MTLArgs args) {
		final Activity context = args.getContext();
		if (PermissionsUtil.hasPermission(context, permisions)) {
			scan(args, context);
		} else {
			PermissionsUtil.requestPermission(context, new PermissionListener() {
				@Override
				public void permissionGranted(@NonNull String[] permission) {
					scan(args, context);
				}
				
				@Override
				public void permissionDenied(@NonNull String[] permission) {
					Toast.makeText(context, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
				}
			}, permisions);
		}
	}
	
	private void generateQrcode(final MTLArgs args) {
		final Activity context = args.getContext();
		if (PermissionsUtil.hasPermission(context, permisions)) {
			qrcode(args, context);
		} else {
			PermissionsUtil.requestPermission(context, new PermissionListener() {
				@Override
				public void permissionGranted(@NonNull String[] permission) {
					qrcode(args, context);
				}
				
				@Override
				public void permissionDenied(@NonNull String[] permission) {
					Toast.makeText(context, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
				}
			}, permisions);
		}
	}
	
	
	private void scan(final MTLArgs args, final Activity context) {
		final boolean toast = args.getBoolean("toast");
		boolean loopScan = args.getBoolean("loopScan");
		boolean autoZoom = args.getBoolean("autoZoom");
		boolean vibrate = args.getBoolean("vibrate");
		boolean sound = args.getBoolean("sound");
		int loopWaitTime = args.getInteger("loopWaitTime");
		Intent commonScanIntent = new Intent(context, CommonScanActivity.class);
		commonScanIntent.putExtra("loopScan", loopScan);
		commonScanIntent.putExtra("autoZoom", autoZoom);
		commonScanIntent.putExtra("vibrate", vibrate);
		commonScanIntent.putExtra("sound", sound);
		commonScanIntent.putExtra("loopWaitTime", loopWaitTime);
		CommonScanActivity.setResultListener(new ResultListener() {
			@Override
			public void onSuccess(String result) {
				if (toast) {
					Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
					// zhangg test
					ApiCallback callback = args.getCallback();
//					if (null != callback){
//						callback.success(result);
//					}
				}
				args.success("resultStr", result, true);
			}
			
			@Override
			public void onError(String error) {
			
			}
		});
		OnActivityForResultUtils.startActivityForResult(context, REQUEST_SCAN, commonScanIntent,
			  new SimpleOnActivityForResultCallback() {
				  @Override
				  public void success(Integer resultCode, Intent data) {
					  switch (resultCode) {
						  case CommonScanActivity.CAPTURE_TWO_DCODE:
							  String resultStr = data.getStringExtra(CommonScanActivity.EXTRA_QRVALUE);
							  Toast.makeText(context, resultStr, Toast.LENGTH_SHORT).show();
							  args.success("resultStr", resultStr, false);
							  break;
					  }
				  }
			  });
	}
	
	private void qrcode(MTLArgs args, Activity context) {
		String str = args.getString("str");
		int size = args.getInteger("size", 100);
		//        Bitmap bitmap = CodeCreator.createQRCode(str, size, size, null);
		Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(str, BGAQRCodeUtil.dp2px(context, size));
		if (bitmap != null) {
			String QRBase64 = ImageBase64Utils.bitmapToBase64(bitmap);
			if (!TextUtils.isEmpty(QRBase64)) {
				QRBase64 = "data:image/jpeg;base64," + QRBase64;
				JSONObject json = new JSONObject();
				try {
					json.put("imgSrc", QRBase64);
					args.success(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				args.error("生成二维码失败");
			}
		} else {
			args.error("生成二维码失败");
		}
	}
	
	
	private void startPermissionSettingActivity(Activity context) {
		Uri packageURI = Uri.parse("package:" + context.getPackageName());
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		Toast.makeText(context, ResourcesUtils.getStringResourse(context, R.string.mtl_no_permission),
			  Toast.LENGTH_LONG).show();
		
	}
	
}
