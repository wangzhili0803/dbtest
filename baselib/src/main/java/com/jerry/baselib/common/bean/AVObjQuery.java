package com.jerry.baselib.common.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.OnDataChangedListener;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.json.JSON;
import cn.leancloud.json.JSONArray;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Jerry
 * @createDate 2019-10-18
 * @description AVObj查询基类
 */
public class AVObjQuery<T> {

    private Class<T> tClass;
    private AVQuery<AVObject> mAVQuery;

    public AVObjQuery(Class<T> tClass) {
        this.tClass = tClass;
        mAVQuery = new AVQuery<>(tClass.getSimpleName());
    }

    public AVObjQuery<T> whereEqualTo(final String key, final Object value) {
        if (mAVQuery != null) {
            mAVQuery.whereEqualTo(key, value);
        }
        return this;
    }

    public AVObjQuery<T> whereDoesNotExist(final String key) {
        if (mAVQuery != null) {
            mAVQuery.whereDoesNotExist(key);
        }
        return this;
    }

    public AVObjQuery<T> whereStartsWith(final String key, final String value) {
        if (mAVQuery != null) {
            mAVQuery.whereStartsWith(key, value);
        }
        return this;
    }

    public AVObjQuery<T> whereLessThan(final String key, final Object value) {
        if (mAVQuery != null) {
            mAVQuery.whereLessThan(key, value);
        }
        return this;
    }

    public AVObjQuery<T> whereGreaterThan(final String key, final Object value) {
        if (mAVQuery != null) {
            mAVQuery.whereGreaterThan(key, value);
        }
        return this;
    }

    public AVObjQuery<T> whereContainedIn(final String key, final List<String> values) {
        if (mAVQuery != null) {
            mAVQuery.whereContainedIn(key, values);
        }
        return this;
    }

    public AVObjQuery<T> skip(final int skip) {
        if (mAVQuery != null) {
            mAVQuery.skip(skip);
        }
        return this;
    }

    public AVObjQuery<T> limit(final int limit) {
        if (mAVQuery != null) {
            mAVQuery.limit(limit);
        }
        return this;
    }

    public AVObjQuery<T> orderByAscending(final String order) {
        if (mAVQuery != null) {
            mAVQuery.orderByAscending(order);
        }
        return this;
    }

    public AVObjQuery<T> orderByDescending(final String order) {
        if (mAVQuery != null) {
            mAVQuery.orderByDescending(order);
        }
        return this;
    }

