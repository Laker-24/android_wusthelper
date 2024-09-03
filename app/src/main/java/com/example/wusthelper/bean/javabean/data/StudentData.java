package com.example.wusthelper.bean.javabean.data;

import com.google.gson.annotations.SerializedName;

/**
 * 用于解析学生信息获取接口
 * */
public class StudentData extends BaseData {

    @SerializedName("data")
    public Content data;
    public class Content{
        private String stuNum;
        private String stuName;
        private String nickName;
        private String college;
        private String major;
        private String classes;
        @Override
        public String toString() {
            return "data{" +
                    "stuNum='" + stuNum + '\'' +
                    ", stuName='" + stuName + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", college='" + college + '\'' +
                    ", major='" + major + '\'' +
                    ", classes='" + classes + '\'' +
                    '}';
        }

        public String getStuNum() {
            return stuNum;
        }

        public void setStuNum(String stuNum) {
            this.stuNum = stuNum;
        }

        public String getStuName() {
            return stuName;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public String getClasses() {
            return classes;
        }

        public void setClasses(String classes) {
            this.classes = classes;
        }
    }

    @Override
    public String toString() {
        return "StudentData{" +
                "data=" + data +
                '}';
    }
}
