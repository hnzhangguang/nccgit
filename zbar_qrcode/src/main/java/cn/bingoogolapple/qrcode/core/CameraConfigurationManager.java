package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;

import java.util.Collection;
import java.util.List;


final class CameraConfigurationManager {
	private final Context mContext;
	private Point mCameraResolution;
	private Point mPreviewResolution;
	
	CameraConfigurationManager(Context context) {
		this.mContext = context;
	}
	
	private static boolean autoFocusAble(Camera camera) {
		List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
		String focusMode = findSettableValue(supportedFocusModes, new String[]{"auto"});
		return (focusMode != null);
	}
	
	private static String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		return result;
	}
	
	private static Point getPreviewResolution(Camera.Parameters parameters, Point screenResolution) {
		Point previewResolution = findBestPreviewSizeValue(parameters.getSupportedPreviewSizes(), screenResolution);
		if (previewResolution == null) {
			previewResolution = new Point(screenResolution.x >> 3 << 3, screenResolution.y >> 3 << 3);
		}
		return previewResolution;
	}
	
	private static Point findBestPreviewSizeValue(List<Camera.Size> supportSizeList, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (Camera.Size previewSize : supportSizeList) {
			
			int newX = previewSize.width;
			int newY = previewSize.height;
			
			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			}
			if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}
		}
		
		
		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}
	
	void initFromCameraParameters(Camera camera) {
		Point screenResolution = BGAQRCodeUtil.getScreenResolution(this.mContext);
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;
		
		if (BGAQRCodeUtil.isPortrait(this.mContext)) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		
		this.mPreviewResolution = getPreviewResolution(camera.getParameters(), screenResolutionForCamera);
		
		if (BGAQRCodeUtil.isPortrait(this.mContext)) {
			this.mCameraResolution = new Point(this.mPreviewResolution.y, this.mPreviewResolution.x);
		} else {
			this.mCameraResolution = this.mPreviewResolution;
		}
	}
	
	Point getCameraResolution() {
		return this.mCameraResolution;
	}
	
	void setDesiredCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(this.mPreviewResolution.x, this.mPreviewResolution.y);
		
		
		int[] previewFpsRange = selectPreviewFpsRange(camera, 60.0F);
		if (previewFpsRange != null) {
			parameters.setPreviewFpsRange(previewFpsRange[0], previewFpsRange[1]);
		}
		
		
		camera.setDisplayOrientation(getDisplayOrientation());
		camera.setParameters(parameters);
	}
	
	private int[] selectPreviewFpsRange(Camera camera, float desiredPreviewFps) {
		int desiredPreviewFpsScaled = (int) (desiredPreviewFps * 1000.0F);
		
		
		int[] selectedFpsRange = null;
		int minDiff = Integer.MAX_VALUE;
		List<int[]> previewFpsRangeList = camera.getParameters().getSupportedPreviewFpsRange();
		for (int[] range : previewFpsRangeList) {
			int deltaMin = desiredPreviewFpsScaled - range[0];
			int deltaMax = desiredPreviewFpsScaled - range[1];
			int diff = Math.abs(deltaMin) + Math.abs(deltaMax);
			if (diff < minDiff) {
				selectedFpsRange = range;
				minDiff = diff;
			}
		}
		return selectedFpsRange;
	}
	
	void openFlashlight(Camera camera) {
		doSetTorch(camera, true);
	}
	
	void closeFlashlight(Camera camera) {
		doSetTorch(camera, false);
	}
	
	private void doSetTorch(Camera camera, boolean newSetting) {
		String flashMode;
		Camera.Parameters parameters = camera.getParameters();
		
		
		if (newSetting) {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), new String[]{"torch", "on"});
		} else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), new String[]{"off"});
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}
		camera.setParameters(parameters);
	}
	
	private int getDisplayOrientation() {
		int result;
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(0, info);
		WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
		if (wm == null) {
			return 0;
		}
		Display display = wm.getDefaultDisplay();
		
		int rotation = display.getRotation();
		int degrees = 0;
		switch (rotation) {
			case 0:
				degrees = 0;
				break;
			case 1:
				degrees = 90;
				break;
			case 2:
				degrees = 180;
				break;
			case 3:
				degrees = 270;
				break;
		}
		
		
		if (info.facing == 1) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}
}
