package com.example.wusthelper.dbhelper;

import android.content.Context;
import android.util.Log;

import com.example.wusthelper.bean.javabean.CourseBean;
import com.example.wusthelper.bean.javabean.GradeBean;
import com.example.wusthelper.bean.javabean.GraduateGradeBean;
import com.example.wusthelper.bean.javabean.WidgetCourseBean;
import com.example.wusthelper.helper.DrawableLab;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.ui.activity.PhysicalDetailActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
/**
 * 封装了一些Course数据库的交互方法
 * */
public class CourseDB {

    private static final String TAG = "CourseDB";
    /**
     * 网络请求获得课程列表后添加到数据库(需要学期作为参数)
     * @param courseBeans
     * @param semester */
    public static void addAllCourseData(List<CourseBean> courseBeans,String studentId,
                                        String semester,int classType){
        for(CourseBean bean : courseBeans){
            addOneCourse(bean,studentId,semester,classType);
        }
    }

    /**
     * 删除所有课程信息，在退出登录的时候使用*/
    public static void deleteOneCourse(long id){
        LitePal.delete(CourseBean.class,id);
    }

    /**
     * 删除所有课程信息，在退出登录的时候使用*/
    public static void deleteAllCourse(){
        LitePal.deleteAll(CourseBean.class);
    }

    /**
     * 根据学期删除课表信息，用于更新课表，删除旧数据
     * @param studentId
     * @param isDefault
     * @param semester */
    public static void deleteCourse(String studentId,String semester,int isDefault){
        LitePal.deleteAll(CourseBean.class,"studentId = ? " +
                        "and isDefault = ?" +
                        "and semester = ? " ,
                studentId,isDefault+"",semester);
    }

    /**
     * 删除情侣课表数据
     */
    public static void deleteQrCourse() {
        LitePal.deleteAll(CourseBean.class,"classType = ? ",
                CourseBean.TYPE_QR+"");
    }

