package com.example.wusthelper.mvp.view;

import android.content.Intent;
import android.graphics.Bitmap;

import com.example.wusthelper.base.BaseMvpView;
import com.example.wusthelper.bean.javabean.CycleImageBean;

import java.util.List;

public interface HomeFragmentView extends BaseMvpView {

    void showCycleImageFromNet(List<CycleImageBean> data);

    void showCycleImageFromLocal(List<Bitmap> data);

    void showPhysicalLoginDialog();

    void showLoadDialog();

    void cancelLoadDialog();

    void startPhysicalDetailActivity();
}
