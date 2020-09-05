package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Collections;

public class CameraPreview
	  extends SurfaceView implements SurfaceHolder.Callback {
	private Camera mCamera;
	private boolean mPreviewing = false;
	private boolean mSurfaceCreated = false;
	private boolean mIsTouchFocusing = false;
	private float mOldDist = 1.0F;
	private CameraConfigurationManager mCameraConfigurationManager;
	private Delegate mDelegate;
	
	public CameraPreview(Context context) {
		super(context);
		getHolder().addCallback(this);
	}
	
	private static void handleZoom(boolean isZoomIn, Camera camera) {
		Camera.Parameters params = camera.getParameters();
		if (params.isZoomSupported()) {
			int zoom = params.getZoom();
			if (isZoomIn && zoom < params.getMaxZoom()) {
				BGAQRCodeUtil.d("放大");
				zoom++;
			} else if (!isZoomIn && zoom > 0) {
				BGAQRCodeUtil.d("缩小");
				zoom--;
			} else {
				BGAQRCodeUtil.d("既不放大也不缩小");
			}
			params.setZoom(zoom);
			camera.setParameters(params);
		} else {
			BGAQRCodeUtil.d("不支持缩放");
		}
	}
	
	void setCamera(Camera camera) {
		this.mCamera = camera;
		if (this.mCamera != null) {
			this.mCameraConfigurationManager = new CameraConfigurationManager(getContext());
			this.mCameraConfigurationManager.initFromCameraParameters(this.mCamera);
			
			if (this.mPreviewing) {
				requestLayout();
			} else {
				showCameraPreview();
			}
		}
	}
	
	void setDelegate(Delegate delegate) {
		this.mDelegate = delegate;
	}
	
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		this.mSurfaceCreated = true;
	}
	
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		if (surfaceHolder.getSurface() == null) {
			return;
		}
		stopCameraPreview();
		showCameraPreview();
	}
	
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		this.mSurfaceCreated = false;
		stopCameraPreview();
	}
	
	public void reactNativeShowCameraPreview() {
		if (getHolder() == null || getHolder().getSurface() == null) {
			return;
		}
		
		stopCameraPreview();
		showCameraPreview();
	}
	
	private void showCameraPreview() {
		if (this.mCamera != null) {
			try {
				this.mPreviewing = true;
				SurfaceHolder surfaceHolder = getHolder();
				surfaceHolder.setKeepScreenOn(true);
				this.mCamera.setPreviewDisplay(surfaceHolder);
				
				this.mCameraConfigurationManager.setDesiredCameraParameters(this.mCamera);
				this.mCamera.startPreview();
				if (this.mDelegate != null) {
					this.mDelegate.onStartPreview();
				}
				startContinuousAutoFocus();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void stopCameraPreview() {
		if (this.mCamera != null) {
			try {
				this.mPreviewing = false;
				this.mCamera.cancelAutoFocus();
				this.mCamera.setOneShotPreviewCallback(null);
				this.mCamera.stopPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void openFlashlight() {
		if (flashLightAvailable()) {
			this.mCameraConfigurationManager.openFlashlight(this.mCamera);
		}
	}
	
	void closeFlashlight() {
		if (flashLightAvailable()) {
			this.mCameraConfigurationManager.closeFlashlight(this.mCamera);
		}
	}
	
	private boolean flashLightAvailable() {
		return (isPreviewing() && getContext().getPackageManager().hasSystemFeature("android.hardware.camera.flash"));
	}
	
	void onScanBoxRectChanged(Rect scanRect) {
		if (this.mCamera == null || scanRect == null || scanRect.left <= 0 || scanRect.top <= 0) {
			return;
		}
		int centerX = scanRect.centerX();
		int centerY = scanRect.centerY();
		int rectHalfWidth = scanRect.width() / 2;
		int rectHalfHeight = scanRect.height() / 2;
		
		BGAQRCodeUtil.printRect("转换前:", scanRect);
		
		if (BGAQRCodeUtil.isPortrait(getContext())) {
			int temp = centerX;
			centerX = centerY;
			centerY = temp;
			
			temp = rectHalfWidth;
			rectHalfWidth = rectHalfHeight;
			rectHalfHeight = temp;
		}
		scanRect = new Rect(centerX - rectHalfWidth, centerY - rectHalfHeight, centerX + rectHalfWidth, centerY + rectHalfHeight);
		BGAQRCodeUtil.printRect("转换后:", scanRect);
		
		BGAQRCodeUtil.d("扫码框发生变化触发对焦测光");
		handleFocusMetering(scanRect.centerX(), scanRect.centerY(), scanRect.width(), scanRect.height());
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if (!isPreviewing()) {
			return super.onTouchEvent(event);
		}
		
		if (event.getPointerCount() == 1 && (event.getAction() & 0xFF) == 1) {
			if (this.mIsTouchFocusing) {
				return true;
			}
			this.mIsTouchFocusing = true;
			BGAQRCodeUtil.d("手指触摸触发对焦测光");
			float centerX = event.getX();
			float centerY = event.getY();
			if (BGAQRCodeUtil.isPortrait(getContext())) {
				float temp = centerX;
				centerX = centerY;
				centerY = temp;
			}
			int focusSize = BGAQRCodeUtil.dp2px(getContext(), 120.0F);
			handleFocusMetering(centerX, centerY, focusSize, focusSize);
		}
		
		if (event.getPointerCount() == 2) {
			float newDist;
			switch (event.getAction() & 0xFF) {
				case 5:
					this.mOldDist = BGAQRCodeUtil.calculateFingerSpacing(event);
					break;
				case 2:
					newDist = BGAQRCodeUtil.calculateFingerSpacing(event);
					if (newDist > this.mOldDist) {
						handleZoom(true, this.mCamera);
						break;
					}
					if (newDist < this.mOldDist) {
						handleZoom(false, this.mCamera);
					}
					break;
			}
		}
		return true;
	}
	
	private void handleFocusMetering(float originFocusCenterX, float originFocusCenterY, int originFocusWidth, int originFocusHeight) {
		try {
			boolean isNeedUpdate = false;
			Camera.Parameters focusMeteringParameters = this.mCamera.getParameters();
			Camera.Size size = focusMeteringParameters.getPreviewSize();
			if (focusMeteringParameters.getMaxNumFocusAreas() > 0) {
				BGAQRCodeUtil.d("支持设置对焦区域");
				isNeedUpdate = true;
				Rect focusRect = BGAQRCodeUtil.calculateFocusMeteringArea(1.0F, originFocusCenterX, originFocusCenterY, originFocusWidth, originFocusHeight, size.width, size.height);
				
				
				BGAQRCodeUtil.printRect("对焦区域", focusRect);
				focusMeteringParameters.setFocusAreas(Collections.singletonList(new Camera.Area(focusRect, 1000)));
				focusMeteringParameters.setFocusMode("macro");
			} else {
				BGAQRCodeUtil.d("不支持设置对焦区域");
			}
			
			if (focusMeteringParameters.getMaxNumMeteringAreas() > 0) {
				BGAQRCodeUtil.d("支持设置测光区域");
				isNeedUpdate = true;
				Rect meteringRect = BGAQRCodeUtil.calculateFocusMeteringArea(1.5F, originFocusCenterX, originFocusCenterY, originFocusWidth, originFocusHeight, size.width, size.height);
				
				
				BGAQRCodeUtil.printRect("测光区域", meteringRect);
				focusMeteringParameters.setMeteringAreas(Collections.singletonList(new Camera.Area(meteringRect, 1000)));
			} else {
				BGAQRCodeUtil.d("不支持设置测光区域");
			}
			
			if (isNeedUpdate) {
				this.mCamera.cancelAutoFocus();
				this.mCamera.setParameters(focusMeteringParameters);
				this.mCamera.autoFocus(new Camera.AutoFocusCallback() {
					public void onAutoFocus(boolean success, Camera camera) {
						if (success) {
							BGAQRCodeUtil.d("对焦测光成功");
						} else {
							BGAQRCodeUtil.e("对焦测光失败");
						}
						CameraPreview.this.startContinuousAutoFocus();
					}
				});
			} else {
				this.mIsTouchFocusing = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			BGAQRCodeUtil.e("对焦测光失败:" + e.getMessage());
			startContinuousAutoFocus();
		}
	}
	
	private void startContinuousAutoFocus() {
		this.mIsTouchFocusing = false;
		if (this.mCamera == null) {
			return;
		}
		try {
			Camera.Parameters parameters = this.mCamera.getParameters();
			
			parameters.setFocusMode("continuous-picture");
			this.mCamera.setParameters(parameters);
			
			this.mCamera.cancelAutoFocus();
		} catch (Exception e) {
			BGAQRCodeUtil.e("连续对焦失败");
		}
	}
	
	boolean isPreviewing() {
		return (this.mCamera != null && this.mPreviewing && this.mSurfaceCreated);
	}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		if (this.mCameraConfigurationManager != null && this.mCameraConfigurationManager.getCameraResolution() != null) {
			Point cameraResolution = this.mCameraConfigurationManager.getCameraResolution();
			
			int cameraPreviewWidth = cameraResolution.x;
			int cameraPreviewHeight = cameraResolution.y;
			if (width * 1.0F / height < cameraPreviewWidth * 1.0F / cameraPreviewHeight) {
				float ratio = cameraPreviewHeight * 1.0F / cameraPreviewWidth;
				width = (int) (height / ratio + 0.5F);
			} else {
				float ratio = cameraPreviewWidth * 1.0F / cameraPreviewHeight;
				height = (int) (width / ratio + 0.5F);
			}
		}
		super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
	}
	
	
	static interface Delegate {
		void onStartPreview();
	}
}
