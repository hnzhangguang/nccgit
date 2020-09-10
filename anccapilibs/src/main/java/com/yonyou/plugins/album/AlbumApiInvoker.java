package com.yonyou.plugins.album;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.yonyou.album.plugin.AlbumActivity;
import com.yonyou.album.plugin.CallCamera;
import com.yonyou.album.plugin.PicturesActivity;
import com.yonyou.album.plugin.callback.AlbumCallback;
import com.yonyou.album.plugin.utils.PicFileUtil;
import com.yonyou.ancclibs.R;
import com.yonyou.common.callback.MTLCallback;
import com.yonyou.common.download.OfflineUpdateControl;
import com.yonyou.common.net.HttpCallBack;
import com.yonyou.common.onActivityForResult.OnActivityForResultUtils;
import com.yonyou.common.onActivityForResult.SimpleOnActivityForResultCallback;
import com.yonyou.common.service.MTLHttpService;
import com.yonyou.common.utils.CommonRes;
import com.yonyou.common.utils.GlobalConstants;
import com.yonyou.common.utils.ImageUtil;
import com.yonyou.common.utils.bitmap.BitmapUtil;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.utils.NetUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.component.SelectDialog;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.yonyou.album.plugin.AlbumActivity.RESULT_IMAGE;

public class AlbumApiInvoker implements IApiInvoker {

    private static final String ALBUM_TYPE = "album";
    private static final String CAMERA_TYPE = "camera";

    private static final String CHOOSE_IMAGE = "chooseImage";
    private static final String PREVIEW_IMAGE = "previewImage";
    private static final String GET_LOCAL_IMGSRC = "getLocalImgSrc"; // 图片 路径
    private static final String GET_LOCAL_IMGDATA = "getLocalImgData"; // 图片 base64
    private static final String UPLOAD_IMAGE = "uploadImage";
    private static final String DOWNLOAD_IMAGE = "downloadImage";
    private static final String compressImg = "compressImg";   // 压缩图片

    private static final int REQUEST_ALBUM = 999;

