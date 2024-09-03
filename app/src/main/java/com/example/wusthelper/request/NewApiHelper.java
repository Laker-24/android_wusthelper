package com.example.wusthelper.request;

import com.example.wusthelper.bean.javabean.CountDownAddData;
import com.example.wusthelper.bean.javabean.CountDownBean;
import com.example.wusthelper.bean.javabean.CountDownChangeData;
import com.example.wusthelper.bean.javabean.data.AnnouncementContentData;
import com.example.wusthelper.bean.javabean.data.AnnouncementData;
import com.example.wusthelper.bean.javabean.data.BaseData;
import com.example.wusthelper.bean.javabean.data.BookData;
import com.example.wusthelper.bean.javabean.data.CollegeData;
import com.example.wusthelper.bean.javabean.data.ConfigData;
import com.example.wusthelper.bean.javabean.data.CourseData;
import com.example.wusthelper.bean.javabean.data.CourseNameData;
import com.example.wusthelper.bean.javabean.data.CreditsData;
import com.example.wusthelper.bean.javabean.data.CycleImageData;
import com.example.wusthelper.bean.javabean.data.EmptyClassroomData;
import com.example.wusthelper.bean.javabean.data.GradeData;
import com.example.wusthelper.bean.javabean.data.GraduateData;
import com.example.wusthelper.bean.javabean.data.GraduateGradeData;
import com.example.wusthelper.bean.javabean.data.LibCollectData;
import com.example.wusthelper.bean.javabean.LibraryAnnouncementBean;
import com.example.wusthelper.bean.javabean.data.LibraryHistoryData;
import com.example.wusthelper.bean.javabean.data.LostData;
import com.example.wusthelper.bean.javabean.data.NoticeData;
import com.example.wusthelper.bean.javabean.data.SearchBookData;
import com.example.wusthelper.bean.javabean.data.SearchCourseData;
import com.example.wusthelper.bean.javabean.data.StudentData;
import com.example.wusthelper.bean.javabean.data.TokenData;

import com.example.wusthelper.bean.javabean.CountDownData;
import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.request.okhttp.request.RequestParams;
import com.example.wusthelper.utils.CountDownUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Response;

/**
 * 请求中心，所以的api调用都在这里进行
 * 这里面所有的请求，都会默认带上token
 */
public class NewApiHelper {

    private static final String TAG = "NewApiHelper";

    /**
     * 判断新接口是否登陆
     *
     * @return
     */
    public static boolean isLogin() {
        return RequestCenter.isLogin();
    }

    public static String getToken() {
        return RequestCenter.getToken();
    }

    public static void setToken(String token) {
        RequestCenter.setToken(token);
    }

    public static void setMessage(String message) {
        RequestCenter.setMessage(message);
    }

    /**
     * 用户登陆请求
     */
    public static void login(String studentId, String password, DisposeDataListener listener) {

        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("stuNum", studentId);
        params.put("jwcPwd", password);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Platform", "android");
        RequestCenter.postRequest(WustApi.LOGIN_API, params, headers, listener, TokenData.class);
    }

    /**
     * 用户登陆请求,研究生
     */
    public static void loginGraduate(String studentId, String password, DisposeDataListener listener) {

        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
//        params.put("username", studentId);
//        params.put("password", password);
//        headers.put("Content-Type", "application/json");
//        headers.put("Platform", "android");
//        RequestCenter.postJsonRequest(WustApi.LOGIN_GRADUATE_API, params,headers, listener, TokenData.class);
        params.put("stuNum", studentId);
        params.put("jwcPwd", password);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Platform", "android");
        RequestCenter.postRequest(WustApi.LOGIN_GRADUATE_API, params, headers, listener, TokenData.class);
    }

    /**
     * 同步请求登录
     * 专用于token失效以后的重新请求
     */
    public static Response login(String username, String password) throws IOException {
        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("stuNum", username);
        params.put("jwcPwd", password);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Platform", "android");
        return RequestCenter.postRequestExecute(WustApi.LOGIN_API, params, headers, TokenData.class);
    }

