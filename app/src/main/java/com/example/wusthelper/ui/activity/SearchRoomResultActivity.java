package com.example.wusthelper.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wusthelper.R;
import com.example.wusthelper.base.activity.BaseActivity;
import com.example.wusthelper.bean.javabean.EmptyClassroomBean;
import com.example.wusthelper.bean.javabean.data.EmptyClassroomData;
import com.example.wusthelper.databinding.ActivitySearchRoomResultBinding;
import com.example.wusthelper.helper.MyDialogHelper;
import com.example.wusthelper.request.NewApiHelper;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.utils.ToastUtil;
import com.example.wusthelper.widget.ExpandRecyclerView.Item;
import com.example.wusthelper.widget.ExpandRecyclerView.ItemClickListener;
import com.example.wusthelper.widget.ExpandRecyclerView.Section;
import com.example.wusthelper.widget.ExpandRecyclerView.SectionedExpandableLayoutHelper;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchRoomResultActivity extends BaseActivity<ActivitySearchRoomResultBinding> implements ItemClickListener {

    private static String buildingName = "";
    private static String areaNum = "";
    private static String campusName = "";
    private static String week = "";
    private static String weekDay = "";
    private static String section = "";
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;

    private AlertDialog loadingView;

    public static Intent newInstance(Context context,String mBuildingName,String mAreaNum,String mCampusName,String mWeek,String mWeekDay,String mSection) {
        buildingName = mBuildingName;
        areaNum = mAreaNum;
        campusName = mCampusName;
        week = mWeek;
        weekDay = mWeekDay;
        section = mSection;
        return new Intent(context,SearchRoomResultActivity.class);

    }

    @Override
    public void initView() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(this,
                getBinding().recyclerView, this, 4);
        getBinding().ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();

//        //random data
//        ArrayList<Item> arrayList = new ArrayList<>();
//        arrayList.add(new Item("iPhone", 0));
//        arrayList.add(new Item("iPad", 1));
//        arrayList.add(new Item("iPod", 2));
//        arrayList.add(new Item("iMac", 3));
//        sectionedExpandableLayoutHelper.addSection("Apple Products/1", arrayList);
//        arrayList = new ArrayList<>();
//        arrayList.add(new Item("LG", 0));
//        arrayList.add(new Item("Apple", 1));
//        arrayList.add(new Item("Samsung", 2));
//        arrayList.add(new Item("Motorola", 3));
//        arrayList.add(new Item("Sony", 4));
//        arrayList.add(new Item("Nokia", 5));
//        sectionedExpandableLayoutHelper.addSection("Companies/1", arrayList);
//        arrayList = new ArrayList<>();
//        arrayList.add(new Item("Chocolate", 0));
//        arrayList.add(new Item("Strawberry", 1));
//        arrayList.add(new Item("Vanilla", 2));
//        arrayList.add(new Item("Butterscotch", 3));
//        arrayList.add(new Item("Grape", 4));
//        sectionedExpandableLayoutHelper.addSection("Ice cream/1", arrayList);
//
//        sectionedExpandableLayoutHelper.notifyDataSetChanged();
//
//        //checking if adding single item works
//        sectionedExpandableLayoutHelper.addItem("Ice cream/1", new Item("Tutti frutti",5));
//        sectionedExpandableLayoutHelper.notifyDataSetChanged();
    }

    private void initData(){
        showLoading("正在查询空教室");
        NewApiHelper.findEmptyClassroom(buildingName, areaNum, campusName, week, weekDay, section, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                EmptyClassroomData emptyClassroomData = (EmptyClassroomData) responseObj;
                if(emptyClassroomData.getCode().equals("10000")){
                    if(emptyClassroomData.getData().size()>0){
                        for(EmptyClassroomBean emptyClassroomBean : emptyClassroomData.getData()){
                            String section = "第"+emptyClassroomBean.getFloor()+"层的空闲教室"+"/"+emptyClassroomBean.getCount();
                            ArrayList<Item> arrayList = new ArrayList<>();
                            for(int i = 0; i<emptyClassroomBean.getRooms().size(); i++){
                               arrayList.add(new Item("教室"+emptyClassroomBean.getRooms().get(i),i));
                            }
                            sectionedExpandableLayoutHelper.addSection(section,arrayList);
                        }
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    }else{
                        ToastUtil.show("未查询到空教室");
                    }
                    cancelDialog();
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                cancelDialog();
                ToastUtil.show("请求失败，可能是网络未链接或请求超时");
            }
        });
    }
    @Override
    public void itemClicked(Item item) {

    }

    @Override
    public void itemClicked(Section section) {

    }

    public void showLoading(String msg) {
        loadingView = MyDialogHelper.createLoadingDialog(this,msg, false);
        loadingView.show();
    }

    public void cancelDialog() {
        if (loadingView != null)
            loadingView.cancel();
    }
}