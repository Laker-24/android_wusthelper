package com.example.wusthelper.ui.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.wusthelper.R;
import com.example.wusthelper.base.activity.BaseActivity;
import com.example.wusthelper.databinding.ActivitySchoolCalendarBinding;
import com.example.wusthelper.request.WustApi;
import com.example.wusthelper.utils.NetWorkUtils;
import com.example.wusthelper.utils.ResourcesUtils;

public class SchoolCalendarActivity extends BaseActivity<ActivitySchoolCalendarBinding> {

    public static Intent newInstance(Context context){
        return new Intent(context,SchoolCalendarActivity.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initView(){
        getWindow().setStatusBarColor(ResourcesUtils.getRealColor(R.color.white));
        changeStatusBarTextColor(true);
        initImageView();
        initWebView();
        isNetWorkConnect();
    }

    private void initWebView() {
        getBinding().wvSchoolcalendar.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress ==100){
                    getBinding().scholcalendarProgress.setVisibility(View.GONE);
                }else {
                    getBinding().scholcalendarProgress.setVisibility(View.VISIBLE);
                    getBinding().scholcalendarProgress.setProgress(newProgress);
                }
            }
        });
    }

    private void initImageView() {
        getBinding().ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void isNetWorkConnect() {
        if (!NetWorkUtils.isConnected(this)) {
            getBinding().flSchoolcalendarNoInternet.setVisibility(View.VISIBLE);
            getBinding().wvSchoolcalendar.setVisibility(View.GONE);
        }else{
            startWebView();
        }
    }

    private void startWebView() {
        getBinding().wvSchoolcalendar.getSettings().setJavaScriptEnabled(true);
        getBinding().wvSchoolcalendar.getSettings().setUseWideViewPort(true);
        getBinding().wvSchoolcalendar.getSettings().setLoadWithOverviewMode(true);
        getBinding().wvSchoolcalendar.loadUrl(WustApi.SHOOL_CALENDAR);
        getBinding().wvSchoolcalendar.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}