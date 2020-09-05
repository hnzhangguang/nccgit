package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yonyou.zbarqrcode.R;


public class ScanBoxView
	  extends View {
	private int mMoveStepDistance;
	private int mAnimDelayTime;
	private Rect mFramingRect;
	private float mScanLineTop;
	private float mScanLineLeft;
	private Paint mPaint;
	private TextPaint mTipPaint;
	private int mMaskColor;
	private int mCornerColor;
	private int mCornerLength;
	private int mCornerSize;
	private int mRectWidth;
	private int mRectHeight;
	private int mBarcodeRectHeight;
	private int mTopOffset;
	private int mScanLineSize;
	private int mScanLineColor;
	private int mScanLineMargin;
	private boolean mIsShowDefaultScanLineDrawable;
	private Drawable mCustomScanLineDrawable;
	private Bitmap mScanLineBitmap;
	private int mBorderSize;
	private int mBorderColor;
	private int mAnimTime;
	private float mVerticalBias;
	private int mCornerDisplayType;
	private int mToolbarHeight;
	private boolean mIsBarcode;
	private String mQRCodeTipText;
	private String mBarCodeTipText;
	private String mTipText;
	private int mTipTextSize;
	private int mTipTextColor;
	private boolean mIsTipTextBelowRect;
	private int mTipTextMargin;
	private boolean mIsShowTipTextAsSingleLine;
	private int mTipBackgroundColor;
	private boolean mIsShowTipBackground;
	private boolean mIsScanLineReverse;
	private boolean mIsShowDefaultGridScanLineDrawable;
	private Drawable mCustomGridScanLineDrawable;
	private Bitmap mGridScanLineBitmap;
	private float mGridScanLineBottom;
	private float mGridScanLineRight;
	private Bitmap mOriginQRCodeScanLineBitmap;
	private Bitmap mOriginBarCodeScanLineBitmap;
	private Bitmap mOriginQRCodeGridScanLineBitmap;
	private Bitmap mOriginBarCodeGridScanLineBitmap;
	private float mHalfCornerSize;
	private StaticLayout mTipTextSl;
	private int mTipBackgroundRadius;
	private boolean mIsOnlyDecodeScanBoxArea;
	private boolean mIsShowLocationPoint;
	private boolean mIsAutoZoom;
	private QRCodeView mQRCodeView;
	
	public ScanBoxView(Context context) {
		super(context);
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mMaskColor = Color.parseColor("#33FFFFFF");
		this.mCornerColor = -1;
		this.mCornerLength = BGAQRCodeUtil.dp2px(context, 20.0F);
		this.mCornerSize = BGAQRCodeUtil.dp2px(context, 3.0F);
		this.mScanLineSize = BGAQRCodeUtil.dp2px(context, 1.0F);
		this.mScanLineColor = -1;
		this.mTopOffset = BGAQRCodeUtil.dp2px(context, 90.0F);
		this.mRectWidth = BGAQRCodeUtil.dp2px(context, 200.0F);
		this.mBarcodeRectHeight = BGAQRCodeUtil.dp2px(context, 140.0F);
		this.mScanLineMargin = 0;
		this.mIsShowDefaultScanLineDrawable = false;
		this.mCustomScanLineDrawable = null;
		this.mScanLineBitmap = null;
		this.mBorderSize = BGAQRCodeUtil.dp2px(context, 1.0F);
		this.mBorderColor = -1;
		this.mAnimTime = 1000;
		this.mVerticalBias = -1.0F;
		this.mCornerDisplayType = 1;
		this.mToolbarHeight = 0;
		this.mIsBarcode = false;
		this.mMoveStepDistance = BGAQRCodeUtil.dp2px(context, 2.0F);
		this.mTipText = null;
		this.mTipTextSize = BGAQRCodeUtil.sp2px(context, 14.0F);
		this.mTipTextColor = -1;
		this.mIsTipTextBelowRect = false;
		this.mTipTextMargin = BGAQRCodeUtil.dp2px(context, 20.0F);
		this.mIsShowTipTextAsSingleLine = false;
		this.mTipBackgroundColor = Color.parseColor("#22000000");
		this.mIsShowTipBackground = false;
		this.mIsScanLineReverse = false;
		this.mIsShowDefaultGridScanLineDrawable = false;
		
		this.mTipPaint = new TextPaint();
		this.mTipPaint.setAntiAlias(true);
		
		this.mTipBackgroundRadius = BGAQRCodeUtil.dp2px(context, 4.0F);
		
		this.mIsOnlyDecodeScanBoxArea = false;
		this.mIsShowLocationPoint = false;
		this.mIsAutoZoom = false;
	}
	
	void init(QRCodeView qrCodeView, AttributeSet attrs) {
		this.mQRCodeView = qrCodeView;
		
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.QRCodeView);
		int count = typedArray.getIndexCount();
		for (int i = 0; i < count; i++) {
			initCustomAttr(typedArray.getIndex(i), typedArray);
		}
		typedArray.recycle();
		
		afterInitCustomAttrs();
	}
	
	private void initCustomAttr(int attr, TypedArray typedArray) {
		if (attr == R.styleable.QRCodeView_qrcv_topOffset) {
			this.mTopOffset = typedArray.getDimensionPixelSize(attr, this.mTopOffset);
		} else if (attr == R.styleable.QRCodeView_qrcv_cornerSize) {
			this.mCornerSize = typedArray.getDimensionPixelSize(attr, this.mCornerSize);
		} else if (attr == R.styleable.QRCodeView_qrcv_cornerLength) {
			this.mCornerLength = typedArray.getDimensionPixelSize(attr, this.mCornerLength);
		} else if (attr == R.styleable.QRCodeView_qrcv_scanLineSize) {
			this.mScanLineSize = typedArray.getDimensionPixelSize(attr, this.mScanLineSize);
		} else if (attr == R.styleable.QRCodeView_qrcv_rectWidth) {
			this.mRectWidth = typedArray.getDimensionPixelSize(attr, this.mRectWidth);
		} else if (attr == R.styleable.QRCodeView_qrcv_maskColor) {
			this.mMaskColor = typedArray.getColor(attr, this.mMaskColor);
		} else if (attr == R.styleable.QRCodeView_qrcv_cornerColor) {
			this.mCornerColor = typedArray.getColor(attr, this.mCornerColor);
		} else if (attr == R.styleable.QRCodeView_qrcv_scanLineColor) {
			this.mScanLineColor = typedArray.getColor(attr, this.mScanLineColor);
		} else if (attr == R.styleable.QRCodeView_qrcv_scanLineMargin) {
			this.mScanLineMargin = typedArray.getDimensionPixelSize(attr, this.mScanLineMargin);
		} else if (attr == R.styleable.QRCodeView_qrcv_isShowDefaultScanLineDrawable) {
			this.mIsShowDefaultScanLineDrawable = typedArray.getBoolean(attr, this.mIsShowDefaultScanLineDrawable);
		} else if (attr == R.styleable.QRCodeView_qrcv_customScanLineDrawable) {
			this.mCustomScanLineDrawable = typedArray.getDrawable(attr);
		} else if (attr == R.styleable.QRCodeView_qrcv_borderSize) {
			this.mBorderSize = typedArray.getDimensionPixelSize(attr, this.mBorderSize);
		} else if (attr == R.styleable.QRCodeView_qrcv_borderColor) {
			this.mBorderColor = typedArray.getColor(attr, this.mBorderColor);
		} else if (attr == R.styleable.QRCodeView_qrcv_animTime) {
			this.mAnimTime = typedArray.getInteger(attr, this.mAnimTime);
		} else if (attr == R.styleable.QRCodeView_qrcv_verticalBias) {
			this.mVerticalBias = typedArray.getFloat(attr, this.mVerticalBias);
		} else if (attr == R.styleable.QRCodeView_qrcv_cornerDisplayType) {
			this.mCornerDisplayType = typedArray.getInteger(attr, this.mCornerDisplayType);
		} else if (attr == R.styleable.QRCodeView_qrcv_toolbarHeight) {
			this.mToolbarHeight = typedArray.getDimensionPixelSize(attr, this.mToolbarHeight);
		} else if (attr == R.styleable.QRCodeView_qrcv_barcodeRectHeight) {
			this.mBarcodeRectHeight = typedArray.getDimensionPixelSize(attr, this.mBarcodeRectHeight);
		} else if (attr == R.styleable.QRCodeView_qrcv_isBarcode) {
			this.mIsBarcode = typedArray.getBoolean(attr, this.mIsBarcode);
		} else if (attr == R.styleable.QRCodeView_qrcv_barCodeTipText) {
			this.mBarCodeTipText = typedArray.getString(attr);
		} else if (attr == R.styleable.QRCodeView_qrcv_qrCodeTipText) {
			this.mQRCodeTipText = typedArray.getString(attr);
		} else if (attr == R.styleable.QRCodeView_qrcv_tipTextSize) {
			this.mTipTextSize = typedArray.getDimensionPixelSize(attr, this.mTipTextSize);
		} else if (attr == R.styleable.QRCodeView_qrcv_tipTextColor) {
			this.mTipTextColor = typedArray.getColor(attr, this.mTipTextColor);
		} else if (attr == R.styleable.QRCodeView_qrcv_isTipTextBelowRect) {
			this.mIsTipTextBelowRect = typedArray.getBoolean(attr, this.mIsTipTextBelowRect);
		} else if (attr == R.styleable.QRCodeView_qrcv_tipTextMargin) {
			this.mTipTextMargin = typedArray.getDimensionPixelSize(attr, this.mTipTextMargin);
		} else if (attr == R.styleable.QRCodeView_qrcv_isShowTipTextAsSingleLine) {
			this.mIsShowTipTextAsSingleLine = typedArray.getBoolean(attr, this.mIsShowTipTextAsSingleLine);
		} else if (attr == R.styleable.QRCodeView_qrcv_isShowTipBackground) {
			this.mIsShowTipBackground = typedArray.getBoolean(attr, this.mIsShowTipBackground);
		} else if (attr == R.styleable.QRCodeView_qrcv_tipBackgroundColor) {
			this.mTipBackgroundColor = typedArray.getColor(attr, this.mTipBackgroundColor);
		} else if (attr == R.styleable.QRCodeView_qrcv_isScanLineReverse) {
			this.mIsScanLineReverse = typedArray.getBoolean(attr, this.mIsScanLineReverse);
		} else if (attr == R.styleable.QRCodeView_qrcv_isShowDefaultGridScanLineDrawable) {
			this.mIsShowDefaultGridScanLineDrawable = typedArray.getBoolean(attr, this.mIsShowDefaultGridScanLineDrawable);
		} else if (attr == R.styleable.QRCodeView_qrcv_customGridScanLineDrawable) {
			this.mCustomGridScanLineDrawable = typedArray.getDrawable(attr);
		} else if (attr == R.styleable.QRCodeView_qrcv_isOnlyDecodeScanBoxArea) {
			this.mIsOnlyDecodeScanBoxArea = typedArray.getBoolean(attr, this.mIsOnlyDecodeScanBoxArea);
		} else if (attr == R.styleable.QRCodeView_qrcv_isShowLocationPoint) {
			this.mIsShowLocationPoint = typedArray.getBoolean(attr, this.mIsShowLocationPoint);
		} else if (attr == R.styleable.QRCodeView_qrcv_isAutoZoom) {
			this.mIsAutoZoom = typedArray.getBoolean(attr, this.mIsAutoZoom);
		}
	}
	
	private void afterInitCustomAttrs() {
		if (this.mCustomGridScanLineDrawable != null) {
			this.mOriginQRCodeGridScanLineBitmap = ((BitmapDrawable) this.mCustomGridScanLineDrawable).getBitmap();
		}
		if (this.mOriginQRCodeGridScanLineBitmap == null) {
			this.mOriginQRCodeGridScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_grid_scan_line);
			this.mOriginQRCodeGridScanLineBitmap = BGAQRCodeUtil.makeTintBitmap(this.mOriginQRCodeGridScanLineBitmap, this.mScanLineColor);
		}
		this.mOriginBarCodeGridScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(this.mOriginQRCodeGridScanLineBitmap, 90);
		this.mOriginBarCodeGridScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(this.mOriginBarCodeGridScanLineBitmap, 90);
		this.mOriginBarCodeGridScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(this.mOriginBarCodeGridScanLineBitmap, 90);
		
		
		if (this.mCustomScanLineDrawable != null) {
			this.mOriginQRCodeScanLineBitmap = ((BitmapDrawable) this.mCustomScanLineDrawable).getBitmap();
		}
		if (this.mOriginQRCodeScanLineBitmap == null) {
			this.mOriginQRCodeScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_scan_line);
			this.mOriginQRCodeScanLineBitmap = BGAQRCodeUtil.makeTintBitmap(this.mOriginQRCodeScanLineBitmap, this.mScanLineColor);
		}
		this.mOriginBarCodeScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(this.mOriginQRCodeScanLineBitmap, 90);
		
		this.mTopOffset += this.mToolbarHeight;
		this.mHalfCornerSize = 1.0F * this.mCornerSize / 2.0F;
		
		this.mTipPaint.setTextSize(this.mTipTextSize);
		this.mTipPaint.setColor(this.mTipTextColor);
		
		setIsBarcode(this.mIsBarcode);
	}
	
	
	public void onDraw(Canvas canvas) {
		if (this.mFramingRect == null) {
			return;
		}
		
		
		drawMask(canvas);
		
		
		drawBorderLine(canvas);
		
		
		drawCornerLine(canvas);
		
		
		drawScanLine(canvas);
		
		
		drawTipText(canvas);
		
		
		moveScanLine();
	}
	
	
	private void drawMask(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		
		if (this.mMaskColor != 0) {
			this.mPaint.setStyle(Paint.Style.FILL);
			this.mPaint.setColor(this.mMaskColor);
			canvas.drawRect(0.0F, 0.0F, width, this.mFramingRect.top, this.mPaint);
			canvas.drawRect(0.0F, this.mFramingRect.top, this.mFramingRect.left, (this.mFramingRect.bottom + 1), this.mPaint);
			canvas.drawRect((this.mFramingRect.right + 1), this.mFramingRect.top, width, (this.mFramingRect.bottom + 1), this.mPaint);
			canvas.drawRect(0.0F, (this.mFramingRect.bottom + 1), width, height, this.mPaint);
		}
	}
	
	
	private void drawBorderLine(Canvas canvas) {
		if (this.mBorderSize > 0) {
			this.mPaint.setStyle(Paint.Style.STROKE);
			this.mPaint.setColor(this.mBorderColor);
			this.mPaint.setStrokeWidth(this.mBorderSize);
			canvas.drawRect(this.mFramingRect, this.mPaint);
		}
	}
	
	
	private void drawCornerLine(Canvas canvas) {
		if (this.mHalfCornerSize > 0.0F) {
			this.mPaint.setStyle(Paint.Style.STROKE);
			this.mPaint.setColor(this.mCornerColor);
			this.mPaint.setStrokeWidth(this.mCornerSize);
			if (this.mCornerDisplayType == 1) {
				canvas.drawLine(this.mFramingRect.left - this.mHalfCornerSize, this.mFramingRect.top, this.mFramingRect.left - this.mHalfCornerSize + this.mCornerLength, this.mFramingRect.top, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.left, this.mFramingRect.top - this.mHalfCornerSize, this.mFramingRect.left, this.mFramingRect.top - this.mHalfCornerSize + this.mCornerLength, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right + this.mHalfCornerSize, this.mFramingRect.top, this.mFramingRect.right + this.mHalfCornerSize - this.mCornerLength, this.mFramingRect.top, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right, this.mFramingRect.top - this.mHalfCornerSize, this.mFramingRect.right, this.mFramingRect.top - this.mHalfCornerSize + this.mCornerLength, this.mPaint);
				
				
				canvas.drawLine(this.mFramingRect.left - this.mHalfCornerSize, this.mFramingRect.bottom, this.mFramingRect.left - this.mHalfCornerSize + this.mCornerLength, this.mFramingRect.bottom, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.left, this.mFramingRect.bottom + this.mHalfCornerSize, this.mFramingRect.left, this.mFramingRect.bottom + this.mHalfCornerSize - this.mCornerLength, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right + this.mHalfCornerSize, this.mFramingRect.bottom, this.mFramingRect.right + this.mHalfCornerSize - this.mCornerLength, this.mFramingRect.bottom, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right, this.mFramingRect.bottom + this.mHalfCornerSize, this.mFramingRect.right, this.mFramingRect.bottom + this.mHalfCornerSize - this.mCornerLength, this.mPaint);
			} else if (this.mCornerDisplayType == 2) {
				canvas.drawLine(this.mFramingRect.left, this.mFramingRect.top + this.mHalfCornerSize, (this.mFramingRect.left + this.mCornerLength), this.mFramingRect.top + this.mHalfCornerSize, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.left + this.mHalfCornerSize, this.mFramingRect.top, this.mFramingRect.left + this.mHalfCornerSize, (this.mFramingRect.top + this.mCornerLength), this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right, this.mFramingRect.top + this.mHalfCornerSize, (this.mFramingRect.right - this.mCornerLength), this.mFramingRect.top + this.mHalfCornerSize, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right - this.mHalfCornerSize, this.mFramingRect.top, this.mFramingRect.right - this.mHalfCornerSize, (this.mFramingRect.top + this.mCornerLength), this.mPaint);
				
				
				canvas.drawLine(this.mFramingRect.left, this.mFramingRect.bottom - this.mHalfCornerSize, (this.mFramingRect.left + this.mCornerLength), this.mFramingRect.bottom - this.mHalfCornerSize, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.left + this.mHalfCornerSize, this.mFramingRect.bottom, this.mFramingRect.left + this.mHalfCornerSize, (this.mFramingRect.bottom - this.mCornerLength), this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right, this.mFramingRect.bottom - this.mHalfCornerSize, (this.mFramingRect.right - this.mCornerLength), this.mFramingRect.bottom - this.mHalfCornerSize, this.mPaint);
				
				canvas.drawLine(this.mFramingRect.right - this.mHalfCornerSize, this.mFramingRect.bottom, this.mFramingRect.right - this.mHalfCornerSize, (this.mFramingRect.bottom - this.mCornerLength), this.mPaint);
			}
		}
	}
	
	
	private void drawScanLine(Canvas canvas) {
		if (this.mIsBarcode) {
			if (this.mGridScanLineBitmap != null) {
				RectF dstGridRectF = new RectF(this.mFramingRect.left + this.mHalfCornerSize + 0.5F, this.mFramingRect.top + this.mHalfCornerSize + this.mScanLineMargin, this.mGridScanLineRight, this.mFramingRect.bottom - this.mHalfCornerSize - this.mScanLineMargin);
				
				
				Rect srcGridRect = new Rect((int) (this.mGridScanLineBitmap.getWidth() - dstGridRectF.width()), 0, this.mGridScanLineBitmap.getWidth(), this.mGridScanLineBitmap.getHeight());
				
				if (srcGridRect.left < 0) {
					srcGridRect.left = 0;
					dstGridRectF.left = dstGridRectF.right - srcGridRect.width();
				}
				
				canvas.drawBitmap(this.mGridScanLineBitmap, srcGridRect, dstGridRectF, this.mPaint);
			} else if (this.mScanLineBitmap != null) {
				RectF lineRect = new RectF(this.mScanLineLeft, this.mFramingRect.top + this.mHalfCornerSize + this.mScanLineMargin, this.mScanLineLeft + this.mScanLineBitmap.getWidth(), this.mFramingRect.bottom - this.mHalfCornerSize - this.mScanLineMargin);
				
				canvas.drawBitmap(this.mScanLineBitmap, null, lineRect, this.mPaint);
			} else {
				this.mPaint.setStyle(Paint.Style.FILL);
				this.mPaint.setColor(this.mScanLineColor);
				canvas.drawRect(this.mScanLineLeft, this.mFramingRect.top + this.mHalfCornerSize + this.mScanLineMargin, this.mScanLineLeft + this.mScanLineSize, this.mFramingRect.bottom - this.mHalfCornerSize - this.mScanLineMargin, this.mPaint);
			}
			
		} else if (this.mGridScanLineBitmap != null) {
			RectF dstGridRectF = new RectF(this.mFramingRect.left + this.mHalfCornerSize + this.mScanLineMargin, this.mFramingRect.top + this.mHalfCornerSize + 0.5F, this.mFramingRect.right - this.mHalfCornerSize - this.mScanLineMargin, this.mGridScanLineBottom);
			
			
			Rect srcRect = new Rect(0, (int) (this.mGridScanLineBitmap.getHeight() - dstGridRectF.height()), this.mGridScanLineBitmap.getWidth(), this.mGridScanLineBitmap.getHeight());
			
			if (srcRect.top < 0) {
				srcRect.top = 0;
				dstGridRectF.top = dstGridRectF.bottom - srcRect.height();
			}
			
			canvas.drawBitmap(this.mGridScanLineBitmap, srcRect, dstGridRectF, this.mPaint);
		} else if (this.mScanLineBitmap != null) {
			
			RectF lineRect = new RectF(this.mFramingRect.left + this.mHalfCornerSize + this.mScanLineMargin, this.mScanLineTop, this.mFramingRect.right - this.mHalfCornerSize - this.mScanLineMargin, this.mScanLineTop + this.mScanLineBitmap.getHeight());
			canvas.drawBitmap(this.mScanLineBitmap, null, lineRect, this.mPaint);
		} else {
			this.mPaint.setStyle(Paint.Style.FILL);
			this.mPaint.setColor(this.mScanLineColor);
			canvas.drawRect(this.mFramingRect.left + this.mHalfCornerSize + this.mScanLineMargin, this.mScanLineTop, this.mFramingRect.right - this.mHalfCornerSize - this.mScanLineMargin, this.mScanLineTop + this.mScanLineSize, this.mPaint);
		}
	}
	
	
	private void drawTipText(Canvas canvas) {
		if (TextUtils.isEmpty(this.mTipText) || this.mTipTextSl == null) {
			return;
		}
		
		if (this.mIsTipTextBelowRect) {
			if (this.mIsShowTipBackground) {
				this.mPaint.setColor(this.mTipBackgroundColor);
				this.mPaint.setStyle(Paint.Style.FILL);
				if (this.mIsShowTipTextAsSingleLine) {
					Rect tipRect = new Rect();
					this.mTipPaint.getTextBounds(this.mTipText, 0, this.mTipText.length(), tipRect);
					float left = ((canvas.getWidth() - tipRect.width()) / 2 - this.mTipBackgroundRadius);
					canvas.drawRoundRect(new RectF(left, (this.mFramingRect.bottom + this.mTipTextMargin - this.mTipBackgroundRadius), left + tipRect
						  .width() + (2 * this.mTipBackgroundRadius), (this.mFramingRect.bottom + this.mTipTextMargin + this.mTipTextSl
						  .getHeight() + this.mTipBackgroundRadius)), this.mTipBackgroundRadius, this.mTipBackgroundRadius, this.mPaint);
				} else {
					
					canvas.drawRoundRect(new RectF(this.mFramingRect.left, (this.mFramingRect.bottom + this.mTipTextMargin - this.mTipBackgroundRadius), this.mFramingRect.right, (this.mFramingRect.bottom + this.mTipTextMargin + this.mTipTextSl
						  .getHeight() + this.mTipBackgroundRadius)), this.mTipBackgroundRadius, this.mTipBackgroundRadius, this.mPaint);
				}
			}
			
			
			canvas.save();
			if (this.mIsShowTipTextAsSingleLine) {
				canvas.translate(0.0F, (this.mFramingRect.bottom + this.mTipTextMargin));
			} else {
				canvas.translate((this.mFramingRect.left + this.mTipBackgroundRadius), (this.mFramingRect.bottom + this.mTipTextMargin));
			}
			this.mTipTextSl.draw(canvas);
			canvas.restore();
		} else {
			if (this.mIsShowTipBackground) {
				this.mPaint.setColor(this.mTipBackgroundColor);
				this.mPaint.setStyle(Paint.Style.FILL);
				
				if (this.mIsShowTipTextAsSingleLine) {
					Rect tipRect = new Rect();
					this.mTipPaint.getTextBounds(this.mTipText, 0, this.mTipText.length(), tipRect);
					float left = ((canvas.getWidth() - tipRect.width()) / 2 - this.mTipBackgroundRadius);
					canvas.drawRoundRect(new RectF(left, (this.mFramingRect.top - this.mTipTextMargin - this.mTipTextSl.getHeight() - this.mTipBackgroundRadius), left + tipRect
						  .width() + (2 * this.mTipBackgroundRadius), (this.mFramingRect.top - this.mTipTextMargin + this.mTipBackgroundRadius)), this.mTipBackgroundRadius, this.mTipBackgroundRadius, this.mPaint);
				} else {
					
					canvas.drawRoundRect(new RectF(this.mFramingRect.left, (this.mFramingRect.top - this.mTipTextMargin - this.mTipTextSl
						  .getHeight() - this.mTipBackgroundRadius), this.mFramingRect.right, (this.mFramingRect.top - this.mTipTextMargin + this.mTipBackgroundRadius)), this.mTipBackgroundRadius, this.mTipBackgroundRadius, this.mPaint);
				}
			}
			
			
			canvas.save();
			if (this.mIsShowTipTextAsSingleLine) {
				canvas.translate(0.0F, (this.mFramingRect.top - this.mTipTextMargin - this.mTipTextSl.getHeight()));
			} else {
				canvas.translate((this.mFramingRect.left + this.mTipBackgroundRadius), (this.mFramingRect.top - this.mTipTextMargin - this.mTipTextSl.getHeight()));
			}
			this.mTipTextSl.draw(canvas);
			canvas.restore();
		}
	}
	
	
	private void moveScanLine() {
		if (this.mIsBarcode) {
			if (this.mGridScanLineBitmap == null) {
				
				this.mScanLineLeft += this.mMoveStepDistance;
				int scanLineSize = this.mScanLineSize;
				if (this.mScanLineBitmap != null) {
					scanLineSize = this.mScanLineBitmap.getWidth();
				}
				
				if (this.mIsScanLineReverse) {
					if (this.mScanLineLeft + scanLineSize > this.mFramingRect.right - this.mHalfCornerSize || this.mScanLineLeft < this.mFramingRect.left + this.mHalfCornerSize) {
						this.mMoveStepDistance = -this.mMoveStepDistance;
					}
				} else if (this.mScanLineLeft + scanLineSize > this.mFramingRect.right - this.mHalfCornerSize) {
					this.mScanLineLeft = this.mFramingRect.left + this.mHalfCornerSize + 0.5F;
				}
				
			} else {
				
				this.mGridScanLineRight += this.mMoveStepDistance;
				if (this.mGridScanLineRight > this.mFramingRect.right - this.mHalfCornerSize) {
					this.mGridScanLineRight = this.mFramingRect.left + this.mHalfCornerSize + 0.5F;
				}
			}
			
		} else if (this.mGridScanLineBitmap == null) {
			
			this.mScanLineTop += this.mMoveStepDistance;
			int scanLineSize = this.mScanLineSize;
			if (this.mScanLineBitmap != null) {
				scanLineSize = this.mScanLineBitmap.getHeight();
			}
			
			if (this.mIsScanLineReverse) {
				if (this.mScanLineTop + scanLineSize > this.mFramingRect.bottom - this.mHalfCornerSize || this.mScanLineTop < this.mFramingRect.top + this.mHalfCornerSize) {
					this.mMoveStepDistance = -this.mMoveStepDistance;
				}
			} else if (this.mScanLineTop + scanLineSize > this.mFramingRect.bottom - this.mHalfCornerSize) {
				this.mScanLineTop = this.mFramingRect.top + this.mHalfCornerSize + 0.5F;
			}
			
		} else {
			
			this.mGridScanLineBottom += this.mMoveStepDistance;
			if (this.mGridScanLineBottom > this.mFramingRect.bottom - this.mHalfCornerSize) {
				this.mGridScanLineBottom = this.mFramingRect.top + this.mHalfCornerSize + 0.5F;
			}
		}
		
		
		postInvalidateDelayed(this.mAnimDelayTime, this.mFramingRect.left, this.mFramingRect.top, this.mFramingRect.right, this.mFramingRect.bottom);
	}
	
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calFramingRect();
	}
	
	private void calFramingRect() {
		int leftOffset = (getWidth() - this.mRectWidth) / 2;
		this.mFramingRect = new Rect(leftOffset, this.mTopOffset, leftOffset + this.mRectWidth, this.mTopOffset + this.mRectHeight);
		
		if (this.mIsBarcode) {
			this.mGridScanLineRight = this.mScanLineLeft = this.mFramingRect.left + this.mHalfCornerSize + 0.5F;
		} else {
			this.mGridScanLineBottom = this.mScanLineTop = this.mFramingRect.top + this.mHalfCornerSize + 0.5F;
		}
		
		if (this.mQRCodeView != null && isOnlyDecodeScanBoxArea()) {
			this.mQRCodeView.onScanBoxRectChanged(new Rect(this.mFramingRect));
		}
	}
	
	public Rect getScanBoxAreaRect(int previewHeight) {
		if (this.mIsOnlyDecodeScanBoxArea && getVisibility() == VISIBLE) {
			Rect rect = new Rect(this.mFramingRect);
			float ratio = 1.0F * previewHeight / getMeasuredHeight();
			
			float centerX = rect.exactCenterX() * ratio;
			float centerY = rect.exactCenterY() * ratio;
			
			float halfWidth = rect.width() / 2.0F;
			float halfHeight = rect.height() / 2.0F;
			float newHalfWidth = halfWidth * ratio;
			float newHalfHeight = halfHeight * ratio;
			
			rect.left = (int) (centerX - newHalfWidth);
			rect.right = (int) (centerX + newHalfWidth);
			rect.top = (int) (centerY - newHalfHeight);
			rect.bottom = (int) (centerY + newHalfHeight);
			return rect;
		}
		return null;
	}
	
	private void refreshScanBox() {
		if (this.mCustomGridScanLineDrawable != null || this.mIsShowDefaultGridScanLineDrawable) {
			if (this.mIsBarcode) {
				this.mGridScanLineBitmap = this.mOriginBarCodeGridScanLineBitmap;
			} else {
				this.mGridScanLineBitmap = this.mOriginQRCodeGridScanLineBitmap;
			}
		} else if (this.mCustomScanLineDrawable != null || this.mIsShowDefaultScanLineDrawable) {
			if (this.mIsBarcode) {
				this.mScanLineBitmap = this.mOriginBarCodeScanLineBitmap;
			} else {
				this.mScanLineBitmap = this.mOriginQRCodeScanLineBitmap;
			}
		}
		
		if (this.mIsBarcode) {
			this.mTipText = this.mBarCodeTipText;
			this.mRectHeight = this.mBarcodeRectHeight;
			this.mAnimDelayTime = (int) (1.0F * this.mAnimTime * this.mMoveStepDistance / this.mRectWidth);
		} else {
			this.mTipText = this.mQRCodeTipText;
			this.mRectHeight = this.mRectWidth;
			this.mAnimDelayTime = (int) (1.0F * this.mAnimTime * this.mMoveStepDistance / this.mRectHeight);
		}
		
		if (!TextUtils.isEmpty(this.mTipText)) {
			if (this.mIsShowTipTextAsSingleLine) {
				this.mTipTextSl = new StaticLayout(this.mTipText, this.mTipPaint, (BGAQRCodeUtil.getScreenResolution(getContext())).x, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
			} else {
				
				this.mTipTextSl = new StaticLayout(this.mTipText, this.mTipPaint, this.mRectWidth - 2 * this.mTipBackgroundRadius, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
			}
		}
		
		if (this.mVerticalBias != -1.0F) {
			int screenHeight = (BGAQRCodeUtil.getScreenResolution(getContext())).y - BGAQRCodeUtil.getStatusBarHeight(getContext());
			if (this.mToolbarHeight == 0) {
				this.mTopOffset = (int) (screenHeight * this.mVerticalBias - (this.mRectHeight / 2));
			} else {
				this.mTopOffset = this.mToolbarHeight + (int) ((screenHeight - this.mToolbarHeight) * this.mVerticalBias - (this.mRectHeight / 2));
			}
		}
		
		calFramingRect();
		
		postInvalidate();
	}
	
	public boolean getIsBarcode() {
		return this.mIsBarcode;
	}
	
	public void setIsBarcode(boolean isBarcode) {
		this.mIsBarcode = isBarcode;
		refreshScanBox();
	}
	
	public int getMaskColor() {
		return this.mMaskColor;
	}
	
	
	public void setMaskColor(int maskColor) {
		this.mMaskColor = maskColor;
		refreshScanBox();
	}
	
	
	public int getCornerColor() {
		return this.mCornerColor;
	}
	
	
	public void setCornerColor(int cornerColor) {
		this.mCornerColor = cornerColor;
		refreshScanBox();
	}
	
	
	public int getCornerLength() {
		return this.mCornerLength;
	}
	
	
	public void setCornerLength(int cornerLength) {
		this.mCornerLength = cornerLength;
		refreshScanBox();
	}
	
	
	public int getCornerSize() {
		return this.mCornerSize;
	}
	
	
	public void setCornerSize(int cornerSize) {
		this.mCornerSize = cornerSize;
		refreshScanBox();
	}
	
	
	public int getRectWidth() {
		return this.mRectWidth;
	}
	
	
	public void setRectWidth(int rectWidth) {
		this.mRectWidth = rectWidth;
		refreshScanBox();
	}
	
	
	public int getRectHeight() {
		return this.mRectHeight;
	}
	
	
	public void setRectHeight(int rectHeight) {
		this.mRectHeight = rectHeight;
		refreshScanBox();
	}
	
	
	public int getBarcodeRectHeight() {
		return this.mBarcodeRectHeight;
	}
	
	
	public void setBarcodeRectHeight(int barcodeRectHeight) {
		this.mBarcodeRectHeight = barcodeRectHeight;
		refreshScanBox();
	}
	
	
	public int getTopOffset() {
		return this.mTopOffset;
	}
	
	
	public void setTopOffset(int topOffset) {
		this.mTopOffset = topOffset;
		refreshScanBox();
	}
	
	
	public int getScanLineSize() {
		return this.mScanLineSize;
	}
	
	
	public void setScanLineSize(int scanLineSize) {
		this.mScanLineSize = scanLineSize;
		refreshScanBox();
	}
	
	
	public int getScanLineColor() {
		return this.mScanLineColor;
	}
	
	
	public void setScanLineColor(int scanLineColor) {
		this.mScanLineColor = scanLineColor;
		refreshScanBox();
	}
	
	
	public int getScanLineMargin() {
		return this.mScanLineMargin;
	}
	
	
	public void setScanLineMargin(int scanLineMargin) {
		this.mScanLineMargin = scanLineMargin;
		refreshScanBox();
	}
	
	
	public boolean isShowDefaultScanLineDrawable() {
		return this.mIsShowDefaultScanLineDrawable;
	}
	
	
	public void setShowDefaultScanLineDrawable(boolean showDefaultScanLineDrawable) {
		this.mIsShowDefaultScanLineDrawable = showDefaultScanLineDrawable;
		refreshScanBox();
	}
	
	
	public Drawable getCustomScanLineDrawable() {
		return this.mCustomScanLineDrawable;
	}
	
	
	public void setCustomScanLineDrawable(Drawable customScanLineDrawable) {
		this.mCustomScanLineDrawable = customScanLineDrawable;
		refreshScanBox();
	}
	
	
	public Bitmap getScanLineBitmap() {
		return this.mScanLineBitmap;
	}
	
	
	public void setScanLineBitmap(Bitmap scanLineBitmap) {
		this.mScanLineBitmap = scanLineBitmap;
		refreshScanBox();
	}
	
	
	public int getBorderSize() {
		return this.mBorderSize;
	}
	
	
	public void setBorderSize(int borderSize) {
		this.mBorderSize = borderSize;
		refreshScanBox();
	}
	
	
	public int getBorderColor() {
		return this.mBorderColor;
	}
	
	
	public void setBorderColor(int borderColor) {
		this.mBorderColor = borderColor;
		refreshScanBox();
	}
	
	
	public int getAnimTime() {
		return this.mAnimTime;
	}
	
	
	public void setAnimTime(int animTime) {
		this.mAnimTime = animTime;
		refreshScanBox();
	}
	
	
	public float getVerticalBias() {
		return this.mVerticalBias;
	}
	
	
	public void setVerticalBias(float verticalBias) {
		this.mVerticalBias = verticalBias;
		refreshScanBox();
	}
	
	
	public int getToolbarHeight() {
		return this.mToolbarHeight;
	}
	
	
	public void setToolbarHeight(int toolbarHeight) {
		this.mToolbarHeight = toolbarHeight;
		refreshScanBox();
	}
	
	
	public String getQRCodeTipText() {
		return this.mQRCodeTipText;
	}
	
	
	public void setQRCodeTipText(String qrCodeTipText) {
		this.mQRCodeTipText = qrCodeTipText;
		refreshScanBox();
	}
	
	
	public String getBarCodeTipText() {
		return this.mBarCodeTipText;
	}
	
	
	public void setBarCodeTipText(String barCodeTipText) {
		this.mBarCodeTipText = barCodeTipText;
		refreshScanBox();
	}
	
	
	public String getTipText() {
		return this.mTipText;
	}
	
	
	public void setTipText(String tipText) {
		if (this.mIsBarcode) {
			this.mBarCodeTipText = tipText;
		} else {
			this.mQRCodeTipText = tipText;
		}
		refreshScanBox();
	}
	
	
	public int getTipTextColor() {
		return this.mTipTextColor;
	}
	
	
	public void setTipTextColor(int tipTextColor) {
		this.mTipTextColor = tipTextColor;
		this.mTipPaint.setColor(this.mTipTextColor);
		refreshScanBox();
	}
	
	
	public int getTipTextSize() {
		return this.mTipTextSize;
	}
	
	
	public void setTipTextSize(int tipTextSize) {
		this.mTipTextSize = tipTextSize;
		this.mTipPaint.setTextSize(this.mTipTextSize);
		refreshScanBox();
	}
	
	
	public boolean isTipTextBelowRect() {
		return this.mIsTipTextBelowRect;
	}
	
	
	public void setTipTextBelowRect(boolean tipTextBelowRect) {
		this.mIsTipTextBelowRect = tipTextBelowRect;
		refreshScanBox();
	}
	
	
	public int getTipTextMargin() {
		return this.mTipTextMargin;
	}
	
	
	public void setTipTextMargin(int tipTextMargin) {
		this.mTipTextMargin = tipTextMargin;
		refreshScanBox();
	}
	
	
	public boolean isShowTipTextAsSingleLine() {
		return this.mIsShowTipTextAsSingleLine;
	}
	
	
	public void setShowTipTextAsSingleLine(boolean showTipTextAsSingleLine) {
		this.mIsShowTipTextAsSingleLine = showTipTextAsSingleLine;
		refreshScanBox();
	}
	
	
	public boolean isShowTipBackground() {
		return this.mIsShowTipBackground;
	}
	
	
	public void setShowTipBackground(boolean showTipBackground) {
		this.mIsShowTipBackground = showTipBackground;
		refreshScanBox();
	}
	
	
	public int getTipBackgroundColor() {
		return this.mTipBackgroundColor;
	}
	
	
	public void setTipBackgroundColor(int tipBackgroundColor) {
		this.mTipBackgroundColor = tipBackgroundColor;
		refreshScanBox();
	}
	
	
	public boolean isScanLineReverse() {
		return this.mIsScanLineReverse;
	}
	
	
	public void setScanLineReverse(boolean scanLineReverse) {
		this.mIsScanLineReverse = scanLineReverse;
		refreshScanBox();
	}
	
	
	public boolean isShowDefaultGridScanLineDrawable() {
		return this.mIsShowDefaultGridScanLineDrawable;
	}
	
	
	public void setShowDefaultGridScanLineDrawable(boolean showDefaultGridScanLineDrawable) {
		this.mIsShowDefaultGridScanLineDrawable = showDefaultGridScanLineDrawable;
		refreshScanBox();
	}
	
	
	public float getHalfCornerSize() {
		return this.mHalfCornerSize;
	}
	
	
	public void setHalfCornerSize(float halfCornerSize) {
		this.mHalfCornerSize = halfCornerSize;
		refreshScanBox();
	}
	
	
	public StaticLayout getTipTextSl() {
		return this.mTipTextSl;
	}
	
	
	public void setTipTextSl(StaticLayout tipTextSl) {
		this.mTipTextSl = tipTextSl;
		refreshScanBox();
	}
	
	
	public int getTipBackgroundRadius() {
		return this.mTipBackgroundRadius;
	}
	
	
	public void setTipBackgroundRadius(int tipBackgroundRadius) {
		this.mTipBackgroundRadius = tipBackgroundRadius;
		refreshScanBox();
	}
	
	
	public boolean isOnlyDecodeScanBoxArea() {
		return this.mIsOnlyDecodeScanBoxArea;
	}
	
	
	public void setOnlyDecodeScanBoxArea(boolean onlyDecodeScanBoxArea) {
		this.mIsOnlyDecodeScanBoxArea = onlyDecodeScanBoxArea;
		calFramingRect();
	}
	
	
	public boolean isShowLocationPoint() {
		return this.mIsShowLocationPoint;
	}
	
	
	public void setShowLocationPoint(boolean showLocationPoint) {
		this.mIsShowLocationPoint = showLocationPoint;
	}
	
	
	public boolean isAutoZoom() {
		return this.mIsAutoZoom;
	}
	
	
	public void setAutoZoom(boolean autoZoom) {
		this.mIsAutoZoom = autoZoom;
	}
}
