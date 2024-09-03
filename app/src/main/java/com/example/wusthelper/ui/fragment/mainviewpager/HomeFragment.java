package com.example.wusthelper.ui.fragment.mainviewpager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.example.wusthelper.MyApplication;
import com.example.wusthelper.R;
import com.example.wusthelper.adapter.LocalImageHolderView;
import com.example.wusthelper.adapter.NetImageHolderView;
import com.example.wusthelper.base.fragment.BaseMvpFragment;
import com.example.wusthelper.base.BaseMvpView;
import com.example.wusthelper.bean.javabean.CycleImageBean;
import com.example.wusthelper.databinding.FragmentHomeBinding;
import com.example.wusthelper.helper.MyDialogHelper;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.mvp.presenter.HomePagePresenter;
import com.example.wusthelper.request.WustApi;
import com.example.wusthelper.mvp.view.HomeFragmentView;
import com.example.wusthelper.ui.activity.CountdownActivity;
import com.example.wusthelper.ui.activity.CreditsStatisticsActivity;
import com.example.wusthelper.ui.activity.EmptyClassRoomActivity;
import com.example.wusthelper.ui.activity.FeedBackActivity;
import com.example.wusthelper.ui.activity.GradeActivity;

import com.example.wusthelper.ui.activity.LibraryActivity;
import com.example.wusthelper.ui.activity.LibraryLoginActivity;
import com.example.wusthelper.ui.activity.LostCardActivity;
import com.example.wusthelper.ui.activity.NewEmptyClassRoomActivity;
import com.example.wusthelper.ui.activity.SchoolBusActivity;
import com.example.wusthelper.ui.activity.SchoolCalendarActivity;

import com.example.wusthelper.ui.activity.OtherWebActivity;
import com.example.wusthelper.ui.activity.PhysicalDetailActivity;

import com.example.wusthelper.ui.activity.SearchClassRoomActivity;
import com.example.wusthelper.ui.activity.SearchCourseActivity;
import com.example.wusthelper.ui.activity.YellowPageActivity;
import com.example.wusthelper.ui.dialog.PhysicalLoginDialog;
import com.example.wusthelper.ui.dialog.UpdateDialog;
import com.example.wusthelper.ui.dialog.listener.PhysicalLoginDialogListener;
import com.example.wusthelper.utils.ScreenUtils;
import com.example.wusthelper.utils.ToastUtil;

import java.util.List;


