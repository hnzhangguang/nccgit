package com.yonyou.common.utils.bitmap;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ncc 移动图片工具类
 *
 * <p>author zhangg
 */
public class BitmapUtil {
	
	/**
	 * 设备外部存储目录
	 */
	private static String path = Environment.getExternalStorageDirectory().getPath() + "/";
	// 相机拍照生成图片名及后缀(临时文件)
	private static String fileName = "temp.png";
	
	/*
	 * @功能: 质量压缩 : 设置bitmap options属性，降低图片的质量，像素不会减少;
	 * @Param [bmp, file]
	 * 第一个参数为需要压缩的bitmap图片对象，
	 * 第二个参数为压缩后图片保存的位置 设置options 属性0-100，来实现压缩（因为png是无损压缩，所以该属性对png是无效的）
	 * @return void
	 * @Date 2:41 PM 2020/7/11
	 * @Author zhangg
	 **/
	public static void qualityCompress(Bitmap bmp, File file) {
		// 0-100 100为不压缩
		int quality = 60;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 把压缩后的数据存放到baos中
		bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @功能: 图片尺寸压缩 :通过缩放图片像素来减少图片占用内存大小
	 * 使用场景：缓存缩略图的时候（头像处理）
	 * @Param
	 * @return
	 * @Date 2:43 PM 2020/7/11
	 * @Author zhangg
	 **/
	public static void sizeCompress(Bitmap bmp, File file) {
		// 尺寸压缩倍数,值越大，图片尺寸越小
		int ratio = 8;
		// 压缩Bitmap到对应尺寸
		Bitmap result =
			  Bitmap.createBitmap(
				    bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
		canvas.drawBitmap(bmp, null, rect, null);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 把压缩后的数据存放到baos中
		result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @功能: 采样率压缩（设置图片的采样率，降低图片像素）
	 * @Param
	 * @return
	 * @Date 2:47 PM 2020/7/11
	 * @Author zhangg
	 **/
	public static void samplingRateCompress(String filePath, File file) {
		// 数值越高，图片像素越低
		int inSampleSize = 2;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		//          options.inJustDecodeBounds = true;//为true的时候不会真正加载图片，而是得到图片的宽高信息。
		// 采样率
		options.inSampleSize = inSampleSize;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 把压缩后的数据存放到baos中
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		try {
			if (file.exists()) {
				file.delete();
			} else {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 将图片按照指定的角度进行旋转
	 *
	 * @param path   需要旋转的图片的路径
	 * @param degree 指定的旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(String path, int degree) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		return rotateBitmapByDegree(bitmap, degree);
	}
	
	/*
	 * @功能:获取新的文件名
	 * @return
	 * @Date 2:48 PM 2020/7/11
	 * @Author zhangg
	 **/
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return "IMG_" + dateFormat.format(date);
	}
	
	/**
	 * 三星手机设置 三星手机拍照要旋转
	 *
	 * @param filePath
	 * @throws IOException
	 */
	public static File samsungPhoneSetting(String filePath) throws IOException {
		
		// 根据图片判断要旋转多少角度
		int bitmapDegree = getBitmapDegree(filePath);
		// 根据图片路径转bitmap
		Bitmap bitMBitmap;
		bitMBitmap = decodeFile(filePath);
		if (bitMBitmap == null) {
			return null;
		}
		// 旋转后的bitmap
		Bitmap rotateBitmapByDegree = rotateBitmapByDegree(bitMBitmap, bitmapDegree);
		File saveBitmapFile = saveBitmapFile(rotateBitmapByDegree, filePath);
		return saveBitmapFile;
	}
	
	/**
	 * 根据路径 转bitmap
	 *
	 * @param urlpath
	 * @return
	 */
	public static Bitmap getBitMBitmap(String urlpath) {
		
		Bitmap map = null;
		try {
			URL url = new URL(urlpath);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream in;
			in = conn.getInputStream();
			map = BitmapFactory.decodeStream(in);
			// TODO Auto-generated catch block
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 把batmap 转file
	 *
	 * @param bitmap
	 * @param filepath
	 */
	public static File saveBitmapFile(Bitmap bitmap, String filepath) {
		File file = new File(filepath); // 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path 图片绝对路径
	 * @return 图片的旋转角度
	 * @throws IOException
	 */
	public static int getBitmapDegree(String path) throws IOException {
		int degree = 0;
		// 从指定路径下读取图片，并获取其EXIF信息
		ExifInterface exifInterface = new ExifInterface(path);
		// 获取图片的旋转信息
		int orientation =
			  exifInterface.getAttributeInt(
				    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
		}
		
		return degree;
	}
	
	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm     需要旋转的图片
	 * @param degree 旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;
		
		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		
		// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
		returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}
	
	/**
	 * 根据 路径 得到 file 得到 bitmap
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Bitmap decodeFile(String filePath) throws IOException {
		Bitmap b = null;
		int IMAGE_MAX_SIZE = 600;
		
		File f = new File(filePath);
		if (f == null) {
			return null;
		}
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		
		FileInputStream fis = new FileInputStream(f);
		BitmapFactory.decodeStream(fis, null, o);
		fis.close();
		
		int scale = 1;
		if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
			scale =
				  (int)
					    Math.pow(
							2,
							(int)
								  Math.round(
									    Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth))
											/ Math.log(0.5)));
		}
		
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		fis = new FileInputStream(f);
		b = BitmapFactory.decodeStream(fis, null, o2);
		fis.close();
		return b;
	}
	
	/**
	 * 从文件载入Bitmap
	 *
	 * @param path
	 * @return
	 */
	public static Bitmap loadFromFile(String path) {
		return loadFromFile(path, null);
	}
	
	public static Bitmap loadFromFile(File file) {
		if (null == file) {
			return null;
		}
		Bitmap bm = BitmapFactory.decodeFile(file.getPath(), null);
		return bm;
	}
	
	/**
	 * 从文件载入Bitmap
	 *
	 * @param path
	 * @param opts
	 * @return
	 */
	public static Bitmap loadFromFile(String path, BitmapFactory.Options opts) {
		try {
			File f = new File(path);
			if (!f.exists() || f.isDirectory()) {
				return null;
			}
			Bitmap bm = BitmapFactory.decodeFile(path, opts);
			return bm;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @brief 从文件载入采样后的Bitmap
	 * @see android.graphics.BitmapFactory.Options#inSampleSize
	 */
	public static Bitmap loadSampleSize(String path, int sampleSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = sampleSize;
		return loadFromFile(path, opts);
	}
	
	/**
	 * bitmap转为base64
	 *
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(String bitmapPath) {
		
		String result = null;
		if (!TextUtils.isEmpty(bitmapPath)) {
			Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
			if (null != bitmap) {
				return bitmapToBase64(bitmap);
			}
		}
		return result;
	}
	
	/**
	 * bitmap转为base64
	 *
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {
		
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				
				baos.flush();
				baos.close();
				
				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * base64转为bitmap
	 *
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
	/**
	 * uri 转 File
	 *
	 * @param uri
	 * @return
	 */
	public File uriTurnFile(Uri uri, Activity activity) {
		
		if (uri == null) {
			return null;
		}
		
		File file = null;
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index =
			  actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		file = new File(img_path);
		return file;
	}
	
	/**
	 * @brief 从文件载入只获边框的Bitmap www.it165.net
	 * @see android.graphics.BitmapFactory.Options#inJustDecodeBounds
	 */
	public BitmapFactory.Options loadJustDecodeBounds(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		loadFromFile(path, opts);
		return opts;
	}
	
	/**
	 * @param bm   Bitmap
	 * @param path 图片路径
	 * @return 成功与否
	 * @brief 保存Bitmap至文件
	 */
	public boolean compressBitmap(Bitmap bm, String path) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return true;
	}
	
	/**
	 * @param path 图片路径
	 * @return 角度
	 * @brief 读取图片方向信息
	 */
	public int readPhotoDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation =
				  exifInterface.getAttributeInt(
					    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				default:
					degree = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * 生成缩略图
	 *
	 * @param src
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap extractThumbnail(Bitmap src, int width, int height) {
		return ThumbnailUtils.extractThumbnail(
			  src, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	}
	
	/**
	 * @brief 缩放Bitmap，自动回收原Bitmap
	 * @see
	 */
	public Bitmap scaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
		return scaleBitmap(src, dstWidth, dstHeight, true);
	}
	
	/**
	 * @param src       源Bitmap
	 * @param dstWidth  目标宽度
	 * @param dstHeight 目标高度
	 * @param isRecycle 是否回收原图像
	 * @return Bitmap
	 * @brief 缩放Bitmap
	 */
	public Bitmap scaleBitmap(Bitmap src, int dstWidth, int dstHeight, boolean isRecycle) {
		if (src.getWidth() == dstWidth && src.getHeight() == dstHeight) {
			return src;
		}
		Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
		if (isRecycle && dst != src) {
			src.recycle();
		}
		return dst;
	}
	
	/**
	 * @brief 裁剪Bitmap，自动回收原Bitmap
	 * @see
	 */
	public Bitmap cropBitmap(Bitmap src, int x, int y, int width, int height) {
		return cropBitmap(src, x, y, width, height, true);
	}
	
	/**
	 * @param src       源Bitmap
	 * @param x         开始x坐标
	 * @param y         开始y坐标
	 * @param width     截取宽度
	 * @param height    截取高度
	 * @param isRecycle 是否回收原图像
	 * @return Bitmap
	 * @brief 裁剪Bitmap
	 */
	public Bitmap cropBitmap(Bitmap src, int x, int y, int width, int height, boolean isRecycle) {
		if (x == 0 && y == 0 && width == src.getWidth() && height == src.getHeight()) {
			return src;
		}
		Bitmap dst = Bitmap.createBitmap(src, x, y, width, height);
		if (isRecycle && dst != src) {
			src.recycle();
		}
		return dst;
	}
	
	/**
	 * @brief 旋转Bitmap，自动回收原Bitmap
	 * @see
	 */
	public Bitmap rotateBitmap(Bitmap src, int degree) {
		return rotateBitmap(src, degree, true);
	}
	
	/**
	 * @param src       源Bitmap
	 * @param degree    旋转角度
	 * @param isRecycle 是否回收原图像
	 * @return Bitmap
	 * @brief 旋转Bitmap，顺时针
	 */
	public Bitmap rotateBitmap(Bitmap src, int degree, boolean isRecycle) {
		if (degree % 360 == 0) {
			return src;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);
		if (isRecycle && dst != src) {
			src.recycle();
		}
		return dst;
	}
	
	
}
