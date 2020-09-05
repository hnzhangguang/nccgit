package com.yonyou.album.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.yalantis.ucrop.UCrop;
import com.yonyou.album.plugin.adapter.AlbumGridViewAdapter;
import com.yonyou.album.plugin.adapter.PhotoAibumAdapter;
import com.yonyou.album.plugin.config.AlbumConfig;
import com.yonyou.album.plugin.model.ImageItem;
import com.yonyou.album.plugin.model.YYPhotoAlbum;
import com.yonyou.album.plugin.model.YYPhotoItem;
import com.yonyou.album.plugin.utils.Bimp;
import com.yonyou.album.plugin.utils.PicFileUtil;
import com.yonyou.album.plugin.utils.PublicWay;
import com.yonyou.ancclibs.R;
import com.yonyou.common.utils.permissions.PermissionsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends Activity {
	public static final int RESULT_IMAGE = 9090321;
	
	/**
	 * 查询字段
	 */
	private static final String[] IMAGE_PROJECTION = {MediaStore.Images.ImageColumns._ID, // 图片id
		  MediaStore.Images.ImageColumns.DISPLAY_NAME, // 图片显示名称
		  MediaStore.Images.ImageColumns.DATA, // 图片path
		  MediaStore.Images.ImageColumns.ORIENTATION, // 图片旋转
		  MediaStore.Images.ImageColumns.BUCKET_ID, // dir id 目录
		  MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, // 目录显示名称
		  MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC, // 缩略图id
		  MediaStore.Images.ImageColumns.SIZE};
	
	/**
	 * where条件
	 */
	private static final String IMAGE_SELECTION = MediaStore.Images.ImageColumns.SIZE + ">?";
	/**
	 * 图片大小限制
	 */
	private static final int IMAGE_SIZE_LIMIT = 10240;
	private static final int REQUEST_CAMERA = 200;
	
	//显示手机里的所有图片的列表控件
	private GridView gridView;
	//当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;
	//gridView的adapter
	private AlbumGridViewAdapter gridImageAdapter;
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			gridImageAdapter.notifyDataSetChanged();
		}
	};
	//完成按钮
	private TextView okButton;
	private CheckBox cb_original;
	// 返回按钮
	private RelativeLayout back;
	// 相册按钮
	private TextView cancel;
	/**
	 * 相册背景
	 */
	private View viewBackground;
	/**
	 * 相册列表
	 */
	private ListView listAlbum;
	/**
	 * 相册显示的适配器
	 */
	private PhotoAibumAdapter albumAdapter;
	/**
	 * 相册总列表
	 */
	private List<YYPhotoAlbum> aibumList;
	/**
	 * album进入动画
	 */
	private TranslateAnimation enterAnimation;
	/**
	 * album退出动画
	 */
	private TranslateAnimation exitAnimation;
	/**
	 * 当前类别的相册列表
	 */
	private YYPhotoAlbum aibum;
	private TextView headerTitle;
	private Intent intent;
	// 预览按钮
	private TextView preview;
	private ArrayList<ImageItem> dataList;
	private Context mContext;
	private boolean needCrop;
	private int maxWidth;
	private int maxHeight;
	private int quality = -1;
	private String[] permisions = {Manifest.permission.READ_EXTERNAL_STORAGE};
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_album);
		mContext = this;
		PublicWay.activityList.add(this);
		Bimp.tempSelectBitmap.clear();
		//注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		checkPermission();
	}
	
	private void checkPermission() {
		if (PermissionsUtil.hasPermission(this, permisions)) {
			//这个函数主要用来控制预览和完成按钮的状态
			init();
		} else {
			PermissionsUtil.requestPermission(this, new com.yonyou.common.utils.permissions.PermissionListener() {
				@Override
				public void permissionGranted(@NonNull String[] permission) {
				
				}
				
				@Override
				public void permissionDenied(@NonNull String[] permission) {
					Toast.makeText(AlbumActivity.this, "缺少必要权限，请同意申请权限", Toast.LENGTH_LONG).show();
				}
			}, permisions);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
			ArrayList<String> paths = new ArrayList<>();
			paths.add(PicFileUtil.getFilePath());
			Intent data = new Intent();
			data.putStringArrayListExtra("paths", paths);
			setResult(RESULT_IMAGE, data);
			finish();
		} else if (resultCode == UCrop.RESULT_ERROR) {
			final Throwable cropError = UCrop.getError(intent);
		}
	}
	
	// 初始化，给一些对象赋值
	private void init() {
		// 先查询所有的图片，并分类
		aibumList = getYYPhotoAlbumList();
		aibum = aibumList.get(0);
		aibum.setCurrentChoice(true);
		// 图片Adapter
		List<YYPhotoItem> photoItems = aibum.getPhotoList();
		dataList = new ArrayList<ImageItem>();
		for (int i = 0; i < photoItems.size(); i++) {
			YYPhotoItem yyPhotoItem = photoItems.get(i);
			ImageItem imageItem = new ImageItem();
			imageItem.setImagePath(yyPhotoItem.getPhotoPath());
			imageItem.setImageId(yyPhotoItem.getPhotoId() + "");
			dataList.add(imageItem);
		}
		
		
		back = findViewById(R.id.back);
		viewBackground = findViewById(R.id.album_list_background);
		cancel = findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener());
		preview = findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());
		cb_original = findViewById(R.id.cb_original);
		
		listAlbum = findViewById(R.id.album_list_view);
		headerTitle = findViewById(R.id.headerTitle);
		// 设标题
		headerTitle.setText(aibum.getName());
		
		// 相册adapter
		albumAdapter = new PhotoAibumAdapter(aibumList, this);
		listAlbum.setAdapter(albumAdapter);
		// 相册选择监听
		listAlbum.setOnItemClickListener(new AlbumOnItemClickListener());
		
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		HashMap<String, Object> map = (HashMap<String, Object>) bundle.getSerializable("map");
		int count = 9;
		if (map != null) {
			boolean crop = (boolean) map.get("crop");
			count = (int) map.get("count");
			if (count > 9 || count <= 0) {
				count = 9;
			}
			if (count == 1 && crop) {
				needCrop = true;
				maxWidth = (int) map.get("maxWidth");
				maxHeight = (int) map.get("maxHeight");
				quality = (int) map.get("quality");
			}
		}
		PublicWay.num = count;
		gridView = findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = findViewById(R.id.ok_button);
		okButton.setText("完成(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
		isShowOkBt();
		initListener();
	}
	
	private void initListener() {
		
		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(final ToggleButton toggleButton,
			                        int position, boolean isChecked, Button chooseBt) {
				if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
					toggleButton.setChecked(false);
					chooseBt.setVisibility(View.GONE);
					if (!removeOneData(dataList.get(position))) {
						Toast.makeText(AlbumActivity.this, "选择图片超过上限",
							  Toast.LENGTH_SHORT).show();
					}
					return;
				}
				if (isChecked) {
					chooseBt.setVisibility(View.VISIBLE);
					Bimp.tempSelectBitmap.add(dataList.get(position));
					okButton.setText("完成(" + Bimp.tempSelectBitmap.size()
						  + "/" + PublicWay.num + ")");
				} else {
					Bimp.tempSelectBitmap.remove(dataList.get(position));
					chooseBt.setVisibility(View.GONE);
					okButton.setText("完成(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				}
				isShowOkBt();
			}
		});
		
		okButton.setOnClickListener(new AlbumSendListener());
		viewBackground.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 隐藏事件
				hideAlbumList();
			}
		});
		
	}
	
	public void changeAlbum() {
		// 选择相册
		if (listAlbum.getVisibility() == View.VISIBLE) {
			hideAlbumList();
		} else {
			showAlbumList();
		}
	}
	
	private boolean removeOneData(ImageItem imageItem) {
		if (Bimp.tempSelectBitmap.contains(imageItem)) {
			Bimp.tempSelectBitmap.remove(imageItem);
			okButton.setText("完成(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			return true;
		}
		return false;
	}
	
	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText("完成(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
		} else {
			okButton.setText("完成(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}
	
	/**
	 * 进入动画
	 *
	 * @return
	 */
	private AnimationSet getEnterAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		if (enterAnimation == null) {
			enterAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
				  Animation.RELATIVE_TO_SELF, 0f,
				  Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
			enterAnimation.setDuration(400);
		}
		animationSet.addAnimation(enterAnimation);
		return animationSet;
	}
	
	/**
	 * 退出动画
	 *
	 * @return
	 */
	private AnimationSet getExitAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		if (exitAnimation == null) {
			exitAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
				  Animation.RELATIVE_TO_SELF, 0f,
				  Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
			exitAnimation.setDuration(400);
		}
		animationSet.addAnimation(exitAnimation);
		return animationSet;
	}
	
	/**
	 * 展示相册
	 */
	private void showAlbumList() {
		gridView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		listAlbum.startAnimation(getEnterAnimation());
		viewBackground.setVisibility(View.VISIBLE);
		listAlbum.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏相册
	 */
	private void hideAlbumList() {
		gridView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		listAlbum.startAnimation(getExitAnimation());
		viewBackground.setVisibility(View.GONE);
		listAlbum.setVisibility(View.GONE);
	}
	
	/**
	 * 获取相册列表
	 *
	 * @return
	 */
	private List<YYPhotoAlbum> getYYPhotoAlbumList() {
		// 查询sd卡中所有的图片
		Cursor cursor = MediaStore.Images.Media.query(this.getContentResolver(),
			  MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_SELECTION,
			  new String[]{String.valueOf(IMAGE_SIZE_LIMIT)}, MediaStore.Images.Media.DATE_TAKEN +
				    " DESC");
		
		// 所有图片
		YYPhotoAlbum total = new YYPhotoAlbum();
		// 名称
		total.setName(getString(R.string.album_all_photo));
		// 封面
		total.setBitmap(R.drawable.album_photo_default);
		// 所有图片标记
		total.setTotal(true);
		
		// 所有相册
		Map<String, YYPhotoAlbum> albumMap = new LinkedHashMap<>();
		// 遍历图片
		while (cursor.moveToNext()) {
			// id
			String photoId = cursor.getString(0);
			// 路径
			String photoPath = cursor.getString(2);
			// 目录id
			String dirId = cursor.getString(4);
			// 目录名
			String dirName = cursor.getString(5);
			
			// 生成YYPhotoItem
			YYPhotoItem item = new YYPhotoItem(Integer.valueOf(photoId), photoPath);
			// 添加到total
			total.addPhotoItem(item);
			// 取相册对象
			YYPhotoAlbum photoAlbum = albumMap.get(dirId);
			if (photoAlbum == null) {
				photoAlbum = new YYPhotoAlbum(dirName, Integer.parseInt(photoId));
				albumMap.put(dirId, photoAlbum);
			}
			// 相册里添加照片
			photoAlbum.addPhotoItem(item);
		}
		// 关闭cursor
		cursor.close();
		
		// 相册列表
		List<YYPhotoAlbum> aibumList = new ArrayList<YYPhotoAlbum>();
		aibumList.add(total);
		aibumList.addAll(albumMap.values());
		return aibumList;
	}
	
	public interface PermissionListener {
		
		/**
		 * 通过授权
		 *
		 * @param permission
		 */
		void permissionGranted(@NonNull String[] permission);
		
		/**
		 * 拒绝授权
		 *
		 * @param permission
		 */
		void permissionDenied(@NonNull String[] permission);
	}
	
	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.setClass(AlbumActivity.this, GalleryActivity.class);
				startActivity(intent);
			}
		}
		
	}
	
	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			if (needCrop) {
				ImageItem item = Bimp.tempSelectBitmap.get(0);
				Uri uri = null;
				if (Build.VERSION.SDK_INT >= 24) {
					uri = FileProvider.getUriForFile(mContext,
						  getPackageName() + AlbumConfig.FILE_PROVIDER_PACKAGE,
						  new File(item.imagePath));
				} else {
					uri = Uri.parse("file://" + item.imagePath);
				}
				UCrop uCrop = UCrop.of(uri, Uri.fromFile(PicFileUtil.getExternalDirFileName(mContext,
					  "jpg")));
				if (maxWidth > UCrop.MIN_SIZE && maxHeight > UCrop.MIN_SIZE) {
					uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
				}
				UCrop.Options options = new UCrop.Options();
				options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
				if (cb_original.isChecked()) {
					quality = 100;
				} else {
					if (quality == -1) {
						quality = 50;
					}
				}
				options.setCompressionQuality(quality);
				options.setHideBottomControls(true);
				options.setFreeStyleCropEnabled(true);
				uCrop.withOptions(options);
				uCrop.start(AlbumActivity.this);
			} else {
				ArrayList<String> paths = new ArrayList<>();
				for (ImageItem item : Bimp.tempSelectBitmap) {
					if (cb_original.isChecked()) {
						String imagePath = item.getImagePath();
						paths.add(imagePath);
					} else {
						File resizeFile = PicFileUtil.getExternalDirFileName(mContext, "jpg");
						PicFileUtil.resize(item.getBitmap(), resizeFile);
						paths.add(resizeFile.getPath());
					}
				}
				Intent data = new Intent();
				data.putStringArrayListExtra("paths", paths);
				setResult(RESULT_IMAGE, data);
				finish();
			}
		}
		
	}
	
	// 返回按钮监听
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			finish();
		}
	}
	
	// 相册按钮的监听
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			changeAlbum();
		}
	}
	
	/**
	 * 相册点击监听
	 *
	 * @author litfb
	 * @version 1.0
	 * @date 2014年10月13日
	 */
	private class AlbumOnItemClickListener implements AdapterView.OnItemClickListener {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// 隐藏列表
			hideAlbumList();
			// 旧相册取消选中
			aibum.setCurrentChoice(false);
			// 设置新的选中相册
			aibum = aibumList.get(position);
			// 设置选中
			aibum.setCurrentChoice(true);
			// 设标题
			headerTitle.setText(aibum.getName());
			List<YYPhotoItem> photoItems = aibum.getPhotoList();
			// 重设Adapter
			dataList.clear();
			for (int i = 0; i < photoItems.size(); i++) {
				YYPhotoItem yyPhotoItem = photoItems.get(i);
				ImageItem imageItem = new ImageItem();
				imageItem.setImagePath(yyPhotoItem.getPhotoPath());
				imageItem.setImageId(yyPhotoItem.getPhotoId() + "");
				dataList.add(imageItem);
			}
			Bimp.tempSelectBitmap.clear();
			isShowOkBt();
			gridImageAdapter = new AlbumGridViewAdapter(AlbumActivity.this, dataList,
				  Bimp.tempSelectBitmap);
			gridView.setAdapter(gridImageAdapter);
			initListener();
			
			// 通知变更
			albumAdapter.notifyDataSetChanged();
		}
		
	}
	
}
