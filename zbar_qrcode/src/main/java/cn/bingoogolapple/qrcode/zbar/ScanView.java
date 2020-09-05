package cn.bingoogolapple.qrcode.zbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.core.ScanResult;

public class ScanView
	  extends QRCodeView {
	static {
		System.loadLibrary("iconv");
	}
	
	private ImageScanner mScanner;
	private List<BarcodeFormat> mFormatList;
	private MultiFormatReader mMultiFormatReader;
	
	
	private Map<DecodeHintType, Object> mHintMap;
	
	
	public ScanView(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}
	
	
	public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupReader();
	}
	
	
	protected void setupReader() {
		this.mScanner = new ImageScanner();
		this.mScanner.setConfig(0, 256, 3);
		this.mScanner.setConfig(0, 257, 3);
		
		this.mScanner.setConfig(0, 0, 0);
		
		for (BarcodeFormat format : getFormats()) {
			this.mScanner.setConfig(format.getId(), 0, 1);
		}
		
		setupReaderZxing();
	}
	
	
	protected void setupReaderZxing() {
		this.mMultiFormatReader = new MultiFormatReader();
		
		if (this.mBarcodeType == BarcodeType.ONE_DIMENSION) {
			this.mMultiFormatReader.setHints(QRCodeDecoder.ONE_DIMENSION_HINT_MAP);
		} else if (this.mBarcodeType == BarcodeType.TWO_DIMENSION) {
			this.mMultiFormatReader.setHints(QRCodeDecoder.TWO_DIMENSION_HINT_MAP);
		} else if (this.mBarcodeType == BarcodeType.ONLY_QR_CODE) {
			this.mMultiFormatReader.setHints(QRCodeDecoder.QR_CODE_HINT_MAP);
		} else if (this.mBarcodeType == BarcodeType.ONLY_CODE_128) {
			this.mMultiFormatReader.setHints(QRCodeDecoder.CODE_128_HINT_MAP);
		} else if (this.mBarcodeType == BarcodeType.ONLY_EAN_13) {
			this.mMultiFormatReader.setHints(QRCodeDecoder.EAN_13_HINT_MAP);
		} else if (this.mBarcodeType == BarcodeType.HIGH_FREQUENCY) {
			this.mMultiFormatReader.setHints(QRCodeDecoder.HIGH_FREQUENCY_HINT_MAP);
		} else if (this.mBarcodeType == BarcodeType.CUSTOM) {
			this.mMultiFormatReader.setHints(this.mHintMap);
		} else {
			this.mMultiFormatReader.setHints(QRCodeDecoder.ALL_HINT_MAP);
		}
	}
	
	
	public Collection<BarcodeFormat> getFormats() {
		if (this.mBarcodeType == BarcodeType.ONE_DIMENSION)
			return BarcodeFormat.ONE_DIMENSION_FORMAT_LIST;
		if (this.mBarcodeType == BarcodeType.TWO_DIMENSION)
			return BarcodeFormat.TWO_DIMENSION_FORMAT_LIST;
		if (this.mBarcodeType == BarcodeType.ONLY_QR_CODE)
			return Collections.singletonList(BarcodeFormat.QRCODE);
		if (this.mBarcodeType == BarcodeType.ONLY_CODE_128)
			return Collections.singletonList(BarcodeFormat.CODE128);
		if (this.mBarcodeType == BarcodeType.ONLY_EAN_13)
			return Collections.singletonList(BarcodeFormat.EAN13);
		if (this.mBarcodeType == BarcodeType.HIGH_FREQUENCY)
			return BarcodeFormat.HIGH_FREQUENCY_FORMAT_LIST;
		if (this.mBarcodeType == BarcodeType.CUSTOM) {
			return this.mFormatList;
		}
		return BarcodeFormat.ALL_FORMAT_LIST;
	}
	
	
	public void setType(BarcodeType barcodeType, List<BarcodeFormat> formatList) {
		this.mBarcodeType = barcodeType;
		this.mFormatList = formatList;
		
		if (this.mBarcodeType == BarcodeType.CUSTOM && (this.mFormatList == null || this.mFormatList.isEmpty())) {
			throw new RuntimeException("barcodeType 为 BarcodeType.CUSTOM 时 formatList 不能为空");
		}
		setupReader();
	}
	
	
	protected ScanResult processData(byte[] data, int width, int height, boolean isRetry) {
		Image barcode = new Image(width, height, "Y800");
		
		Rect scanBoxAreaRect = this.mScanBoxView.getScanBoxAreaRect(height);
		if (scanBoxAreaRect != null && !isRetry && scanBoxAreaRect.left + scanBoxAreaRect.width() <= width && scanBoxAreaRect.top + scanBoxAreaRect
			  .height() <= height) {
			barcode.setCrop(scanBoxAreaRect.left, scanBoxAreaRect.top, scanBoxAreaRect.width(), scanBoxAreaRect.height());
		}
		
		barcode.setData(data);
		String result = processData(barcode);
		return new ScanResult(result);
	}
	
	private String processData(Image barcode) {
		if (this.mScanner.scanImage(barcode) == 0) {
			return null;
		}
		
		for (Symbol symbol : this.mScanner.getResults()) {
			String symData;
			if (symbol.getType() == 0) {
				continue;
			}
			
			
			if (Build.VERSION.SDK_INT >= 19) {
				symData = new String(symbol.getDataBytes(), StandardCharsets.UTF_8);
			} else {
				symData = symbol.getData();
			}
			
			if (TextUtils.isEmpty(symData)) {
				continue;
			}
			
			
			boolean isNeedAutoZoom = isNeedAutoZoom(symbol);
			if (isShowLocationPoint() || isNeedAutoZoom) {
				if (transformToViewCoordinates(symbol.getLocationPoints(), null, isNeedAutoZoom, symData)) {
					return null;
				}
				return symData;
			}
			
			return symData;
		}
		
		return null;
	}
	
	
	private boolean isNeedAutoZoom(Symbol symbol) {
		return (isAutoZoom() && symbol.getType() == 64);
	}
	
	
	protected ScanResult processBitmapData(Bitmap bitmap) {
		ScanResult result = null;
		try {
			String qrcontent = QRUtils.getInstance().decodeQRcode(bitmap);
			result = new ScanResult(qrcontent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	protected ScanResult processBitmapData(String imagePath) {
		ScanResult result = null;
		try {
			String qrcontent = QRUtils.getInstance().decodeQRcode(imagePath);
			result = new ScanResult(qrcontent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	protected ScanResult processZxingBitmapData(Bitmap bitmap) {
		return new ScanResult(QRCodeDecoder.syncDecodeQRCode(bitmap));
	}
	
	
	protected ScanResult processZxingData(byte[] data, int width, int height, boolean isRetry) {
		Result rawResult = null;
		Rect scanBoxAreaRect = null;
		
		try {
			PlanarYUVLuminanceSource source;
			scanBoxAreaRect = this.mScanBoxView.getScanBoxAreaRect(height);
			if (scanBoxAreaRect != null) {
				
				source = new PlanarYUVLuminanceSource(data, width, height, scanBoxAreaRect.left, scanBoxAreaRect.top, scanBoxAreaRect.width(), scanBoxAreaRect.height(), false);
			} else {
				source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
			}
			
			rawResult = this.mMultiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
			if (rawResult == null) {
				rawResult = this.mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
				if (rawResult != null) {
					BGAQRCodeUtil.d("GlobalHistogramBinarizer 没识别到，HybridBinarizer 能识别到");
				}
			}
		} catch (NoSuchMethodError error) {
			error.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.mMultiFormatReader.reset();
		}
		
		if (rawResult == null) {
			return null;
		}
		
		String result = rawResult.getText();
		if (TextUtils.isEmpty(result)) {
			return null;
		}
		
		com.google.zxing.BarcodeFormat barcodeFormat = rawResult.getBarcodeFormat();
		BGAQRCodeUtil.d("GlobalHistogramBinarizer 没识别到，HybridBinarizer 能识别到" + barcodeFormat.name());
		
		
		boolean isNeedAutoZoom = isNeedZxingAutoZoom(barcodeFormat);
		if (isShowLocationPoint() || isNeedAutoZoom) {
			ResultPoint[] resultPoints = rawResult.getResultPoints();
			PointF[] pointArr = new PointF[resultPoints.length];
			int pointIndex = 0;
			for (ResultPoint resultPoint : resultPoints) {
				pointArr[pointIndex] = new PointF(resultPoint.getX(), resultPoint.getY());
				pointIndex++;
			}
			
			if (transformToViewCoordinates(pointArr, scanBoxAreaRect, isNeedAutoZoom, result)) {
				return null;
			}
		}
		return new ScanResult(result);
	}
	
	
	private boolean isNeedZxingAutoZoom(com.google.zxing.BarcodeFormat barcodeFormat) {
		return (isAutoZoom() && barcodeFormat == com.google.zxing.BarcodeFormat.QR_CODE);
	}
	
	
	public void setAutoZoom(boolean autoZoom) {
		this.mScanBoxView.setAutoZoom(autoZoom);
	}
}
