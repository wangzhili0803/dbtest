package com.jerry.baselib.common.weidgt;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.jerry.baselib.R;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.BackgroundTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.bean.Area;
import com.jerry.baselib.common.bean.City;
import com.jerry.baselib.common.bean.Province;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.ParseUtil;

public class City2PickerDialog extends BaseDialog {

    @StringRes
    private int title;
    private NumberPicker proviceNp;
    private NumberPicker cityNp;
    private Area provice;
    private Area city;
    private List<Area> provices = new ArrayList<>();
    private List<Area> citys = new ArrayList<>();
    private int initProviceId;
    private int initCityId;

    public City2PickerDialog(Context context) {
        super(context);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_city2;
    }

    @Override
    protected void initView() {
        super.initView();
        TextView titleTv = findViewById(R.id.title_tv);
        titleTv.setText(title);
        proviceNp = findViewById(R.id.year_np);
        cityNp = findViewById(R.id.month_np);
        proviceNp.setMinValue(0);
        cityNp.setMinValue(0);
        proviceNp.setFormatter(value -> {
            if (value < provices.size()) {
                Area bean = provices.get(value);
                if (bean != null) {
                    return bean.getName();
                }
            }
            return String.valueOf(value);
        });
        cityNp.setFormatter(value -> {
            if (value < citys.size()) {
                Area bean = citys.get(value);
                if (bean != null) {
                    return bean.getName();
                }
            }
            return String.valueOf(value);
        });

        proviceNp.setOnValueChangedListener((picker, oldVal, newVal) -> {
            provice = provices.get(picker.getValue());
            getInfo(1, provice);
        });
        cityNp.setOnValueChangedListener((picker, oldVal, newVal) -> city = citys.get(picker.getValue()));
        getInfo(0, new Area());
    }

    public void setTitleText(@StringRes int id) {
        title = id;
    }

    public void setValue(final int provice, final int city, final int district) {
        initProviceId = provice;
        initCityId = city;
    }

    private synchronized void getInfo(int areaType, Area area) {
        int areaId = ParseUtil.parseInt(area.getCode());
        proviceNp.setEnabled(false);
        cityNp.setEnabled(false);
        AppTask.withoutContext().assign((BackgroundTask<List<Area>>) () -> {
            List<Area> respose = new ArrayList<>();
            String json = ListCacheUtil.getValueFromAssetJsonFile("pcc.json");
            List<Province> provinceList = JJSON.parseObject(json, AppUtils.type(List.class, Province.class, City.class, Area.class));
            if (areaType == 0) {
                for (Province province : provinceList) {
                    Area item = new Area();
                    item.setCode(province.getCode());
                    item.setName(province.getName());
                    respose.add(item);
                }
            } else {
                int proviceId = areaId / 10000 * 10000;
                for (Province province : provinceList) {
                    if (ParseUtil.parseInt(province.getCode()) == proviceId) {
                        List<City> cities = province.getCityList();
                        for (City city : cities) {
                            Area item = new Area();
                            item.setCode(city.getCode());
                            item.setName(city.getName());
                            respose.add(item);
                        }
                        break;
                    }
                }
            }
            return respose;
        }).whenDone((WhenTaskDone<List<Area>>) result -> {
            if (mContext == null || result == null || result.size() == 0) {
                return;
            }
            handleResponse(result, areaType);
        }).whenTaskEnd(() -> {
            proviceNp.setEnabled(true);
            cityNp.setEnabled(true);
        }).execute();
    }

    private synchronized void handleResponse(List<Area> response, int areaType) {
        switch (areaType) {
            case 0:
                proviceNp.setMaxValue(response.size() - 1);
                provices.clear();
                provices.addAll(response);
                provice = provices.get(0);
                if (initProviceId > 0) {
                    for (int i = 0; i < provices.size(); i++) {
                        Area area = provices.get(i);
                        int areaId = ParseUtil.parseInt(area.getCode());
                        if (areaId == initProviceId) {
                            provice = area;
                            initProviceId = 0;
                            proviceNp.setValue(i);
                            break;
                        }
                    }
                } else {
                    proviceNp.setValue(0);
                }
                proviceNp.invalidate();
                getInfo(1, provice);
                break;
            case 1:
            default:
                cityNp.setMaxValue(response.size() - 1);
                citys.clear();
                citys.addAll(response);
                if (initCityId > 0) {
                    for (int i = 0; i < citys.size(); i++) {
                        Area area = citys.get(i);
                        int areaId = ParseUtil.parseInt(area.getCode());
                        if (areaId == initCityId) {
                            city = area;
                            initCityId = 0;
                            cityNp.setValue(i);
                            break;
                        }
                    }
                } else {
                    city = citys.get(0);
                    cityNp.setValue(0);
                }
                cityNp.invalidate();
                break;
        }
    }

    public Area getProvice() {
        return provice;
    }

    public Area getCity() {
        return city;
    }
}
