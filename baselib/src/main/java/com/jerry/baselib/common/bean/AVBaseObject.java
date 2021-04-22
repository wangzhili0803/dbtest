package com.jerry.baselib.common.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.jerry.baselib.common.util.OnDataCallback;

import cn.leancloud.AVObject;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Jerry
 * @createDate 2019-10-18
 * @description
 */
public class AVBaseObject {

    private String objectId;
    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * 更新时间
     */
    private String updatedAt;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void save(OnDataCallback<DataResponse<String>> dataChangedListener) {
        AVObject object = new AVObject(getClass().getSimpleName());
        Field[] tempClass = getClass().getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(tempClass));
        DataResponse<String> dataResponse = new DataResponse<>();
        for (Field field : fieldList) {
            // 设置字段的可访问性
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(AVBaseObject.this);
                if (value != null) {
                    object.put(name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        object.saveInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(AVObject todo) {
                dataResponse.setCode(0);
                dataResponse.setData(todo.getObjectId());
                dataChangedListener.onDataCallback(dataResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                dataResponse.setCode(1);
                dataResponse.setMsg(throwable.getMessage());
                dataChangedListener.onDataCallback(dataResponse);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void update(OnDataCallback<DataResponse<String>> dataChangedListener) {
        Map<String, Object> map = new ArrayMap<>();
        Field[] tempClass = getClass().getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(tempClass));
        DataResponse<String> dataResponse = new DataResponse<>();
        for (Field field : fieldList) {
            // 设置字段的可访问性
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(AVBaseObject.this);
                if (value != null) {
                    map.put(name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(objectId)) {
            AVObject object = AVObject.createWithoutData(getClass().getSimpleName(), objectId);
            map.remove("objectId");
            Set<String> keys = map.keySet();
            for (String key : keys) {
                object.put(key, map.get(key));
            }
            object.saveInBackground().subscribe(new Observer<AVObject>() {
                @Override
                public void onSubscribe(Disposable disposable) {
                }

                @Override
                public void onNext(AVObject todo) {
                    dataResponse.setCode(0);
                    dataResponse.setData(todo.getObjectId());
                    dataChangedListener.onDataCallback(dataResponse);
                }

                @Override
                public void onError(Throwable throwable) {
                    dataResponse.setCode(1);
                    dataResponse.setMsg(throwable.getMessage());
                    dataChangedListener.onDataCallback(dataResponse);
                }

                @Override
                public void onComplete() {
                }
            });
            return;
        }
        dataResponse.setCode(1);
        dataResponse.setMsg("Object update failed！");
        dataChangedListener.onDataCallback(dataResponse);
    }

    public void delete(OnDataCallback<DataResponse<AVBaseObject>> dataChangedListener) {
        DataResponse<AVBaseObject> dataResponse = new DataResponse<>();
        if (!TextUtils.isEmpty(objectId)) {
            AVObject object = AVObject.createWithoutData(getClass().getSimpleName(), objectId);
            object.deleteInBackground().subscribe(new Observer<AVNull>() {
                @Override
                public void onSubscribe(final Disposable d) {

                }

                @Override
                public void onNext(final AVNull avNull) {
                    dataResponse.setCode(0);
                    dataResponse.setData(AVBaseObject.this);
                    dataChangedListener.onDataCallback(dataResponse);
                }

                @Override
                public void onError(final Throwable e) {
                    dataResponse.setCode(1);
                    dataResponse.setMsg(e.getMessage());
                    dataChangedListener.onDataCallback(dataResponse);
                }

                @Override
                public void onComplete() {

                }
            });
            return;
        }
        dataResponse.setCode(1);
        dataResponse.setMsg("Object delete failed！objectId is null");
        dataChangedListener.onDataCallback(dataResponse);
    }

    public void save() {
        AVObject object;
        if (TextUtils.isEmpty(objectId)) {
            object = new AVObject(getClass().getSimpleName());
        } else {
            object = AVObject.createWithoutData(getClass().getSimpleName(), objectId);
        }
        Field[] tempClass = getClass().getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(tempClass));
        for (Field field : fieldList) {
            // 设置字段的可访问性
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(AVBaseObject.this);
                if (value != null) {
                    object.put(name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        object.save();
    }
}