    public void getObject(String id, OnDataChangedListener<DataResponse<T>> dataChangedListener) {
        if (mAVQuery != null) {
            mAVQuery.whereEqualTo("objectId", id);
        }
        DataResponse<T> dataResponse = new DataResponse<>();
        if (mAVQuery == null) {
            if (dataChangedListener != null) {
                dataResponse.setCode(1);
                dataResponse.setMsg("mAVQuery not null!");
                dataChangedListener.onDataChanged(dataResponse);
            }
            return;
        }
        mAVQuery.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(final Disposable d) {

            }

            @Override
            public void onNext(final List<AVObject> avObjects) {
                if (dataChangedListener != null) {
                    if (!CollectionUtils.isEmpty(avObjects)) {
                        dataResponse.setCode(0);
                        dataResponse.setData(JJSON.parseObject(JSON.toJSONString(avObjects.get(0).getServerData()), AppUtils.type(tClass)));
                    } else {
                        dataResponse.setCode(1);
                        dataResponse.setMsg("not found any data!");
                    }
                    dataChangedListener.onDataChanged(dataResponse);
                }
            }

            @Override
            public void onError(final Throwable e) {
                if (dataChangedListener != null) {
                    dataResponse.setCode(1);
                    dataResponse.setMsg(e.getMessage());
                    dataChangedListener.onDataChanged(dataResponse);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void findObjects(OnDataChangedListener<DataResponse<List<T>>> dataChangedListener) {
        DataResponse<List<T>> dataResponse = new DataResponse<>();
        if (mAVQuery == null) {
            if (dataChangedListener != null) {
                dataResponse.setCode(1);
                dataResponse.setMsg("mAVQuery not null!");
                dataChangedListener.onDataChanged(dataResponse);
            }
            return;
        }

        mAVQuery.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(final Disposable d) {

            }

            @Override
            public void onNext(final List<AVObject> avObjects) {
                if (dataChangedListener != null) {
                    List<T> list = new ArrayList<>();
                    for (AVObject avObject : avObjects) {
                        if (avObject == null) {
                            continue;
                        }
                        T df = JJSON.parseObject(JSON.toJSONString(avObject.getServerData()), AppUtils.type(tClass));
                        list.add(df);
                    }
                    dataResponse.setCode(0);
                    dataResponse.setData(list);
                    dataChangedListener.onDataChanged(dataResponse);
                }
            }

            @Override
            public void onError(final Throwable e) {
                if (dataChangedListener != null) {
                    dataResponse.setCode(1);
                    dataResponse.setMsg(e.getMessage());
                    dataChangedListener.onDataChanged(dataResponse);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 同步查找
     */
    public List<T> findObjects() {
        if (mAVQuery == null) {
            return new ArrayList<>();
        }
        List<AVObject> avObjects = mAVQuery.find();
        List<T> list = new ArrayList<>();
        for (AVObject avObject : avObjects) {
            if (avObject == null) {
                continue;
            }
            T df = JJSON.parseObject(JSON.toJSONString(avObject.getServerData()), AppUtils.type(tClass));
            list.add(df);
        }
        return list;
    }

    /**
     * 批量添加
     */
    public static <T extends AVBaseObject> void saveAll(List<T> list, OnDataChangedListener<DataResponse<List<T>>> dataChangedListener) {
        DataResponse<List<T>> dataResponse = new DataResponse<>();
        if (CollectionUtils.isEmpty(list)) {
            dataResponse.setCode(1);
            dataResponse.setMsg("list is Empty!");
            dataChangedListener.onDataChanged(dataResponse);
            return;
        }
        List<AVObject> objects = new ArrayList<>();
        for (T t : list) {
            AVObject object = new AVObject(t.getClass().getSimpleName());
            Field[] tempClass = t.getClass().getDeclaredFields();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(tempClass));
            for (Field field : fieldList) {
                field.setAccessible(true); // 设置字段的可访问性
                try {
                    String name = field.getName();
                    Object value = field.get(t);
                    if (value != null) {
                        object.put(name, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            objects.add(object);
        }
        AVObject.saveAllInBackground(objects).subscribe(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(final JSONArray objs) {
                if (list.size() == objs.size()) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setObjectId(objs.getJSONObject(i).getJSONObject("success").getString("objectId"));
                    }
                }
                dataResponse.setCode(0);
                dataResponse.setData(list);
                dataChangedListener.onDataChanged(dataResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                dataResponse.setCode(1);
                dataResponse.setMsg(throwable.getMessage());
                dataChangedListener.onDataChanged(dataResponse);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * 批量删除
     */
    public static <T extends AVBaseObject> void deleteAll(List<T> list, OnDataChangedListener<DataResponse<Integer>> dataChangedListener) {
        DataResponse<Integer> dataResponse = new DataResponse<>();
        if (CollectionUtils.isEmpty(list)) {
            dataResponse.setCode(1);
            dataResponse.setMsg("list is Empty!");
            dataChangedListener.onDataChanged(dataResponse);
            return;
        }
        List<AVObject> objects = new ArrayList<>();
        for (T t : list) {
            Map<String, Object> map = new ArrayMap<>();
            Field[] tempClass = t.getClass().getDeclaredFields();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(tempClass));
            for (Field field : fieldList) {
                // 设置字段的可访问性
                field.setAccessible(true);
                try {
                    String name = field.getName();
                    Object value = field.get(t);
                    if (value != null) {
                        map.put(name, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(t.getObjectId())) {
                AVObject object = AVObject.createWithoutData(t.getClass().getSimpleName(), t.getObjectId());
                map.remove("objectId");
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    object.put(key, map.get(key));
                }
                objects.add(object);
            }
        }
        AVObject.deleteAllInBackground(objects).subscribe(new Observer<AVNull>() {
            @Override
            public void onSubscribe(final Disposable d) {

            }

            @Override
            public void onNext(AVNull todo) {
                dataResponse.setCode(0);
                dataResponse.setData(list.size());
                dataChangedListener.onDataChanged(dataResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                dataResponse.setCode(1);
                dataResponse.setMsg(throwable.getMessage());
                dataChangedListener.onDataChanged(dataResponse);
            }


            @Override
            public void onComplete() {

            }
        });
    }
}
