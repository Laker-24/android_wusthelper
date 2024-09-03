package com.example.wusthelper.ui.fragment.mainviewpager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.wusthelper.request.WustApi;
import com.example.wusthelper.base.fragment.BaseBindingFragment;
import com.example.wusthelper.databinding.FragmentConsultBinding;
import com.example.wusthelper.helper.MyDialogHelper;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.request.NewApiHelper;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.utils.NetWorkUtils;

public class ConsultFragment extends BaseBindingFragment<FragmentConsultBinding> {
    
    private static final String TAG = "ConsultFragment";

    private static final String WEB_VIEW_URl = WustApi.CONSULT_URL;
    //MainActivity设置了隐藏状态栏，需要记录下状态栏高度，自己设置状态栏
    private int height;

    /**
     * UI
     * */
    private AlertDialog loadingView;

    public static ConsultFragment newInstance() {
        return new ConsultFragment();
    }
    

    @Override
    public void initView() {
        initStatusBar();
    }
    /**
     * 懒加载数据和界面
     * */
    @Override
    protected void lazyLoad() {
        if (NetWorkUtils.isConnected(getContext())) {
            showLoadView();
            startWebView();
            SetCookie();
        }else {
            getBinding().flNoContent.setVisibility(View.VISIBLE);
            getBinding().wvConsult.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView() {

        setWebClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getBinding().wvConsult.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String cacheDirPath = getContext().getFilesDir().getAbsolutePath()+"cache/";
        getBinding().wvConsult.getSettings().setAppCachePath(cacheDirPath);
        // 1. 设置缓存路径

        getBinding().wvConsult.getSettings().setAppCacheMaxSize(20*1024*1024);
        // 2. 设置缓存大小

        getBinding().wvConsult.getSettings().setAppCacheEnabled(true);
        // 3. 开启Application Cache存储机制

        getBinding().wvConsult.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        getBinding().wvConsult.getSettings().setPluginState(WebSettings.PluginState.ON);
        getBinding().wvConsult.getSettings().setDomStorageEnabled(true);

        getBinding().wvConsult.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则getBinding().wvConsult必须设置支持Javascript
        getBinding().wvConsult.getSettings().setAllowFileAccess(true);
        getBinding().wvConsult.getSettings().setDomStorageEnabled(true);
        getBinding().wvConsult.getSettings().setDatabaseEnabled(true);
        getBinding().wvConsult.getSettings().setJavaScriptEnabled(true);
        getBinding().wvConsult.getSettings().setUseWideViewPort(true);
        getBinding().wvConsult.getSettings().setLoadWithOverviewMode(true);

        // 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getBinding().wvConsult.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    private void setWebClient() {

        getBinding().wvConsult.setWebChromeClient(new WebChromeClient(){
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert: "+message);
                try{
                    Uri uri = Uri.parse(message);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    //处理下载
                    result.confirm();
                    return true;
                }catch (Exception e){
                    return true;
                }
            }
        });

        getBinding().wvConsult.setWebViewClient(new WebViewClient(){
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
                Log.i(TAG, "Finished loading URL: " +url);
                if(loadingView!=null)
                    loadingView.cancel();
            }
        });

    }

    private void SetCookie() {
        NewApiHelper.getCheckToken(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                syncCookie();
                getBinding().wvConsult.loadUrl(WEB_VIEW_URl);
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    /**
     * 同步Cookie
     * */
    private void syncCookie() {

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(null);// 移除cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(getBinding().wvConsult, true);//TODO 跨域cookie读取
        }

        StringBuilder sbCookie = new StringBuilder();
        sbCookie.append(String.format("cookie" + "=%s", SharePreferenceLab.getToken()));
        //webview在使用cookie前会前判断保存cookie的domain和当前要请求的domain是否相同，相同才会发送cookie
        sbCookie.append(";domain=wustlinghang.cn"); //注意，是getHost()，不是getAuthority(),
        Log.e(TAG, "syncCookie: aURL.getHost() == wust.linghang,cn");
        sbCookie.append(String.format(";path=%s","/"));

        String cookieValue = sbCookie.toString();
        cookieManager.setCookie(WEB_VIEW_URl, cookieValue);

        cookieManager.flush();

        CookieSyncManager.getInstance().sync();

    }

    /**
     * 接下来重写的两个方法，是为了储存Fragment的状态栏高度height
     * */
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
     * */
    public void setHeight(int statusBarHeight) {
        this.height = statusBarHeight;
    }

    /**
     * 设置状态栏高度，这个在Fragment调用，是为了给状态栏设置高度
     * */
    public void initStatusBar() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        getBinding().viewStatus.setLayoutParams(lp);
    }


    public boolean onKeyDownBack()
    {
        if (getBinding().wvConsult.canGoBack()) {
            // 返回上一页面
            getBinding().wvConsult.goBack();
            return true;
        }
        return false;
    }

    private void showLoadView() {
        try{
            loadingView = MyDialogHelper.createLoadingDialog(getContext(),"加载中...", false);
            loadingView.show();

            Thread t = new Thread(() -> {

                try {

                    Thread.sleep(5000);//让他显示5秒后，取消ProgressDialog

                } catch (InterruptedException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }

                if(loadingView != null){
                    loadingView.dismiss();
                }

            });

            t.start();
        }catch (Exception e){
            Log.d(TAG, "startWebView: "+e.toString());
        }

    }


}
