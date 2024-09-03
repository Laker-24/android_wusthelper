package com.example.wusthelper.mvp.model;

import android.util.Log;

import com.example.wusthelper.MyApplication;
import com.example.wusthelper.bean.javabean.data.ConfigData;
import com.example.wusthelper.bean.javabean.CourseBean;
import com.example.wusthelper.bean.javabean.DateBean;
import com.example.wusthelper.bean.javabean.data.GraduateData;
import com.example.wusthelper.bean.javabean.data.StudentData;
import com.example.wusthelper.bean.javabean.data.TokenData;
import com.example.wusthelper.dbhelper.CourseDB;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.helper.TimeTools;
import com.example.wusthelper.request.NewApiHelper;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.helper.ConfigHelper;

import java.util.Date;
import java.util.List;

public class LoginModel {
    private static final String TAG = "LoginModel";

    public void login(String studentId, String password, DisposeDataListener listener){
        NewApiHelper.login(studentId,password,listener);
    }

    //研究生登录
    public void loginGraduate(String studentId, String password, DisposeDataListener listener){
        NewApiHelper.loginGraduate(studentId,password,listener);
    }

    public void getUserInfo(DisposeDataListener listener){
        NewApiHelper.getUserInfo(listener);
    }

    public void getGraduateInfo(DisposeDataListener listener){
        NewApiHelper.getGraduateInfo(listener);
    }

    public void getConfig(DisposeDataListener listener) {
        NewApiHelper.getConfig(listener);
    }

    public void getCourse(String semester, DisposeDataListener listener) {
        NewApiHelper.getCourse(semester,listener);
    }

    public void getGraduateCourse(DisposeDataListener listener) {
        NewApiHelper.getGraduateCourse(listener);
    }

    public void saveAllCourseToDB(List<CourseBean> data, String semester) {
        String studentId = SharePreferenceLab.getInstance().getStudentId(MyApplication.getContext());
        Log.d(TAG, "saveAllCourse: "+studentId);
        //添加之前把原本的数据删除
        CourseDB.deleteCourse(studentId,semester,CourseBean.IS_DEFAULT);
        CourseDB.addAllCourseData(data,studentId,semester,CourseBean.TYPE_COMMON);
    }

    public void saveLoginData(TokenData tokenData, String studentId,String password ,String semester) {
        NewApiHelper.setToken(tokenData.getData());
        SharePreferenceLab.setToken(tokenData.getData());

        SharePreferenceLab.setSelectSemester(semester);

        long startTime =Long.parseLong(ConfigHelper.get_now_Term_startDate());
        int week = getCurrentWeek(startTime);
        SharePreferenceLab.getInstance().setData(MyApplication.getContext(), true,
                studentId, TimeTools.getFormatToday(), week, TimeTools.getWeekday(),
                password, semester, true);

        Log.e(TAG, "saveLoginData: startTime = " + startTime);
        Log.e(TAG, "saveLoginData: " + week );

    }

    public void saveStudentInfo(StudentData studentData) {
        SharePreferenceLab.getInstance().setCollege(MyApplication.getContext(),studentData.data.getCollege());
        SharePreferenceLab.getInstance().setMajor(MyApplication.getContext(), studentData.data.getMajor());
        SharePreferenceLab.getInstance().setUsername(MyApplication.getContext(), studentData.data.getStuName());
    }

    public void saveGraduateInfo(GraduateData graduateData) {
        SharePreferenceLab.getInstance().setCollege(MyApplication.getContext(),graduateData.data.getAcademy());
        SharePreferenceLab.getInstance().setMajor(MyApplication.getContext(), graduateData.data.getSpecialty());
        SharePreferenceLab.getInstance().setUsername(MyApplication.getContext(), graduateData.data.getName());
        SharePreferenceLab.setGrade(graduateData.data.getGrade());
        SharePreferenceLab.setDegree(graduateData.data.getDegree());
        SharePreferenceLab.setTutorName(graduateData.data.getTutorName());
    }


    public void saveConfig(ConfigData configData) {
        //接下来储存学期信息
        SharePreferenceLab.setSemester(configData.getData().getCurrentTerm());
        //设置配置信息
        ConfigHelper.setConfigBean(configData);
    }

    private int getCurrentWeek ( long startTermTime){
        String termStartDateStr = TimeTools.getDateFromTime(startTermTime);

        Log.e(TAG, "InitCurrentWeek: str : " + termStartDateStr);
        Date termStartDate = TimeTools.getDate(termStartDateStr);
        Log.d(TAG, "getCurrentWeek: termStartDate = "+termStartDate);
        int weekday = TimeTools.getWeekday(termStartDate);
        Log.e(TAG, "getCurrentWeek: weekday "+weekday );

        Date currentDate = new Date();
        String currentStr = TimeTools.getDateFromTime(currentDate.getTime());
        DateBean dateBean = new DateBean(termStartDateStr, 0, weekday);
        Log.d(TAG, "getCurrentWeek: dateBean="+dateBean);
        Log.d(TAG, "getCurrentWeek: currentStr="+currentStr);
        int gap = TimeTools.getRealWeek(dateBean, currentStr);
        Log.e(TAG, "getCurrentWeek: gap =" + gap );
        return gap;
//        int week = 1 + gap;
//
//        return week;
    }
}