    /**
     * 同步请求登录
     * 专用于token失效以后的重新请求,研究生
     */
    public static Response loginGraduate(String username, String password) throws IOException {
        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        headers.put("Content-Type", "application/json");
        headers.put("Platform", "android");
        return RequestCenter.postJsonRequestExecute(WustApi.LOGIN_GRADUATE_API, params,headers, TokenData.class);
    }

    /**
     * 用于获取学生基础信息
     */
    public static void getUserInfo(DisposeDataListener listener) {
        RequestCenter.get(WustApi.INFO_API, null, listener, StudentData.class);
    }

    /**
     * 用于获取研究生基础信息
     */
    public static void getGraduateInfo(DisposeDataListener listener) {
        RequestCenter.get(WustApi.GRADUATE_INFO_API, null, listener, GraduateData.class);
    }


    /**
     * 获取课程表
     */
    public static void getCourse(String semester, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("schoolTerm", semester);
        RequestCenter.get(WustApi.CURRICULUM_API, params, listener, CourseData.class);
    }

    /**
     * 获取情侣课表
     * @param token
     * @param semester
     * @param listener
     */
    public static void getQrCourse(String token,String semester, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("schoolTerm", semester);
        RequestCenter.getQr(token,WustApi.CURRICULUM_API, params, listener, CourseData.class);
    }

    /**
     * 获取研究生课程表
     */
    public static void getGraduateCourse( DisposeDataListener listener) {
        RequestCenter.get(WustApi.GRADUATE_CURRICULUM_API, null, listener, CourseData.class);
    }

    /**
     * 检查Token是否过期
     */
    public static void getCheckToken(DisposeDataListener listener) {
        RequestCenter.get(WustApi.CHECK_TOKEN, null, listener, BaseData.class);
    }

    /**
     * 管理端接口，接口的url与其余的不一样
     * 获取配置信息
     */
    public static void getConfig(DisposeDataListener listener) {
        RequestCenter.getRequest(WustApi.GET_CONFIG, null, listener, ConfigData.class);
    }

    /**
     * 管理端接口，接口的url与其余的不一样
     * 获取公告
     */
    public static void getNotice(DisposeDataListener listener) {
        RequestCenter.get(WustApi.NOTICE_URL, null, listener, NoticeData.class);
    }

    /**
     * 获取成绩
     */
    public static void getGrade(DisposeDataListener listener) {
        RequestCenter.get(WustApi.GRADE_API, null, listener, GradeData.class);
    }

    public static void getGraduateGrade(DisposeDataListener listener) {
        RequestCenter.get(WustApi.GRADUATE_GRADE_API, null, listener, GraduateGradeData.class);
    }

    public static void getShareCountdown(String onlyId, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("uuid", onlyId);
        RequestCenter.get(WustApi.ADD_SHARE_COUNTDOWN_URL, params, listener, BaseData.class);
    }

    public static void getCountDownFormNet(DisposeDataListener listener) {
        RequestCenter.get(WustApi.GET_COUNT_DOWN_URL, null, listener, CountDownData.class);
    }

    public static void deleteCountDownFromNet(DisposeDataListener listener, String onlyId) {
        RequestParams params = new RequestParams();
        params.put("uuid", onlyId);
        RequestCenter.get(WustApi.DELETE_COUNTDOWN_URL, params, listener, BaseData.class);
    }

    public static void uploadCountDownFromNet(DisposeDataListener listener, CountDownBean countDownBean) {
        RequestParams params = new RequestParams();
        params.put("name",countDownBean.getName());
        params.put("time",CountDownUtils.getShowTime(countDownBean.getTargetTime()));
        params.put("comment",countDownBean.getNote());
        RequestCenter.postJsonRequest(WustApi.ADD_COUNT_DOWN_URL, params, listener, CountDownAddData.class);
    }

