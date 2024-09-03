package com.example.wusthelper.request;

import android.util.Log;

import com.example.wusthelper.MyApplication;
import com.example.wusthelper.helper.SharePreferenceLab;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/**
 * token拦截器
 * 每次网络请求，都会拦截通过token进行拦截
 * 如果过期
 * 就会重新请求一个新的token,然后再以新的token进行请求
 * */
public class TokenInterceptor implements Interceptor {
    public static final int CODE_JWC_CHANGE_PWD_ERR = 30001;
    public static final int CODE_JWC_FIN_INFO_ERR = 30101;
    public static final int CODE_JWC_MOD_DEF_PWD = 30102;
    public static final int CODE_JWC_ERR_INFO_ERR = 30103;
    public static final int CODE_YJS_FIN_INFO_ERR = 70002;
    public static final int CODE_YJS_MOD_DEF_PWD = 70003;
    public static final int CODE_YJS_ERR_INFO_ERR = 70005;
    public static final int TOKEN_TIMEOUT = 312;
    public static final int PWD_CHANGE = 313;
    final String TAG ="TokenTimeoutIntercept: ";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (isTokenTimeOut(response)){
            Log.d(TAG, "intercept: ");
            getNewToken();
            String token = SharePreferenceLab.getInstance().getToken(MyApplication.getContext());
            String msg = SharePreferenceLab.getInstance().getMessage(MyApplication.getContext());

            RequestCenter.setToken(token);
            RequestCenter.setMessage(msg);

            Request newRequest = chain.request().newBuilder()
                    .header("token", token)
                    .build();

            return chain.proceed(newRequest);
        }
        return response;
    }

    private void getNewToken() {
        String studentId = SharePreferenceLab.getInstance().getStudentId(MyApplication.getContext());
        String psd = SharePreferenceLab.getInstance().getPassword(MyApplication.getContext());
        Log.d(TAG,"studentId ：" + studentId);
        Log.d(TAG,"psd ：" + psd);
        try {
            Response response;
            if(SharePreferenceLab.getIsGraduate()) {
                response = NewApiHelper.loginGraduate(studentId, psd);
            }else {
                response = NewApiHelper.login(studentId, psd);
            }
            String res = response.body().string();
            final JSONObject jsonObject = new JSONObject(res);
            int code = jsonObject.getInt("code");
            final String message = jsonObject.getString("msg");
            Log.d(TAG, "getNewToken: "+code);
            if(code==CODE_JWC_CHANGE_PWD_ERR||code==CODE_JWC_FIN_INFO_ERR||code==CODE_JWC_MOD_DEF_PWD||code==CODE_JWC_ERR_INFO_ERR
                    ||code==CODE_YJS_FIN_INFO_ERR||code==CODE_YJS_MOD_DEF_PWD||code==CODE_YJS_ERR_INFO_ERR){
//                Activity activity = ActivityManager.getActivityManager().currentActivity();
//                Intent intent = new Intent(activity, LoginMvpActivity.class);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        ToastUtil.showToast("密码已修改，请重新登录");
//                    }
//                });
////                CourseDB.getInstance().logout(MyApplication.getContext());
////                SharePreferenceLab.getInstance().logout(MyApplication.getContext());
////                DBHelper.logout();
//                activity.startActivity(intent);
            }else{
                if (jsonObject.has("data")) {
                    String token = jsonObject.getString("data");
                    RequestCenter.setToken(token);
                    SharePreferenceLab.setToken(token);
                    SharePreferenceLab.getInstance().setMessage(MyApplication.getContext(),message);
                }
            }



        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isTokenTimeOut(Response response) {
        Log.d(TAG, "isTokenTimeOut: "+response.code() );
        return response.code() == TOKEN_TIMEOUT||response.code() == PWD_CHANGE;
    }
}
