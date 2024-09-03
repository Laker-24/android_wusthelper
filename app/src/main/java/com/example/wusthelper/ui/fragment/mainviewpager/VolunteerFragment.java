package com.example.wusthelper.ui.fragment.mainviewpager;

import static android.app.Activity.RESULT_OK;

import static com.example.wusthelper.utils.MyBitmapTool.bitmapToBase64;
import static com.xuexiang.xutil.XUtil.getContentResolver;
import static com.xuexiang.xutil.XUtil.getPackageManager;
import static com.xuexiang.xutil.app.AppUtils.getPackageName;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.wusthelper.BuildConfig;
import com.example.wusthelper.request.WustApi;
import com.example.wusthelper.base.fragment.BaseBindingFragment;
import com.example.wusthelper.databinding.FragmentVolunteerBinding;
import com.example.wusthelper.helper.MyDialogHelper;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.request.NewApiHelper;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;

import com.example.wusthelper.ui.activity.QRCodeShareActivity;
import com.example.wusthelper.utils.Base64Util;
import com.example.wusthelper.utils.NetWorkUtils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;


public class VolunteerFragment extends BaseBindingFragment<FragmentVolunteerBinding> {
    //TODO 工时系统 vue调用android方法 传参，在android方法体内部实现base64转图片，然后存储到本地
    private static final String TAG = "VolunteerFragment";

    private static final String WEB_VIEW_URl = WustApi.VOLUNTEER_URL;

    //MainActivity设置了隐藏状态栏，需要记录下状态栏高度，自己设置状态栏
    private int height;

    /**
     * UI
     */
    private AlertDialog loadingView;

    private boolean isCameraPermissionGranted = false;
//     不同方式的请求码
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    /**
     * 接收安卓5.0以上的
     * <p>*/
      private ValueCallback<Uri[]> mUploadMessageAboveL;

      private String currentPhotoPath;
      private String mLastPhotoUri;
    private Uri mPhotoUri;

    private boolean isUploadHandled = false;


    public static VolunteerFragment newInstance() {
        return new VolunteerFragment();
    }

    @Override
    public void initView() {
        initStatusBar();
    }

    /**
     * 懒加载数据和界面
     */
    @Override
    protected void lazyLoad() {
        if (NetWorkUtils.isConnected(getContext())) {
            showLoadView();
            startWebView();
            SetCookie();
        } else {
            getBinding().flNoContent.setVisibility(View.VISIBLE);
            getBinding().wvVolunteer.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView() {

        setWebClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getBinding().wvVolunteer.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String cacheDirPath = getContext().getFilesDir().getAbsolutePath() + "cache/";
        getBinding().wvVolunteer.getSettings().setAppCachePath(cacheDirPath);
        // 1. 设置缓存路径

        getBinding().wvVolunteer.getSettings().setAppCacheMaxSize(20 * 1024 * 1024);
        // 2. 设置缓存大小

        getBinding().wvVolunteer.getSettings().setAppCacheEnabled(true);
        // 3. 开启Application Cache存储机制

        getBinding().wvVolunteer.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        getBinding().wvVolunteer.getSettings().setPluginState(WebSettings.PluginState.ON);
        getBinding().wvVolunteer.getSettings().setDomStorageEnabled(true);

        getBinding().wvVolunteer.getSettings().setDefaultTextEncodingName("UTF-8");
        getBinding().wvVolunteer.getSettings().setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        getBinding().wvVolunteer.getSettings().setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        getBinding().wvVolunteer.getSettings().setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        getBinding().wvVolunteer.getSettings().setAllowUniversalAccessFromFileURLs(false);

        getBinding().wvVolunteer.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则getBinding().wvVolunteer必须设置支持Javascript
        getBinding().wvVolunteer.getSettings().setAllowFileAccess(true);
        getBinding().wvVolunteer.getSettings().setDomStorageEnabled(true);
        getBinding().wvVolunteer.getSettings().setDatabaseEnabled(true);
        getBinding().wvVolunteer.getSettings().setJavaScriptEnabled(true);
        getBinding().wvVolunteer.getSettings().setUseWideViewPort(true);
        getBinding().wvVolunteer.getSettings().setLoadWithOverviewMode(true);
        getBinding().wvVolunteer.addJavascriptInterface(this, "WustHelper");
        // 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        getBinding().wvVolunteer.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    private void setWebClient() {

        getBinding().wvVolunteer.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert: " + message);
                try {
                    Uri uri = Uri.parse(message);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    //处理下载
                    result.confirm();
                    return true;
                } catch (Exception e) {
                    return true;
                }

            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // 确保之前的回调被清理
                if (mUploadMessageAboveL != null) {
                    mUploadMessageAboveL.onReceiveValue(null);
                    mUploadMessageAboveL = null;
                }
                mUploadMessageAboveL = filePathCallback ;
                showNativeFileChooserDialog();
                return true;

            }
        });