    public static void changeCountDown(CountDownBean countDownBean,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("uuid",countDownBean.getOnlyId());
        params.put("name",countDownBean.getName());
        params.put("time",CountDownUtils.getShowTime(countDownBean.getTargetTime()));
        params.put("comment",countDownBean.getNote());
        //Log.e(TAG, "changeCountDown: onlyId = "+countDownBean.getOnlyId() );
        RequestCenter.postJsonRequest(WustApi.CHANGE_COUNTDOWN_URL, params, listener, CountDownChangeData.class);
    }

    public static void getCredit(DisposeDataListener listener){
        if(SharePreferenceLab.getIsGraduate()) {
            RequestCenter.get(WustApi.GRADUATE_CREDIT_API,null,listener, CreditsData.class);
        }else {
            RequestCenter.get(WustApi.CREDIT_API,null,listener, CreditsData.class);
        }
    }

    public static void getScheme(DisposeDataListener listener){
        if(SharePreferenceLab.getIsGraduate()) {
            RequestCenter.get(WustApi.GRADUATE_CREDIT_API,null,listener, CreditsData.class);
        }else {
            RequestCenter.get(WustApi.SCHEME_API,null,listener, CreditsData.class);
        }
    }

    public static void getCycleImage(DisposeDataListener listener){
        RequestCenter.get(WustApi.GET_CYCLE_IMAGE,null,listener, CycleImageData.class);
    }

    /**
     * 物理实验登录
     * @param password
     * @param listener
     */
    public static void postLoginPhysical(String password, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("wlsyPwd", password);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Token", getToken());
        headers.put("Platform", "android");
        RequestCenter.postRequest(WustApi.WLSYLOGIN, params, headers, listener, BaseData.class);
    }

    public static void getPhysicalCourse(DisposeDataListener listener){
        RequestCenter.get(WustApi.WLSYGETCOURSES,null,listener, CourseData.class);
    }

    /**
     * 图书馆登录
     * @param password
     * @param listener
     */
    public static void postLoginLibrary(String password, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("libPwd", password);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Token", getToken());
        headers.put("Platform", "android");
        RequestCenter.postRequest(WustApi.LibLogin, params, headers, listener, BaseData.class);
    }

    /**
     * 历史借阅
     * @param listener
     */
    public static void getHistoryBook(DisposeDataListener listener) {
        RequestCenter.get(WustApi.LIB_HISTORY, null,listener, LibraryHistoryData.class);
    }


    /**
     * 借阅信息
     * @param listener
     */
    public static void getRentInfo(DisposeDataListener listener) {
        RequestCenter.get(WustApi.LIB_RENT_INFO, null,listener, LibraryHistoryData.class);
    }

    /**
     * 收藏图书
     */
    public static void getLibMakeAddCollection(String title,String isbn,String author,String publisher,String bookDetailUrl, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("title", title);
        params.put("isbn", isbn);
        params.put("author", author);
        params.put("publisher", publisher);
        params.put("detailUrl", bookDetailUrl);
        RequestCenter.postJsonRequest(WustApi.LIB_ADD_COLLECTION, params, listener, BaseData.class);
    }

    /**
     * 取消收藏
     * @param isbn
     * @param listener
     * @throws FileNotFoundException
     */
    public static void getDelCollection(String isbn,DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("isbn", isbn);
        RequestCenter.get(WustApi.LIB_DEL_COLLECTION, params, listener, BaseData.class);
    }

    /**
     * 获取图书详细资料
     * @param url
     * @param listener
     */
    public static void getBookDetail(String url, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("url", url);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Token", getToken());
        headers.put("Platform", "android");
        RequestCenter.postRequest(WustApi.LIB_BOOK_INFO, params, headers, listener, BookData.class);
    }

    /**
     * 获取收藏图书
     * @param pageNum
     * @param listener
     */
    public static void getLibListCollection(String pageNum,DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("pageNum", pageNum);
        RequestCenter.get(WustApi.LIB_LIST_COLLECTION, params, listener, LibCollectData.class);
    }

    /**
     * 获取图书馆公告
     * @param pageNum
     * @param listener
     */
    public static void getLibraryAnnouncement(String pageNum,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("pageNum",pageNum);
        RequestCenter.get(WustApi.LIB_ANNOUNCEMENTLIST,params,listener, AnnouncementData.class);
    }

