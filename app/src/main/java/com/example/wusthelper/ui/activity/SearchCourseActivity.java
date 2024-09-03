package com.example.wusthelper.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.wusthelper.R;
import com.example.wusthelper.adapter.CourseInfoAdapter;
import com.example.wusthelper.adapter.CourseListAdapter;
import com.example.wusthelper.adapter.SearchCollegeAdapter;
import com.example.wusthelper.base.activity.BaseActivity;
import com.example.wusthelper.bean.javabean.CollegeBean;
import com.example.wusthelper.bean.javabean.CourseNameBean;
import com.example.wusthelper.bean.javabean.SearchCourseBean;
import com.example.wusthelper.bean.javabean.data.CollegeData;
import com.example.wusthelper.bean.javabean.data.CourseNameData;
import com.example.wusthelper.bean.javabean.data.SearchBookData;
import com.example.wusthelper.bean.javabean.data.SearchCourseData;
import com.example.wusthelper.databinding.ActivitySearchCourseBinding;
import com.example.wusthelper.helper.MyDialogHelper;
import com.example.wusthelper.request.NewApiHelper;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.utils.ToastUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 偷懒把三个页面融合在一起了,时间很紧写的很糙。
 */
public class SearchCourseActivity extends BaseActivity<ActivitySearchCourseBinding> implements View.OnClickListener{

    public static final String COLLEGE_ID = "collegeId";
    public static final String COLLEGE_NAME = "collegeName";
    public static final String COURSE_NAME = "courseName";
    public static final String TAGE = "tage";
    private static final String TAG = "SearchCourseActivity";

    private AlertDialog loadingView;

    private List<CollegeBean> collegeBeanList = new ArrayList<>();
    private SearchCollegeAdapter searchCollegeAdapter;

    private List<CourseNameBean> courseNameBeans = new ArrayList<>();
    private CourseListAdapter courseListAdapter;

    private List<SearchCourseBean> searchCourseBeans = new ArrayList<>();
    private CourseInfoAdapter courseInfoAdapter;

    //显示页面 1为学院 2为课程列表 3为详细课程
    private int isCollegePage = 1;
    //学院id
    private String collegeId;
    private String collegeName;
    private String courseName;
    private String tage = "0";

    private int pageNum = 1;
    //是否有更多
    private boolean isMore = true;

    public static Intent newInstance(Context context){
        return new Intent(context,SearchCourseActivity.class);
    }

    public static Intent newInstance(Context context,String collegeId,String collegeName){
        Intent intent = new Intent(context,SearchCourseActivity.class);
        intent.putExtra(COLLEGE_ID,collegeId);
        intent.putExtra(COLLEGE_NAME,collegeName);
        return intent;
    }

    public static Intent newInstance(Context context,String collegeId,String courseName,String tage){
        Intent intent = new Intent(context,SearchCourseActivity.class);
        intent.putExtra(COLLEGE_ID,collegeId);
        intent.putExtra(COURSE_NAME,courseName);
        intent.putExtra(TAGE,tage);
        return intent;
    }




