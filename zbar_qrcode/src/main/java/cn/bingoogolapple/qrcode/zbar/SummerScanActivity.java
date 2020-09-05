package cn.bingoogolapple.qrcode.zbar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yonyou.zbarqrcode.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bingoogolapple.qrcode.core.QRCodeView;


public class SummerScanActivity
	  extends Activity
	  implements QRCodeView.Delegate {
	public static final int REQUEST_CODE_CHOOSE_QRCODE = 333;
	public static final int RESULT_CODE_MANUAL_INPUT = 555;
	public static final int CAPTURE_TWO_DCODE = 222;
	public static final int RESULT_ERROR = 111;
	private static final String TAG = SummerScanActivity.class.getSimpleName();
	public static String EXTRA_QRVALUE = "result";
	public static String EXTRA_CLICK_ID = "clickId";
	private String webPath = "file:///android_asset/www/";
	private LinearLayout customLayout;
	private ScanView mZBarView;
	private boolean isOn = false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summer_scan);
		this.mZBarView = (ScanView) findViewById(R.id.zbarview);
		this.mZBarView.setDelegate(this);
		
		initView();
	}
	
	private void initView() {
		this.customLayout = (LinearLayout) findViewById(R.id.custom_layout);
		if (getIntent().getExtras() != null) {
			String intentValue = getIntent().getExtras().getString("params", "");
			this.webPath = getIntent().getExtras().getString("webPath", "");
			initCustomView(intentValue);
		}
	}
	
	private void initCustomView(String intentValue) {
		if (!TextUtils.isEmpty(intentValue)) {
			JSONObject json = new JSONObject();
			try {
				json = new JSONObject(intentValue);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			JSONArray array = json.optJSONArray("buttons");
			if (array != null) {
				if (this.customLayout.getVisibility() != View.VISIBLE) {
					this.customLayout.setVisibility(View.VISIBLE);
				}
				for (int i = 0; i < array.length(); i++) {
					JSONObject buttonJson = (JSONObject) array.opt(i);
					createView(buttonJson);
				}
			}
		}
	}
	
	
	private void createView(JSONObject buttonJson) {
		if (buttonJson == null) {
			return;
		}
		String image = buttonJson.optString("image");
		String id = buttonJson.optString("id");
		String title = buttonJson.optString("title");
		
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0F));
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(17);
		layout.setOnClickListener(new MyClickListener(buttonJson));
		layout.setTag(id);
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundColor(0);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		if (!TextUtils.isEmpty(this.webPath) && this.webPath.contains("/data")) {

//			Bitmap bitmap = YYBitmapUtil.getBitmap(this, this.webPath.substring(this.webPath.indexOf("/data")) + image);
//			if (bitmap != null) {
//				imageView.setImageBitmap(bitmap);
//			}
			Bitmap bitmap = BitmapFactory.decodeFile(this.webPath.substring(this.webPath.indexOf("/data")) + image);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
		
		layout.addView(imageView, 120, 120);
		
		if (!TextUtils.isEmpty(title)) {
			TextView textView = new TextView(this);
			textView.setPadding(0, 20, 0, 0);
			textView.setGravity(17);
			textView.setBackgroundColor(0);
			textView.setText(title);
			textView.setTextColor(-1);
			textView.setTextSize(15.0F);
			layout.addView(textView);
		}
		this.customLayout.addView(layout);
	}
	
	public void getImage(View v) {
		Intent intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 333);
	}
	
	
	public void onReturnClick(View v) {
		finish();
	}
	
	
	public void toggleFlash(View view) {
		switchLight(!isOn());
	}
	
	protected final boolean isOn() {
		return this.isOn;
	}
	
	protected void switchLight(boolean on) {
		if (on) {
			this.mZBarView.openFlashlight();
		} else {
			this.mZBarView.closeFlashlight();
		}
		this.isOn = on;
	}
	
	protected void onStart() {
		super.onStart();
		this.mZBarView.startCamera();
		
		
		this.mZBarView.startSpotAndShowRect();
	}
	
	protected void onStop() {
		this.mZBarView.stopCamera();
		super.onStop();
	}
	
	protected void onDestroy() {
		this.mZBarView.onDestroy();
		super.onDestroy();
	}
	
	private void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200L);
	}
	
	public void onScanQRCodeSuccess(String result) {
		Log.i(TAG, "result:" + result);
		vibrate();
		setResult(result);
	}
	
	private void setResult(String result) {
		if (!TextUtils.isEmpty(result)) {
			Intent reData = new Intent();
			reData.putExtra(EXTRA_QRVALUE, result);
			setResult(222, reData);
			finish();
		} else {
			Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onCameraAmbientBrightnessChanged(boolean isDark) {
		String tipText = this.mZBarView.getScanBoxView().getTipText();
		String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
		if (isDark) {
			if (!tipText.contains(ambientBrightnessTip)) {
				this.mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
			}
		} else if (tipText.contains(ambientBrightnessTip)) {
			tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
			this.mZBarView.getScanBoxView().setTipText(tipText);
		}
	}
	
	public void onScanQRCodeOpenCameraError() {
		Log.e(TAG, "打开相机出错");
	}
	
	public void onClick(View v) {
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.mZBarView.showScanRect();
		if (resultCode == -1 && requestCode == 333) {
			if (data == null) {
				return;
			}
			String picturePath = getUriToPath(this, data.getData());
			this.mZBarView.decodeQRCode(picturePath);
		}
	}
	
	private String getUriToPath(Context context, Uri uri) {
		String[] columns = {"_id", "_data"};
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(uri, columns, (String) null, (String[]) null, (String) null);
		if (cur == null)
			return uri.getPath();
		if (cur.moveToFirst()) {
			int photoPathIndex = cur.getColumnIndexOrThrow("_data");
			return cur.getString(photoPathIndex);
		}
		return "";
	}
	
	class MyClickListener
		  implements View.OnClickListener {
		private JSONObject json;
		
		public MyClickListener(JSONObject json) {
			this.json = json;
		}
		
		
		public void onClick(View v) {
			Intent intent = new Intent();
			if (this.json != null) {
				intent.putExtra(SummerScanActivity.EXTRA_CLICK_ID, this.json.toString());
			}
			SummerScanActivity.this.setResult(555, intent);
			SummerScanActivity.this.finish();
		}
	}
}
