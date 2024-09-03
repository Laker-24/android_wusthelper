package com.example.wusthelper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wusthelper.R;
import com.example.wusthelper.base.activity.BaseActivity;
import com.example.wusthelper.bean.javabean.City;
import com.example.wusthelper.bean.javabean.County;
import com.example.wusthelper.bean.javabean.Province;
import com.example.wusthelper.databinding.ActivitySearchClassRoomBinding;
import com.example.wusthelper.helper.MyDialogHelper;

import org.jaaksi.pickerview.dataset.OptionDataSet;
import org.jaaksi.pickerview.picker.OptionPicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchClassRoomActivity extends BaseActivity<ActivitySearchClassRoomBinding> implements
        View.OnClickListener, OptionPicker.OnOptionSelectListener{

    public static Intent newInstance(Context context) {
        return new Intent(context, SearchClassRoomActivity.class);
    }

    private static final String TAG = "SearchClassRoomActivity";

    private int tag = 1;

    private OptionPicker mPicker;

    private String provinceId /*= "200"*/, cityId /*= "230"*/, countyId/* = "234"*/;

    List<String> weeks = new ArrayList<String>(Arrays.asList("第一周","第二周","第三周","第四周","第五周","第六周","第七周","第八周","第九周","第十周","第十一周","第十二周","第十三周","第十四周","第十五周","第十六周","第十七周","第十八周","第十九周"));
    List<String> weekdays = new ArrayList<String>(Arrays.asList("周一","周二","周三","周四","周五","周六","周日"));
    List<String> sections = new ArrayList<String>(Arrays.asList("1,2节课","3,4节课","5,6节课","7,8节课","9,10节课","11,12节课","上午","下午","晚上","全天"));


//    private String[] weeks = {"第一周","第二周","第三周","第四周","第五周","第六周","第七周","第八周","第九周","第十周","第十一周","第十二周","第十三周","第十四周","第十五周","第十六周","第十七周","第十八周","第十九周"};
//    private String[] weekdays = {"周一","周二","周三","周四","周五","周六","周日"};
//    private String[] sections = {"3,4节课","5,6节课","7,8节课","9,10节课","11,12节课","上午","下午","晚上","全天"};


    private boolean isChooseBuild = true;
    private String buildingName = "";
    private String areaNum = "";
    private String campusName = "";
    private String week = "";
    private String weekDay = "";
    private String section = "";

    private final String getJson = "{\n" +
            "  \"data\" : []\n" +
            "}";
    private final String json = "{\n" +
            "  \"data\" : [\n" +
            "  {\n" +
            "    \"province\":\"黄家湖\",\n" +
            "    \"city\":[\n" +
            "      {\n" +
            "        \"name\":\"恒大楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"一区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"二区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"三区\"\n" +
            "          }]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教二楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"一区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"二区\"\n" +
            "          }]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教三楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教四楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"一区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"二区\"\n" +
            "          }\n" +
            "          ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教五楼\",\n" +
            "        \"country\":[{\n" +
            "            \"name\" : \"二区\"\n" +
            "          }]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教六楼\",\n" +
            "        \"country\":[{\n" +
            "            \"name\" : \"一区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"三区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"四区\"\n" +
            "          }]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教七楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教八楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教九楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教十一楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"A区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"B区\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"C区\"\n" +
            "          }\n" +
            "          ]\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"province\":\"青山\",\n" +
            "    \"city\":[\n" +
            "      {\n" +
            "        \"name\":\"主楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教三楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\":\"教四楼\",\n" +
            "        \"country\":[\n" +
            "          {\n" +
            "            \"name\" : \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]\n" +
            "}";


    @Override
    public void initView() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        getBinding().searchRoom.setOnClickListener(this);
        getBinding().ivBack.setOnClickListener(this);
        getBinding().roomBuild.setOnClickListener(this);
        getBinding().roomTime.setOnClickListener(this);
        mPicker = new OptionPicker.Builder(this, 3, this).create();
    }

    private void createPickerTime(){
        List<Province> provinces = new ArrayList<>();
        List<City> cities = new ArrayList<>();
        List<County> counties = new ArrayList<>();
        for (int i = 0; i < weeks.size(); i++) {
            Province province = new Province();
            province.id = i;
            province.name = weeks.get(i);
            provinces.add(province);
        }

        for (int i = 0; i < weekdays.size(); i++) {
            City city = new City();
            city.id = 10 * i;
            city.name = weekdays.get(i);
            cities.add(city);
        }

        for (int i = 0; i < sections.size(); i++) {
            County county = new County();
            county.id = 100 * i;
            county.name = sections.get(i);
            counties.add(county);
        }
        mPicker.setData(provinces, cities, counties);
    }

    private List<Province> createData() throws JSONException {
        List<Province> list = new ArrayList<>();
        JSONObject jsonArraya = new JSONObject(json);
        JSONArray jsonArray = jsonArraya.getJSONArray("data");
        Log.d("jsonArray",jsonArray.toString());
        for (int i = 0; i<jsonArray.length(); i++) {
            Province province = new Province();
            province.citys= new ArrayList<>();
//            List<City> cities = new ArrayList<>();
            province.setId(100*i);
            JSONObject jsonObjectProvince = jsonArray.getJSONObject(i);
            province.setName(jsonObjectProvince.getString("province"));
            JSONArray jsonArray1 = jsonObjectProvince.getJSONArray("city");
            for(int j=0; j<jsonArray1.length(); j++) {
                City city = new City();
                city.counties = new ArrayList<>();
//                List<County> counties = new ArrayList<>();
                city.setId(100*i+10*j);
                JSONObject jsonObjectCity = jsonArray1.getJSONObject(j);
                city.setName(jsonObjectCity.getString("name"));
                JSONArray jsonArray2 = jsonObjectCity.getJSONArray("country");
                for (int k=0;k<jsonArray2.length(); k++) {
                    County county = new County();
                    county.setId(100*i+10*j+k);
                    JSONObject jsonObjectCounty = jsonArray2.getJSONObject(k);
                    county.setName(jsonObjectCounty.getString("name"));
                    city.counties.add(county);
                }
                province.citys.add(city);
            }
            list.add(province);
        }

        return list;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(getBinding().roomBuild)) {
            isChooseBuild = true;
            List<Province> data = null;
            try {
                data = createData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPicker.setData(data);
            mPicker.setSelectedWithValues(provinceId, cityId, countyId);
            mPicker.show();
        }else if(v.equals(getBinding().roomTime)) {
            isChooseBuild = false;
            createPickerTime();
            mPicker.setSelectedWithValues(provinceId, cityId, countyId);
            mPicker.show();
        }else if(v.equals(getBinding().ivBack)) {
            finish();
        }else if(v.equals(getBinding().searchRoom)){
            if(campusName.equals("")&&buildingName.equals("")&&areaNum.equals("")){
                SweetAlertDialog dialog = MyDialogHelper.getCommonDialog(this,SweetAlertDialog.BUTTON_CONFIRM,"请选择教学楼","确认");
                dialog.show();
            }else if(week.equals("")&&weekDay.equals("")&&section.equals("")){
                SweetAlertDialog dialog = MyDialogHelper.getCommonDialog(this,SweetAlertDialog.BUTTON_CONFIRM,"请选择节次","确认");
                dialog.show();
            }else {
                Log.e(TAG,campusName+","+buildingName+","+areaNum+","+week+","+weekDay+","+section);
                startActivity(SearchRoomResultActivity.newInstance(this,buildingName,areaNum,campusName,week,weekDay,section));
            }
        }
    }

    @Override
    public void onOptionSelect(OptionPicker picker, int[] selectedPosition, OptionDataSet[] selectedOptions) {
        String text;
        provinceId = "";
        cityId = "";
        countyId = "";
        Province province = (Province) selectedOptions[0];
        if(province!=null){
            provinceId = province.getName();
        }
        City city = (City) selectedOptions[1];
        if(city!=null){
            cityId = city.getName();
        }
        County county = (County) selectedOptions[2];
        if(county!=null){
            countyId = county.getName();
        }
        text = provinceId+" "+cityId+" "+countyId;
        if(isChooseBuild){
            getBinding().roomBuildTv.setText(text);
            campusName = provinceId;
            buildingName = cityId;
            areaNum = countyId;
        }else{
            getBinding().roomTimeTv.setText(text);
            week = weeks.indexOf(provinceId)+1+"";
            weekDay = weekdays.indexOf(cityId)+1+"";
            section = sections.indexOf(countyId)+1+"";
        }
    }
}