package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

public class BGAQRCodeUtil {
	private static boolean debug = false;
	
	public static boolean isDebug() {
		return debug;
	}
	
	public static void setDebug(boolean debug) {
		BGAQRCodeUtil.debug = debug;
	}
	
	public static void d(String msg) {
		d("BGAQRCode", msg);
	}
	
	
	public static void printRect(String prefix, Rect rect) {
		d("BGAQRCodeFocusArea", prefix + " centerX:" + rect.centerX() + " centerY:" + rect.centerY() + " width:" + rect.width() + " height:" + rect.height() + " rectHalfWidth:" + (rect
			  .width() / 2) + " rectHalfHeight:" + (rect.height() / 2) + " left:" + rect.left + " top:" + rect.top + " right:" + rect.right + " bottom:" + rect.bottom);
	}
	
	
	public static void d(String tag, String msg) {
		if (debug) {
			Log.d(tag, msg);
		}
	}
	
	public static void e(String msg) {
		if (debug) {
			Log.e("BGAQRCode", msg);
		}
	}
	
	
	public static boolean isPortrait(Context context) {
		Point screenResolution = getScreenResolution(context);
		return (screenResolution.y > screenResolution.x);
	}
	
	static Point getScreenResolution(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point screenResolution = new Point();
		display.getSize(screenResolution);
		return screenResolution;
	}
	
	public static int getStatusBarHeight(Context context) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{16843277});
		
		
		boolean windowFullscreen = typedArray.getBoolean(0, false);
		typedArray.recycle();
		
		if (windowFullscreen) {
			return 0;
		}
		
		int height = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			height = context.getResources().getDimensionPixelSize(resourceId);
		}
		return height;
	}
	
	
	public static int dp2px(Context context, float dpValue) {
		return (int) TypedValue.applyDimension(1, dpValue, context.getResources().getDisplayMetrics());
	}
	
	
	public static int sp2px(Context context, float spValue) {
		return (int) TypedValue.applyDimension(2, spValue, context.getResources().getDisplayMetrics());
	}
	
	static Bitmap adjustPhotoRotation(Bitmap inputBitmap, int orientationDegree) {
		float outputY, outputX;
		if (inputBitmap == null) {
			return null;
		}
		
		Matrix matrix = new Matrix();
		matrix.setRotate(orientationDegree, inputBitmap.getWidth() / 2.0F, inputBitmap.getHeight() / 2.0F);
		
		if (orientationDegree == 90) {
			outputX = inputBitmap.getHeight();
			outputY = 0.0F;
		} else {
			outputX = inputBitmap.getHeight();
			outputY = inputBitmap.getWidth();
		}
		
		float[] values = new float[9];
		matrix.getValues(values);
		float x1 = values[2];
		float y1 = values[5];
		matrix.postTranslate(outputX - x1, outputY - y1);
		Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getHeight(), inputBitmap.getWidth(), Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(outputBitmap);
		canvas.drawBitmap(inputBitmap, matrix, paint);
		return outputBitmap;
	}
	
	static Bitmap makeTintBitmap(Bitmap inputBitmap, int tintColor) {
		if (inputBitmap == null) {
			return null;
		}
		
		Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), inputBitmap.getConfig());
		Canvas canvas = new Canvas(outputBitmap);
		Paint paint = new Paint();
		paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(inputBitmap, 0.0F, 0.0F, paint);
		return outputBitmap;
	}
	
	
	static Rect calculateFocusMeteringArea(float coefficient, float originFocusCenterX, float originFocusCenterY, int originFocusWidth, int originFocusHeight, int previewViewWidth, int previewViewHeight) {
		int halfFocusAreaWidth = (int) (originFocusWidth * coefficient / 2.0F);
		int halfFocusAreaHeight = (int) (originFocusHeight * coefficient / 2.0F);
		
		int centerX = (int) (originFocusCenterX / previewViewWidth * 2000.0F - 1000.0F);
		int centerY = (int) (originFocusCenterY / previewViewHeight * 2000.0F - 1000.0F);
		
		
		RectF rectF = new RectF(clamp(centerX - halfFocusAreaWidth, -1000, 1000), clamp(centerY - halfFocusAreaHeight, -1000, 1000), clamp(centerX + halfFocusAreaWidth, -1000, 1000), clamp(centerY + halfFocusAreaHeight, -1000, 1000));
		return new Rect(Math.round(rectF.left), Math.round(rectF.top),
			  Math.round(rectF.right), Math.round(rectF.bottom));
	}
	
	
	static int clamp(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}
	
	
	static float calculateFingerSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt((x * x + y * y));
	}
	
	
	public static Bitmap getDecodeAbleBitmap(String picturePath) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picturePath, options);
			int sampleSize = options.outHeight / 400;
			if (sampleSize <= 0) {
				sampleSize = 1;
			}
			options.inSampleSize = sampleSize;
			options.inJustDecodeBounds = false;
			
			return BitmapFactory.decodeFile(picturePath, options);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