        getBinding().wvVolunteer.setWebViewClient(new WebViewClient() {
            @SuppressLint("JavascriptInterface")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();    //表示等待证书响应
                // handler.cancel();      //表示挂起连接，为默认方式
                // handler.handleMessage(null);    //可做其他处理
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);

                if (loadingView != null)
                    loadingView.cancel();
            }
        });

    }
    private void showNativeFileChooserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("选择操作")
                .setItems(new CharSequence[]{"拍照", "从图库选择", "取消"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // 拍照
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                                Log.d("权限","申请中");
                            } else {
                                Log.d("权限", "已有相机权限，直接启动相机");
                                launchCamera();
                            }
                            break;
                        case 1: // 从图库选择
                            launchGallery();
                            break;
                        case 2: // 取消
                            if (mUploadMessageAboveL != null) {
                                mUploadMessageAboveL.onReceiveValue(null);
                                mUploadMessageAboveL = null;
                            }
                            dialog.dismiss();
                            break;
                    }
                }).setCancelable(false);
//
        builder.create().show();
    }
    private void launchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
    }
    //拍照
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                mPhotoUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            Log.e("FileCreation", "Failed to create image file: " + e.getMessage());
            return null;
        }
    }

    private void SetCookie() {
        NewApiHelper.getCheckToken(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                syncCookie();
                getBinding().wvVolunteer.loadUrl(WEB_VIEW_URl);
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static int REQUEST_PERMISSION_CODE = 102;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (mUploadMessageAboveL == null) {
            return; // 如果已处理或 mUploadMessageAboveL 为空，退出
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                updatePhotos();
                mUploadMessageAboveL.onReceiveValue(new Uri[]{mPhotoUri});
            } else {
                mUploadMessageAboveL.onReceiveValue(null);
            }
        } else if (requestCode == REQUEST_IMAGE_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                Uri[] results = new Uri[]{intent.getData()};
                mUploadMessageAboveL.onReceiveValue(results);
            } else {
                mUploadMessageAboveL.onReceiveValue(null);
            }
        }
        mUploadMessageAboveL = null;
    }
    //取消上传
    private void cancelFileUpload() {
        if (mUploadMessageAboveL != null) {
            mUploadMessageAboveL.onReceiveValue(null);
            mUploadMessageAboveL = null;
        }
    }

    //刷新图片
    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(mPhotoUri);
        requireContext().sendBroadcast(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("dialog", "onRequestPermissionsResult 被调用了");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.e("dialog","调用了");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("dialog","调用了");
                launchCamera();
            } else {
                Toast.makeText(getActivity(), "相机权限被拒绝，无法使用相机。", Toast.LENGTH_SHORT).show();
                showNativeFileChooserDialog();
            }
        }
    }


    //    保存志愿者证书，与vue通信，将base64存储为图片
    @JavascriptInterface
    public boolean base64ToJpg(String base64str) throws IOException {
        Log.d(TAG, "调用base64ToJpg");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.requireActivity(), PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
//在/Pictures目录下新建FirstApp目录
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator + "wust_campus/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            //创建目录、
            Log.d(TAG, "base64ToJpg: 创建目录ing");
            while (!dir.mkdirs())
                Log.e(TAG, "base64ToJpg: 文件夹创建失败");
        }
        String imageName = "time_" + System.currentTimeMillis() + "_certificate_.jpg";
