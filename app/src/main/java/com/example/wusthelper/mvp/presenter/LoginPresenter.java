package com.example.wusthelper.mvp.presenter;

import android.util.Log;

import com.example.wusthelper.R;
import com.example.wusthelper.base.BasePresenter;
import com.example.wusthelper.MyApplication;
import com.example.wusthelper.bean.javabean.data.GraduateData;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.bean.javabean.data.ConfigData;
import com.example.wusthelper.bean.javabean.data.CourseData;
import com.example.wusthelper.bean.javabean.data.StudentData;
import com.example.wusthelper.bean.javabean.data.TokenData;

import com.example.wusthelper.mvp.model.LoginModel;
import com.example.wusthelper.mvp.view.LoginView;
import com.example.wusthelper.helper.ConfigHelper;
import com.example.wusthelper.request.okhttp.exception.OkHttpException;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.ui.dialog.ErrorLoginDialog;
import com.example.wusthelper.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter extends BasePresenter<LoginView> {

    private static final String TAG = "LoginPresenter";

    private String studentId;
    private String password ;
    private String semester;
    private final LoginModel loginModel;

    //记录本科生登录错误次数
    private Map<String,Integer> errorStudentMap = new HashMap<String,Integer>();
    //是否点击了登录按钮，默认为没有点击
    private static boolean isGetLogin = false;

    public LoginPresenter(){
        this.loginModel = new LoginModel();
    }

    @Override
    public void initPresenterData() {

    }

    //获取配置信息可能在两种情况下调用：1，进入登录界面就进行请求，获取配置信息,如果成功，则正常进行后续步骤，第二次调用不会发生。
    //                            2，第一次请求没有成功，则在点击登录按钮的时候，确实学期数据，所以要再次获取配置信息
    public void getConfig(){

        loginModel.getConfig(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                ConfigData configData = (ConfigData) responseObj;
                if(configData.getCode().equals("0")&& configData.getMsg().equals("成功")){
                    loginModel.saveConfig(configData);
                    if(isGetLogin){
                        //如果是因为点击登录按钮进入获取的配置信息，到这里证明获取到了需要的学期数据，发起一次获取课程请求
                        login(studentId,password);
                    }else{
                        //如果有更新就弹窗更新公告
                        if(ConfigHelper.getIfHasNewVersion()){
                            getView().showUpdateDialog(ConfigHelper.getConfigBean().getData());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.d(TAG, "onFailure: "+ reasonObj.toString());
                if(isGetLogin){
                    ToastUtil.show("请求失败，可能是网络未链接或请求超时");
                    getView().onLoadingCancel();
                }

            }
        });

    }

    public void login(String studentId, String password){
        //该账号是否错误登陆过
        boolean errorLogin = false;
        //将studentId与map里的学号比对，如过有就不再存入
        for( Map.Entry<String,Integer> entry : errorStudentMap.entrySet()){
            if(studentId.equals(entry.getKey())){
                errorLogin =true;
                break;
            }
        }
        //没有错误登录初始化错误登录次数为0
        if(!errorLogin) {
            errorStudentMap.put(studentId,0);
        }
        getView().onLoadingShow("登录中...",false);
        this.studentId = studentId;
        this.password  = password ;
        this.semester  = ConfigHelper.get_currentTerm() ;

        //如果当前学期不为空 ，则进行登录，如果学期为空，则证明配置信息没有请求，就再次请求配置接口，保证学期不为空
        if(!semester.equals("")){
            //判断是否为研究生
            if(SharePreferenceLab.getIsGraduate()){
                //研究生登录入口
                loginModel.loginGraduate(studentId, password, new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        TokenData tokenData = (TokenData) responseObj;
                        Log.e(TAG,"登录token :" + tokenData.getData());
                        if(tokenData.getCode().equals("10000")){
                            loginModel.saveLoginData(tokenData,studentId,password,semester);
                            //获取学生基本信息
                            getUserInfo();
                            getCourse();
                        }else if(tokenData.getCode().equals("80002")){
                            getView().onLoadingCancel();
                            getView().onToastShow("账号或密码错误");
                        }else {
                            getView().onLoadingCancel();
                            getView().onToastShow(tokenData.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        Log.d(TAG, "onFailure: "+reasonObj.toString());
                        ToastUtil.show("请求失败，可能是网络未链接或请求超时");
                        getView().onLoadingCancel();
                    }
                });
            }else {
                //本科生登录入口
                loginModel.login(studentId, password, new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        TokenData tokenData = (TokenData) responseObj;
                        if(tokenData.getCode().equals("10000")|| tokenData.getCode().equals("11000")){
                            loginModel.saveLoginData(tokenData,studentId,password,semester);
                            //获取学生基本信息
                            getUserInfo();
                            getCourse();
                        }else if(tokenData.getCode().equals("10001")){
                            getView().onLoadingCancel();
                            getView().onToastShow("请将密码或者学号填写完整");
                        }else {
                            errorStudentMap.put(studentId,errorStudentMap.get(studentId)+1);
                            getView().onLoadingCancel();
                            getView().onToastShow(tokenData.getMsg());
                            if(  errorStudentMap.get(studentId) == 3){
                                Log.e(TAG,"errorStudentMap.get(studentId) = " +errorStudentMap.get(studentId));
                                ErrorLoginDialog.errorTitle = MyApplication.getContext().getString(R.string.notice_errorTitle_01);
                                ErrorLoginDialog.errorContent = MyApplication.getContext().getString(R.string.notice_errorContent_01);
                                getView().showErrorDialog();
                            } else if (errorStudentMap.get(studentId) > 4){
                                Log.e(TAG,"errorStudentMap.get(studentId) = " +errorStudentMap.get(studentId));
                                ErrorLoginDialog.errorTitle = MyApplication.getContext().getString(R.string.notice_errorTitle_02);
                                ErrorLoginDialog.errorContent = MyApplication.getContext().getString(R.string.notice_errorContent_02);
                                getView().showErrorDialog();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        Log.d(TAG, "onFailure: "+reasonObj.toString());
                        ToastUtil.show("请求失败，可能是网络未链接或请求超时");
                        getView().onLoadingCancel();
                    }
                });
            }

        }else {
            //没有学期数据，需要请求配置接口获取数据
            isGetLogin = true ;
            getConfig();
        }

    }

    public void getUserInfo(){
        //判断是否为研究生
        if(SharePreferenceLab.getIsGraduate()){
            //获取研究生信息
            this.loginModel.getGraduateInfo(new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                    Log.e(TAG, "成功");
                    GraduateData graduateData = (GraduateData) responseObj;
                    Log.e(TAG,"graduateData.getCode()" + graduateData.getCode());
                    if(graduateData.getCode().equals("10000")){
                        loginModel.saveGraduateInfo((GraduateData) responseObj);
                    }
                }

                @Override
                public void onFailure(Object reasonObj) {
                    Log.e(TAG, "失败");
                    OkHttpException e = (OkHttpException) reasonObj;
                    String msg = e.getEmsg().toString();
                    Log.d(TAG, "onFailure: " + msg);
                }
            });
        }else{
            //获取本科生信息
            this.loginModel.getUserInfo(new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                    StudentData studentData = (StudentData) responseObj;
                    if(studentData.getCode().equals("10000")|| studentData.getCode().equals("11000")){
                        loginModel.saveStudentInfo((StudentData) responseObj);
                    }
                }

                @Override
                public void onFailure(Object reasonObj) {
                    Log.d(TAG, "onFailure: "+reasonObj.toString());
                }
            });
        }
    }

    public void getCourse(){

        getView().onLoadingCancel();
        getView().onLoadingShow("登录成功，正在请求课表...",false);
        //判断是否为研究生
        if(SharePreferenceLab.getIsGraduate()) {
            //获取研究生课程
            this.loginModel.getGraduateCourse(new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                    CourseData courseData = (CourseData) responseObj;
                    if(courseData.getCode().equals("10000")){
                        loginModel.saveAllCourseToDB(courseData.data,semester);
                    }else{
                        getView().onToastShow("登录成功，但是课表获取失败，可能是教务处不稳定");
                    }
                    getView().onLoadingCancel();
                    getView().openMainActivity();
                }

                @Override
                public void onFailure(Object reasonObj) {
                    Log.d(TAG, "onFailure: "+reasonObj.toString());
                    getView().onToastShow("登录成功，但是课表获取失败，可能是教务处不稳定或者请求超时");
                    getView().onLoadingCancel();
                    getView().openMainActivity();
                }
            });
        }else {
            this.loginModel.getCourse(semester,new DisposeDataListener() {
                //获取本科生课程
                @Override
                public void onSuccess(Object responseObj) {
                    CourseData courseData = (CourseData) responseObj;
                    if(courseData.getCode().equals("10000")|| courseData.getCode().equals("11000")){
                        loginModel.saveAllCourseToDB(courseData.data,semester);
                    }else{
                        getView().onToastShow("登录成功，但是课表获取失败，可能是教务处不稳定");
                    }
                    getView().onLoadingCancel();
                    getView().openMainActivity();
                }

                @Override
                public void onFailure(Object reasonObj) {
                    Log.d(TAG, "onFailure: "+reasonObj.toString());
                    getView().onToastShow("登录成功，但是课表获取失败，可能是教务处不稳定或者请求超时");
                    getView().onLoadingCancel();
                    getView().openMainActivity();
                }
            });
        }

    }

    public boolean getIsConfirmPolicy() {
        return SharePreferenceLab.getInstance().get_is_confirm_policy(MyApplication.getContext());
    }
}
