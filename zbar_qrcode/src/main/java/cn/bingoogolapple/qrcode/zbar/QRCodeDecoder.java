package cn.bingoogolapple.qrcode.zbar;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;


public class QRCodeDecoder {
	
	static final Map<DecodeHintType, Object> ALL_HINT_MAP = new EnumMap(DecodeHintType.class);
	static final Map<DecodeHintType, Object> ONE_DIMENSION_HINT_MAP;
	static final Map<DecodeHintType, Object> TWO_DIMENSION_HINT_MAP;
	static final Map<DecodeHintType, Object> QR_CODE_HINT_MAP;
	static final Map<DecodeHintType, Object> CODE_128_HINT_MAP;
	static final Map<DecodeHintType, Object> EAN_13_HINT_MAP;
	static final Map<DecodeHintType, Object> HIGH_FREQUENCY_HINT_MAP;
	static ArrayList allFormatList = null;
	static ArrayList highFrequencyFormatList = null;
	static ArrayList twoDimenFormatList = null;
	
	static {
		allFormatList = new ArrayList();
		allFormatList.add(BarcodeFormat.AZTEC);
		allFormatList.add(BarcodeFormat.CODABAR);
		allFormatList.add(BarcodeFormat.CODE_39);
		allFormatList.add(BarcodeFormat.CODE_93);
		allFormatList.add(BarcodeFormat.CODE_128);
		allFormatList.add(BarcodeFormat.DATA_MATRIX);
		allFormatList.add(BarcodeFormat.EAN_8);
		allFormatList.add(BarcodeFormat.EAN_13);
		allFormatList.add(BarcodeFormat.ITF);
		allFormatList.add(BarcodeFormat.MAXICODE);
		allFormatList.add(BarcodeFormat.PDF_417);
		allFormatList.add(BarcodeFormat.QR_CODE);
		allFormatList.add(BarcodeFormat.RSS_14);
		allFormatList.add(BarcodeFormat.RSS_EXPANDED);
		allFormatList.add(BarcodeFormat.UPC_A);
		allFormatList.add(BarcodeFormat.UPC_E);
		allFormatList.add(BarcodeFormat.UPC_EAN_EXTENSION);
		
		
		ALL_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, allFormatList);
		
		ALL_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		
		
		ALL_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
		
		
		ONE_DIMENSION_HINT_MAP = new EnumMap(DecodeHintType.class);
		
		
		highFrequencyFormatList = new ArrayList();
		highFrequencyFormatList.add(BarcodeFormat.CODABAR);
		highFrequencyFormatList.add(BarcodeFormat.CODE_39);
		highFrequencyFormatList.add(BarcodeFormat.CODE_93);
		highFrequencyFormatList.add(BarcodeFormat.CODE_128);
		highFrequencyFormatList.add(BarcodeFormat.EAN_8);
		highFrequencyFormatList.add(BarcodeFormat.EAN_13);
		highFrequencyFormatList.add(BarcodeFormat.ITF);
		highFrequencyFormatList.add(BarcodeFormat.PDF_417);
		highFrequencyFormatList.add(BarcodeFormat.RSS_14);
		highFrequencyFormatList.add(BarcodeFormat.RSS_EXPANDED);
		highFrequencyFormatList.add(BarcodeFormat.UPC_A);
		highFrequencyFormatList.add(BarcodeFormat.UPC_E);
		highFrequencyFormatList.add(BarcodeFormat.UPC_EAN_EXTENSION);
		
		ONE_DIMENSION_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, highFrequencyFormatList);
		ONE_DIMENSION_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		ONE_DIMENSION_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
		
		
		TWO_DIMENSION_HINT_MAP = new EnumMap(DecodeHintType.class);
		
		
		twoDimenFormatList = new ArrayList();
		twoDimenFormatList.add(BarcodeFormat.AZTEC);
		twoDimenFormatList.add(BarcodeFormat.DATA_MATRIX);
		twoDimenFormatList.add(BarcodeFormat.MAXICODE);
		twoDimenFormatList.add(BarcodeFormat.QR_CODE);
		
		TWO_DIMENSION_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, twoDimenFormatList);
		TWO_DIMENSION_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		TWO_DIMENSION_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
		
		
		QR_CODE_HINT_MAP = new EnumMap(DecodeHintType.class);
		
		
		QR_CODE_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
		QR_CODE_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		QR_CODE_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
		
		
		CODE_128_HINT_MAP = new EnumMap(DecodeHintType.class);
		
		
		CODE_128_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.CODE_128));
		CODE_128_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		CODE_128_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
		
		
		EAN_13_HINT_MAP = new EnumMap(DecodeHintType.class);
		
		
		EAN_13_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.EAN_13));
		EAN_13_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		EAN_13_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
		
		
		HIGH_FREQUENCY_HINT_MAP = new EnumMap(DecodeHintType.class);
		
		
		twoDimenFormatList = new ArrayList();
		twoDimenFormatList.add(BarcodeFormat.QR_CODE);
		twoDimenFormatList.add(BarcodeFormat.UPC_A);
		twoDimenFormatList.add(BarcodeFormat.EAN_13);
		twoDimenFormatList.add(BarcodeFormat.CODE_128);
		
		HIGH_FREQUENCY_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, twoDimenFormatList);
		HIGH_FREQUENCY_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		HIGH_FREQUENCY_HINT_MAP.put(DecodeHintType.CHARACTER_SET, "utf-8");
	}
	
	public static String syncDecodeQRCode(String picturePath) {
		return syncDecodeQRCode(BGAQRCodeUtil.getDecodeAbleBitmap(picturePath));
	}
	
	
	public static String syncDecodeQRCode(Bitmap bitmap) {
		RGBLuminanceSource source = null;
		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			source = new RGBLuminanceSource(width, height, pixels);
			Result result = (new MultiFormatReader()).decode(new BinaryBitmap(new HybridBinarizer(source)), ALL_HINT_MAP);
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
			if (source != null) {
				try {
					Result result = (new MultiFormatReader()).decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)), ALL_HINT_MAP);
					return result.getText();
				} catch (Throwable e2) {
					e2.printStackTrace();
				}
			}
			return null;
		}
	}
}