    private Activity mContext;
    private MTLArgs mArgs;
    private AlbumCallback mAlbumCallback = new AlbumCallback() {
        @Override
        public void onResult(JSONObject json) {
            String path = json.optString("path");
            JSONObject jsonObject = new JSONObject();
            JSONObject imgSrcJson = new JSONObject();
            JSONArray localIdArray = new JSONArray();
            String localId = PicFileUtil.getLocalId(path);
            GlobalConstants.idPathMap.put(localId, path);
            localIdArray.put(localId);
            try {
                // localID - localSrc 对应关系
                imgSrcJson.put(localId, path);

                jsonObject.put("localIds", localIdArray);
                jsonObject.put("localSrcs", imgSrcJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mArgs.success(jsonObject);
        }

        @Override
        public void onError(String errMsg) {

        }
    };

    @Override
    public String call(String apiname, final MTLArgs args) throws MTLException {
        mContext = args.getContext();
        mArgs = args;
        switch (apiname) {
            case compressImg:

                int type1 = args.getInteger("type", 1);//0 是图片的base64, 1图片路径
                int level = args.getInteger("level", 1);// 0 不压缩,1,低质量 2 中质量 3 高质量
                String imgData = args.getString("imgData", "");// path -> 原图路径
                if (1 == type1) {
                    String path = imgData;  // 图片绝对路径
//                    path = OfflineUpdateControl.getOfflinePathWhitoutAppId(args.getContext()) + "/mtl_icon.png";

                    File originImgFile = new File(path);
                    if (!originImgFile.exists()) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("message", "原图路径不对"); // 压缩后图片路径
                            args.error(json.toString());
                        } catch (JSONException e) {
                            args.error(e.toString());
                            e.printStackTrace();
                        }
                        return "";
                    }

                    // 如果目录不存在则创建
                    String tempFilePath = OfflineUpdateControl.getOfflinePathWhitoutAppId(args.getContext()) + "/img/"; // 压缩后图片路径
                    File comDir = new File(tempFilePath);
                    if (!comDir.exists()) {
                        comDir.mkdir();
                    }

                    // 获取文件名
                    String fileName = path.substring(path.lastIndexOf("/") + 1);
                    // 组装压缩后的图片存放路径+名称
                    tempFilePath = tempFilePath + fileName;
                    File file = new File(tempFilePath);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (Exception eee) {
                            LogerNcc.e(eee);
                        }
                    }
                    // 压缩处理
                    BitmapUtil.samplingRateCompress(path, file);
                    // 转换为base64
                    String base64String = BitmapUtil.bitmapToBase64(file.getPath());

                    JSONObject json = new JSONObject();

                    try {

                        json.put("base64str", base64String); // 压缩后图片路径
                        json.put("orignimg_path", imgData); // 原图路径

                        args.success(json);

                    } catch (JSONException e) {
                        args.error(e.toString());
                        e.printStackTrace();
                    }


                }


                return "";
            case CHOOSE_IMAGE:
                JSONArray sourceType = args.getJsonArray("sourceType");
                if (sourceType != null) {
                    if (sourceType.length() == 2) { //相册和拍照二选一对话框
                        if ((ALBUM_TYPE.equals(sourceType.optString(0)) || CAMERA_TYPE.equals(sourceType.optString(0)))) {
                            chooseAlbumOrCamera();
                        } else {
                            args.error(prompt(mContext, R.string.album_params_error));
                        }
                    } else if (sourceType.length() == 1) {
                        String type = sourceType.optString(0);
                        if (ALBUM_TYPE.equals(type)) {
//                            openAlbum();
                            openWXAlbum();
                        } else if (CAMERA_TYPE.equals(type)) {
                            openCamera();
                        } else {
                            args.error(prompt(mContext, R.string.album_params_error));
                        }
                    } else {
                        args.error(prompt(mContext, R.string.album_params_error));
                    }
                } else {
                    chooseAlbumOrCamera();
                }
                return "";

            case PREVIEW_IMAGE:
                String current = args.getString("current");
                if (TextUtils.isEmpty(current)) {
                    args.error(prompt(mContext, R.string.album_params_error));
                    return "";
                }
                String urls = args.getString("urls");
                if (TextUtils.isEmpty(urls)) {
                    args.error(prompt(mContext, R.string.album_params_error));
                    return "";
                }
                Intent intent = new Intent(mContext, PicturesActivity.class);
                intent.putExtra("current", current);
                intent.putExtra("urls", urls);
                mContext.startActivity(intent);
                return "";

            case GET_LOCAL_IMGSRC:
                String imgsrcLocalId = args.getString("localId");
                if (TextUtils.isEmpty(imgsrcLocalId)) {
                    args.error("localId为空");
                    return "";
                }
                final String imgPath = GlobalConstants.idPathMap.get(imgsrcLocalId);
                if (!TextUtils.isEmpty(imgPath)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("imgSrc", imgPath);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    args.getCallback().success(jsonObject);
                } else {
                    args.getCallback().error(prompt(mContext, R.string.album_file_error));
                }

                return "";
            case GET_LOCAL_IMGDATA:
                String imgDataLocalId = args.getString("localId");
                if (TextUtils.isEmpty(imgDataLocalId)) {
                    args.error("localId为空");
                    return "";
                }
                final String imgDataPath = GlobalConstants.idPathMap.get(imgDataLocalId);

                if (!TextUtils.isEmpty(imgDataPath)) {
                    String data = ImageUtil.fileToBase64(new File(imgDataPath));
                    if (!TextUtils.isEmpty(data)) {
                        data = "data:image/jpeg;base64," + data;
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("localData", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        args.getCallback().success(jsonObject);
                    } else {
                        args.getCallback().error(prompt(mContext, R.string.album_file_error));
                    }
                } else {
                    args.getCallback().error(prompt(mContext, R.string.album_file_error));
                }
                return "";
            case UPLOAD_IMAGE:

                // filePath -> localId
                // billId : billpk
                // fullPath : imageGroup

                String uploadLocalId = args.getString("localId");
                if (TextUtils.isEmpty(uploadLocalId)) {
                    args.error("localId为空");
                    return "";
                }
                // 获取传递的参数
                String params = args.getParams();
                JsonObjectEx paramJsonObj = JsonObjectEx.getJsonObj(params);
                String localId = paramJsonObj.getValue("localId");
                String uploadUrl = paramJsonObj.getValue("uploadUrl");  // 调用哪个action
                // 获取要上传的文件路径
                final String uploadPath = GlobalConstants.idPathMap.get(uploadLocalId);
                if (TextUtils.isEmpty(uploadPath)) {
                    args.error(prompt(mContext, R.string.album_file_error));
                    return "";
                }
//                String upConfig = ResourcesUtils.getFromAssets(mContext, "www/config.json");
//                MTLHttpService uploadService = new MTLHttpService(args.getContext().getApplication(), args.getContext());
//                String uploadUrl = "https://mdoctor.yonyoucloud.com/mtldebugger/mtl/file/uploadToOSS";
                File file = new File(uploadPath);
                if (file.exists()) {
                    final ProgressDialog tip = showTip(mContext, args.getInteger("isShowProgressTips", 1), mContext.getResources().getString(R.string.mtl_image_up_loading));

                    // 发送请求参数
                    JsonObjectEx bodyParams = JsonObjectEx.getJsonObj();
                    bodyParams.putEx("file", file);
                    // 发送请求
                    NetUtil.callAction(args.getContext(), uploadUrl, bodyParams, new HttpCallBack() {
                        @Override
                        public void onFailure(JSONObject error) {
                            dismissTip(tip);
                            args.error(error.toString());
                        }

                        @Override
                        public void onResponse(JSONObject successJson) {
                            dismissTip(tip);
                            if (successJson != null) {
                                JSONObject jsonObject = new JSONObject();
                                int code = successJson.optInt("code");
                                String msg = successJson.optString("msg");
                                if (code == 0) {
                                    String serviceId = successJson.optString("data");
                                    try {
                                        jsonObject.put("code", code);
                                        jsonObject.put("msg", msg);
                                        jsonObject.put("serverId", serviceId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    args.success(jsonObject);
                                } else {
                                    args.error(msg);
                                }
                            } else {
                                args.error(prompt(mContext, R.string.album_params_error));
                            }
                        }
                    });

                    //                    uploadService.uploadFile(uploadUrl, file, new MTLCallback() {
//                        @Override
//                        public void onResult(JSONObject data) {
//                            dismissTip(tip);
//                            if (data != null) {
//                                JSONObject jsonObject = new JSONObject();
//                                int code = data.optInt("code");
//                                String msg = data.optString("msg");
//                                if (code == 0) {
//                                    String serviceId = data.optString("data");
//                                    try {
//                                        jsonObject.put("code", code);
//                                        jsonObject.put("msg", msg);
//                                        jsonObject.put("serverId", serviceId);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    args.success(jsonObject);
//                                } else {
//                                    args.error(msg);
//                                }
//                            } else {
//                                args.error(prompt(mContext, R.string.album_params_error));
//                            }
//                        }
//
//                        @Override
//                        public void onError(String message) {
//                            dismissTip(tip);
//                            args.error(message);
//                        }
//                    });
                }

                return "";
            case DOWNLOAD_IMAGE:
                final String serverId = args.getString("serverId");
                if (TextUtils.isEmpty(serverId)) {
                    args.error("serverId为空");
                    return "";
                }
                String cachePath = GlobalConstants.idPathMap.get(serverId);
                if (!TextUtils.isEmpty(cachePath)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("localId", PicFileUtil.getLocalId(cachePath));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    args.success(jsonObject);
                    return "";
                }
                String downloadUrl;
//                String downConfig = ResourcesUtils.getFromAssets(mContext, "www/config.json");
                try {
//                    JSONObject configJson = new JSONObject(downConfig);
                    JSONObject config = CommonRes.appConfig.getJSONObject("config");
                    if (config == null) {
                        args.error(mContext.getResources().getString(R.string.album_config_error));
                        return "";
                    }
                    JSONObject serviceUrl = config.getJSONObject("serviceUrl");
                    if (serviceUrl == null) {
                        args.error(mContext.getResources().getString(R.string.album_service_url_null));
                        return "";
                    }
                    downloadUrl = serviceUrl.optString("downloadUrl");
                    if (TextUtils.isEmpty(downloadUrl)) {
                        args.error(mContext.getResources().getString(R.string.album_download_address_null));
                        return "";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    args.error(mContext.getResources().getString(R.string.album_config_error));
                    return "";
                }
                MTLHttpService downloadService = new MTLHttpService(args.getContext().getApplication(), args.getContext());
                downloadUrl = downloadUrl + "?serviceId=" + serverId;
//                String downloadUrl = "https://mdoctor.yonyoucloud.com/mtldebugger/mtl/stream/download" + "?serviceId=" + serverId;
                final ProgressDialog downTip = showTip(mContext, args.getInteger("isShowProgressTips", 1), mContext.getResources().getString(R.string.mtl_image_down_loading));
                downloadService.downloadFile(downloadUrl, new MTLCallback() {
                    @Override
                    public void onResult(JSONObject data) {
                        dismissTip(downTip);
                        String downPath = data.optString("path");
                        String localId = PicFileUtil.getLocalId(downPath);
                        GlobalConstants.idPathMap.put(localId, downPath);
                        GlobalConstants.idPathMap.put(serverId, downPath);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("localId", localId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        args.success(jsonObject);
                    }

                    @Override
                    public void onError(String message) {
                        dismissTip(downTip);
                        args.error(message);
                    }
                });
                return "";
        }
        throw new MTLException(apiname + ": function not found");
    }

    private void chooseAlbumOrCamera() {
        List<String> names = new ArrayList<>();
        names.add(mContext.getResources().getString(R.string.album_camera));
        names.add(mContext.getResources().getString(R.string.album_album));
        showDialog(mContext, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 直接调起相机
                        openCamera();
                        break;
                    case 1:
                        openWXAlbum();
                        break;
                    default:
                        break;
                }

            }
        }, names);
    }

    private void initImagePicker(int maxImgCount) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
    }

