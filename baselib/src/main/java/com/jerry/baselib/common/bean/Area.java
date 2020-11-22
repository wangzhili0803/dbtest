package com.jerry.baselib.common.bean;

import java.util.List;
import java.util.Random;

import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.ListCacheUtil;

/**
 * 区，县
 *
 * @author jx on 2018/4/12.
 */

public class Area {

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void setArea(String area, Product product) {
        if (area == null) {
            return;
        }
        String[] areas = area.split(",");
        String areap;
        String areac;
        String json = ListCacheUtil.getValueFromAssetJsonFile("pcc.json");
        if (areas.length > 1) {
            areap = areas[0];
            areac = areas[1];
            List<Province> provinceList = JJSON.parseObject(json, AppUtils.type(List.class, Province.class, City.class, Area.class));
            for (Province province : provinceList) {
                if (province.getName().contains(areap)) {
                    product.setProvice(province.getName());
                    List<City> cities = province.getCityList();
                    for (City city : cities) {
                        if (city.getName().contains(areac)) {
                            product.setCity(city.getName());
                            break;
                        }
                    }
                    break;
                }
            }
        } else {
            if (areas[0].contains(" ")) {
                areas = area.split(" ");
                areap = areas[0].replace("省", "").replace("市", "");
                areac = areas[1].replace("市", "");
                List<Province> provinceList = JJSON.parseObject(json, AppUtils.type(List.class, Province.class, City.class, Area.class));
                for (Province province : provinceList) {
                    if (province.getName().contains(areap)) {
                        product.setProvice(province.getName());
                        List<City> cities = province.getCityList();
                        for (City city : cities) {
                            if (city.getName().contains(areac)) {
                                product.setCity(city.getName());
                                if (areas.length >= 3) {
                                    List<Area> areas1 = city.getAreaList();
                                    for (Area area1 : areas1) {
                                        if (area1.getName().contains(areas[2].replace("市", ""))) {
                                            product.setDistrict(area1.getName());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            } else {
                if (areas[0].length() > 1) {
                    areap = areas[0].substring(0, 2);
                    areac = areas[0].substring(areas[0].length() - 2);
                } else {
                    areap = areas[0];
                    areac = areas[0];
                }
                List<Province> provinceList = JJSON.parseObject(json, AppUtils.type(List.class, Province.class, City.class, Area.class));
                for (Province province : provinceList) {
                    if (province.getName().contains(areap)) {
                        product.setProvice(province.getName());
                        List<City> cities = province.getCityList();
                        for (City city : cities) {
                            if (city.getName().contains(areac)) {
                                product.setCity(city.getName());
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void setRandomArea(Product product) {
        String json = ListCacheUtil.getValueFromAssetJsonFile("pcc.json");
        List<Province> provinceList = JJSON.parseObject(json, AppUtils.type(List.class, Province.class, City.class, Area.class));
        int max = 22;
        int min = 0;
        Random random = new Random();
        int s;
        do {
            s = random.nextInt(max) % (max - min + 1) + min;
        } while (s == 4);

        Province province = provinceList.get(s);
        product.setProvice(province.getName());
        List<City> cities = province.getCityList();
        City city = cities.get(0);
        product.setCity(city.getName());
        List<Area> areas = city.getAreaList();
        max = areas.size();
        if (max > 0) {
            s = random.nextInt(max) % (max - min + 1) + min;
            product.setDistrict(areas.get(s).getName());
        }
    }

}