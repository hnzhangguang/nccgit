package com.yonyou.album.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.yonyou.album.plugin.callback.AlbumCallback;
import com.yonyou.album.plugin.config.AlbumConfig;
import com.yonyou.album.plugin.utils.PicFileUtil;
import com.yonyou.common.onActivityForResult.OnActivityForResultUtils;
import com.yonyou.common.onActivityForResult.SimpleOnActivityForResultCallback;
import com.yonyou.common.utils.permissions.PermissionListener;
import com.yonyou.common.utils.permissions.PermissionsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class CallCamera {
	
	//返回码，相机
	private static final int REQUEST_CAMERA = 200;
	private String[] permisions = {Manifest.permission.READ_EXTERNAL_STORAGE,
		  Manifest.permission.CAMERA};
	
	public void capture(final Activity context, final AlbumCallback callback, final HashMap<String,
		  Object> map) {
		if (PermissionsUtil.hasPermission(context, permisions)) {
			//这个函数主要用来控制预览和完成按钮的状态
			init(context, callback, map);
		} else {
			PermissionsUtil.requestPermission(context, new PermissionListener() {
				@Override
				public void permissionGranted(@NonNull String[] permission) {
					init(context, callback, map);
				}
				
				@Override
				public void permissionDenied(@NonNull String[] permission) {
					Toast.makeText(context, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
				}
			}, permisions);
		}
	}
	
	private void init(final Activity context, final AlbumCallback callback, final HashMap<String,
		  Object> map) {
		File albumPath = PicFileUtil.getExternalDirFileName(context, "jpg");
		Uri uri = null;
		if (Build.VERSION.SDK_INT >= 24) {
			
			uri = FileProvider.getUriForFile(context,
				  context.getPackageName() + AlbumConfig.FILE_PROVIDER_PACKAGE,
				  new File(albumPath.getPath()));
		} else {
			uri = Uri.parse("file://" + albumPath.getPath());
		}
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("output", uri);
		final Uri finalUri = uri;
		OnActivityForResultUtils.startActivityForResult(context, REQUEST_CAMERA, intent,
			  new SimpleOnActivityForResultCallback() {
				  
				  @Override
				  public void success(Integer resultCode, Intent data) {
					  if (map != null) {
						  if ((Boolean) map.get("crop")) {
							  CropPicture cropPicture = new CropPicture();
							  cropPicture.crop(context, finalUri, callback, map);
						  } else {
							  JSONObject jsonObject = new JSONObject();
							  try {
								  jsonObject.put("path", PicFileUtil.getFilePath());
							  } catch (JSONException e) {
								  e.printStackTrace();
							  }
							  callback.onResult(jsonObject);
						  }
						  
					  }
					  
					  
				  }
			  });
	}
}
