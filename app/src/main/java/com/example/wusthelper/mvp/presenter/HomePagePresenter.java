package com.example.wusthelper.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.wusthelper.MyApplication;
import com.example.wusthelper.R;
import com.example.wusthelper.base.BaseMvpView;
import com.example.wusthelper.base.BasePresenter;
import com.example.wusthelper.bean.javabean.CourseBean;
import com.example.wusthelper.bean.javabean.data.BaseData;
import com.example.wusthelper.bean.javabean.data.CourseData;
import com.example.wusthelper.bean.javabean.data.CycleImageData;
import com.example.wusthelper.dbhelper.CourseDB;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.mvp.model.HomePageModel;
import com.example.wusthelper.mvp.view.HomeFragmentView;
import com.example.wusthelper.request.okhttp.exception.OkHttpException;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.utils.NetWorkUtils;
import com.example.wusthelper.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class HomePagePresenter extends BasePresenter<HomeFragmentView> {

    private static final String TAG = "HomePagePresenter";

    private HomePageModel model;

    public HomePagePresenter(){
        model = new HomePageModel();
    }
    @Override
    public void initPresenterData() {

    }

    public void getCycleImageData(Context context) {
        if(NetWorkUtils.isConnected(context)){
            model.getCycleImageDataFormNet(new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                    Log.d("TAG", "onSuccess: "+responseObj);
                    CycleImageData data = (CycleImageData) responseObj;
                    if(data.getCode().equals("0")){
                        getView().showCycleImageFromNet(data.getData());
                    }else {
                        getView().showCycleImageFromLocal(getLocalCycleImage(context));
                    }
                }

                @Override
                public void onFailure(Object reasonObj) {
                    getView().showCycleImageFromLocal(getLocalCycleImage(context));
                }
            });
        }else {
            getView().showCycleImageFromLocal(getLocalCycleImage(context));
        }

    }

    private List<Bitmap> getLocalCycleImage(Context context) {
        List<Bitmap> list = new ArrayList<>();
        Bitmap bitmap001 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_bg);
        list.add(bitmap001);
        return list;
    }

    public void LoginPhysical(String password) {

        getView().showLoadDialog();


        model.postLoginPhysical(password, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseData data = (BaseData) responseObj;
                Log.d(TAG, "onSuccess: "+data);
                if(data.getCode().equals("10000")){
                    getPhysicalCourse();
                }else{
                    ToastUtil.showShortToastCenter(data.getMsg());
                }
                getView().cancelLoadDialog();
            }

            @Override
            public void onFailure(Object reasonObj) {
                ToastUtil.show("登录失败，可能是网络状态不佳或者物理实验官网崩溃");
                getView().cancelLoadDialog();
            }
        });
    }

    public void getPhysicalCourse(){
        model.getPhysicalCourse(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                getView().cancelLoadDialog();
                CourseData data = (CourseData) responseObj;
                Log.d(TAG, "onSuccess: "+data);
                if(data.getCode().equals("10000")){
                    CourseDB.addAllCourseData(data.getData(),
                            SharePreferenceLab.getStudentId(),SharePreferenceLab.getSemester(),
                            CourseBean.TYPE_PHYSICAL);

                    SharePreferenceLab.getInstance().setIsPhysicalLogin(MyApplication.getContext(),
                            true);
                    getView().startPhysicalDetailActivity();
                }else {
                    ToastUtil.showShortToastCenter(data.getMsg());
                }

            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.d(TAG, "onFailure: "+ ((OkHttpException)reasonObj).getEmsg());
                ToastUtil.show("登录成功,但是物理实验课表请求失败");
                getView().cancelLoadDialog();
            }
        });
    }
}
