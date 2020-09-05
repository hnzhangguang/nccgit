package com.yonyou.album.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yalantis.ucrop.UCrop;
import com.yonyou.album.plugin.callback.AlbumCallback;
import com.yonyou.album.plugin.utils.PicFileUtil;
import com.yonyou.common.onActivityForResult.OnActivityForResultUtils;
import com.yonyou.common.onActivityForResult.SimpleOnActivityForResultCallback;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CropPicture {
	
	//返回码，相机
	private static final int REQUEST_CAMERA = 200;
	private String[] permisions = {Manifest.permission.READ_EXTERNAL_STORAGE,
		  Manifest.permission.CAMERA};
	
	public void crop(final Activity context, final Uri uri, final AlbumCallback callback,
	                 final HashMap<String, Object> args) {
		if (PermissionsUtil.hasPermission(context, permisions)) {
			//这个函数主要用来控制预览和完成按钮的状态
			startCrop(context, uri, callback, args);
		} else {
			PermissionsUtil.requestPermission(context, new PermissionListener() {
				@Override
				public void permissionGranted(@NonNull String[] permission) {
					startCrop(context, uri, callback, args);
				}
				
				@Override
				public void permissionDenied(@NonNull String[] permission) {
					Toast.makeText(context, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
				}
			}, permisions);
		}
	}
	
	public void startCrop(Activity context, Uri uri, final AlbumCallback callback, HashMap<String,
		  Object> map) {
		UCrop uCrop = UCrop.of(uri, Uri.fromFile(PicFileUtil.getExternalDirFileName(context, "jpg")));
		if (map != null) {
			int maxWidth = (int) map.get("maxWidth");
			int maxHeight = (int) map.get("maxHeight");
			int quality = (int) map.get("quality");
			if (maxWidth > UCrop.MIN_SIZE && maxHeight > UCrop.MIN_SIZE) {
				uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
			}
			UCrop.Options options = new UCrop.Options();
			options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
			options.setCompressionQuality(quality);
			options.setHideBottomControls(true);
			options.setFreeStyleCropEnabled(true);
			uCrop.withOptions(options);
			Intent intent = uCrop.getIntent(context);
			OnActivityForResultUtils.startActivityForResult(context, REQUEST_CAMERA, intent,
				  new SimpleOnActivityForResultCallback() {
					  @Override
					  public void success(Integer resultCode, Intent data) {
						  JSONObject jsonObject = new JSONObject();
						  try {
							  jsonObject.put("path", PicFileUtil.getFilePath());
						  } catch (JSONException e) {
							  e.printStackTrace();
						  }
						  callback.onResult(jsonObject);
						  
					  }
				  });
		}
	}
	
}
