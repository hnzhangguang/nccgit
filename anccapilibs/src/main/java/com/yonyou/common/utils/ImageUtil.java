/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.yonyou.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
	
	public static void resize(Bitmap bitmap, File outputFile, int maxWidth, int maxHeight) {
		try {
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// 图片大于最大高宽，按大的值缩放
			if (bitmapWidth > maxHeight || bitmapHeight > maxWidth) {
				float widthScale = maxWidth * 1.0f / bitmapWidth;
				float heightScale = maxHeight * 1.0f / bitmapHeight;
				
				float scale = Math.min(widthScale, heightScale);
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
			}
			Log.i("APIService", "upload face size" + bitmap.getWidth() + "*" + bitmap.getHeight());
			// save image
			FileOutputStream out = new FileOutputStream(outputFile);
			try {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void resizeBig(Bitmap bitmap, File outputFile, int maxWidth, int maxHeight) {
		try {
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// 图片大于最大高宽，按大的值缩放
			if (bitmapWidth > maxHeight || bitmapHeight > maxWidth) {
				float widthScale = maxWidth * 1.0f / bitmapWidth;
				float heightScale = maxHeight * 1.0f / bitmapHeight;
				
				float scale = Math.min(widthScale, heightScale);
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
			}
			Log.i("APIService", "upload face size" + bitmap.getWidth() + "*" + bitmap.getHeight());
			// save image
			FileOutputStream out = new FileOutputStream(outputFile);
			try {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String fileToBase64(String filePath) {
		String base64 = "";
		try {
			File file = new File(filePath);
			byte[] content = new byte[(int) file.length()];
			FileInputStream fileInputstream = new FileInputStream(file);
			int length = fileInputstream.read(content);
			fileInputstream.close();
			base64 = Base64.encodeToString(content, 0, length, Base64.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return base64;
	}
	
	public static String imageFileToBase64(File imageFile) {
		String imgBase64 = "";
		try {
			byte[] content = new byte[(int) imageFile.length()];
			FileInputStream fileInputstream = new FileInputStream(imageFile);
			int length = fileInputstream.read(content);
			fileInputstream.close();
			imgBase64 = Base64.encodeToString(content, 0, length, Base64.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgBase64;
	}
	
	
	public static int calculateInSampleSize(
		  BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight
				  && (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}
		
		return inSampleSize;
	}
	
	public static void saveBitmap(String outputPath, Bitmap bitmap) {
		// save image
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputPath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static Bitmap loadZoomBitmap(Context context, Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int bmpHeight = bitmap.getHeight();
		int bmpWidth = bitmap.getWidth();
		int newHeight = 0;
		int zoom = 1;
		if (bmpWidth > 200) {
			zoom = bmpWidth / 200;
			newHeight = bmpHeight / zoom;
		} else {
			zoom = 1;
			newHeight = bmpHeight;
		}
		if (zoom <= 1) {
			return bitmap;
		}
		Bitmap bm = Bitmap.createScaledBitmap(bitmap, 200, newHeight, true);
		if (bitmap != null) {
			bitmap.recycle();
		}
		return bm;
	}
	
	public static Bitmap base642bitmap(String image) {
		if (TextUtils.isEmpty(image)) {
			return null;
		}
		byte[] bytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		
		return bitmap;
	}
	
	/**
	 * 转换图片成圆形
	 *
	 * @param bitmap 传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left;
		float top;
		float right;
		float bottom;
		float dstLeft;
		float dstTop;
		float dstRight;
		float dstBottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dstLeft = 0;
			dstTop = 0;
			dstRight = width;
			dstBottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dstLeft = 0;
			dstTop = 0;
			dstRight = height;
			dstBottom = height;
		}
		
		Bitmap output = Bitmap.createBitmap(width,
			  height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dstLeft, (int) dstTop, (int) dstRight, (int) dstBottom);
		final RectF rectF = new RectF(dst);
		
		paint.setAntiAlias(true);
		
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}


//    public static Bitmap getBase64Image(HashMap<String, String> imageMap) {
//
//        Set<Map.Entry<String, String>> sets = imageMap.entrySet();
//        Bitmap bmp = null;
//        for (Map.Entry<String, String> entry : sets) {
//            bmp = base64ToBitmap(entry.getValue());
//        }
//        return bmp;
//    }
	
	public static Bitmap cropFaceImg(Bitmap bitmap, int needW) {
		Bitmap face = null;
		int x = bitmap.getWidth();
		int y = bitmap.getHeight();
		int left = (x - needW) / 2;
		int top = (y - needW) / 2;
		face = Bitmap.createBitmap(bitmap, left, top, needW, needW);
		
		return face;
	}
	
	/**
	 * 文件转base64字符串
	 *
	 * @param file
	 * @return
	 */
	public static String fileToBase64(File file) {
		String base64 = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] bytes = new byte[in.available()];
			int length = in.read(bytes);
			base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return base64;
	}
}
