package com.yonyou.common.utils.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageBase64Utils {

    public static String fileToBase64(String filePath) {
        String base64 = "";
        try {
            File file = new File(filePath);
            byte[] content = new byte[(int) file.length()];
            FileInputStream fileInputstream = new FileInputStream(file);
            int length = fileInputstream.read(content);
            fileInputstream.close();
            base64 = Base64.encodeToString(content,0,length,Base64.DEFAULT);
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
            imgBase64 = Base64.encodeToString(content,0,length,Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgBase64;
    }


    public Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap sendImage(String bmMsg){
        byte [] input = Base64.decode(bmMsg, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(input, 0, input.length);
        return bitmap;
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
}