    private String prompt(Context context, int res) {
        return context.getResources().getString(res);
    }

    private void openAlbum() {
        Intent intent = new Intent(mContext, AlbumActivity.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("crop", mArgs.getBoolean("crop"));
        map.put("maxWidth", mArgs.getInteger("maxWidth"));
        map.put("maxHeight", mArgs.getInteger("maxHeight"));
        map.put("quality", mArgs.getInteger("quality"));
        map.put("count", mArgs.getInteger("count"));
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", map);
        intent.putExtras(bundle);
        OnActivityForResultUtils.startActivityForResult(mContext, REQUEST_ALBUM, intent, new SimpleOnActivityForResultCallback() {
            @Override
            public void success(Integer resultCode, Intent data) {
                if (resultCode == RESULT_IMAGE) {
                    JSONObject jsonObject = new JSONObject();
                    JSONArray localIdArray = new JSONArray();
                    ArrayList<String> paths = data.getStringArrayListExtra("paths");
                    for (String path : paths) {
                        String localId = PicFileUtil.getLocalId(path);
                        GlobalConstants.idPathMap.put(localId, path);
                        localIdArray.put(localId);
                    }
                    try {
                        jsonObject.put("localIds", localIdArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mArgs.success(jsonObject);
                }
            }
        });
    }

    private void openWXAlbum() {
        initImagePicker(mArgs.getInteger("count", 9));
        Intent intent = new Intent(mContext, ImageGridActivity.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("crop", mArgs.getBoolean("crop"));
        map.put("maxWidth", mArgs.getInteger("maxWidth"));
        map.put("maxHeight", mArgs.getInteger("maxHeight"));
        map.put("quality", mArgs.getInteger("quality"));
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", map);
        intent.putExtras(bundle);
        OnActivityForResultUtils.startActivityForResult(mContext, REQUEST_ALBUM, intent, new SimpleOnActivityForResultCallback() {
            @Override
            public void success(Integer resultCode, Intent data) {
                if (resultCode == RESULT_IMAGE) {
                    JSONObject jsonObject = new JSONObject();
                    JSONArray localIdArray = new JSONArray();
                    JSONObject imgSrcJson = new JSONObject(); // localId - localSrc 对应的json
                    ArrayList<String> paths = data.getStringArrayListExtra("paths");
                    for (String path : paths) {
                        String localId = PicFileUtil.getLocalId(path);
                        GlobalConstants.idPathMap.put(localId, path);
                        localIdArray.put(localId);
                        try {
                            imgSrcJson.put(localId, path); // localId - localSrc 对应关系记录下来
                        } catch (Exception ee) {

                        }


                    }
                    try {
                        jsonObject.put("localIds", localIdArray);
                        jsonObject.put("localSrcs", imgSrcJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mArgs.success(jsonObject);
                }
            }
        });
    }

    private void openCamera() {
        CallCamera camera = new CallCamera();
        HashMap<String, Object> map = new HashMap<>();
        map.put("crop", mArgs.getBoolean("crop"));
        map.put("maxWidth", mArgs.getInteger("maxWidth"));
        map.put("maxHeight", mArgs.getInteger("maxHeight"));
        map.put("quality", mArgs.getInteger("quality"));
        camera.capture(mContext, mAlbumCallback, map);
    }

    private void showDialog(Activity activity, SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(activity, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!activity.isFinishing()) {
            dialog.show();
        }
    }

    private ProgressDialog showTip(Context context, int isShowProgressTips, String tipText) {
        ProgressDialog mDialog = null;
        if (isShowProgressTips == 1) {
            mDialog = ProgressDialog.show(context, "", tipText);
        }
        return mDialog;
    }

    private void dismissTip(ProgressDialog tip) {
        if (tip != null && tip.isShowing()) {
            tip.dismiss();
        }
    }
}