//使用文件的路径+文件名生成File对象
        File tempFile = new File(dirPath + imageName);
//如果存在就删除掉然后新建
        if (tempFile.exists()) {
            tempFile.delete();
        }
        if (tempFile.createNewFile())
            Log.e(TAG, "base64ToJpg: 创建文件");
        if (tempFile.exists()) {
            Log.e(TAG, "创建文件，base64str=" + base64str);
            StringBuilder builder = new StringBuilder(base64str);
            base64str = builder.substring(builder.indexOf(",", 1) + 1);
            byte[] b = Base64.decode(base64str, Base64.DEFAULT);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            // 保存图片
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                if (tempFile.exists() && tempFile.length() > 0) {
                    Uri uri = Uri.fromFile(tempFile);
                    intent.setData(uri);
                    requireContext().sendBroadcast(intent);
                    Log.e(TAG, "base64ToJpg: " + tempFile.getPath());
                    Toast.makeText(this.requireActivity(), "证书保存成功！已保存在" + tempFile.getPath(), Toast.LENGTH_SHORT).show();
                    // 写入成功返回文件路径
                    return true;
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "文件未找到");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "写入失败");
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e(TAG, "创建文件失败");
        }
        return false;
    }

    /**
     * 同步Cookie
     */
    private void syncCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(null);// 移除cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(getBinding().wvVolunteer, true);//TODO 跨域cookie读取
        }

        StringBuilder sbCookie = new StringBuilder();
        sbCookie.append(String.format("cookie" + "=%s", SharePreferenceLab.getToken()));
        //webview在使用cookie前会前判断保存cookie的domain和当前要请求的domain是否相同，相同才会发送cookie
        sbCookie.append(";domain=wustlinghang.cn"); //注意，是getHost()，不是getAuthority(),
        Log.e(TAG, "syncCookie: aURL.getHost() == wust.linghang,cn");
        sbCookie.append(String.format(";path=%s", "/"));

        String cookieValue = sbCookie.toString();

        Log.d(TAG, "syncCookie: " + cookieValue);

        cookieManager.setCookie(WEB_VIEW_URl, cookieValue);

        cookieManager.flush();

    }

    /**
     * 接下来重写的两个方法，是为了储存Fragment的状态栏高度height
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            height = savedInstanceState.getInt("statusBarHeight");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("statusBarHeight", height);
    }

    /**
     * 设置状态栏高度，在MainActivity调用 （因为在MainActivity 做过处理，隐藏了状态栏）
     */
    public void setHeight(int statusBarHeight) {
        this.height = statusBarHeight;
    }

    /**
     * 设置状态栏高度，这个在Fragment调用，是为了给状态栏设置高度
     */
    public void initStatusBar() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        getBinding().viewStatus.setLayoutParams(lp);
    }

    public boolean onKeyDownBack() {
        if (getBinding().wvVolunteer.canGoBack()) {
            // 返回上一页面
            getBinding().wvVolunteer.goBack();
            return true;
        }
        return false;
    }

    private void showLoadView() {
        try {
            loadingView = MyDialogHelper.createLoadingDialog(getActivity(), "加载中...", false);
            loadingView.show();

            Thread t = new Thread(() -> {

                try {
                    Thread.sleep(5000);//让他显示5秒后，取消ProgressDialog
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (loadingView != null) {
                    loadingView.dismiss();
                }

            });
            t.start();

        } catch (Exception e) {
            Log.d(TAG, "startWebView: " + e.toString());
        }

    }





}
