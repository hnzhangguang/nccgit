package com.yonyou.album.plugin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PicFileUtil {

    private static String mCurrentLocalId;
    private static String mFilePath;

    public static File getExternalDirFileName(Context context, String type) {
        String dir = context.getExternalFilesDir((String) null).getPath();
//        String dir = context.getFilesDir().getAbsolutePath(); //file provider 添加<files-path
        if (dir.endsWith("/")) {
            dir = dir + "image/";
        } else {
            dir = dir + "/image/";
        }
        mCurrentLocalId = "image" + System.currentTimeMillis();
        File rs = new File(dir, mCurrentLocalId + "." + type);
        rs.getParentFile().mkdirs();
        mFilePath = rs.getAbsolutePath();
        return rs;
    }

    public static String getLocalId() {
        return mCurrentLocalId;
    }

    public static String getFilePath() {
        return mFilePath;
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

    public static String getLocalId(String path) {
        String localId;
        if (path.lastIndexOf(".") > path.lastIndexOf("/") + 1) {
            localId = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        } else {
            localId = path.substring(path.lastIndexOf("/") + 1);
        }
        return localId;
    }

    public static void resize(Bitmap bitmap, File outputFile/*, int maxWidth, int maxHeight*/) {
        try {
//            int bitmapWidth = bitmap.getWidth();
//            int bitmapHeight = bitmap.getHeight();
//            // 图片大于最大高宽，按大的值缩放
//            if (bitmapWidth > maxHeight || bitmapHeight > maxWidth) {
//                float widthScale = maxWidth * 1.0f / bitmapWidth;
//                float heightScale = maxHeight * 1.0f / bitmapHeight;
//
//                float scale = Math.min(widthScale, heightScale);
//                Matrix matrix = new Matrix();
//                matrix.postScale(scale, scale);
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
//            }
            // save image
            FileOutputStream out = new FileOutputStream(outputFile);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
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

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}