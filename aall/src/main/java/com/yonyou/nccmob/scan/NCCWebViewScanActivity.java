package com.yonyou.nccmob.scan;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bingoogolapple.qrcode.core.QRCodeView.Delegate;
import cn.bingoogolapple.qrcode.core.ScanViewParams;
import cn.bingoogolapple.qrcode.zbar.ScanView;
import cn.bingoogolapple.qrcode.zbar.listener.ResultListener;


/*
 * @功能: ncc 混合扫描界面
 * @参数  ;
 * @Date  2020/7/23;
 * @Author zhangg
 **/
public class NCCWebViewScanActivity extends BaseActivity implements Delegate {
	public static final int REQUEST_CODE_CHOOSE_QRCODE = 333;
	public static final int RESULT_CODE_MANUAL_INPUT = 555;
	public static final int CAPTURE_TWO_DCODE = 222;
	public static final int RESULT_ERROR = 111;
	private static final String TAG = "mmmm";
	public static String EXTRA_QRVALUE = "result";
	private static ResultListener resultListener;
	ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
	private ScanView mZBarView;
	private SoundPool soundPool;
	private boolean isOn = false;
	
	public NCCWebViewScanActivity() {
	}
	
	public static void setResultListener(ResultListener listener) {
		resultListener = listener;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_ncc_scan_webview);
		this.mZBarView = (ScanView) this.findViewById(R.id.zbarview);
		// flash 按钮隐藏掉
		findViewById(R.id.image_icon).setVisibility(View.GONE);
		
		this.mZBarView.setDelegate(this);
		Intent intent = this.getIntent();
		this.setScanView(intent);
	}
	
	private void setScanView(Intent intent) {
		this.soundPool = new SoundPool(10, 1, 5);
		this.soundPool.load(this, R.raw.nccqrcode, 1);
		long loopWaitTime = (long) intent.getIntExtra("loopWaitTime", 1);
		ScanViewParams.loopWaitTime = loopWaitTime * 1000L;
		boolean autoZoom = intent.getBooleanExtra("autoZoom", true);
		ScanViewParams.autoZoom = autoZoom;
		this.mZBarView.setAutoZoom(autoZoom);
		boolean vibrate = intent.getBooleanExtra("vibrate", true);
		ScanViewParams.vibrate = vibrate;
		boolean sound = intent.getBooleanExtra("sound", false);
		ScanViewParams.sound = sound;
		boolean loopScan = intent.getBooleanExtra("loopScan", false);
		ScanViewParams.loopScan = loopScan;
	}
	
	public void getImage(View v) {
		Intent intent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
		this.startActivityForResult(intent, 333);
	}
	
	public void onReturnClick(View v) {
		this.finish();
	}
	
	public void toggleFlash(View view) {
		this.switchLight(!this.isOn());
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
		this.soundPool.release();
		this.mZBarView.onDestroy();
		super.onDestroy();
	}
	
	private void vibrate() {
		Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		vibrator.vibrate(200L);
	}
	
	public void onScanQRCodeSuccess(String result) {
		Log.i(TAG, "result:" + result);
		if (ScanViewParams.vibrate) {
			this.vibrate();
		}
		
		if (ScanViewParams.sound) {
			this.soundPool.play(1, 1.0F, 1.0F, 0, 0, 1.0F);
		}
		
		if (ScanViewParams.loopScan) {
			if (resultListener != null) {
				resultListener.onSuccess(result);
			}
			
			this.reStartScan();
		} else {
			this.setResult(result);
		}
		
	}
	
	private void reStartScan() {
		this.singleThreadPool.execute(new Runnable() {
			public void run() {
				try {
					Thread.sleep(ScanViewParams.loopWaitTime);
					NCCWebViewScanActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							NCCWebViewScanActivity.this.mZBarView.startSpot();
						}
					});
				} catch (InterruptedException var2) {
					var2.printStackTrace();
				}
				
			}
		});
	}
	
	private void setResult(String result) {
		if (!TextUtils.isEmpty(result)) {
			Intent reData = new Intent();
			reData.putExtra(EXTRA_QRVALUE, result);
//            this.setResult(222, reData);
			LogerNcc.e(result);
			this.reStartScan();
//            this.finish();
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
			
			String picturePath = this.getUriToPath(this, data.getData());
			this.mZBarView.decodeQRCode(picturePath);
		}
		
	}
	
	private String getUriToPath(Context context, Uri uri) {
		String[] columns = new String[]{"_id", "_data"};
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(uri, columns, (String) null, (String[]) null, (String) null);
		if (cur == null) {
			return uri.getPath();
		} else if (cur.moveToFirst()) {
			int photoPathIndex = cur.getColumnIndexOrThrow("_data");
			return cur.getString(photoPathIndex);
		} else {
			return "";
		}
	}
}
