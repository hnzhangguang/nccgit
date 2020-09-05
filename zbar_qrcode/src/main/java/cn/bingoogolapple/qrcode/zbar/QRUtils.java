package cn.bingoogolapple.qrcode.zbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.File;
import java.util.Hashtable;


public class QRUtils {
	private static QRUtils instance;
	private Bitmap scanBitmap;
	private Context mContext;
	
	public static QRUtils getInstance() {
		if (instance == null) {
			instance = new QRUtils();
		}
		return instance;
	}
	
	
	public String decodeQRcode(String path) throws Exception {
		Bitmap qrbmp = compressImage(path);
		qrbmp = toGrayscale(qrbmp);
		if (qrbmp != null) {
			return decodeQRcode(qrbmp);
		}
		return "";
	}
	
	
	public String decodeQRcode(ImageView iv) throws Exception {
		Bitmap qrbmp = ((BitmapDrawable) iv.getDrawable()).getBitmap();
		if (qrbmp != null) {
			return decodeQRcode(qrbmp);
		}
		return "";
	}
	
	
	public String decodeQRcode(Bitmap barcodeBmp) throws Exception {
		int width = barcodeBmp.getWidth();
		int height = barcodeBmp.getHeight();
		int[] pixels = new int[width * height];
		barcodeBmp.getPixels(pixels, 0, width, 0, 0, width, height);
		Image barcode = new Image(width, height, "RGB4");
		barcode.setData(pixels);
		ImageScanner reader = new ImageScanner();
		reader.setConfig(0, 0, 0);
		reader.setConfig(64, 0, 1);
		int result = reader.scanImage(barcode.convert("Y800"));
		String qrCodeString = null;
		if (result != 0) {
			SymbolSet syms = reader.getResults();
			for (Symbol sym : syms) {
				qrCodeString = sym.getData();
			}
		}
		barcodeBmp.recycle();
		return qrCodeString;
	}
	
	
	public String decodeQRcodeByZxing(String path) throws Exception {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		Bitmap scanBitmap = compressImage(path);
		int[] data = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
		scanBitmap.getPixels(data, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), data);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(rgbLuminanceSource));
		QRCodeReader reader = new QRCodeReader();
		Result result = null;
		try {
			result = reader.decode(binaryBitmap, hints);
		} catch (NotFoundException notFoundException) {
		
		} catch (ChecksumException checksumException) {
		
		} catch (FormatException formatException) {
		}
		
		
		if (result == null) {
			return "";
		}
		return result.getText();
	}
	
	
	public String decodeQRcodeByZxing(Bitmap bitmap) throws Exception {
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		this.scanBitmap = bitmap;
		int[] data = new int[this.scanBitmap.getWidth() * this.scanBitmap.getHeight()];
		this.scanBitmap.getPixels(data, 0, this.scanBitmap.getWidth(), 0, 0, this.scanBitmap.getWidth(), this.scanBitmap.getHeight());
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(this.scanBitmap.getWidth(), this.scanBitmap.getHeight(), data);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(rgbLuminanceSource));
		QRCodeReader reader = new QRCodeReader();
		Result result = null;
		try {
			result = reader.decode(binaryBitmap, hints);
		} catch (NotFoundException notFoundException) {
		
		} catch (ChecksumException checksumException) {
		
		} catch (FormatException formatException) {
		}
		
		
		if (result == null) {
			return "";
		}
		return result.getText();
	}
	
	
	public String decodeBarcode(String url) throws Exception {
		Bitmap qrbmp = BitmapFactory.decodeFile(url);
		if (qrbmp != null) {
			return decodeBarcode(qrbmp);
		}
		return "";
	}
	
	
	public String decodeBarcode(ImageView iv) throws Exception {
		Bitmap qrbmp = ((BitmapDrawable) iv.getDrawable()).getBitmap();
		if (qrbmp != null) {
			return decodeBarcode(qrbmp);
		}
		return "";
	}
	
	
	public String decodeBarcode(Bitmap barcodeBmp) throws Exception {
		int width = barcodeBmp.getWidth();
		int height = barcodeBmp.getHeight();
		int[] pixels = new int[width * height];
		barcodeBmp.getPixels(pixels, 0, width, 0, 0, width, height);
		Image barcode = new Image(width, height, "RGB4");
		barcode.setData(pixels);
		ImageScanner reader = new ImageScanner();
		reader.setConfig(0, 0, 0);
		reader.setConfig(128, 0, 1);
		reader.setConfig(39, 0, 1);
		reader.setConfig(13, 0, 1);
		reader.setConfig(8, 0, 1);
		reader.setConfig(12, 0, 1);
		reader.setConfig(9, 0, 1);
		int result = reader.scanImage(barcode.convert("Y800"));
		String qrCodeString = null;
		if (result != 0) {
			SymbolSet syms = reader.getResults();
			for (Symbol sym : syms) {
				qrCodeString = sym.getData();
			}
		}
		return qrCodeString;
	}
	
	
	public Bitmap createQRCode(String content) {
		return createQRCode(content, 300, 300);
	}
	
	
	public Bitmap createQRCode(String content, int width, int height) {
		Bitmap bitmap = null;
		BitMatrix result = null;
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		try {
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.MARGIN, Integer.valueOf(1));
			result = multiFormatWriter.encode(new String(content.getBytes("UTF-8"), "ISO-8859-1"), BarcodeFormat.QR_CODE, width, height, hints);
			
			int w = result.getWidth();
			int h = result.getHeight();
			int[] pixels = new int[w * h];
			for (int y = 0; y < h; y++) {
				int offset = y * w;
				for (int x = 0; x < w; x++) {
					pixels[offset + x] = result.get(x, y) ? -16777216 : -1;
				}
			}
			bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	
	public Bitmap createQRCodeAddLogo(String content, Bitmap logo) {
		Bitmap qrCode = createQRCode(content);
		int qrheight = qrCode.getHeight();
		int qrwidth = qrCode.getWidth();
		int waterWidth = (int) (qrwidth * 0.3D);
		float scale = waterWidth / logo.getWidth();
		return createWaterMaskCenter(qrCode, zoomImg(logo, scale));
	}
	
	
	public Bitmap createQRCodeAddLogo(String content, int width, int height, Bitmap logo) {
		Bitmap qrCode = createQRCode(content, width, height);
		int qrheight = qrCode.getHeight();
		int qrwidth = qrCode.getWidth();
		int waterWidth = (int) (qrwidth * 0.3D);
		float scale = waterWidth / logo.getWidth();
		return createWaterMaskCenter(qrCode, zoomImg(logo, scale));
	}
	
	
	@Deprecated
	public Bitmap createBarcode(Context context, String contents, int desiredWidth, int desiredHeight) {
		if (TextUtils.isEmpty(contents)) {
			throw new NullPointerException("contents not be null");
		}
		if (desiredWidth == 0 || desiredHeight == 0) {
			throw new NullPointerException("desiredWidth or desiredHeight not be null");
		}
		
		
		BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
		
		return encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
	}
	
	
	public Bitmap createBarCodeWithText(Context context, String contents, int desiredWidth, int desiredHeight) {
		return createBarCodeWithText(context, contents, desiredWidth, desiredHeight, null);
	}
	
	
	public Bitmap createBarCodeWithText(Context context, String contents, int desiredWidth, int desiredHeight, TextViewConfig config) {
		if (TextUtils.isEmpty(contents)) {
			throw new NullPointerException("contents not be null");
		}
		if (desiredWidth == 0 || desiredHeight == 0) {
			throw new NullPointerException("desiredWidth or desiredHeight not be null");
		}
		
		
		BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
		
		Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
		
		
		Bitmap codeBitmap = createCodeBitmap(contents, barcodeBitmap.getWidth(), barcodeBitmap
			  .getHeight(), context, config);
		
		return mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0.0F, desiredHeight));
	}
	
	
	private Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) {
		int WHITE = -1;
		int BLACK = -16777216;
		
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = null;
		try {
			result = writer.encode(contents, format, desiredWidth, desiredHeight, null);
		} catch (WriterException e) {
			
			e.printStackTrace();
		}
		
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? -16777216 : -1;
			}
		}
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	
	private Bitmap createCodeBitmap(String contents, int width, int height, Context context, TextViewConfig config) {
		if (config == null) {
			config = new TextViewConfig();
		}
		TextView tv = new TextView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		
		tv.setLayoutParams(layoutParams);
		tv.setText(contents);
		tv.setTextSize((config.size == 0.0F) ? tv.getTextSize() : config.size);
		tv.setHeight(height);
		tv.setGravity(config.gravity);
		tv.setMaxLines(config.maxLines);
		tv.setWidth(width);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(config.color);
		tv.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
			  View.MeasureSpec.makeMeasureSpec(0, 0));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
		
		tv.buildDrawingCache();
		return tv.getDrawingCache();
	}
	
	private Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}
		
		int width = Math.max(first.getWidth(), second.getWidth());
		Bitmap newBitmap = Bitmap.createBitmap(width, first
			  
			  .getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0.0F, 0.0F, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save();
		cv.restore();
		
		return newBitmap;
	}
	
	private Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
		return createWaterMaskBitmap(src, watermark, (src
			  .getWidth() - watermark.getWidth()) / 2, (src
			  .getHeight() - watermark.getHeight()) / 2);
	}
	
	private Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
		if (src == null) {
			return null;
		}
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newb);
		canvas.drawBitmap(src, 0.0F, 0.0F, null);
		canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
		canvas.save();
		canvas.restore();
		return newb;
	}
	
	private Bitmap zoomImg(Bitmap bm, float f) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		
		float scaleWidth = f;
		float scaleHeight = f;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	}
	
	public boolean isMIUI() {
		String manufacturer = Build.MANUFACTURER;
		if ("xiaomi".equalsIgnoreCase(manufacturer)) {
			return true;
		}
		return false;
	}
	
	public int getScreenWidth(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService("window");
		Point point = new Point();
		if (Build.VERSION.SDK_INT >= 17) {
			
			wm.getDefaultDisplay().getRealSize(point);
		} else {
			
			wm.getDefaultDisplay().getSize(point);
		}
		return point.x;
	}
	
	public int getScreenHeight(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService("window");
		Point point = new Point();
		if (Build.VERSION.SDK_INT >= 17) {
			
			wm.getDefaultDisplay().getRealSize(point);
		} else {
			
			wm.getDefaultDisplay().getSize(point);
		}
		return point.y;
	}
	
	public boolean isScreenOriatationPortrait(Context context) {
		return ((context.getResources().getConfiguration()).orientation == 1);
	}
	
	public float getFingerSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt((x * x + y * y));
	}
	
	private Bitmap toGrayscale(Bitmap bmpOriginal) {
		int height = bmpOriginal.getHeight();
		int width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0.0F);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
		return bmpGrayscale;
	}
	
	private Bitmap compressImage(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		this.scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		int sampleSizeH = (int) (options.outHeight / 800.0F);
		int sampleSizeW = (int) (options.outWidth / 800.0F);
		int sampleSize = Math.max(sampleSizeH, sampleSizeW);
		if (sampleSize <= 0) {
			sampleSize = 1;
		}
		options.inSampleSize = sampleSize;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(path, options);
	}
	
	public boolean deleteTempFile(String delFile) {
		File file = new File(delFile);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			}
			return false;
		}
		
		return false;
	}
	
	@SuppressLint({"MissingPermission"})
	public void getVibrator(Context mContext) {
		Vibrator vibrator = (Vibrator) mContext.getSystemService("vibrator");
		long[] pattern = {0L, 50L, 0L, 0L};
		vibrator.vibrate(pattern, -1);
	}
	
	public static class TextViewConfig {
		private int gravity = 17;
		private int maxLines = 1;
		private int color = -16777216;
		
		
		private float size;
		
		
		public void setGravity(int gravity) {
			this.gravity = gravity;
		}
		
		
		public void setMaxLines(int maxLines) {
			this.maxLines = maxLines;
		}
		
		
		public void setColor(int color) {
			this.color = color;
		}
		
		
		public void setSize(float size) {
			this.size = size;
		}
	}
}
