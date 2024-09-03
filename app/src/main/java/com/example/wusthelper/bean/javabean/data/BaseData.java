package com.example.wusthelper.bean.javabean.data;

import com.google.gson.annotations.SerializedName;

/**
 * JavaBean的简单封装，用于解析网络请求的解析,
 * 首先就定义了 code和 msg两个比较常用的量
 * 后续网络解析类希望继承自BaseBean */
public class BaseData {

    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaseData{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
