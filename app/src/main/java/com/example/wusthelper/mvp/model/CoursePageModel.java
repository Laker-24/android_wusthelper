package com.example.wusthelper.mvp.model;

import android.util.Log;

import com.example.wusthelper.helper.RoughDataResolver;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.helper.TimeTools;
import com.example.wusthelper.bean.itembean.DateItemForShow;
import com.example.wusthelper.bean.itembean.WeekItemForShow;
import com.example.wusthelper.bean.javabean.CourseBean;
import com.example.wusthelper.dbhelper.CourseDB;
import com.example.wusthelper.bean.javabean.DateBean;
import com.example.wusthelper.request.NewApiHelper;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.bean.itembean.CourseListForShow;
import com.example.wusthelper.helper.ConfigHelper;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoursePageModel {

    private static final String TAG = "CoursePageModel";

    //周一到周日
    private final String[] weekdayEnum = {"周一\n","周二\n","周三\n","周四\n","周五\n","周六\n","周日\n"};

    //周日到周六到
    private final String[] weekdayEnumSundayFirst = {"周日\n","周一\n","周二\n","周三\n","周四\n","周五\n","周六\n"};

    /**
     * 网络请求，获取课程表
     * */
    public void getCourseFromNet(String semester, DisposeDataListener listener) {
        NewApiHelper.getCourse(semester,listener);
    }

    public void getGraduateCourseFromNet( DisposeDataListener listener) {
        NewApiHelper.getGraduateCourse(listener);
    }

    public void getQRCourse(String token,String semester,DisposeDataListener listener) {
        NewApiHelper.getQrCourse(token,semester,listener);
    }
    /**
     * 向数据库储存 课程数据
     * */
    public void saveAllCourseToDB(List<CourseBean> data, String masterSemester, String studentId) {
        //添加之前把原本的数据删除
        CourseDB.deleteCourse(studentId,masterSemester,CourseBean.IS_DEFAULT);
        CourseDB.addAllCourseData(data,studentId,masterSemester,CourseBean.TYPE_COMMON);
    }

    /**
     * 获取指定周数的 用于显示的课表数据
     * */
    public List<CourseListForShow> getCourseShowListFormDB(String studentId ,String semester,int week) {

        Log.d(TAG, "getCourseShowList: ");
        //先定一个列表用于当返回值，再定义一个二维数组，用于添加课程数据
        List<CourseListForShow> listForShows = new ArrayList<>();
        CourseListForShow [][] courseListForShows = new CourseListForShow[6][7];

        for(int section = 0 ; section < 6; section++){
            for(int weekday = 0 ; weekday < 7; weekday++){
                courseListForShows[section][weekday] = new CourseListForShow();
            }
        }

        List<CourseBean> courseBeanList = new ArrayList<>();

        if(!SharePreferenceLab.getIsChooseSundayFirst()){
            Log.d(TAG, "getCourseShowListFormDB: ");
            for(int weekday = 0 ; weekday < 7; weekday++){

                courseBeanList = CourseDB.getCourseInAWeekday(weekday+1+"",studentId,semester);
                //检查获取的课程，查看是否在上课时间，并设置相应的标志，如果不在上课时间，则后续可能显示为灰白色
                //或者进过用户设置不显示非上课时间课程，该课程不显示
                checkCourseIsInClass(courseBeanList,week);

                for(CourseBean courseBean : courseBeanList){
                    //因为数组是从零开始的，但是数据库存的是从一开始的，所以要减一
                    int section = courseBean.getStartTime()-1;
                    if(checkCourseIsShow(courseBean)){

                        addOnShowCourse(courseListForShows[section][weekday],courseBean);
                    }

                }

                courseBeanList.clear();
            }
        }else {
            for(int weekday = 0 ; weekday < 7; weekday++){
                if(weekday == 0){
                    //获取星期天的课程
                    courseBeanList = CourseDB.getCourseInAWeekday("7",studentId,semester);
                }else {
                    courseBeanList = CourseDB.getCourseInAWeekday(weekday+"",studentId,semester);
                }

                //检查获取的课程，查看是否在上课时间，并设置相应的标志，如果不在上课时间，则后续可能显示为灰白色
                //或者进过用户设置不显示非上课时间课程，该课程不显示
                checkCourseIsInClass(courseBeanList,week);

                for(CourseBean courseBean : courseBeanList){
                    //因为数组是从零开始的，但是数据库存的是从一开始的，所以要减一
                    int section = courseBean.getStartTime()-1;
                    if(checkCourseIsShow(courseBean)){

                        addOnShowCourse(courseListForShows[section][weekday],courseBean);
                    }

                }

                courseBeanList.clear();
            }
        }


        //将二维数组的数据 转化为一维，用于做返回数据显示
        for(int section = 0 ; section < 6; section++){
            for(int weekday = 0 ; weekday < 7; weekday++){
                listForShows.add(courseListForShows[section][weekday]);
            }
        }

        return listForShows;
    }

    private void addOnShowCourse(CourseListForShow courseListForShow, CourseBean courseBean) {

        if(courseBean.isInClass()){
            courseListForShow.addListForShowInHead(courseBean);
        }else{
            courseListForShow.addListForShow(courseBean);
        }

    }

    private boolean checkCourseIsShow(CourseBean courseBean) {
        //检测是否是可以显示的课程数据
        if(SharePreferenceLab.getIsShowNotThisWeek()){
            //如果缓存设置的是显示全部，就直接返回True 。否则就进行判断，在上课的课程才返回True
            return true;
        }else {
            return courseBean.isInClass();
        }
    }

    /**
     * 检查课程List是否在本周课程
     * 值得一提的是在以周一为第一天的情况下，周日的课程起始周和结束周要减 1进行计算*/
    private void checkCourseIsInClass(List<CourseBean> courseBeanList, int week) {
        //查看课程时间是否在 给出的week之间,如果不在 week内，在该week显示为灰色
        Log.d(TAG, "checkCourseIsInClass: "+week);
        for(int i = 0; i < courseBeanList.size(); i++){
            //当设置周日为第一天时，且课程是周日的课程，则进行特殊处理(且只对教务处接口获取的课程进行该操作)
            if(SharePreferenceLab.getIsChooseSundayFirst()&&courseBeanList.get(i).getWeekday()==7
                    &&courseBeanList.get(i).getIsDefault()==CourseBean.IS_DEFAULT){
                courseBeanList.get(i).setStartWeek(courseBeanList.get(i).getStartWeek()+1);
                courseBeanList.get(i).setEndWeek(courseBeanList.get(i).getEndWeek()+1);
            } else if (SharePreferenceLab.getIsChooseSundayFirst()&&courseBeanList.get(i).getWeekday()==7
                    &&courseBeanList.get(i).getIsDefault()==CourseBean.IS_MYSELF) {
                //当设置周日为第一天时，且课程是周日的课程，则进行特殊处理(且只对自己添加的课程进行该操作)
                courseBeanList.get(i).setStartWeek(courseBeanList.get(i).getStartWeek()+1);
                courseBeanList.get(i).setEndWeek(courseBeanList.get(i).getEndWeek()+1);
            }
            if(week >=  courseBeanList.get(i).getStartWeek()
                    && week <= courseBeanList.get(i).getEndWeek()){
                courseBeanList.get(i).setInClass(true);
            }else {
                courseBeanList.get(i).setInClass(false);
            }

        }

    }

    /**
     * 用于获取WeekItem所需要的数据
     * */
    public WeekItemForShow getWeekIconListFromDB(String studentId, String semester, int week,int masterWeek, int realWeek) {

        List<CourseBean> roughData =CourseDB.getRoughDataInAWeek(studentId,semester,week);

        List<Integer> integerList = RoughDataResolver.getRoughDataList(roughData);
        return new WeekItemForShow(week,integerList,week==masterWeek,week==realWeek);
    }
    /**
     * 用于获取DateItem所需要的数据
     * */
    public List<DateItemForShow> getDateListFromDB(DateBean dateBean, int week) {
        List<DateItemForShow> list = new ArrayList<>();
        //获得七天（一周）的 date数据
        List<DateBean> dateBeanList;
        if(SharePreferenceLab.getIsChooseSundayFirst()){
            dateBeanList = TimeTools.getDateInAWeekSundayFirst(dateBean, week);
        }else {
            dateBeanList = TimeTools.getDateInAWeek(dateBean, week);
        }


        for(int i=0; i<7; i++){

            DateItemForShow itemForShow = new DateItemForShow();
            if(SharePreferenceLab.getIsChooseSundayFirst()){
                itemForShow.setWeekday(weekdayEnumSundayFirst[i]);
            }else {
                itemForShow.setWeekday(weekdayEnum[i]);
            }
            itemForShow.setDate(dateBeanList.get(i).getMonth() + "/" +dateBeanList.get(i).getDay());
            itemForShow.setIsDay(TimeTools.isToday(dateBeanList.get(i)));
            list.add(itemForShow);
        }
        return list;
    }


    /**
     * 获取到指定周数下的 月份字符串，用于显示
     * */
    public String getMonth(DateBean dateBean, int week) {
        //获得七天（一周）的 date数据
        List<DateBean> dateBeanList = TimeTools.getDateInAWeek(dateBean, week);
        String month = "";
        if(dateBeanList.size()>0){
            month = dateBeanList.get(0).getMonth()+"\n月";
        }
        return month;
    }

    /**
     * 获取新课表后调用，储存当前学期的开始周数据（Date）
     * 计算当前周数据
     * */
    public void saveCurrentWeek() {
        long thisTermStartTimeStr = -1;
        if(!ConfigHelper.get_now_Term_startDate().equals("")){
            thisTermStartTimeStr = Long.parseLong(ConfigHelper.get_now_Term_startDate());
        }
        long thisTermStartTime;
//      如果非本学期会返回本周null
        if (thisTermStartTimeStr == -1){
            String now = TimeTools.getFormatToday();
            Date nowDate = TimeTools.getDate(now);
            if(nowDate == null) return;
            thisTermStartTime = nowDate.getTime();
        }else{
            thisTermStartTime = thisTermStartTimeStr;
        }

        String termStartDateStr = TimeTools.getDateFromTime(thisTermStartTime);
        Log.e(TAG, "SaveCurrentWeek: "+termStartDateStr );
        Date termStartDate = TimeTools.getDate(termStartDateStr);
        int weekday = TimeTools.getWeekday(termStartDate);

        Log.e(TAG, "SaveCurrentWeek: "+weekday );

        //      计算当前周数
        Date currentDate = new Date();
        String currentStr = TimeTools.getDateFromTime(currentDate.getTime());
        DateBean termStartDateBean = new DateBean(termStartDateStr,0,weekday);

        int gap = TimeTools.getRealWeek(termStartDateBean,currentStr);

        SharePreferenceLab.setWeek(gap);
        SharePreferenceLab.setWeekday(TimeTools.getWeekday());
        SharePreferenceLab.setDate(currentStr);
    }

    public void saveRealWeek(int week) {
        int weekday = TimeTools.getWeekday();
        String date = TimeTools.getFormatToday();
        SharePreferenceLab.setWeek(week);
        SharePreferenceLab.setWeekday(weekday);
        SharePreferenceLab.setDate(date);
    }

    public int getCourseSizeFormDB(String studentId, String realSemester) {
        return CourseDB.getCourseInaSemester(studentId,realSemester).size();
    }
}