    /**
     * 获取图书馆公告详情
     * @param announcementId
     * @param listener
     */
    public static void getLibraryAnnouncementDetail(String announcementId, DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("announcementId",announcementId);
        RequestCenter.get(WustApi.LIB_ANNOUNCEMENTCONTENT,params,listener, AnnouncementContentData.class);
    }

    /**
     * 搜索图书
     * @param pageNum
     * @param keyWord
     * @param listener
     */
    public static void searchLibraryBook(String pageNum,String keyWord,DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        RequestParams headers = new RequestParams();
        params.put("pageNum", pageNum);
        params.put("keyWord", keyWord);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Token", getToken());
        headers.put("Platform", "android");
        RequestCenter.postRequest(WustApi.LIB_BOOK_SEARCH, params, headers, listener, SearchBookData.class);
    }

    /**
     * 查询空教室
     * @param buildingName
     * @param areaNum
     * @param campusName
     * @param week
     * @param weekDay
     * @param section
     * @param listener
     */
    public static void findEmptyClassroom(String buildingName,String areaNum,String campusName,String week,String weekDay,String section,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("buildingName",buildingName);
        params.put("areaNum",areaNum);
        params.put("campusName",campusName);
        params.put("week",week);
        params.put("weekDay",weekDay);
        params.put("section",section);
        RequestCenter.get(WustApi.CLASSROOM_EMPTY_FIND,params,listener, EmptyClassroomData.class);
    }

    /**
     * 获取学院列表
     * @param listener
     */
    public static void getCollegeList(DisposeDataListener listener){
        RequestCenter.get(WustApi.CLASSROOM_COLLEGE_LIST,null,listener, CollegeData.class);
    }

    /**
     * 获取课程名列表
     * @param collegeId
     * @param pageNum
     * @param listener
     */
    public static void getCourseNameList(String collegeId,String pageNum,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("collegeId",collegeId);
        params.put("pageNum",pageNum);
        RequestCenter.get(WustApi.CLASSROOM_COURSE_LIST,params,listener, CourseNameData.class);
    }

    /**
     * 获取课程详情，获取某学院的某同名课程的详情
     * @param collegeId
     * @param courseName
     * @param listener
     */
    public static void getCourseInfo(String collegeId,String courseName,String pageNum,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("collegeId",collegeId);
        params.put("courseName",courseName);
        params.put("pageNum",pageNum);
        RequestCenter.get(WustApi.CLASSROOM_COURSE_INFO,params,listener, SearchCourseData.class);
    }

    /**
     * 院内搜索课程，在指定学院范围内搜索，搜索为模糊搜索，即返回所有包含关键词的记录
     * @param collegeId
     * @param key
     * @param pageNum
     * @param listener
     */
    public static void searchInCollege(String collegeId,String key,String pageNum,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("collegeId",collegeId);
        params.put("key",key);
        params.put("pageNum",pageNum);
        RequestCenter.get(WustApi.CLASSROOM_SEARCH_COLLEGE,params,listener, SearchCourseData.class);
    }


    /**
     * 在全范围内搜索，搜索为模糊搜索，即返回所有包含关键词的记录
     * @param key
     * @param pageNum
     * @param listener
     */
    public static void searchALL(String key,String pageNum,DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("key",key);
        params.put("pageNum",pageNum);
        RequestCenter.get(WustApi.CLASSROOM_SEARCH,params,listener, SearchCourseData.class);
    }

    /**
     * 失物招领接口，接口的url与其余的不一样
     * 获取公告
     */
    public static void getLostUnread(DisposeDataListener listener) {
        RequestCenter.get(WustApi.LOST_NOTICE_UNREAD, null, listener, LostData.class);
    }

    public static void getLostMark(DisposeDataListener listener) {
        RequestCenter.get(WustApi.LOST_NOTICE_MARK, null, listener, LostData.class);
    }
}
