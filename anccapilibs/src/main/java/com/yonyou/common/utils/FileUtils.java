package com.yonyou.common.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.yonyou.common.app.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    private static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    public static Boolean CopyAssetsDir(Context ctx, String src, String des) {
        // 复制Assets下的“非空目录”到des文件夹
        Boolean isSuccess = true;
        String[] files;
        try {
            files = ctx.getResources().getAssets().list(src);
        } catch (IOException e1) {
            return false;
        }

        if (files.length == 0) {
            isSuccess = CopyAssetsFile(ctx, src, des);// 对于文件直接复制
            if (!isSuccess)
                return isSuccess;
        } else {
            File srcfile = new File(des + "/" + src);
            if (!srcfile.exists()) {
                if (srcfile.mkdirs()) {// 对于目录自行创建
                    for (int i = 0; i < files.length; i++) {
                        isSuccess = CopyAssetsDir(ctx, src + "/" + files[i],
                                des);// 递归调用
                        if (!isSuccess)
                            return isSuccess;
                    }
                } else {
                    return false;
                }
            } else {
                for (int i = 0; i < files.length; i++) {
                    isSuccess = CopyAssetsDir(ctx, src + "/" + files[i],
                            des);// 递归调用
                    if (!isSuccess)
                        return isSuccess;
                }
            }

        }
        return isSuccess;
    }

    public static Boolean CopyAssetsFile(Context ctx, String filename,
                                         String des) {
        Boolean isSuccess = true;
        AssetManager assetManager = ctx.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = des + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }

        return isSuccess;

    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(
                        new File(targetDir).getAbsolutePath() + File.separator
                                + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    public static void deleteDirectory(String path) {
        File file = new File(path);
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteDirectory(childFiles[i].toString());
            }
            file.delete();
        }
    }

    public static JSONObject getJsonFile(InputStream f) {
        String result = "";

        try {
            BufferedReader bis = new BufferedReader(new InputStreamReader(f));
            String line = "";
            while ((line = bis.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * @param fromPath    解压文件得路径(aaa/bbb/xxxx.zip)
     * @param toPath      解压文件目标路径(aaa/bbb)
     * @param isDeleteZip 解压完毕是否删除解压文件
     * @throws IOException
     */
    public static void unZipFile(String fromPath, String toPath, boolean isDeleteZip) throws IOException {
        BufferedInputStream bi;
        ZipFile zf = new ZipFile(fromPath);
        Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();  // zip里面的每一个文件 eg: mobile_platform/ca/sha256.js
            if (entryName.startsWith("__MACOSX") || entryName.endsWith(".exe")) { // 无用文件过滤掉
                continue;
            }
            String path = toPath + "/" + entryName;
            if (ze2.isDirectory()) {
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                //                String fileDir = path.substring(0, path.lastIndexOf("/"));
                //                if (decompressDir.endsWith(RemindAudioConstant.ZIP_FILE_SUFFIX)) {
                //                    decompressDir = decompressDir.substring(0, decompressDir
                //                    .lastIndexOf(RemindAudioConstant.ZIP_FILE_SUFFIX));
                //                }
                File fileDirFile = new File(toPath);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }

//				把每个具体文件都放到根目录下,改变源文件的路径结构
//				String substring = entryName.substring(entryName.lastIndexOf("/") + 1,
//					  entryName.length());
//				BufferedOutputStream bos =
//					  new BufferedOutputStream(new FileOutputStream(toPath + "/" + substring));

                // 把每个具体文件放到原来的目录结构下,不改变原来的目录结构
                BufferedOutputStream bos =
                        new BufferedOutputStream(new FileOutputStream(toPath + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
        if (isDeleteZip) {
            File zipFile = new File(fromPath);
            if (zipFile.exists() && zipFile.getName().endsWith(".zip")) {
                zipFile.delete();
            }
        }
    }


    public static String readTextFile(String filePath) {
        FileInputStream fis = null;
        String result = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "";
            }
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            result = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static boolean writeTextFile(String filePath, String content) {
        FileOutputStream fos = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(filePath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                byte[] buffer = content.getBytes();
                fos.write(buffer);
                fos.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }
        return true;
    }


    /**
     * 删除文件夹里面的文件
     *
     * @param path
     */
    public static boolean deleteDirFile(String path) {
        File dir = new File(path);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return false;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
        return true;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    ///////////////////
    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            intent.setDataAndType(uri, "video/*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            intent.setDataAndType(uri, "audio/*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
                .encodedPath(param).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开doc,Word文件的intent
    public static Intent getWordFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/msword");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/x-chm");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "text/plain");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(BaseApplication.getBaseApp(), BaseApplication.getBaseApp().getApplicationContext().getPackageName() + ".fileprovider", new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }


    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectoryEx(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectoryEx(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }


}