public class HomeFragment extends BaseMvpFragment<HomeFragmentView, HomePagePresenter,FragmentHomeBinding>
        implements HomeFragmentView, View.OnClickListener,PhysicalLoginDialogListener{

    private static final String TAG = "HomeFragment";

    //MainActivity设置了隐藏状态栏，需要记录下状态栏高度，自己设置状态栏
    private int height;

    /**
     * UI
     * */
    private AlertDialog loadingView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public HomePagePresenter createPresenter() {
        return new HomePagePresenter();
    }

    @Override
    public HomeFragmentView createView() {
        return this;
    }

    @Override
    public void initView() {
        initStatusBar();
        setListener();
    }

    @Override
    protected void lazyLoad() {
        getPresenter().getCycleImageData(getContext());
    }

    private void setListener() {
        //热门功能
        getBinding().cardScoreNew.setOnClickListener(this);
        getBinding().cardCountdownNew.setOnClickListener(this);
        getBinding().cardLostCard.setOnClickListener(this);
        getBinding().cardCreditsStatisticsNew.setOnClickListener(this);
        getBinding().cardEmptyclassroomNew.setOnClickListener(this);
        getBinding().cardLibraryNew.setOnClickListener(this);
        //小工具
        getBinding().cardSchoolBusNew.setOnClickListener(this);
        getBinding().cardSchoolCalendarNew.setOnClickListener(this);
        getBinding().cardPhysical.setOnClickListener(this);
        getBinding().cardYellowPageNew.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if(v.equals(getBinding().cardScoreNew)){

            startActivity(GradeActivity.newInstance(getContext()));

        }else if(v.equals(getBinding().cardCountdownNew)){

            startActivity(CountdownActivity.newInstance(getContext()));
        }else if(v.equals(getBinding().cardLostCard)){
            LostCardActivity.setUrl(WustApi.LOSTCARD_URL);
            startActivity(LostCardActivity.newInstance(getContext()));
        }else if(v.equals(getBinding().cardCreditsStatisticsNew)){

            startActivity(CreditsStatisticsActivity.newInstance(getContext()));

        }else if(v.equals(getBinding().cardEmptyclassroomNew)){
//            startActivity(EmptyClassRoomActivity.newInstance(getContext()));
//            startActivity(SearchClassRoomActivity.newInstance(getContext()));
//            startActivity(SearchCourseActivity.newInstance(getContext()));
            startActivity(NewEmptyClassRoomActivity.newInstance(getContext()));

        }else if(v.equals(getBinding().cardLibraryNew)){

//            ToastUtil.show("图书馆功能暂不可用");
            if(SharePreferenceLab.getIsLibraryLogin()){
                startActivity(LibraryActivity.newInstance(getContext()));
            }else{
                startActivity(LibraryLoginActivity.newInstance(getContext()));
            }
        }else if(v.equals(getBinding().cardSchoolBusNew)){
            startActivity(SchoolBusActivity.newInstance(getContext()));
        }else if(v.equals(getBinding().cardSchoolCalendarNew)){
            startActivity(SchoolCalendarActivity.newInstance(getContext()));
        }else if(v.equals(getBinding().cardPhysical)){

            if(SharePreferenceLab.getInstance().getIsPhysicalLogin(MyApplication.getContext())){
                startActivity(PhysicalDetailActivity.newInstance(getActivity()));
            }else{
                showPhysicalLoginDialog();
            }

        }else if(v.equals(getBinding().cardYellowPageNew)){
            startActivity(YellowPageActivity.newInstance(getContext()));
        }
    }

    @Override
    public void showCycleImageFromNet(List<CycleImageBean> data) {
        getBinding().vpHomeAdNew.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                return new NetImageHolderView(itemView, getActivity());
            }

            @Override
            public int getLayoutId() {
                return R.layout.card_app;
            }
        }, data) //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                //设置指示器是否可见
                .setPointViewVisible(true)
                .setOnItemClickListener(position -> {
                    Log.d(TAG, "showCycleImageFromNet: "+data.get(position).getContent());
                    if(data.get(position).getContent().equals(
                            "https://support.qq.com/products/275699"))
                    {
                        //链接为反馈信息，跳转到反馈界面
                        Intent intentFeedback = FeedBackActivity.newInstance(getContext());
                        startActivity(intentFeedback);
                    }else if(data.get(position).getContent().equals(
                            "https://cpw.h5.xeknow.com/s/bQ1RN")){
                        Uri uri = Uri.parse("https://cpw.h5.xeknow.com/s/bQ1RN");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }else {
                        OtherWebActivity.setName(data.get(position).getTitle());
                        OtherWebActivity.setUrl(data.get(position).getContent());
                        Intent privacy = OtherWebActivity.getInstance(getContext());
                        getActivity().startActivity(privacy);
                    }
                });
    }

    @Override
    public void showCycleImageFromLocal(List<Bitmap> data) {
        getBinding().vpHomeAdNew.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                return new LocalImageHolderView(itemView, getActivity());
            }

            @Override
            public int getLayoutId() {
                return R.layout.card_app;
            }
        }, data)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器，不需要圆点指示器可以不设
                //.setPageIndicator(new int[] { R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused })
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                //设置指示器是否可见
                .setPointViewVisible(true);
    }

    @Override
    public void showPhysicalLoginDialog() {
        if(getActivity()==null)
            return;
        PhysicalLoginDialog physicalLoginDialog = new PhysicalLoginDialog(this);
        physicalLoginDialog.show(getActivity().getSupportFragmentManager(), "PhysicalLogin");
    }

    @Override
    public void showLoadDialog() {
        if(loadingView==null){
            loadingView = MyDialogHelper.createLoadingDialog(getContext(),"登录中...", false);
        }
        loadingView.show();
    }

    @Override
    public void cancelLoadDialog() {
        if(loadingView!=null){
            loadingView.cancel();
        }
    }

    @Override
    public void startPhysicalDetailActivity() {
        startActivity(PhysicalDetailActivity.newInstance(getContext()));
    }

    @Override
    public void loginPhysical(String password) {
        getPresenter().LoginPhysical(password);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBinding().vpHomeAdNew.startTurning();
    }

    @Override
    public void onPause() {
        super.onPause();
        getBinding().vpHomeAdNew.stopTurning();

    }


}
