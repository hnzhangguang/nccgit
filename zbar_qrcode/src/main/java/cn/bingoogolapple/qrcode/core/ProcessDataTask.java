package cn.bingoogolapple.qrcode.core;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

class ProcessDataTask
	  extends AsyncTask<Void, Void, ScanResult> {
	private static long sLastStartTime = 0L;
	private Camera mCamera;
	private byte[] mData;
	private boolean mIsPortrait;
	private String mPicturePath;
	private Bitmap mBitmap;
	private WeakReference<QRCodeView> mQRCodeViewRef;
	
	ProcessDataTask(Camera camera, byte[] data, QRCodeView qrCodeView, boolean isPortrait) {
		this.mCamera = camera;
		this.mData = data;
		this.mQRCodeViewRef = new WeakReference(qrCodeView);
		this.mIsPortrait = isPortrait;
	}
	
	ProcessDataTask(String picturePath, QRCodeView qrCodeView) {
		this.mPicturePath = picturePath;
		this.mQRCodeViewRef = new WeakReference(qrCodeView);
	}
	
	ProcessDataTask(Bitmap bitmap, QRCodeView qrCodeView) {
		this.mBitmap = bitmap;
		this.mQRCodeViewRef = new WeakReference(qrCodeView);
	}
	
	ProcessDataTask perform() {
		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
		return this;
	}
	
	void cancelTask() {
		if (getStatus() != AsyncTask.Status.FINISHED) {
			cancel(true);
		}
	}
	
	
	protected void onCancelled() {
		super.onCancelled();
		this.mQRCodeViewRef.clear();
		this.mBitmap = null;
		this.mData = null;
	}
	
	private ScanResult processData(QRCodeView qrCodeView) {
		if (this.mData == null) {
			return null;
		}
		
		int width = 0;
		int height = 0;
		byte[] data = this.mData;
		try {
			Camera.Parameters parameters = this.mCamera.getParameters();
			Camera.Size size = parameters.getPreviewSize();
			width = size.width;
			height = size.height;
			
			if (this.mIsPortrait) {
				data = new byte[this.mData.length];
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						data[x * height + height - y - 1] = this.mData[x + y * width];
					}
				}
				int tmp = width;
				width = height;
				height = tmp;
			}
			
			ScanResult result = qrCodeView.processData(data, width, height, false);
			if (result == null || TextUtils.isEmpty(result.result)) {
				result = qrCodeView.processZxingData(data, width, height, false);
			}
			return result;
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				if (width != 0 && height != 0) {
					BGAQRCodeUtil.d("识别失败重试!");
					return qrCodeView.processData(data, width, height, true);
				}
				return null;
			} catch (Exception e2) {
				e2.printStackTrace();
				return null;
			}
		}
	}
	
	
	protected ScanResult doInBackground(Void... params) {
		QRCodeView qrCodeView = (QRCodeView) this.mQRCodeViewRef.get();
		if (qrCodeView == null) {
			return null;
		}
		
		if (this.mPicturePath != null) {
			ScanResult result = qrCodeView.processBitmapData(this.mPicturePath);
			if (result == null || TextUtils.isEmpty(result.result)) {
				result = qrCodeView.processZxingBitmapData(BGAQRCodeUtil.getDecodeAbleBitmap(this.mPicturePath));
			}
			return result;
		}
		if (this.mBitmap != null) {
			ScanResult result = qrCodeView.processBitmapData(this.mBitmap);
			if (result == null || TextUtils.isEmpty(result.result)) {
				result = qrCodeView.processZxingBitmapData(this.mBitmap);
			}
			this.mBitmap = null;
			return result;
		}
		if (BGAQRCodeUtil.isDebug()) {
			BGAQRCodeUtil.d("两次任务执行的时间间隔:" + (System.currentTimeMillis() - sLastStartTime));
			sLastStartTime = System.currentTimeMillis();
		}
		long startTime = System.currentTimeMillis();
		
		ScanResult scanResult = processData(qrCodeView);
		
		if (BGAQRCodeUtil.isDebug()) {
			long time = System.currentTimeMillis() - startTime;
			if (scanResult != null && !TextUtils.isEmpty(scanResult.result)) {
				BGAQRCodeUtil.d("识别成功时间为:" + time);
			} else {
				BGAQRCodeUtil.e("识别失败时间为:" + time);
			}
		}
		
		return scanResult;
	}
	
	
	protected void onPostExecute(ScanResult result) {
		QRCodeView qrCodeView = (QRCodeView) this.mQRCodeViewRef.get();
		if (qrCodeView == null) {
			return;
		}
		
		if (this.mPicturePath != null || this.mBitmap != null) {
			this.mBitmap = null;
			qrCodeView.onPostParseBitmapOrPicture(result);
		} else {
			qrCodeView.onPostParseData(result);
		}
	}
}