    /**
     * 查询是否添加过情侣课表
     * @return
     */
    public static boolean queryQrCourse(){
        List<CourseBean> courseBeanList = LitePal.where("classType = ? ",
                CourseBean.TYPE_QR+"").find(CourseBean.class);
        if(courseBeanList.size()>0) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 添加一个课程数据
     * @param courseBean
     * @param studentId
     * @param semester
     * @param classType*/
    public static void addOneCourse(CourseBean courseBean, String studentId, String semester, int classType){
        int color = DrawableLab.getDrawableIndex(courseBean.getCourseName());
        courseBean.setStudentId(studentId);
        courseBean.setColor(color);
        courseBean.setSemester(semester);
        courseBean.setClassType(classType);
        courseBean.save();
    }

    /**
     * 获取指定学期的课程
     * @param studentId
     * @param semester
     * */
    public static List<CourseBean> getCourseInaSemester(String studentId, String semester){
        return LitePal.where("studentId = ?" +
                "and semester = ?",studentId,semester).find(CourseBean.class);
    }

    /**
     * 获取指定weekday的课程数据
     * @param weekday
     * @param studentId
     * @param semester
     * */
    public static List<CourseBean> getCourseInAWeekday(String weekday, String studentId, String semester){


        if(CourseDB.queryQrCourse()&&SharePreferenceLab.getIsGetQr()){
            return LitePal.where("weekday = ? " +
                    "and studentId = ?" +
                    "and semester = ?"+
                    "and classType = ? "
                    ,weekday,studentId,semester,CourseBean.TYPE_QR+"").find(CourseBean.class);
        }else{
            return LitePal.where("weekday = ? " +
                    "and studentId = ?" +
                    "and semester = ?" +
                    "and classType <> ? ",weekday,studentId,semester,CourseBean.TYPE_QR+"").find(CourseBean.class);
        }

    }

    /**
     * 获取指定week可能出现的课
     * 目前用于weekItem的使用，显示粗略的数据点
     * */
    public static List<CourseBean> getRoughDataInAWeek(String studentId, String semester, int week) {
        List<CourseBean> list =new ArrayList<>();
        List<CourseBean> listNew = new ArrayList<>();
        if(CourseDB.queryQrCourse()&&SharePreferenceLab.getIsGetQr()){
            list = LitePal.where(
                            "studentId = ?" +
                            "and semester = ?"+
                            "and classType = ? "
                    ,studentId,semester,CourseBean.TYPE_QR+"").find(CourseBean.class);
        }else{
            list = LitePal.where(
                    "studentId = ?" +
                    "and semester = ?" +
                    "and classType <> ? ",studentId,semester,CourseBean.TYPE_QR+"").find(CourseBean.class);
        }
//        list = LitePal.findAll(CourseBean.class);
//        list=LitePal.where("startWeek <= ? " +
//                "and endWeek >= ?" +
//                "and studentId = ?" +
//                "and semester = ?",week+"",week+"",studentId,semester).find(CourseBean.class);
        for(int i = 0; i < list.size(); i++){
            //当设置周一为第一天时，且课程是周日的课程，则进行特殊处理(且只对教务处接口获取的课程进行该操作)
            if(SharePreferenceLab.getIsChooseSundayFirst()&&list.get(i).getWeekday()==7
                    &&list.get(i).getIsDefault()==CourseBean.IS_DEFAULT){
                list.get(i).setStartWeek(list.get(i).getStartWeek()+1);
                list.get(i).setEndWeek(list.get(i).getEndWeek()+1);
            } else if (SharePreferenceLab.getIsChooseSundayFirst()&&list.get(i).getWeekday()==7
                    &&list.get(i).getIsDefault()==CourseBean.IS_MYSELF) {
                //当设置周日为第一天时，且课程是周日的课程，则进行特殊处理(且只对自己添加的课程进行该操作)
                list.get(i).setStartWeek(list.get(i).getStartWeek()+1);
                list.get(i).setEndWeek(list.get(i).getEndWeek()+1);
            }
            if(list.get(i).getStartWeek() <= week && list.get(i).getEndWeek() >= week) {
                listNew.add(list.get(i));
            }

        }
        return listNew;
    }

    /**
     * 用于今日课本数据获取
     * 获取指定weekday出现的课
     * 目前用于weekItem的使用，显示粗略的数据点
     * */
    public static List<CourseBean> getTodayCourse(String studentId, String semester, int week, int weekday,int section) {

        List<CourseBean> list =new ArrayList<>();
//        list=LitePal.where("startWeek <= ? " +
//                "and endWeek >= ?" +
//                "and studentId = ?" +
//                "and semester = ?" +
//                "and weekday = ?"+
//                "and startTime >= ?",week+"",week+"",studentId,semester,weekday+"",section+"")
//                .order("startTime asc")
//                .find(CourseBean.class);
        list=LitePal.where("startWeek <= ? " +
                "and endWeek >= ?" +
                "and studentId = ?" +
                "and semester = ?" +
                "and weekday = ?"+
                "and startTime >= ?"+
                "and classType <> ? ",week+"",week+"",studentId,semester,weekday+"",section+"",CourseBean.TYPE_QR+"")
                .order("startTime asc")
                .find(CourseBean.class);
        return list;
    }

    public static List<CourseBean> getCoursesByID(long courseID) {
        return LitePal.where(
                "id = ?",courseID+"").find(CourseBean.class);
    }

    /**
     * 注销，删除课程数据
     * */
    public static void logout() {

        LitePal.deleteAll(GradeBean.class);
        LitePal.deleteAll(GraduateGradeBean.class);

    }

    public static List<CourseBean> getPhysicalCourse(String studentId, String semester,int typePhysical) {
        return LitePal.where(
                "studentId = ?" +
                "and semester = ?" +
                "and classType = ?",studentId,semester,typePhysical+"").order("startWeek asc").find(CourseBean.class);
    }

    /**
     * 删除所有物理课程
     * */
    public static void removePhysicalData(String studentId,int typePhysical,String semester) {
        LitePal.deleteAll(CourseBean.class,
                "classType = ?"+
                        "and studentId = ? " +
                        "and semester = ? ", typePhysical+"",studentId,semester);
    }

    public static boolean isHavingCourse() {
        return LitePal.findAll(CourseBean.class).size()!=0;
    }
}