    @Override
    public void initView() {
        if(getIntent().getStringExtra(TAGE)!=null){
            Log.d(TAG,"课程详情");
            tage = getIntent().getStringExtra(TAGE);
            if(tage.equals("3")) {
                Log.d(TAG,"isCollegePage = 3");
                collegeId = getIntent().getStringExtra(COLLEGE_ID);
                courseName = getIntent().getStringExtra(COURSE_NAME);
                isCollegePage = 3;
                getBinding().toolbar.setText(courseName);
                getBinding().ivSearch.setVisibility(View.GONE);
            }else if(tage.equals("1") || tage.equals("2")){
                collegeId = getIntent().getStringExtra(COLLEGE_ID);
                courseName = getIntent().getStringExtra(COURSE_NAME);
                isCollegePage = 4;
                getBinding().toolbar.setText(courseName);
                getBinding().ivSearch.setVisibility(View.GONE);
            }
        }
        if(getIntent().getStringExtra(COLLEGE_ID)!=null&&getIntent().getStringExtra(COURSE_NAME)==null){
            collegeId = getIntent().getStringExtra(COLLEGE_ID);
            isCollegePage = 2;
        }
        if(getIntent().getStringExtra(COLLEGE_NAME)!=null){
            collegeName = getIntent().getStringExtra(COLLEGE_NAME);
            getBinding().toolbar.setText(collegeName);
        }
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        getBinding().searchCourseRecycler.setLayoutManager(new LinearLayoutManager(SearchCourseActivity.this));
        getBinding().ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setListener();
        initPage();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(getBinding().ivBack)){
            finish();
        }else if(v.equals(getBinding().ivSearch)){
            showSearch();
        }else if(v.equals(getBinding().ivSearchCancel)){
            cancelSearch();
        }
    }

    private void showSearch() {
        getBinding().etSearchCourse.setVisibility(View.VISIBLE);
        getBinding().ivSearchCancel.setVisibility(View.VISIBLE);
        getBinding().ivSearch.setVisibility(View.GONE);
        getBinding().toolbar.setVisibility(View.GONE);
        getBinding().etSearchCourse.setFocusable(true);
        getBinding().etSearchCourse.setFocusableInTouchMode(true);
        getBinding().etSearchCourse.requestFocus();
        //activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getBinding().etSearchCourse,0);
        getBinding().etSearchCourse.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if(!v.getText().toString().equals("")){
//                    String key = v.getText().toString();
//                    Log.d(TAG,"key = "+key);
//                    startActivity(SearchCourseActivity.newInstance(SearchCourseActivity.this,collegeId,key,isCollegePage+""));
//                }
//                    return true;
//                }
//                return true;
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(!v.getText().toString().equals("")){
                        String key = v.getText().toString();
                        Log.d(TAG,"key = "+key);
                        startActivity(SearchCourseActivity.newInstance(SearchCourseActivity.this,collegeId,key,isCollegePage+""));
//                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void cancelSearch() {
        getBinding().etSearchCourse.setVisibility(View.GONE);
        getBinding().ivSearchCancel.setVisibility(View.GONE);
        getBinding().ivSearch.setVisibility(View.VISIBLE);
        getBinding().toolbar.setVisibility(View.VISIBLE);
        getBinding().etSearchCourse.setText("");
        InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setListener(){
        getBinding().ivSearch.setOnClickListener(this);
        getBinding().ivSearchCancel.setOnClickListener(this);
        getBinding().ivBack.setOnClickListener(this);
    }

    private void initPage(){
        if(isCollegePage==1){
            initCollege();
        }else if(isCollegePage == 2){
            initCourse();
            getBinding().srSearch.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if(isMore){
                        if (isMore) {
                            pageNum++;
                            initCourseData(collegeId,pageNum+"");
                        } else {
                            getBinding().srSearch.finishLoadMore(500);
                        }
                    }
                }
            });
        }else if(isCollegePage == 3){
            initCourseInfo();
            getBinding().srSearch.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if(isMore){
                        if (isMore) {
                            pageNum++;
                            initCourseInfoData(collegeId,courseName,pageNum+"");
                        } else {
                            getBinding().srSearch.finishLoadMore(500);
                        }
                    }
                }
            });
        }else if(isCollegePage == 4){
            initCourseInfoSearch();
            getBinding().srSearch.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if(isMore){
                        if (isMore) {
                            pageNum++;
                            initCourseInfoSearchData(collegeId,courseName,pageNum+"");
                        } else {
                            getBinding().srSearch.finishLoadMore(500);
                        }
                    }
                }
            });
        }
    }

    /**
     * 初始化学院recyclerView
     */
    private void initCollege(){
        searchCollegeAdapter = new SearchCollegeAdapter(SearchCourseActivity.this,collegeBeanList);
        getBinding().searchCourseRecycler.setAdapter(searchCollegeAdapter);
        searchCollegeAdapter.setOnItemClickListener(new SearchCollegeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(SearchCourseActivity.newInstance(SearchCourseActivity.this,collegeBeanList.get(position).getId(),collegeBeanList.get(position).getCollegeName()));
            }
        });
        initCollegeData();
    }

    /**
     * 获得学院数据
     */
    private void initCollegeData(){
        showLoading("正在加载学院");
        NewApiHelper.getCollegeList(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                CollegeData collegeData = (CollegeData) responseObj;
                if(collegeData.getCode().equals("10000")){
                    collegeBeanList.addAll(collegeData.getData());
                    searchCollegeAdapter.notifyDataSetChanged();
                }else{
                    ToastUtil.show(collegeData.getMsg());
                }
                cancelDialog();
            }

            @Override
            public void onFailure(Object reasonObj) {
                cancelDialog();
                ToastUtil.show("请求失败，可能是网络未链接或请求超时");
            }
        });
    }

    /**
     * 初始化课程列表recyclerView
     */
    private void initCourse(){
        courseListAdapter = new CourseListAdapter(SearchCourseActivity.this,courseNameBeans);
        getBinding().searchCourseRecycler.setAdapter(courseListAdapter);
        courseListAdapter.setOnItemClickListener(new CourseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG,"courseListAdapter : "+collegeId+courseName);
                startActivity(SearchCourseActivity.newInstance(SearchCourseActivity.this,collegeId,courseNameBeans.get(position).getCourseName(),"3"));
            }
        });
        initCourseData(collegeId,pageNum+"");
    }

    /**
     * 获得课程列表数据
     * @param collegeId
     * @param pageNum
     */
    private void initCourseData(String collegeId,String pageNum){
        if(pageNum.equals("1")){
            showLoading("正在获取课程");
        }
        NewApiHelper.getCourseNameList(collegeId, pageNum, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                CourseNameData courseNameData = (CourseNameData) responseObj;
                if(courseNameData.getCode().equals("10000")){
                    courseNameBeans.addAll(courseNameData.getData());
                    courseListAdapter.notifyDataSetChanged();
                }else {
                    isMore = false;
                }
                cancelDialog();
                getBinding().srSearch.finishLoadMore();
            }

            @Override
            public void onFailure(Object reasonObj) {
                cancelDialog();
                ToastUtil.show("请求失败，可能是网络未链接或请求超时");
            }
        });
    }

    /**
     * 初始化搜索课程详情
     */
    private void initCourseInfoSearch(){
        courseInfoAdapter = new CourseInfoAdapter(SearchCourseActivity.this,searchCourseBeans);
        getBinding().searchCourseRecycler.setAdapter(courseInfoAdapter);
        initCourseInfoSearchData(collegeId,courseName,pageNum+"");
    }

    /**
     * 获取课程详细数据
     * @param collegeId
     * @param courseName
     * @param pageNum
     */
    private void initCourseInfoSearchData(String collegeId,String courseName,String pageNum){
        if(pageNum.equals("1")){
            showLoading("正在获取课程");
        }
        if(tage.equals("1")){
            NewApiHelper.searchALL(courseName, pageNum, new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                    SearchCourseData searchCourseData = (SearchCourseData) responseObj;
                    if(searchCourseData.getCode().equals("10000")){
                        searchCourseBeans.addAll(searchCourseData.getData());
                        courseInfoAdapter.notifyDataSetChanged();

                    }else{
                        ToastUtil.show(searchCourseData.getMsg());
                        getBinding().srSearch.finishLoadMore();
                    }
                    cancelDialog();
                }

                @Override
                public void onFailure(Object reasonObj) {
                    cancelDialog();
                    ToastUtil.show("请求失败，可能是网络未链接或请求超时");
                }
            });
        }else if(tage.equals("2")){
            NewApiHelper.searchInCollege(collegeId, courseName, pageNum, new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                    SearchCourseData searchCourseData = (SearchCourseData) responseObj;
                    if(searchCourseData.getCode().equals("10000")){
                        searchCourseBeans.addAll(searchCourseData.getData());
                        courseInfoAdapter.notifyDataSetChanged();

                    }else{
                        ToastUtil.show(searchCourseData.getMsg());
                        getBinding().srSearch.finishLoadMore();
                    }
                    cancelDialog();
                }
                @Override
                public void onFailure(Object reasonObj) {
                    cancelDialog();
                    ToastUtil.show("请求失败，可能是网络未链接或请求超时");
                }
            });
        }
    }

    /**
     * 获取课程详细数据
     * @param collegeId
     * @param courseName
     * @param pageNum
     */
    private void initCourseInfoData(String collegeId,String courseName,String pageNum){
        if(pageNum.equals("1")){
            showLoading("正在获取课程");
        }
        NewApiHelper.getCourseInfo(collegeId, courseName, pageNum, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                SearchCourseData searchCourseData = (SearchCourseData) responseObj;
                if(searchCourseData.getCode().equals("10000")){
                    searchCourseBeans.addAll(searchCourseData.getData());
                    courseInfoAdapter.notifyDataSetChanged();

                }else{
                    ToastUtil.show(searchCourseData.getMsg());
                    getBinding().srSearch.finishLoadMore();
                }
                cancelDialog();
            }

            @Override
            public void onFailure(Object reasonObj) {
                cancelDialog();
                ToastUtil.show("请求失败，可能是网络未链接或请求超时");
            }
        });
    }


    /**
     * 初始化课程详情
     */
    private void initCourseInfo(){
        courseInfoAdapter = new CourseInfoAdapter(SearchCourseActivity.this,searchCourseBeans);
        getBinding().searchCourseRecycler.setAdapter(courseInfoAdapter);
        initCourseInfoData(collegeId,courseName,pageNum+"");
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