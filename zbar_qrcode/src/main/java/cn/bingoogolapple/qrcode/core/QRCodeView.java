package cn.bingoogolapple.qrcode.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yonyou.zbarqrcode.R;


public abstract class QRCodeView
	  extends RelativeLayout implements Camera.PreviewCallback {
	private static final int NO_CAMERA_ID = -1;
	private static final long[] AMBIENT_BRIGHTNESS_DARK_LIST = {255L, 255L, 255L, 255L};
	private static final int AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME = 150;
	private static final int AMBIENT_BRIGHTNESS_DARK = 60;
	protected Camera mCamera;
	protected CameraPreview mCameraPreview;
	protected ScanBoxView mScanBoxView;
	protected Delegate mDelegate;
	protected boolean mSpotAble = false;
	protected ProcessDataTask mProcessDataTask;
	protected int mCameraId = 0;
	protected BarcodeType mBarcodeType = BarcodeType.HIGH_FREQUENCY;
	private PointF[] mLocationPoints;
	private Paint mPaint;
	private long mLastPreviewFrameTime = 0L;
	private ValueAnimator mAutoZoomAnimator;
	private long mLastAutoZoomTime = 0L;
	private boolean allowAnalysis = true;
	private long mLastAmbientBrightnessRecordTime = System.currentTimeMillis();
	private int mAmbientBrightnessDarkIndex = 0;
	
	
	public QRCodeView(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}
	
	
	public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, attrs);
		setupReader();
	}
	
	private void initView(Context context, AttributeSet attrs) {
		this.mCameraPreview = new CameraPreview(context);
		this.mCameraPreview.setDelegate(new CameraPreview.Delegate() {
			public void onStartPreview() {
				QRCodeView.this.setOneShotPreviewCallback();
			}
		});
		
		this.mScanBoxView = new ScanBoxView(context);
		this.mScanBoxView.init(this, attrs);
		this.mCameraPreview.setId(R.id.bgaqrcode_camera_preview);
		addView(this.mCameraPreview);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(context, attrs);
		layoutParams.addRule(6, this.mCameraPreview.getId());
		layoutParams.addRule(8, this.mCameraPreview.getId());
		addView(this.mScanBoxView, layoutParams);
		
		this.mPaint = new Paint();
		this.mPaint.setColor(getScanBoxView().getCornerColor());
		this.mPaint.setStyle(Paint.Style.FILL);
	}
	
	private void setOneShotPreviewCallback() {
		if (this.mSpotAble && this.mCameraPreview.isPreviewing()) {
			try {
				this.mCamera.setOneShotPreviewCallback(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void setDelegate(Delegate delegate) {
		this.mDelegate = delegate;
	}
	
	
	public CameraPreview getCameraPreview() {
		return this.mCameraPreview;
	}
	
	
	public ScanBoxView getScanBoxView() {
		return this.mScanBoxView;
	}
	
	
	public void showScanRect() {
		if (this.mScanBoxView != null) {
			this.mScanBoxView.setVisibility(VISIBLE);
		}
	}
	
	
	public void hiddenScanRect() {
		if (this.mScanBoxView != null) {
			this.mScanBoxView.setVisibility(GONE);
		}
	}
	
	
	public void startCamera() {
		startCamera(this.mCameraId);
	}
	
	
	public void startCamera(int cameraFacing) {
		if (this.mCamera != null || Camera.getNumberOfCameras() == 0) {
			return;
		}
		int ultimateCameraId = findCameraIdByFacing(cameraFacing);
		if (ultimateCameraId != -1) {
			startCameraById(ultimateCameraId);
			
			return;
		}
		if (cameraFacing == 0) {
			ultimateCameraId = findCameraIdByFacing(1);
		} else if (cameraFacing == 1) {
			ultimateCameraId = findCameraIdByFacing(0);
		}
		if (ultimateCameraId != -1) {
			startCameraById(ultimateCameraId);
		}
	}
	
	private int findCameraIdByFacing(int cameraFacing) {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
			try {
				Camera.getCameraInfo(cameraId, cameraInfo);
				if (cameraInfo.facing == cameraFacing) {
					return cameraId;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	private void startCameraById(int cameraId) {
		try {
			this.mCameraId = cameraId;
			this.mCamera = Camera.open(cameraId);
			this.mCameraPreview.setCamera(this.mCamera);
		} catch (Exception e) {
			e.printStackTrace();
			if (this.mDelegate != null) {
				this.mDelegate.onScanQRCodeOpenCameraError();
			}
		}
	}
	
	
	public void stopCamera() {
		try {
			stopSpotAndHiddenRect();
			if (this.mCamera != null) {
				this.mCameraPreview.stopCameraPreview();
				this.mCameraPreview.setCamera(null);
				this.mCamera.release();
				this.mCamera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void startSpot() {
		this.mSpotAble = true;
		startCamera();
		setOneShotPreviewCallback();
	}
	
	
	public void stopSpot() {
		this.mSpotAble = false;
		
		if (this.mProcessDataTask != null) {
			this.mProcessDataTask.cancelTask();
			this.mProcessDataTask = null;
		}
		
		if (this.mCamera != null) {
			try {
				this.mCamera.setOneShotPreviewCallback(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void stopSpotAndHiddenRect() {
		stopSpot();
		hiddenScanRect();
	}
	
	
	public void startSpotAndShowRect() {
		startSpot();
		showScanRect();
	}
	
	
	public void openFlashlight() {
		postDelayed(new Runnable() {
			public void run() {
				QRCodeView.this.mCameraPreview.openFlashlight();
			}
		}, this.mCameraPreview.isPreviewing() ? 0L : 500L);
	}
	
	
	public void closeFlashlight() {
		this.mCameraPreview.closeFlashlight();
	}
	
	
	public void onDestroy() {
		stopCamera();
		this.mDelegate = null;
	}
	
	
	public void changeToScanBarcodeStyle() {
		if (!this.mScanBoxView.getIsBarcode()) {
			this.mScanBoxView.setIsBarcode(true);
		}
	}
	
	
	public void changeToScanQRCodeStyle() {
		if (this.mScanBoxView.getIsBarcode()) {
			this.mScanBoxView.setIsBarcode(false);
		}
	}
	
	
	public boolean getIsScanBarcodeStyle() {
		return this.mScanBoxView.getIsBarcode();
	}
	
	
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (BGAQRCodeUtil.isDebug()) {
			BGAQRCodeUtil.d("两次 onPreviewFrame 时间间隔" + (System.currentTimeMillis() - this.mLastPreviewFrameTime));
			this.mLastPreviewFrameTime = System.currentTimeMillis();
		}
		
		if (this.mCameraPreview != null && this.mCameraPreview.isPreviewing()) {
			try {
				handleAmbientBrightness(data, camera);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (!this.mSpotAble || (this.mProcessDataTask != null && (this.mProcessDataTask.getStatus() == AsyncTask.Status.PENDING || this.mProcessDataTask
			  .getStatus() == AsyncTask.Status.RUNNING))) {
			return;
		}
		
		this.mProcessDataTask = (new ProcessDataTask(camera, data, this, BGAQRCodeUtil.isPortrait(getContext()))).perform();
	}
	
	private void handleAmbientBrightness(byte[] data, Camera camera) {
		if (this.mCameraPreview == null || !this.mCameraPreview.isPreviewing()) {
			return;
		}
		long currentTime = System.currentTimeMillis();
		if (currentTime - this.mLastAmbientBrightnessRecordTime < 150L) {
			return;
		}
		this.mLastAmbientBrightnessRecordTime = currentTime;
		
		int width = (camera.getParameters().getPreviewSize()).width;
		int height = (camera.getParameters().getPreviewSize()).height;
		
		long pixelLightCount = 0L;
		
		long pixelCount = (width * height);
		
		int step = 10;
		
		
		if (Math.abs(data.length - (float) pixelCount * 1.5F) < 1.0E-5F) {
			int i;
			for (i = 0; i < pixelCount; i += step) {
				
				pixelLightCount += (data[i] & 0xFFL);
			}
			
			long cameraLight = pixelLightCount / pixelCount / step;
			
			int lightSize = AMBIENT_BRIGHTNESS_DARK_LIST.length;
			AMBIENT_BRIGHTNESS_DARK_LIST[this.mAmbientBrightnessDarkIndex %= lightSize] = cameraLight;
			this.mAmbientBrightnessDarkIndex++;
			boolean isDarkEnv = true;
			
			for (long ambientBrightness : AMBIENT_BRIGHTNESS_DARK_LIST) {
				if (ambientBrightness > 60L) {
					isDarkEnv = false;
					break;
				}
			}
			BGAQRCodeUtil.d("摄像头环境亮度为: " + cameraLight);
			if (this.mDelegate != null) {
				this.mDelegate.onCameraAmbientBrightnessChanged(isDarkEnv);
			}
		}
	}
	
	
	public void decodeQRCode(String picturePath) {
		this.mProcessDataTask = (new ProcessDataTask(picturePath, this)).perform();
	}
	
	
	public void decodeQRCode(Bitmap bitmap) {
		this.mProcessDataTask = (new ProcessDataTask(bitmap, this)).perform();
	}
	
	
	void onPostParseData(ScanResult scanResult) {
		if (!this.mSpotAble) {
			return;
		}
		String result = (scanResult == null) ? null : scanResult.result;
		if (TextUtils.isEmpty(result)) {
			try {
				if (this.mCamera != null) {
					this.mCamera.setOneShotPreviewCallback(this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.mSpotAble = false;
			try {
				if (this.mDelegate != null) {
					this.mDelegate.onScanQRCodeSuccess(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void onPostParseBitmapOrPicture(ScanResult scanResult) {
		if (this.mDelegate != null) {
			String result = (scanResult == null) ? null : scanResult.result;
			this.mDelegate.onScanQRCodeSuccess(result);
		}
	}
	
	
	void onScanBoxRectChanged(Rect rect) {
		this.mCameraPreview.onScanBoxRectChanged(rect);
	}
	
	
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		
		if (!isShowLocationPoint() || this.mLocationPoints == null) {
			return;
		}
		
		for (PointF pointF : this.mLocationPoints) {
			canvas.drawCircle(pointF.x, pointF.y, 10.0F, this.mPaint);
		}
		this.mLocationPoints = null;
		postInvalidateDelayed(2000L);
	}
	
	
	protected boolean isShowLocationPoint() {
		return (this.mScanBoxView != null && this.mScanBoxView.isShowLocationPoint());
	}
	
	
	protected boolean isAutoZoom() {
		return (this.mScanBoxView != null && this.mScanBoxView.isAutoZoom());
	}
	
	
	protected boolean transformToViewCoordinates(PointF[] pointArr, Rect scanBoxAreaRect, boolean isNeedAutoZoom, String result) {
		if (pointArr == null || pointArr.length == 0) {
			return false;
		}
		
		
		try {
			Camera.Size size = this.mCamera.getParameters().getPreviewSize();
			boolean isMirrorPreview = (this.mCameraId == 1);
			int statusBarHeight = BGAQRCodeUtil.getStatusBarHeight(getContext());
			
			PointF[] transformedPoints = new PointF[pointArr.length];
			int index = 0;
			for (PointF qrPoint : pointArr) {
				transformedPoints[index] = transform(qrPoint.x, qrPoint.y, size.width, size.height, isMirrorPreview, statusBarHeight, scanBoxAreaRect);
				index++;
			}
			this.mLocationPoints = transformedPoints;
			postInvalidate();
			
			if (isNeedAutoZoom) {
				return handleAutoZoom(transformedPoints, result);
			}
			return false;
		} catch (Exception e) {
			this.mLocationPoints = null;
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean handleAutoZoom(PointF[] locationPoints, final String result) {
		if (this.mCamera == null || this.mScanBoxView == null) {
			return false;
		}
		if (locationPoints == null || locationPoints.length < 1) {
			return false;
		}
		if (this.mAutoZoomAnimator != null && this.mAutoZoomAnimator.isRunning()) {
			return true;
		}
		if (System.currentTimeMillis() - this.mLastAutoZoomTime < 1200L) {
			return true;
		}
		Camera.Parameters parameters = this.mCamera.getParameters();
		if (!parameters.isZoomSupported()) {
			return false;
		}
		
		float point1X = (locationPoints[0]).x;
		float point1Y = (locationPoints[0]).y;
		float point2X = (locationPoints[1]).x;
		float point2Y = (locationPoints[1]).y;
		float xLen = Math.abs(point1X - point2X);
		float yLen = Math.abs(point1Y - point2Y);
		int len = (int) Math.sqrt((xLen * xLen + yLen * yLen));
		
		int scanBoxWidth = this.mScanBoxView.getRectWidth();
		if (len > scanBoxWidth / 4) {
			return false;
		}
		
		final int maxZoom = parameters.getMaxZoom();
		final int zoomStep = maxZoom / 4;
		final int zoom = parameters.getZoom();
		post(new Runnable() {
			public void run() {
				QRCodeView.this.startAutoZoom(zoom, Math.min(zoom + zoomStep, maxZoom), result);
			}
		});
		return true;
	}
	
	private void startAutoZoom(int oldZoom, int newZoom, final String result) {
		this.mAutoZoomAnimator = ValueAnimator.ofInt(new int[]{oldZoom, newZoom});
		this.mAutoZoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				if (QRCodeView.this.mCameraPreview == null || !QRCodeView.this.mCameraPreview.isPreviewing()) {
					return;
				}
				int zoom = ((Integer) animation.getAnimatedValue()).intValue();
				Camera.Parameters parameters = QRCodeView.this.mCamera.getParameters();
				parameters.setZoom(zoom);
				QRCodeView.this.mCamera.setParameters(parameters);
			}
		});
		this.mAutoZoomAnimator.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				QRCodeView.this.onPostParseData(new ScanResult(result));
			}
		});
		this.mAutoZoomAnimator.setDuration(600L);
		this.mAutoZoomAnimator.setRepeatCount(0);
		this.mAutoZoomAnimator.start();
		this.mLastAutoZoomTime = System.currentTimeMillis();
	}
	
	
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (this.mAutoZoomAnimator != null) {
			this.mAutoZoomAnimator.cancel();
		}
	}
	
	private PointF transform(float originX, float originY, float cameraPreviewWidth, float cameraPreviewHeight, boolean isMirrorPreview, int statusBarHeight, Rect scanBoxAreaRect) {
		PointF result;
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		
		
		if (BGAQRCodeUtil.isPortrait(getContext())) {
			float scaleX = viewWidth / cameraPreviewHeight;
			float scaleY = viewHeight / cameraPreviewWidth;
			result = new PointF((cameraPreviewHeight - originX) * scaleX, (cameraPreviewWidth - originY) * scaleY);
			result.y = viewHeight - result.y;
			result.x = viewWidth - result.x;
			
			if (scanBoxAreaRect == null) {
				result.y += statusBarHeight;
			}
		} else {
			float scaleX = viewWidth / cameraPreviewWidth;
			float scaleY = viewHeight / cameraPreviewHeight;
			result = new PointF(originX * scaleX, originY * scaleY);
			if (isMirrorPreview) {
				result.x = viewWidth - result.x;
			}
		}
		
		if (scanBoxAreaRect != null) {
			result.y += scanBoxAreaRect.top;
			result.x += scanBoxAreaRect.left;
		}
		
		return result;
	}
	
	protected abstract void setupReader();
	
	protected abstract ScanResult processData(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);
	
	protected abstract ScanResult processZxingData(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);
	
	protected abstract ScanResult processBitmapData(String paramString);
	
	protected abstract ScanResult processBitmapData(Bitmap paramBitmap);
	
	protected abstract ScanResult processZxingBitmapData(Bitmap paramBitmap);
	
	public static interface Delegate {
		void onScanQRCodeSuccess(String param1String);
		
		void onCameraAmbientBrightnessChanged(boolean param1Boolean);
		
		void onScanQRCodeOpenCameraError();
	}
}
