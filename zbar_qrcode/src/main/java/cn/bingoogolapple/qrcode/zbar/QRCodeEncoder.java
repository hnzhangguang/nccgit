package cn.bingoogolapple.qrcode.zbar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yonyou.common.app.BaseApplication;
import com.yonyou.zbarqrcode.R;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public class QRCodeEncoder {
	public static final Map<EncodeHintType, Object> HINTS = new EnumMap(EncodeHintType.class);
	
	static {
		HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
		HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		HINTS.put(EncodeHintType.MARGIN, Integer.valueOf(0));
	}
	
	
	public static Bitmap syncEncodeQRCode(String content, int size) {
		return syncEncodeQRCode(content, size, BaseApplication.getBaseApp().getResources().getColor(R.color.color_424342), -1, null);
	}
	
	
	public static Bitmap syncEncodeQRCode(String content, int size, int foregroundColor) {
		return syncEncodeQRCode(content, size, foregroundColor, -1, null);
	}
	
	
	public static Bitmap syncEncodeQRCode(String content, int size, int foregroundColor, Bitmap logo) {
		return syncEncodeQRCode(content, size, foregroundColor, -1, logo);
	}
	
	
	public static Bitmap syncEncodeQRCode(String content, int size, int foregroundColor, int backgroundColor, Bitmap logo) {
		try {
			BitMatrix matrix = (new MultiFormatWriter()).encode(content, BarcodeFormat.QR_CODE, size, size, HINTS);
			int[] pixels = new int[size * size];
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					if (matrix.get(x, y)) {
						pixels[y * size + x] = foregroundColor;
					} else {
						pixels[y * size + x] = backgroundColor;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
			return addLogoToQRCode(bitmap, logo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static Bitmap addLogoToQRCode(Bitmap src, Bitmap logo) {
		if (src == null || logo == null) {
			return src;
		}
		
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();
		
		float scaleFactor = srcWidth * 1.0F / 5.0F / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0.0F, 0.0F, null);
			canvas.scale(scaleFactor, scaleFactor, (srcWidth / 2), (srcHeight / 2));
			canvas.drawBitmap(logo, ((srcWidth - logoWidth) / 2), ((srcHeight - logoHeight) / 2), null);
			canvas.save();
			canvas.restore();
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}
	
	
	public static Bitmap syncEncodeBarcode(String content, int width, int height, int textSize) {
		if (TextUtils.isEmpty(content)) {
			return null;
		}
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, Integer.valueOf(0));
		
		try {
			BitMatrix bitMatrix = (new MultiFormatWriter()).encode(content, BarcodeFormat.CODE_128, width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = -16777216;
					} else {
						pixels[y * width + x] = -1;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			if (textSize > 0) {
				bitmap = showContent(bitmap, content, textSize);
			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			
			
			return null;
		}
	}
	
	
	private static Bitmap showContent(Bitmap barcodeBitmap, String content, int textSize) {
		if (TextUtils.isEmpty(content) || null == barcodeBitmap) {
			return null;
		}
		Paint paint = new Paint();
		paint.setColor(-16777216);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(textSize);
		paint.setTextAlign(Paint.Align.CENTER);
		int textWidth = (int) paint.measureText(content);
		Paint.FontMetrics fm = paint.getFontMetrics();
		int textHeight = (int) (fm.bottom - fm.top);
		float scaleRateX = barcodeBitmap.getWidth() * 1.0F / textWidth;
		if (scaleRateX < 1.0F) {
			paint.setTextScaleX(scaleRateX);
		}
		int baseLine = barcodeBitmap.getHeight() + textHeight;
		Bitmap bitmap = Bitmap.createBitmap(barcodeBitmap.getWidth(), barcodeBitmap.getHeight() + 2 * textHeight, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas();
		canvas.drawColor(-1);
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(barcodeBitmap, 0.0F, 0.0F, null);
		canvas.drawText(content, (barcodeBitmap.getWidth() / 2), baseLine, paint);
		canvas.save();
		canvas.restore();
		return bitmap;
	}
}
